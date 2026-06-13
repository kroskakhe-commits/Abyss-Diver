package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;
import com.deepdiver.game.managers.MissionManager;

/**
 * Экран победы.
 * Отображается при выполнении всех 30 заданий.
 * Показывает итоговую статистику прохождения.
 * Рекорд уже сохранён в GameScreen до вызова этого экрана.
 */
public class VictoryScreen implements Screen {
    private final Main game;
    private Rectangle menuBtnBounds;
    private Rectangle resetBtnBounds;
    private final Vector3 touchPoint;

    private final int finalScore;
    private final int finalDistance;
    private final int crystalsNormal;
    private final int crystalsGold;
    private final int bubbles;

    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);

    public VictoryScreen(Main game, int score, int distance,
                         int crystalsNormal, int crystalsGold, int bubbles) {
        this.game = game;
        this.finalScore = score;
        this.finalDistance = distance;
        this.crystalsNormal = crystalsNormal;
        this.crystalsGold = crystalsGold;
        this.bubbles = bubbles;

        this.touchPoint = new Vector3();
        calculateButtonPositions();
    }

    public VictoryScreen(Main game) {
        this.game = game;
        this.finalScore = 0;
        this.finalDistance = 0;
        this.crystalsNormal = 0;
        this.crystalsGold = 0;
        this.bubbles = 0;
        this.touchPoint = new Vector3();
        calculateButtonPositions();
    }

    private void calculateButtonPositions() {
        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float menuBottomY = GameSettings.y(80);
        float resetBottomY = GameSettings.y(10);

        float menuWidth = GameSettings.size(260);
        float menuHeight = GameSettings.size(80);
        float resetWidth = GameSettings.size(260);
        float resetHeight = GameSettings.size(200);
        float spacing = GameSettings.size(40);

        // Кнопка "В МЕНЮ" (слева, выше)
        menuBtnBounds = new Rectangle(
            centerX - menuWidth - spacing / 2,
            menuBottomY,
            menuWidth,
            menuHeight
        );

        // Кнопка "СБРОС" (справа, ниже)
        resetBtnBounds = new Rectangle(
            centerX + spacing / 2,
            resetBottomY,
            resetWidth,
            resetHeight
        );
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        handleInput();

        game.batch.begin();

        // Фон победы
        if (GameResources.backgroundVictory != null) {
            game.batch.draw(GameResources.backgroundVictory, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        // Заголовок
        game.textView.setScale(GameSettings.size(2.5f));
        game.textView.draw(game.batch, "★ ПОБЕДА! ★",
            centerX - GameSettings.size(200),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 120), gold);

        game.textView.setScale(GameSettings.size(1.5f));
        game.textView.draw(game.batch, "МОЛОДЕЦ!",
            centerX - GameSettings.size(160),
            centerY + GameSettings.size(130), oxygenOk);

        // Статистика
        if (finalDistance > 0) {
            game.textView.setScale(GameSettings.size(1.2f));
            game.textView.draw(game.batch, "Дистанция: " + finalDistance + " метров",
                centerX - GameSettings.size(200),
                centerY + GameSettings.size(60), bioCyan);

            game.textView.draw(game.batch, "Счёт: " + finalScore + " очков",
                centerX - GameSettings.size(150),
                centerY - GameSettings.size(20), gold);
        }

        // Поздравление
        game.textView.draw(game.batch, "Ты выполнил все " + GameSettings.MISSIONS_COUNT + " заданий",
            centerX - GameSettings.size(300),
            centerY - GameSettings.size(80), bioCyan);

        game.textView.draw(game.batch, "и стал настоящим Хранителем Океана!",
            centerX - GameSettings.size(400),
            centerY - GameSettings.size(140), gold);

        // Кнопка "В МЕНЮ"
        if (GameResources.buttonBackMenuMarine != null) {
            game.batch.draw(GameResources.buttonBackMenuMarine,
                menuBtnBounds.x, menuBtnBounds.y,
                menuBtnBounds.width, menuBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.2f));
            game.textView.draw(game.batch, "В МЕНЮ",
                menuBtnBounds.x + GameSettings.size(80),
                menuBtnBounds.y + GameSettings.size(45), bioCyan);
        }

        // Кнопка "СБРОС" (сброс прогресса заданий)
        if (GameResources.victoryResetBtn != null) {
            game.batch.draw(GameResources.victoryResetBtn,
                resetBtnBounds.x, resetBtnBounds.y,
                resetBtnBounds.width, resetBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.3f));
            game.textView.draw(game.batch, "СБРОС",
                resetBtnBounds.x + GameSettings.size(100),
                resetBtnBounds.y + GameSettings.size(50), oxygenOk);
        }

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPoint);

            if (menuBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }

            if (resetBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                MissionManager.resetProgress();
                game.setScreen(new MissionBoardScreen(game));
                return;
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) { calculateButtonPositions(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
