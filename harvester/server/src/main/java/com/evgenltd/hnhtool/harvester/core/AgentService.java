package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtool.harvester.core.component.script.Script;
import com.evgenltd.hnhtool.harvester.core.entity.Account;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:27
 */
public interface AgentService {

    List<String> loadCharacterList(Account account);

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
