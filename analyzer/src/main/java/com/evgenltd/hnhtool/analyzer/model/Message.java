package com.evgenltd.hnhtool.analyzer.model;

import com.evgenltd.hnhtool.analyzer.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:04</p>
 */
public class Message {

    public Message(final Type type) {
        this.time = new ReadOnlyObjectWrapper<>(LocalDateTime.now());
        this.type = new ReadOnlyObjectWrapper<>(type);
    }

    // getter

    public String getValue(final MessageColumn column) {
        String s = column.getName();
        if ("Time".equals(s)) {
            return getTime().format(Constants.TIME_FORMATTER);
        } else if ("Type".equals(s)) {
            return getType().name();
        } else if (column.getPath() != null) {
            final JsonNode value = getBody().at(column.getPath());
            return value.asText();
        } else {
            return "<empty>";
        }
    }

    // time

    private ReadOnlyObjectProperty<LocalDateTime> time;
    public LocalDateTime getTime() {
        return time.get();
    }
    public ReadOnlyObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    // type

    private ReadOnlyObjectProperty<Type> type;
    public Type getType() {
        return type.get();
    }
    public ReadOnlyObjectProperty<Type> typeProperty() {
        return type;
    }

    // body

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
