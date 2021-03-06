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

    default KnownObject findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("KnownObject with id [%s] not found", id));
    }

    Optional<KnownObject> findByResourceName(String resourceName);

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
    List<KnownObject> findPropInArea(Long spaceId, Integer x1, Integer y1, Integer x2, Integer y2);

    @SuppressWarnings("JpaQlInspection")
    @Query("""
            select ko 
            from KnownObject ko 
            left join fetch ko.children
            left join ko.resource r
            where
                ko.space.id = ?1
                and ko.position.x >= ?2
                and ko.position.y >= ?3
                and ko.position.x <= ?4
                and ko.position.y <= ?5
                and r.visual = 'PROP'
                and (r.box = true or r.heap = true)
                and ko.lost = false
                and ko.invalid = false
            """)
    List<KnownObject> findContainerInArea(Long spaceId, Integer x1, Integer y1, Integer x2, Integer y2);

    @Query("""
            select ko 
            from KnownObject ko 
            left join fetch ko.children
            left join ko.resource r
            where
                ko.space.id = ?1
                and ko.position.x >= ?2
                and ko.position.y >= ?3
                and ko.position.x <= ?4
                and ko.position.y <= ?5
                and r.visual = 'PROP'
                and r.heap = true
                and ko.lost = false
                and ko.invalid = true
            """)
    List<KnownObject> findInvalidHeapInArea(Long spaceId, Integer x1, Integer y1, Integer x2, Integer y2);

    @SuppressWarnings("JpaQlInspection")
    @Query("""
            select f 
            from KnownObject f, KnownObject s
            where
                f.resource.name = ?1
                and s.resource.name = ?4
                and (s.position.x - f.position.x) = (?5 - ?2)
                and (s.position.y - f.position.y) = (?6 - ?3)
                and f.lost = false
                and s.lost = false
                and f.resource.visual = 'PROP'
                and s.resource.visual = 'PROP'
            """)
    List<KnownObject> findObjectByPattern(
            final String firstResource,
            final Integer firstX,
            final Integer firstY,
            final String secondResource,
            final Integer secondX,
            final Integer secondY
    );

    List<KnownObject> findByParentIdAndPlace(Long parentId, KnownObject.Place place);

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
    Range calculateRange(Long spaceId);

    List<KnownObject> findBySpaceIdAndLostIsFalse(Long spaceId);

    List<KnownObject> findByResourceNameLikeAndLostIsFalse(final String resourceName);

    List<KnownObject> findByResourceHeapIsTrueAndInvalidIsTrue();

}
