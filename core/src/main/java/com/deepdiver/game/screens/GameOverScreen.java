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

/**
 * Экран окончания игры.
 * Отображается при смерти дайвера (кислород закончился).
 * Показывает итоговый счёт и дистанцию.
 * Рекорд уже сохранён в GameScreen до вызова этого экрана.
 */
public class GameOverScreen implements Screen {
    private final Main game;
    private final int finalScore;
    private final int finalDistance;

    private Rectangle restartBtnBounds;
    private Rectangle menuBtnBounds;
    private Vector3 touchPoint;

    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;
    private GlyphLayout glyphLayout;

    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    public GameOverScreen(Main game, int score, int distance) {
        this.game = game;
        this.finalScore = score;
        this.finalDistance = distance;

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

        calculateButtonPositions();
    }

    private void calculateButtonPositions() {
        float centerX = GameSettings.SCREEN_WIDTH / 2;

        // Кнопка рестарта (круглая)
        float restartSize = GameSettings.size(120);
        restartBtnBounds = new Rectangle(
            centerX - restartSize / 2,
            GameSettings.y(120),
            restartSize,
            restartSize
        );

        // Кнопка возврата в меню
        float menuW = GameSettings.size(260);
        float menuH = GameSettings.size(80);
        menuBtnBounds = new Rectangle(
            centerX - menuW / 2,
            GameSettings.y(30),
            menuW,
            menuH
        );
    }

    @Override
    public void render(float delta) {
        // Настройка камеры
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // Отрисовка фона
        game.batch.begin();
        if (GameResources.backgroundGame != null) {
            game.batch.draw(GameResources.backgroundGame, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        game.batch.end();

        // Затемнение
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        shapeRenderer.end();

        // Отрисовка текста
        game.batch.begin();

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        // Заголовок
        game.textView.setScale(GameSettings.size(2.5f));
        game.textView.draw(game.batch, "GAME OVER",
            centerX - GameSettings.size(240),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100),
            oxygenCritical);

        // Статистика
        game.textView.setScale(GameSettings.size(1.5f));
        game.textView.draw(game.batch, "Счёт: " + finalScore,
            centerX - GameSettings.size(150),
            centerY + GameSettings.size(80),
            gold);

        game.textView.draw(game.batch, "Дистанция: " + finalDistance + "м",
            centerX - GameSettings.size(230),
            centerY + GameSettings.size(20),
            bioCyan);

        // Кнопка рестарта
        if (GameResources.buttonRestartMarine != null) {
            game.batch.draw(GameResources.buttonRestartMarine,
                restartBtnBounds.x, restartBtnBounds.y,
                restartBtnBounds.width, restartBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.2f));
            game.textView.draw(game.batch, "⟳",
                restartBtnBounds.x + GameSettings.size(45),
                restartBtnBounds.y + GameSettings.size(75),
                oxygenCritical);
        }

        // Кнопка "В МЕНЮ"
        if (GameResources.buttonBackMenuMarine != null) {
            game.batch.draw(GameResources.buttonBackMenuMarine,
                menuBtnBounds.x, menuBtnBounds.y,
                menuBtnBounds.width, menuBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.3f));
            game.textView.draw(game.batch, "В МЕНЮ",
                menuBtnBounds.x + GameSettings.size(80),
                menuBtnBounds.y + GameSettings.size(45),
                bioCyan);
        }

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPoint);

            // Рестарт игры
            if (restartBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                return;
            }

            // Возврат в меню
            if (menuBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) { calculateButtonPositions(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
