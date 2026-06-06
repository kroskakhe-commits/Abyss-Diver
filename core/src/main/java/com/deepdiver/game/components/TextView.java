package com.deepdiver.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

/**
 * Универсальный текст с поддержкой кириллицы и масштабирования.
 * Загружает TTF-шрифт, сразу включает все нужные символы.
 */
public class TextView implements Disposable {
    private BitmapFont font;
    private float scale = 1.0f;
    public String text;   // не везде используется, но пусть будет
    public float x, y;

    public TextView() {
        String fontPath = "fonts/Roboto-Bold.ttf";
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;

        // БЕЗ ЭТОГО РУССКИЙ НЕ РАБОТАЕТ
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"'`<>«» ";

        font = generator.generateFont(parameter);
        generator.dispose();
        setScale(1.2f);
    }

    public TextView(BitmapFont font, float x, float y, String text) {
        this.font = font;
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public void draw(SpriteBatch batch, String text, float x, float y, Color color) {
        font.setColor(color);
        font.getData().setScale(scale);
        font.draw(batch, text, x, y);
    }

    public void setScale(float scale) {
        this.scale = scale;
        font.getData().setScale(scale);
    }

    public void setText(String text) {
        this.text = text;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
