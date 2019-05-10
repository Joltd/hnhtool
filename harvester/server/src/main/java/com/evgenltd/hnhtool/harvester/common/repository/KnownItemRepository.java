package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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

    Optional<KnownItem> findByOwnerIdAndXAndY(final Long ownerId, final Integer x, final Integer y);

    default Result<KnownItem> findByPosition(final Long ownerId, final IntPoint position) {
        return findByOwnerIdAndXAndY(ownerId, position.getX(), position.getY())
                .map(Result::ok)
                .orElse(Result.fail(ServerResultCode.ITEM_NOT_FOUND));
    }

    @Transactional
    @Modifying
    @Query("delete from KnownItem where owner.id = ?1 and x > ?2")
    void deleteFromStack(final Long ownerId, final Integer depth);

}
