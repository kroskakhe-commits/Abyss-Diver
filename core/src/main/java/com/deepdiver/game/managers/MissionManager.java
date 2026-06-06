package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.deepdiver.game.GameSettings;

/**
 * Управляет прогрессом квестов.
 * Запоминает, сколько заданий уже выполнено.
 *
 * Отдельный файл сохранений, чтобы не мешать с остальными настройками.
 */
public class MissionManager {
    private static final Preferences prefs = Gdx.app.getPreferences("DeepDiverMissions");
    private static int completedMissions = 0;

    static {
        loadProgress();
    }

    public static void loadProgress() {
        completedMissions = prefs.getInteger("completedMissions", 0);
        System.out.println("📋 Загружен прогресс миссий: " + completedMissions + "/" + GameSettings.MISSIONS_COUNT);
    }

    public static void saveProgress() {
        prefs.putInteger("completedMissions", completedMissions);
        prefs.flush();
        System.out.println("💾 Сохранён прогресс миссий: " + completedMissions + "/" + GameSettings.MISSIONS_COUNT);
    }

    /**
     * Вызывается, когда игрок выполнил текущее задание.
     */
    public static void completeCurrentMission() {
        if (completedMissions < GameSettings.MISSIONS_COUNT) {
            completedMissions++;
            saveProgress();
            System.out.println("🎉 Выполнена миссия! Всего выполнено: " + completedMissions);
        }
    }

    /**
     * Сброс прогресса — всё начинается заново.
     * Кнопка "СБРОС" на экране квестов.
     */
    public static void resetProgress() {
        completedMissions = 0;
        saveProgress();
        System.out.println("🔄 Прогресс миссий сброшен");
    }

    public static int getCompletedMissions() {
        return completedMissions;
    }

    public static int getCurrentMission() {
        return completedMissions;
    }

    public static boolean isGameComplete() {
        return completedMissions >= GameSettings.MISSIONS_COUNT;
    }

    public static Object[] getCurrentMissionData() {
        int current = getCurrentMission();
        if (current >= GameSettings.MISSIONS_COUNT) {
            return GameSettings.MISSIONS[GameSettings.MISSIONS_COUNT - 1];
        }
        return GameSettings.MISSIONS[current];
    }

    public static int getCurrentMissionType() {
        return (int) getCurrentMissionData()[0];
    }

    public static int getCurrentMissionTarget() {
        return (int) getCurrentMissionData()[1];
    }

    public static String getCurrentMissionDescription() {
        return (String) getCurrentMissionData()[2];
    }
}
