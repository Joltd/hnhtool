package com.evgenltd.hnhtools.clientapp;

import com.evgenltd.hnhtools.clientapp.impl.ClientAppImpl;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 07-11-2019 00:57</p>
 */
public class ClientAppFactory {

    public static ClientApp buildClientApp() {
        return new ClientAppImpl();
    }

}
