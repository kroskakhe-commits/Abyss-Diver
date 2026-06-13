package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;
import com.deepdiver.game.managers.MemoryManager;

import java.util.ArrayList;

/**
 * Экран таблицы рекордов.
 * Отображает 5 лучших результатов игрока.
 * Позволяет очистить все рекорды кнопкой сброса.
 */
public class RecordsScreen implements Screen {
    private final Main game;
    private Rectangle backBtnBounds;
    private Rectangle resetRecordsBtnBounds;
    private Vector3 touchPoint;
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;
    private GlyphLayout glyphLayout;

    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    private ArrayList<Integer> records;

    public RecordsScreen(Main game) {
        this.game = game;
        this.touchPoint = new Vector3();
        this.shapeRenderer = new ShapeRenderer();
        this.glyphLayout = new GlyphLayout();

        // Настройка шрифта
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.round(GameSettings.size(24));
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюЯABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"´`'<>«» ";
        this.uiFont = generator.generateFont(parameter);
        generator.dispose();

        loadRecords();
        calculateButtonPositions();
    }

    /**
     * Загружает рекорды из памяти.
     */
    private void loadRecords() {
        records = MemoryManager.loadRecordsTable();
        if (records == null) records = new ArrayList<>();
        records.sort((a, b) -> b - a);
    }

    /**
     * Рассчитывает позиции кнопок на экране.
     */
    private void calculateButtonPositions() {
        float btnW = GameSettings.size(260);
        float btnH = GameSettings.size(70);

        // Кнопка "НАЗАД" (слева внизу)
        backBtnBounds = new Rectangle(
            GameSettings.x(50),
            GameSettings.y(60),
            btnW,
            btnH
        );

        // Кнопка сброса рекордов (справа внизу)
        float resetSize = GameSettings.size(80);
        resetRecordsBtnBounds = new Rectangle(
            GameSettings.SCREEN_WIDTH - resetSize - GameSettings.x(50),
            GameSettings.y(55),
            resetSize,
            resetSize
        );
    }

    @Override
    public void render(float delta) {
        // Настройка камеры
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        Gdx.gl.glClearColor(0.03f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // Фон
        game.batch.begin();
        if (GameResources.backgroundMenu != null) {
            game.batch.draw(GameResources.backgroundMenu, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        game.batch.end();

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        game.batch.begin();

        // Заголовок
        game.textView.setScale(GameSettings.size(2.0f));
        game.textView.draw(game.batch, "ТАБЛИЦА РЕКОРДОВ",
            centerX - GameSettings.size(290),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100),
            gold);

        game.textView.setScale(GameSettings.size(1.2f));

        // Отображение рекордов
        if (records.isEmpty()) {
            game.textView.draw(game.batch, "Пока нет рекордов",
                centerX - GameSettings.size(120),
                centerY,
                primaryText);
        } else {
            for (int i = 0; i < records.size() && i < 5; i++) {
                String medal = "";
                if (i == 0) medal = "🥇 ";
                else if (i == 1) medal = "🥈 ";
                else if (i == 2) medal = "🥉 ";

                String text = medal + (i + 1) + ". " + records.get(i) + " метров";
                float yPos = centerY + GameSettings.size(120) - i * GameSettings.size(60);
                game.textView.draw(game.batch, text,
                    centerX - GameSettings.size(120),
                    yPos,
                    (i == 0) ? gold : primaryText);
            }
        }

        // Кнопка "НАЗАД"
        if (GameResources.buttonBackMenuMarine != null) {
            game.batch.draw(GameResources.buttonBackMenuMarine,
                backBtnBounds.x, backBtnBounds.y,
                backBtnBounds.width, backBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.4f));
            game.textView.draw(game.batch, "НАЗАД",
                backBtnBounds.x + GameSettings.size(60),
                backBtnBounds.y + GameSettings.size(45),
                bioCyan);
        }

        // Кнопка сброса рекордов
        if (GameResources.buttonResetMarine != null) {
            game.batch.draw(GameResources.buttonResetMarine,
                resetRecordsBtnBounds.x, resetRecordsBtnBounds.y,
                resetRecordsBtnBounds.width, resetRecordsBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.2f));
            game.textView.draw(game.batch, "🗑",
                resetRecordsBtnBounds.x + GameSettings.size(30),
                resetRecordsBtnBounds.y + GameSettings.size(55),
                oxygenCritical);
        }

        game.batch.end();
    }

    /**
     * Обрабатывает нажатия на кнопки.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPoint);

            // Возврат в меню
            if (backBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }

            // Сброс всех рекордов
            if (resetRecordsBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                MemoryManager.clearAllRecords();
                loadRecords();
                return;
            }
        }
    }

    @Override
    public void show() {
        loadRecords();
    }

    @Override
    public void resize(int w, int h) {
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        calculateButtonPositions();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
