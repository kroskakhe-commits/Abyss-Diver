package com.deepdiver.game.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameSettings;

/**
 * Объект пузырька кислорода.
 * Восстанавливает кислород при столкновении с дайвером.
 */
public class BubbleObject {
    public float x, y;
    public float width = GameSettings.BUBBLE_SIZE;
    public float height = GameSettings.BUBBLE_SIZE;
    public Rectangle bounds;

    /**
     * Конструктор. Создаёт пузырёк в случайной позиции справа от экрана.
     */
    public BubbleObject() {
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(50f, GameSettings.SCREEN_HEIGHT - 80f);
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Обновляет позицию границ объекта.
     * @param delta время между кадрами (не используется для движения, только для границ)
     */
    public void update(float delta) {
        bounds.setPosition(x, y);
    }

    /**
     * Проверяет, вышел ли пузырёк за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }
}
