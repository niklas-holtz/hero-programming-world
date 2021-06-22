package controller.simulation;

import java.util.Observable;

import controller.HeroSimulatorStage;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

public class SimulationManager extends Observable {

    public volatile int MAX_SPEED = 40;
    private volatile double currentSpeed;
    private Simulation sim;
    private final HeroSimulatorStage stage;
    private final Button start, stop, pause;
    private final MenuItem m_start, m_stop, m_pause;
    private SimulationState state;

    public SimulationManager(HeroSimulatorStage stage) {
        this.stage = stage;
        this.start = this.stage.getT_play();
        this.stop = this.stage.getT_stop();
        this.pause = this.stage.getT_pause();
        this.m_start = this.stage.getStart();
        this.m_pause = this.stage.getPause();
        this.m_stop = this.stage.getStop();
        this.setState(SimulationState.not_running);
    }

    public void run() {
        if (this.getState() == SimulationState.not_running) {
            this.sim = new Simulation(this, this.stage.getMapController().getMap());
            this.sim.start();
        } else if (this.getState() == SimulationState.pause) {
            this.sim.resumeSimulation();
        }
        this.setState(SimulationState.running);

    }

    public void pause() {
        sim.pauseSimulation();
        this.setState(SimulationState.pause);
    }

    public void stop() {
        try {
            sim.stopSimulation();
            sim = null;
        } catch (NullPointerException e) {
        }
    }

    public synchronized void setCurrentSpeed(double value) {
        this.currentSpeed = value;
    }

    public synchronized double getCurrentSpeed() {
        return this.currentSpeed;
    }

    public void setState(SimulationState state) {
        this.state = state;
        switch (state.name()) {
            case ("not_running"):
                this.start.setDisable(false);
                this.pause.setDisable(true);
                this.stop.setDisable(true);
                this.m_start.setDisable(false);
                this.m_pause.setDisable(true);
                this.m_stop.setDisable(true);
                this.stage.setDisabledToggleGroups(false);
                this.stage.setInteractiveMenuItemsDisabled(false);
                break;
            case ("running"):
                this.start.setDisable(true);
                this.pause.setDisable(false);
                this.stop.setDisable(false);
                this.m_start.setDisable(true);
                this.m_pause.setDisable(false);
                this.m_stop.setDisable(false);
                this.stage.setDisabledToggleGroups(true);
                this.stage.setInteractiveMenuItemsDisabled(true);
                break;
            case ("pause"):
                this.start.setDisable(false);
                this.pause.setDisable(true);
                this.stop.setDisable(false);
                this.m_start.setDisable(false);
                this.m_pause.setDisable(true);
                this.m_stop.setDisable(false);
                this.stage.setDisabledToggleGroups(false);
                this.stage.setInteractiveMenuItemsDisabled(false);
                break;
        }
    }

    public SimulationState getState() {
        return this.state;
    }
}
