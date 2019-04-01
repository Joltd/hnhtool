package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
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

    @Query("select ko from KnownObject ko where ko.x >= ?1 and ko.y >= ?2 and ko.x <= ?3 and ko.y <= ?4")
    List<KnownObject> findObjectsInArea(final Integer x1, final Integer y1, final Integer x2, final Integer y2);

}
