package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtools.common.ApplicationException;
import com.evgenltd.hnhtools.message.DataReader;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:50</p>
 */
public class ResourceReader {

    private static final byte[] signature = "Haven Resource 1".getBytes(Charset.forName("US-ASCII"));

    public void read(final String name, final DataReader reader) {
        final byte[] resourceSignature = reader.bytes(signature.length);
        if (!Arrays.equals(signature, resourceSignature)) {
            throw new ApplicationException("Blob is not a resource");
        }

        final int version = reader.uint16();

        final ResourceData resourceData = new ResourceData(name, version);

        while (reader.hasNext()) {
            final Layer layer = LayerFactory.build(reader);
            resourceData.addLayer(layer);
        }
    }

}
