package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.component.script.Script;
import org.jetbrains.annotations.NotNull;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:27
 */
public interface AgentService {

    @NotNull
    Long scheduleScriptExecution(@NotNull Script script);

    @NotNull
    ExecutionStatus getScriptExecutionStatus(@NotNull Long scheduleId);

    enum ExecutionStatus {
        NOT_FOUND,
        QUEUED,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

}
