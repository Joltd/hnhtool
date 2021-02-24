package com.evgenltd.hnhtool.harvester.modules.learning.controller;

import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import com.evgenltd.hnhtool.harvester.modules.learning.record.LearningRecord;
import com.evgenltd.hnhtool.harvester.modules.learning.record.LearningStatRecord;
import com.evgenltd.hnhtool.harvester.modules.learning.reposityory.LearningRepository;
import com.evgenltd.hnhtool.harvester.modules.learning.service.LearningService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learning")
public class LearningController {

    private final LearningRepository learningRepository;
    private final LearningService learningService;

    public LearningController(
            final LearningRepository learningRepository,
            final LearningService learningService
    ) {
        this.learningRepository = learningRepository;
        this.learningService = learningService;
    }

    @GetMapping("/{id}/stat")
    public PageData<LearningStatRecord> listStat(
            @PathVariable final Long id,
            @RequestParam final int page,
            @RequestParam final int size
    ) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        return learningService.listStatePageable(id, pageRequest);
    }

    @GetMapping("/{id}")
    public LearningRecord byId(@PathVariable final Long id) {
        final Learning learning = learningRepository.findOne(id);
        return LearningRecord.of(learning);
    }

    @PostMapping
    public void save(@RequestBody final LearningRecord learningRecord) {
        learningService.save(learningRecord);
    }

}
