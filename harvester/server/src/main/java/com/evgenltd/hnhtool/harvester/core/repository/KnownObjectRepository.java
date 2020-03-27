package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
public interface KnownObjectRepository extends JpaRepository<KnownObject, Long> {

    Optional<KnownObject> findByResourceName(final String resourceName);

    @Query("select ko from KnownObject ko where ko.space = ?1 and ko.x >= ?2 and ko.y >= ?3 and ko.x <= ?4 and ko.y <= ?5 and ko.resource.visual = Resource.Visual.PROP")
    List<KnownObject> findObjectsInArea(final Space space, final Integer x1, final Integer y1, final Integer x2, final Integer y2);

    @Query(value = "select f from KnownObject f, KnownObject s where f.resource.name = ?1 and s.resource.name = ?4 " +
            "and (s.x - f.x) = (?5 - ?2) and (s.y - f.y) = (?6 - ?3) and f.lost = false and s.lost = false and s.resource.visual = Resource.Visual.PROP")
    List<KnownObject> findObjectByPattern(
            final String firstResource,
            final Integer firstX,
            final Integer firstY,
            final String secondResource,
            final Integer secondX,
            final Integer secondY
    );

    List<KnownObject> findByParentIdAndPlace(final Long parentId, final KnownObject.Place place);

}
