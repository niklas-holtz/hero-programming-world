package controller.map;

import java.util.Optional;

import controller.GameController;
import controller.HeroSimulatorStage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChangeCoinsEventHandler extends GameController implements EventHandler<ActionEvent> {

    public ChangeCoinsEventHandler(HeroSimulatorStage sim) {
        super(sim);
    }

    @Override
    public void handle(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(super.lang.getRes().getString("coinsChange_title"));
        dialog.setHeaderText(super.lang.getRes().getString("coinsChange_header") + " " + super.sim.getMapController().getMap().getInventorySize() + ".");

        // Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/coins_24.png")));

        // Set the button types.
        ButtonType okay = new ButtonType("Ok", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okay, new ButtonType(this.lang.getRes().getString(("n_cancel")), ButtonData.CANCEL_CLOSE));

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        Slider coinSlider = new Slider(0, 10, super.sim.getMapController().getMap().getCurrentCoins());
        coinSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            coinSlider.setValue(Math.round(newVal.doubleValue()));
        });
        coinSlider.setShowTickLabels(true);
        coinSlider.setShowTickMarks(true);
        coinSlider.setMajorTickUnit(2);

        grid.add(new Label(super.lang.getRes().getString("coinsChange_label") + ": "), 0, 0);
        grid.add(coinSlider, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okay) {
                return (int) coinSlider.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(count -> {
            super.sim.getMapController().getMap().setCoins(count);
        });
    }
}
