package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 00:27</p>
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(final Task.Status status);

    default void rejectTask(final Task task) {
        task.setStatus(Task.Status.REJECTED);
        save(task);
    }

    default void startTask(final Task task) {
        task.setStatus(Task.Status.IN_PROGRESS);
        save(task);
    }

    default void doneTask(final Task task) {
        task.setStatus(Task.Status.DONE);
        save(task);
    }

    default void failTask(final Task task) {
        task.setStatus(Task.Status.FAILED);
        save(task);
    }

}
