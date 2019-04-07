package com.evgenltd.hnhtools.command;

import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.ResultCode;

import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-04-2019 20:28</p>
 */
public class CommandUtils {

    private static final long COMMAND_AWAIT_TIMEOUT = 1000L;

    public static Result<Void> await(final Supplier<Boolean> isAwaitDone) {
        return awaitWithResult(() -> Result.ok(isAwaitDone.get()));
    }

    public static Result<Void> awaitWithResult(final Supplier<Result<Boolean>> isAwaitDone) {

        while (true) {

            try {
                Thread.sleep(COMMAND_AWAIT_TIMEOUT);
            } catch (InterruptedException e) {
                return Result.fail(ResultCode.INTERRUPTED);
            }

            final Result<Boolean> result = isAwaitDone.get();
            if (result.isFailed()) {
                return result.map();
            }

            if (result.getValue()) {
                return Result.ok();
            }

        }

    }

}
