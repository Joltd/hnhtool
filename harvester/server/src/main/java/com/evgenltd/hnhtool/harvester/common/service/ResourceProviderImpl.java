package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.repository.ResourceRepository;
import com.evgenltd.hnhtools.agent.ResourceProvider;
import com.evgenltd.hnhtools.entity.WorldObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 00:04</p>
 */
@Service
public class ResourceProviderImpl implements ResourceProvider {

    private ResourceRepository resourceRepository;

    public ResourceProviderImpl(final ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Nullable
    public String getResourceName(@NotNull final Long id) {
        return resourceRepository.findById(id)
                .map(Resource::getName)
                .orElseGet(() -> {
                    saveResource(id, null);
                    return null;
                });
    }

    @Override
    public void saveResource(@NotNull final Long id, @Nullable final String name) {
        final Resource resource = new Resource();
        resource.setId(id);
        resource.setName(name);
        resourceRepository.save(resource);
    }

    public void initWorldObjectResource(@NotNull final WorldObject worldObject) {
        final String resourceName = getResourceName(worldObject.getResourceId());
        worldObject.setResourceName(resourceName);
    }

}
