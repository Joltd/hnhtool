package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    default Task findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Task with id [%s] not found", id));
    }

    List<Task> findByStatusOrderByActualDesc(Task.Status status);

}
