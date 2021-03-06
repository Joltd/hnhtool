package com.evgenltd.hnhtool.harvester.core.component.storekeeper;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.entity.ResourceGroup;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.*;

public final class Warehousing {

    private final Set<IntPoint> freeCells = new HashSet<>();
    private final List<Heap> heaps = new ArrayList<>();
    private final List<Box> boxes = new ArrayList<>();

    public Result solve(final List<KnownObject> containers, final List<IntPoint> cells, final KnownObject item) {
        prepareContainerModel(containers, cells);
        return solveImpl(item);
    }

    public BoxEntry solve(final KnownObject box, final KnownObject item) {
        final Result solution = solve(Collections.singletonList(box), Collections.emptyList(), item);
        return solution.boxEntry() != null
                ? solution.boxEntry()
                : null;
    }

    private Result solveImpl(final KnownObject item) {
        final HeapEntry heapEntry = findHeap(item);
        if (heapEntry != null) {
            return new Result(heapEntry);
        }

        final HeapEntry newHeapEntry = placeNewHeap(item);
        if (newHeapEntry != null) {
            return new Result(newHeapEntry);
        }

        final BoxEntry boxEntry = findBox(item);
        if (boxEntry != null) {
            return new Result(boxEntry);
        }

        return new Result();
    }

    private void prepareContainerModel(final List<KnownObject> containers, final List<IntPoint> cells) {

        freeCells.addAll(cells);

        for (final KnownObject container : containers) {

            final Resource resource = container.getResource();

            freeCells.remove(container.getPosition());

            if (resource.isBox()) {

                final Box box = new Box(container);

                boxes.add(box);

            } else if (resource.isHeap()) {

                final Heap heap = new Heap(container, container.getChildren().size());
                heaps.add(heap);

            }
        }
    }

    private HeapEntry findHeap(final KnownObject item) {
        for (final Heap heap : heaps) {
            final Long itemResourceGroup = Optional.of(item)
                    .map(KnownObject::getResource)
                    .map(Resource::getGroup)
                    .map(ResourceGroup::getId)
                    .orElse(null);
            final Long heapResourceGroup = Optional.of(heap)
                    .map(Heap::getKnownObject)
                    .map(KnownObject::getResource)
                    .map(Resource::getGroup)
                    .map(ResourceGroup::getId)
                    .orElse(null);
            final boolean belongToSameGroup = itemResourceGroup != null
                    && heapResourceGroup != null
                    && Objects.equals(itemResourceGroup, heapResourceGroup);
            if (!belongToSameGroup) {
                continue;
            }

            if (heap.getCount() >= heap.getMax()) {
                System.out.printf("Heap [%s], count [%s], max [%s]\n", heap.getKnownObject().getId(), heap.getCount(), heap.getMax());
                continue;
            }

            heap.incrementCount();
            return new HeapEntry(heap.getKnownObject());
        }

        return null;
    }

    private HeapEntry placeNewHeap(final KnownObject item) {
        if (freeCells.isEmpty()) {
            return null;
        }

        final ResourceGroup resourceGroup = item.getResource().getGroup();
        if (resourceGroup == null) {
            return null;
        }

        final Resource heapResource = resourceGroup.getResources()
                .stream()
                .filter(Resource::isHeap)
                .findFirst()
                .orElse(null);
        if (heapResource == null) {
            return null;
        }

        final Iterator<IntPoint> iterator = freeCells.iterator();
        final IntPoint position = iterator.next();
        iterator.remove();

        final KnownObject newHeap = new KnownObject();
        newHeap.setPosition(position);
        newHeap.setResource(heapResource);

        final Heap heap = new Heap(newHeap, 1);
        heaps.add(heap);

        return new HeapEntry(heap.getKnownObject());
    }

    private BoxEntry findBox(final KnownObject item) {
        for (final Box box : boxes) {
            final IntPoint suitablePosition = box.findSuitablePosition(item.getResource().getSize());
            if (suitablePosition != null) {
                box.fillCells(suitablePosition, item.getResource().getSize());
                return new BoxEntry(box.getKnownObject(), suitablePosition);
            }
        }

        return null;
    }

    public record Result(HeapEntry heapEntry, BoxEntry boxEntry, boolean skipped) {
        public Result(final HeapEntry heapEntry) {
            this(heapEntry, null, false);
        }

        public Result(final BoxEntry boxEntry) {
            this(null, boxEntry, false);
        }

        public Result() {
            this(null, null, true);
        }
    }

    public record HeapEntry(KnownObject heap) {}

    public record BoxEntry(KnownObject container, IntPoint position) {}

    private static final class Heap {
        private final KnownObject knownObject;
        private int count;
        private final int max;

        Heap(final KnownObject knownObject, final int count) {
            this.knownObject = knownObject;
            this.count = count;
            this.max = knownObject.getResource().getSize().getX();
        }

        KnownObject getKnownObject() {
            return knownObject;
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

    public static final class Box {
        private KnownObject knownObject;
        private final boolean[][] cells;

        Box(final KnownObject knownObject) {
            this.knownObject = knownObject;
            final IntPoint size = knownObject.getResource().getSize();
            this.cells = new boolean[size.getY()][size.getX()];
            for (final KnownObject child : knownObject.getChildren()) {
                fillCells(child.getPosition(), child.getResource().getSize());
            }
        }

        public Box(final IntPoint size) {
            this.cells = new boolean[size.getY()][size.getX()];
        }

        KnownObject getKnownObject() {
            return knownObject;
        }

        public void fillCells(final IntPoint position, final IntPoint size) {
            for (int y = position.getY(); y < position.getY() + size.getY(); y++) {
                for (int x = position.getX(); x < position.getX() + size.getX(); x++) {
                    cells[y][x] = true;
                }
            }
        }

        public IntPoint findSuitablePosition(final IntPoint size) {
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
