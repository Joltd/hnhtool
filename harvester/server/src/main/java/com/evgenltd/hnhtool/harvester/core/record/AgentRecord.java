package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AgentRecord(
        Long id,
        String name,
        String username,
        String character,
        Agent.Status status,
        boolean accident,
        boolean enabled
) {

    public static AgentRecord of(final Agent agent) {
        return new AgentRecord(
                agent.getId(),
                agent.getName(),
                agent.getUsername(),
                agent.getCharacter(),
                agent.getStatus(),
                agent.isAccident(),
                agent.isEnabled()
        );
    }

}
