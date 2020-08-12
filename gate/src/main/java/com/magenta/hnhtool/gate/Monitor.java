package com.magenta.hnhtool.gate;

import java.rmi.registry.LocateRegistry;

public class Monitor {

    private static final long MAX_TIMEOUT = 2000L;

    private Gate gate;
    private long timeout = 20;
    private long lastAttempt = 0;

    public void sendInbound(final byte[] data, final int length) {
        lookup();
        try {
            if (gate != null) {
                gate.inbound(data, length);
            }
        } catch (Exception e) {
            gate = null;
        }
    }

    public void sendOutbound(final byte[] data) {
        lookup();
        try {
            if (gate != null) {
                gate.outbound(data, data.length);
            }
        } catch (Exception e) {
            gate = null;
        }
    }

    private void lookup() {
        if (gate != null) {
            return;
        }

        final long now = System.currentTimeMillis();
        if (now - lastAttempt < timeout) {
            return;
        }

        try {
            gate = (Gate) LocateRegistry.getRegistry(7777)
                    .lookup(Gate.class.getSimpleName());
            timeout = 20;
        } catch (Exception e) {
            lastAttempt = now;
            timeout = Math.min(timeout * 2, MAX_TIMEOUT);
        }

    }

}
