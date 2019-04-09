package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.research.command.OpenInventory;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.Inventory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-04-2019 21:03</p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseBehaviorTest {

    @Autowired
    private AgentService agentService;

    @Autowired
    private KnownObjectRepository knownObjectRepository;

    @Test
    public void addAccount() {
        final Result<Void> result = agentService.registerAgent("Grafbredbery", "15051953", "Surname");
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void commonTest() throws InterruptedException {
        agentService.offerWork(agent -> {
            final List<KnownObject> containers = knownObjectRepository.findUnknownContainers();
            if (containers.isEmpty()) {
                return Result.ok();
            }
            final KnownObject knownObject = containers.get(0);
            final Result<Inventory> inventory = OpenInventory.perform(agent, knownObject);
            return Result.ok();
        });
        Thread.sleep(60 * 60 * 1000L);
    }

}
