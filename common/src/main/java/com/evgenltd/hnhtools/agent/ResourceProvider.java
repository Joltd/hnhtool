package com.evgenltd.hnhtools.agent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 30-03-2019 11:58</p>
 */
public interface ResourceProvider {

    @Nullable
    String getResourceName(@NotNull Integer id);

    void saveResource(@NotNull Integer id, @NotNull String name);

}
