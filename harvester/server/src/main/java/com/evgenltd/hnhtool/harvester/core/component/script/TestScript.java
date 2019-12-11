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
        getAgent().scan();

//        final KnownObject bark = knownObjectRepository.findByResourceName("gfx/invobjs/bark").get();
//
//        getAgent().takeItemInHandFromInventory(bark);

//        final IntPoint position = getAgent().getCharacterPosition();

//        getAgent().placeHeap(new IntPoint(-1003866, -960910));

        final KnownObject heap = knownObjectRepository.findById(268L).get();
        getAgent().openHeap(heap);

        getAgent().takeItemInHandFromCurrentHeap();

//        getAgent().move(new IntPoint(1000, 1000));
/*
        final KnownObject container = knownObjectRepository.findById(95L).get();
        getAgent().openContainer(container);

        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(
                1L,
                KnownObject.Place.MAIN_INVENTORY
        );

        for (int i = 0; i < items.size(); i++) {
            final KnownObject item = items.get(i);
            final int y = i / 3;
            final int x = i % 3;
            getAgent().takeItemInHandFromInventory(item);
            getAgent().dropItemFromHandInCurrentInventory(new IntPoint(x, y));
        }
*/
    }

}
