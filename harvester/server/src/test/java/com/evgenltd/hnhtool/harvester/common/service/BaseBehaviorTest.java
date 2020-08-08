package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.component.script.TestScript;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import com.evgenltd.hnhtool.harvester.core.service.AccountService;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    @Test
    public void addAccount() {
        accountService.registerAccount("Grafbredbery", "15051953", "Hild");
        accountService.registerAccount("temedrou", "nRJWfd2v", "Vilco");

        Resource resource = new Resource();
        resource.setName("Vilco");
        resource.setPlayer(true);
        resourceRepository.save(resource);

        KnownObject knownObject = new KnownObject();
        knownObject.setResource(resource);
        knownObjectRepository.save(knownObject);
    }

    @Test
    public void commonTest() {
        final TestScript script = testScriptFactory.getObject();
        agentService.scheduleScriptExecution(script);
    }

    @Test
    public void charList() {
        final Account account = accountService.randomAccount();
        final byte[] cookie = accountService.loginByAccount(account.getUsername(), account.getToken());
        final ClientApp clientApp = ClientAppFactory.buildClientApp(
                objectMapper,
                server,
                port,
                account.getUsername(),
                cookie
        );

        clientApp.login();
        System.out.println();
    }

}
