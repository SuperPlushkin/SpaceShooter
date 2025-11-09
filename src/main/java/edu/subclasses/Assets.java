package edu.subclasses;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    private static final Map<String, Image> CACHE = new HashMap<>();
    public static Image getImage(String name){
        String extension;
        String pathWithoutExtension;

        int dotIndex = name.lastIndexOf('.');

        if (dotIndex != -1)
        {
            pathWithoutExtension = "/" + name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        }
        else throw new RuntimeException("НЕТ РАСШИРЕНИЯ");

        return CACHE.computeIfAbsent(pathWithoutExtension, key -> {
            InputStream is = Assets.class.getResourceAsStream(pathWithoutExtension + extension);

            if (is == null)
            {
                throw new RuntimeException("НЕ НАЙДЕН ФАЙЛ ПО ПУТИ --> " + pathWithoutExtension + extension);
            }
            else return new Image(is);
        });
    }
}
