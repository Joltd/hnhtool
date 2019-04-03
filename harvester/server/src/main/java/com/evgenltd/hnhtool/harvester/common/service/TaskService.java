package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Task;
import com.evgenltd.hnhtool.harvester.common.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 00:28</p>
 */
@Service
public class TaskService {

    private AgentService agentService;
    private TaskRepository taskRepository;
    private Map<String, Module> modules;

    public TaskService(
            final AgentService agentService,
            final TaskRepository taskRepository,
            final List<Module> modules
    ) {
        this.agentService = agentService;
        this.taskRepository = taskRepository;
        this.modules = modules.stream()
                .collect(Collectors.toMap(module -> module.getClass().getSimpleName(), module -> module));
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void planTasks() {

        final List<Task> openTasks = taskRepository.findByStatus(Task.Status.OPEN);
        if (openTasks.isEmpty()) {
            return;
        }

        for (final Task task : openTasks) {
            final Predicate<Agent> requirements = getTaskRequirements(task);
            if (requirements == null) {
                continue;
            }

            final boolean requirementsPassed = agentService.checkRequirements(requirements);
            if (!requirementsPassed) {
                taskRepository.rejectTask(task);
                continue;
            }

            agentService.offerWork(getTaskWork(task));
            // increment trying count, and do something if it too big
        }

    }

    private Predicate<Agent> getTaskRequirements(final Task task) {
        final Module module = modules.get(task.getModule());
        if (module == null) {
            return null;
        }

        return module.getTaskRequirements(task.getStep());
    }

    private Runnable getTaskWork(final Task task) {
        final Module module = modules.get(task.getModule());
        if (module == null) {
            return null;
        }

        return () -> {
            try {
                taskRepository.startTask(task);
                module.getTaskWork(task.getStep()).run();
                taskRepository.doneTask(task);
            } catch (Throwable e) {
                taskRepository.failTask(task);
            }
        };
    }

}
