package com.evgenltd.hnhtool.harvester.modules.learning.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.evgenltd.hnhtool.harvester.core.service.A;
import com.evgenltd.hnhtool.harvester.core.service.TaskService;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.LearningStat;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningRepository;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningStatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LearningScript {

    private final LearningRepository learningRepository;
    private final LearningStatRepository learningStatRepository;
    private final TaskService taskService;

    public LearningScript(
            final LearningRepository learningRepository,
            final LearningStatRepository learningStatRepository,
            final TaskService taskService
    ) {
        this.learningRepository = learningRepository;
        this.learningStatRepository = learningStatRepository;
        this.taskService = taskService;
    }

    public void execute() {
        final Task task = A.getTask();
        final Agent agent = A.getAgentContext().getAgent();

        final LearningStat learningStat = new LearningStat();
        learningStat.setDate(LocalDateTime.now());
        learningStat.setAgent(agent.asString());
        learningStat.setTask(task);
        learningStatRepository.save(learningStat);

        final Optional<Learning> learning = learningRepository.findByJobId(task.getJob().getId());
        if (learning.isEmpty()) {
            taskService.failTask(task, "Task is not actual for learning");
            return;
        }



    }

}
