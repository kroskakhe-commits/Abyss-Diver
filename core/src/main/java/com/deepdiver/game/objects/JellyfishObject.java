package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Объект медузы - враг.
 * Движется синусоидально по вертикали.
 * При столкновении наносит урон кислороду.
 */
public class JellyfishObject {
    public float x, y;
    public float width = GameSettings.JELLYFISH_SIZE;
    public float height = GameSettings.JELLYFISH_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;
    private final float sinOffset;
    private final float speedY;

    /**
     * Конструктор. Создаёт медузу в случайной позиции справа от экрана.
     * Каждая медуза имеет уникальные параметры движения.
     */
    public JellyfishObject() {
        this.x = GameSettings.SCREEN_WIDTH + 100;
        this.y = MathUtils.random(50f, GameSettings.SCREEN_HEIGHT - 80f);

        float boundsWidth = width * 0.75f;
        float boundsHeight = height * 0.75f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);

        this.sinOffset = MathUtils.random(0f, 100f);
        this.speedY = MathUtils.random(30f, 80f);
    }

    /**
     * Обновляет анимацию, позицию и границы медузы.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        // Анимация
        stateTime += delta;
        if (stateTime > 0.2f) {
            currentFrame = (currentFrame + 1) % GameResources.jellyfishFrames.length;
            stateTime = 0;
        }

        // Вертикальное синусоидальное движение
        y += MathUtils.sin(x * 0.02f + sinOffset) * speedY * delta;

        // Ограничения по вертикали
        if (y < 30) y = 30;
        if (y > GameSettings.SCREEN_HEIGHT - height - 30) y = GameSettings.SCREEN_HEIGHT - height - 30;

        // Обновление границ столкновений
        float boundsWidth = width * 0.75f;
        float boundsHeight = height * 0.75f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        bounds.setPosition(boundsX, boundsY);
    }

    /**
     * Проверяет, вышла ли медуза за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    /**
     * Возвращает текущий кадр анимации медузы.
     * @return текстура текущего кадра
     */
    public Texture getTexture() {
        return GameResources.jellyfishFrames[currentFrame];
    }
}
