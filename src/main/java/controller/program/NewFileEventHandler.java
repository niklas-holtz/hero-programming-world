package controller.program;

import java.util.Optional;

import controller.language.LanguageController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

public class NewFileEventHandler implements EventHandler<ActionEvent> {

    private final ProgramController controller;
    private final LanguageController lang;

    public NewFileEventHandler(ProgramController controller) {
        this.controller = controller;
        this.lang = this.controller.getStage().getLanguageController();
    }

    @Override
    public void handle(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(this.lang.getRes().getString("n_title"));
        dialog.setHeaderText(this.lang.getRes().getString("n_header"));

        ButtonType create = new ButtonType(this.lang.getRes().getString("n_button"), ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(create, new ButtonType(this.lang.getRes().getString(("n_cancel")), ButtonData.CANCEL_CLOSE));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField newName = new TextField();

        grid.add(new Label(this.lang.getRes().getString("n_name") + ": "), 0, 0);
        grid.add(newName, 1, 0);

        Node createButton = dialog.getDialogPane().lookupButton(create);
        createButton.setDisable(true);

        newName.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.length() >= 1 && controller.isValidFileName(newValue) && !controller.isProgramOpen(newValue))
                    createButton.setDisable(false);
                else
                    createButton.setDisable(true);
            } catch (NumberFormatException ex) {
                createButton.setDisable(true);
            }
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == create) {
                return (newName.getText());
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            controller.startNewStage(name);
        });
    }
}
