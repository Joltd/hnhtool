package com.evgenltd.hnhtool.harvester.core.component;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 02:28</p>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestScript extends Script {

    @Override
    public void execute() {
        getAgent().await();
//        getAgent().openContainer(10L);
        // list of all items
//        getAgent().takeItemInHand(100L);
        // determine free space
        // get character id
//        getAgent().dropItemFromHandInInventory(1L, new IntPoint(1,1));

//        getAgent().move(new IntPoint(10,10));
    }

}
