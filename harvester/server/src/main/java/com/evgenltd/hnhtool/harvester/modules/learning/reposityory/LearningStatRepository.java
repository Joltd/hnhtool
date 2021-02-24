package com.evgenltd.hnhtool.harvester.modules.learning.reposityory;

import com.evgenltd.hnhtool.harvester.modules.learning.entity.LearningStat;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningStatRepository extends JpaRepository<LearningStat, Long> {

    default LearningStat findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("LearningStat with id [%s] not found", id));
    }

    Page<LearningStat> findByLearningId(Long learningId, Pageable pageable);

}
