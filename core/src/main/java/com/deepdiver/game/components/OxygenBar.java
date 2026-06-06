package com.deepdiver.game.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.deepdiver.game.GameSettings;

/**
 * Простая шкала кислорода.
 * Серый фон, поверх цветная полоска.
 * Цвет зависит от уровня: зелёный (>50), оранжевый (25–50), красный (<25).
 */
public class OxygenBar {
    private final ShapeRenderer shapeRenderer; // Рисуем примитивы, не текстуры

    public OxygenBar() {
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * @param oxygen текущий запас кислорода (0–100)
     * @param x      левый верхний угол шкалы
     * @param y      левый верхний угол шкалы
     * @param width  полная ширина шкалы
     * @param height высота полоски
     */
    public void draw(float oxygen, float x, float y, float width, float height) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Тёмно-серый фон
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, width, height);

        // Выбираем цвет в зависимости от уровня
        if (oxygen > 50) {
            shapeRenderer.setColor(Color.GREEN);
        } else if (oxygen > 25) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Заполняем пропорциональную ширину
        float currentBarWidth = (oxygen / GameSettings.START_OXYGEN) * width;
        shapeRenderer.rect(x, y, Math.max(0, currentBarWidth), height);

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
