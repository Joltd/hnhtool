package com.evgenltd.hnhtool.analyzer.service;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.common.Lifecycle;
import com.evgenltd.hnhtool.analyzer.model.Message;
import com.evgenltd.hnhtool.analyzer.model.MessageColumn;
import com.evgenltd.hnhtools.common.Resources;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:03</p>
 */
public class MainModel implements Lifecycle {

    private static final Logger log  = LogManager.getLogger(MainModel.class);

    @Override
    public void init() {
        messages = FXCollections.observableArrayList();
        analyzingEnabledProperty().addListener((observable, oldValue, newValue) -> C.getGate().setEnabled(newValue));
        loadMessageColumn();
    }

    @Override
    public void stop() {}

    // ##################################################
    // #                                                #
    // #  Properties                                    #
    // #                                                #
    // ##################################################

    // messages

    private ObservableList<Message> messages;
    public ObservableList<Message> getMessages() {
        return messages;
    }


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

    // analyzingEnabled

    private BooleanProperty analyzingEnabled = new SimpleBooleanProperty(false);
    public boolean isAnalyzingEnabled() {
        return analyzingEnabled.get();
    }
    public BooleanProperty analyzingEnabledProperty() {
        return analyzingEnabled;
    }
    public void setAnalyzingEnabled(final boolean analyzingEnabled) {
        this.analyzingEnabled.set(analyzingEnabled);
    }

    // messageColumns

    private ObservableList<MessageColumn> messageColumns;
    public ObservableList<MessageColumn> getMessageColumns() {
        return messageColumns;
    }

    // ##################################################
    // #                                                #
    // #  Logic                                         #
    // #                                                #
    // ##################################################

    private void loadMessageColumn() {
        this.messageColumns = FXCollections.observableArrayList();
        final String content = Resources.load(getClass(), "message-columns.json");
        try {
            final List<MessageColumn> messageColumns = C.getMapper().readValue(content, new TypeReference<List<MessageColumn>>() {});
            this.messageColumns.addAll(messageColumns);
        } catch (IOException e) {
            log.error(e);
        }
    }

}
