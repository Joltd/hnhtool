package com.evgenltd.hnhtools.messagebroker;

import com.evgenltd.hnhtools.messagebroker.impl.MessageBrokerImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MessageBrokerFactory {

    public static MessageBroker buildMessageBroker(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String host,
            final int port,
            @NotNull final String username,
            @NotNull final byte[] cookie,
            @NotNull final Consumer<JsonNode> relReceiver,
            @NotNull final Consumer<JsonNode> objectDataReceiver,
            boolean withMonitoring
    ) {
        return new MessageBrokerImpl(
                objectMapper,
                host,
                port,
                username,
                cookie,
                relReceiver,
                objectDataReceiver,
                withMonitoring
        );
    }

}
