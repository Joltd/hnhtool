package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    default Area findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Area with id [%s] not found", id));
    }

    List<Area> findBySpaceId(Long spaceId);

    @Query("""
            select a from Area a
            where
                a.space.id = ?1 
                and a.from.x <= ?2 and a.to.x >= ?2
                and a.from.y <= ?3 and a.to.y >= ?3
            """)
    List<Area> findByPosition(Long spaceId, Integer x, Integer y);

}
