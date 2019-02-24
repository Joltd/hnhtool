package com.evgenltd.hnhtool.analyzer.service;

import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtool.analyzer.model.Message;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:03</p>
 */
public class MainModel implements Lifecycle {

    private ObservableList<Message> messages;
    public ObservableList<Message> getMessages() {
        return messages;
    }

    @Override
    public void init() {
        messages = FXCollections.observableArrayList();
    }

    @Override
    public void stop() {}

    public void addInboundMessage(final ObjectNode node) {
        Platform.runLater(() -> {
            final Message message = new Message(Message.Type.INBOUND);
            message.setBody(node);
            messages.add(message);
        });
    }

    public void addOutboundMessage(final ObjectNode node) {
        Platform.runLater(() -> {
            final Message message = new Message(Message.Type.OUTBOUND);
            message.setBody(node);
            messages.add(message);
        });
    }

}
