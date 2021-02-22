package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TaskRecord(Long id, LocalDateTime date, Task.Status status, String assignee) {}
