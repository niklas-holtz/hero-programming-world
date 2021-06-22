package controller;

import controller.program.Program;
import javafx.application.Application;
import javafx.stage.Stage;
import models.Map;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Map map = new Map();
        new HeroSimulatorStage(map, new Program("Untitled"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
