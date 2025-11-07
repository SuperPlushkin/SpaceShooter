package edu;

import edu.engine.GameScenes;
import edu.ui.MainMenuScene;
import edu.engine.SceneController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        stage.setTitle("Космо шутер");
        stage.setResizable(false);

        var sceneController = new SceneController();
        sceneController.init(stage, 520, 580); // размер игрового поля
        sceneController.set(GameScenes.MainMenuScene);

        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}