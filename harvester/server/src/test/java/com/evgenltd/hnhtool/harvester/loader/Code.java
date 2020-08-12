package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtools.message.DataReader;

public final class Code extends AbstractLayer implements Layer {

    private final String name;
    private final byte[] data;

    public Code(final Resource resource, final DataReader reader) {
        super(resource);
        name = reader.string();
        data = reader.bytes();
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void initialization() {}

}
