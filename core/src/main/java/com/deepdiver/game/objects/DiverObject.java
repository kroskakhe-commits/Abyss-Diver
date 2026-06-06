package com.deepdiver.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.managers.MemoryManager;

/**
 * Главный герой — дайвер.
 *
 * Управление: тап — поднимается, отпустил — опускается.
 * Зона столкновений НАМНОГО МЕНЬШЕ визуальной картинки (отступы по 60 пикселей).
 * Это сделано намеренно, чтобы игрок не расстраивался от "нечестных" попаданий.
 */
public class DiverObject {
    public float x, y;
    public float width = GameSettings.DIVER_SIZE;
    public float height = GameSettings.DIVER_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;

    private float hitTimer = 0f;           // Таймер покраснения после удара
    private static final float HIT_DURATION = 0.3f;

    private static final float FIXED_X = 160f;   // Дайвер всегда на фиксированной позиции по X

    // Физика полёта
    private float verticalSpeed = 0f;
    private static final float LIFT_FORCE = 1200f;   // Сила подъёма при тапе
    private static final float GRAVITY = 600f;       // Сила притяжения

    // Система скинов
    private int currentSkin;
    private Texture[][] skinFrames;

    public DiverObject() {
        this.x = FIXED_X;
        this.y = GameSettings.SCREEN_HEIGHT / 2;

        // Зона столкновений — только тело, без плавников и баллона
        this.bounds = new Rectangle(x + 60, y + 60, width - 120, height - 120);
        System.out.println("Дайвер: зона столкновений = " + bounds.width + "x" + bounds.height);

        skinFrames = new Texture[][]{
            GameResources.diverFrames0,
            GameResources.diverFrames1,
            GameResources.diverFrames2
        };

        currentSkin = MemoryManager.getEquippedSkin();
    }

    public void hit() {
        hitTimer = HIT_DURATION;
    }

    public void update(float delta) {
        if (hitTimer > 0) hitTimer -= delta;

        // Анимация — 3 кадра, смена каждые 0.15 секунды
        stateTime += delta;
        if (stateTime > 0.15f) {
            currentFrame = (currentFrame + 1) % 3;
            stateTime = 0;
        }

        // Управление: тап — вверх, нет тапа — вниз
        if (Gdx.input.isTouched()) {
            verticalSpeed += LIFT_FORCE * delta;
        } else {
            verticalSpeed -= GRAVITY * delta;
        }

        verticalSpeed = Math.max(-800f, Math.min(800f, verticalSpeed));
        y += verticalSpeed * delta;

        // Границы экрана
        if (y < 20) {
            y = 20;
            verticalSpeed = 0;
        }
        if (y > GameSettings.SCREEN_HEIGHT - height - 20) {
            y = GameSettings.SCREEN_HEIGHT - height - 20;
            verticalSpeed = 0;
        }

        bounds.setPosition(x + 60, y + 60);

        // Проверка смены скина (если купили в магазине)
        int newSkin = MemoryManager.getEquippedSkin();
        if (newSkin != currentSkin) {
            currentSkin = newSkin;
            System.out.println("Скин сменён на: " + currentSkin);
        }
    }

    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        Texture texture = skinFrames[currentSkin][currentFrame];

        if (hitTimer > 0) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);   // Красный оттенок при укусе
            batch.draw(texture, x, y, width, height);
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.draw(texture, x, y, width, height);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
