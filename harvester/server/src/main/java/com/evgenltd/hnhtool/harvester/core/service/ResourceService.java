package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 23:11</p>
 */
@Service
@Transactional
public class ResourceService {

    private ResourceRepository resourceRepository;

    public ResourceService(final ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
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

    private Resource storeUnknownResource(final String name) {
        final Resource resource = new Resource();
        resource.setName(name);
        resource.setUnknown(true);
        return resourceRepository.save(resource);
    }

}
