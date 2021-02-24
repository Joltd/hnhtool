package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.record.JobRecord;
import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.core.service.JobService;
import com.evgenltd.hnhtool.harvester.modules.Modules;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    public JobController(final JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/type")
    public List<String> type() {
        return Stream.of(Modules.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @GetMapping
    public PageData<JobRecord> listPageable(
            @RequestParam final int page,
            @RequestParam final int size
    ) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        return jobService.listPageable(pageRequest);
    }

}
