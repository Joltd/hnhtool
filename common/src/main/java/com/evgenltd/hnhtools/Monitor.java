package com.evgenltd.hnhtools;

import com.evgenltd.hnhtools.message.UdpToJson;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 15:37</p>
 */
public class Monitor {

    private static final Logger inbound = LogManager.getLogger("inbound");
    private static final Logger outbound = LogManager.getLogger("outbound");

    public static void logInboud(final byte[] data) {
        try {
            final UdpToJson udpToJson = new UdpToJson();
            final ObjectNode body = udpToJson.convert(data);
            inbound.info(body.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
