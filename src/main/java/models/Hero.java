package models;

public class Hero {

    private Map map;

    public Hero(Map m) {
        this.map = m;
    }

    @annotations.Invisible
    public void main() {

    }

    public void walk() throws MauerDaException, WaterException {
        this.map.walk();
    }

    public void swim() throws MauerDaException, NoWaterException {
        this.map.swim();
    }

    public void turnLeft() {
        this.map.turnLeft();
    }

    public void turnRight() {
        this.map.turnRight();
    }

    public void takeCoin() throws FullInventoryException {
        this.map.takeCoin();
    }

    public boolean frontIsClear() {
        return this.map.frontIsClear();
    }

    public boolean frontIsWater() {
        return this.map.frontIsWater();
    }

    public boolean isInventoryFull() {
        return this.map.isInventoryFull();
    }

    public boolean isSwimming() {
        return this.map.isSwimming();
    }

    public boolean getCoin() {
        return this.map.getCoin();
    }


}
