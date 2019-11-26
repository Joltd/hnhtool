package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-11-2019 23:16</p>
 */
@Service
public class AccountService {

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private AccountRepository accountRepository;

    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void registerAccount(@NotNull final String username, @NotNull final String password, @NotNull final String characterName) {
        Objects.requireNonNull(username, "[Username] should not be empty");
        Objects.requireNonNull(password, "[Password] should not be empty");

        final boolean accountAlreadyRegistered = accountRepository.findAccountByUsername(username).isPresent();
        if (accountAlreadyRegistered) {
            throw new ApplicationException("Account [%s] already registered", username);
        }

        try (final Authentication init = buildAuthentication()) {
            final AuthenticationResult result = init.login(
                    username,
                    Authentication.passwordHash(password)
            );
            final byte[] token = result.getToken();

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
            final AuthenticationResult result = init.loginByToken(
                    username,
                    token
            );
            return result.getCookie();
        }
    }

    private Authentication buildAuthentication() {
        return Authentication.of()
                .setHost(server)
                .init();
    }

}
