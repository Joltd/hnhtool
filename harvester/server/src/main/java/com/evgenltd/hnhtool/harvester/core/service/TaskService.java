package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.evgenltd.hnhtool.harvester.core.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AgentService agentService;

    public TaskService(
            final TaskRepository taskRepository,
            final AgentService agentService
    ) {
        this.taskRepository = taskRepository;
        this.agentService = agentService;
        Executors.newFixedThreadPool(1);
    }

    public void doSchedule() {
        final List<Task> tasks = taskRepository.findAll(Sort.by("actual"));
        for (final Task task : tasks) {
//            final Agent agent = agentService.takeAgent();
//            if (agent == null) {
//                return;
//            }
//
//            final AgentLog agentLog = new AgentLog();
//            agentLog.setDate(LocalDateTime.now());
//            agentLog.setAccount(agent.getAccount());
//            agentLog.setTask(task);

        }
    }

}
