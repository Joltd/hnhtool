package com.evgenltd.hnhtool.harvester.core.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class SingleResponseAdvise implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(final MethodParameter methodParameter, final Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            final Object o,
            final MethodParameter methodParameter,
            final MediaType mediaType,
            final Class<? extends HttpMessageConverter<?>> aClass,
            final ServerHttpRequest serverHttpRequest,
            final ServerHttpResponse serverHttpResponse
    ) {
        if (o instanceof Response) {
            return o;
        }
        return new Response<>(o);
    }
}
