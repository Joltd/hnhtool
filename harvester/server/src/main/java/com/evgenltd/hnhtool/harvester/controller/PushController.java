package com.evgenltd.hnhtool.harvester.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 14-03-2019 22:07</p>
 */
@Controller
public class PushController {

    @MessageMapping("/tasks")
    public String task() {
        return "Oh, hi";
    }

}
