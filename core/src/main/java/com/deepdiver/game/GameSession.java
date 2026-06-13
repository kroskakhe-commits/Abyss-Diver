package com.deepdiver.game;

import com.deepdiver.game.managers.MemoryManager;

/**
 * Глобальная сессия игрока.
 * Хранит общий счёт, накопленный за всё время игры.
 * Счёт используется для покупки скинов и сохраняется между запусками.
 */
public class GameSession {
    private static int totalScore = 0;

    /**
     * Инициализирует сессию, загружая сохранённый счёт.
     * Должен быть вызван при старте приложения.
     */
    public static void init() {
        totalScore = MemoryManager.loadTotalScore();
    }

    /**
     * Добавляет очки к общему счёту и сохраняет изменения.
     * @param points количество добавляемых очков
     */
    public static void addScore(int points) {
        totalScore += points;
        MemoryManager.saveTotalScore(totalScore);
    }

    public static int getTotalScore() {
        return totalScore;
    }
}
