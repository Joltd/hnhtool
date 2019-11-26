package com.evgenltd.hnhtool.harvester.core;

import com.evgenltd.hnhtools.entity.IntPoint;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 25-11-2019 18:41
 */
public interface Agent {

    void move(final IntPoint position);

    void openContainer(final Long objectId);

}
