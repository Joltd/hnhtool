package com.evgenltd.hnhtool.harvester.common.entity;

import com.evgenltd.hnhtool.harvester.common.service.Agent;
import com.evgenltd.hnhtools.common.Result;

import java.util.function.Function;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-04-2019 20:09</p>
 */
public interface Work extends Function<Agent, Result<Void>> {

}
