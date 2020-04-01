package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2020 23:58</p>
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(final ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public List<ResourceRecord> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "unknown", required = false) final Boolean unknown
    ) {
        if (page == null) {
            page = 0;
        }
        return resourceRepository.list(name, unknown, PageRequest.of(page, 16))
                .stream()
                .map(resource -> new ResourceRecord(resource.getId(), resource.getName(), resource.getVisual(), resource.getUnknown()))
                .collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record ResourceRecord(
            long id,
            String name,
            Resource.Visual visual,
            boolean unknown
    ) {}

}
