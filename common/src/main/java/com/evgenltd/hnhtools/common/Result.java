package com.evgenltd.hnhtools.common;

import com.evgenltd.hnhtools.entity.ResultCode;
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

    private String code;
    private boolean success;
    private T value;

    private Result() {}

    public static <T> Result<T> of(final T value) {
        final Result<T> result = new Result<>();
        result.value = value;
        result.code = ResultCode.OK;
        result.success = true;
        return result;
    }

    public static Result<Void> ok() {
        return Result.of(null);
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

    public Result<T> peek(@NotNull final Consumer<? super T> action) {
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
            return Result.of(mapper.apply(value));
        }
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

    @Override
    public String toString() {
        return String.format("code=[%s], value=[%s]", code, value);
    }
}
