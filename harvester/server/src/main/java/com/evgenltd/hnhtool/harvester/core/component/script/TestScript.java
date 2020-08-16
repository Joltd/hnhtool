package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.WarehouseRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class TestScript extends Script {

    private final KnownObjectRepository knownObjectRepository;
    private final WarehouseRepository warehouseRepository;

    public TestScript(
            final KnownObjectRepository knownObjectRepository,
            final WarehouseRepository warehouseRepository
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void execute() {
        getAgent().scan();

        getRouting().move(new IntPoint(-948736,-923136));

//        final Warehouse warehouse = warehouseRepository.findAll().stream().findFirst().get();
//        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(getAgent().getCharacterId(), KnownObject.Place.MAIN_INVENTORY);
//
//        getStorekeeper().store(warehouse, items);

//        final List<KnownObject> items = knownObjectRepository.findByParentIdAndPlace(
////                getAgent().getCharacterId(),
//                342L,
////                KnownObject.Place.MAIN_INVENTORY
//                null
//        )
//                .stream()
//                .filter(item -> item.getResource().getName().equals("gfx/invobjs/bark"))
//                .collect(Collectors.toList());
//
//
//        /*getAgent().takeItemInHandFromInventory(items.get(0));
//        getAgent().placeHeap(new IntPoint(-1003866, -960910));*/
//
//        final KnownObject heap = knownObjectRepository.findById(342L).get();
//        getAgent().openHeap(heap);
//
////        getAgent().takeItemInHandFromInventory(items.get(0));
////        getAgent().dropItemFromHandInCurrentHeap();
//
//        for (int i = 0; i < items.size(); i++) {
//            getAgent().takeItemInHandFromCurrentHeap();
//            getAgent().dropItemFromHandInMainInventory(new IntPoint(i, 2));
//        }

    }

}
