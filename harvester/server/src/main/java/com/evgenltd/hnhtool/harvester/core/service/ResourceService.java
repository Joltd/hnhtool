package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.ResourceGroup;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceGroupRepository;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceGroupRepository resourceGroupRepository;

    public ResourceService(
            final ResourceRepository resourceRepository,
            final ResourceGroupRepository resourceGroupRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.resourceGroupRepository = resourceGroupRepository;
    }

    public Resource findByName(final String name) {
        return resourceRepository.findByName(name)
                .orElseGet(() -> storeUnknownResource(name));
    }

    public <T> Map<String, Resource> loadResourceIndexByNames(final List<T> visuals, final Function<T,String> resourceGetter) {

        final Set<String> resources = visuals.stream()
                .map(resourceGetter)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<String, Resource> index = resourceRepository.findAllByNameIn(resources)
                .stream()
                .collect(Collectors.toMap(Resource::getName, resource -> resource));

        for (final String resource : resources) {
            if (index.containsKey(resource)) {
                continue;
            }

            final Resource newResource = storeUnknownResource(resource);
            index.put(resource, newResource);
        }

        return index;

    }

    public List<String> loadResourceOfGroup(final String name) {
        final Resource resource = findByName(name);
        if (resource.getGroup() == null) {
            return Collections.singletonList(name);
        }

        return resource.getGroup()
                .getResources()
                .stream()
                .map(Resource::getName)
                .collect(Collectors.toList());
    }

    public void updateGroup(final Long resourceId, final Long groupId) {
        final Resource resource = resourceRepository.findOne(resourceId);
        if (groupId == null) {
            resource.setGroup(null);
        } else if (groupId == -1) {
            final ResourceGroup group = resourceGroupRepository.save(new ResourceGroup());
            resource.setGroup(group);
        } else {
            final ResourceGroup resourceGroup = resourceGroupRepository.findOne(groupId);
            resource.setGroup(resourceGroup);
        }
    }

    private Resource storeUnknownResource(final String name) {
        final Resource resource = new Resource();
        resource.setName(name);
        resource.setUnknown(true);
        if (name.contains("terobjs")) {
            resource.setProp(true);
        } else if (name.contains("invobjs")) {
            resource.setItem(true);
        }
        return resourceRepository.save(resource);
    }

}
