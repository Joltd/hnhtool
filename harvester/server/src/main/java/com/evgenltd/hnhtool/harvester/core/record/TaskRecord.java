package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TaskRecord(
        Long id,
        LocalDateTime date,
        Task.Status status,
        String assignee
) {

    public static TaskRecord of(final Task task) {
        return new TaskRecord(
                task.getId(),
                task.getActual(),
                task.getStatus(),
                Optional.ofNullable(task.getAgent())
                        .map(Agent::getUsername)
                        .orElse(null)
        );
    }

}
