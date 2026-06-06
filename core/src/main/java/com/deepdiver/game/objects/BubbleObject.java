package com.deepdiver.game.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameSettings;

/**
 * Пузырёк кислорода.
 * Появляется справа, летит влево. Даёт кислород при сборе.
 *
 * Летит БЫСТРЕЕ остальных (x1.5 скорости мира), чтобы создать ощущение срочности.
 */
public class BubbleObject {
    public float x, y;
    public float width = GameSettings.BUBBLE_SIZE;
    public float height = GameSettings.BUBBLE_SIZE;
    public Rectangle bounds;

    public BubbleObject() {
        // Появляемся за правым краем экрана
        this.x = GameSettings.SCREEN_WIDTH + 50;
        // Случайная высота, но не слишком близко к краям
        this.y = MathUtils.random(50f, GameSettings.SCREEN_HEIGHT - 80f);
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta) {
        // Скорость в 1.5 раза выше обычной
        x -= (GameSettings.MOVE_SPEED * 1.5f) * delta;
        bounds.setPosition(x, y);
    }

    public boolean isOutOfFrame() {
        return x + width < 0;
    }
}
