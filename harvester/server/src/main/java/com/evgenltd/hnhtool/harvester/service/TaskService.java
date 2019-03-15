package com.evgenltd.hnhtool.harvester.service;

import com.evgenltd.hnhtool.harvester.entity.DummyTask;
import com.evgenltd.hnhtool.harvester.entity.TaskEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 15-03-2019 13:45
 */
@Service
public class TaskService {

    private List<DummyTask> tasks = new ArrayList<>();
    private AtomicLong idHolder = new AtomicLong(2000);

    private PushService pushService;

    public TaskService(final PushService pushService) {
        this.pushService = pushService;
    }

    @Scheduled(fixedDelay = 1000L)
    public void handleTask() {
        if (Math.random() > 0.5 || tasks.isEmpty()) {

            final DummyTask task = new DummyTask();
            final long id = idHolder.getAndIncrement();
            task.setId(id);
            task.setStart(System.currentTimeMillis());
            task.setName("DummyTask#" + task.getStart());
            tasks.add(task);

            pushService.sendTaskUpdate(TaskEvent.start(task.getId(), task.getName(), task.getStart()));

        } else {

            final int targetIndex = (int) (Math.random() * tasks.size());
            final DummyTask task = tasks.remove(targetIndex);
            task.setEnd(System.currentTimeMillis());

            pushService.sendTaskUpdate(TaskEvent.end(task.getId(), task.getName(), task.getEnd()));

        }
    }

}
