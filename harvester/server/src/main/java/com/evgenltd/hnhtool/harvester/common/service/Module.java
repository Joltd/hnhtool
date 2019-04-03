package com.evgenltd.hnhtool.harvester.common.service;

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
    default Runnable getTaskWork(String step) {
        return () -> {};
    }

}
