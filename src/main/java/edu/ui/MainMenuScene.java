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

public class MainMenuScene implements IScene {

    private final Scene MyScene;
    private final ScenesManager scenesManager;

    public MainMenuScene(ScenesManager scenesManager){
        this.scenesManager = scenesManager;
        MyScene = create();
    }

    public Scene create() {
        Label title = new Label("STAR WARS\n(Kirill Edition)");

        StylesHelper.setLabelStyle(title, "-fx-text-fill: #FFD700; -fx-padding: 80 0 20 0;");

        Button start = new Button("СТАРТ");
        Button score = new Button("РЕКОРДЫ");
        Button author = new Button("АВТОР");
        Button exit = new Button("ВЫХОД");

        StylesHelper.addBaseHoverPressEffects(start);
        StylesHelper.addBaseHoverPressEffects(score);
        StylesHelper.addBaseHoverPressEffects(author);
        StylesHelper.addBaseHoverPressEffects(exit);

        start.setMaxWidth(200);
        score.setMaxWidth(200);
        author.setMaxWidth(200);
        exit.setMaxWidth(200);

        VBox box = new VBox(16, title, start, score, author, exit);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle(
            "-fx-background-image: url('background_stars-for-wars.jpg'); " +
            "-fx-background-size: cover; " +
            "-fx-background-position: center center;"
        );

        Scene scene = new Scene(box, scenesManager.getW(), scenesManager.getH());
        start.setOnAction(e -> scenesManager.set(GameScenes.GameScene));
        score.setOnAction(e -> scenesManager.set(GameScenes.HighScoreScene));
        author.setOnAction(e -> scenesManager.set(GameScenes.AuthorScene));
        exit.setOnAction(e -> System.exit(0));

        return scene;
    }

    public Scene getScene() {
        return MyScene;
    }
}
