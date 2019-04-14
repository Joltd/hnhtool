package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
public interface KnownObjectRepository extends JpaRepository<KnownObject, Long> {

    @Query("select ko from KnownObject ko where ko.owner = ?1 and ko.x >= ?2 and ko.y >= ?3 and ko.x <= ?4 and ko.y <= ?5 and ko.player is null")
    List<KnownObject> findObjectsInArea(final Space space, final Integer x1, final Integer y1, final Integer x2, final Integer y2);

    @Query("select ko from KnownObject ko where ko.doorway = true and ko.researched is null")
    List<KnownObject> findUnknownDoorways();

    @Query("select ko from KnownObject ko where ko.owner = ?1 and ko.doorway = true order by sqrt(power(convert(decimal(38,0),ko.x) - ?2, 2) + power(convert(decimal(38,0), ko.y) - ?3, 2))")
    List<KnownObject> findNearestDoorway(final Space owner, final Integer x, final Integer y);

    @Query("select ko from KnownObject ko where ko.container = true and ko.researched is null")
    List<KnownObject> findUnknownContainers();

    @Query(value = "", nativeQuery = true)
    List<SpaceInfo> findSpaceByPattern(
            final Long firstResource,
            final Integer firstX,
            final Integer firstY,
            final Long secondResource,
            final Integer secondX,
            final Integer secondY
    );

    default void markAsResearched(final KnownObject knownObject) {
        knownObject.setResearched(true);
        save(knownObject);
    }

    interface SpaceInfo {
        Long getSpaceId();
        Integer getX();
        Integer getY();
    }

}
