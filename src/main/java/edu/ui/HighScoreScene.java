package edu.ui;

import edu.subclasses.classes.StylesHelper;
import edu.subclasses.interfaces.IScene;
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
        Label title = new Label("Таблица рекордов\n(Когда рак свистнет)");

        StylesHelper.setLabelStyle(title, "-fx-text-fill: #FFD700; -fx-padding: 80 0 20 0;");

        Button back = new Button("Назад");

        StylesHelper.addBaseHoverPressEffects(back);

        VBox root = new VBox(16,title,back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle(
            "-fx-background-image: url('background_stars-for-wars.jpg'); " +
            "-fx-background-size: cover; " +
            "-fx-background-position: center center;"
        );

        Scene scene = new Scene(root, scenesManager.getW(), scenesManager.getH());
        back.setOnAction(e -> scenesManager.set(GameScenes.MainMenuScene));
        return scene;
    }
    public Scene getScene(){
        return MyScene;
    }
}
