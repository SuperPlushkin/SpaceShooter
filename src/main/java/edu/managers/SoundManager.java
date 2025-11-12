package edu.managers;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.net.URL;

public class SoundManager {
    private final Map<String, AudioClip> sfxCache = new HashMap<>();
    private MediaPlayer currentBGM = null;
    private final Random random = new Random();

    public void init() {
        loadSound("player_shoot", "/sfx/shoot_player.mp3");
        loadSound("enemy_shoot1", "/sfx/shoot_enemy1.mp3");
        loadSound("enemy_shoot2", "/sfx/shoot_enemy2.mp3");
        loadSound("player_get_hit", "/sfx/player_get_hit.mp3");
        loadSound("player_death", "/sfx/player_death.mp3");

        // Фразы противников добавлю как-нибудь потом
//        for (int i = 1; i <= 5; i++) {
//            loadSound("phrase_" + i, "/sfx/phrase_" + i + ".wav");
//        }

        for (int i = 1; i <= 7; i++) {
            loadSound("enemy_death_" + i, "/sfx/enemy_death_" + i + ".mp3");
        }
    }

    private URL getResource(String path) {
        return getClass().getResource(path);
    }
    public void loadSound(String name, String path) {
        URL resource = getResource(path);
        if (resource != null) {
            try
            {
                AudioClip clip = new AudioClip(resource.toExternalForm());
                sfxCache.put(name, clip);
            }
            catch (Exception e) {
                System.err.println("Ошибка загрузки AudioClip: " + path + ". Ошибка: " + e.getMessage());
            }
        }
        else System.err.println("Звуковой файл не найден: " + path);
    }

    public void playSFX(String name) {
        AudioClip clip = sfxCache.get(name);

        if (clip != null){
            clip.setVolume(0.2);
            clip.play();
        }
    }
    public void playBGM(String path, int cycleCount) {
        stopBGM();

        URL resource = getResource("/bgm/" + path);
        if (resource != null) {
            try {
                Media media = new Media(resource.toExternalForm());
                currentBGM = new MediaPlayer(media);
                currentBGM.setCycleCount(cycleCount);
                currentBGM.setVolume(0.3);
                currentBGM.play();
            } catch (Exception e) {
                System.err.println("Ошибка загрузки MediaPlayer (BGM): " + path + ". Ошибка: " + e.getMessage());
            }
        }
    }
    public void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBGM.dispose(); // Освобождаем ресурсы
            currentBGM = null;
        }
    }

    public String getRandomPhraseName() {
        int index = random.nextInt(5) + 1;
        return "phrase_" + index;
    }

}