package com.evgenltd.hnhtool.analyzer.ui;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.model.Message;
import com.evgenltd.hnhtool.analyzer.model.MessageColumn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 24-02-2019 17:24</p>
 */
public class MainScreen {

    public TableView<Message> messages;
    public TextArea body;
    public ToggleButton analyzingEnabled;
    public TextArea data;

    public void initialize() {
        analyzingEnabled.selectedProperty().bindBidirectional(C.getMainModel().analyzingEnabledProperty());
        toggleAnalyzing(null);

        final List<TableColumn<Message, String>> columns = C.getMainModel()
                .getMessageColumns()
                .stream()
                .map(this::prepareColumn)
                .collect(Collectors.toList());

        messages.getColumns().setAll(columns);
        messages.setItems(C.getMainModel().getMessages());
        messages.getSelectionModel().getSelectedItems().addListener((InvalidationListener) observable -> showBody());
    }

    private void showBody() {
        body.clear();
        data.clear();
        final Message message = messages.getSelectionModel().getSelectedItem();
        if (message == null) {
            return;
        }

        try {
            final ObjectNode body = message.getBody();
            final JsonNode analyzerException = body.get("analyzer_exception");
            if (analyzerException != null) {
                this.body.setText(analyzerException.asText());
            } else {
                final String json = C.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body);
                this.body.setText(json);
            }
        } catch (JsonProcessingException e) {
            body.setText(e.getMessage());
        }

        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < message.getData().size(); index++) {
            if (index != 0 && index % 8 == 0) {
                sb.append("\n");
            }
            final String b = message.getData().get(index);
            sb.append(b).append(" ");
        }
        data.setText(sb.toString());
    }

    public void clearMessages(final ActionEvent actionEvent) {
        C.getMainModel().getMessages().clear();
    }

    public void toggleAnalyzing(final ActionEvent actionEvent) {
        if (C.getGate().isEnabled()) {
            analyzingEnabled.setText("Pause");
        } else {
            analyzingEnabled.setText("Play");
        }
    }

    private TableColumn<Message, String> prepareColumn(final MessageColumn messageColumn) {
        final TableColumn<Message, String> column = new TableColumn<>(messageColumn.getName());
        column.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue(messageColumn)));
        column.setPrefWidth(messageColumn.getWidth());
        messageColumn.enabledProperty().addListener(observable -> column.setVisible(messageColumn.isEnabled()));
        return column;
    }

    public void configureColumns(final ActionEvent actionEvent) {

        final Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Configure columns");
        final Parent content = C.load(MessageColumnConfigureScreen.class);

        dialog.getDialogPane().setContent(content);

        final ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.APPLY);

        dialog.getDialogPane().getButtonTypes().addAll(okButton);
        dialog.show();

    }

}
