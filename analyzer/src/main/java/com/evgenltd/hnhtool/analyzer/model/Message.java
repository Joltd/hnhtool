package com.evgenltd.hnhtool.analyzer.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:04</p>
 */
public class Message {

    public Message(final Type type) {
        this.type = new ReadOnlyObjectWrapper<>(type);
    }

    private ReadOnlyObjectProperty<Type> type;
    public Type getType() {
        return type.get();
    }
    public ReadOnlyObjectProperty<Type> typeProperty() {
        return type;
    }

    private ObjectProperty<ObjectNode> body = null;
    public ObjectNode getBody() {
        return body != null ? body.get() : null;
    }
    public ObjectProperty<ObjectNode> bodyProperty() {
        if (body == null) {
            body = new SimpleObjectProperty<>();
        }
        return body;
    }
    public void setBody(final ObjectNode body) {
        this.bodyProperty().set(body);
    }

    public enum Type {
        INBOUND,
        OUTBOUND
    }

}
