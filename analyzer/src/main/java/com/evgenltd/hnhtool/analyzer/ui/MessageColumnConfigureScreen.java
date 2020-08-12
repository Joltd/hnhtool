package com.evgenltd.hnhtool.analyzer.ui;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.model.MessageColumn;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class MessageColumnConfigureScreen {

    public ListView<MessageColumn> columns;

    public void initialize() {
        columns.setCellFactory(param -> new MessageColumnListCell());
        columns.setItems(C.getMainModel().getMessageColumns());
    }

    private static final class MessageColumnListCell extends ListCell<MessageColumn> {

        private HBox box;
        private CheckBox enabled;
        private Label name;

        MessageColumnListCell() {
            enabled = new CheckBox();
            name = new Label();
            box = new HBox();
            box.getChildren().addAll(enabled, name);
            box.setSpacing(5);
        }

        @Override
        protected void updateItem(final MessageColumn item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                enabled.setSelected(item.isEnabled());
                item.enabledProperty().bind(enabled.selectedProperty());
                name.setText(item.getName());
                setGraphic(box);
            }
        }

    }

}
