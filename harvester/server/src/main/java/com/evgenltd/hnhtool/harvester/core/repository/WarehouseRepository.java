package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Warehouse;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    default Warehouse findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Warehouse with id [%s] not found", id));
    }

    List<Warehouse> findBySpaceId(Long spaceId);

}
