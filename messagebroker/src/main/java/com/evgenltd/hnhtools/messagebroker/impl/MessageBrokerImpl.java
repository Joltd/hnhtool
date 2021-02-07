package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.message.DataWriter;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.evgenltd.hnhtools.messagebroker.MessageBrokerException;
import com.evgenltd.hnhtools.messagebroker.RelType;
import com.evgenltd.hnhtools.messagebroker.impl.message.InboundMessageConverter;
import com.evgenltd.hnhtools.messagebroker.impl.message.Message;
import com.evgenltd.hnhtools.messagebroker.impl.message.MessageType;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.magenta.hnhtool.gate.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class MessageBrokerImpl implements MessageBroker {

    private static final Logger log = LogManager.getLogger(MessageBroker.class);

    private static final int PROTOCOL_VERSION = 24;
    private static final String BROKER_NAME = "Hafen";
    private static final int SOCKET_TIMEOUT = 1_000;
    private static final long BEAT_TIMEOUT = 5_000L;
    private static final int AUTH_TIMEOUT = 200;
    private static final int CLOSE_TIMEOUT = 200;
    private static final int LIFE_TIMEOUT = 100;

    // configurable
    private final ObjectMapper objectMapper;
    private final Consumer<JsonNode> relReceiver;
    private final Consumer<JsonNode> objectDataReceiver;
    private final String host;
    private final int port;
    private String username;
    private byte[] cookie;

    // inner state
    private Monitor monitor;

    private Status status = Status.INIT;

    private final ObjectDataHolder objectDataHolder;
    private final InboundRelHolder inboundRelHolder;
    private final OutboundRelHolder outboundRelHolder;
    private final RelFragmentAssembly relFragmentAssembly;

    private final Thread inbound;
    private final Thread outbound;

    private SocketAddress server;
    private DatagramSocket socket;

    public MessageBrokerImpl(
            @NotNull final ObjectMapper objectMapper,
            @NotNull final String host,
            final int port,
            @NotNull final Consumer<JsonNode> relReceiver,
            @NotNull final Consumer<JsonNode> objectDataReceiver,
            boolean withMonitoring
    ) {
        Objects.requireNonNull(objectMapper, "[ObjectMapper] should not be empty");
        Objects.requireNonNull(host, "[Host] should not be empty");
        Objects.requireNonNull(relReceiver, "[RelReceiver] should not be empty");
        Objects.requireNonNull(objectDataReceiver, "[ObjectDataReceiver] should not be empty");

        this.objectMapper = new ObjectMapper();
        this.host = host;
        this.port = port;
        this.relReceiver = relReceiver;
        this.objectDataReceiver = objectDataReceiver;

        objectDataHolder = new ObjectDataHolder();
        inboundRelHolder = new InboundRelHolder();
        outboundRelHolder = new OutboundRelHolder();
        relFragmentAssembly = new RelFragmentAssembly();

        inbound = new Thread(this::inboundLoop);
        outbound = new Thread(this::outboundLoop);

        if (withMonitoring) {
            monitor = new Monitor();
        }
    }
    
    private String getMessageBrokerName() {
        return String.format("message-broker-%s", username);
    }

    // ##################################################
    // #                                                #
    // #  Lifecycle                                     #
    // #                                                #
    // ##################################################

    @Override
    public void connect(final String username, final byte[] cookie) {
        if (!isInit()) {
            throw new MessageBrokerException("%s not in init state", getMessageBrokerName());
        }

        this.username = username;
        this.cookie = cookie;
        setStatus(Status.CONNECTION);

        try {
            server = new InetSocketAddress(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            throw new MessageBrokerException("Wrong server host [%s]", e, host);
        }

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (final SocketException e) {
            throw new MessageBrokerException("%s failed on starting connection", e, getMessageBrokerName());
        }

        inbound.setName(String.format("%s-inbound", getMessageBrokerName()));
        outbound.setName(String.format("%s-outbound", getMessageBrokerName()));
        inbound.start();
        outbound.start();
    }

    @Override
    public void disconnect() {
        if (!isLife() && !isClosing()) {
            throw new MessageBrokerException("%s not in life state", getMessageBrokerName());
        }

        setStatus(Status.CLOSING);
    }

    @Override
    public synchronized State getState() {
        final SocketState socketState = socket != null
                ? new SocketState(
                        socket.isConnected(),
                        socket.isClosed(),
                        socket.isBound()
                )
                : null;
        return new State(
                username,
                getStatus(),
                inbound.getState(),
                outbound.getState(),
                socketState
        );
    }

    @Override
    public synchronized Status getStatus() {
        return status;
    }

    private synchronized void setStatus(final Status status) {
        this.status = status;
    }

    @Override
    public boolean isInit() {
        return getStatus().equals(Status.INIT);
    }

    @Override
    public boolean isConnection() {
        return getStatus().equals(Status.CONNECTION);
    }

    @Override
    public boolean isLife() {
        return getStatus().equals(Status.LIFE);
    }

    @Override
    public boolean isClosing() {
        return getStatus().equals(Status.CLOSING);
    }

    @Override
    public boolean isClosed() {
        return getStatus().equals(Status.CLOSED);
    }

    // ##################################################
    // #                                                #
    // #  Exchange                                      #
    // #                                                #
    // ##################################################

    @Override
    public void sendRel(final int id, final String name, final Object... args) {
        outboundRelHolder.register(id, name, args);
    }

    // ##################################################
    // #                                                #
    // #  Inbound processing                            #
    // #                                                #
    // ##################################################

    private void inboundLoop() {
        while (true) {

            if (Thread.currentThread().isInterrupted()) {
                shutdown();
                return;
            }

            if (isClosed()) {
                return;
            }

            if (isInit()) {
                continue;
            }

            final Message data = receive();

            if (isConnection()) {
                authProcessor(data);
            } else if (isLife()) {
                baseInboundProcessor(data);
                closingProcessor(data);
            } else if (isClosing()) {
                shutdown();
            }

        }
    }

    private Message receive() {
        final DatagramPacket packet = new DatagramPacket(new byte[ByteUtil.WORD], ByteUtil.WORD);
        final ObjectNode rootNode = objectMapper.createObjectNode();
        final Message message = new Message(rootNode);
        try {
            socket.receive(packet);
            if (!packet.getSocketAddress().equals(server)) {
                return message;
            }

            final byte[] data = packet.getData();
            final int length = packet.getLength();
            if (monitor != null) {
                monitor.sendInbound(data, length);
            }

            final byte[] truncatedDate = new byte[length];
            System.arraycopy(data, 0, truncatedDate, 0, length);
            InboundMessageConverter.convert(rootNode, truncatedDate);
        } catch (final SocketTimeoutException ignored) {
        } catch (final Exception e) {
            log.debug(String.format("%s unable to receive message", getMessageBrokerName()), e);
        }

        return message;
    }

    private void authProcessor(final Message message) {
        if (!message.isSessionMessage()) {
            return;
        }

        if (message.isConnectionErrorCodeOk()) {
            setStatus(Status.LIFE);
        } else {
            setStatus(Status.CLOSED);
        }
    }

    private void baseInboundProcessor(final Message message) {
        final MessageType type = message.getType();
        if (type == null) {
            return;
        }

        switch (type) {
            case MESSAGE_TYPE_REL:
                for (final Message.Rel rel : message.getRel()) {
                    final Message.Rel actualRel = inboundRelHolder.register(rel);
                    if (actualRel == null) {
                        continue;
                    }

                    receiveRel(rel);
                    inboundRelHolder.getNearestAwaiting().forEach(this::receiveRel);
                }
                break;
            case MESSAGE_TYPE_OBJECT_DATA:
                for (final Message.ObjectData objectData : message.getObjectData()) {
                    objectDataHolder.registerObjectData(objectData);
                    objectDataReceiver.accept(objectData.getData());
                }
                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                outboundRelHolder.acknowledge(message.getRelAcknowledge());
                break;
        }
    }

    private void receiveRel(final Message.Rel rel) {
        final RelType relType = rel.getRelType();
        if (!Objects.equals(relType, RelType.REL_MESSAGE_FRAGMENT)) {
            relReceiver.accept(rel.getData());
            return;
        }

        final boolean result = relFragmentAssembly.append(rel);
        if (result) {
            final ObjectNode relFragmentComposition = objectMapper.createObjectNode();
            final byte[] data = relFragmentAssembly.convert(relFragmentComposition);
            relReceiver.accept(relFragmentComposition);
            if (monitor != null) {
                monitor.sendInbound(data, data.length);
            }
        }
    }

    private void closingProcessor(final Message message) {
        if (Objects.equals(message.getType(), MessageType.MESSAGE_TYPE_CLOSE)) {
            shutdown();
        }
    }

    private void shutdown() {
        setStatus(Status.CLOSED);
        socket.close();
    }

    // ##################################################
    // #                                                #
    // #  Outbound processing                           #
    // #                                                #
    // ##################################################

    private void outboundLoop() {
        long previousTime = 0;
        while (true) {

            if (Thread.currentThread().isInterrupted() || isClosed()) {
                return;
            }

            if (isInit()) {
                continue;
            }

            if (isConnection()) {
                doAuth();
                continue;
            }

            if (isClosing()) {
                doClose();
                continue;
            }

            sleep(LIFE_TIMEOUT);

            long now = System.currentTimeMillis();
            long timeElapsed = now - previousTime;
            boolean skipBeat = relProcessor();
            skipBeat = objectDataAcknowledgeProcessor() || skipBeat;
            skipBeat = relAcknowledgeProcessor() || skipBeat;

            if (!skipBeat && timeElapsed > BEAT_TIMEOUT) {
                final DataWriter beat = new DataWriter();
                beat.adduint8(MessageType.MESSAGE_TYPE_BEAT.getValue());
                send(beat);
                previousTime = now;
            }

        }
    }

    private void doAuth() {
        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_SESSION.getValue());
        writer.adduint16(2);
        writer.addString(BROKER_NAME);
        writer.adduint16(PROTOCOL_VERSION);
        writer.addString(username);
        writer.adduint16(cookie.length);
        writer.addbytes(cookie);
        send(writer);
        sleep(AUTH_TIMEOUT);
    }

    private void doClose() {
        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_CLOSE.getValue());
        send(writer);
        sleep(CLOSE_TIMEOUT);
    }

    private boolean relProcessor() {
        final List<RelRequest> nextAwaiting = outboundRelHolder.getNextAwaiting();
        for (final RelRequest relRequest : nextAwaiting) {
            send(relRequest.toWriter());
        }
        return !nextAwaiting.isEmpty();
    }

    private boolean objectDataAcknowledgeProcessor() {
        final List<ObjectDataHolder.ObjectDataEntry> objectDataForAcknowledge = objectDataHolder.getObjectDataForAcknowledge();
        if (objectDataForAcknowledge.isEmpty()) {
            return false;
        }

        final int batchSize = 125;
        final int objectDataSize = objectDataForAcknowledge.size();
        for (int index = 0; index < objectDataSize; index = index + batchSize) {
            final List<ObjectDataHolder.ObjectDataEntry> batch = objectDataForAcknowledge.subList(
                    index,
                    Math.min(index + batchSize, objectDataSize)
            );

            final DataWriter writer = new DataWriter();
            writer.adduint8(MessageType.MESSAGE_TYPE_OBJECT_ACKNOWLEDGE.getValue());
            for (ObjectDataHolder.ObjectDataEntry entry : batch) {
                writer.adduint32(entry.getId());
                writer.addint32(entry.getFrame());
            }
            send(writer);
        }
        return true;
    }

    private boolean relAcknowledgeProcessor() {
        final Integer relSequenceForAcknowledge = inboundRelHolder.getAcknowledgeSequence();
        if (relSequenceForAcknowledge == null) {
            return false;
        }

        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_ACKNOWLEDGE.getValue());
        writer.adduint16(relSequenceForAcknowledge);
        send(writer);
        return true;
    }

    private void send(final DataWriter data) {
        try {
            final byte[] byteData = data.bytes();
            if (monitor != null) {
                monitor.sendOutbound(byteData);
            }

            final DatagramPacket packet = new DatagramPacket(byteData, byteData.length, server);
            socket.send(packet);
        } catch (final IOException e) {
            log.debug("Unable to send message", e);
        }
    }

    private void sleep(final long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ignored) {}
    }

}
