package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtools.common.ApplicationException;
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

    default KnownObject findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("There is no KnownObject with id=[%s]", id));
    }

    @Query("select ko from KnownObject ko where ko.space = ?1 and ko.x >= ?2 and ko.y >= ?3 and ko.x <= ?4 and ko.y <= ?5 and ko.resource.prop = true")
    List<KnownObject> findObjectsInArea(final Space space, final Integer x1, final Integer y1, final Integer x2, final Integer y2);

    @Query(value = "select f from KnownObject f, KnownObject s where f.resource.name = ?1 and s.resource.name = ?4 " +
            "and (s.x - f.x) = (?5 - ?2) and (s.y - f.y) = (?6 - ?3) and f.lost = false and s.lost = false and s.resource.prop = true")
    List<KnownObject> findObjectByPattern(
            final String firstResource,
            final Integer firstX,
            final Integer firstY,
            final String secondResource,
            final Integer secondX,
            final Integer secondY
    );

    @Query("select ko from KnownObject ko where ko.resource.player = true and ko.resource.name = ?1")
    Optional<KnownObject> findCharacterObject(String characterName);

    @Query("select ko from KnownObject ko where ko.lost = false and ko.resource.container = true")
    List<KnownObject> findContainers();

    List<KnownObject> findByParentIdAndPlace(final Long parentId, final KnownObject.Place place);

}
