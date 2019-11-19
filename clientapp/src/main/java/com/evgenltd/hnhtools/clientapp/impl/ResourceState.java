package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 19-11-2019 19:57
 */
final class ResourceState {

    private static final String ID = "resourceId";
    private static final String NAME = "resourceName";

    private final Map<Long, String> index = new HashMap<>();

    synchronized void putResource(@NotNull final JsonNode data) {
        final Long id = JsonUtil.asLong(data, ID);
        if (id == null) {
            return;
        }
        index.put(id, JsonUtil.asText(data, NAME));
    }

    @Nullable
    synchronized String getResource(@NotNull final Long id) {
        return index.get(id);
    }

}
