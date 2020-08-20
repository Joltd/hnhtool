package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.component.exception.HandNotEmptyException;
import com.evgenltd.hnhtool.harvester.core.component.script.Script;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.evgenltd.hnhtools.clientapp.widgets.CharListWidget;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentServiceImpl implements AgentService {

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final ObjectFactory<AgentImpl> agentFactory;

    public AgentServiceImpl(
            final ObjectMapper objectMapper,
            final AccountService accountService,
            final ObjectFactory<AgentImpl> agentFactory
    ) {
        this.objectMapper = objectMapper;
        this.accountService = accountService;
        this.agentFactory = agentFactory;
    }

    @Override
    public List<String> loadCharacterList(final Account account) {
        final byte[] cookie = accountService.loginByAccount(account.getUsername(), account.getToken());
        final ClientApp clientApp = ClientAppFactory.buildClientApp(
                objectMapper,
                server,
                port,
                account.getUsername(),
                cookie
        );

        try {
            clientApp.login();
            return clientApp.getWidgets()
                    .stream()
                    .filter(widget -> widget instanceof CharListWidget)
                    .map(widget -> (CharListWidget) widget)
                    .flatMap(widget -> widget.getCharacters().stream())
                    .distinct()
                    .collect(Collectors.toList());
        } finally {
            clientApp.logout();
        }
    }

    @Override
    @NotNull
    public Long scheduleScriptExecution(@NotNull final Script script) {
        final Account account = accountService.randomAccount();
        final byte[] cookie = accountService.loginByAccount(account.getUsername(), account.getToken());
        final ClientApp clientApp = ClientAppFactory.buildClientApp(
                objectMapper,
                server,
                port,
                account.getUsername(),
                cookie
        );

        clientApp.play(account.getCharacterName());

        final AgentImpl agent = agentFactory.getObject();
        agent.setClientApp(clientApp);

        script.setAgent(agent);
        try {
            script.execute();
        } catch (final HandNotEmptyException e) {
            // accident case
        } finally {
            clientApp.logout();
        }

        return 0L;
    }

    @Override
    @NotNull
    public ExecutionStatus getScriptExecutionStatus(@NotNull final Long scheduleId) {
        return ExecutionStatus.NOT_FOUND;
    }

}
