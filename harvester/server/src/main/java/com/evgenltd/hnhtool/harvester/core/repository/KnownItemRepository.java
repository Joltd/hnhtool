package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.KnownItem;
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

    void deleteByOwnerId(final Long ownerId);

    List<KnownItem> findByOwnerIdAndPlace(final Long ownerId, final KnownItem.Place place);

}
