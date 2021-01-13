package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
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

    private final AccountRepository accountRepository;

    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @NotNull
    public Account findById(final Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("There is no Account for id [%s]", id));
    }

    @Transactional
    public void enableAccount(final Long id, final boolean enabled) {
        final Account account = findById(id);
        account.setEnabled(enabled);
    }

    public void authenticateAccount(final Account account, final String password) {
        if (account.getId() == null) {
            final boolean accountAlreadyExists = accountRepository.findAccountByUsername(account.getUsername()).isPresent();
            if (accountAlreadyExists) {
                throw new ApplicationException("Account [%s] already exists", account.getUsername());
            }

            if (Assert.isEmpty(password)) {
                throw new ApplicationException("Password should be specified");
            }
        }

        if (Assert.isNotEmpty(password)) {
            final byte[] token = acquireToken(account.getUsername(), password);
            account.setToken(token);
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

        final boolean accountAlreadyRegistered = accountRepository.findAccountByUsername(username).isPresent();
        if (accountAlreadyRegistered) {
            throw new ApplicationException("Account [%s] already registered", username);
        }

        try (final Authentication init = buildAuthentication()) {
            final Authentication.Result result = init.login(
                    username,
                    Authentication.passwordHash(password)
            );
            final byte[] token = result.token();

            final Account account = new Account();
            account.setUsername(username);
            account.setToken(token);
            account.setCharacterName(characterName);
            accountRepository.save(account);
        }
    }

    public Account randomAccount() {
        return accountRepository.findAll()
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
