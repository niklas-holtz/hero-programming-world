package controller.map;

import java.util.Optional;

import controller.GameController;
import controller.HeroSimulatorStage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.Map;
import views.MapPanel;

public class ResizeMapEventHandler extends GameController implements EventHandler<ActionEvent> {

    public ResizeMapEventHandler(HeroSimulatorStage sim) {
        super(sim);
    }

    @Override
    public void handle(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        BorderPane borderPane = new BorderPane();
        TitledPane titledPane = new TitledPane();
        titledPane.setText(super.sim.getLanguageController().getRes().getString("rm_pane"));

        dialog.setTitle(super.sim.getLanguageController().getRes().getString("rm_dialog"));
        dialog.setHeaderText(super.sim.getLanguageController().getRes().getString("rm_header"));

        // Icon
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/Terrain24.gif")));

        // Set the button types.
        ButtonType createMap = new ButtonType(super.sim.getLanguageController().getRes().getString("rm_create"), ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createMap, new ButtonType(this.lang.getRes().getString(("n_cancel")), ButtonData.CANCEL_CLOSE));

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField height = new TextField();
        height.setPromptText(Integer.toString(super.sim.getMapController().getMap().getRows()));
        TextField width = new TextField();
        width.setPromptText(Integer.toString(super.sim.getMapController().getMap().getColls()));

        grid.add(new Label(super.sim.getLanguageController().getRes().getString("rm_height") + ":"), 0, 0);
        grid.add(height, 1, 0);
        grid.add(new Label(super.sim.getLanguageController().getRes().getString("rm_width") + ":"), 0, 1);
        grid.add(width, 1, 1);

        CheckBox checkBox = new CheckBox(super.sim.getLanguageController().getRes().getString("rm_checkbox"));
        grid.add(checkBox, 0, 2);

        checkBox.setOnAction(e -> {
            if (checkBox.isSelected() && !titledPane.isExpanded()) {
                titledPane.setExpanded(true);
                if (height.getText().isEmpty())
                    height.setText(height.getPromptText());
                if (width.getText().isEmpty())
                    width.setText(width.getPromptText());
            } else if ((!checkBox.isSelected() || checkBox.isIndeterminate()) && titledPane.isExpanded()) {
                titledPane.setExpanded(false);
            }
        });

        titledPane.expandedProperty().addListener(e -> {
            if (checkBox.isSelected() && !titledPane.isExpanded()) {
                titledPane.setExpanded(true);
                if (height.getText().isEmpty())
                    height.setText(height.getPromptText());
                if (width.getText().isEmpty())
                    width.setText(width.getPromptText());
            } else if ((!checkBox.isSelected() || checkBox.isIndeterminate()) && titledPane.isExpanded()) {
                titledPane.setExpanded(false);
            }
        });


        //f�r titledPane
        GridPane randomGrid = new GridPane();
        BorderPane randomBorder = new BorderPane();

        int Height = Integer.parseInt(height.getPromptText());
        int Width = Integer.parseInt(width.getPromptText());

        Slider coins = new Slider(0, (int) (Height * Width) / 8, (int) (Height * Width) / 10);
        Slider walls = new Slider(0, (int) (Height * Width) / 6, (int) (Height * Width) / 7);
        Slider water = new Slider(0, (int) (Height * Width) / 7, (int) (Height * Width) / 8);


        Label coinCount = new Label("Chance " + super.sim.getLanguageController().getRes().getString("rm_coin") + ": \t" + (int) coins.getValue());
        Label wallCount = new Label("Chance " + super.sim.getLanguageController().getRes().getString("rm_wall") + ": \t" + (int) walls.getValue());
        Label waterCount = new Label("Chance " + super.sim.getLanguageController().getRes().getString("rm_water") + ": \t" + (int) water.getValue());

        Button generate = new Button(super.sim.getLanguageController().getRes().getString("rm_generate"));

        randomGrid.add(coinCount, 0, 0);
        randomGrid.add(wallCount, 0, 1);
        randomGrid.add(waterCount, 0, 2);
        randomGrid.add(coins, 1, 0);
        randomGrid.add(walls, 1, 1);
        randomGrid.add(water, 1, 2);
        randomGrid.add(generate, 0, 3);

        randomGrid.setHgap(40);
        randomGrid.setVgap(10);
        randomGrid.setPadding(new Insets(5, 10, 10, 10));

        Map pseudoMap = new Map();
        pseudoMap.resize(super.sim.getMapController().getMap().getRows(), super.sim.getMapController().getMap().getColls());
        pseudoMap.next((int) water.getValue(), (int) walls.getValue(), (int) coins.getValue());
        MapController controller = new MapController(pseudoMap);
        ScrollPane scrollPane = new ScrollPane();
        MapPanel panel = new MapPanel(controller, scrollPane);
        pseudoMap.addObserver(panel);
        scrollPane.setContent(panel);
        scrollPane.setMaxWidth(500);
        scrollPane.setMaxHeight(350);

        generate.setOnAction(e -> {
            try {
                int newHeight = Integer.parseInt(height.getText());
                int newWidth = Integer.parseInt(width.getText());
                if (newHeight >= 3 && newHeight <= 50 && newWidth >= 3 && newWidth <= 50) {
                    pseudoMap.resize(newHeight, newWidth);
                }
            } catch (NumberFormatException ex) {
            }

            pseudoMap.next((int) water.getValue(), (int) walls.getValue(), (int) coins.getValue());
        });

        coins.valueProperty().addListener((obs, oldval, newVal) -> {
            if (oldval.intValue() == newVal.intValue()) return;
            coins.setValue(newVal.intValue());
            coinCount.setText("Chance " + super.sim.getLanguageController().getRes().getString("rm_coin") + ": \t" + (int) coins.getValue());
        });
        walls.valueProperty().addListener((obs, oldval, newVal) -> {
            if (oldval.intValue() == newVal.intValue()) return;
            walls.setValue(newVal.intValue());
            wallCount.setText("Chance " + super.sim.getLanguageController().getRes().getString("rm_wall") + ": \t" + (int) walls.getValue());
        });
        water.valueProperty().addListener((obs, oldval, newVal) -> {
            if (oldval.intValue() == newVal.intValue()) return;
            water.setValue(newVal.intValue());
            waterCount.setText("Chance " + super.sim.getLanguageController().getRes().getString("rm_water") + ": \t" + (int) water.getValue());
        });


        randomBorder.setTop(randomGrid);
        randomBorder.setBottom(scrollPane);

        titledPane.setContent(randomBorder);
        titledPane.setExpanded(false);
        //Um die Gr��e vom DialogPane nach Ausklappen des TitledPanes wieder zu aktualisieren.
        titledPane.heightProperty().addListener(e -> {
            Platform.runLater(() -> {
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            });
        });

        Node createButton = dialog.getDialogPane().lookupButton(createMap);
        createButton.setDisable(true);


        // Do some validation (using the Java 8 lambda syntax).
        height.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newHeight = Integer.parseInt(newValue);
                int newWidth = Integer.parseInt(width.getText());
                if (newHeight >= 3 && newHeight <= 50 && newWidth >= 3 && newWidth <= 50) {
                    createButton.setDisable(false);
                    coins.setMax((int) (newHeight * newWidth) / 8);
                    coins.setValue((int) (Height * Width) / 10);
                    walls.setMax((int) (newHeight * newWidth) / 6);
                    walls.setValue((int) (Height * Width) / 7);
                    water.setMax((int) (newHeight * newWidth) / 7);
                    water.setValue((int) (Height * Width) / 8);
                } else {
                    createButton.setDisable(true);
                }
            } catch (NumberFormatException ex) {
                createButton.setDisable(true);
            }
        });
        width.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newHeight = Integer.parseInt(height.getText());
                int newWidth = Integer.parseInt(newValue);
                if (newHeight >= 3 && newHeight <= 50 && newWidth >= 3 && newWidth <= 50) {
                    createButton.setDisable(false);
                    coins.setMax((int) (newHeight * newWidth) / 8);
                    coins.setValue((int) (Height * Width) / 10);
                    walls.setMax((int) (newHeight * newWidth) / 6);
                    walls.setValue((int) (Height * Width) / 7);
                    water.setMax((int) (newHeight * newWidth) / 7);
                    water.setValue((int) (Height * Width) / 8);
                } else {
                    createButton.setDisable(true);
                }
            } catch (NumberFormatException ex) {
                createButton.setDisable(true);
            }
        });

        borderPane.setTop(grid);
        borderPane.setBottom(titledPane);
        dialog.getDialogPane().setContent(borderPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createMap) {
                return new Pair<>(height.getText(), width.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(mapSize -> {
            if (checkBox.isSelected()) {
                super.sim.getMapController().getMap().deserializeMap(pseudoMap);
            } else
                super.sim.getMapController().getMap().resize(Integer.parseInt(mapSize.getKey()), Integer.parseInt(mapSize.getValue()));
        });
    }
}
