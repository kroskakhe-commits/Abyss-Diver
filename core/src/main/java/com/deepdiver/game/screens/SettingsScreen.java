package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;
import com.deepdiver.game.components.MovingBackgroundView;
import com.deepdiver.game.components.TextView;
import com.deepdiver.game.managers.MemoryManager;

/**
 * Экран настроек.
 * Позволяет включать/выключать музыку и звуковые эффекты.
 * Настройки сохраняются в памяти и применяются мгновенно.
 */
public class SettingsScreen extends ScreenAdapter {

    Main main;
    MovingBackgroundView backgroundView;
    TextView titleTextView;
    TextView musicSettingView;
    TextView soundSettingView;

    private Rectangle backBtnBounds;
    private Rectangle musicBtnBounds;
    private Rectangle soundBtnBounds;
    private Vector3 touchPoint;

    private ShapeRenderer shapeRenderer;

    public SettingsScreen(Main main) {
        this.main = main;
        this.touchPoint = new Vector3();
        this.shapeRenderer = new ShapeRenderer();

        backgroundView = new MovingBackgroundView(GameResources.backgroundMenu);

        // Размеры и позиции элементов
        float panelWidth = GameSettings.size(500);
        float panelHeight = GameSettings.size(280);
        float panelOffsetY = GameSettings.y(0);

        float backSize = GameSettings.size(60);
        float backOffsetX = GameSettings.x(30);
        float backOffsetY = GameSettings.y(30);

        float titleOffsetY = GameSettings.y(-20);

        float itemWidth = GameSettings.size(400);
        float itemHeight = GameSettings.size(60);
        float itemOffsetX = GameSettings.x(60);
        float lineSpacing = GameSettings.size(90);
        float textDownOffset = GameSettings.size(40);

        // Расчёт координат
        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        float panelX = centerX - panelWidth / 2;
        float panelY = centerY - panelHeight / 2 + panelOffsetY;

        // Кнопка "НАЗАД"
        backBtnBounds = new Rectangle(
            backOffsetX,
            GameSettings.SCREEN_HEIGHT - backSize - backOffsetY,
            backSize,
            backSize
        );

        // Заголовок
        titleTextView = new TextView(main.textView.getFont(),
            centerX - GameSettings.size(200),
            panelY + panelHeight + GameSettings.size(40) + titleOffsetY,
            "НАСТРОЙКИ");

        // Зоны нажатия для строк настроек
        float itemX = panelX + (panelWidth - itemWidth) / 2;
        float firstItemY = panelY + panelHeight - GameSettings.size(80) - textDownOffset;

        musicBtnBounds = new Rectangle(itemX, firstItemY, itemWidth, itemHeight);
        soundBtnBounds = new Rectangle(itemX, firstItemY - lineSpacing, itemWidth, itemHeight);

        // Тексты настроек
        musicSettingView = new TextView(main.textView.getFont(),
            itemX + itemOffsetX,
            firstItemY + GameSettings.size(40),
            "музыка: " + translateStateToText(MemoryManager.loadIsMusicOn()));

        soundSettingView = new TextView(main.textView.getFont(),
            itemX + itemOffsetX,
            firstItemY + GameSettings.size(40) - lineSpacing,
            "звуки: " + translateStateToText(MemoryManager.loadIsSoundOn()));
    }

    @Override
    public void render(float delta) {
        handleInput();

        main.camera.update();
        main.batch.setProjectionMatrix(main.camera.combined);
        shapeRenderer.setProjectionMatrix(main.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        // Движущийся фон
        backgroundView.update(delta);
        main.batch.begin();
        backgroundView.draw(main.batch);
        main.batch.end();

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        float panelWidth = GameSettings.size(500);
        float panelHeight = GameSettings.size(280);
        float panelX = centerX - panelWidth / 2;
        float panelY = centerY - panelHeight / 2;

        // Полупрозрачная панель
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.25f, 0.85f));
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        // Обводка панели
        shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.6f, 0.9f));
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
        shapeRenderer.rect(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10);
        shapeRenderer.end();

        // Отрисовка текста
        main.batch.begin();

        // Заголовок
        main.textView.setScale(GameSettings.size(1.8f));
        main.textView.draw(main.batch, titleTextView.text,
            titleTextView.x, titleTextView.y,
            Color.GOLD);

        // Настройки
        main.textView.setScale(GameSettings.size(1.3f));
        main.textView.draw(main.batch, musicSettingView.text,
            musicSettingView.x, musicSettingView.y,
            Color.WHITE);
        main.textView.draw(main.batch, soundSettingView.text,
            soundSettingView.x, soundSettingView.y,
            Color.WHITE);

        // Кнопка "НАЗАД"
        if (GameResources.buttonBackMarine != null) {
            main.batch.draw(GameResources.buttonBackMarine,
                backBtnBounds.x, backBtnBounds.y,
                backBtnBounds.width, backBtnBounds.height);
        } else {
            main.textView.setScale(GameSettings.size(1.5f));
            main.textView.draw(main.batch, "← НАЗАД",
                backBtnBounds.x + GameSettings.size(15),
                backBtnBounds.y + GameSettings.size(40),
                Color.CYAN);
        }

        main.batch.end();
    }

    /**
     * Обрабатывает нажатия на кнопки настроек.
     */
    void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touchPoint);

            // Кнопка "НАЗАД"
            if (backBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                main.setScreen(main.menuScreen);
                return;
            }

            // Переключение музыки
            if (musicBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                boolean newState = !MemoryManager.loadIsMusicOn();
                MemoryManager.saveMusicSettings(newState);
                musicSettingView.setText("музыка: " + translateStateToText(newState));
                main.audioManager.updateMusicFlag();
                return;
            }

            // Переключение звуков
            if (soundBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                boolean newState = !MemoryManager.loadIsSoundOn();
                MemoryManager.saveSoundSettings(newState);
                soundSettingView.setText("звуки: " + translateStateToText(newState));
                main.audioManager.updateSoundFlag();
                return;
            }
        }
    }

    /**
     * Преобразует булево значение в текстовое представление.
     * @param state true для ON, false для OFF
     * @return строка "ON" или "OFF"
     */
    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
