package com.evgenltd.hnhtools.complexclient;

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
    String getResourceName(@NotNull Long id);

    void saveResource(@NotNull Long id, @Nullable String name);

}
