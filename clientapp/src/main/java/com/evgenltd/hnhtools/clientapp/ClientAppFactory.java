package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.impl.ClientAppImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-11-2019 00:57</p>
 */
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
