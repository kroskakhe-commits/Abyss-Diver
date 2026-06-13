package com.deepdiver.game;

import com.badlogic.gdx.Gdx;

/**
 * Централизованное хранилище игровых параметров.
 * Содержит настройки физики, размеров объектов, локаций и заданий.
 */
public class GameSettings {
    // ========== РАЗРЕШЕНИЕ И МАСШТАБИРОВАНИЕ ==========
    public static final float DESIGN_WIDTH = 1280f;
    public static final float DESIGN_HEIGHT = 720f;
    public static float SCREEN_WIDTH = 1280f;
    public static float SCREEN_HEIGHT = 720f;
    public static float scaleX = 1f;
    public static float scaleY = 1f;

    /**
     * Обновляет размеры экрана и коэффициенты масштабирования.
     * Вызывается при изменении размеров окна или при старте.
     */
    public static void updateScreenSize(float width, float height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        scaleX = width / DESIGN_WIDTH;
        scaleY = height / DESIGN_HEIGHT;
    }

    /** Преобразует X-координату из дизайнерской в экранную */
    public static float x(float value) { return value * scaleX; }

    /** Преобразует Y-координату из дизайнерской в экранную */
    public static float y(float value) { return value * scaleY; }

    /** Масштабирует размер, сохраняя пропорции (берёт минимум из scaleX и scaleY) */
    public static float size(float value) { return value * Math.min(scaleX, scaleY); }

    // ========== РАЗМЕРЫ ОБЪЕКТОВ (в дизайнерских пикселях) ==========
    public static final float DIVER_SIZE = 260f;
    public static final float CRYSTAL_SIZE = 120f;
    public static final float JELLYFISH_SIZE = 160f;
    public static final float BUBBLE_SIZE = 85f;
    public static final float SHARK_SIZE = 180f;
    public static final float OCTOPUS_SIZE = 150f;
    public static final float RAY_SIZE = 140f;

    // ========== ИГРОВАЯ МЕХАНИКА ==========
    public static final float ACCELEROMETER_SENSITIVITY = 120f;
    public static final float START_OXYGEN = 100f;
    public static final float OXYGEN_DEPLETION_RATE = 4f;      // Единиц кислорода в секунду
    public static final float BUBBLE_REPLENISH = 20f;          // Восстановление за один пузырёк
    public static final int SCORE_CRYSTAL = 100;
    public static final int SCORE_GOLD_CRYSTAL = 300;
    public static final float JELLY_DAMAGE = 25f;
    public static final float SHARK_DAMAGE = 35f;
    public static final float OCTOPUS_DAMAGE = 20f;
    public static final float RAY_DAMAGE = 40f;
    public static final float MOVE_SPEED = 300f;               // Пикселей в секунду

    // ========== БОНУСЫ ==========
    public static final float SHIELD_DURATION = 5f;
    public static final float SPEED_BOOST_DURATION = 4f;
    public static final float SPEED_BOOST_MULTIPLIER = 1.5f;

    // ========== ЛОКАЦИИ ==========
    public static final int LOCATION_SHALLOWS = 0;
    public static final int LOCATION_REEF = 1;
    public static final int LOCATION_DEEP = 2;
    public static final int LOCATION_RUINS = 3;

    /** После какого количества выполненных заданий открывается каждая локация */
    public static final int[] LOCATION_UNLOCK_AT = {0, 6, 16, 26};

    public static int getLocationByMission(int completedMissions) {
        if (completedMissions >= LOCATION_UNLOCK_AT[3]) return LOCATION_RUINS;
        if (completedMissions >= LOCATION_UNLOCK_AT[2]) return LOCATION_DEEP;
        if (completedMissions >= LOCATION_UNLOCK_AT[1]) return LOCATION_REEF;
        return LOCATION_SHALLOWS;
    }

    // ========== СИСТЕМА ЗАДАНИЙ ==========
    public static final int MISSIONS_COUNT = 30;

    // Типы заданий
    public static final int MISSION_TYPE_DISTANCE = 0;          // Проплыть N метров
    public static final int MISSION_TYPE_CRYSTALS_NORMAL = 1;   // Собрать N синих кристаллов
    public static final int MISSION_TYPE_CRYSTALS_GOLD = 2;     // Собрать N золотых кристаллов
    public static final int MISSION_TYPE_BUBBLES = 3;           // Собрать N пузырьков

    /**
     * Массив заданий: [тип, целевое значение, описание]
     * Индекс в массиве соответствует номеру задания (0-based)
     */
    public static final Object[][] MISSIONS = {
        // 1-5: Мелководье
        {MISSION_TYPE_DISTANCE, 500, "Проплыви 500 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 10, "Собери 10 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 1000, "Проплыви 1000 метров"},
        {MISSION_TYPE_BUBBLES, 5, "Собери 5 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 1, "Найди 1 золотой кристалл"},

        // 6-10: Коралловый риф
        {MISSION_TYPE_DISTANCE, 2000, "Проплыви 2000 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 25, "Собери 25 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 3000, "Проплыви 3000 метров"},
        {MISSION_TYPE_BUBBLES, 15, "Собери 15 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 3, "Найди 3 золотых кристалла"},

        // 11-15: Коралловый риф (продолжение)
        {MISSION_TYPE_DISTANCE, 4500, "Проплыви 4500 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 40, "Собери 40 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 6000, "Проплыви 6000 метров"},
        {MISSION_TYPE_BUBBLES, 25, "Собери 25 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 5, "Найди 5 золотых кристаллов"},

        // 16-20: Тёмные глубины
        {MISSION_TYPE_DISTANCE, 8000, "Проплыви 8000 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 60, "Собери 60 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 10000, "Проплыви 10000 метров"},
        {MISSION_TYPE_BUBBLES, 35, "Собери 35 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 8, "Найди 8 золотых кристаллов"},

        // 21-25: Тёмные глубины (продолжение)
        {MISSION_TYPE_DISTANCE, 13000, "Проплыви 13000 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 80, "Собери 80 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 16000, "Проплыви 16000 метров"},
        {MISSION_TYPE_BUBBLES, 50, "Собери 50 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 12, "Найди 12 золотых кристаллов"},

        // 26-30: Затонувший город
        {MISSION_TYPE_DISTANCE, 20000, "Проплыви 20000 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 100, "Собери 100 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 25000, "Проплыви 25000 метров"},
        {MISSION_TYPE_BUBBLES, 70, "Собери 70 пузырьков кислорода"},
        {MISSION_TYPE_CRYSTALS_GOLD, 20, "Найди 20 золотых кристаллов"}
    };
}
