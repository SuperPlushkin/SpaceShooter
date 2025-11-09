package edu.ui;

import edu.subclasses.IScene;
import edu.subclasses.GameScenes;
import edu.managers.ScenesManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HighScoreScene implements IScene {

    private final ScenesManager scenesManager;
    private final Scene MyScene;

    public HighScoreScene(ScenesManager scenesManager){
        this.scenesManager = scenesManager;
        MyScene = createScene();
    }

    public Scene createScene(){
        Label title = new Label("Таблица рекордов");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold");
        Button back = new Button("Назад");

        VBox root = new VBox(16,title,back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, scenesManager.WIDTH, scenesManager.HEIGHT);
        back.setOnAction(e -> scenesManager.set(GameScenes.MainMenuScene));
        return scene;
    }
    public Scene getScene(){
        return MyScene;
    }
}
