package com.deepdiver.game;

import com.badlogic.gdx.Gdx;

/**
 * Все настройки игры в одном месте.
 * Размеры, скорости, параметры геймплея.
 *
 * Важно: есть дизайнерское разрешение (1280x720) и реальный экран.
 * Методы x(), y(), size() помогают адаптировать координаты под любой экран.
 */
public class GameSettings {
    // Базовое разрешение, в котором всё спроектировано
    public static final float DESIGN_WIDTH = 1280f;
    public static final float DESIGN_HEIGHT = 720f;

    // Реальные размеры экрана (узнаются при запуске)
    public static float SCREEN_WIDTH = 1280f;
    public static float SCREEN_HEIGHT = 720f;

    // Коэффициенты для адаптации
    public static float scaleX = 1f;
    public static float scaleY = 1f;

    /**
     * Обновить размеры экрана и пересчитать коэффициенты масштабирования.
     * Вызывается при создании игры и при resize().
     */
    public static void updateScreenSize(float width, float height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        scaleX = width / DESIGN_WIDTH;
        scaleY = height / DESIGN_HEIGHT;
        System.out.println("📱 Экран: " + width + "x" + height + " | Масштаб: " + scaleX + " x " + scaleY);
    }

    // Адаптивные методы: преобразуют дизайнерские координаты в реальные
    public static float x(float value) { return value * scaleX; }
    public static float y(float value) { return value * scaleY; }
    public static float size(float value) { return value * Math.min(scaleX, scaleY); }

    // ========== РАЗМЕРЫ ОБЪЕКТОВ (в дизайнерских пикселях) ==========
    public static final float DIVER_SIZE = 260f;
    public static final float CRYSTAL_SIZE = 120f;
    public static final float JELLYFISH_SIZE = 160f;
    public static final float BUBBLE_SIZE = 85f;

    // ========== ИГРОВЫЕ ПАРАМЕТРЫ ==========
    public static final float ACCELEROMETER_SENSITIVITY = 120f;
    public static final float START_OXYGEN = 100f;
    public static final float OXYGEN_DEPLETION_RATE = 4f;      // % в секунду
    public static final float BUBBLE_REPLENISH = 20f;          // % за один пузырёк
    public static final int SCORE_CRYSTAL = 100;
    public static final int SCORE_GOLD_CRYSTAL = 300;
    public static final float JELLY_DAMAGE = 25f;              // % урона от медузы

    // Скорость движения мира (пикселей в секунду). Чем больше — тем быстрее игра.
    public static final float MOVE_SPEED = 300f;

    // ========== КВЕСТЫ ==========
    public static final int MISSIONS_COUNT = 6;
    public static final int MISSION_TYPE_DISTANCE = 0;
    public static final int MISSION_TYPE_CRYSTALS_NORMAL = 1;
    public static final int MISSION_TYPE_CRYSTALS_GOLD = 2;
    public static final int MISSION_TYPE_BUBBLES = 3;

    // Массив всех квестов: [тип, цель, описание]
    public static final Object[][] MISSIONS = {
        {MISSION_TYPE_DISTANCE, 1500, "Проплыви 1500 метров"},
        {MISSION_TYPE_CRYSTALS_NORMAL, 25, "Собери 25 синих кристаллов"},
        {MISSION_TYPE_DISTANCE, 1800, "Проплыви 1800 метров"},
        {MISSION_TYPE_CRYSTALS_GOLD, 5, "Собери 5 золотых кристаллов"},
        {MISSION_TYPE_BUBBLES, 20, "Собери 20 пузырьков кислорода"},
        {MISSION_TYPE_DISTANCE, 5000, "Проплыви 5000 метров — финальный рубеж!"}
    };
}
