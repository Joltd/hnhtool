package com.evgenltd.hnhtool.harvester.modules.learning.service;

import com.evgenltd.hnhtool.harvester.core.entity.Job;
import com.evgenltd.hnhtool.harvester.core.service.TaskService;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningService {

    private final LearningRepository learningRepository;
    private final TaskService taskService;

    public LearningService(
            final LearningRepository learningRepository,
            final TaskService taskService
    ) {
        this.learningRepository = learningRepository;
        this.taskService = taskService;
    }

    @Scheduled(cron = "0 */20 * * * *")
    public void handle() {
        final List<Learning> learnings = learningRepository.findAll();
        for (final Learning learning : learnings) {
            final Job job = learning.getJob();
            if (!job.isEnabled()) {
                continue;
            }

            final boolean hasActualTask = taskService.hasActualTaskForJob(job);
            if (hasActualTask) {
                continue;
            }

            taskService.createTask(job, LearningScript.class.getName(), learning.getAgent());
        }
    }

}
