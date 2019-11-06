package com.evgenltd.hnhtools.messagebroker.impl;

import com.evgenltd.hnhtools.message.*;
import com.evgenltd.hnhtools.messagebroker.MessageBroker;
import com.evgenltd.hnhtools.messagebroker.MessageBrokerException;
import com.evgenltd.hnhtools.util.ByteUtil;
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

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-11-2019 22:06</p>
 */
public class MessageBrokerImpl implements MessageBroker {

    private static final Logger log = LogManager.getLogger(MessageBroker.class);

    private static final int PROTOCOL_VERSION = 17;
    private static final String BROKER_NAME = "Hafen";
    private static final int SOCKET_TIMEOUT = 1_000;
    private static final long BEAT_TIMEOUT = 5_000L;
    private static final int AUTH_TIMEOUT = 200;
    private static final int CLOSE_TIMEOUT = 200;
    private static final int LIFE_TIMEOUT = 100;

    // configurable
    private ObjectMapper objectMapper;
    private Consumer<InboundMessageAccessor.RelAccessor> relReceiver;
    private Consumer<InboundMessageAccessor.ObjectDataAccessor> objectDataReceiver;
    private SocketAddress server;
    private String username;
    private byte[] cookie;
    private Monitor monitor;

    // inner state
    private String messageBrokerName = "message-broker";
    private State state = State.INIT;

    private final ObjectDataHolder objectDataHolder;
    private final InboundRelHolder inboundRelHolder;
    private final OutboundRelHolder outboundRelHolder;

    private final RelFragmentAssembly relFragmentAssembly;

    private DatagramSocket socket;

    private final Thread inbound;
    private final Thread outbound;

    public MessageBrokerImpl() {
        this.objectMapper = new ObjectMapper();
        objectDataHolder = new ObjectDataHolder();
        inboundRelHolder = new InboundRelHolder();
        outboundRelHolder = new OutboundRelHolder();
        relFragmentAssembly = new RelFragmentAssembly();

        inbound = new Thread(this::inboundLoop);
        outbound = new Thread(this::outboundLoop);
    }

    @Override
    public void setObjectMapper(@NotNull final ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "[ObjectMapper] should not be empty");
        this.objectMapper = objectMapper;
    }

    @Override
    public void setServer(@NotNull final String host, final int port) {
        Objects.requireNonNull(host, "[Host] should not be empty");
        try {
            server = new InetSocketAddress(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            throw new MessageBrokerException("Wrong server host [%s]", e, host);
        }
    }

    @Override
    public void setCredentials(@NotNull final String username, @NotNull final byte[] cookie) {
        Objects.requireNonNull(username, "[Username] should not be empty");
        Objects.requireNonNull(cookie, "[Cookie] should not be empty");
        this.username = username;
        this.cookie = cookie;
        this.messageBrokerName = String.format("%s-%s", username, messageBrokerName);
        inbound.setName(String.format("%s-inbound", this.messageBrokerName));
        outbound.setName(String.format("%s-outbound", this.messageBrokerName));
    }

    @Override
    public void setRelReceiver(@NotNull final Consumer<InboundMessageAccessor.RelAccessor> relReceiver) {
        Objects.requireNonNull(relReceiver, "[RelReceiver] should not be empty");
        this.relReceiver = relReceiver;
    }

    @Override
    public void setObjectDataReceiver(@NotNull final Consumer<InboundMessageAccessor.ObjectDataAccessor> objectDataReceiver) {
        Objects.requireNonNull(objectDataReceiver, "[ObjectDataReceiver] should not be empty");
        this.objectDataReceiver = objectDataReceiver;
    }

    @Override
    public void withMonitoring() {
        monitor = new Monitor();
    }

    // ##################################################
    // #                                                #
    // #  Lifecycle                                     #
    // #                                                #
    // ##################################################

    @Override
    public void connect() {
        Objects.requireNonNull(relReceiver, "[RelReceiver] should not be empty");
        Objects.requireNonNull(objectDataReceiver, "[ObjectDataReceiver] should not be empty");
        Objects.requireNonNull(server, "[Server] should not be empty");
        Objects.requireNonNull(username, "[Username] should not be empty");
        Objects.requireNonNull(cookie, "[Cookie] should not be empty");

        if (!isInit()) {
            throw new MessageBrokerException("%s not in init state", this.messageBrokerName);
        }

        setState(State.CONNECTION);

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (final SocketException e) {
            throw new MessageBrokerException("%s failed on starting connection", e, this.messageBrokerName);
        }

        inbound.start();
        outbound.start();
    }

    @Override
    public void disconnect() {
        if (!isLife() && !isClosing()) {
            throw new MessageBrokerException("%s not in life state", this.messageBrokerName);
        }

        setState(State.CLOSING);
    }

    @Override
    public synchronized State getState() {
        return state;
    }

    private synchronized void setState(final State state) {
        this.state = state;
    }

    @Override
    public boolean isInit() {
        return getState().equals(State.INIT);
    }

    @Override
    public boolean isConnection() {
        return getState().equals(State.CONNECTION);
    }

    @Override
    public boolean isLife() {
        return getState().equals(State.LIFE);
    }

    @Override
    public boolean isClosing() {
        return getState().equals(State.CLOSING);
    }

    @Override
    public boolean isClosed() {
        return getState().equals(State.CLOSED);
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

            if (Thread.currentThread().isInterrupted() || isClosed()) {
                return;
            }

            if (isInit()) {
                continue;
            }

            final InboundMessageAccessor data = receive();

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

    private InboundMessageAccessor receive() {
        final DatagramPacket packet = new DatagramPacket(new byte[ByteUtil.WORD], ByteUtil.WORD);
        final ObjectNode rootNode = objectMapper.createObjectNode();
        final InboundMessageAccessor accessor = new InboundMessageAccessor(rootNode);
        try {
            socket.receive(packet);
            if (!packet.getSocketAddress().equals(server)) {
                return accessor;
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
            log.debug(String.format("%s unable to receive message", this.messageBrokerName), e);
        }

        return accessor;
    }

    private void authProcessor(final InboundMessageAccessor data) {
        if (!Objects.equals(data.getType(), MessageType.MESSAGE_TYPE_SESSION)) {
            return;
        }

        final InboundMessageAccessor.ConnectionErrorCode connectionErrorCode = data.getConnectionErrorCode();
        if (connectionErrorCode.equals(InboundMessageAccessor.ConnectionErrorCode.OK)) {
            setState(State.LIFE);
        } else {
            setState(State.CLOSED);
        }
    }

    private void baseInboundProcessor(final InboundMessageAccessor data) {
        final MessageType type = data.getType();
        if (type == null) {
            return;
        }

        switch (type) {
            case MESSAGE_TYPE_REL:
                for (InboundMessageAccessor.RelAccessor relAccessor : data.getRel()) {
                    final InboundMessageAccessor.RelAccessor actualRelAccessor = inboundRelHolder.register(relAccessor);
                    if (actualRelAccessor == null) {
                        continue;
                    }

                    receiveRel(relAccessor);
                    inboundRelHolder.getNearestAwaiting().forEach(this::receiveRel);
                }
                break;
            case MESSAGE_TYPE_OBJECT_DATA:
                for (InboundMessageAccessor.ObjectDataAccessor objectDataAccessor : data.getObjectData()) {
                    objectDataHolder.registerObjectData(objectDataAccessor);
                    objectDataReceiver.accept(objectDataAccessor);
                }
                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                outboundRelHolder.acknowledge(data.getRelAcknowledge());
                break;
        }
    }

    private void receiveRel(final InboundMessageAccessor.RelAccessor relAccessor) {
        final RelType relType = relAccessor.getRelType();
        if (!Objects.equals(relType, RelType.REL_MESSAGE_FRAGMENT)) {
            relReceiver.accept(relAccessor);
            return;
        }

        final boolean result = relFragmentAssembly.append(relAccessor);
        if (result) {
            final InboundMessageAccessor.RelAccessor relFragmentComposition = new InboundMessageAccessor.RelAccessor(objectMapper.createObjectNode());
            final byte[] data = relFragmentAssembly.convert(relFragmentComposition);
            relReceiver.accept(relFragmentComposition);
            if (monitor != null) {
                monitor.sendInbound(data, data.length);
            }
        }
    }

    private void closingProcessor(final InboundMessageAccessor data) {
        if (Objects.equals(data.getType(), MessageType.MESSAGE_TYPE_CLOSE)) {
            shutdown();
        }
    }

    private void shutdown() {
        setState(State.CLOSED);
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
            skipBeat = skipBeat || objectDataAcknowledgeProcessor();
            skipBeat = skipBeat || relAcknowledgeProcessor();

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
        return outboundRelHolder.getNextAwaiting()
                .stream()
                .map(RelRequest::toWriter)
                .peek(this::send)
                .count() > 0;
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
