package com.deepdiver.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdiver.game.GameSettings;

/**
 * Бесконечный скроллинг фона.
 * Две копии текстуры бегут влево. Когда одна уходит за экран — перепрыгивает вперёд другой.
 * Никакой магии, только старый добрый环形 бафер на два элемента.
 */
public class MovingBackgroundView {
    private float x1, x2;          // Позиции двух копий фона по оси X
    private final float width;     // Ширина экрана (дизайнерская, 1280)
    private final float height;    // Высота экрана (дизайнерская, 720)
    private final Texture texture; // Одна текстура на обе копии

    /**
     * @param texture загруженная текстура фона (должна быть валидной)
     */
    public MovingBackgroundView(Texture texture) {
        this.texture = texture;
        this.width = GameSettings.SCREEN_WIDTH;
        this.height = GameSettings.SCREEN_HEIGHT;
        this.x1 = 0;               // Первая копия — левый край
        this.x2 = width;           // Вторая — сразу за правым
    }

    /**
     * Двигаем фон влево, перекидываем вышедшие за край копии.
     * @param delta время между кадрами (сек)
     */
    public void update(float delta) {
        x1 -= GameSettings.MOVE_SPEED * delta;
        x2 -= GameSettings.MOVE_SPEED * delta;

        // Если первая ушла за левый край — ставим её сразу за второй
        if (x1 <= -width) {
            x1 = x2 + width;
        }
        // Если вторая ушла — ставим за первой
        if (x2 <= -width) {
            x2 = x1 + width;
        }
    }

    /**
     * Рисует обе копии, растягивая текстуру на весь экран.
     * @param batch SpriteBatch из главного цикла рендеринга
     */
    public void draw(SpriteBatch batch) {
        float texWidth = texture.getWidth();
        float texHeight = texture.getHeight();

        // Масштабирование: если текстура меньше экрана — растянем
        float scaleX = width / texWidth;
        float scaleY = height / texHeight;

        // Рисуем принудительно с нужными размерами — фон должен покрывать всё
        batch.draw(texture, x1, 0, width, height);
        batch.draw(texture, x2, 0, width, height);
    }
}
