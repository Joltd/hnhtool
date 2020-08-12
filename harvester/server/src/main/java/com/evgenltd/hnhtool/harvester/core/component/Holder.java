package com.evgenltd.hnhtool.harvester.core.component;

public class Holder<T> {

    private T value;

    private Holder(final T value) {
        this.value = value;
    }

    public static <T> Holder<T> of(final T value) {
        return new Holder<>(value);
    }

    public T get() {
        return value;
    }
    public void set(final T value) {
        this.value = value;
    }

}
