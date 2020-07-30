package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.record.Range;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnownObjectRepository extends JpaRepository<KnownObject, Long> {

    default KnownObject loadById(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("There is no entry [%s]", id));
    }

    Optional<KnownObject> findByResourceName(final String resourceName);

    @SuppressWarnings("JpaQlInspection")
    @Query("""
            select ko 
            from KnownObject ko 
            left join ko.resource r
            where
                ko.space.id = ?1
                and ko.position.x >= ?2
                and ko.position.y >= ?3
                and ko.position.x <= ?4
                and ko.position.y <= ?5
                and r.visual = 'PROP'
            """)
    List<KnownObject> findObjectsInArea(final Long spaceId, final Integer x1, final Integer y1, final Integer x2, final Integer y2);

    @SuppressWarnings("JpaQlInspection")
    @Query("""
            select f 
            from KnownObject f, KnownObject s
            left join s.resource r 
            where
                f.resource.name = ?1
                and s.resource.name = ?4
                and (s.position.x - f.position.y) = (?5 - ?2)
                and (s.position.y - f.position.y) = (?6 - ?3)
                and f.lost = false
                and s.lost = false
                and r.visual = 'PROP'
            """)
    List<KnownObject> findObjectByPattern(
            final String firstResource,
            final Integer firstX,
            final Integer firstY,
            final String secondResource,
            final Integer secondX,
            final Integer secondY
    );

    List<KnownObject> findByParentIdAndPlace(final Long parentId, final KnownObject.Place place);

    @Query("""
            select new com.evgenltd.hnhtool.harvester.core.record.Range(
                coalesce(min(ko.position.x), 0), 
                coalesce(min(ko.position.y), 0),
                coalesce(max(ko.position.x), 0), 
                coalesce(max(ko.position.y), 0)
            ) from KnownObject ko
            where 
                ko.space.id = ?1
            """)
    Range calculateRange(final Long spaceId);

}
