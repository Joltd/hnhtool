package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
