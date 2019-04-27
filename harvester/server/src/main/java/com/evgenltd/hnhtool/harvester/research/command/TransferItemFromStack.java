package com.evgenltd.hnhtool.harvester.research.command;

import com.evgenltd.hnhtool.harvester.common.command.Await;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.service.Agent;
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

    private TransferItemFromStack(final Agent agent, final KnownObject stack) {
        this.agent = agent;
        this.knownStack = stack;
    }

    public static Result<Void> perform(@NotNull final Agent agent, @NotNull final KnownObject stack) {
        Assert.valueRequireNonEmpty(agent, "Agent");
        Assert.valueRequireNonEmpty(stack, "Stack");
        return new TransferItemFromStack(agent, stack).performImpl();
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
