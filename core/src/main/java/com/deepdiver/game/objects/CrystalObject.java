package com.deepdiver.game.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameSettings;

/**
 * Объект кристалла.
 * Может быть обычным (синим) или золотым.
 * Обычный даёт 100 очков, золотой - 300 очков.
 */
public class CrystalObject {
    public float x, y;
    public float width = GameSettings.CRYSTAL_SIZE;
    public float height = GameSettings.CRYSTAL_SIZE;
    public Rectangle bounds;
    public boolean isGold;

    /**
     * Конструктор. Создаёт кристалл в случайной позиции справа от экрана.
     * Золотые кристаллы появляются с вероятностью 8%.
     */
    public CrystalObject() {
        this.x = GameSettings.SCREEN_WIDTH + 50;
        this.y = MathUtils.random(60f, GameSettings.SCREEN_HEIGHT - 100f);
        this.bounds = new Rectangle(x, y, width, height);
        this.isGold = MathUtils.random() < 0.08f;
    }

    /**
     * Обновляет позицию границ объекта.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        bounds.setPosition(x, y);
    }

    /**
     * Проверяет, вышел ли кристалл за левый край экрана.
     * @return true если объект полностью невидим
     */
    public boolean isOutOfFrame() {
        return x + width < 0;
    }
}
