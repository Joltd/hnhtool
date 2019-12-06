package com.evgenltd.hnhtool.harvester.core.component.knapsack;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 06-12-2019 19:22
 */
public class KnapsackTest {

    @Test
    public void test() {

        final Box first = new Box(new IntPoint(8, 8));
        first.getItems().add(new Item(new IntPoint(0,0), new IntPoint(1,2)));
        first.getItems().add(new Item(new IntPoint(1,2), new IntPoint(1,1)));
        first.getItems().add(new Item(new IntPoint(1,3), new IntPoint(3,3)));
        first.getItems().add(new Item(new IntPoint(6,3), new IntPoint(2,1)));
        first.getItems().add(new Item(new IntPoint(2,4), new IntPoint(2,3)));
        first.getItems().add(new Item(new IntPoint(6,6), new IntPoint(2,2)));

        System.out.println(first);

//        final Box second = new Box(new IntPoint(4, 4));
//
//        final List<Box> boxes = new ArrayList<>(
//
//        );
//        final List<Item> items = Arrays.asList(
//
//        );
//
//        final List<Knapsack.Result<Box, Item>> results = new Knapsack<>(
//                boxes,
//                items,
//                Item::getPosition,
//                Item::getSize,
//                Box::getItems,
//                Box::getSize
//        ).place();

    }

    private static final class Item {
        private IntPoint position;
        private IntPoint size;

        public Item(final IntPoint position, final IntPoint size) {
            this.position = position;
            this.size = size;
        }

        public IntPoint getPosition() {
            return position;
        }

        public IntPoint getSize() {
            return size;
        }
    }

    private static final class Box {
        private IntPoint size;
        private List<Item> items = new ArrayList<>();

        public Box(final IntPoint size) {
            this.size = size;
        }

        public IntPoint getSize() {
            return size;
        }

        public List<Item> getItems() {
            return items;
        }

        @Override
        public String toString() {
            final boolean[][] cells = new boolean[size.getX()][size.getY()];
            for (final Item item : items) {
                for (int x = 0; x < item.getSize().getX(); x++) {
                    for (int y = 0; y < item.getSize().getY(); y++) {
                        cells[item.getPosition().getY() + y][item.getPosition().getX() + x] = true;
                    }
                }
            }

            final StringBuilder sb = new StringBuilder();
            for (int y = 0; y < cells.length; y++) {
                for (int x = 0; x < cells[y].length; x++) {
                    sb.append("[").append(cells[y][x] ? "o" : " ").append("]");
                }
                sb.append("\n");
            }

            return sb.toString();
        }
    }

}
