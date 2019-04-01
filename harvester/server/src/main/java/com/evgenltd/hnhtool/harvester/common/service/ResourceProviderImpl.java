package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.repository.ResourceRepository;
import com.evgenltd.hnhtools.agent.ResourceProvider;
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
    public String getResourceName(@NotNull final Integer id) {
        return resourceRepository.findById(new Long(id))
                .map(Resource::getName)
                .orElse(null);
    }

    @Override
    public void saveResource(@NotNull final Integer id, @NotNull final String name) {
        final Resource resource = new Resource();
        resource.setId(new Long(id));
        resource.setName(name);
        resourceRepository.save(resource);
    }

    @Nullable
    public Resource findResource(@Nullable final Integer id) {
        if (id == null) {
            return null;
        }

        return resourceRepository.findById(id.longValue())
                .orElse(buildEmptyResource(id));
    }

    private Resource buildEmptyResource(final Integer id) {
        final Resource resource = new Resource();
        resource.setId(id.longValue());
        resourceRepository.save(resource);
        return resource;
    }

}
