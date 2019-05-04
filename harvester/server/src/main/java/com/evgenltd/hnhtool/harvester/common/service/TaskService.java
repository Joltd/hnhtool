package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Task;
import com.evgenltd.hnhtool.harvester.common.entity.Work;
import com.evgenltd.hnhtools.common.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
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

    private static final Logger log = LogManager.getLogger(TaskService.class);

    private AgentService agentService;

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, Task> index = new ConcurrentHashMap<>();

    public TaskService(final AgentService agentService) {
        this.agentService = agentService;
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void planTasks() {

        final List<Task> openTasks = index.values()
                .stream()
                .filter(task -> task.getStatus().equals(Task.Status.OPEN))
                .collect(Collectors.toList());
        if (openTasks.isEmpty()) {
            return;
        }

        for (final Task task : openTasks) {
            if (task.getRequirements() == null) {
                continue;
            }

            final boolean requirementsPassed = agentService.checkRequirements(task.getRequirements());
            if (!requirementsPassed) {
                rejectTask(task);
                continue;
            }

            agentService.offerWork(task.getWork());
            // increment trying count, and do something if it too big
        }

    }

    public Task openTask(final Work work) {
        return openTask(work, agent -> true);
    }

    public Task openTask(final Work work, final Predicate<Agent> requirements) {
        final Task task = new Task();
        task.setId(idGenerator.getAndIncrement());
        task.setWork(workWrapper(task, work));
        task.setRequirements(requirements);
        task.setStatus(Task.Status.OPEN);
        index.put(task.getId(), task);
        return task;
    }

    private Work workWrapper(final Task task, final Work work) {
        return agent -> {
            try {
                TaskContext.setupContext(agent);
                startTask(task);
                final Result<Void> result = work.apply(agent);
                if (result.isSuccess()) {
                    doneTask(task);
                } else {
                    failTask(task, result.getCode());
                }
            } catch (Throwable e) {
                log.error("Unable to perform task", e);
                failTask(task, ServerResultCode.EXCEPTION_DURING_TASK_PERFORMING);
            } finally {
                TaskContext.clearContext();
            }
            return Result.ok();
        };
    }

    private void rejectTask(final Task task) {
        task.setStatus(Task.Status.REJECTED);
    }

    private void startTask(final Task task) {
        task.setStatus(Task.Status.IN_PROGRESS);
    }

    private void doneTask(final Task task) {
        task.setStatus(Task.Status.DONE);
    }

    private void failTask(final Task task, final String reason) {
        task.setStatus(Task.Status.FAILED);
        task.setFailReason(reason);
    }

}
