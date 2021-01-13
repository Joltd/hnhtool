package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    default Space findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Space with id [%s] not found", id));
    }

    List<Space> findByType(Space.Type type);

}
