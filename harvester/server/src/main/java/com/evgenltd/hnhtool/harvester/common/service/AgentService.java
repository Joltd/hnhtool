package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Account;
import com.evgenltd.hnhtool.harvester.common.entity.ServerResultCode;
import com.evgenltd.hnhtool.harvester.common.entity.Work;
import com.evgenltd.hnhtool.harvester.common.repository.AccountRepository;
import com.evgenltd.hnhtools.common.Result;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 23:55</p>
 */
@Service
public class AgentService {

    private AccountRepository accountRepository;
    private ObjectFactory<Agent> agentFactory;

    private final List<Agent> agents = new ArrayList<>();

    public AgentService(
            final AccountRepository accountRepository,
            final ObjectFactory<Agent> agentFactory
    ) {
        this.accountRepository = accountRepository;
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

    public Result<Void> offerWork(final Work work) {

        final List<Agent> passedByRequirements = agents.stream()
                .filter(agent -> agent.checkRequirements(work))
                .collect(Collectors.toList());

        if (passedByRequirements.isEmpty()) {
            return Result.fail(ServerResultCode.AGENT_NO_MATCH_REQUIREMENTS);
        }

        final Optional<Agent> firsReadyAgent = passedByRequirements.stream()
                .filter(Agent::isReady)
                .findFirst();

        if (!firsReadyAgent.isPresent()) {
            return Result.fail(ServerResultCode.AGENT_ALL_BUSY);
        }

        firsReadyAgent.get().assignWork(work.getRunnable());

        return Result.ok();

    }

    private Agent buildAgent(final Account account) {
        final Agent agent = agentFactory.getObject();
        agent.setAccount(account);
        return agent;
    }

}
