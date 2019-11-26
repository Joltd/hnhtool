package com.evgenltd.hnhtool.harvester_old.research.repository;

import com.evgenltd.hnhtool.harvester_old.research.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-04-2019 01:04</p>
 */
@Repository
public interface PathRepository extends JpaRepository<Path, Long> {
}
