package com.evgenltd.hnhtool.harvester.core.component.knapsack;

import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 06-12-2019 17:12
 */
public class Knapsack<B,I> {

    private final List<B> boxes;
    private final List<I> items;
    private final Function<I,IntPoint> itemGetPosition;
    private final Function<I,IntPoint> itemGetSize;
    private final Function<B,Collection<I>> boxGetItems;
    private final Function<B,IntPoint> boxGetSize;

    public Knapsack(
            final List<B> boxes,
            final List<I> items,
            final Function<I, IntPoint> itemGetPosition,
            final Function<I, IntPoint> itemGetSize,
            final Function<B, Collection<I>> boxGetItems,
            final Function<B, IntPoint> boxGetSize
    ) {
        this.boxes = boxes;
        this.items = items;
        this.itemGetPosition = itemGetPosition;
        this.itemGetSize = itemGetSize;
        this.boxGetItems = boxGetItems;
        this.boxGetSize = boxGetSize;
    }

    public List<Result<B,I>> place() {

        final List<BoxWrapper<B>> boxWrappers = this.boxes.stream()
                .map(this::wrapBox)
                .collect(Collectors.toList());

        items.sort(Comparator.comparing(this::getItemKey).reversed());

        final List<Result<B,I>> results = new ArrayList<>();

        for (final I item : items) {

            final IntPoint itemSize = itemGetSize.apply(item);

            for (final BoxWrapper<B> boxWrapper : boxWrappers) {

                final IntPoint suitablePosition = boxWrapper.findSuitablePosition(itemSize);
                if (suitablePosition != null) {

                    boxWrapper.fillCells(suitablePosition, itemSize);
                    results.add(new Result<>(item, boxWrapper.getBox(), suitablePosition));
                    break;

                }

            }

        }

        return results;

    }

    private BoxWrapper<B> wrapBox(final B box) {
        final IntPoint boxSize = boxGetSize.apply(box);
        final boolean[][] cells = new boolean[boxSize.getX()][boxSize.getY()];
        final BoxWrapper<B> boxWrapper = new BoxWrapper<>(box, cells);

        boxGetItems.apply(box)
                .forEach(item -> {
                    final IntPoint position = itemGetPosition.apply(item);
                    final IntPoint size = itemGetSize.apply(item);
                    boxWrapper.fillCells(position, size);
                });


        return boxWrapper;
    }

    private Integer getItemKey(final I item) {
        final IntPoint size = itemGetSize.apply(item);
        return size.getX() * size.getY();
    }

    public static final class Result<B,I> {
        private I item;
        private B box;
        private IntPoint position;

        Result(final I item, final B box, final IntPoint position) {
            this.item = item;
            this.box = box;
            this.position = position;
        }

        public I getItem() {
            return item;
        }

        public B getBox() {
            return box;
        }

        public IntPoint getPosition() {
            return position;
        }
    }

    private static final class BoxWrapper<B> {
        private B box;
        private boolean[][] cells;

        BoxWrapper(final B box, final boolean[][] cells) {
            this.box = box;
            this.cells = cells;
        }

        B getBox() {
            return box;
        }

        void fillCells(final IntPoint position, final IntPoint size) {
            for (int x = position.getX(); x < position.getX() + size.getX(); x++) {
                for (int y = position.getY(); y < position.getY() + size.getY(); y++) {
                    cells[x][y] = true;
                }
            }
        }

        IntPoint findSuitablePosition(final IntPoint size) {
            for (int x = 0; x < cells.length - size.getX() - 1; x++) {
                for (int y = 0; y < cells[x].length - size.getY() - 1; y++) {
                    if (isFit(x, y, size.getX(), size.getY())) {
                        return new IntPoint(x,y);
                    }
                }
            }
            return null;
        }

        private boolean isFit(final int positionX, final int positionY, final int sizeX, final int sizeY) {
            for (int x = positionX; x < positionX + sizeX; x++) {
                for (int y = positionY; y < positionY + sizeY; y++) {
                    if (cells[x][y]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
