package com.evgenltd.hnhtool.analyzer.model;

import com.evgenltd.hnhtool.analyzer.serializer.MessageColumnMarshaller;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.*;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 20:05</p>
 */
@JsonSerialize(using = MessageColumnMarshaller.Serializer.class)
@JsonDeserialize(using = MessageColumnMarshaller.Deserializer.class)
public class MessageColumn implements Cloneable {

    // enabled

    private BooleanProperty enabled;
    public boolean isEnabled() {
        return enabled != null && enabled.get();
    }
    public BooleanProperty enabledProperty() {
        if (enabled == null) {
            enabled = new SimpleBooleanProperty();
        }
        return enabled;
    }
    public void setEnabled(final boolean enabled) {
        this.enabledProperty().set(enabled);
    }

    // name

    private StringProperty name;
    public String getName() {
        return name != null ? name.get() : null;
    }
    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    // path

    private ObjectProperty<JsonPointer> path;
    public JsonPointer getPath() {
        return path != null ? path.get() : null;
    }
    public ObjectProperty<JsonPointer> pathProperty() {
        if (this.path == null) {
            this.path = new SimpleObjectProperty<>();
        }
        return path;
    }
    public void setPath(final JsonPointer path) {
        this.pathProperty().set(path);
    }

    // width

    private DoubleProperty width;
    public double getWidth() {
        return width != null ? width.get() : 0;
    }
    public DoubleProperty widthProperty() {
        if (width == null) {
            width = new SimpleDoubleProperty();
        }
        return width;
    }
    public void setWidth(final double width) {
        this.widthProperty().set(width);
    }
}