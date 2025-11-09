package edu.subclasses;

public enum PauseType {
    None,               // Игра идет
    JustPause,          // Обычная пауза (по ESC)
    GameOver,           // Игра окончена (проигрыш/победа)
    ShowingLevelResults // Показ промежуточных результатов после уровня
}
