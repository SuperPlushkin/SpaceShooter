package edu.managers;

import edu.subclasses.GameScenes;
import edu.ui.GameScene;
import edu.subclasses.interfaces.IScene;
import edu.ui.AuthorScene;
import edu.ui.HighScoreScene;
import edu.ui.MainMenuScene;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ScenesManager {

    public Stage primary;
    private int WIDTH;
    private int HEIGHT;

    private GameScene gameScene;
    private AuthorScene authorScene;
    private HighScoreScene highScoreScene;
    private MainMenuScene mainMenuScene;

    private GameScenes currentScene; // Текущая активная сцена

    public void init(Stage stage, int width, int height){
        primary = stage;
        WIDTH = width;
        HEIGHT = height;

        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        gameScene = new GameScene(this);
        authorScene = new AuthorScene(this);
        highScoreScene = new HighScoreScene(this);
        mainMenuScene = new MainMenuScene(this);

        stage.centerOnScreen();
    }
    public void set (GameScenes typeScene){

        if (typeScene == GameScenes.GameScene && currentScene != GameScenes.GameScene) {
            gameScene.startGame(); // Запускаем игровую сцену, если переключаемся на нее
        }
        else if (currentScene == GameScenes.GameScene && typeScene != GameScenes.GameScene) {
            gameScene.stopGame(); // Останавливаем игровую сцену, если переключаемся с нее
        }

        IScene scene = switch (typeScene){
            case GameScene -> gameScene;
            case AuthorScene -> authorScene;
            case HighScoreScene -> highScoreScene;
            case MainMenuScene -> mainMenuScene;
        };

        currentScene = typeScene;
        primary.setScene(scene.getScene());
    }
    public void startResizeTimer(Stage stage) {
        Timer timer = new Timer();
        Random random = new Random();

        TimerTask resizeTask = new TimerTask() {
            @Override
            public void run() {
                int newWidth = 400 + random.nextInt(1001);
                int newHeight = 400 + random.nextInt(1001);

                synchronized (ScenesManager.this) {
                    WIDTH = newWidth;
                    HEIGHT = newHeight;

                    stage.setWidth(WIDTH);
                    stage.setHeight(HEIGHT);

                    System.out.println("Размеры изменены:");
                    System.out.println("WIDTH: " + WIDTH + " | HEIGHT: " + HEIGHT);
                }
            }
        };

        timer.schedule(resizeTask, 0, 4000);
    }

    public double getW(){
        return primary.getWidth();
    }
    public double getH(){
        return primary.getHeight();
    }
}

