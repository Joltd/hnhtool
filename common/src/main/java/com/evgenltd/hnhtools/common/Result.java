package com.evgenltd.hnhtools.common;

import com.evgenltd.hnhtools.entity.ResultCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public <U> Result<U> cast() {
        return then(() -> null);
    }

    public Result<T> then(Runnable runnable) {
        if (isSuccess()) {
            runnable.run();
        }
        return this;
    }

    public Result<T> then(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value);
        }
        return this;
    }

    public <U> Result<U> then(Supplier<U> supplier) {
        if (isSuccess()) {
            return Result.ok(supplier.get());
        } else {
            return Result.fail(code);
        }
    }

    public <U> Result<U> thenCombine(Supplier<Result<U>> supplier) {
        if (isSuccess()) {
            return supplier.get();
        } else {
            return Result.fail(code);
        }
    }

    public <U> Result<U> thenApply(Function<T,U> function) {
        if (isSuccess()) {
            return Result.ok(function.apply(value));
        } else {
            return Result.fail(code);
        }
    }

    public <U> Result<U> thenApplyCombine(Function<T,Result<U>> function) {
        if (isSuccess()) {
            return function.apply(value);
        } else {
            return Result.fail(code);
        }
    }

    public Result<T> anyway(Runnable runnable) {
        runnable.run();
        return this;
    }

    public Result<T> anyway(Consumer<? super T> consumer) {
        consumer.accept(value);
        return this;
    }

//    public <U> Result<U> anyway(Supplier<U> supplier) {}
//    public <U> Result<U> anywayCombine(Supplier<Result<U>> supplier) {}
//    public <U> Result<U> anyway(Function<T,U> function) {}
//    public <U> Result<U> anywayCombine(Function<T,Result<U>> function) {}

    public Result<T> whenFail(Runnable runnable) {
        if (isFailed()) {
            runnable.run();
        }
        return this;
    }

//    public Result<T> whenFail(Consumer<T> consumer) {}
    public Result<T> whenFail(Supplier<T> supplier) {
        if (isFailed()) {
            return Result.ok(supplier.get());
        }
        return this;
    }
    public Result<T> whenFailCombine(Supplier<Result<T>> supplier) {
        if (isFailed()) {
            return supplier.get();
        }
        return this;
    }
//    public Result<T> whenFail(Function<T,T> function) {}
//    public Result<T> whenFailCombine(Function<T,Result<T>> function) {}

    @Override
    public String toString() {
        if (value == null) {
            return String.format("code=[%s]", code);
        }
        return String.format("code=[%s], value=[%s]", code, value);
    }
}
