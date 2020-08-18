package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.core.repository.WarehouseRepository;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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

        getAgent().move(new IntPoint(-955904,-926208));
        getAgent().move(new IntPoint(-949760,-924160));

//        takeItem();
//        placeHeap();
    }

    private void takeItem() {
        final KnownObject heap = knownObjectRepository.findByResourceNameLikeAndLostIsFalse(
                "gfx/terobjs/stockpile-branch")
                .stream()
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Stockpile not found"));

        getAgent().openHeap(heap.getId());
        getAgent().takeItemInHandFromCurrentHeap();

//        final Long heapId = getAgent().placeHeap(new IntPoint(-953856, -929280));
//
//        getAgent().openHeap(heap.getId());
//        getAgent().takeItemInHandFromCurrentHeap();

        getAgent().dropItemFromHandInMainInventory(new IntPoint(1,1));

//        getAgent().openHeap(heapId);
//
//        getAgent().takeItemInHandFromInventory(4987L);
//
//        getAgent().dropItemFromHandInCurrentHeap();
    }

    private void placeHeap() {
        getAgent().takeItemInHandFromInventory(4987L);
        getAgent().placeHeap(new IntPoint(-952832,-927232));
        getAgent().move(new IntPoint(-950784,-927232));
    }

}
