package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Account;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Space;
import com.evgenltd.hnhtool.harvester.common.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.common.repository.SpaceRepository;
import com.evgenltd.hnhtools.common.Result;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:55</p>
 */
@Service
public class AgentService {

    private static final Logger log = LogManager.getLogger(AgentService.class);

    private AccountRepository accountRepository;
    private SpaceRepository spaceRepository;
    private ObjectFactory<Agent> agentFactory;

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final List<Agent> agents = Collections.synchronizedList(new ArrayList<>());

    public AgentService(
            final AccountRepository accountRepository,
            final SpaceRepository spaceRepository,
            final ObjectFactory<Agent> agentFactory
    ) {
        this.accountRepository = accountRepository;
        this.spaceRepository = spaceRepository;
        this.agentFactory = agentFactory;
    }

    @PostConstruct
    public void postConstruct() {
        accountRepository.findAll()
                .stream()
                .map(this::buildAgent)
                .forEach(agents::add);
    }

    @PreDestroy
    public void preDestroy() {
        for (final Agent agent : agents) {
            agent.deactivate();
        }
    }

    public boolean checkRequirements(final Predicate<Agent> requirements) {
        return agents.stream().anyMatch(requirements);
    }

    public Result<Void> offerWork(final Runnable work) {

        for (final Agent agent : agents) {
            if (!agent.isReady()) {
                continue;
            }

            final Result<Void> result = agent.assignWork(work);
            if (result.isSuccess()) {
                return Result.ok();
            }
        }

        return Result.fail(ServerResultCode.AGENT_ALL_BUSY);
    }

    public Result<Void> registerAgent(final String username, final String password, final String defaultCharacter) {
        final byte[] token = authenticateAccount(username, password);
        if (token == null) {
            return Result.fail(ServerResultCode.AGENT_NOT_AUTHENTICATED);
        }

        final Space surfaceSpace = findSurfaceSpace();

        final Account account = new Account();
        account.setUsername(username);
        account.setToken(token);
        account.setCurrentSpace(surfaceSpace);
        account.setDefaultCharacter(defaultCharacter);
        accountRepository.save(account);
        return Result.ok();
    }

    private Agent buildAgent(final Account account) {
        final Agent agent = agentFactory.getObject();
        agent.setAccount(account);
        agent.activate();
        return agent;
    }

    private Space findSurfaceSpace() {
        final Space surface = spaceRepository.findByType(Space.Type.SURFACE);
        if (surface != null) {
            return surface;
        }

        final Space newSurface = new Space();
        newSurface.setName("Surface");
        newSurface.setType(Space.Type.SURFACE);
        return spaceRepository.save(newSurface);
    }

    private byte[] authenticateAccount(final String username, final String password) {
        try (final Authentication init = buildAuthentication()) {
            final AuthenticationResult result = init.login(
                    username,
                    Authentication.passwordHash(password)
            );
            return result.getToken();
        } catch (final Exception e) {
            log.error("Unable to authenticate", e);
            return null;
        }
    }

    private Authentication buildAuthentication() {
        return Authentication.of()
                .setHost(server)
                .setPort(port)
                .init();
    }

}
