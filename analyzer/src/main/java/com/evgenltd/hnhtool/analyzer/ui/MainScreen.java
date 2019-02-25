package com.evgenltd.hnhtool.analyzer.ui;

import com.evgenltd.hnhtool.analyzer.C;
import com.evgenltd.hnhtool.analyzer.Constants;
import com.evgenltd.hnhtool.analyzer.model.DoubleWord;
import com.evgenltd.hnhtool.analyzer.model.Message;
import com.evgenltd.hnhtool.analyzer.model.MessageColumn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

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
    public TableView<DoubleWord> data;
    public Label selectedCells;

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
        messages.setRowFactory(param -> new ErrorTableRow());

        data.getSelectionModel().setCellSelectionEnabled(true);
        data.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        data.getColumns().clear();
        for (int index = 0; index < 8; index++) {
            final int finalIndex = index;
            final TableColumn<DoubleWord, DoubleWord.Byte> column = new TableColumn<>(String.format("%02X", index));
            column.setMaxWidth(22);
            column.setPrefWidth(22);
            column.setCellValueFactory(param -> param.getValue().getByte(finalIndex));
            column.setCellFactory(param -> new ByteTableCell());
            data.getColumns().add(column);
        }
        data.getSelectionModel().getSelectedCells().addListener((InvalidationListener) observable -> selectedCells.setText(String.format("Selected: %s", data.getSelectionModel().getSelectedCells().size())));
    }

    private void showBody() {
        body.clear();
        data.getItems().clear();
        final Message message = messages.getSelectionModel().getSelectedItem();
        if (message == null) {
            return;
        }

        try {
            final ObjectNode body = message.getBody();
            final JsonNode analyzerException = body.get(Constants.ANALYZER_EXCEPTION_TOKEN);
            if (analyzerException != null) {
                this.body.appendText(analyzerException.asText());
            }
            final String json = C.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(body);
            this.body.appendText(json);
        } catch (JsonProcessingException e) {
            body.setText(e.getMessage());
        }

        data.setItems(C.getMainModel().toDoubleWord(message.getData()));
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

    private static final class ErrorTableRow extends TableRow<Message> {
        @Override
        protected void updateItem(final Message item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setStyle("");
            } else if(item.getBody().has(Constants.ANALYZER_EXCEPTION_TOKEN)) {
                setStyle("-fx-background-color: tomato");
            } else {
                setStyle("");
            }
        }
    }

    private static final class ByteTableCell extends TableCell<DoubleWord, DoubleWord.Byte> {

        public ByteTableCell() {
            setOnMouseClicked(event -> {
                final ObservableList<TablePosition> selectedCells = getTableView().getSelectionModel()
                        .getSelectedCells();
                if (selectedCells.size() == 1) {
                    Holder.firstCell = selectedCells.get(0);
                }
            });
            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!event.isShiftDown()) {
                    return;
                }

                if (Holder.firstCell == null) {
                    return;
                }

                event.consume();

                getTableView().getSelectionModel().clearSelection();

                int firstPosition = convertToPosition(Holder.firstCell.getRow(), Holder.firstCell.getColumn());
                int lastPosition = convertToPosition(getIndex(), getTableView().getColumns().indexOf(getTableColumn()));

                for (
                        int position = Math.min(firstPosition, lastPosition);
                        position <= Math.max(firstPosition, lastPosition);
                        position++
                ) {
                    final int row = position / 8;
                    final int column = position % 8;
                    getTableView().getSelectionModel().select(row, getTableView().getColumns().get(column));
                }

            });
        }

        @Override
        protected void updateItem(final DoubleWord.Byte item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText("");
            } else {
                setText(item.getLabel());
            }
        }
    }

    private static int convertToPosition(final int row, final int column) {
        return row * 8 + column;
    }

    private static class Holder {
        private static TablePosition firstCell;
    }

}
