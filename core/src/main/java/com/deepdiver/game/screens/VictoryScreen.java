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
 * ЭКРАН ПОБЕДЫ.
 *
 * Показывается, когда игрок выполнил все 6 квестов.
 * Поздравляет и предлагает два варианта:
 * 1. Вернуться в меню
 * 2. Сбросить прогресс квестов и начать заново
 *
 * СТАТИСТИКА: здесь можно было бы показывать результаты последней игры,
 * но для простоты оставлено только поздравление.
 */
public class VictoryScreen implements Screen {
    private final Main game;
    private Rectangle menuBtnBounds;
    private Rectangle resetBtnBounds;
    private final Vector3 touchPoint;

    // Цвета для красивого текста
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);

    /**
     * Конструктор с полной статистикой (может пригодиться в будущем).
     * Сейчас параметры не используются, но оставлены для совместимости.
     */
    public VictoryScreen(Main game, int score, int distance,
                         int crystalsNormal, int crystalsGold, int bubbles) {
        this.game = game;
        this.touchPoint = new Vector3();
        calculateButtonPositions();
    }

    public VictoryScreen(Main game) {
        this.game = game;
        this.touchPoint = new Vector3();
        calculateButtonPositions();
    }

    /**
     * Расчёт позиций кнопок.
     * Кнопки расположены вертикально: сверху "В МЕНЮ", снизу "СБРОСИТЬ РЕЗУЛЬТАТ".
     *
     * ВНИМАНИЕ: координаты подобраны так, чтобы кнопки были внизу экрана,
     * не перекрывая поздравительный текст.
     */
    private void calculateButtonPositions() {
        float centerX = GameSettings.SCREEN_WIDTH / 2;

        float btnW = GameSettings.size(260);           // Ширина обеих кнопок
        float menuBtnH = GameSettings.size(80);        // Высота кнопки "В МЕНЮ"
        float resetBtnH = GameSettings.size(120);      // Высота кнопки "СБРОС" (побольше)
        float btnSpacing = GameSettings.size(20);      // Расстояние между кнопками

        // Кнопки опущены ближе к низу экрана
        float startY = GameSettings.y(120);

        // ===== КНОПКА "В МЕНЮ" (верхняя) =====
        menuBtnBounds = new Rectangle(
            centerX - btnW/2,
            startY,
            btnW, menuBtnH
        );

        // ===== КНОПКА "СБРОСИТЬ РЕЗУЛЬТАТ" (нижняя, крупнее) =====
        resetBtnBounds = new Rectangle(
            centerX - btnW/2,
            startY - menuBtnH - btnSpacing,
            btnW, resetBtnH
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Обработка нажатий
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPoint);

            if (menuBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }

            if (resetBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                MissionManager.resetProgress();              // Сбрасываем квесты
                game.setScreen(new MissionBoardScreen(game)); // Показываем доску квестов
                return;
            }
        }

        game.batch.begin();

        // ФОН (победный, яркий)
        if (GameResources.backgroundVictory != null) {
            game.batch.draw(GameResources.backgroundVictory, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        // ========== ПОЗДРАВИТЕЛЬНЫЙ ТЕКСТ ==========
        game.textView.setScale(GameSettings.size(2.5f));
        game.textView.draw(game.batch, "★ ПОБЕДА! ★",
            centerX - GameSettings.size(200),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 120), gold);

        game.textView.setScale(GameSettings.size(1.5f));
        game.textView.draw(game.batch, "МОЛОДЕЦ!",
            centerX - GameSettings.size(160),
            centerY + GameSettings.size(100), oxygenOk);

        game.textView.setScale(GameSettings.size(1.2f));
        game.textView.draw(game.batch, "Ты выполнил все 6 заданий",
            centerX - GameSettings.size(280),
            centerY - GameSettings.size(10), bioCyan);

        game.textView.draw(game.batch, "и стал настоящим Хранителем Океана!",
            centerX - GameSettings.size(350),
            centerY - GameSettings.size(80), gold);

        // ========== КНОПКИ ==========
        // Кнопка "В МЕНЮ"
        if (GameResources.buttonBackMenuMarine != null) {
            game.batch.draw(GameResources.buttonBackMenuMarine,
                menuBtnBounds.x, menuBtnBounds.y,
                menuBtnBounds.width, menuBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.2f));
            game.textView.draw(game.batch, "В МЕНЮ",
                menuBtnBounds.x + GameSettings.size(80),
                menuBtnBounds.y + GameSettings.size(45), gold);
        }

        // Кнопка "СБРОСИТЬ РЕЗУЛЬТАТ"
        if (GameResources.victoryResetBtn != null) {
            game.batch.draw(GameResources.victoryResetBtn,
                resetBtnBounds.x, resetBtnBounds.y,
                resetBtnBounds.width, resetBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(1.1f));
            game.textView.draw(game.batch, "СБРОСИТЬ РЕЗУЛЬТАТ",
                resetBtnBounds.x + GameSettings.size(40),
                resetBtnBounds.y + GameSettings.size(55), oxygenOk);
        }

        game.batch.end();
    }

    @Override public void show() {}

    @Override
    public void resize(int w, int h) {
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        calculateButtonPositions();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
