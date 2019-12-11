package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(
//                getAgent().getCharacterId(),
                342L,
//                KnownObject.Place.MAIN_INVENTORY
                null
        )
                .stream()
                .filter(item -> item.getResource().getName().equals("gfx/invobjs/bark"))
                .collect(Collectors.toList());


        /*getAgent().takeItemInHandFromInventory(items.get(0));
        getAgent().placeHeap(new IntPoint(-1003866, -960910));*/

        final KnownObject heap = knownObjectRepository.findById(342L).get();
        getAgent().openHeap(heap);

//        getAgent().takeItemInHandFromInventory(items.get(0));
//        getAgent().dropItemFromHandInCurrentHeap();

        for (int i = 0; i < items.size(); i++) {
            getAgent().takeItemInHandFromCurrentHeap();
            getAgent().dropItemFromHandInMainInventory(new IntPoint(i, 2));
        }

    }

}
