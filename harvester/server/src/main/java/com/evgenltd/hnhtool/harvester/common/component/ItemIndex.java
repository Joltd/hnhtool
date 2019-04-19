package com.evgenltd.hnhtool.harvester.common.component;

import com.evgenltd.hnhtools.common.Result;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 17-04-2019 23:41</p>
 */
public class ItemIndex {

    public Result<Integer> getMatchedWorldItemId(final Long knownItemId) {
        return Result.ok(0);
    }

    public Result<Long> getMatchedKnownItemId(final Integer worldItemId) {
        return Result.ok(0L);
    }

}
