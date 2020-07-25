package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.ResourceGroup;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 10-04-2020 00:04</p>
 */
@Repository
public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, Long> {

    default ResourceGroup loadById(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("There is no entry [%s]", id));
    }

    @Query("""
            select rg from ResourceGroup rg 
            left join fetch rg.resources r
            where
                :name is null or r.name like :name
            """)
    List<ResourceGroup> list(@Param("name") String name, Pageable page);

}
