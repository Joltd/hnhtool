package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.repository.AgentRepository;
import com.evgenltd.hnhtool.harvester.core.service.AgentService;
import com.evgenltd.hnhtools.util.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentRepository agentRepository;
    private final AgentService agentService;

    public AgentController(
            final AgentRepository agentRepository,
            final AgentService agentService
    ) {
        this.agentRepository = agentRepository;
        this.agentService = agentService;
    }

    @GetMapping
    public List<AgentRecord> list() {
        return agentRepository.findAll()
                .stream()
                .map(agent -> new AgentRecord(
                        agent.getId(),
                        agent.getUsername(),
                        agent.getCharacter(),
                        agent.getStatus(),
                        agent.isAccident(),
                        agent.isEnabled()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AgentRecord byId(@PathVariable final Long id) {
        final Agent agent = agentRepository.findOne(id);
        return new AgentRecord(
                agent.getId(),
                agent.getUsername(),
                agent.getCharacter(),
                agent.getStatus(),
                agent.isAccident(),
                agent.isEnabled()
        );
    }

    @PostMapping("/authentication")
    public void authentication(@RequestBody final AgentAuthenticationRecord agentAuthenticationRecord) {
        agentService.authenticateAgent(
                agentAuthenticationRecord.id(),
                agentAuthenticationRecord.username(),
                Util.hexStringToByteArray(agentAuthenticationRecord.password())
        );
    }

    @GetMapping("/{id}/character")
    public List<String> characterList(@PathVariable final Long id) {
        return agentService.characterList(id);
    }

    @PostMapping("/{id}/character/{character}")
    public void updateCharacter(@PathVariable final Long id, @PathVariable final String character) {
        agentService.updateCharacter(id, character);
    }

    @PostMapping("/{id}")
    public void updateState(
            @PathVariable final Long id,
            @RequestParam("accident") final Boolean accident,
            @RequestParam("enabled") final Boolean enabled
    ) {
        agentService.updateState(id, accident, enabled);
    }

    @PostMapping("/{id}/login")
    public void login(@PathVariable final Long id) {
        agentService.login(id);
    }

    @PostMapping("/{id}/logout")
    public void logout(@PathVariable final Long id) {
        agentService.logout(id);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AgentAuthenticationRecord(Long id, String username, String password) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AgentRecord(
            Long id,
            String username,
            String character,
            Agent.Status status,
            boolean accident,
            boolean enabled
    ) {}

}
