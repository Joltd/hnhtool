package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.repository.AgentRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.common.Assert;
import com.hnh.auth.Authentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class AccountService {

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private final AgentRepository agentRepository;

    public AccountService(final AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @NotNull
    public Agent findById(final Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("There is no Account for id [%s]", id));
    }

    @Transactional
    public void enableAccount(final Long id, final boolean enabled) {
        final Agent agent = findById(id);
        agent.setEnabled(enabled);
    }

    public void authenticateAccount(final Agent agent, final String password) {
        if (agent.getId() == null) {
            final boolean accountAlreadyExists = agentRepository.findAccountByUsername(agent.getUsername()).isPresent();
            if (accountAlreadyExists) {
                throw new ApplicationException("Account [%s] already exists", agent.getUsername());
            }

            if (Assert.isEmpty(password)) {
                throw new ApplicationException("Password should be specified");
            }
        }

        if (Assert.isNotEmpty(password)) {
            final byte[] token = acquireToken(agent.getUsername(), password);
            agent.setToken(token);
        }
    }

    private byte[] acquireToken(final String username, final String password) {
        try (final Authentication init = buildAuthentication()) {
            final byte[] passwordHashAsBytes = Authentication.passwordHash(password);
            final Authentication.Result result = init.login(username, passwordHashAsBytes);
            return result.token();
        }
    }

    public void registerAccount(@NotNull final String username, @NotNull final String password, @NotNull final String characterName) {
        Objects.requireNonNull(username, "[Username] should not be empty");
        Objects.requireNonNull(password, "[Password] should not be empty");

        final boolean accountAlreadyRegistered = agentRepository.findAccountByUsername(username).isPresent();
        if (accountAlreadyRegistered) {
            throw new ApplicationException("Account [%s] already registered", username);
        }

        try (final Authentication init = buildAuthentication()) {
            final Authentication.Result result = init.login(
                    username,
                    Authentication.passwordHash(password)
            );
            final byte[] token = result.token();

            final Agent agent = new Agent();
            agent.setUsername(username);
            agent.setToken(token);
            agent.setCharacter(characterName);
            agentRepository.save(agent);
        }
    }

    public Agent randomAccount() {
        return agentRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ApplicationException("No one registered accounts"));
    }

    public byte[] loginByAccount(@NotNull final String username, @NotNull final byte[] token) {
        Objects.requireNonNull(username, "[Username] should not be empty");
        Objects.requireNonNull(token, "[Token] should not be empty");

        try (final Authentication init = buildAuthentication()) {
            final Authentication.Result result = init.loginByToken(
                    username,
                    token
            );
            return result.cookie();
        }
    }

    private Authentication buildAuthentication() {
        return Authentication.of()
                .setHost(server)
                .init();
    }

}
