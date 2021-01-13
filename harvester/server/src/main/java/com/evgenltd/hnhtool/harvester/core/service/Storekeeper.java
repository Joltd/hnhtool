package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.Agent;
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

    private Agent agent;

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

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

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
                getAgent().moveByRoute(heap.getPosition());
                if (heap.getId() == null) {
                    getAgent().takeItemInHandFromInventory(itemId);
                    getAgent().placeHeap(heap.getPosition());
                    return true;
                } else {
                    final boolean valid = getAgent().openHeap(heap.getId());
                    if (valid) {
                        getAgent().takeItemInHandFromInventory(itemId);
                        getAgent().dropItemFromHandInCurrentHeap();
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



}
