package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Account;
import com.evgenltd.hnhtool.harvester.common.entity.Agent;
import com.evgenltd.hnhtool.harvester.common.repository.AccountRepository;
import com.evgenltd.hnhtools.agent.ComplexClient;
import com.evgenltd.hnhtools.agent.ResourceProvider;
import com.evgenltd.hnhtools.command.Connect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private ObjectMapper objectMapper;
    private AccountRepository accountRepository;
    private ResourceProvider resourceProvider;

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private List<Agent> agents = Collections.synchronizedList(new ArrayList<>());

    public AgentService(
            final ObjectMapper objectMapper,
            final AccountRepository accountRepository,
            final ResourceProvider resourceProvider
    ) {
        this.objectMapper = objectMapper;
        this.accountRepository = accountRepository;
        this.resourceProvider = resourceProvider;
    }

    @PostConstruct
    public void postConstruct() {
        for (final Account account : accountRepository.findAll()) {
            final byte[] cookie = authenticateAccount(account);
            if (cookie == null) {
                account.setToken(null);
                accountRepository.save(account);
                continue;
            }

            final Agent agent = new Agent();
            agent.setAccount(account);
            agents.add(agent);
        }
    }

    public void begin() {

        if (agents.isEmpty()) {
            return;
        }

        final Agent agent = agents.stream()
                .findFirst()
                .orElse(null);

        final ComplexClient client = new ComplexClient(
                objectMapper,
                resourceProvider,
                server,
                port,
                agent.getAccount().getUsername(),
                agent.getAccount().getToken(),
                agent.getAccount().getDefaultCharacter()
        );

        Connect.perform(client);
        client.play();


    }

    private byte[] authenticateAccount(final Account account) {
        try (final Authentication init = buildAuthentication()) {
            final AuthenticationResult result = init.loginByToken(
                    account.getUsername(),
                    account.getToken()
            );
            return result.getCookie();
        } catch (final Exception e) {
            log.error(String.format("Unable to authenticate account [%s]", account.getUsername()), e);
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
