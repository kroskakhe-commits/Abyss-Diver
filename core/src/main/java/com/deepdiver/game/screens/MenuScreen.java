package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;

/**
 * Главное меню игры.
 * Содержит кнопки для перехода на другие экраны:
 * - Игра
 * - Квесты
 * - Обучение
 * - Магазин
 * - Рекорды
 * - Настройки
 */
public class MenuScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    OrthographicCamera camera;
    BitmapFont font;

    Rectangle startBtnBounds;
    Rectangle questBtnBounds;
    Rectangle tutBtnBounds;
    Rectangle shopBtnBounds;
    Rectangle recordsBtnBounds;
    Rectangle settingsBtnBounds;
    Vector3 touchPoint;

    public MenuScreen(final Main game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        float centerX = GameSettings.DESIGN_WIDTH / 2;
        float centerY = GameSettings.DESIGN_HEIGHT / 2;

        // Размеры кнопок
        float startW = 280;
        float startH = 280;
        float smallW = 260;
        float smallH = 120;
        float iconSize = 60;
        float recordsSize = 300;

        // Кнопка PLAY (сдвинута вправо)
        float startOffsetX = 550;
        float startOffsetY = -200;
        float startX = centerX + startOffsetX;
        float startY = centerY + startOffsetY;

        // Вертикальное расположение кнопок
        float firstButtonY = centerY - 10;
        float buttonSpacing = 130;

        // Кнопка PLAY
        startBtnBounds = new Rectangle(
            GameSettings.x(startX - startW / 2),
            GameSettings.y(startY - startH / 2),
            GameSettings.size(startW),
            GameSettings.size(startH)
        );

        // Кнопка КВЕСТЫ
        questBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // Кнопка ОБУЧЕНИЕ
        tutBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY - buttonSpacing),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // Кнопка МАГАЗИН
        shopBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY - buttonSpacing * 2),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // Кнопка РЕКОРДЫ
        recordsBtnBounds = new Rectangle(
            GameSettings.x(centerX - 570),
            GameSettings.y(firstButtonY - 350),
            GameSettings.size(recordsSize),
            GameSettings.size(recordsSize)
        );

        // Кнопка НАСТРОЙКИ (шестерёнка)
        settingsBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - 75),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 75),
            GameSettings.size(iconSize),
            GameSettings.size(iconSize)
        );

        touchPoint = new Vector3();
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0.07f, 0.09f, 0.17f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Фон
        batch.draw(GameResources.backgroundMenu, 0, 0,
            GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        // Заголовок игры
        font.getData().setScale(GameSettings.size(2.2f));
        font.draw(batch, "ABYSS DIVER",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2f - 110),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100));

        // Подзаголовок
        game.textView.setScale(GameSettings.size(1.2f));
        game.textView.draw(batch, "Глубинный ныряльщик",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2f - 200),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 150), Color.WHITE);

        // Отрисовка кнопок
        if (GameResources.buttonStartMarine != null) {
            batch.draw(GameResources.buttonStartMarine,
                startBtnBounds.x, startBtnBounds.y,
                startBtnBounds.width, startBtnBounds.height);
        }
        if (GameResources.buttonQuestMarine != null) {
            batch.draw(GameResources.buttonQuestMarine,
                questBtnBounds.x, questBtnBounds.y,
                questBtnBounds.width, questBtnBounds.height);
        }
        if (GameResources.buttonTutorialMarine != null) {
            batch.draw(GameResources.buttonTutorialMarine,
                tutBtnBounds.x, tutBtnBounds.y,
                tutBtnBounds.width, tutBtnBounds.height);
        }
        if (GameResources.buttonShopMarine != null) {
            batch.draw(GameResources.buttonShopMarine,
                shopBtnBounds.x, shopBtnBounds.y,
                shopBtnBounds.width, shopBtnBounds.height);
        }
        if (GameResources.buttonRecordsMarine != null) {
            batch.draw(GameResources.buttonRecordsMarine,
                recordsBtnBounds.x, recordsBtnBounds.y,
                recordsBtnBounds.width, recordsBtnBounds.height);
        }
        if (GameResources.settingsIcon != null) {
            batch.draw(GameResources.settingsIcon,
                settingsBtnBounds.x, settingsBtnBounds.y,
                settingsBtnBounds.width, settingsBtnBounds.height);
        }

        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint);

            // Начать игру
            if (startBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                return;
            }
            // Квесты
            if (questBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new MissionBoardScreen(game));
                return;
            }
            // Обучение
            if (tutBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.tutorialScreen);
                return;
            }
            // Магазин
            if (shopBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new ShopScreen(game));
                return;
            }
            // Рекорды
            if (recordsBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new RecordsScreen(game));
                return;
            }
            // Настройки
            if (settingsBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.settingsScreen);
                return;
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
