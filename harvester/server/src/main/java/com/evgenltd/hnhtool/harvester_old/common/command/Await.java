package com.evgenltd.hnhtool.harvester_old.common.command;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.entity.ResultCode;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-04-2019 20:28</p>
 */
public class Await {

    private static final Long DEFAULT_TIMEOUT = 1000L;
    private static final Long MIN_TIMEOUT = 100L;
    private static final Long MAX_TIMEOUT = 10000L;

    private static final Integer DEFAULT_ITERATIONS = 30;
    private static final Integer MIN_ITERATIONS = 1;
    private static final Integer MAX_ITERATIONS = 100;

    private Supplier<Result<Boolean>> isAwaitDone;
    private Long timeout = DEFAULT_TIMEOUT;
    private Integer iterations = DEFAULT_ITERATIONS;

    public static Result<Void> perform(@NotNull final Supplier<Result<Boolean>> isAwaitDone) {
        return of(isAwaitDone).perform();
    }

    public static Result<Void> performSimple(@NotNull final Supplier<Boolean> isAwaitDone) {
        return simple(isAwaitDone).perform();
    }

    public static Await of(@NotNull final Supplier<Result<Boolean>> isAwaitDone) {
        Assert.valueRequireNonEmpty(isAwaitDone, "IsAwaitDone");

        final Await await = new Await();
        await.isAwaitDone = isAwaitDone;
        return await;
    }

    public static Await simple(@NotNull final Supplier<Boolean> isAwaitDone) {
        return of(() -> Result.ok(isAwaitDone.get()));
    }

    public Await timeout(@NotNull final Long timeout) {
        Assert.valueRequireNonEmpty(timeout, "Timeout");
        if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT) {
            throw new ApplicationException("Timeout [%s] is not satisfied requirements", timeout);
        }
        this.timeout = timeout;
        return this;
    }

    public Await iterations(@NotNull final Integer iterations) {
        Assert.valueRequireNonEmpty(iterations, "Iterations");
        if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
            throw new ApplicationException("Iterations [%s] is not satisfied requirements", iterations);
        }
        this.iterations = iterations;
        return this;
    }

    public Result<Void> perform() {
        for (int iterations = 0; iterations < this.iterations; iterations++) {

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                return Result.fail(ResultCode.INTERRUPTED);
            }

            final Result<Boolean> result = isAwaitDone.get();
            if (result.isFailed()) {
                return result.cast();
            }

            if (result.getValue()) {
                return Result.ok();
            }

        }

        return Result.fail(ResultCode.AWAIT_MAX_ITERATION_EXCEEDED);
    }

}
