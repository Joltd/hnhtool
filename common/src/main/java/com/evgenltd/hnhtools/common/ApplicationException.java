package com.evgenltd.hnhtools.common;

/**
 * <p>Common exception class for any exception cases in application.
 * Contains suitable constructors with string formatter {@link String#format(String, Object...)}</p>
 * <p></p>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 01:55</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ApplicationException extends RuntimeException {

    public ApplicationException() {
        super();
    }

    public ApplicationException(final Throwable throwable) {
        super(throwable);
    }

    public ApplicationException(final Throwable throwable, final String template, final Object... args) {
        super(String.format(template, args), throwable);
    }

    public ApplicationException(final String template, final Object... args) {
        super(String.format(template, args));
    }

    public static <T extends Enum<?>> ApplicationException unsupportedEnumValue(final T value) {
        return new ApplicationException("Unsupported value [%s] of enum [%s]", value, value.getClass().getSimpleName());
    }

}
