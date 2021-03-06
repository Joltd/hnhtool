package com.evgenltd.hnhtool.analyzer.service;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.Constants;
import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.messagebroker.impl.message.InboundMessageConverter;
import com.evgenltd.hnhtools.messagebroker.impl.message.OutboundMessageConverter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.magenta.hnhtool.gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GateImpl extends UnicastRemoteObject implements Lifecycle, Gate {

    private static final Logger log = LogManager.getLogger(GateImpl.class);

    private MessageFilter messageFilter;

    private boolean enabled;

    public GateImpl() throws RemoteException {}

    @Override
    public void init() {
        messageFilter = new MessageFilter();
    }

    @Override
    public void stop() {}

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void inbound(final byte[] data, final int length) {
        if (!isEnabled()) {
            return;
        }

        final byte[] truncated = new byte[length];
        System.arraycopy(data, 0, truncated, 0, length);

        final ObjectNode root = C.getMapper().createObjectNode();
        try {
            InboundMessageConverter.convert(root, truncated);
        } catch (Exception e) {
            log.error("", e);
            printStackTrace(root, e);
            storeErrorPacket("INBOUND", truncated);
        }
        C.getMainModel().addInboundMessage(root, convertByteData(truncated), messageFilter.hideInbound(root));
        C.getStandHandler().handle(root);
    }

    @Override
    public void outbound(final byte[] data, final int length) {
        if (!isEnabled()) {
            return;
        }
        final ObjectNode root = C.getMapper().createObjectNode();
        try {
            OutboundMessageConverter.convert(root, data);
        } catch (Exception e) {
            log.error("", e);
            printStackTrace(root, e);
            storeErrorPacket("OUTBOUND", data);
        }
        C.getMainModel().addOutboundMessage(root, convertByteData(data), messageFilter.hideOutbound(root));
    }

    private List<Byte> convertByteData(final byte[] data) {
        final List<Byte> result = new ArrayList<>();
        for (final byte b : data) {
            result.add(b);
        }
        return result;
    }

    private void printStackTrace(
            final ObjectNode node,
            final Throwable throwable
    ) {
        final StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString();
        node.put(Constants.ANALYZER_EXCEPTION_TOKEN, stackTrace);
    }

    private void storeErrorPacket(final String type, final byte[] data) {
        final String name = String.format("%s - %s - %s.data", System.currentTimeMillis(), type, data.length);
        try (final FileOutputStream stream = new FileOutputStream(name)) {
            stream.write(data);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}
