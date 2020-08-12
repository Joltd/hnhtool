package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;

public class AbstractLayer {

    private final Resource resource;

    public AbstractLayer(final Resource resource) {
        this.resource = resource;
    }

    protected Resource getResource() {
        return resource;
    }

}
