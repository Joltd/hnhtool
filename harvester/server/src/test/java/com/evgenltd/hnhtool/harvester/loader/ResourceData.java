package com.evgenltd.hnhtool.harvester.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceData {

    private final String name;
    private final int version;
    private final List<Layer> layers = new ArrayList<>();

    public ResourceData(final String name, final int version) {
        this.name = name;
        this.version = version;
    }

    public void addLayer(final Layer layer) {
        layers.add(layer);
    }

    public <T extends Layer> List<T> getLayers(final Class<T> layerClass) {
        return layers.stream()
                .filter(layer -> layerClass.isAssignableFrom(layer.getClass()))
                .map(layerClass::cast)
                .collect(Collectors.toList());
    }

}
