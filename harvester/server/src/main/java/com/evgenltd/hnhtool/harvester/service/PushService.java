package com.evgenltd.hnhtool.harvester.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.entity.TaskEvent;
import com.evgenltd.hnhtools.common.Assert;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 14-03-2019 22:08</p>
 */
@Service
public class PushService {

    public static final String TASKS_TOPIC = Application.BROKER_PATH + "/tasks";

    private SimpMessagingTemplate messagingTemplate;

    public PushService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTaskUpdate(@NotNull final TaskEvent taskEvent) {
        Assert.valueRequireNonEmpty(taskEvent, "TaskEvent");
        messagingTemplate.convertAndSend(TASKS_TOPIC, taskEvent);
    }

}
