package com.evgenltd.hnhtools.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-11-2019 17:58
 */
public class JsonUtil {

    public static String asText(@NotNull final JsonNode node, @NotNull final String field) {
        return as(node, field, JsonNode::asText);
    }

    public static Boolean asBoolean(@NotNull final JsonNode node, @NotNull final String field) {
        return as(node, field, JsonNode::asBoolean);
    }

    public static Integer asInt(@NotNull final JsonNode node, @NotNull final String field) {
        return as(node, field, JsonNode::asInt);
    }

    public static Long asLong(@NotNull final JsonNode node, @NotNull final String field) {
        return as(node, field, JsonNode::asLong);
    }

    public static Double asDouble(@NotNull final JsonNode node, @NotNull final String field) {
        return as(node, field, JsonNode::asDouble);
    }

    private static <T> T as(@NotNull final JsonNode node, @NotNull final String field, @NotNull final Function<JsonNode,T> getter) {
        return Optional.ofNullable(node.get(field))
                .map(getter)
                .orElse(null);
    }

}
