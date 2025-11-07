package edu;

import edu.engine.Keys;
import edu.engine.SceneController;
import edu.game.Bullet;
import edu.game.Player;
import edu.managers.BulletsManager;
import edu.managers.EnemiesManager;
import edu.ui.MainMenuScene;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;


public class GameScene implements IShootHandler, IGameActionsHandler {

    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;

    private boolean isGameOver = false;

    private final Keys keys = new Keys();
    private AnimationTimer loop;
    private boolean paused = false;

    private final Player player = new Player(W / 2.0, H - 140, this);
    private final EnemiesManager enemiesManager = new EnemiesManager(this);
    private final BulletsManager bulletsManager = new BulletsManager();

    private Label LifesPlayer;
    private Label hpPlayer;
    private VBox gameOver;

    public Scene create (){
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setImageSmoothing(true);

        // жизни и хп
        Label labelLifes = new Label("Жизни:");
        LifesPlayer = new Label(Integer.toString(player.getLives()));
        LifesPlayer.setPadding(new Insets(0, 10, 0, 0));
        Label labelHP = new Label("Hp:");
        hpPlayer = new Label(Integer.toString(player.getHp()));
        HBox lifesBox = new HBox(6, labelLifes, LifesPlayer, labelHP, hpPlayer);
        lifesBox.setAlignment(Pos.TOP_LEFT);
        lifesBox.setPadding(new Insets(10));
        lifesBox.setVisible(true);

        // оверлей проигрыша
        Label loh = new Label("Вы проиграли 🤣");
        loh.setPadding(new Insets(10));
        loh.setStyle("-fx-background-color: #ff4444");
        Button toMenu = new Button("Выйти в меню");
        gameOver = new VBox(12, loh, toMenu);
        gameOver.setAlignment(Pos.CENTER);
        gameOver.setPadding(new Insets(10));
        gameOver.setVisible(false);
        gameOver.setMouseTransparent(true);

        // оверлей паузы
        Button resume = new Button("Продолжить");
        Button toMenu2 = new Button("Выйти в меню");
        VBox overlay = new VBox(12, resume, toMenu2);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(10));
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        StackPane root = new StackPane(canvas, lifesBox, overlay, gameOver);
        Scene scene = new Scene(root, W, H, Color.WHITE);

        keys.attach(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e-> {
            if (e.getCode() == KeyCode.ESCAPE && !isGameOver) {
                paused = !paused;
                overlay.setVisible(paused);
                overlay.setMouseTransparent(!paused);
            }
        });
        resume.setOnAction(e-> {
            paused = false;
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
        });
        toMenu.setOnAction(e -> SceneController.set(new MainMenuScene().create()));
        toMenu2.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        // 👇 создаём врагов в начале сцены
        enemiesManager.spawnEnemies();

        // 👇 создаем обработчик сцены
        makeUpdateTimer(g);

        return scene;
    }

    private void render (GraphicsContext g){
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, W, H);

        enemiesManager.renderEnemies(g);
        bulletsManager.renderBullets(g);
        player.render(g);
    }
    private void makeUpdateTimer (GraphicsContext g){
        loop = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if (prev == 0){
                    prev = now;
                    return;
                }
                double dt = Math.min((now - prev) / 1_000_000_000.0, 0.05);
                prev = now;

                if (!paused){
                    player.update(dt, now, keys);
                    enemiesManager.updateEnemies(dt, W);
                    bulletsManager.updateBullets(dt);

                    checkBulletCollision();
                }
                render(g);
            }
        };

        loop.start();
    }

    public void makeShoot(double x, double y, double vy, boolean byPlayer){
        bulletsManager.makeBullet(x, y, vy, byPlayer);
    }
    public void checkBulletCollision(){
        var bullets = bulletsManager.getBullets();
        var bulletsToDelete = new ArrayList<Integer>();

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            if (enemiesManager.checkBulletCollision(bullet)) {
                bulletsToDelete.add(i);
                continue;
            }

            if (player.checkBulletCollision(bullet)) {
                bulletsToDelete.add(i);
                LifesPlayer.setText(Integer.toString(player.getLives()));
                hpPlayer.setText(Integer.toString(player.getHp()));
            }
        }

        for (int i = bulletsToDelete.size() - 1; i >= 0; i--) {
            int index = bulletsToDelete.get(i);

            if(index < bullets.size()){
                bullets.remove(index);
            }
        }
    }

    public void restartLastLevel() {
        bulletsManager.getBullets().clear();
        enemiesManager.spawnEnemies();
    }
    public void endGame() {
        isGameOver = true;
        paused = true;
        gameOver.setVisible(true);
        gameOver.setMouseTransparent(false);
    }
}
