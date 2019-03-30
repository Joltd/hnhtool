package com.evgenltd.hnhtools.command;

import com.evgenltd.hnhtools.agent.ComplexClient;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.evgenltd.hnhtools.entity.ResultCode;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 29-03-2019 00:54</p>
 */
public class Move extends AbstractCommand {

    private ComplexClient client;
    private IntPoint target;

    private Move(final ComplexClient client, final IntPoint target) {
        this.client = client;
        this.target = target;
    }

    public static Result<Void> perform(@NotNull final ComplexClient client, @NotNull final IntPoint target) {
        Assert.valueRequireNonEmpty(client, "Client");
        Assert.valueRequireNonEmpty(target, "Target");
        return new Move(client, target).performImpl();
    }

    private Result<Void> performImpl() {
        return client.move(target)
                .then(this::await);
    }

    @Override
    protected Result<Boolean> isDone() {
        final Result<Boolean> isReached = client.getCharacterPosition()
                .map(characterPosition -> characterPosition.equals(target));
        System.out.println(isReached);
        if (isReached.isFailed() || isReached.getValue()) {
            return isReached;
        }

        final Result<Boolean> isMoving = client.isCharacterMoving();
        System.out.println(isMoving);
        if (isMoving.isFailed() || isMoving.getValue()) {
            return Result.of(false);
        }

        return Result.fail(ResultCode.NOT_REACHED);
    }

}
