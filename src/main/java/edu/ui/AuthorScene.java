package edu.ui;

import edu.subclasses.IScene;
import edu.subclasses.GameScenes;
import edu.managers.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AuthorScene implements IScene {

    private final SceneController sceneController;
    private final Scene MyScene;

    public AuthorScene(SceneController sceneController){
        this.sceneController = sceneController;
        MyScene = createScene();
    }

    public Scene createScene(){
        Label title = new Label("Автор: Великолепный Кирилл");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");
        Button back = new Button("Назад");

        VBox root = new VBox(16,title,back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, sceneController.WIDTH, sceneController.HEIGHT);
        back.setOnAction(e -> sceneController.set(GameScenes.MainMenuScene));
        return scene;
    }
    public Scene getScene(){
        return MyScene;
    }
}
