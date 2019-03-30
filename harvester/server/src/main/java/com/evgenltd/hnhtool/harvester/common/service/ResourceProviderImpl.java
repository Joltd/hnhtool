package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtools.agent.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 00:04</p>
 */
@Service
public class ResourceProviderImpl implements ResourceProvider {

    private final Map<Integer, String> index = new ConcurrentHashMap<>();

    @Override
    @Nullable
    public String getResourceName(@NotNull final Integer id) {
        return index.get(id);
    }

    @Override
    public void saveResource(@NotNull final Integer id, @NotNull final String name) {
        index.put(id, name);
    }
}
