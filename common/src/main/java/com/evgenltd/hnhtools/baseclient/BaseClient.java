package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.common.ApplicationException;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-03-2019 21:48</p>
 */
public class BaseClient {

    private State state;

    private DatagramSocket socket;

    public BaseClient() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new ApplicationException(e);
        }
    }

    private enum State {

    }

}
