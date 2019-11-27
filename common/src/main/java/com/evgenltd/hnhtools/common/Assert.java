package com.evgenltd.hnhtools.common;

import org.jetbrains.annotations.Contract;

/**
 * <p>Class for basic method arguments validation.</p>
 * <p></p>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 01:55</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Assert {

    public static boolean isNotEmpty(final Object value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(final Object value) {
        return value == null
                || (value instanceof String && ((String) value).isEmpty());
    }

    @Contract("null, _ -> fail")
    public static void valueRequireNonEmpty(final Object value, final String valueName) {
        requireNonEmpty(value, "[%s] should not be empty", valueName);
    }

    @Contract("null, _, _ -> fail")
    public static void requireNonEmpty(final Object value, final String template, final Object... args) {
        if (isEmpty(value)) {
            throw new ApplicationException(template, args);
        }
    }

    public static <T> T orDefault(final T value, final T defaultValue) {
        return isEmpty(value)
                ? defaultValue
                : value;
    }

}
