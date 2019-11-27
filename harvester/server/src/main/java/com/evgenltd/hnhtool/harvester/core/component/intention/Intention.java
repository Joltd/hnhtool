package com.evgenltd.hnhtool.harvester.core.component.intention;

import com.evgenltd.hnhtools.clientapp.ClientApp;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 26-11-2019 02:32</p>
 */
public interface Intention {

    void doIntention(ClientApp clientApp);

    boolean condition();

    void scan();
}
