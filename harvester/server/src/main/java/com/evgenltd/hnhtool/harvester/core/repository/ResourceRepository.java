package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    default Resource loadById(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("There is no entry [%s]", id));
    }

    Optional<Resource> findByName(String name);

    List<Resource> findAllByNameIn(Iterable<String> names);

    @Query("""
            select r from Resource r 
            where
                (:name is null or r.name like :name)
                and (:unknown is null or :unknown = false or r.unknown = :unknown)
            """)
    List<Resource> list(@Param("name") String name, @Param("unknown") Boolean unknown, Pageable page);

}
