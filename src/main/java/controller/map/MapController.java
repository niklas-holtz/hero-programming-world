package controller.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import controller.GameController;
import controller.HeroSimulatorStage;
import controller.SoundController;
import javafx.stage.FileChooser;
import models.FullInventoryException;
import models.NoCoinException;
import models.NoWaterException;
import models.Map;
import models.MauerDaException;
import models.WaterException;
import views.MapPanel;

public class MapController extends GameController {

    public MapController(Map m, MapPanel mp, HeroSimulatorStage sim) {
        super(m, mp, sim);
    }

    public MapController(Map m) {
        super(m);
    }

    public void walk() {
        try {
            super.map.walk();
            sim.getSoundController().playSound(SoundController.Sounds.GRASS);
        } catch (MauerDaException | WaterException ex) {
            sim.getSoundController().playSound(SoundController.Sounds.ERROR);
        }
    }

    public void swim() {
        try {
            super.map.swim();
            sim.getSoundController().playSound(SoundController.Sounds.WATER);
        } catch (MauerDaException | NoWaterException ex) {
            sim.getSoundController().playSound(SoundController.Sounds.ERROR);
        }
    }

    public void takeCoin() {
        try {
            super.map.takeCoin();
            sim.getSoundController().playSound(SoundController.Sounds.COIN);
        } catch (NoCoinException | FullInventoryException ex) {
            sim.getSoundController().playSound(SoundController.Sounds.ERROR);
        }
    }

    public synchronized void serialize() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Karte");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map-Datei", "*.map"));
        fileChooser.setInitialDirectory(super.sim.getMapDir());
        fileChooser.setInitialFileName("Karte_serial");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            ObjectOutputStream mapObject;
            try {
                mapObject = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
                mapObject.writeObject(this.getMap());
                mapObject.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public synchronized void deserialize() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Karte öffnen..");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map-Datei", "*.map"));
        chooser.setInitialDirectory(super.sim.getMapDir());
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            ObjectInputStream mapObject;
            try {
                mapObject = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                Map m = (Map) mapObject.readObject();
                this.getMap().deserializeMap(m);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void saveXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Karte (XML)");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MXML-Datei", "*.mxml"));
        fileChooser.setInitialDirectory(super.sim.getMapDir());
        fileChooser.setInitialFileName("Karte_xml");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            this.getMap().saveXML(file);
        }
    }

    public void saveXML(File file) {
        this.getMap().saveXML(file);
    }

    public synchronized void loadXML() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Karte öffnen..");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MXML-Datei", "*.mxml"));
        chooser.setInitialDirectory(super.sim.getMapDir());
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            super.map.loadXML(file);
        }
    }

    public void loadXML(File file) {
        this.getMap().loadXML(file);
    }

    public void turnLeft() {
        sim.getSoundController().playSound(SoundController.Sounds.TURNAROUND);
        this.getMap().turnLeft();
    }

    public void turnRight() {
        sim.getSoundController().playSound(SoundController.Sounds.TURNAROUND);
        this.getMap().turnRight();
    }
}
