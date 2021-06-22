package controller.database;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class SaveExampleEventHandler implements EventHandler<ActionEvent> {

    private final DBController dbc;

    public SaveExampleEventHandler(DBController dbc) {
        this.dbc = dbc;
    }

    @Override
    public void handle(ActionEvent event) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Beispiel speichern..");
        dialog.setHeaderText("Tags");

        ButtonType okay = new ButtonType("Ok", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okay, ButtonType.CANCEL);

        // Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/Save24.gif")));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField tag = new TextField();
        tag.setPromptText("Tag");
        TextField name = new TextField();
        name.setPromptText("Name");

        grid.add(new Label("Bitte Tags (durch Leerzeichen getrennt) eingeben: "), 0, 1);
        grid.add(tag, 1, 1);
        grid.add(new Label("Bitte Namen eintragen."), 0, 0);
        grid.add(name, 1, 0);

        Node saveButton = dialog.getDialogPane().lookupButton(okay);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        tag.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                saveButton.setDisable(newValue.trim().isEmpty() || name.getText().trim().isEmpty() || name.getText().contains(" "));
            } catch (NumberFormatException ex) {
                saveButton.setDisable(true);
            }
        });

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                saveButton.setDisable(newValue.trim().isEmpty() || newValue.contains(" ") || tag.getText().trim().isEmpty());
            } catch (NumberFormatException ex) {
                saveButton.setDisable(true);
            }
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okay) {
                return new Pair<String, String>(tag.getText(), name.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(data -> {
            this.dbc.saveExample(data.getKey(), data.getValue());
        });
    }
}
