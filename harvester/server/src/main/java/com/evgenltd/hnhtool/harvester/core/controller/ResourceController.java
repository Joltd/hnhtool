package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceGroupRepository;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import com.evgenltd.hnhtool.harvester.core.service.ResourceService;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
    private final ResourceGroupRepository resourceGroupRepository;
    private final ResourceService resourceService;

    public ResourceController(
            final ResourceRepository resourceRepository,
            final ResourceGroupRepository resourceGroupRepository,
            final ResourceService resourceService
    ) {
        this.resourceRepository = resourceRepository;
        this.resourceGroupRepository = resourceGroupRepository;
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceRecord> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "unknown", required = false) final Boolean unknown
    ) {
        if (page == null) {
            page = 0;
        }
        if (size == null || size < 0) {
            size = 10;
        }
        name = Assert.isEmpty(name)
                ? null
                : String.format("%%%s%%", name);
        return resourceRepository.list(name, unknown, PageRequest.of(page, size))
                .stream()
                .map(resource -> new ResourceRecord(resource.getId(), resource.getName(), resource.getVisual(), resource.getUnknown(),
                        resource.isBox(),
                        resource.isHeap(),
                        resource.isItem(),
                        resource.getSize().getX(),
                        resource.getSize().getY()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResourceRecord byId(@PathVariable(value = "id") final Long id) {
        return resourceRepository.findById(id)
                .map(resource -> new ResourceRecord(
                        resource.getId(),
                        resource.getName(),
                        resource.getVisual(),
                        resource.getUnknown(),
                        resource.isBox(),
                        resource.isHeap(),
                        resource.isItem(),
                        resource.getSize().getX(),
                        resource.getSize().getY()
                ))
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource [%s] not found", id)));
    }

    @PostMapping
    public void update(@RequestBody final ResourceRecord resourceRecord) {
        final Resource resource = new Resource();
        resource.setId(resourceRecord.id());
        resource.setName(resourceRecord.name());
        resource.setVisual(resourceRecord.visual());
        resource.setUnknown(resourceRecord.unknown());
        resource.setBox(resourceRecord.box());
        resource.setHeap(resourceRecord.heap());
        resource.setItem(resourceRecord.item());
        resource.setSize(new IntPoint(resourceRecord.x(), resourceRecord.y()));
        resourceRepository.save(resource);
    }

    @GetMapping("/group")
    public List<ResourceGroupRecord> listGroup(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "name", required = false) String name
    ) {
        if (page == null) {
            page = 0;
        }
        if (size == null || size < 0) {
            size = 10;
        }
        name = Assert.isEmpty(name)
                ? null
                : String.format("%%%s%%", name);
        return resourceGroupRepository.list(name, PageRequest.of(page, size))
                .stream()
                .map(resourceGroup -> new ResourceGroupRecord(
                        resourceGroup.getId(),
                        resourceGroup.getResources()
                                .stream()
                                .map(Resource::getName)
                                .collect(Collectors.joining("\n"))
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/group")
    public void updateGroup(
            @RequestParam("resource") final Long resourceId,
            @RequestParam(value = "group", required = false) final Long groupId
    ) {
        resourceService.updateGroup(resourceId, groupId);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record ResourceRecord(
            Long id,
            String name,
            Resource.Visual visual,
            boolean unknown,
            boolean box, boolean heap, boolean item, int x, int y
    ) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record ResourceGroupRecord(long id, String resources) {}

}
