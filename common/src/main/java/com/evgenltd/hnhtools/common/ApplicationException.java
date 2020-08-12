package com.evgenltd.hnhtools.common;

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
        return new ApplicationException("Unsupported value [%s] ok enum [%s]", value, value.getClass().getSimpleName());
    }

}
