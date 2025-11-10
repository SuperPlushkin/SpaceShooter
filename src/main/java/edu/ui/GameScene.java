package edu.ui;

import edu.subclasses.interfaces.IScene;
import edu.subclasses.GameScenes;
import edu.subclasses.classes.Keys;
import edu.managers.ScenesManager;
import edu.managers.LevelsManager;
import edu.subclasses.classes.StylesHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameScene implements IScene {

    private final ScenesManager scenesManager;
    private final Scene MyScene;
    private GraphicsContext g;

    private final LevelsManager levelsManager;
    private final Keys pressedKeys = new Keys();

    private Label LivesPlayer;
    private Label hpPlayer;
    private Label LevelTitle;
    private Label KilledCount;
    private Label TimerLabel;

    private enum ActiveMenu { NONE, PAUSE, LEVEL_COMPLETE, LOSE, WIN }
    private VBox pauseMenu;
    private VBox loseMenu;
    private VBox winMenu;
    private VBox levelCompleteMenu;

    private Label levelCompleteTitle;
    private Label levelCompleteStatsLabel;
    private Label loseStatsLabel;
    private Label winStatsLabel;

    public GameScene(ScenesManager scenesManager) {
        this.scenesManager = scenesManager;

        MyScene = createScene();
        // ВАЖНО: точка 'g' инициализируется внутри createScene(), поэтому LevelsManager должен быть создан после него.
        levelsManager = new LevelsManager(getW() / 2.0, getH() - 140, g, this);
    }

    private Scene createScene() {
        Canvas canvas = new Canvas(getW(), getH());
        g = canvas.getGraphicsContext2D();
        g.setImageSmoothing(true);

        // Полоса с информацией об игроке и уровне
        var infoTopBox = createTopInfoBar();

        // Инициализация всех меню
        levelCompleteMenu = makeCompletedLevelMenu();
        loseMenu = makeLoseMenu();
        winMenu = makeWinMenu();
        pauseMenu = makePauseMenu();

        // Инициализации самой сцены
        StackPane root = new StackPane(canvas, infoTopBox, pauseMenu, levelCompleteMenu, loseMenu, winMenu);
        Scene scene = new Scene(root, getW(), getH(), Color.WHITE);

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        // обработчик нажатий клавиш
        scene.setOnKeyPressed(e -> {
            var key = e.getCode();
            pressedKeys.addKey(key);
            if (key == KeyCode.ESCAPE)
                levelsManager.togglePause();
        });
        scene.setOnKeyReleased(e -> pressedKeys.removeKey(e.getCode()));

        return scene;
    }
    private HBox createTopInfoBar(){
        // Убийства
        Label labelKilled = new Label("Убито:");
        KilledCount = new Label("0");
        KilledCount.setPadding(new Insets(0, 10, 0, 0));

        // Время
        Label labelTime = new Label("Время:");
        TimerLabel = new Label("00:00");
        TimerLabel.setPadding(new Insets(0, 10, 0, 0));

        // Уровень
        Label labelTitle = new Label("Уровень:");
        LevelTitle = new Label("");
        LevelTitle.setPadding(new Insets(0, 20, 0, 0));

        // Жизни и ХП
        Label labelLives = new Label("Жизни:");
        LivesPlayer = new Label("");
        LivesPlayer.setPadding(new Insets(0, 10, 0, 0));
        Label labelHP = new Label("Hp:");
        hpPlayer = new Label("");

        // Разделяем информацию на две части для выравнивания
        HBox leftStats = new HBox(6, labelLives, LivesPlayer, labelHP, hpPlayer);
        HBox rightStats = new HBox(6, labelKilled, KilledCount, labelTime, TimerLabel, labelTitle, LevelTitle);

        // Это короче хрень, которая растягивается по размеру оставшемуся
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Главный HBox: Левая часть | хрень | Правая часть
        HBox infoTopBox = new HBox(10, leftStats, spacer, rightStats);
        infoTopBox.setAlignment(Pos.TOP_LEFT);
        infoTopBox.setPadding(new Insets(10));
        infoTopBox.setVisible(true);

        return infoTopBox;
    }
    public VBox makeCompletedLevelMenu(){

        levelCompleteTitle = new Label("Уровень пройден!");
        StylesHelper.setLabelStyle(levelCompleteTitle, "-fx-text-fill: #90EE90;");

        levelCompleteStatsLabel = new Label(); // Для отображения статистики
        StylesHelper.setSubLabelStyle(levelCompleteStatsLabel);

        Button nextLevelBtn = new Button("Продолжить");

        StylesHelper.addBaseHoverPressEffects(nextLevelBtn);

        VBox levelCompleteMenu = new VBox(15, levelCompleteTitle, levelCompleteStatsLabel, nextLevelBtn);
        StylesHelper.setMenuStyle(levelCompleteMenu);

        setMenuVisibility(levelCompleteMenu, false);

        nextLevelBtn.setOnAction(e -> {
            levelsManager.goToNextLevel();
            setActiveMenu(ActiveMenu.NONE);
        });

        return levelCompleteMenu;
    }
    public VBox makeLoseMenu(){

        Label gameOverTitle = new Label("Проклятые повстанцы\nпобедили!");
        StylesHelper.setLabelStyle(gameOverTitle, "-fx-text-fill: red;");

        loseStatsLabel = new Label(); // Для отображения статистики
        StylesHelper.setSubLabelStyle(loseStatsLabel);

        Button restartBtn = new Button("Начать с первого уровня");
        Button toMenuBtn = new Button("Выйти в меню");

        StylesHelper.addBaseHoverPressEffects(restartBtn);
        StylesHelper.addBaseHoverPressEffects(toMenuBtn);

        VBox gameOverMenu = new VBox(15, gameOverTitle, loseStatsLabel, restartBtn, toMenuBtn);
        StylesHelper.setMenuStyle(gameOverMenu);

        setMenuVisibility(gameOverMenu, false);

        restartBtn.setOnAction(e -> startGame());
        toMenuBtn.setOnAction(e -> scenesManager.set(GameScenes.MainMenuScene));

        return gameOverMenu;
    }
    public VBox makeWinMenu(){

        Label winTitle = new Label("Игра пройдена!");
        StylesHelper.setLabelStyle(winTitle, "-fx-text-fill: #FFD700;"); // яркий желтый/золотой цвет

        winStatsLabel = new Label(); // Для отображения статистики
        StylesHelper.setSubLabelStyle(winStatsLabel);

        Button restartBtn = new Button("Начать с первого уровня");
        Button toMenuBtn = new Button("В главное меню");

        StylesHelper.addBaseHoverPressEffects(restartBtn);
        StylesHelper.addBaseHoverPressEffects(toMenuBtn);

        VBox menu = new VBox(15, winTitle, winStatsLabel, restartBtn, toMenuBtn);
        StylesHelper.setMenuStyle(menu);

        setMenuVisibility(menu, false);

        restartBtn.setOnAction(e -> startGame());
        toMenuBtn.setOnAction(e -> scenesManager.set(GameScenes.MainMenuScene));

        return menu;
    }
    public VBox makePauseMenu(){

        Label pauseTitle = new Label("Пауза");
        StylesHelper.setLabelStyle(pauseTitle, "-fx-text-fill: white;");

        Button resumeBtn = new Button("Продолжить");
        Button restartBtn = new Button("Повторить");
        Button toMenuBtn = new Button("Выйти в меню");

        StylesHelper.addBaseHoverPressEffects(resumeBtn);
        StylesHelper.addBaseHoverPressEffects(restartBtn);
        StylesHelper.addBaseHoverPressEffects(toMenuBtn);

        VBox pauseMenu = new VBox(10, pauseTitle, resumeBtn, restartBtn, toMenuBtn);
        StylesHelper.setMenuStyle(pauseMenu);

        setMenuVisibility(pauseMenu, false);

        resumeBtn.setOnAction(e -> levelsManager.togglePause());
        restartBtn.setOnAction(e -> startGame());
        toMenuBtn.setOnAction(e -> scenesManager.set(GameScenes.MainMenuScene));

        return pauseMenu;
    }
    private void setActiveMenu(ActiveMenu activeMenu) {
        setMenuVisibility(pauseMenu, activeMenu == ActiveMenu.PAUSE);
        setMenuVisibility(loseMenu, activeMenu == ActiveMenu.LOSE);
        setMenuVisibility(levelCompleteMenu, activeMenu == ActiveMenu.LEVEL_COMPLETE);
        setMenuVisibility(winMenu, activeMenu == ActiveMenu.WIN);
    }
    private void setMenuVisibility(VBox menu, boolean visible) {
        if (menu != null) {
            menu.setVisible(visible);
            menu.setMouseTransparent(!visible);
        }
    }

    public void startGame() {
        resetGame();
        levelsManager.startGame();
    }
    private void resetGame() {
        updatePlayerInfo(0, 0);
        setActiveMenu(ActiveMenu.NONE);
    }
    public void stopGame() {
        levelsManager.stopGame();
        setActiveMenu(ActiveMenu.PAUSE);
    }

    public void onPauseChange(boolean pause) {
        setActiveMenu(pause ? ActiveMenu.PAUSE : ActiveMenu.NONE);
    }

    public void updateStatsInfo(int killed, double totalTime) {
        KilledCount.setText(Integer.toString(killed));
        TimerLabel.setText(formatTime(totalTime));
    }
    public void updatePlayerInfo(int lives, int hp) {
        LivesPlayer.setText(Integer.toString(lives));
        hpPlayer.setText(Integer.toString(hp));
    }
    public void updateLevelTitle(int levelIndex, int totalLevels) {
        LevelTitle.setText((levelIndex + 1) + " / " + totalLevels);
    }

    public void renderLevelComplete(String levelCompletedInfo, int activeLevel, double totalTime, double levelTime) {
        setStatisticsLabel(levelCompletedInfo, totalTime, levelTime, levelCompleteStatsLabel);

        levelCompleteTitle.setText("Уровень " + activeLevel + " пройден!");

        setActiveMenu(ActiveMenu.LEVEL_COMPLETE);
    }
    public void renderLoseGame(String levelCompletedInfo, double totalTime, double levelTime) {
        setStatisticsLabel(levelCompletedInfo, totalTime, levelTime, loseStatsLabel);

        setActiveMenu(ActiveMenu.LOSE);
    }
    public void renderWinGame(String levelCompletedInfo, double totalTime, double levelTime) {
        setStatisticsLabel(levelCompletedInfo, totalTime, levelTime, winStatsLabel);

        setActiveMenu(ActiveMenu.WIN);
    }

    public Keys getPressedKeys(){
        return pressedKeys;
    }
    public void clearPressedKeys() {
        pressedKeys.clearKeys();
    }

    public Scene getScene(){
        return MyScene;
    }

    public double getW() {
        return scenesManager.getW();
    }
    public double getH() {
        return scenesManager.getH();
    }

    private static String formatTime(double timeSeconds) {
        long totalSeconds = (long) timeSeconds;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        // Вычисляем сотые доли секунды (centiseconds)
        double fractional = timeSeconds - totalSeconds;
        long centiseconds = (long) (fractional * 100);

        // Защита от ошибок округления, когда fractional очень близко к 1.0
        if (centiseconds >= 100) {
            centiseconds = 99;
        }

        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
    }
    private static void setStatisticsLabel(String levelCompletedInfo, double totalTime, double levelTime, Label statsLabel) {
        var stats = String.format(
            """
            Общее время: %s
            Время уровня: %s
            Пройдено уровней: %s
            """,
            formatTime(totalTime), formatTime(levelTime), levelCompletedInfo
        );

        statsLabel.setText(stats);
    }
}