package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
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

        final List<String> resources = visuals.stream()
                .map(resourceGetter)
                .collect(Collectors.toList());

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

    private Resource storeUnknownResource(final String name) {
        final Resource resource = new Resource();
        resource.setName(name);
        resource.setUnknown(true);
        return resourceRepository.save(resource);
    }

}
