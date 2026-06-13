package com.deepdiver.game.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.deepdiver.game.GameSettings;

/**
 * Компонент отображения шкалы кислорода.
 * Отображает текущий запас кислорода цветной полосой на тёмном фоне.
 * Цвет полосы зависит от уровня: зелёный (>50%), оранжевый (25-50%), красный (<25%).
 */
public class OxygenBar {
    private final ShapeRenderer shapeRenderer;

    public OxygenBar() {
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * Отрисовывает шкалу кислорода.
     * @param oxygen текущий уровень кислорода (0-100)
     * @param x левая координата шкалы
     * @param y нижняя координата шкалы
     * @param width полная ширина шкалы
     * @param height высота шкалы
     */
    public void draw(float oxygen, float x, float y, float width, float height) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Фон
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, width, height);

        // Цвет заполнения
        if (oxygen > 50) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (oxygen > 25) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Заполненная часть
        float currentBarWidth = (oxygen / GameSettings.START_OXYGEN) * width;
        shapeRenderer.rect(x, y, Math.max(0, currentBarWidth), height);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
