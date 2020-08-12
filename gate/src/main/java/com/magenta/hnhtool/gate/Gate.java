package com.magenta.hnhtool.gate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Gate extends Remote {

    void inbound(byte[] data, int length) throws RemoteException;

    void outbound(byte[] data, int length) throws RemoteException;

}
