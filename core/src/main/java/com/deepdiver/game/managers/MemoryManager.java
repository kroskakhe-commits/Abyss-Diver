package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;

/**
 * Управление сохранением и загрузкой данных игры.
 * Использует LibGDX Preferences для хранения настроек, рекордов и прогресса.
 */
public class MemoryManager {
    private static final Preferences preferences = Gdx.app.getPreferences("DeepDiver");

    private static final String TOTAL_SCORE_KEY = "totalScore";
    private static final String UNLOCKED_SKINS_KEY = "unlockedSkins";
    private static final String EQUIPPED_SKIN_KEY = "equippedSkin";
    private static final String SKIN_LEVEL_KEY = "skinLevel";
    private static final String SOUND_ON_KEY = "isSoundOn";
    private static final String MUSIC_ON_KEY = "isMusicOn";
    private static final String RECORD_TABLE_KEY = "recordTable";

    // ================================================================================
    // НАСТРОЙКИ ЗВУКА
    // ================================================================================

    /**
     * Сохраняет настройку звуковых эффектов.
     * @param isOn true - звук включён, false - выключен
     */
    public static void saveSoundSettings(boolean isOn) {
        preferences.putBoolean(SOUND_ON_KEY, isOn);
        preferences.flush();
    }

    /**
     * Загружает настройку звуковых эффектов.
     * @return true если звук включён, по умолчанию true
     */
    public static boolean loadIsSoundOn() {
        return preferences.getBoolean(SOUND_ON_KEY, true);
    }

    /**
     * Сохраняет настройку музыки.
     * @param isOn true - музыка включена, false - выключена
     */
    public static void saveMusicSettings(boolean isOn) {
        preferences.putBoolean(MUSIC_ON_KEY, isOn);
        preferences.flush();
    }

    /**
     * Загружает настройку музыки.
     * @return true если музыка включена, по умолчанию true
     */
    public static boolean loadIsMusicOn() {
        return preferences.getBoolean(MUSIC_ON_KEY, true);
    }

    // ================================================================================
    // ТАБЛИЦА РЕКОРДОВ
    // ================================================================================

    /**
     * Сохраняет таблицу рекордов в формате JSON.
     * @param table список рекордов (максимум 5 записей)
     */
    public static void saveTableOfRecords(ArrayList<Integer> table) {
        Json json = new Json();
        String tableInString = json.toJson(table);
        preferences.putString(RECORD_TABLE_KEY, tableInString);
        preferences.flush();
    }

    /**
     * Загружает таблицу рекордов из сохранения.
     * @return список рекордов или null, если записей нет
     */
    public static ArrayList<Integer> loadRecordsTable() {
        if (!preferences.contains(RECORD_TABLE_KEY)) return null;
        String scores = preferences.getString(RECORD_TABLE_KEY);
        Json json = new Json();
        return json.fromJson(ArrayList.class, scores);
    }

    /**
     * Добавляет новый рекорд дистанции.
     * Сохраняет только 5 лучших результатов в порядке убывания.
     * @param distance пройденная дистанция в метрах
     */
    public static void addRecord(int distance) {
        if (distance <= 0) return;
        ArrayList<Integer> records = loadRecordsTable();
        if (records == null) records = new ArrayList<>();
        records.add(distance);
        records.sort((a, b) -> b - a);
        if (records.size() > 5) {
            records = new ArrayList<>(records.subList(0, 5));
        }
        saveTableOfRecords(records);
    }

    /**
     * Очищает всю таблицу рекордов.
     */
    public static void clearAllRecords() {
        preferences.remove(RECORD_TABLE_KEY);
        preferences.flush();
    }

    /**
     * Возвращает лучший рекорд.
     * @return максимальная дистанция или 0, если рекордов нет
     */
    public static int getBestRecord() {
        ArrayList<Integer> records = loadRecordsTable();
        if (records == null || records.isEmpty()) return 0;
        return records.get(0);
    }

    // ================================================================================
    // СЧЁТ ИГРОКА
    // ================================================================================

    /**
     * Сохраняет общий счёт игрока.
     * @param score общее количество очков
     */
    public static void saveTotalScore(int score) {
        preferences.putInteger(TOTAL_SCORE_KEY, score);
        preferences.flush();
    }

    /**
     * Загружает общий счёт игрока.
     * @return общее количество очков, по умолчанию 0
     */
    public static int loadTotalScore() {
        return preferences.getInteger(TOTAL_SCORE_KEY, 0);
    }

    // ================================================================================
    // СИСТЕМА СКИНОВ (разблокировка и экипировка)
    // ================================================================================

    /**
     * Разблокирует скин для использования.
     * Используется битовая маска для хранения состояния до 32 скинов.
     * @param skinId идентификатор скина (0, 1, 2...)
     */
    public static void unlockSkin(int skinId) {
        int unlockedFlags = preferences.getInteger(UNLOCKED_SKINS_KEY, 1);
        unlockedFlags |= (1 << skinId);
        preferences.putInteger(UNLOCKED_SKINS_KEY, unlockedFlags);
        preferences.flush();
    }

    /**
     * Проверяет, разблокирован ли скин.
     * @param skinId идентификатор скина
     * @return true если скин разблокирован
     */
    public static boolean isSkinUnlocked(int skinId) {
        int unlockedFlags = preferences.getInteger(UNLOCKED_SKINS_KEY, 1);
        return (unlockedFlags & (1 << skinId)) != 0;
    }

    /**
     * Экипирует выбранный скин.
     * @param skinId идентификатор скина для экипировки
     */
    public static void equipSkin(int skinId) {
        preferences.putInteger(EQUIPPED_SKIN_KEY, skinId);
        preferences.flush();
    }

    /**
     * Возвращает идентификатор текущего экипированного скина.
     * @return идентификатор скина, по умолчанию 0
     */
    public static int getEquippedSkin() {
        return preferences.getInteger(EQUIPPED_SKIN_KEY, 0);
    }

    // ================================================================================
    // ПРОКАЧКА СКИНОВ (уровни и бонусы)
    // ================================================================================

    /**
     * Возвращает уровень прокачки скина.
     * @param skinId идентификатор скина
     * @return уровень от 1 до 5, по умолчанию 1
     */
    public static int getSkinLevel(int skinId) {
        return preferences.getInteger(SKIN_LEVEL_KEY + skinId, 1);
    }

    /**
     * Повышает уровень скина на 1 (максимум 5).
     * @param skinId идентификатор скина
     */
    public static void upgradeSkin(int skinId) {
        int currentLevel = getSkinLevel(skinId);
        if (currentLevel < 5) {
            preferences.putInteger(SKIN_LEVEL_KEY + skinId, currentLevel + 1);
            preferences.flush();
        }
    }

    /**
     * Возвращает стоимость улучшения скина до следующего уровня.
     * @param currentLevel текущий уровень скина (1-4)
     * @return цена в очках, или -1 если максимальный уровень достигнут
     */
    public static int getUpgradePrice(int currentLevel) {
        int[] prices = {0, 3000, 5000, 8000, 10000};
        if (currentLevel >= 5) return -1;
        return prices[currentLevel];
    }

    /**
     * Возвращает бонус к скорости движения.
     * @param level уровень скина (1-5)
     * @return множитель скорости (0.05 = 5%, 0.20 = 20%)
     */
    public static float getSpeedBonus(int level) {
        float[] bonuses = {0, 0.05f, 0.10f, 0.15f, 0.20f};
        return bonuses[Math.min(level, bonuses.length - 1)];
    }

    /**
     * Возвращает бонус к максимальному запасу кислорода.
     * @param level уровень скина (1-5)
     * @return дополнительный кислород (5-20 единиц)
     */
    public static float getOxygenBonus(int level) {
        float[] bonuses = {0, 5, 10, 15, 20};
        return bonuses[Math.min(level, bonuses.length - 1)];
    }

    /**
     * Возвращает множитель начисления очков.
     * @param level уровень скина (1-5)
     * @return множитель очков (1.1 = +10%, 1.5 = +50%)
     */
    public static float getScoreMultiplier(int level) {
        float[] multipliers = {1.0f, 1.1f, 1.2f, 1.35f, 1.5f};
        return multipliers[Math.min(level, multipliers.length - 1)];
    }
}
