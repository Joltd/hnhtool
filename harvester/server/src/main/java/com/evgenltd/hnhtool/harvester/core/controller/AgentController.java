package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.component.script.TestScript;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.core.service.AccountService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AgentService agentService;
    private final ObjectFactory<TestScript> testScriptFactory;

    public AgentController(
            final AccountRepository accountRepository,
            final AccountService accountService,
            final AgentService agentService,
            final ObjectFactory<TestScript> testScriptFactory
    ) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.agentService = agentService;
        this.testScriptFactory = testScriptFactory;
    }

    @GetMapping
    public Response<List<AgentRecord>> list() {
        final List<AgentRecord> result = accountRepository.findAll()
                .stream()
                .map(account -> new AgentRecord(
                        account.getId(),
                        account.getUsername(),
                        null,
                        account.getCharacterName(),
                        account.isEnabled()
                ))
                .collect(Collectors.toList());
        return new Response<>(result);
    }

    @GetMapping("/{id}")
    public Response<AgentRecord> byId(@PathVariable final Long id) {
        final Account account = accountService.findById(id);
        return new Response<>(new AgentRecord(account.getId(), account.getUsername(), null, account.getCharacterName(), account.isEnabled()));
    }

    @PostMapping
    public Response<Void> update(@RequestBody final AgentRecord agentRecord) {
        final Account account = agentRecord.id != null
                ? accountService.findById(agentRecord.id)
                : new Account();
        account.setUsername(agentRecord.username());
        account.setCharacterName(agentRecord.character());
        account.setEnabled(agentRecord.enabled());
        accountService.authenticateAccount(account, agentRecord.password());
        accountRepository.save(account);
        return new Response<>();
    }

    @PostMapping("/{id}")
    public Response<Void> updateEnabled(@PathVariable final Long id, @RequestParam(name = "enabled") final Boolean enabled) {
        accountService.enableAccount(id, enabled);
        return new Response<>();
    }

    @GetMapping("/{id}/character")
    public Response<List<String>> characterList(@PathVariable final Long id) {
        final Account account = accountService.findById(id);
        return new Response<>(agentService.loadCharacterList(account));
    }

    @PostMapping("/task")
    public Response<Void> task() {
        final TestScript testScript = testScriptFactory.getObject();
        agentService.scheduleScriptExecution(testScript);
        return new Response<>();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AgentRecord(
            Long id,
            String username,
            String password,
            String character,
            boolean enabled
    ) {}

}
