package com.evgenltd.hnhtools.command;

import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.ResultCode;

/**
 * <p>Wrapper for performing async commands in synchronous way.</p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 17:06</p>
 */
public abstract class AbstractCommand {

    private static final long COMMAND_AWAIT_TIMEOUT = 1000L;

    Result<Void> await() {
        while (true) {

            try {
                Thread.sleep(COMMAND_AWAIT_TIMEOUT);
            } catch (InterruptedException e) {
                return Result.fail(ResultCode.INTERRUPTED);
            }

            final Result<Boolean> result = isDone();
            if (result.isFailed()) {
                return result.map();
            }

            if (result.getValue()) {
                return Result.ok();
            }

        }
    }

    protected abstract Result<Boolean> isDone();

}
