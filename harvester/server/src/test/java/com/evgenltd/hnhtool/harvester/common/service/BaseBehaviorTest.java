package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.common.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

            try {
                Thread.sleep(10000L);
            } catch (InterruptedException ignored) {
            }
//
//            final int currentInventories = agent.getClient().getInventories().size();
//
//            final KnownObject container = knownObjectRepository.findUnknownContainers().get(0);
//            agent.getMatchedWorldObjectId(container.getId())
//                    .then(id -> agent.getClient().setParentIdForNewInventory(id))
//                    .then(id -> agent.getClient().interact(id))
//                    .then(() -> CommandUtils.await(() -> agent.getClient().getInventories().size() > currentInventories));

            return Result.ok();
        });
        Thread.sleep(60 * 60 * 1000L);
    }

}
