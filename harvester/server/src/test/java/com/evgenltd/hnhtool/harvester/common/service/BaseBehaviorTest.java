package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.component.TestScript;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import com.evgenltd.hnhtool.harvester.core.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
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
    private ResourceRepository resourceRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ObjectFactory<TestScript> testScriptFactory;

    @Autowired
    private KnownObjectRepository knownObjectRepository;

    @Test
    public void addAccount() {
        accountService.registerAccount("Grafbredbery", "15051953", "botixo");

        Resource resource = new Resource();
        resource.setName("botixo");
        resource.setPlayer(true);
        resourceRepository.save(resource);

        KnownObject knownObject = new KnownObject();
        knownObject.setResource(resource);
    }

    @Test
    public void commonTest() {
        final TestScript script = testScriptFactory.getObject();
        agentService.scheduleScriptExecution(script);
    }

}
