package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:52</p>
 */
@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByType(final Space.Type type);

}
