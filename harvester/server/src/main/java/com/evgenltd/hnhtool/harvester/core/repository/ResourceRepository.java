package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-12-2019 01:05</p>
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByName(String name);

    List<Resource> findAllByNameIn(Iterable<String> names);

    @Query("""
            select r from Resource r 
            where
                (:name is null or r.name = :name)
                and (:unknown is null or r.unknown = :unknown)
            """)
    List<Resource> list(@Param("name") String name, @Param("unknown") Boolean unknown, Pageable page);

}
