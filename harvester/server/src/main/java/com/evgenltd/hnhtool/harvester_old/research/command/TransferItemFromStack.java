package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester_old.common.command.Await;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtool.harvester_old.common.service.Agent;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-04-2019 22:49</p>
 */
public class TransferItemFromStack {

    private Agent agent;
    private KnownObject knownStack;
    private Integer number;

    private TransferItemFromStack(final KnownObject stack) {
        this.agent = TaskContext.getAgent();
        this.knownStack = stack;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject stack) {
        Assert.valueRequireNonEmpty(stack, "Stack");
        return new TransferItemFromStack(stack).performImpl();
    }

    private Result<Void> performImpl() {
        return agent.getMatchedWorldObjectId(knownStack.getId())
                .thenApplyCombine(woId -> agent.getClient().getStack(woId))
                .then(stack -> number = stack.getCount())
                .then(stack -> agent.getClient().transferItemFromStack(stack.getId()))
                .then(stack -> Await.performSimple(() -> !Objects.equals(stack.getCount(), number)))
                .cast();
    }

}
