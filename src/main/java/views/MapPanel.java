package views;

import controller.map.MapController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import util.observer.ReverseObservable;
import util.observer.ReverseObserver;

public class MapPanel extends Region implements ReverseObserver {

    Image coin;
    Image wall;
    Image[] hero;
    private Canvas canvas;
    private GraphicsContext gc;
    private ScrollPane sc;
    private int pixelSize = 32;
    private MapController mapController;

    public MapPanel(MapController mapController, ScrollPane sc) {
        this.mapController = mapController;
        this.mapController.getMap().addObserver(this);
        this.sc = sc;

        init();
        draw();
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public int getPixelSize() {
        return this.pixelSize;
    }


    private void init() {
        canvas = new Canvas();
        sc.widthProperty().addListener(evt -> update(null, null));
        sc.heightProperty().addListener(evt -> update(null, null));


        this.getChildren().add(this.canvas);
        gc = canvas.getGraphicsContext2D();
    }

    private void resizeCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int cWidth = this.mapController.getMap().getColls() * pixelSize;
        int cHeight = this.mapController.getMap().getRows() * pixelSize;
        canvas.setHeight(cHeight);
        canvas.setWidth(cWidth);
        if (cWidth >= sc.getWidth()) {
            if (canvas.getLayoutX() != 0) canvas.setLayoutX(0);
        } else {
            int k = (int) (sc.getWidth() - (int) cWidth) / 2;
            if (canvas.getLayoutX() != k) canvas.setLayoutX(k);
        }

        if (cHeight >= sc.getHeight()) {
            if (canvas.getLayoutY() != 0) canvas.setLayoutY(0);
        } else {
            int k = (int) (sc.getHeight() - cHeight) / 2;
            if (canvas.getLayoutY() != k) canvas.setLayoutY(k);
        }
    }

    public void draw() {
        resizeCanvas();
        for (int x = 0; x < this.mapController.getMap().getRows(); x++) {
            for (int y = 0; y < this.mapController.getMap().getColls(); y++) {
                int xPos = y * pixelSize;
                int yPos = x * pixelSize;
                if (gc.getGlobalAlpha() != 1) gc.setGlobalAlpha(1f);

                //grass
                gc.setFill(Color.LIGHTGREEN);
                gc.fillRect(xPos, yPos, pixelSize, pixelSize);

                switch (this.mapController.getMap().getTile(y, x)) {
                    case -1:    //swim tile
                        gc.setFill(Color.LIGHTBLUE);
                        gc.fillRect(xPos, yPos, pixelSize, pixelSize);
                        break;
                    case 1:        //mauer
                        gc.drawImage(loadImage("/world/wall_32.png"), xPos, yPos);
                        break;
                    case 2:        //coin
                        gc.drawImage(loadImage("/world/coin_32.png"), xPos, yPos);
                        break;
                }

                //draw hero
                if (this.mapController.getMap().heroIsOnPos(y, x)) {
                    Image heroImg = loadImage("/character/Hero_" + this.mapController.getMap().getDir().substring(0, 1) + "_32.png");
                    gc.drawImage(heroImg, xPos, yPos);
                }
            }
        }
    }

    public void drawHeroOnPos(int xPos, int yPos) {
        draw();
        Image heroImg = loadImage("/character/Hero_" + this.mapController.getMap().getDir().substring(0, 1) + "_32.png");
        gc.setGlobalAlpha(0.5f);
        gc.drawImage(heroImg, xPos * pixelSize, yPos * pixelSize);
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (NullPointerException e) {
            System.err.println("Bild nicht gefunden! Pfad: " + path);
            return null;
        }
    }

    @Override
    public void update(ReverseObservable o, Object arg) {
        if (Platform.isFxApplicationThread()) {

            draw();
        } else {
            Platform.runLater(this::draw);
        }
    }
}
