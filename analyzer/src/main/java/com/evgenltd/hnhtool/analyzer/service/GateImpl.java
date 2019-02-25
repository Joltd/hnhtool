package com.evgenltd.hnhtool.analyzer.service;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtools.message.InboundMessageConverter;
import com.evgenltd.hnhtools.message.OutboundMessageConverter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.magenta.hnhtool.gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 16:46</p>
 */
public class GateImpl extends UnicastRemoteObject implements Lifecycle, Gate {

    private static final Logger log = LogManager.getLogger(GateImpl.class);

    private InboundMessageConverter inboundMessageConverter;
    private OutboundMessageConverter outboundMessageConverter;

    private boolean enabled;

    public GateImpl() throws RemoteException {}

    @Override
    public void init() {
        inboundMessageConverter = new InboundMessageConverter();
        outboundMessageConverter = new OutboundMessageConverter();
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
    public void inbound(final byte[] data, final int length) throws RemoteException {
        if (!isEnabled()) {
            return;
        }

        final byte[] truncated = new byte[length];
        System.arraycopy(data, 0, truncated, 0, length);

        ObjectNode node;
        try {
            node = inboundMessageConverter.convert(truncated);
        } catch (Exception e) {
            log.error("", e);
            node = printStackTrace(e);
        }
        C.getMainModel().addInboundMessage(node, convertByteData(truncated));
    }

    @Override
    public void outbound(final byte[] data, final int length) throws RemoteException {
        if (!isEnabled()) {
            return;
        }
        ObjectNode node;
        try {
            node = outboundMessageConverter.convert(data);
        } catch (Exception e) {
            log.error("", e);
            node = printStackTrace(e);
        }
        C.getMainModel().addOutboundMessage(node, convertByteData(data));
    }

    private List<String> convertByteData(final byte[] data) {
        final List<String> result = new ArrayList<>();
        for (final byte b : data) {
            result.add(String.format("%02X", b));
        }
        return result;
    }

    private ObjectNode printStackTrace(final Throwable throwable) {
        final StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString();
        final ObjectNode errorNode = C.getMapper().createObjectNode();
        errorNode.put("analyzer_exception", stackTrace);
        return errorNode;
    }

}
