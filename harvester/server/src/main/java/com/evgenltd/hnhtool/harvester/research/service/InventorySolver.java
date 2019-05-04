package com.evgenltd.hnhtool.harvester.research.service;/**
 * Project: hnhtool-root
 * Author:  lebed
 * Created: 03-05-2019 12:05
 */

import com.evgenltd.hnhtool.harvester.common.entity.KnownItem;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 03-05-2019 12:05</p>
 */
public class InventorySolver {

    public InventorySolver(final KnownObject inventory, final List<KnownItem> items) {

    }

    public static final class Inventory {
        private IntPoint size;
        private List<Item> items = new ArrayList<>();

        public IntPoint getSize() {
            return size;
        }
        public void setSize(final IntPoint size) {
            this.size = size;
        }

        public List<Item> getItems() {
            return items;
        }
        public void setItems(final List<Item> items) {
            this.items = items;
        }
    }

    public static final class Item {
        private IntPoint position;
        private IntPoint size;

        public IntPoint getPosition() {
            return position;
        }
        public void setPosition(final IntPoint position) {
            this.position = position;
        }

        public IntPoint getSize() {
            return size;
        }
        public void setSize(final IntPoint size) {
            this.size = size;
        }
    }

}
