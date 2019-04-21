package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:52</p>
 */
@Repository
public interface KnownItemRepository extends JpaRepository<KnownItem, Long> {

    List<KnownItem> findByOwnerId(final Long ownerId);

    List<KnownItem> findByParentId(final Long parentId);

}
