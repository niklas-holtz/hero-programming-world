package controller.buttons;

import controller.HeroSimulatorStage;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import models.Map;

public class CoinButton extends Button {

    private final Color textColor = Color.DARKGOLDENROD;
    private final Map map;
    private final HeroSimulatorStage sim;

    public CoinButton(ImageView image, HeroSimulatorStage sim) {
        super(sim.getLanguageController().getRes().getString("coinButton") + ": " + sim.getMapController().getMap().getCurrentCoins() + "/" + sim.getMapController().getMap().getInventorySize(), image);
        this.sim = sim;
        this.map = sim.getMapController().getMap();
        setShadow();
        map.getCoinProperty().addListener((obs, o, n) -> {
            Platform.runLater(
                    this::update
            );
        });
    }

    public synchronized void update() {
        this.setText(sim.getLanguageController().getRes().getString("coinButton") + ": " + map.getCurrentCoins() + "/" + map.getInventorySize());
        setShadow();
        if (map.getCurrentCoins() == map.getInventorySize()) {
            if (this.getTextFill() != textColor) {
                this.setTextFill(textColor);
            }
        } else {
            if (this.getTextFill() == textColor) this.setTextFill(Color.BLACK);
        }
    }

    private void setShadow() {
        DropShadow ds = new DropShadow();
        double maxRadius = 10.0;
        double newRadius = ((double) map.getCurrentCoins() / (double) map.getInventorySize()) * maxRadius;
        double minRadius = 1.75;
        ds.setRadius(Math.max(minRadius, newRadius));
        ds.setColor(Color.GOLD);
        this.setEffect(ds);
    }
}
