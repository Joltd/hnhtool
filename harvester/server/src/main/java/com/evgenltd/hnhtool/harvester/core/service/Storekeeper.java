package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.aspect.AgentCommand;
import com.evgenltd.hnhtool.harvester.core.component.storekeeper.Warehousing;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.repository.KnownObjectRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Storekeeper {

    private final KnownObjectRepository knownObjectRepository;
    private final KnownObjectService knownObjectService;
    private final AreaService areaService;

    public Storekeeper(
            final KnownObjectRepository knownObjectRepository,
            final KnownObjectService knownObjectService,
            final AreaService areaService
    ) {
        this.knownObjectRepository = knownObjectRepository;
        this.knownObjectService = knownObjectService;
        this.areaService = areaService;
    }

    @AgentCommand
    public boolean store(final Long areaId, final Long itemId) {

        final List<IntPoint> cells = areaService.splitByPositions(areaId);
        final KnownObject item = knownObjectRepository.findOne(itemId);

        // more than one iteration in case if selected container becomes invalid
        while (true) {

            final List<KnownObject> containers = knownObjectService.loadContainersInArea(areaId);

            final Warehousing.Result solution = new Warehousing().solve(containers, cells, item);
            if (solution.skipped()) {
                return false;
            }

            if (solution.heapEntry() != null) {
                final KnownObject heap = solution.heapEntry().heap();
                A.moveByRoute(heap.getPosition());
                if (heap.getId() == null) {
                    A.takeItemInHandFromInventory(itemId);
                    A.placeHeap(heap.getPosition());
                    return true;
                } else {
                    final boolean valid = A.openHeap(heap.getId());
                    if (valid) {
                        A.takeItemInHandFromInventory(itemId);
                        A.dropItemFromHandInCurrentHeap();
                        return true;
                    }
                }
            } else if (solution.boxEntry() != null) {

                // move by route
                // open container -> scanned, may change configuration
                // (!) check if item can be placed in selected position
                //  - yes -> do it
                //  - no -> continue to new iteration

                throw new UnsupportedOperationException();
            }

        }

    }

    @AgentCommand
    public boolean takeItemInInventoryFromHeap(final Long heapId, final Agent.InventoryType type) {
        final KnownObject heap = knownObjectRepository.findOne(heapId);
        A.moveByRoute(heap.getPosition());
        A.openHeap(heapId);
        boolean result = A.takeItemInHandFromCurrentHeap();
        if (!result) {
            return false;
        }

        result = A.dropItemFromHandInInventory(type);
        if (!result) {
            A.dropItemFromHandInCurrentHeap();
        }

        return result;
    }

    @AgentCommand
    public void takeItemsInInventoryFromHeap(final Long heapId, final Agent.InventoryType type) {
        final KnownObject heap = knownObjectRepository.findOne(heapId);
        A.moveByRoute(heap.getPosition());
        A.openHeap(heapId);

        while (true) {

            boolean result = A.takeItemInHandFromCurrentHeap();
            if (!result) {
                return;
            }

            result = A.dropItemFromHandInInventory(type);
            if (!result) {
                A.dropItemFromHandInCurrentHeap();
                return;
            }

        }
    }



}
