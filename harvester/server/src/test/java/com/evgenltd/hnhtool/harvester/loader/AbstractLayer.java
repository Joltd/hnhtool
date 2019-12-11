package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:35</p>
 */
public class AbstractLayer {

    private Resource resource;

    public AbstractLayer(final Resource resource) {
        this.resource = resource;
    }

    protected Resource getResource() {
        return resource;
    }

}
