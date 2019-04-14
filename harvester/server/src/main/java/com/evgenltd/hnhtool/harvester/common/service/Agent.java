package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.component.ObjectIndex;
import com.evgenltd.hnhtool.harvester.common.entity.*;
import com.evgenltd.hnhtool.harvester.common.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.agent.ComplexClient;
import com.evgenltd.hnhtools.agent.ResourceProvider;
import com.evgenltd.hnhtools.command.CommandUtils;
import com.evgenltd.hnhtools.command.Connect;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.WorldObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnh.auth.Authentication;
import com.hnh.auth.AuthenticationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 02-04-2019 00:51</p>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Agent {

    private static final Long TIMEOUT = 1000L;

    private ObjectMapper objectMapper;
    private ResourceProvider resourceProvider;
    private AccountRepository accountRepository;
    private KnowledgeMatchingService knowledgeMatchingService;
    private KnownObjectRepository knownObjectRepository;
    private Account account;
    private Logger log;

    @Value("${hafen.server}")
    private String server;
    @Value("${hafen.port}")
    private Integer port;

    private volatile State state;
    private BlockingQueue<Work> queue = new LinkedBlockingQueue<>(1);

    private ComplexClient client;
    private AtomicBoolean withResearch = new AtomicBoolean(true);
    private ObjectIndex index;

    public Agent(
            final ObjectMapper objectMapper,
            final ResourceProvider resourceProvider,
            final AccountRepository accountRepository,
            final KnowledgeMatchingService knowledgeMatchingService,
            final KnownObjectRepository knownObjectRepository
            ) {
        this.objectMapper = objectMapper;
        this.resourceProvider = resourceProvider;
        this.accountRepository = accountRepository;
        this.knowledgeMatchingService = knowledgeMatchingService;
        this.knownObjectRepository = knownObjectRepository;
    }

    @PostConstruct
    public void postConstruct() {
        state = State.DEACTIVATED;
    }

    @PreDestroy
    public void preDestroy() {
        doDeactivation();
    }

    @Scheduled(fixedDelay = 5_000L)
    public void matchKnowledge() {
        if (client == null || !client.isLife()) {
            return;
        }

        final Result<WorldObject> character = client.getCharacter();
        if (character.isFailed()) {
            return;
        }

        log.info("Start matching index with KDB, oldIndex=[{}]", index);

        if (account.getCharacterObject() == null) {
            final KnownObject koCharacter = knowledgeMatchingService.rememberCharacterObject(character.getValue());
            account.setCharacterObject(koCharacter);
            accountRepository.save(account);
        }

        final List<WorldObject> worldObjects = client.getWorldObjects();

        final Result<ObjectIndex> matchResult = knowledgeMatchingService.match(
                index,
                character.getValue(),
                account.getCharacterObject(),
                worldObjects,
                withResearch.get()
        );
        if (matchResult.isSuccess()) {
            index = matchResult.getValue();
            log.info("End matching index with KDB, newIndex=[{}]", index);
        } else {
            index = new ObjectIndex();
            log.info("Failed matching index with KDB, {}", matchResult);
        }

    }

    // ##################################################
    // #                                                #
    // #  Public API                                    #
    // #                                                #
    // ##################################################

    public void setAccount(final Account account) {
        this.account = account;
        this.log = LogManager.getLogger("Agent-" + account.getUsername());
    }

    public ComplexClient getClient() {
        return client;
    }

    public boolean isReady() {
        return state.equals(State.READY);
    }

    public Result<Void> activate() {
        if (!Objects.equals(state, State.DEACTIVATED)) {
            return Result.fail(ServerResultCode.AGENT_ACTIVATED);
        }

        Assert.valueRequireNonEmpty(account, "Account");

        state = State.READY;

        final Thread thread = new Thread(this::worker);
        thread.start();

        return Result.ok();
    }

    public Result<Void> deactivate() {
        if (Objects.equals(state, State.DEACTIVATED)) {
            return Result.fail(ServerResultCode.AGENT_DEACTIVATED);
        }

        doDeactivation();

        return Result.ok();
    }

    public Result<Void> assignWork(final Work work) {
        if (state.equals(State.DEACTIVATED)) {
            return Result.fail(ServerResultCode.AGENT_DEACTIVATED);
        }

        final boolean result = queue.offer(work);
        if (!result) {
            return Result.fail(ServerResultCode.AGENT_REJECT_WORK_OFFER);
        }

        return Result.ok();
    }

    public void knowledgeMatchingWithResearch(final boolean value) {
        withResearch.set(value);
    }

    public void changeSpace(final Space space) {
        log.info(String.format("Update space, id=[%s]", space.getId()));
        account.getCharacterObject()
                .setOwner(space);
        knownObjectRepository.save(account.getCharacterObject());
    }

    public Result<Long> getMatchedWorldObjectId(final Long knownObjectId) {
        return index.getMatchedWorldObjectId(knownObjectId);
    }

    public Result<Long> getMatchedKnownObjectId(final Long worldObjectId) {
        return index.getMatchedKnownObjectId(worldObjectId);
    }

    public KnownObject getCharacter() {
        return account.getCharacterObject();
    }

    // ##################################################
    // #                                                #
    // #  Private                                       #
    // #                                                #
    // ##################################################

    private void doDeactivation() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        state = State.DEACTIVATED;
    }

    private void worker() {
        while (true) {

            try {

                final Work work = queue.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                if (state.equals(State.DEACTIVATED)) {
                    return;
                }

                if (work == null) {
                    continue;
                }

                connectIfNecessary();

                state = State.BUSY;
                work.apply(this);
                state = State.READY;

            } catch (InterruptedException e) {
                doDeactivation();
                return;
            }

        }
    }

    private void connectIfNecessary() {
        if (client != null && client.isLife()) {
            return;
        }

        final byte[] cookie = authenticateAccount(account);
        if (cookie == null) {
            deactivate();
            account.setToken(null);
            accountRepository.save(account);
            return;
        }

        client = new ComplexClient(
                objectMapper, resourceProvider,
                server, port,
                account.getUsername(), cookie,
                account.getDefaultCharacter()
        );
        Connect.perform(client);
        client.play();

        final Result<Void> result = CommandUtils.await(() -> index != null);
        if (result.isFailed()) {
            doDeactivation();
        }
    }

    private byte[] authenticateAccount(final Account account) {
        try (final Authentication init = buildAuthentication()) {
            final AuthenticationResult result = init.loginByToken(
                    account.getUsername(),
                    account.getToken()
            );
            return result.getCookie();
        } catch (final Exception e) {
            log.error("Unable to authenticate", e);
            return null;
        }
    }

    private Authentication buildAuthentication() {
        return Authentication.of()
                .setHost(server)
                .init();
    }

    private enum State {
        READY,
        BUSY,
        DEACTIVATED
    }

}
