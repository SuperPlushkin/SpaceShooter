package edu;

import edu.subclasses.GameScenes;
import edu.managers.ScenesManager;
import edu.subclasses.classes.Assets;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        stage.setTitle("STAR WARS (Kirill Edition)");
        stage.getIcons().add(Assets.getImage("LOGO_DEATH_STAR.png"));
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);

        var sceneController = new ScenesManager();
        sceneController.init(stage, 520, 580); // размер игрового поля
        sceneController.set(GameScenes.MainMenuScene);

        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}