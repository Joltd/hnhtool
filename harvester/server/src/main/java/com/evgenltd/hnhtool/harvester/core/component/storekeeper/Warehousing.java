package com.evgenltd.hnhtool.harvester.core.component.storekeeper;

import com.evgenltd.hnhtool.harvester.core.component.Holder;
import com.evgenltd.hnhtool.harvester.core.entity.*;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 23-03-2020 22:40</p>
 */
public final class Warehousing {

    private final Set<IntPoint> freePoints = new HashSet<>();
    private final List<Heap> heaps = new ArrayList<>();
    private final List<Box> boxes = new ArrayList<>();

    public Result solve(final List<KnownObject> containers, final List<KnownObject> items) {
        prepareContainerModel(containers);
        return solveImpl(items);
    }

    public Result solve(final Warehouse warehouse, final List<KnownObject> items) {
        prepareContainerModel(warehouse);
        return solveImpl(items);
    }

    private Result solveImpl(final List<KnownObject> items) {

        final Result result = new Result();

        for (final KnownObject item : items) {

            final HeapEntry heapEntry = findHeap(item);
            if (heapEntry != null) {
                result.heapEntries().add(heapEntry);
                continue;
            }

            final HeapEntry newHeapEntry = placeNewHeap(item);
            if (newHeapEntry != null) {
                result.heapEntries().add(newHeapEntry);
                continue;
            }

            final BoxEntry boxEntry = findBox(item);
            if (boxEntry != null) {
                result.boxEntries().add(boxEntry);
                continue;
            }

            result.skipped().add(item);

        }

        return result;
    }

    private void prepareContainerModel(final Warehouse warehouse) {
        warehouse.getCells()
                .stream()
                .filter(cell -> cell.getContainer() == null)
                .forEach(cell -> freePoints.add(cell.getPosition()));

        final List<KnownObject> containers = warehouse.getCells()
                .stream()
                .filter(cell -> cell.getContainer() != null)
                .map(WarehouseCell::getContainer)
                .collect(Collectors.toList());

        prepareContainerModel(containers);
    }

    private void prepareContainerModel(final List<KnownObject> containers) {

        for (final KnownObject container : containers) {

            final Resource resource = container.getResource();
            final IntPoint size = resource.getSize();

            freePoints.remove(container.getPosition());

            if (resource.isBox()) {

                final Box box = new Box(container, new boolean[size.getY()][size.getX()]);
                for (final KnownObject child : container.getChildren()) {
                    box.fillCells(child.getPosition(), child.getResource().getSize());
                }
                boxes.add(box);

            } else if (resource.isHeap()) {

                final Heap heap = new Heap(container, container.getChildren().size());
                heaps.add(heap);

            }
        }
    }

    private HeapEntry findHeap(final KnownObject item) {
        for (final Heap heap : heaps) {
            final Long itemResourceGroup = item.getResource().getGroup().getId();
            final Long heapResourceGroup = heap.getKnownObject().getResource().getGroup().getId();
            final boolean belongToSameGroup = itemResourceGroup != null
                    && heapResourceGroup != null
                    && Objects.equals(itemResourceGroup, heapResourceGroup);
            if (!belongToSameGroup) {
                continue;
            }

            if (heap.getCount() >= heap.getMax()) {
                continue;
            }

            // select nearest heap

            heap.incrementCount();
            return new HeapEntry(item, heap.getKnownObjectHolder());

        }

        return null;
    }

    private HeapEntry placeNewHeap(final KnownObject item) {
        if (freePoints.isEmpty()) {
            return null;
        }

        final ResourceGroup resourceGroup = item.getResource().getGroup();
        final Resource heapResource = resourceGroup.getResources()
                .stream()
                .filter(Resource::isHeap)
                .findFirst()
                .orElse(null);
        if (heapResource == null) {
            return null;
        }

        final Iterator<IntPoint> iterator = freePoints.iterator();
        final IntPoint position = iterator.next();
        iterator.remove();

        final KnownObject newHeap = new KnownObject();
        newHeap.setPosition(position);
        newHeap.setResource(heapResource);

        final Heap heap = new Heap(newHeap, 1);
        heaps.add(heap);

        return new HeapEntry(item, heap.getKnownObjectHolder());
    }

    private BoxEntry findBox(final KnownObject item) {
        for (final Box box : boxes) {
            final IntPoint suitablePosition = box.findSuitablePosition(item.getResource().getSize());
            if (suitablePosition != null) {
                box.fillCells(suitablePosition, item.getResource().getSize());
                return new BoxEntry(item, box.getKnownObject(), suitablePosition);
            }
        }

        return null;
    }

    public record Result(List<HeapEntry> heapEntries, List<BoxEntry> boxEntries, List<KnownObject> skipped) {
        public Result() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    public record HeapEntry(KnownObject item, Holder<KnownObject>heap) {}

    public record BoxEntry(KnownObject item, KnownObject container, IntPoint position) {}

    private static final class Heap {
        private final KnownObject knownObject;
        private final Holder<KnownObject> knownObjectHolder;
        private int count;
        private final int max;

        Heap(final KnownObject knownObject, final int count) {
            this.knownObject = knownObject;
            this.knownObjectHolder = Holder.of(knownObject);
            this.count = count;
            this.max = knownObject.getResource().getSize().getX();
        }

        KnownObject getKnownObject() {
            return knownObject;
        }

        public Holder<KnownObject> getKnownObjectHolder() {
            return knownObjectHolder;
        }

        int getCount() {
            return count;
        }
        void incrementCount() {
            this.count = this.count + 1;
        }

        int getMax() {
            return max;
        }
    }

    private static final class Box {
        private final KnownObject knownObject;
        private final boolean[][] cells;

        Box(final KnownObject knownObject, final boolean[][] cells) {
            this.knownObject = knownObject;
            this.cells = cells;
        }

        KnownObject getKnownObject() {
            return knownObject;
        }

        void fillCells(final IntPoint position, final IntPoint size) {
            for (int y = position.getY(); y < position.getY() + size.getY(); y++) {
                for (int x = position.getX(); x < position.getX() + size.getX(); x++) {
                    cells[y][x] = true;
                }
            }
        }

        IntPoint findSuitablePosition(final IntPoint size) {
            for (int y = 0; y < cells.length - (size.getY() - 1); y++) {
                for (int x = 0; x < cells[y].length - (size.getX() - 1); x++) {
                    if (isFit(x, y, size.getX(), size.getY())) {
                        return new IntPoint(x,y);
                    }
                }
            }
            return null;
        }

        private boolean isFit(final int positionX, final int positionY, final int sizeX, final int sizeY) {
            for (int y = positionY; y < positionY + sizeY; y++) {
                for (int x = positionX; x < positionX + sizeX; x++) {
                    if (cells[y][x]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
