package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.component.TestScript;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
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
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ObjectFactory<TestScript> testScriptFactory;

    @Test
    public void addAccount() {
//        accountService.registerAccount("Grafbredbery", "15051953", "botixo");

        Account account = new Account();
        account.setUsername("Grafbredbery");
        account.setToken(new byte[] {120,119,-56,68,-14,108,123,-89,110,-105,-59,-73,58,34,-90,-77,63,-69,109,-2,35,39,-5,-1,-119,68,-52,-62,109,50,52,-7});
        account.setCharacterName("botixo");
        accountRepository.save(account);
    }

    @Test
    public void commonTest() {
        final TestScript script = testScriptFactory.getObject();
        agentService.scheduleScriptExecution(script);
    }

}
