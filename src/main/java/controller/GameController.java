package controller;

import controller.language.LanguageController;
import models.Map;
import views.MapPanel;

public abstract class GameController {
    // Basic methods for MouseController, MapController

    protected Map map;
    protected MapPanel mapPanel;
    protected HeroSimulatorStage sim;
    protected LanguageController lang;

    public GameController(Map m, MapPanel mp, HeroSimulatorStage sim) {
        this.map = m;
        this.mapPanel = mp;
        this.sim = sim;
        this.lang = sim.getLanguageController();
    }

    public GameController(Map m, MapPanel mp) {
        this.map = m;
        this.mapPanel = mp;
    }

    public GameController(Map m) {
        this.map = m;
    }

    public GameController(HeroSimulatorStage sim, Map m) {
        this.map = m;
        this.sim = sim;
        this.lang = sim.getLanguageController();
    }

    public GameController(HeroSimulatorStage sim) {
        this.sim = sim;
        this.lang = sim.getLanguageController();
    }

    public Map getMap() {
        return this.map;
    }

    public MapPanel getMapPanel() {
        return this.mapPanel;
    }

    public HeroSimulatorStage getStage() {
        return this.sim;
    }
}
