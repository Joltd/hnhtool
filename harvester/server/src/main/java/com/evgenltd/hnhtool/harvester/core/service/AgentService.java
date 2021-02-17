package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.AgentRepository;
import com.evgenltd.hnhtools.clientapp.ClientApp;
import com.evgenltd.hnhtools.clientapp.ClientAppFactory;
import com.evgenltd.hnhtools.clientapp.widgets.CharListWidget;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private static final Logger log = LogManager.getLogger(AgentService.class);

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final List<AgentContext> idleAgents = new ArrayList<>();

    private final ObjectMapper objectMapper;
    private final AgentRepository agentRepository;
    private final ObjectFactory<AgentContext> agentContextFactory;
    private final KnownObjectService knownObjectService;

    public AgentService(
            final ObjectMapper objectMapper,
            final AgentRepository agentRepository,
            final ObjectFactory<AgentContext> agentContextFactory,
            final KnownObjectService knownObjectService
    ) {
        this.objectMapper = objectMapper;
        this.agentRepository = agentRepository;
        this.agentContextFactory = agentContextFactory;
        this.knownObjectService = knownObjectService;
    }

    @PostConstruct
    public void postConstruct() {
        agentRepository.findAll()
                .forEach(agent -> {
                    agent.setStatus(Agent.Status.OFFLINE);
                    agentRepository.save(agent);
                });
    }

    public void authenticateAgent(final Long id, final String username, final byte[] password) {
        final Agent agent = id != null
                ? agentRepository.findOne(id)
                : new Agent();

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
        agent.setCharacter(character);
        agent.setStatus(Agent.Status.OFFLINE);
        agentRepository.save(agent);
    }

    public void updateState(final Long id, final Boolean accident, final Boolean enabled) {
        final Agent agent = agentRepository.findOne(id);
        if (accident != null) {
            agent.setAccident(accident);
        }
        if (enabled != null) {
            if (agent.isEnabled() && !enabled) {
                agent.setEnabled(false);
            } else if (!agent.isEnabled() && enabled) {
                agent.setEnabled(true);
            }
        }
        agentRepository.save(agent);
    }

    public AgentStatus agentStatus(final Long id) {
        final Agent agent = agentRepository.findOne(id);
        final AgentContext agentContext = findIdleAgent(id);
        if (!agent.isEnabled() || agentContext == null) {
            return new AgentStatus(id, agent.getUsername(), agent.getStatus(), null);
        }

        return new AgentStatus(
                id,
                agent.getUsername(),
                agent.getStatus(),
                agentContext.getConnectionState()
        );
    }

    public void login(final Long id) {
        if (id == null) {
            return;
        }
        final Agent agent = agentRepository.findOne(id);
        final Agent.Status status = agent.getStatus();
        switch (status) {
            case IDLE -> {
                return;
            }
            case IN_PROGRESS -> throw new ApplicationException("Agent [%s] currently performing task", agent.getUsername());
            case NOT_AUTHENTICATED, CHARACTER_NOT_SELECTED -> throw new ApplicationException("Unable to login due to agent [%s] status [%s]", agent.getUsername(), agent.getStatus());
        }
        final AgentContext agentContext = agentContextFactory.getObject();
        try (final Authentication authentication = Authentication.of().setHost(server).init()) {
            final Authentication.Result result = authentication.loginByToken(agent.getUsername(), agent.getToken());
            final byte[] cookie = result.cookie();
            agentContext.play(cookie);
            agentIdle(agentContext);
        }
    }

    private void loginImpl(final AgentContext agentContext) {
        try (final Authentication authentication = Authentication.of().setHost(server).init()) {
            final Agent agent = agentContext.getAgent();
            final Authentication.Result result = authentication.loginByToken(agent.getUsername(), agent.getToken());
            final byte[] cookie = result.cookie();
            agentContext.play(cookie);
        }
    }

    public void logout(final Long id) {
        if (id == null) {
            return;
        }
        final AgentContext agentContext = findIdleAgent(id);
        if (agentContext == null) {
            return;
        }
        agentContext.logout();
        agentOffline(agentContext.getAgent());
    }

//    @Scheduled(cron = "0 */6 * * * *")
    public void releaseLostConnectionAgents() {
        synchronized (idleAgents) {
            for (final Iterator<AgentContext> iterator = idleAgents.iterator(); iterator.hasNext(); ) {
                final AgentContext agentContext = iterator.next();
                final MessageBroker.State connectionState = agentContext.getConnectionState();
                final MessageBroker.Status status = connectionState.status();
                if (status.equals(MessageBroker.Status.CLOSED) || status.equals(MessageBroker.Status.CLOSING)) {
                    final Long agentId = agentContext.getAgent().getId();
                    final Agent agent = agentRepository.findOne(agentId);
                    agentOffline(agent);
                    iterator.remove();
                }
            }
        }
    }

    public AgentContext takeRandomAgent() {
        synchronized (idleAgents) {
            releaseLostConnectionAgents();
            final AgentContext agentForReUse = idleAgents.stream()
                    .findAny()
                    .orElse(null);
            if (agentForReUse != null) {
                agentInProgress(agentForReUse.getAgent());
                return agentForReUse;
            }


            final Agent agent = agentRepository.findAll()
                    .stream()
                    .filter(a -> Objects.equals(a.getStatus(), Agent.Status.OFFLINE))
                    .findAny()
                    .orElse(null);
            if (agent == null) {
                return null;
            }
            agentInProgress(agent);

            final AgentContext agentContext = agentContextFactory.getObject();
            agentContext.initialize(agent);
            return agentContext;
        }
    }

    private void agentInProgress(final Agent agent) {
        agent.setStatus(Agent.Status.IN_PROGRESS);
        agentRepository.save(agent);
    }

    private void agentIdle(final AgentContext agentContext) {
        synchronized (idleAgents) {
            idleAgents.add(agentContext);
            final Agent agent = agentContext.getAgent();
            agent.setStatus(Agent.Status.IDLE);
            agentRepository.save(agent);
        }
    }

    private void agentOffline(final Agent agent) {
        agent.setStatus(Agent.Status.OFFLINE);
        agentRepository.save(agent);
    }

    public void initializeAgent(final AgentContext agentContext) {
        try {
            final MessageBroker.State connectionState = agentContext.getConnectionState();
            if (connectionState.status().equals(MessageBroker.Status.INIT)) {
                loginImpl(agentContext);
            }
        } catch (Exception e) {
            agentOffline(agentContext.getAgent());
            throw e;
        }
    }

    public void releaseAgent(final AgentContext agentContext) {
        agentIdle(agentContext);
    }

    private AgentContext findIdleAgent(final Long agentId) {
        synchronized (idleAgents) {
            return idleAgents.stream()
                    .filter(ac -> ac.getAgent().getId().equals(agentId))
                    .findFirst()
                    .orElse(null);
        }
    }

    public IntPoint agentPosition(final Long agentId) {
        final Agent agent = agentRepository.findOne(agentId);
        if (Arrays.asList(Agent.Status.NOT_AUTHENTICATED, Agent.Status.CHARACTER_NOT_SELECTED).contains(agent.getStatus())) {
            return null;
        }

        final String character = agent.getCharacter();
        final KnownObject knownObject = knownObjectService.loadCharacterObject(character);
        return knownObject.getPosition();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AgentStatus(
            Long id,
            String username,
            Agent.Status agentStatus,
            MessageBroker.State connectionState
    ) {}

}
