package com.evgenltd.hnhtools.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

    public static <T> T asCustomFromText(@NotNull final JsonNode node, @NotNull final String field, @NotNull final Function<String, T> converter) {
        return Optional.ofNullable(node.get(field))
                .map(JsonNode::asText)
                .map(converter)
                .orElse(null);
    }

    private static <T> T as(@NotNull final JsonNode node, @NotNull final String field, @NotNull final Function<JsonNode,T> getter) {
        return Optional.ofNullable(node.get(field))
                .map(getter)
                .orElse(null);
    }

    public static Iterable<JsonNode> asIterable(@NotNull final JsonNode node, @NotNull final String field) {
        final JsonNode collectionNode = node.get(field);
        if (collectionNode == null) {
            return Collections.emptyList();
        }

        return collectionNode;
    }

    public static ArrayNode asArrayNode(@NotNull final JsonNode node, @NotNull final String field) {
        return Optional.ofNullable(node.get(field))
                .map(n -> (ArrayNode) n)
                .orElse(JsonNodeFactory.instance.arrayNode());
    }

}
