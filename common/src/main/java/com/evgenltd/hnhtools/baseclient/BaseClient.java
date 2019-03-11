package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.DataWriter;
import com.evgenltd.hnhtools.message.InboundMessageAccessor;
import com.evgenltd.hnhtools.message.InboundMessageConverter;
import com.evgenltd.hnhtools.message.MessageType;
import com.evgenltd.hnhtools.util.ByteUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-03-2019 21:48</p>
 */
public final class BaseClient {

    private static final int PROTOCOL_VERSION = 17;
    private static final int SOCKET_TIMEOUT = 1_000;
    private static final long BEAT_TIMEOUT = 5_000L;

    private static final Logger log = LogManager.getLogger(BaseClient.class);

    private State state = State.INIT;

    private final ObjectMapper objectMapper;

    @UsedInInboundThread
    @UsedInOutboundThread
    private final StateHolder stateHolder;
    private final InboundMessageConverter inboundConverter;

    private final DatagramSocket socket;

    private final Thread inbound;
    private final Thread outbound;

    private String username;
    private byte[] cookie;

    public BaseClient(final ObjectMapper objectMapper) {
        try {
            this.objectMapper = objectMapper;
            stateHolder = new StateHolder();
            inboundConverter = new InboundMessageConverter();

            socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT);
            inbound = new Thread(this::inboundLoop);
            outbound = new Thread(this::outboundLoop);
        } catch (final Exception e) {
            throw new ApplicationException(e);
        }
    }

    // ##################################################
    // #                                                #
    // #  API                                           #
    // #                                                #
    // ##################################################

    private boolean isAlive() {
        return Arrays.asList(
                State.CONNECTION,
                State.LIFE,
                State.CLOSING
        ).contains(state);
    }

    private boolean isInit() {
        return state.equals(State.INIT);
    }

    private boolean isConnection() {
        return state.equals(State.CONNECTION);
    }

    private boolean isLife() {
        return state.equals(State.LIFE);
    }

    private boolean isClosing() {
        return state.equals(State.CLOSING);
    }

    private boolean isClosed() {
        return state.equals(State.CLOSED);
    }

    public void setCredentials(final String username, final byte[] cookie) {
        this.username = username;
        this.cookie = cookie;
        inbound.setName(String.format("baseClient-%s-inbound", username));
        outbound.setName(String.format("baseClient-%s-outbound", username));
    }

    public void connect() {
        if (!state.equals(State.INIT)) {
            return; // needed light error response
        }

        state = State.CONNECTION;

        final DataWriter writer = new DataWriter();
        writer.adduint16(2);
        writer.addString("Hafen");
        writer.adduint16(PROTOCOL_VERSION);
        writer.addString(username);
        writer.adduint16(cookie.length);
        writer.addbytes(cookie);
        send(writer);

        inbound.start();
        outbound.start();
    }

    public void disconnect() {
        if (!isLife() && !isClosing()) {
            return; // needed light error response
        }
        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_CLOSE.getValue());
        send(writer);
        state = State.CLOSING;
    }

    private void pushRel(final InboundMessageAccessor.RelAccessor relAccessor) {

    }

    private void pushObjectData(final InboundMessageAccessor.ObjectDataAccessor objectDataAccessor) {

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
            } else if (isClosing()) {
                closingProcessor(data);
            }

        }
    }

    private InboundMessageAccessor receive() {
        final DatagramPacket packet = new DatagramPacket(new byte[ByteUtil.WORD], ByteUtil.WORD);
        final ObjectNode rootNode = objectMapper.createObjectNode();
        final InboundMessageAccessor accessor = new InboundMessageAccessor(rootNode);
        try {
            socket.receive(packet);
            final byte[] data = packet.getData();
            final int length = packet.getLength();
            final byte[] truncatedDate = new byte[length];
            System.arraycopy(data, 0, truncatedDate, 0, length);
            inboundConverter.convert(rootNode, truncatedDate);
        } catch (final Exception e) {
            log.debug("Unable to receive message", e);
        }

        return accessor;
    }

    private void authProcessor(final InboundMessageAccessor data) {
        if (!Objects.equals(data.getType(), MessageType.MESSAGE_TYPE_SESSION)) {
            return;
        }

        final ConnectionErrorCode connectionErrorCode = data.getConnectionErrorCode();
        if (connectionErrorCode.equals(ConnectionErrorCode.OK)) {
            state = State.LIFE;
        } else {
            state = State.CLOSED;
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
                    final InboundMessageAccessor.RelAccessor actualRelAccessor = stateHolder.registerRel(relAccessor);
                    if (actualRelAccessor == null) {
                        continue;
                    }

                    pushRel(relAccessor);
                    stateHolder.getNearestAwaitingRel().forEach(this::pushRel);
                }
                break;
            case MESSAGE_TYPE_OBJECT_DATA:
                for (InboundMessageAccessor.ObjectDataAccessor objectDataAccessor : data.getObjectData()) {
                    stateHolder.registerObjectData(objectDataAccessor);
                    pushObjectData(objectDataAccessor);
                }
                break;
            case MESSAGE_TYPE_ACKNOWLEDGE:
                break;
        }
    }

    private void closingProcessor(final InboundMessageAccessor data) {
        if (Objects.equals(data.getType(), MessageType.MESSAGE_TYPE_CLOSE)) {
            state = State.CLOSED;
        }
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

            long now = System.currentTimeMillis();
            long timeElapsed = now - previousTime;
            boolean sendBeat;
            // process outbound rel
            sendBeat = processObjectDataAcknowledge();
            sendBeat = sendBeat || processRelAcknowledge();

            if (sendBeat && timeElapsed > BEAT_TIMEOUT) {
                final DataWriter beat = new DataWriter();
                beat.adduint8(MessageType.MESSAGE_TYPE_BEAT.getValue());
                send(beat);
            }

        }
    }

    private boolean processRelAcknowledge() {
        final Integer relSequenceForAcknowledge = stateHolder.getRelSequenceForAcknowledge();
        if (relSequenceForAcknowledge == null) {
            return false;
        }

        final DataWriter writer = new DataWriter();
        writer.adduint8(MessageType.MESSAGE_TYPE_ACKNOWLEDGE.getValue());
        writer.adduint16(relSequenceForAcknowledge);
        send(writer);
        return true;
    }

    private boolean processObjectDataAcknowledge() {
        final List<StateHolder.ObjectDataEntry> objectDataForAcknowledge = stateHolder.getObjectDataForAcknowledge();
        if (objectDataForAcknowledge.isEmpty()) {
            return false;
        }

        final int batchSize = 125;
        final int objectDataSize = objectDataForAcknowledge.size();
        for (int index = 0; index < objectDataSize; index = index + batchSize) {
            final List<StateHolder.ObjectDataEntry> batch = objectDataForAcknowledge.subList(
                    index,
                    Math.min(index + batchSize, objectDataSize)
            );

            final DataWriter writer = new DataWriter();
            writer.adduint8(MessageType.MESSAGE_TYPE_OBJECT_ACKNOWLEDGE.getValue());
            for (StateHolder.ObjectDataEntry entry : batch) {
                writer.adduint32(entry.getId());
                writer.addint32(entry.getFrame());
            }
            send(writer);
        }
        return true;
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

    private enum State {
        INIT,
        CONNECTION,
        LIFE,
        CLOSING,
        CLOSED;
    }

}
