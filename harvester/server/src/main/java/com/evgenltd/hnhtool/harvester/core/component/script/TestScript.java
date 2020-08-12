package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Warehouse;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.WarehouseRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

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

        final Warehouse warehouse = warehouseRepository.findById(19L).get();
        final List<KnownObject> items = knownObjectRepository.findAllById(Arrays.asList(4250L, 4251L, 4252L, 4253L));

        getStorekeeper().store(warehouse, items);

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
