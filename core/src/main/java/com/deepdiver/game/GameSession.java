package com.deepdiver.game;

import com.deepdiver.game.managers.MemoryManager;

/**
 * Глобальная сессия игрока.
 * Хранит ОБЩИЙ счёт за всё время (не за одну игру).
 * Очки копятся, тратятся в магазине, сохраняются между запусками.
 *
 * Статический — потому что счёт один на всю игру, нет смысла создавать объект.
 */
public class GameSession {
    private static int totalScore = 0;

    /**
     * Вызвать один раз при старте приложения.
     * Загружает сохранённый счёт из памяти телефона.
     */
    public static void init() {
        totalScore = MemoryManager.loadTotalScore();
        System.out.println("💰 Общий счёт загружен: " + totalScore);
    }

    /**
     * Добавить очки к общему счёту и сразу сохранить.
     * @param points сколько добавить (100 за синий кристалл, 300 за золотой)
     */
    public static void addScore(int points) {
        totalScore += points;
        MemoryManager.saveTotalScore(totalScore);
        System.out.println("💰 Добавлено " + points + " очков. Всего: " + totalScore);
    }

    public static int getTotalScore() {
        return totalScore;
    }
}
