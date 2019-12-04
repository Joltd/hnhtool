package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
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
        getAgent().scan();

//        getAgent().move(new IntPoint(1000, 1000));

        final KnownObject container = knownObjectRepository.findById(95L).get();
        getAgent().openContainer(container);

        final KnownObject leaf = knownObjectRepository.findById(493L).get();
        getAgent().takeItemInHandFromInventory(leaf);
        getAgent().dropItemFromHandInMainInventory(new IntPoint());

//        getAgent().move(new IntPoint(10,10));
    }

}
