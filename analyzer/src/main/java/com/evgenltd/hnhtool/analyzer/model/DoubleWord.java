package com.evgenltd.hnhtool.analyzer.model;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Arrays;
import java.util.List;

public class DoubleWord {

    private List<SimpleObjectProperty<Byte>> bytes = Arrays.asList(
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>(),
            new SimpleObjectProperty<>()
    );

    public void addByte(final int index, final byte value) {
        final Byte wrapper = new Byte();
        wrapper.setValue(value);
        wrapper.setLabel(String.format("%02X", value));
        bytes.get(index).setValue(wrapper);
    }

    public SimpleObjectProperty<Byte> getByte(final int index) {
        return bytes.get(index);
    }

    public static class Byte {
        private byte value;
        private String label;

        public byte getValue() {
            return value;
        }

        public void setValue(final byte value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }
    }

}
