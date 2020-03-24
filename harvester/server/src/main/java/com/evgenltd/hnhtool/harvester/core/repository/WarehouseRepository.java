package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 23-03-2020 22:39</p>
 */
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
