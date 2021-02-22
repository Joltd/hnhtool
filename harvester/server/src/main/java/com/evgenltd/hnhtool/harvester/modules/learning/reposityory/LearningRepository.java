package com.evgenltd.hnhtool.harvester.modules.learning.reposityory;

import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

    Optional<Learning> findByJobId(Long jobId);

}
