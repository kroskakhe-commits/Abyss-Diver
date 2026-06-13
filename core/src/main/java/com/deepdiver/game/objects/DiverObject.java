package com.deepdiver.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.managers.MemoryManager;

/**
 * Объект дайвера, управляемый игроком.
 * Обрабатывает движение по вертикали, анимацию, эффекты щита и звезды,
 * а также отображение текущего скина.
 */
public class DiverObject {
    public float x, y;
    public float width = GameSettings.DIVER_SIZE;
    public float height = GameSettings.DIVER_SIZE;
    public Rectangle bounds;

    private float stateTime = 0;
    private int currentFrame = 0;

    private float hitTimer = 0f;
    private static final float HIT_DURATION = 0.3f;

    private static final float FIXED_X = 160f;

    private float verticalSpeed = 0f;
    private static final float LIFT_FORCE = 1200f;
    private static final float GRAVITY = 600f;

    private int currentSkin;
    private Texture[][] skinFrames;

    private boolean hasShieldEffect = false;
    private float shieldEffectTimer = 0f;

    private boolean hasStarEffect = false;
    private float starEffectTimer = 0f;

    public DiverObject() {
        this.x = FIXED_X;
        this.y = GameSettings.SCREEN_HEIGHT / 2;
        this.bounds = new Rectangle(x + 60, y + 60, width - 120, height - 120);

        skinFrames = new Texture[][]{
            GameResources.diverFrames0,
            GameResources.diverFrames1,
            GameResources.diverFrames2
        };

        currentSkin = MemoryManager.getEquippedSkin();
    }

    /**
     * Вызывается при столкновении с врагом.
     * Активирует визуальный эффект удара (красное мигание).
     */
    public void hit() {
        hitTimer = HIT_DURATION;
    }

    /**
     * Активирует визуальный эффект щита.
     * @param duration длительность эффекта в секундах
     */
    public void activateShieldEffect(float duration) {
        hasShieldEffect = true;
        shieldEffectTimer = duration;
    }

    /**
     * Активирует визуальный эффект звезды.
     * @param duration длительность эффекта в секундах
     */
    public void activateStarEffect(float duration) {
        hasStarEffect = true;
        starEffectTimer = duration;
    }

    /**
     * Обновляет таймеры визуальных эффектов.
     * @param delta время между кадрами
     */
    private void updateEffects(float delta) {
        if (hasShieldEffect) {
            shieldEffectTimer -= delta;
            if (shieldEffectTimer <= 0) {
                hasShieldEffect = false;
            }
        }
        if (hasStarEffect) {
            starEffectTimer -= delta;
            if (starEffectTimer <= 0) {
                hasStarEffect = false;
            }
        }
    }

    public boolean hasShieldEffect() {
        return hasShieldEffect;
    }

    /**
     * Отрисовывает визуальные эффекты (щит и звёзды).
     * @param shapeRenderer рендерер для рисования примитивов
     * @param batch SpriteBatch для отрисовки спрайтов
     */
    public void drawEffects(ShapeRenderer shapeRenderer, com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (hasShieldEffect) {
            float centerX = x + width / 2;
            float centerY = y + height / 2;
            float radius = width / 2 + 15;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0.2f, 0.8f, 1f, 0.9f);
            shapeRenderer.circle(centerX, centerY, radius);

            shapeRenderer.setColor(0.2f, 0.6f, 1f, 0.5f);
            for (int i = 1; i <= 3; i++) {
                shapeRenderer.circle(centerX, centerY, radius - i * 4);
            }
            shapeRenderer.end();
        }

        if (hasStarEffect) {
            float centerX = x + width / 2;
            float centerY = y + height / 2;
            float time = System.currentTimeMillis() / 100f;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < 8; i++) {
                float angle = (float)(time + i * Math.PI * 2 / 8);
                float radius = 80f;
                float px = centerX + (float)Math.cos(angle) * radius;
                float py = centerY + (float)Math.sin(angle) * radius;

                shapeRenderer.setColor(1f, 0.8f, 0.2f, 0.8f);
                shapeRenderer.circle(px, py, 4);
            }
            shapeRenderer.end();
        }
    }

    /**
     * Обновляет состояние дайвера: движение, анимацию, смену скина.
     * @param delta время между кадрами
     */
    public void update(float delta) {
        if (hitTimer > 0) hitTimer -= delta;

        updateEffects(delta);

        stateTime += delta;
        if (stateTime > 0.15f) {
            currentFrame = (currentFrame + 1) % 3;
            stateTime = 0;
        }

        if (Gdx.input.isTouched()) {
            verticalSpeed += LIFT_FORCE * delta;
        } else {
            verticalSpeed -= GRAVITY * delta;
        }

        verticalSpeed = Math.max(-800f, Math.min(800f, verticalSpeed));
        y += verticalSpeed * delta;

        if (y < 20) {
            y = 20;
            verticalSpeed = 0;
        }
        if (y > GameSettings.SCREEN_HEIGHT - height - 20) {
            y = GameSettings.SCREEN_HEIGHT - height - 20;
            verticalSpeed = 0;
        }

        bounds.setPosition(x + 60, y + 60);

        int newSkin = MemoryManager.getEquippedSkin();
        if (newSkin != currentSkin) {
            currentSkin = newSkin;
        }
    }

    /**
     * Отрисовывает дайвера с учётом текущего скина и эффекта удара.
     * @param batch SpriteBatch для отрисовки
     */
    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        Texture texture = skinFrames[currentSkin][currentFrame];

        if (hitTimer > 0) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
            batch.draw(texture, x, y, width, height);
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            batch.draw(texture, x, y, width, height);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
