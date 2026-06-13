package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Объект акулы - опасный враг.
 * Наносит большой урон кислороду при столкновении.
 */
public class SharkObject {
    public float x, y;
    public float width = GameSettings.SHARK_SIZE;
    public float height = GameSettings.SHARK_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;
    private float sinOffset;
    private float speedY;

    /**
     * Конструктор. Создаёт акулу в случайной позиции справа от экрана.
     */
    public SharkObject() {
        this(GameSettings.SCREEN_WIDTH + 50, MathUtils.random(80f, GameSettings.SCREEN_HEIGHT - 130f));
    }

    /**
     * Конструктор с указанием начальной позиции.
     * @param x начальная координата X
     * @param y начальная координата Y
     */
    public SharkObject(float x, float y) {
        this.x = x;
        this.y = y;

        float boundsWidth = width * 0.8f;
        float boundsHeight = height * 0.8f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);

        this.sinOffset = MathUtils.random(0f, MathUtils.PI2);
        this.speedY = MathUtils.random(40f, 90f);
    }

    /**
     * Обновляет анимацию, позицию и границы акулы.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        // Анимация
        stateTime += delta;
        if (stateTime > 0.15f) {
            currentFrame = (currentFrame + 1) % GameResources.sharkFrames.length;
            stateTime = 0;
        }

        // Вертикальное синусоидальное движение
        y += MathUtils.sin(x * 0.015f + sinOffset) * speedY * delta;
        y = MathUtils.clamp(y, 40f, GameSettings.SCREEN_HEIGHT - height - 40f);

        // Обновление границ столкновений
        float boundsWidth = width * 0.8f;
        float boundsHeight = height * 0.8f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        bounds.setPosition(boundsX, boundsY);
    }

    /**
     * Проверяет, вышла ли акула за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    /**
     * Возвращает текущий кадр анимации акулы.
     * @return текстура текущего кадра
     */
    public Texture getTexture() {
        return GameResources.sharkFrames[currentFrame];
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
