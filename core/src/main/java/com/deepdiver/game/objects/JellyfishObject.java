package com.deepdiver.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;

/**
 * Ядовитая медуза — враг.
 *
 * Зона столкновения намеренно сделана очень маленькой (45% от размера картинки).
 * Игрок может буквально проскочить между щупальцами, если умело маневрирует.
 */
public class JellyfishObject {
    public float x, y;
    public float width = GameSettings.JELLYFISH_SIZE;
    public float height = GameSettings.JELLYFISH_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;
    private final float sinOffset;      // Случайное смещение для синусоиды
    private final float speedY;          // Скорость вертикального покачивания

    public JellyfishObject() {
        this.x = GameSettings.SCREEN_WIDTH + 100;
        this.y = MathUtils.random(50f, GameSettings.SCREEN_HEIGHT - 80f);

        // Зона поражения — только жало в центре, 45% от размера
        float boundsWidth = width * 0.45f;
        float boundsHeight = height * 0.45f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);

        this.sinOffset = MathUtils.random(0f, 100f);
        this.speedY = MathUtils.random(30f, 80f);

        System.out.println("Медуза: зона поражения = " + bounds.width + "x" + bounds.height);
    }

    public void update(float delta) {
        // Анимация
        stateTime += delta;
        if (stateTime > 0.2f) {
            currentFrame = (currentFrame + 1) % GameResources.jellyfishFrames.length;
            stateTime = 0;
        }

        // Движение влево
        x -= GameSettings.MOVE_SPEED * delta;
        // Плавное покачивание вверх-вниз
        y += MathUtils.sin(x * 0.02f + sinOffset) * speedY * delta;

        // Границы
        if (y < 30) y = 30;
        if (y > GameSettings.SCREEN_HEIGHT - height - 30) y = GameSettings.SCREEN_HEIGHT - height - 30;

        // Обновляем зону столкновений
        float boundsWidth = width * 0.45f;
        float boundsHeight = height * 0.45f;
        float boundsX = x + (width - boundsWidth) / 2;
        float boundsY = y + (height - boundsHeight) / 2;
        bounds.setPosition(boundsX, boundsY);
    }

    public boolean isOutOfFrame() {
        return x + width < 0;
    }

    public Texture getTexture() {
        return GameResources.jellyfishFrames[currentFrame];
    }
}
