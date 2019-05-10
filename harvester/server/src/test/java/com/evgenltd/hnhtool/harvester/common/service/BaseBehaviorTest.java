package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester.common.repository.KnownItemRepository;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.research.command.OpenContainer;
import com.evgenltd.hnhtool.harvester.research.service.WarehouseService;
import com.evgenltd.hnhtools.common.Result;
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

    @Autowired
    private WarehouseService warehouseService;

    @Test
    public void addAccount() {
        final Result<Void> result = agentService.registerAgent("Grafbredbery", "15051953", "Surname");
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void commonTest() throws InterruptedException {
        agentService.offerWork(() -> {

            try {
                Thread.sleep(10_000L);
            } catch (InterruptedException ignored) {
            }

            knownObjectRepository.findById(3L).ifPresent(container -> {
                final Result<Void> result = OpenContainer.perform(container)
                        .then(() -> TaskContext.getAgent().matchItemKnowledge());
                System.out.println(result);
            });

//            knownItemRepository.findById(64L).ifPresent(knownItem -> {
//                final Result<KnownItem> result = warehouseService.takeItem(knownItem);
//                System.out.println(result);
//            });

//            knownItemRepository.findById(65L).ifPresent(knownItem -> {
//                final Result<KnownItem> result = warehouseService.takeItem(knownItem);
//                System.out.println(result);
//            });

            return Result.ok();
        });
        Thread.sleep(60 * 60 * 1000L);
    }

}
