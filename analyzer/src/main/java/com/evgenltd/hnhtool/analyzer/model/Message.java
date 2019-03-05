package com.evgenltd.hnhtool.analyzer.model;

import com.evgenltd.hnhtool.analyzer.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private BooleanProperty hide;
    public boolean isHide() {
        return hide != null && hide.get();
    }
    public BooleanProperty hideProperty() {
        if (hide == null) {
            hide = new SimpleBooleanProperty();
        }
        return hide;
    }
    public void setHide(final boolean hide) {
        this.hideProperty().set(hide);
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

    // data

    private ObjectProperty<List<Byte>> data = null;
    public List<Byte> getData() {
        return data != null ? data.get() : new ArrayList<>();
    }
    public ObjectProperty<List<Byte>> dataProperty() {
        if (data == null) {
            data = new SimpleObjectProperty<>(new ArrayList<>());
        }
        return data;
    }
    public void setData(final List<Byte> data) {
        this.dataProperty().set(data != null ? data : new ArrayList<>());
    }

    public enum Type {
        INBOUND,
        OUTBOUND
    }

}
