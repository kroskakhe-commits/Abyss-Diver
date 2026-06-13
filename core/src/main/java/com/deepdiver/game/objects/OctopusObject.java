package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Объект осьминога - враг со сложным движением.
 * Периодически меняет направление движения и скорость.
 */
public class OctopusObject {
    public float x, y;
    public float width = GameSettings.OCTOPUS_SIZE;
    public float height = GameSettings.OCTOPUS_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;
    private float sinOffset;
    private float speedY;
    private float speedX;
    private float directionChangeTimer = 0;

    /**
     * Конструктор. Создаёт осьминога в случайной позиции справа от экрана.
     */
    public OctopusObject() {
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(60f, GameSettings.SCREEN_HEIGHT - 100f);

        float boundsWidth = width * 0.7f;
        float boundsHeight = height * 0.7f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);

        this.sinOffset = MathUtils.random(0f, MathUtils.PI2);
        this.speedY = MathUtils.random(50f, 120f);
        this.speedX = MathUtils.random(0.8f, 1.2f);
    }

    /**
     * Обновляет анимацию, позицию и границы осьминога.
     * Каждые 2 секунды меняет параметры движения.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        // Анимация
        stateTime += delta;
        if (stateTime > 0.2f) {
            currentFrame = (currentFrame + 1) % GameResources.octopusFrames.length;
            stateTime = 0;
        }

        // Периодическая смена направления и скорости
        directionChangeTimer += delta;
        if (directionChangeTimer > 2f) {
            speedY = MathUtils.random(40f, 130f);
            speedX = MathUtils.random(0.7f, 1.3f);
            directionChangeTimer = 0;
        }

        // Движение
        y += MathUtils.sin(x * 0.02f + sinOffset) * speedY * delta;
        y = MathUtils.clamp(y, 40f, GameSettings.SCREEN_HEIGHT - height - 40f);

        // Обновление границ столкновений
        float boundsWidth = width * 0.7f;
        float boundsHeight = height * 0.7f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        bounds.setPosition(boundsX, boundsY);
    }

    /**
     * Проверяет, вышел ли осьминог за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    /**
     * Возвращает текущий кадр анимации осьминога.
     * @return текстура текущего кадра
     */
    public Texture getTexture() {
        return GameResources.octopusFrames[currentFrame];
    }

    /**
     * Возвращает относительную скорость движения по оси X.
     * @return множитель скорости (0.7 - 1.3)
     */
    public float getSpeedX() {
        return speedX;
    }
}
