package com.deepdiver.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdiver.game.GameSettings;

/**
 * Компонент бесконечного скроллинга фона.
 * Использует две копии текстуры, которые движутся влево.
 * Когда одна копия полностью уходит за левый край экрана, она перемещается вперёд другой.
 */
public class MovingBackgroundView {
    private float x1, x2;
    private final float width;
    private final float height;
    private final Texture texture;

    /**
     * Конструктор.
     * @param texture текстура фона
     */
    public MovingBackgroundView(Texture texture) {
        this.texture = texture;
        this.width = GameSettings.SCREEN_WIDTH;
        this.height = GameSettings.SCREEN_HEIGHT;
        this.x1 = 0;
        this.x2 = width;
    }

    /**
     * Обновляет позиции фона.
     * @param delta время, прошедшее с последнего кадра
     */
    public void update(float delta) {
        x1 -= GameSettings.MOVE_SPEED * delta;
        x2 -= GameSettings.MOVE_SPEED * delta;

        if (x1 <= -width) {
            x1 = x2 + width;
        }
        if (x2 <= -width) {
            x2 = x1 + width;
        }
    }

    /**
     * Отрисовывает обе копии фона.
     * @param batch SpriteBatch для отрисовки
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x1, 0, width, height);
        batch.draw(texture, x2, 0, width, height);
    }
}
