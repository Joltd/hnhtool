package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.research.command.DropItemInInventory;
import com.evgenltd.hnhtool.harvester.research.command.OpenStack;
import com.evgenltd.hnhtool.harvester.research.command.TakeItemFromStack;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(BaseBehaviorTest.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private KnownObjectRepository knownObjectRepository;

    @Autowired
    private KnownItemRepository knownItemRepository;

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

            final KnownObject stackObject = knownObjectRepository.findById(13L).get();
            final Result<Void> result = OpenStack.perform(agent, stackObject)
                    .thenCombine(() -> TakeItemFromStack.perform(agent, stackObject))
                    .thenCombine(() -> DropItemInInventory.performImpl(
                            agent,
                            agent.getClient().getCharacterId(),
                            new IntPoint(1, 1)
                    ));

//            final KnownObject knownObject = knownObjectRepository.findById(3L).get();
//            final Result<Void> result = OpenContainer.perform(agent, knownObject)
//                    .then(() -> {
//                        log.info("Transfer start");
//                        final List<KnownItem> fibers = knownItemRepository.findByOwnerIdAndResource(
//                                3L,
//                                "gfx/invobjs/hempfibre"
//                        );
//                        for (final KnownItem fiber : fibers) {
//                            TransferItem.perform(agent, fiber);
//                            log.info("Transfer item {} done", fiber.getId());
//                        }
//                    });
//            log.info("Task complete");
            return Result.ok();
        });
        Thread.sleep(60 * 60 * 1000L);
    }

}
