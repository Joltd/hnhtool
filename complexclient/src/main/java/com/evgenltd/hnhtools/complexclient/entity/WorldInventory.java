package com.evgenltd.hnhtools.complexclient.entity;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 21-04-2019 14:38</p>
 */
public interface WorldInventory {

    Integer getId();

    Number getParentId();

    default boolean isObjectParentId() {
        return getParentId() instanceof Long;
    }

    default Long getObjectParentId() {
        final Number parentId = getParentId();
        if (isObjectParentId()) {
            return parentId.longValue();
        }
        throw new ApplicationException("Inventory parent is not an object, parentId=[%s], parentIdClass=[%s]", parentId, parentId.getClass());
    }

    default boolean isItemParentId() {
        return getParentId() instanceof Integer;
    }

    default Integer getItemParentId() {
        final Number parentId = getParentId();
        if (parentId instanceof Long) {
            return parentId.intValue();
        }
        throw new ApplicationException("Inventory parent is not an item, parentId=[%s], parentIdClass=[%s]", parentId, parentId.getClass());
    }

    IntPoint getSize();

    List<WorldItem> getItems();

}
