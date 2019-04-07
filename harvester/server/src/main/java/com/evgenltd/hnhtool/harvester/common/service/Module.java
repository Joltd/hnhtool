package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.common.entity.Work;
import com.evgenltd.hnhtools.common.Result;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 00:58</p>
 */
public interface Module {

    @NotNull
    default Predicate<Agent> getTaskRequirements(String step) {
        return agent -> true;
    }

    @NotNull
    default Work getTaskWork(String step) {
        return agent -> Result.ok();
    }

}
