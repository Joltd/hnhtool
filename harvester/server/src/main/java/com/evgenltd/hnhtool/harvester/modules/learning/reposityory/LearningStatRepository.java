package com.evgenltd.hnhtool.harvester.modules.learning.reposityory;

import com.evgenltd.hnhtool.harvester.modules.learning.entity.LearningStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningStatRepository extends JpaRepository<LearningStat, Long> {



}
