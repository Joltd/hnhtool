package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Job;
import com.evgenltd.hnhtool.harvester.core.record.JobRecord;
import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.core.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(
            final JobRepository jobRepository
    ) {
        this.jobRepository = jobRepository;
    }

    public PageData<JobRecord> listPageable(final PageRequest pageRequest) {
        final Page<Job> result = jobRepository.findAll(pageRequest);
        final List<JobRecord> data = result.get()
                .map(JobRecord::of)
                .collect(Collectors.toList());
        return new PageData<>(data, result.getTotalElements());
    }

}
