package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.component.Script;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-11-2019 23:08</p>
 */
@Service
public class AgentServiceImpl {

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private ObjectMapper objectMapper;
    private AccountService accountService;
    private ObjectFactory<AgentImpl> agentFactory;

    public AgentServiceImpl(
            final ObjectMapper objectMapper,
            final AccountService accountService,
            final ObjectFactory<AgentImpl> agentFactory
    ) {
        this.objectMapper = objectMapper;
        this.accountService = accountService;
        this.agentFactory = agentFactory;
    }

    public void executeScript(final ObjectFactory<? extends Script> scriptFactory) {
        final Account account = accountService.randomAccount();
        final byte[] cookie = accountService.loginByAccount(account.getUsername(), account.getToken());
        final ClientApp clientApp = ClientAppFactory.buildClientApp(
                objectMapper,
                server,
                port,
                account.getUsername(),
                cookie
        );

        clientApp.play();

        final AgentImpl agent = agentFactory.getObject();
        agent.setClientApp(clientApp);

        final Script script = scriptFactory.getObject();
        script.setAgent(agent);
        try {
            script.execute();
        } finally {
            clientApp.logout();
        }
    }

}
