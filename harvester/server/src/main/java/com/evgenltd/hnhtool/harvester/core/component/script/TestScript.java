package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
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

    private KnownObjectRepository knownObjectRepository;

    public TestScript(final KnownObjectRepository knownObjectRepository) {
        this.knownObjectRepository = knownObjectRepository;
    }

    @Override
    public void execute() {
        final KnownObject container = knownObjectRepository.findById(686L).get();
        getAgent().openContainer(container.getId());

        // list of all items
        getAgent().takeItemInHand(100L);
        // determine free space
        // get character id
//        getAgent().dropItemFromHandInInventory(1L, new IntPoint(1,1));

//        getAgent().move(new IntPoint(10,10));
    }

}
