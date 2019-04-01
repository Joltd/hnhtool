package com.evgenltd.hnhtool.harvester.common.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 31-03-2019 13:11</p>
 */
public class ObjectIndex {

    private final BiMap<Long, Long> index = HashBiMap.create();

    public void putMatch(final Long knownObjectId, final Long worldObjectId) {
        index.put(knownObjectId, worldObjectId);
    }

}
