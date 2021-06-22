package controller.mouse;

import controller.GameController;
import controller.HeroSimulatorStage;
import controller.SoundController;
import controller.simulation.SimulationState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import models.Map;
import views.MapPanel;

public class MouseController extends GameController {

    private int toggleGroupValue = -2;
    private final int tilePixelSize;
    private boolean dragging = false;
    private int currentXPos = 0, currentYPos = 0;
    private HeroContextMenu context;

    public MouseController(Map m, MapPanel mp, HeroSimulatorStage sim) {
        super(m, mp, sim);
        this.tilePixelSize = mp.getPixelSize();
    }

    public int getTilePixelSize() {
        return this.tilePixelSize;
    }

    public void initEvents() {
        // ToggleGroupEvent
        ChangeListener<Toggle> toggleGroupListener = new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (new_toggle == null) {
                    toggleGroupValue = -2;
                    return;
                }
                String id = ((ToggleButton) new_toggle).getId();
                switch (id) {
                    case "platz_held":
                        toggleGroupValue = 3;
                        break;
                    case "platz_muenze":
                        toggleGroupValue = 2;
                        break;
                    case "platz_mauer":
                        toggleGroupValue = 1;
                        break;
                    case "platz_wasser":
                        toggleGroupValue = -1;
                        break;
                    case "del_kachel":
                        toggleGroupValue = 0;
                        break;
                    default:
                        toggleGroupValue = -2;
                }
            }
        };

        super.sim.getT_platzierungsGruppe().selectedToggleProperty().addListener(toggleGroupListener);

        super.mapPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (toggleGroupValue != -2) {
                int posX = (int) (event.getX() / tilePixelSize);
                int posY = (int) (event.getY() / tilePixelSize);
                if (!map.heroIsOnPos(posX, posY)) {
                    map.setTile(posX, posY, toggleGroupValue);
                    switch (toggleGroupValue) {
                        case (3):
                        case (1):
                        case (0):
                            sim.getSoundController().playSound(SoundController.Sounds.GRASS);
                            break;
                        case (-1):
                            sim.getSoundController().playSound(SoundController.Sounds.WATER);
                            break;
                        case (2):
                            sim.getSoundController().playSound(SoundController.Sounds.COIN);
                            break;
                    }
                }
            }
            // hide context window if initialized
            try {
                context.hide();
            } catch (NullPointerException e) {

            }
        });

        super.mapPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (sim.getSimManager().getState() == SimulationState.running) return;
            if (map.heroIsOnPos((int) (event.getX() / tilePixelSize), (int) (event.getY() / tilePixelSize)) && event.isPrimaryButtonDown()) {
                dragging = true;
            } else if (dragging) dragging = false;
        });

        super.mapPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (dragging) {
                int newX = (int) (event.getX() / tilePixelSize);
                int newY = (int) (event.getY() / tilePixelSize);
                if (map.getTile(newX, newY) != 1) {
                    currentXPos = newX;
                    currentYPos = newY;
                }
                mapPanel.drawHeroOnPos(currentXPos, currentYPos);
            } else {
                if (toggleGroupValue != -2) {
                    int posX = (int) (event.getX() / tilePixelSize);
                    int posY = (int) (event.getY() / tilePixelSize);
                    if (!map.heroIsOnPos(posX, posY))
                        map.setTile((int) (event.getX() / tilePixelSize), (int) (event.getY() / tilePixelSize), toggleGroupValue);
                }
            }
        });

        super.mapPanel.getCanvas().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (dragging) {
                super.map.moveHero(currentXPos, currentYPos);
                dragging = false;
            }
        });

        // ContextMenu
        super.mapPanel.getCanvas().setOnContextMenuRequested(event -> {
            if (super.sim.getSimManager().getState() == SimulationState.running) return;
            if (super.map.heroIsOnPos((int) (event.getX() / tilePixelSize), (int) (event.getY() / tilePixelSize))) {
                context = new HeroContextMenu(getMap().getHero(), super.sim);
                context.show(getMapPanel().getCanvas(), event.getScreenX(), event.getScreenY());
            }
        });
    }

}
