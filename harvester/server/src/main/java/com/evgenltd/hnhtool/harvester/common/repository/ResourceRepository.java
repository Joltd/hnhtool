package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtools.complexclient.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 20:44</p>
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, ResourceProvider {

    @Override
    @Nullable
    default String getResourceName(@NotNull Long id) {
        return findById(id).map(Resource::getName).orElse(null);
    }

    @Override
    default void saveResource(@NotNull Long id, @Nullable String name) {
        final Resource resource = new Resource();
        resource.setId(id);
        resource.setName(name);
        save(resource);
    }

}
