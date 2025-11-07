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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class GameScene implements IShootHandler {

    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;

    private final Keys keys = new Keys();
    private AnimationTimer loop;
    private boolean paused = false;

    private final Player player = new Player(W / 2.0, H - 140, this);
    private final EnemiesManager enemiesManager = new EnemiesManager(this);
    private final BulletsManager bulletsManager = new BulletsManager();

    public Scene create (){
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setImageSmoothing(true);

        // оверлей паузы
        Button resume = new Button("Продолжить");
        Button toMenu = new Button("Выйти в меню");
        VBox overlay = new VBox(12, resume, toMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(10));
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        StackPane root = new StackPane(canvas, overlay);
        Scene scene = new Scene(root, W, H, Color.WHITE);

        keys.attach(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e-> {
            if (e.getCode() == KeyCode.ESCAPE) {
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
        toMenu.setOnAction(e-> SceneController.set(new MainMenuScene().create()));

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

        for (int bullet_index = 0; bullet_index < bullets.size(); bullet_index++){

            Bullet bullet = bullets.get(bullet_index);

            if (enemiesManager.checkBulletCollision(bullet) || player.checkBulletCollision(bullet)) {
                bullets.remove(bullet_index);
            }
        }

    }
}
