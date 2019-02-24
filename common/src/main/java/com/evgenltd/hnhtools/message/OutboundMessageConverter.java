package com.evgenltd.hnhtools.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 23:25</p>
 */
public class OutboundMessageConverter {

    public ObjectNode convert(final byte[] data) {

        final ObjectMapper mapper = new ObjectMapper();

        final ObjectNode root = mapper.createObjectNode();

        return root;

    }

}
