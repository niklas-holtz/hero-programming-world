package controller.simulation;

import models.Map;
import util.observer.ReverseObservable;
import util.observer.ReverseObserver;

public class Simulation extends Thread implements ReverseObserver {

    private final SimulationManager manager;
    private final Map map;
    private volatile boolean pause;
    private final Object synchronizedObject;

    Simulation(SimulationManager manager, Map map) {
        this.manager = manager;
        this.map = map;
        this.pause = false;
        this.synchronizedObject = map;
        this.map.addObserver(this);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            this.map.getHero().main();
        } catch (RuntimeException e) {
            //simulation stopped
        } finally {
            this.manager.setState(SimulationState.not_running);
            this.map.deleteObserver(this);
        }
    }

    @Override
    public void update(ReverseObservable o, Object arg) {
        try {
            Thread.sleep((long) (510 - manager.getCurrentSpeed()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (this.synchronizedObject) {
            while (this.pause) {
                try {
                    this.synchronizedObject.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void pauseSimulation() {
        synchronized (this.synchronizedObject) {
            pause = true;
        }
        this.map.deleteObserver(this);
    }

    public void resumeSimulation() {
        synchronized (this.synchronizedObject) {
            pause = false;
            this.synchronizedObject.notifyAll();
        }
        this.map.addObserver(this);
    }

    public void stopSimulation() {
        synchronized (this.synchronizedObject) {
            pause = false;
        }
        this.interrupt();
    }
}
