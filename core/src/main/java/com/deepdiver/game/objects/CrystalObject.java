package com.deepdiver.game.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameSettings;

/**
 * Кристалл — основной источник очков.
 *
 * Золотые кристаллы появляются реже (8% шанс) и дают больше очков (300 против 100).
 * Шанс намеренно занижен, чтобы золотые были ценными.
 */
public class CrystalObject {
    public float x, y;
    public float width = GameSettings.CRYSTAL_SIZE;
    public float height = GameSettings.CRYSTAL_SIZE;
    public Rectangle bounds;
    public boolean isGold;

    public CrystalObject() {
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(60f, GameSettings.SCREEN_HEIGHT - 100f);
        this.bounds = new Rectangle(x, y, width, height);

        // 8% шанс выпадения золотого кристалла (было 15%, но решили сделать реже)
        this.isGold = MathUtils.random() < 0.08f;
    }

    public void update(float delta) {
        x -= GameSettings.MOVE_SPEED * delta;
        bounds.setPosition(x, y);
    }

    public boolean isOutOfFrame() {
        return x + width < 0;
    }
}
