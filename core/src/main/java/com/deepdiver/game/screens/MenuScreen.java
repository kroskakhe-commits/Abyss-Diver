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
 * ГЛАВНОЕ МЕНЮ.
 *
 * Первое, что видит игрок. Отсюда можно начать игру, зайти в квесты,
 * обучение, магазин или настройки.
 *
 * Все кнопки расположены по центру экрана с возможностью тонкой настройки.
 */
public class MenuScreen implements Screen {
    final Main game;
    SpriteBatch batch;
    OrthographicCamera camera;
    BitmapFont font;                     // Простой шрифт для заголовка

    Rectangle startBtnBounds;            // Кнопка PLAY (большая, по центру справа)
    Rectangle questBtnBounds;            // Кнопка КВЕСТЫ
    Rectangle tutBtnBounds;              // Кнопка ОБУЧЕНИЕ
    Rectangle shopBtnBounds;             // Кнопка МАГАЗИН
    Rectangle settingsBtnBounds;         // Шестерёнка (настройки)
    Vector3 touchPoint;                  // Для обработки касаний

    public MenuScreen(final Main game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();         // Стандартный шрифт LibGDX (для заголовка)

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        float centerX = GameSettings.DESIGN_WIDTH / 2;
        float centerY = GameSettings.DESIGN_HEIGHT / 2;

        // ============================================================
        // 🎯 НАСТРОЙКИ РАСПОЛОЖЕНИЯ КНОПОК — МЕНЯЙ ЗДЕСЬ!
        // ============================================================

        // ----- РАЗМЕРЫ КНОПОК -----
        float startW = 280;              // Ширина кнопки PLAY
        float startH = 280;              // Высота кнопки PLAY
        float smallW = 260;              // Ширина маленьких кнопок
        float smallH = 120;              // Высота маленьких кнопок
        float iconSize = 60;             // Размер шестерёнки

        // ----- КНОПКА PLAY (большая, справа от центра) -----
        float startOffsetX = 550;        // Смещение по X (положительное = правее)
        float startOffsetY = -200;       // Смещение по Y (отрицательное = ниже)

        float startX = centerX + startOffsetX;
        float startY = centerY + startOffsetY;

        // ----- КНОПКИ КВЕСТЫ, ОБУЧЕНИЕ, МАГАЗИН (вертикальный стек слева) -----
        float firstButtonY = centerY - 10;      // Y первой кнопки
        float buttonSpacing = 130;               // Расстояние между кнопками

        // ============================================================
        // РАСЧЁТ КООРДИНАТ (НЕ ТРОГАТЬ, ЕСЛИ НЕ УВЕРЕН)
        // ============================================================

        // КНОПКА PLAY — центрируем по X и Y
        startBtnBounds = new Rectangle(
            GameSettings.x(startX - startW / 2),
            GameSettings.y(startY - startH / 2),
            GameSettings.size(startW),
            GameSettings.size(startH)
        );

        // КНОПКА КВЕСТЫ — по центру по X
        questBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // КНОПКА ОБУЧЕНИЕ — ниже квестов
        tutBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY - buttonSpacing),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // КНОПКА МАГАЗИН — ещё ниже
        shopBtnBounds = new Rectangle(
            GameSettings.x(centerX - smallW / 2),
            GameSettings.y(firstButtonY - buttonSpacing * 2),
            GameSettings.size(smallW),
            GameSettings.size(smallH)
        );

        // ШЕСТЕРЁНКА — правый верхний угол
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
        // Чистим экран тёмно-синим цветом (на случай, если фон не загрузится)
        Gdx.gl.glClearColor(0.07f, 0.09f, 0.17f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // ФОН
        batch.draw(GameResources.backgroundMenu, 0, 0,
            GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        // ЗАГОЛОВОК "ABYSS DIVER" — крупно, золотом
        font.getData().setScale(GameSettings.size(2.2f));
        font.draw(batch, "ABYSS DIVER",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2f - 110),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100));

        // ПОДЗАГОЛОВОК — поменьше, бело-голубым
        game.textView.setScale(GameSettings.size(1.2f));
        game.textView.draw(batch, "Глубинный ныряльщик",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2f - 200),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 150), Color.WHITE);

        // РИСУЕМ КНОПКИ
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

        // ШЕСТЕРЁНКА (настройки)
        if (GameResources.settingsIcon != null) {
            batch.draw(GameResources.settingsIcon,
                settingsBtnBounds.x, settingsBtnBounds.y,
                settingsBtnBounds.width, settingsBtnBounds.height);
        }

        batch.end();

        handleInput();
    }

    /**
     * Обрабатываем нажатия на кнопки меню.
     * Координаты кнопок уже адаптированы под любой экран.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint);

            if (startBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                // Создаём НОВЫЙ игровой экран (сбрасываем прогресс текущей сессии)
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                return;
            }
            if (questBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new MissionBoardScreen(game));
                return;
            }
            if (tutBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.tutorialScreen);
                return;
            }
            if (shopBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new ShopScreen(game));
                return;
            }
            if (settingsBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.settingsScreen);
                return;
            }
        }
    }

    // ========== СТАНДАРТНЫЕ МЕТОДЫ SCREEN ==========
    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); font.dispose(); }
}
