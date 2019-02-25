package com.magenta.hnhtool.gate;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 25-02-2019 22:03</p>
 */
public interface Gate extends Remote {

    void inbound(byte[] data, int length) throws RemoteException;

    void outbound(byte[] data, int length) throws RemoteException;

}
