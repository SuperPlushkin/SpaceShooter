package edu.ui;

import edu.engine.GameScenes;
import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HighScoreScene {

    private final SceneController sceneController;

    public HighScoreScene(SceneController sceneController){
        this.sceneController = sceneController;
    }

    public Scene create(){
        Label title = new Label("Таблица рекордов");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold");
        Button back = new Button("Назад");

        VBox root = new VBox(16,title,back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, sceneController.WIDTH, sceneController.HEIGHT);
        back.setOnAction(e -> sceneController.set(GameScenes.MainMenuScene));
        return scene;
    }
}
