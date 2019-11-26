package com.evgenltd.hnhtool.harvester_old.research.service;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester_old.common.ResourceConstants;
import com.evgenltd.hnhtool.harvester_old.common.component.TaskContext;
import com.evgenltd.hnhtool.harvester_old.research.entity.ResearchResultCode;
import com.evgenltd.hnhtools.common.Assert;
import com.evgenltd.hnhtools.common.Result;
import com.evgenltd.hnhtools.complexclient.entity.WorldInventory;
import com.evgenltd.hnhtools.complexclient.entity.WorldItem;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-05-2019 19:54</p>
 */
public class InventorySolver {

    private KnownObject container;
    private int totalSize;
    private Set<IntPoint> occupiedSlots;
    private Set<IntPoint> emptySlots;

    private InventorySolver(final KnownObject container) {
        this.container = container;
    }

    public static Result<IntPoint> getFreeSlot(@NotNull final KnownObject container, @NotNull final IntPoint size) {
        Assert.valueRequireNonEmpty(container, "Container");
        Assert.valueRequireNonEmpty(size, "Size");
        final InventorySolver inventorySolver = new InventorySolver(container);
        return inventorySolver.readState()
                .thenCombine(() -> inventorySolver.getFreeSlot(size));
    }

    public static Result<Void> fillItemCount(@NotNull final KnownObject container) {
        Assert.valueRequireNonEmpty(container, "Container");
        final InventorySolver inventorySolver = new InventorySolver(container);
        return inventorySolver.readState()
                .then((Runnable) inventorySolver::fillItemCount);
    }

    //

    private Result<IntPoint> getFreeSlot(final IntPoint size) {
        for (final IntPoint emptySlot : emptySlots) {
            final boolean isSuitable = emptySlots.containsAll(slotRectToCollection(emptySlot, size));
            if (isSuitable) {
                return Result.ok(emptySlot);
            }
        }

        return Result.fail(ResearchResultCode.NOT_ENOUGH_SPACE_IN_INVENTORY);
    }

    private void fillItemCount() {
        container.setCount(occupiedSlots.size());
        container.setMax(totalSize);
    }

    //

    private Result<Void> readState() {
        return TaskContext.getAgent().getMatchedWorldObjectId(container.getId())
                .thenApplyCombine(worldInventoryId -> TaskContext.getClient().getInventory(worldInventoryId))
                .thenApplyCombine(this::readSlots);
    }

    private Result<Void> readSlots(final WorldInventory inventory) {
        totalSize = inventory.getSize().getX() * inventory.getSize().getY();
        emptySlots = slotRectToCollection(new IntPoint(), inventory.getSize());
        occupiedSlots = new LinkedHashSet<>();

        for (final WorldItem item : inventory.getItems()) {
            final IntPoint itemPosition = item.getPosition();
            final Result<IntPoint> itemSize = ResourceConstants.getSize(item.getResource());
            if (itemSize.isFailed()) {
                return itemSize.cast();
            }

            slotRectToCollection(itemPosition, itemSize.getValue()).forEach(occupiedSlot -> {
                occupiedSlots.add(occupiedSlot);
                emptySlots.remove(occupiedSlot);
            });
        }

        return Result.ok();
    }


    private Set<IntPoint> slotRectToCollection(final IntPoint from, final IntPoint size) {
        final IntPoint to = from.add(size);
        final Set<IntPoint> result = new LinkedHashSet<>();
        for (int y = from.getY(); y < to.getY(); y++) {
            for (int x = from.getX(); x < to.getX(); x++) {
                result.add(new IntPoint(x, y));
            }
        }
        return result;
    }

}
