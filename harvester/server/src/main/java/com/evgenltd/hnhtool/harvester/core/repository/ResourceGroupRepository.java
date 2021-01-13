package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.ResourceGroup;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, Long> {

    default ResourceGroup findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("ResourceGroup with id [%s] not found", id));
    }

    @Query("""
            select rg from ResourceGroup rg 
            left join fetch rg.resources r
            where
                :name is null or r.name like :name
            """)
    List<ResourceGroup> list(@Param("name") String name, Pageable page);

}
