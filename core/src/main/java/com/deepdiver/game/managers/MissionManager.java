package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.deepdiver.game.GameSettings;

/**
 * Управление прогрессом выполнения заданий (квестов).
 * Хранит количество выполненных заданий и предоставляет методы для их завершения и сброса.
 */
public class MissionManager {
    private static final Preferences prefs = Gdx.app.getPreferences("DeepDiverMissions");
    private static int completedMissions = 0;

    static {
        loadProgress();
    }

    public static void loadProgress() {
        completedMissions = prefs.getInteger("completedMissions", 0);
    }

    public static void saveProgress() {
        prefs.putInteger("completedMissions", completedMissions);
        prefs.flush();
    }

    /**
     * Завершает текущее задание и увеличивает счётчик выполненных.
     */
    public static void completeCurrentMission() {
        if (completedMissions < GameSettings.MISSIONS_COUNT) {
            completedMissions++;
            saveProgress();
        }
    }

    /**
     * Сбрасывает прогресс заданий. Используется кнопкой сброса.
     */
    public static void resetProgress() {
        completedMissions = 0;
        saveProgress();
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
