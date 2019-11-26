package com.evgenltd.hnhtool.harvester_old.research.command;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskRequired;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-05-2019 21:30</p>
 */
public class TakeItemFromWorld {

    private KnownObject item;

    private TakeItemFromWorld(final KnownObject item) {
        this.item = item;
    }

    @TaskRequired
    public static Result<Void> perform(@NotNull final KnownObject item) {
        Assert.valueRequireNonEmpty(item, "Item");
        return new TakeItemFromWorld(item).performImpl();
    }

    private Result<Void> performImpl() {
        return TaskContext.getAgent().getMatchedWorldObjectId(item.getId())
                .thenApplyCombine(woId -> TaskContext.getClient().interact(woId))
                .thenCombine(() -> AwaitForObject.disappear(item.getId()));
    }

}
