package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.repository.AgentRepository;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.evgenltd.hnhtools.clientapp.widgets.CharListWidget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final Map<Long, AgentContext> agents = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final AgentRepository agentRepository;
    private final ObjectFactory<AgentContext> agentContextFactory;

    public AgentService(
            final ObjectMapper objectMapper,
            final AgentRepository agentRepository,
            final ObjectFactory<AgentContext> agentContextFactory
    ) {
        this.objectMapper = objectMapper;
        this.agentRepository = agentRepository;
        this.agentContextFactory = agentContextFactory;
    }

    @PostConstruct
    public void postConstruct() {
        agentRepository.findAll().forEach(this::registerAgentContext);
    }

    public void authenticateAgent(final Long id, final String username, final byte[] password) {
        final Agent agent = id != null
                ? agentRepository.findOne(id)
                : new Agent();

        unregisterAgentContext(agent);

        agent.setUsername(username);

        try (final Authentication authentication = Authentication.of().setHost(server).init()) {
            agent.setStatus(Agent.Status.NOT_AUTHENTICATED);
            final Authentication.Result result = authentication.login(username, password);
            agent.setToken(result.token());
            agent.setStatus(Agent.Status.CHARACTER_NOT_SELECTED);
            agent.setCharacter(null);
        } finally {
            agentRepository.save(agent);

        }
    }

    public List<String> characterList(final Long id) {
        final Agent agent = agentRepository.findOne(id);
        if (agent.getStatus().equals(Agent.Status.NOT_AUTHENTICATED)) {
            throw new ApplicationException("Agent [%s] is not authenticated", agent.getUsername());
        }

        logout(id);

        try (final Authentication authentication = Authentication.of().setHost(server).init()) {
            final Authentication.Result result = authentication.loginByToken(agent.getUsername(), agent.getToken());
            final byte[] cookie = result.cookie();
            final ClientApp clientApp = ClientAppFactory.buildClientApp(
                    objectMapper,
                    server,
                    port
            );

            clientApp.login(agent.getUsername(), cookie);

            Thread.sleep(1000L); // need more smarter way to wait characters

            try {
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
        } catch (final InterruptedException e) {
            return Collections.emptyList();
        }

    }

    public void updateCharacter(final Long id, final String character) {
        final Agent agent = agentRepository.findOne(id);
        unregisterAgentContext(agent);
        agent.setCharacter(character);
        agent.setStatus(Agent.Status.OFFLINE);
        agentRepository.save(agent);
        registerAgentContext(agent);
    }

    public void updateState(final Long id, final Boolean accident, final Boolean enabled) {
        final Agent agent = agentRepository.findOne(id);
        if (accident != null) {
            agent.setAccident(accident);
        }
        if (enabled != null) {
            if (agent.isEnabled() && !enabled) {
                unregisterAgentContext(agent);
                agent.setEnabled(false);
            } else if (!agent.isEnabled() && enabled) {
                registerAgentContext(agent);
                agent.setEnabled(true);
            }
        }
        agentRepository.save(agent);
    }

    public void login(final Long id) {
        if (id == null) {
            return;
        }
        final AgentContext agentContext = agents.get(id);
        if (agentContext == null) {
            return;
        }
        final Agent agent = agentContext.getAgent();
        final Agent.Status status = agent.getStatus();
        switch (status) {
            case IDLE -> logout(id);
            case IN_PROGRESS -> throw new ApplicationException("Agent [%s] currently performing task", agent.getUsername());
            case NOT_AUTHENTICATED, CHARACTER_NOT_SELECTED -> throw new ApplicationException("Unable to login due to agent [%s] status [%s]", agent.getUsername(), agent.getStatus());
        }
        try (final Authentication authentication = Authentication.of().setHost(server).init()) {
            final Authentication.Result result = authentication.loginByToken(agent.getUsername(), agent.getToken());
            final byte[] cookie = result.cookie();
            agentContext.play(cookie);
        }
    }

    public void logout(final Long id) {
        if (id == null) {
            return;
        }
        final AgentContext agentContext = agents.get(id);
        if (agentContext == null) {
            return;
        }
        agentContext.logout();
        final Agent agent = agentContext.getAgent();
        agent.setStatus(Agent.Status.OFFLINE);
        agentRepository.save(agent);
    }

    private void registerAgentContext(final Agent agent) {
        if (agent.getId() == null) {
            return;
        }
        if (agents.containsKey(agent.getId())) {
            return;
        }
        final AgentContext agentContext = agentContextFactory.getObject();
        agentContext.init(agent);
        agents.put(agent.getId(), agentContext);
    }

    private void unregisterAgentContext(final Agent agent) {
        if (agent.getId() == null) {
            return;
        }
        final AgentContext agentContext = agents.get(agent.getId());
        if (agentContext == null) {
            return;
        }
        logout(agent.getId());
        agents.remove(agent.getId());
    }

}
