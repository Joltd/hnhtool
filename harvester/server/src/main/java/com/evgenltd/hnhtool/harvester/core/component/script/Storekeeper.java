package com.evgenltd.hnhtool.harvester.core.component.script;

import com.evgenltd.hnhtool.harvester.core.Agent;
import com.evgenltd.hnhtool.harvester.core.component.Holder;
import com.evgenltd.hnhtool.harvester.core.component.storekeeper.Warehousing;
import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Warehouse;
import com.evgenltd.hnhtool.harvester.core.entity.WarehouseCell;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;
import java.util.Objects;

public class Storekeeper {

    private final Agent agent;

    public Storekeeper(final Agent agent) {
        this.agent = agent;
    }

    public void store(final Warehouse warehouse, final List<KnownObject> items) {
        final Warehousing warehousing = new Warehousing();
        final Warehousing.Result result = warehousing.solve(warehouse, items);

        for (final Warehousing.HeapEntry entry : result.heapEntries()) {

            final Holder<KnownObject> heap = entry.heap();
            final KnownObject item = entry.item();

            if (heap.get().getId() == null) {
                agent.takeItemInHandFromInventory(item.getId());
                final Long heapId = agent.placeHeap(heap.get().getPosition());
                final KnownObject newHeap = agent.getKnownObjectService().findById(heapId);
                final WarehouseCell cell = warehouse.getCells()
                        .stream()
                        .filter(c -> Objects.equals(c.getPosition(), newHeap.getPosition()))
                        .findFirst()
                        .orElseThrow(() -> new ApplicationException(
                                "Warehouse [%s] does not have cell with  position [%s]",
                                warehouse.getId(),
                                newHeap.getPosition()
                        ));
                cell.setContainer(newHeap);
                heap.set(newHeap);
                continue;
            }

            agent.openHeap(heap.get().getId());
            agent.takeItemInHandFromInventory(item.getId());
            agent.dropItemFromHandInCurrentHeap();
        }

        for (final Warehousing.BoxEntry boxEntry : result.boxEntries()) {
            final KnownObject container = boxEntry.container();
            final KnownObject item = boxEntry.item();
            final IntPoint position = boxEntry.position();

            agent.openContainer(container);
            agent.takeItemInHandFromInventory(item.getId());
            agent.dropItemFromHandInCurrentInventory(position);
        }

    }

}
