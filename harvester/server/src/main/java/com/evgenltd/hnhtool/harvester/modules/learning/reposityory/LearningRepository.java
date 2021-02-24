package com.evgenltd.hnhtool.harvester.modules.learning.reposityory;

import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

    default Learning findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Learning with id [%s] not found", id));
    }

}
