package com.evgenltd.hnhtool.harvester.common.repository;

import com.evgenltd.hnhtool.harvester.common.entity.Task;
import com.evgenltd.hnhtool.harvester.common.service.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("select t from Task t where t.status = 'OPEN' and not exists (select f from Task f where t.module = f.module and t.step = f.step and f.status = 'FAILED')")
    List<Task> findOpenNotFailedTasks();

    default Task openTask(final Class<? extends Module> module, final String step) {
        final Task task = new Task();
        task.setModule(module.getSimpleName());
        task.setStep(step);
        task.setStatus(Task.Status.OPEN);
        return save(task);
    }

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

    default void failTask(final Task task, final String reason) {
        task.setStatus(Task.Status.FAILED);
        task.setFailReason(reason);
        save(task);
    }

}
