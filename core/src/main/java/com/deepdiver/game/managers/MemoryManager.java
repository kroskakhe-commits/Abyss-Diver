package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;

/**
 * Всё, что нужно сохранить между запусками, хранится здесь.
 * Используем Preferences — это как мини-БД, встроенная в LibGDX.
 *
 * Ключи называем константами, чтобы не ошибиться в строках.
 */
public class MemoryManager {
    private static final Preferences preferences = Gdx.app.getPreferences("DeepDiver");

    private static final String TOTAL_SCORE_KEY = "totalScore";
    private static final String UNLOCKED_SKINS_KEY = "unlockedSkins";
    private static final String EQUIPPED_SKIN_KEY = "equippedSkin";
    private static final String SOUND_ON_KEY = "isSoundOn";
    private static final String MUSIC_ON_KEY = "isMusicOn";
    private static final String RECORD_TABLE_KEY = "recordTable";

    // ========== НАСТРОЙКИ ЗВУКА ==========
    public static void saveSoundSettings(boolean isOn) {
        preferences.putBoolean(SOUND_ON_KEY, isOn);
        preferences.flush();  // Без flush() изменения могут не записаться!
    }

    public static boolean loadIsSoundOn() {
        if (!preferences.contains(SOUND_ON_KEY)) return true;  // По умолчанию звук включён
        return preferences.getBoolean(SOUND_ON_KEY, true);
    }

    public static void saveMusicSettings(boolean isOn) {
        preferences.putBoolean(MUSIC_ON_KEY, isOn);
        preferences.flush();
    }

    public static boolean loadIsMusicOn() {
        if (!preferences.contains(MUSIC_ON_KEY)) return true;
        return preferences.getBoolean(MUSIC_ON_KEY, true);
    }

    // ========== ТАБЛИЦА РЕКОРДОВ (ТОП-5) ==========
    public static void saveTableOfRecords(ArrayList<Integer> table) {
        Json json = new Json();
        String tableInString = json.toJson(table);
        preferences.putString(RECORD_TABLE_KEY, tableInString);
        preferences.flush();
    }

    public static ArrayList<Integer> loadRecordsTable() {
        if (!preferences.contains(RECORD_TABLE_KEY)) return null;
        String scores = preferences.getString(RECORD_TABLE_KEY);
        Json json = new Json();
        return json.fromJson(ArrayList.class, scores);
    }

    /**
     * Добавляет результат в таблицу, оставляет только 5 лучших.
     * @param score результат одной игры (дистанция или очки)
     */
    public static void addRecord(int score) {
        ArrayList<Integer> records = loadRecordsTable();
        if (records == null) records = new ArrayList<>();
        records.add(score);
        records.sort((a, b) -> b - a);                    // По убыванию
        if (records.size() > 5) {
            records = new ArrayList<>(records.subList(0, 5));
        }
        saveTableOfRecords(records);
    }

    public static int getBestRecord() {
        ArrayList<Integer> records = loadRecordsTable();
        if (records == null || records.isEmpty()) return 0;
        return records.get(0);
    }

    // ========== СИСТЕМА СКИНОВ ==========
    public static void saveTotalScore(int score) {
        preferences.putInteger(TOTAL_SCORE_KEY, score);
        preferences.flush();
    }

    public static int loadTotalScore() {
        return preferences.getInteger(TOTAL_SCORE_KEY, 0);
    }

    /**
     * Разблокировка скина через битовую маску.
     * Каждый скин — один бит. По умолчанию бит 0 уже установлен (стартовый скин).
     * @param skinId номер скина (0, 1, 2...)
     */
    public static void unlockSkin(int skinId) {
        int unlockedFlags = preferences.getInteger(UNLOCKED_SKINS_KEY, 1);  // 1 = 0001 (бит 0)
        unlockedFlags |= (1 << skinId);  // Устанавливаем нужный бит
        preferences.putInteger(UNLOCKED_SKINS_KEY, unlockedFlags);
        preferences.flush();
    }

    public static boolean isSkinUnlocked(int skinId) {
        int unlockedFlags = preferences.getInteger(UNLOCKED_SKINS_KEY, 1);
        return (unlockedFlags & (1 << skinId)) != 0;
    }

    public static void equipSkin(int skinId) {
        preferences.putInteger(EQUIPPED_SKIN_KEY, skinId);
        preferences.flush();
    }

    public static int getEquippedSkin() {
        return preferences.getInteger(EQUIPPED_SKIN_KEY, 0);
    }
}
