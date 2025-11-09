package edu.subclasses;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/** Отслеживание нажатых клавиш */
public final class Keys {
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void addKey(KeyCode key){
        pressedKeys.add(key);
    }
    public void removeKey(KeyCode key){
        pressedKeys.remove(key);
    }
    public void clearKeys(){
        pressedKeys.clear();
    }
    public boolean isDown(KeyCode code){
        return pressedKeys.contains(code);
    }
}
