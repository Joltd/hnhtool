package com.evgenltd.hnhtool.harvester.service;

import com.evgenltd.hnhtool.harvester.entity.TaskEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    private SimpMessagingTemplate messagingTemplate;

    public PushService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelay = 1000L)
    public void produce() {
        messagingTemplate.convertAndSend("/topic/tasks", new TaskEvent(100L, System.currentTimeMillis(), Math.random() > 0.5 ? "NEW" : "UPDATE"));
    }

//    public void sendTaskCreate() {
//
//    }
//
//    public void sendTask

}
