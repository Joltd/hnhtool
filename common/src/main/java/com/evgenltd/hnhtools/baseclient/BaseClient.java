package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.DataReader;
import com.evgenltd.hnhtools.message.DataWriter;
import com.evgenltd.hnhtools.util.ByteUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-03-2019 21:48</p>
 */
public final class BaseClient {

    private static final int SOCKET_TIMEOUT = 1_000;

    private static final Logger log = LogManager.getLogger(BaseClient.class);

    private State state = State.INIT;

    private DatagramSocket socket;

    private Thread inbound;
    private Thread outbound;

    public BaseClient() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT);
            inbound = new Thread(this::inboundLoop);
            inbound.start();
            outbound = new Thread(this::outboundLoop);
            outbound.start();
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    // ##################################################
    // #                                                #
    // #  Lifecycle                                     #
    // #                                                #
    // ##################################################

    public void connect() {
        if (!state.equals(State.INIT)) {
            throw new ApplicationException("Incorrect client state, [%s]", state);
        }

        state = State.CONNECTION;
    }

    public void disconnect() {

    }

    private void inboundLoop() {
        while (true) {

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            final DataReader reader = receive();
            if (reader == null) {
                continue;
            }

            // base processing
            // if any acknowledge

        }
    }

    private void outboundLoop() {
        while (true) {

            if (Thread.currentThread().isInterrupted()) {
                return;
            }



        }
    }

    private void send(final DataWriter data) {
        try {
            final byte[] byteData = data.bytes();
            final DatagramPacket packet = new DatagramPacket(byteData, byteData.length);
            socket.send(packet);
        } catch (final IOException e) {
            log.debug("Unable to send message", e);
        }
    }

    private DataReader receive() {
        final DatagramPacket packet = new DatagramPacket(new byte[ByteUtil.WORD], ByteUtil.WORD);
        try {
            socket.receive(packet);
            return new DataReader(packet.getData());
        } catch (final Exception e) {
            log.debug("Unable to receive message", e);
            return null;
        }
    }

    private enum State {
        INIT,
        CONNECTION,
        LIFE,
        DEAD,
        CLOSED;
    }

}
