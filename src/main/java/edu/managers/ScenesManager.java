package edu.managers;

import edu.subclasses.GameScenes;
import edu.ui.GameScene;
import edu.subclasses.IScene;
import edu.ui.AuthorScene;
import edu.ui.HighScoreScene;
import edu.ui.MainMenuScene;
import javafx.stage.Stage;

public class ScenesManager {

    public Stage primary;
    public int WIDTH;
    public int HEIGHT;

    private GameScene gameScene;
    private AuthorScene authorScene;
    private HighScoreScene highScoreScene;
    private MainMenuScene mainMenuScene;

    private GameScenes currentScene; // Текущая активная сцена

    public void init(Stage stage, int width, int height){
        primary = stage;
        WIDTH = width;
        HEIGHT = height;

        gameScene = new GameScene(this);
        authorScene = new AuthorScene(this);
        highScoreScene = new HighScoreScene(this);
        mainMenuScene = new MainMenuScene(this);
    }

    public void set (GameScenes typeScene){

        if (typeScene == GameScenes.GameScene && currentScene != GameScenes.GameScene) {
            // Останавливаем игровую сцену, если переключаемся с нее
            gameScene.startGame();
        }
        else if (currentScene == GameScenes.GameScene && typeScene != GameScenes.GameScene) {
            // Запускаем игровую сцену, если переключаемся на нее
            gameScene.stopGame();
        }

        IScene scene = switch (typeScene){
            case GameScene -> gameScene;
            case AuthorScene -> authorScene;
            case HighScoreScene -> highScoreScene;
            case MainMenuScene -> mainMenuScene;
        };

        currentScene = typeScene; // Обновляем текущую сцену
        primary.setScene(scene.getScene());
        primary.centerOnScreen();
    }
}

