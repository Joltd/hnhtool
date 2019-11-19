package com.evgenltd.hnhtools.clientapp.impl;

import com.evgenltd.hnhtools.message.Message;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 19-11-2019 00:28</p>
 */
final class WorldObjectState {

    private ResourceState resourceState;

    WorldObjectState(final ResourceState resourceState) {
        this.resourceState = resourceState;
    }

    void receiveObjectData(final Message.ObjectData objectData) {

    }

}
