package controller.database;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.StringIO;
import util.StringQuartet;

public class LoadExampleEventHandler implements EventHandler<ActionEvent> {

    private final DBController dbc;

    public LoadExampleEventHandler(DBController dbc) {
        this.dbc = dbc;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void handle(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(this.dbc.getStage().getLanguageController().getRes().getString("le_title"));
        dialog.setHeaderText(this.dbc.getStage().getLanguageController().getRes().getString("le_header"));

        // Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/Open24.gif")));

        ButtonType okay = new ButtonType("Ok", ButtonData.OK_DONE);
        Button delete = new Button(this.dbc.getStage().getLanguageController().getRes().getString("le_del"));
        dialog.getDialogPane().getButtonTypes().addAll(okay, new ButtonType(this.dbc.getStage().getLanguageController().getRes().getString("n_cancel"), ButtonData.CANCEL_CLOSE));

        Button filter = new Button(this.dbc.getStage().getLanguageController().getRes().getString("le_filter"));
        BorderPane border = new BorderPane();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField tag = new TextField();
        tag.setPromptText("Tags");

        grid.add(new Label(this.dbc.getStage().getLanguageController().getRes().getString("le_label") + ":"), 0, 1);
        grid.add(tag, 0, 2);
        grid.add(filter, 1, 2);
        grid.add(delete, 2, 2);
        grid.setPrefHeight(80);

        TableView<StringQuartet> table = new TableView<StringQuartet>();
        table.setEditable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn exampleID = new TableColumn("ID");
        exampleID.setCellValueFactory(new PropertyValueFactory<StringQuartet, String>("fourth"));
        TableColumn exampleName = new TableColumn("Name");
        exampleName.setMinWidth(100);
        exampleName.setCellValueFactory(new PropertyValueFactory<StringQuartet, String>("first"));
        TableColumn exampleTags = new TableColumn("Tags");
        exampleTags.setMinWidth(200);
        exampleTags.setCellValueFactory(new PropertyValueFactory<StringQuartet, String>("second"));
        TableColumn examplePath = new TableColumn(this.dbc.getStage().getLanguageController().getRes().getString("le_path"));
        examplePath.setMinWidth(200);
        examplePath.setCellValueFactory(new PropertyValueFactory<StringQuartet, String>("third"));

        fillTableWithDBData(table, null);

        table.getColumns().addAll(exampleID, exampleName, exampleTags, examplePath);
        border.setTop(grid);
        border.setBottom(table);
        Node loadButton = dialog.getDialogPane().lookupButton(okay);
        loadButton.setDisable(true);
        delete.setDisable(true);

        filter.setOnAction(e -> {
            fillTableWithDBData(table, (tag.getText().trim().isEmpty()) ? null : tag.getText());
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadButton.setDisable(false);
                delete.setDisable(false);
            } else {
                // Nothing selected
                loadButton.setDisable(true);
                delete.setDisable(true);
            }

        });

        delete.setOnAction(e -> {
            StringQuartet selected = table.getSelectionModel().getSelectedItem();
            this.dbc.deleteExample(Integer.parseInt(selected.getFourth()));    //example ID
            this.fillTableWithDBData(table, (tag.getText().trim().isEmpty()) ? null : tag.getText());
        });

        dialog.getDialogPane().setContent(border);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okay) {
                StringQuartet selected = table.getSelectionModel().getSelectedItem();
                return selected.getFourth();    // Example ID
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(id -> {
            this.dbc.loadExample(Integer.parseInt(id));
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fillTableWithDBData(TableView table, String tags) {
        ObservableList<StringQuartet> data = FXCollections.observableArrayList();
        String[][] examples = this.dbc.getAllExamplesFromDB();
        if (examples == null)
            return;
        String[] t = StringIO.stringToArrayBySpaces(tags);
        for (int i = 0; i < examples.length; i++) {
            if (tags != null) {
                for (int j = 0; j < t.length; j++) {
                    if (examples[i][1].toLowerCase().contains(t[j])) {
                        StringQuartet row = new StringQuartet(examples[i][0], examples[i][1], examples[i][2], examples[i][3]);
                        data.add(row);
                        break;
                    }
                }
            } else {
                StringQuartet row = new StringQuartet(examples[i][0], examples[i][1], examples[i][2], examples[i][3]);
                data.add(row);
            }
        }
        table.setItems(data);
    }
}
