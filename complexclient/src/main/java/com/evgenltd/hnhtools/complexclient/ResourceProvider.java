package com.evgenltd.hnhtools.complexclient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 11:58</p>
 */
final class ResourceProvider {

    private final Map<Long, String> index = new ConcurrentHashMap<>();

    @Nullable
    String getResourceName(@NotNull final Long id) {
        return index.get(id);
    }

    void saveResource(@NotNull final Long id, @Nullable final String name) {
        index.put(id, name);
    }

}
