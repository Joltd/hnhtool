package com.evgenltd.hnhtools.common;

import com.evgenltd.hnhtools.entity.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 17:16</p>
 */
public class Result<T> {

    private static final Logger log = LogManager.getLogger(Result.class);

    private String code;
    private boolean success;
    private T value;

    private Result() {}

    public static <T> Result<T> ok(final T value) {
        final Result<T> result = new Result<>();
        result.value = value;
        result.code = ResultCode.OK;
        result.success = true;
        return result;
    }

    public static Result<Void> ok() {
        return Result.ok(null);
    }

    public static <T> Result<T> fail(final String code) {
        final Result<T> result = new Result<>();
        result.code = code;
        result.success = false;
        return result;
    }

    public boolean isFailed() {
        return !success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public T getValue() {
        return value;
    }

    public Result<T> thenAnyway(@NotNull final Consumer<? super T> action) {
        Objects.requireNonNull(action, "[Action] should not be empty");
        action.accept(value);
        return this;
    }

    public <U> Result<U> map() {
        return map(value -> null);
    }

    public <U> Result<U> map(@NotNull final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "[Mapper] should not be empty");
        if (isFailed()) {
            return Result.fail(code);
        } else {
            return Result.ok(mapper.apply(value));
        }
    }

    public Result<T> then(@NotNull final Runnable runnable) {
        return then(t -> {
            runnable.run();
            return Result.ok(t);
        });
    }

    public Result<T> then(@NotNull final Supplier<Result<T>> supplier) {
        return then(t -> supplier.get());
    }

    public <U> Result<U> then(@NotNull final Function<? super T, Result<U>> mapper) {
        Objects.requireNonNull(mapper, "[Mapper] should not be empty");
        if (isFailed()) {
            return Result.fail(code);
        } else {
            return mapper.apply(value);
        }
    }

    public Result<T> whenFailed(@NotNull final Runnable runnable) {
        Objects.requireNonNull(runnable, "[Runnable] should not be empty");
        if (isFailed()) {
            log.info("Fail result, " + code);
            runnable.run();
        }
        return this;
    }

    @Override
    public String toString() {
        if (value == null) {
            return String.format("code=[%s]", code);
        }
        return String.format("code=[%s], value=[%s]", code, value);
    }
}
