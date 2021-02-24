package com.evgenltd.hnhtool.harvester.modules.learning.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.core.repository.AgentRepository;
import com.evgenltd.hnhtool.harvester.core.repository.AreaRepository;
import com.evgenltd.hnhtool.harvester.core.service.TaskService;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.LearningStat;
import com.evgenltd.hnhtool.harvester.modules.learning.record.LearningRecord;
import com.evgenltd.hnhtool.harvester.modules.learning.record.LearningStatRecord;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningRepository;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningStatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final LearningRepository learningRepository;
    private final LearningStatRepository learningStatRepository;
    private final TaskService taskService;
    private final AgentRepository agentRepository;
    private final AreaRepository areaRepository;

    public LearningService(
            final LearningRepository learningRepository,
            final LearningStatRepository learningStatRepository,
            final TaskService taskService,
            final AgentRepository agentRepository,
            final AreaRepository areaRepository
    ) {
        this.learningRepository = learningRepository;
        this.learningStatRepository = learningStatRepository;
        this.taskService = taskService;
        this.agentRepository = agentRepository;
        this.areaRepository = areaRepository;
    }

//    @Scheduled(cron = "0 */20 * * * *")
    public void handle() {
        final List<Learning> learnings = learningRepository.findAll();
        for (final Learning learning : learnings) {
            if (!learning.isEnabled()) {
                continue;
            }

            final boolean hasActualTask = taskService.hasActualTaskForJob(learning);
            if (hasActualTask) {
                continue;
            }

            taskService.createTask(learning, LearningScript.class.getName(), learning.getAgent());
        }
    }

    public PageData<LearningStatRecord> listStatePageable(final Long learningId, final PageRequest pageRequest) {
        final Page<LearningStat> result = learningStatRepository.findByLearningId(learningId, pageRequest);
        return new PageData<>(
                result.get().map(LearningStatRecord::of).collect(Collectors.toList()),
                result.getTotalElements()
        );
    }

    public void save(final LearningRecord learningRecord) {

        final Learning learning = learningRecord.getId() != null
                ? learningRepository.findOne(learningRecord.getId())
                : new Learning();

        learning.setEnabled(learningRecord.isEnabled());
        learning.setName(learningRecord.getName());
        learning.setType(learningRecord.getType());

        final Agent agent = agentRepository.findOne(learningRecord.getAgent());
        learning.setAgent(agent);

        final Area area = areaRepository.findOne(learningRecord.getArea());
        learning.setArea(area);

        learningRepository.save(learning);

    }



}
