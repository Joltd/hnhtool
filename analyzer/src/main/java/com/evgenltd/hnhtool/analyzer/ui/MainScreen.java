package com.evgenltd.hnhtool.analyzer.ui;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:24</p>
 */
public class MainScreen {

    public ListView<Message> messages;
    public TextArea body;

    public void initialize() {
        messages.setItems(C.getMainScreenModel().getMessages());
        messages.getSelectionModel().getSelectedItems().addListener((InvalidationListener) observable -> showBody());
        messages.setCellFactory(param -> new MessageListCell());
    }

    private void showBody() {
        body.clear();
        final Message message = messages.getSelectionModel().getSelectedItem();
        if (message == null) {
            return;
        }

        try {
            final String json = C.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getBody());
            body.setText(json);
        } catch (JsonProcessingException e) {
            body.setText(e.getMessage());
        }
    }

    public void clearMessages(final ActionEvent actionEvent) {
        C.getMainScreenModel().getMessages().clear();
    }

    private static final class MessageListCell extends ListCell<Message> {

        @Override
        protected void updateItem(final Message item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getType().name());
            }
        }

    }

}
