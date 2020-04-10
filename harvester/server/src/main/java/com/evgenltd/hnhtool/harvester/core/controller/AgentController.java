package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.AgentService;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtool.harvester.core.repository.AccountRepository;
import com.evgenltd.hnhtool.harvester.core.service.AccountService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 05-04-2020 13:04</p>
 */
@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AgentService agentService;

    public AgentController(
            final AccountRepository accountRepository,
            final AccountService accountService,
            final AgentService agentService
    ) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.agentService = agentService;
    }

    @GetMapping
    public List<AgentRecord> list() {
        return accountRepository.findAll()
                .stream()
                .map(account -> new AgentRecord(account.getId(), account.getUsername(), null, account.getCharacterName(), account.isEnabled()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AgentRecord byId(@PathVariable final Long id) {
        final Account account = accountService.findById(id);
        return new AgentRecord(account.getId(), account.getUsername(), null, account.getCharacterName(), account.isEnabled());
    }

    @PostMapping
    public void update(@RequestBody final AgentRecord agentRecord) {
        final Account account = agentRecord.id != null
                ? accountService.findById(agentRecord.id)
                : new Account();
        account.setUsername(agentRecord.username());
        account.setCharacterName(agentRecord.character());
        account.setEnabled(agentRecord.enabled());
        accountService.authenticateAccount(account, agentRecord.passwordHash());
        accountRepository.save(account);
    }

    @PostMapping("/{id}")
    public void updateEnabled(@PathVariable final Long id, @RequestParam(name = "enabled") final Boolean enabled) {
        final Account account = accountService.findById(id);
        account.setEnabled(enabled != null && enabled);
    }

    @GetMapping("/{id}/character")
    public List<String> characterList(@PathVariable final Long id) {
        final Account account = accountService.findById(id);
        return agentService.loadCharacterList(account);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AgentRecord(
            Long id,
            String username,
            String passwordHash,
            String character,
            boolean enabled
    ) {}

}
