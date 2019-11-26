package com.evgenltd.hnhtool.harvester.core.component;

import com.evgenltd.hnhtools.clientapp.ClientApp;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 02:31</p>
 */
public class OpenContainer implements Intention {

    public OpenContainer(final Long objectId) {

    }

    @Override
    public void doIntention(final ClientApp clientApp) {
        clientApp.click();
    }

    @Override
    public boolean condition() {
        return false;
    }

    @Override
    public void scan() {

    }
}
