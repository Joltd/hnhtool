package com.evgenltd.hnhtools.common;

public interface Log {

    void debug(String message, Object... args);

    void debug(Throwable throwable, String message, Object... args);

    void info(String message, Object... args);

    void info(Throwable throwable, String message, Object... args);

    void warn(String message, Object... args);

    void warn(Throwable throwable, String message, Object... args);

    void error(String message, Object... args);

    void error(Throwable throwable, String message, Object... args);

}
