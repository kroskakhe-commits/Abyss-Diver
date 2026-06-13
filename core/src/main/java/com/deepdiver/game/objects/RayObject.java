package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Объект электрического ската - самый опасный враг.
 * Наносит максимальный урон кислороду при столкновении.
 * Имеет быструю анимацию и сложное движение.
 */
public class RayObject {
    public float x, y;
    public float width = GameSettings.RAY_SIZE;
    public float height = GameSettings.RAY_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;
    private float sinOffset;
    private float speedY;
    private float floatOffset;

    /**
     * Конструктор. Создаёт ската в случайной позиции справа от экрана.
     */
    public RayObject() {
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(60f, GameSettings.SCREEN_HEIGHT - 100f);

        float boundsWidth = width * 0.85f;
        float boundsHeight = height * 0.6f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);

        this.sinOffset = MathUtils.random(0f, MathUtils.PI2);
        this.speedY = MathUtils.random(30f, 70f);
        this.floatOffset = MathUtils.random(0f, MathUtils.PI2);
    }

    /**
     * Обновляет анимацию, позицию и границы ската.
     * Анимация быстрее, чем у других врагов (0.12 сек на кадр).
     * @param delta время между кадрами
     */
    public void update(float delta) {
        // Быстрая анимация
        stateTime += delta;
        if (stateTime > 0.12f) {
            currentFrame = (currentFrame + 1) % GameResources.rayFrames.length;
            stateTime = 0;
        }

        // Сложное вертикальное движение
        y += MathUtils.sin(x * 0.025f + sinOffset) * speedY * delta;
        y += MathUtils.sin(floatOffset + delta * 3f) * 1.5f;
        y = MathUtils.clamp(y, 40f, GameSettings.SCREEN_HEIGHT - height - 40f);

        // Обновление границ столкновений
        float boundsWidth = width * 0.85f;
        float boundsHeight = height * 0.6f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        bounds.setPosition(boundsX, boundsY);
    }

    /**
     * Проверяет, вышел ли скат за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    /**
     * Возвращает текущий кадр анимации ската.
     * @return текстура текущего кадра
     */
    public Texture getTexture() {
        return GameResources.rayFrames[currentFrame];
    }
}
