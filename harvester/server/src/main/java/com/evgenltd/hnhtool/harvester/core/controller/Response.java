package com.evgenltd.hnhtool.harvester.core.controller;


public class Response<T> {
    private T value;
    private boolean success;
    private String error;

    public Response(final T value) {
        this.value = value;
        this.success = true;
    }

    public Response(final String error) {
        this.error = error;
        this.success = false;
    }

    public Response(final String error, final Object... args) {
        this(String.format(error, args));
    }

    public T getValue() {
        return value;
    }
    public void setValue(final T value) {
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }
    public void setError(final String error) {
        this.error = error;
    }
}
