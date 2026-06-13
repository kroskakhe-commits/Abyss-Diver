package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Объект бонуса.
 * Может быть трёх типов: звезда (восстанавливает кислород), щит (защита от урона), ускорение.
 */
public class PowerUpObject {
    public enum PowerUpType {
        STAR,   // Восстанавливает кислород
        SHIELD, // Даёт временную неуязвимость
        SPEED   // Увеличивает скорость движения
    }

    public float x, y;
    public float width = 90f;
    public float height = 90f;
    public Rectangle bounds;
    public PowerUpType type;

    private float floatOffset;
    private float floatSpeed;

    /**
     * Конструктор. Создаёт бонус случайного типа в правой части экрана.
     * @param type тип бонуса
     */
    public PowerUpObject(PowerUpType type) {
        this.type = type;
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(100f, GameSettings.SCREEN_HEIGHT - 180f);
        this.bounds = new Rectangle(x, y, width, height);
        this.floatOffset = MathUtils.random(0f, MathUtils.PI2);
        this.floatSpeed = MathUtils.random(2f, 4f);
    }

    /**
     * Обновляет позицию бонуса с эффектом покачивания.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        y += MathUtils.sin(floatOffset + floatSpeed * delta) * 1.5f;
        y = MathUtils.clamp(y, 80f, GameSettings.SCREEN_HEIGHT - 120f);
        bounds.setPosition(x, y);
    }

    /**
     * Проверяет, вышел ли бонус за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    /**
     * Возвращает текстуру бонуса в зависимости от типа.
     * @return текстура бонуса
     */
    public Texture getTexture() {
        switch (type) {
            case STAR: return GameResources.starPower;
            case SHIELD: return GameResources.shieldPower;
            case SPEED: return GameResources.speedPower;
            default: return GameResources.starPower;
        }
    }

    /**
     * Возвращает случайный тип бонуса.
     * Все три типа имеют равную вероятность выпадения.
     * @return случайный тип бонуса
     */
    public static PowerUpType getRandomType() {
        float rand = MathUtils.random();
        if (rand < 0.33f) return PowerUpType.STAR;
        if (rand < 0.66f) return PowerUpType.SHIELD;
        return PowerUpType.SPEED;
    }
}
