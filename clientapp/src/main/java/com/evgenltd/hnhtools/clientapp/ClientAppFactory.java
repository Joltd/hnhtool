package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.impl.ClientAppImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public final class ClientAppFactory {

    public static ClientApp buildClientApp(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String host,
            final int port,
            @NotNull final String username,
            @NotNull final byte[] cookie
    ) {
        return new ClientAppImpl(objectMapper, host, port, username, cookie);
    }

}
