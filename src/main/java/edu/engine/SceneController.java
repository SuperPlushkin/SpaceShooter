package edu.engine;

import edu.GameScene;
import edu.ui.AuthorScene;
import edu.ui.HighScoreScene;
import edu.ui.MainMenuScene;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    public Stage primary;
    public int WIDTH;
    public int HEIGHT;

    private Scene gameScene;
    private Scene authorScene;
    private Scene highScoreScene;
    private Scene mainMenuScene;

    public void init(Stage stage, int width, int height){
        primary = stage;
        WIDTH = width;
        HEIGHT = height;

        gameScene = new GameScene(this).create();
        authorScene = new AuthorScene(this).create();
        highScoreScene = new HighScoreScene(this).create();
        mainMenuScene = new MainMenuScene(this).create();
    }

    public void set (GameScenes typeScene){
        var scene = switch (typeScene){
            case GameScene -> gameScene;
            case AuthorScene -> authorScene;
            case HighScoreScene -> highScoreScene;
            case MainMenuScene -> mainMenuScene;
        };

        primary.setScene(scene);
        primary.centerOnScreen();
    }
}

