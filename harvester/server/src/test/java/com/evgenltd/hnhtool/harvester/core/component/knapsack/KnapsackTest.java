package com.evgenltd.hnhtool.harvester.core.component.knapsack;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 06-12-2019 19:22
 */
public class KnapsackTest {

    @Test
    public void test() {

        final Box first = new Box(new IntPoint(4, 4));
        first.getItems().add(new Item(new IntPoint(0,0), new IntPoint(1,2)));
        first.getItems().add(new Item(new IntPoint(2,1), new IntPoint(1,1)));
        first.getItems().add(new Item(new IntPoint(0,2), new IntPoint(2,2)));

        final Box second = new Box(new IntPoint(4, 4));
        second.getItems().add(new Item(new IntPoint(0,2), new IntPoint(2,2)));
        second.getItems().add(new Item(new IntPoint(1,0), new IntPoint(2,1)));

        final List<Box> boxes = Arrays.asList(first, second);
        final List<Item> items = Arrays.asList(
                new Item(null, new IntPoint(1,1), "1"),
                new Item(null, new IntPoint(1,1), "2"),
                new Item(null, new IntPoint(1,1), "3"),
                new Item(null, new IntPoint(2,3), "4"),
                new Item(null, new IntPoint(3,3), "5"),
                new Item(null, new IntPoint(1,2), "6"),
                new Item(null, new IntPoint(2,1), "7"),
                new Item(null, new IntPoint(4,2), "8"),
                new Item(null, new IntPoint(1,3), "9"),
                new Item(null, new IntPoint(1,4), "a"),
                new Item(null, new IntPoint(1,4), "b")
        );

        final Knapsack.Result<Box,Item> result = new Knapsack<>(
                boxes,
                items,
                Item::getPosition,
                Item::getSize,
                Box::getItems,
                Box::getSize
        ).place();

        for (final Knapsack.Entry<Box, Item> entry : result.getPlaced()) {
            entry.getItem().position = entry.getPosition();
            entry.getBox().getItems().add(entry.getItem());
        }

        final StringBuilder actual = new StringBuilder();
        actual.append(first).append("\n")
                .append(second).append("\n");

        for (final Item skipped : result.getSkipped()) {
            actual.append(skipped);
        }

        final String expected = "[o][6][1][a]\n" +
                "[o][6][o][a]\n" +
                "[o][o][2][a]\n" +
                "[o][o][3][a]\n" +
                "\n" +
                "[ ][o][o][ ]\n" +
                "[7][7][4][4]\n" +
                "[o][o][4][4]\n" +
                "[o][o][4][4]\n" +
                "\n" +
                "[5][5][5]\n" +
                "[5][5][5]\n" +
                "[5][5][5]\n" +
                "[8][8]\n" +
                "[8][8]\n" +
                "[8][8]\n" +
                "[8][8]\n" +
                "[b][b][b][b]\n" +
                "[9][9][9]\n";

        Assert.assertEquals(expected, actual.toString());

    }

    private static final class Item {
        private IntPoint position;
        private IntPoint size;
        private String symbol;

        Item(final IntPoint position, final IntPoint size, final String symbol) {
            this.position = position;
            this.size = size;
            this.symbol = symbol;
        }

        Item(final IntPoint position, final IntPoint size) {
            this(position, size, "o");
        }

        IntPoint getPosition() {
            return position;
        }

        IntPoint getSize() {
            return size;
        }

        String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (int x = 0; x < getSize().getX(); x++) {
                for (int y = 0; y < getSize().getY(); y++) {
                    sb.append("[").append(getSymbol()).append("]");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private static final class Box {
        private IntPoint size;
        private List<Item> items = new ArrayList<>();

        Box(final IntPoint size) {
            this.size = size;
        }

        IntPoint getSize() {
            return size;
        }

        List<Item> getItems() {
            return items;
        }

        @Override
        public String toString() {
            final String[][] cells = new String[size.getX()][size.getY()];
            for (final Item item : items) {
                for (int x = 0; x < item.getSize().getX(); x++) {
                    for (int y = 0; y < item.getSize().getY(); y++) {
                        cells[item.getPosition().getY() + y][item.getPosition().getX() + x] = item.getSymbol();
                    }
                }
            }

            final StringBuilder sb = new StringBuilder();
            for (final String[] cell : cells) {
                for (final String s : cell) {
                    sb.append("[").append(s != null ? s : " ").append("]");
                }
                sb.append("\n");
            }

            return sb.toString();
        }
    }

}
