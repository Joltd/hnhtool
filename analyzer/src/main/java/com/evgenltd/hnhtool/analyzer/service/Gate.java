package com.evgenltd.hnhtool.analyzer.service;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtools.message.UdpToJson;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 16:46</p>
 */
public class Gate implements Lifecycle {

    private static final Logger log = LogManager.getLogger(Gate.class);

    private DatagramSocket inbound;
    private Thread inboundThread;
    private DatagramSocket outbound;
    private Thread outboundThread;

    private UdpToJson udpToJson;

    @Override
    public void init() {
        try {
            udpToJson = new UdpToJson();
            inbound = new DatagramSocket(new InetSocketAddress("127.0.0.1", 17777));
            inboundThread = new Thread(this::listenInbound);
            inboundThread.setDaemon(true);
            inboundThread.start();
            outbound = new DatagramSocket(new InetSocketAddress("127.0.0.1", 17778));
            outboundThread = new Thread(this::listenOutbound);
            outboundThread.setDaemon(true);
            outboundThread.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (!inbound.isClosed()) {
            inbound.close();
        }
        if (!inboundThread.isInterrupted()) {
            inboundThread.interrupt();
        }
        if (!outbound.isClosed()) {
            outbound.close();
        }
        if (!outboundThread.isInterrupted()) {
            outboundThread.interrupt();
        }
    }

    private void listenInbound() {
        while (true) {
            if (inbound.isClosed() || Thread.currentThread().isInterrupted()) {
                return;
            }

            final DatagramPacket packet = new DatagramPacket(new byte[65536], 65536);
            try {
                inbound.receive(packet);
            } catch (final IOException e) {
                log.error(e);
            }

            if (!packet.getAddress().getHostName().equals("127.0.0.1")) {
                continue;
            }

            final ObjectNode node = udpToJson.convert(packet.getData());
            C.getMainScreenModel().addInboundMessage(node);
        }
    }

    private void listenOutbound() {
        while (true) {
            if (outbound.isClosed() || Thread.currentThread().isInterrupted()) {
                return;
            }

            final DatagramPacket packet = new DatagramPacket(new byte[65536], 65536);
            try {
                outbound.receive(packet);
            } catch (final IOException e) {
                log.error(e);
            }

            if (!packet.getAddress().getHostName().equals("127.0.0.1")) {
                continue;
            }

            final ObjectNode node = udpToJson.convert(packet.getData());
            C.getMainScreenModel().addOutboundMessage(node);
        }
    }

}
