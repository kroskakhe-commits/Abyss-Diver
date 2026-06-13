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
import com.deepdiver.game.managers.MissionManager;

/**
 * Экран доски заданий (квестов).
 * Отображает все 30 заданий в виде горизонтального списка карточек.
 * Поддерживает скроллинг для просмотра всех заданий.
 * Автоматически прокручивается к текущему заданию при открытии.
 */
public class MissionBoardScreen implements Screen {
    private final Main game;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont missionFont;
    private GlyphLayout glyphLayout;

    private Vector3 touchPoint;
    private float scrollX = 0f;
    private float lastTouchX = 0f;
    private boolean isDragging = false;
    private boolean autoScrolled = false;

    private final float CARD_WIDTH = 340f;
    private final float CARD_HEIGHT = 260f;
    private final float CARD_SPACING = 25f;

    // Цветовые константы для интерфейса
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);
    private final Color darkBg = new Color(0.05f, 0.05f, 0.1f, 0.85f);

    public MissionBoardScreen(Main game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.glyphLayout = new GlyphLayout();
        this.touchPoint = new Vector3();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));

        // Настройка шрифта для заголовков
        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = Math.round(GameSettings.size(32));
        titleParam.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"'`<>«» ";
        this.titleFont = generator.generateFont(titleParam);

        // Настройка шрифта для описаний миссий
        FreeTypeFontGenerator.FreeTypeFontParameter missionParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        missionParam.size = Math.round(GameSettings.size(18));
        missionParam.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"'`<>«» ";
        this.missionFont = generator.generateFont(missionParam);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0.03f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        handleInput();

        // Отрисовка фона
        game.batch.begin();
        if (GameResources.backgroundMission != null) {
            game.batch.draw(GameResources.backgroundMission, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        } else if (GameResources.backgroundMenu != null) {
            game.batch.draw(GameResources.backgroundMenu, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        game.batch.end();

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        // Заголовок экрана
        game.batch.begin();
        titleFont.setColor(gold);
        titleFont.getData().setScale(GameSettings.size(1.2f));
        String title = "КВЕСТ-ЛИНИЯ";
        float titleWidth = titleFont.draw(game.batch, title, 0, 0).width;
        titleFont.draw(game.batch, title, centerX - titleWidth / 2, GameSettings.y(GameSettings.DESIGN_HEIGHT - 60));

        // Счётчик выполненных заданий
        int completed = MissionManager.getCompletedMissions();
        missionFont.setColor(primaryText);
        missionFont.getData().setScale(GameSettings.size(0.8f));
        String counter = "ВЫПОЛНЕНО: " + completed + " / " + GameSettings.MISSIONS_COUNT;
        float counterWidth = missionFont.draw(game.batch, counter, 0, 0).width;
        missionFont.draw(game.batch, counter, centerX - counterWidth / 2, GameSettings.y(GameSettings.DESIGN_HEIGHT - 140));
        game.batch.end();

        // Отрисовка карточек заданий
        float startX = GameSettings.SCREEN_WIDTH / 2 - (CARD_WIDTH / 2) + scrollX;

        for (int i = 0; i < GameSettings.MISSIONS_COUNT; i++) {
            float cardX = startX + i * (CARD_WIDTH + CARD_SPACING);
            float cardY = centerY - CARD_HEIGHT / 2;

            // Пропускаем карточки за пределами экрана
            if (cardX + CARD_WIDTH < 0 || cardX > GameSettings.SCREEN_WIDTH) {
                continue;
            }

            boolean isCompleted = i < completed;
            boolean isCurrent = i == completed;

            // Фон карточки (тёмный, полупрозрачный)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(darkBg);
            shapeRenderer.rect(cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
            shapeRenderer.end();

            // Рамка карточки (зелёная для выполненных, золотая для текущего, серая для будущих)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if (isCompleted) {
                shapeRenderer.setColor(oxygenOk);
            } else if (isCurrent) {
                shapeRenderer.setColor(gold);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
            }
            shapeRenderer.rect(cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
            shapeRenderer.end();

            // Номер задания
            game.batch.begin();
            if (isCompleted) {
                titleFont.setColor(oxygenOk);
                titleFont.draw(game.batch, "✓ " + (i + 1), cardX + 15, cardY + CARD_HEIGHT - 25);
            } else if (isCurrent) {
                titleFont.setColor(gold);
                titleFont.draw(game.batch, "▶ " + (i + 1), cardX + 15, cardY + CARD_HEIGHT - 25);
            } else {
                titleFont.setColor(primaryText);
                titleFont.draw(game.batch, "" + (i + 1), cardX + 15, cardY + CARD_HEIGHT - 25);
            }
            game.batch.end();

            // Данные задания
            Object[] missionData = GameSettings.MISSIONS[i];
            String description = (String) missionData[2];
            int target = (int) missionData[1];
            int type = (int) missionData[0];

            // Формирование текста цели в зависимости от типа задания
            String targetText = "";
            switch (type) {
                case GameSettings.MISSION_TYPE_DISTANCE:
                    targetText = "Цель: " + target + " м";
                    break;
                case GameSettings.MISSION_TYPE_CRYSTALS_NORMAL:
                    targetText = "Цель: " + target + " кристаллов";
                    break;
                case GameSettings.MISSION_TYPE_CRYSTALS_GOLD:
                    targetText = "Цель: " + target + " золотых";
                    break;
                case GameSettings.MISSION_TYPE_BUBBLES:
                    targetText = "Цель: " + target + " пузырьков";
                    break;
            }

            // Отрисовка описания задания (с переносом длинных строк)
            game.batch.begin();
            missionFont.setColor(isCompleted ? oxygenOk : (isCurrent ? gold : primaryText));
            missionFont.getData().setScale(GameSettings.size(0.68f));

            int charsPerLine = 22;
            String[] words = description.split(" ");
            StringBuilder currentLine = new StringBuilder();
            float textY = cardY + CARD_HEIGHT - 105;

            for (String word : words) {
                if (currentLine.length() + word.length() + 1 <= charsPerLine) {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(word);
                } else {
                    missionFont.draw(game.batch, currentLine.toString(), cardX + 15, textY);
                    textY -= 22;
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) {
                missionFont.draw(game.batch, currentLine.toString(), cardX + 15, textY);
            }

            // Отрисовка цели задания
            missionFont.setColor(bioCyan);
            missionFont.getData().setScale(GameSettings.size(0.65f));
            missionFont.draw(game.batch, targetText, cardX + 15, cardY + 35);
            game.batch.end();
        }

        drawButtons();
    }

    /**
     * Обрабатывает ввод пользователя:
     * - Скроллинг карточек при перетаскивании пальцем
     * - Нажатия на кнопки "НАЧАТЬ ИГРУ", "НАЗАД", "СБРОС"
     */
    private void handleInput() {
        // Обработка скроллинга
        if (Gdx.input.justTouched()) {
            isDragging = true;
            lastTouchX = Gdx.input.getX();
        }

        if (Gdx.input.isTouched() && isDragging) {
            autoScrolled = true;
            float currentX = Gdx.input.getX();
            float deltaX = currentX - lastTouchX;
            scrollX += deltaX;
            lastTouchX = currentX;

            // Ограничение прокрутки (нельзя уйти за пределы)
            float maxScroll = (GameSettings.MISSIONS_COUNT - 3) * (CARD_WIDTH + CARD_SPACING);
            if (scrollX > 150) scrollX = 150;
            if (scrollX < -maxScroll) scrollX = -maxScroll;
        }

        if (!Gdx.input.isTouched()) {
            isDragging = false;
        }

        // Обработка нажатий на кнопки
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPoint);

            float btnWidth = GameSettings.size(240);
            float btnHeight = GameSettings.size(80);
            float btnSpacing = GameSettings.size(30);
            float bottomOffset = GameSettings.y(60);
            float buttonsShiftRight = GameSettings.x(20);
            float centerX = GameSettings.SCREEN_WIDTH / 2;
            float totalWidth = btnWidth * 2 + btnSpacing;
            float startX = centerX - totalWidth / 2 + buttonsShiftRight;

            // Области кнопок
            Rectangle startBtnBounds = new Rectangle(startX, bottomOffset, btnWidth, btnHeight);
            Rectangle backBtnBounds = new Rectangle(startX + btnWidth + btnSpacing, bottomOffset, btnWidth, btnHeight);
            Rectangle resetBtnBounds = new Rectangle(
                GameSettings.x(GameSettings.DESIGN_WIDTH - 100 - 20),
                GameSettings.y(GameSettings.DESIGN_HEIGHT - 100 - 20),
                100, 100
            );

            // Кнопка "НАЧАТЬ ИГРУ"
            if (startBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                return;
            }
            // Кнопка "НАЗАД"
            if (backBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }
            // Кнопка "СБРОС" (сброс прогресса заданий)
            if (resetBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                MissionManager.resetProgress();
                return;
            }
        }
    }

    /**
     * Отрисовывает кнопки в нижней части экрана:
     * - "НАЧАТЬ ИГРУ" (слева)
     * - "НАЗАД" (справа от кнопки старта)
     * - Кнопка сброса прогресса (в правом верхнем углу)
     */
    private void drawButtons() {
        float btnWidth = GameSettings.size(240);
        float btnHeight = GameSettings.size(80);
        float btnSpacing = GameSettings.size(30);
        float bottomOffset = GameSettings.y(60);
        float buttonsShiftRight = GameSettings.x(20);
        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float totalWidth = btnWidth * 2 + btnSpacing;
        float startX = centerX - totalWidth / 2 + buttonsShiftRight;

        // Кнопка сброса (в правом верхнем углу)
        Rectangle resetBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - 100 - 20),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100 - 20),
            100, 100
        );

        game.batch.begin();

        // Кнопка "НАЧАТЬ ИГРУ"
        if (GameResources.buttonStartDiveMarine != null) {
            game.batch.draw(GameResources.buttonStartDiveMarine, startX, bottomOffset, btnWidth, btnHeight);
        } else {
            titleFont.setColor(oxygenOk);
            titleFont.getData().setScale(GameSettings.size(0.8f));
            titleFont.draw(game.batch, "НАЧАТЬ", startX + 60, bottomOffset + 45);
        }

        // Кнопка "НАЗАД"
        if (GameResources.buttonBackMenuMarine != null) {
            game.batch.draw(GameResources.buttonBackMenuMarine, startX + btnWidth + btnSpacing, bottomOffset, btnWidth, btnHeight);
        } else {
            titleFont.setColor(bioCyan);
            titleFont.draw(game.batch, "НАЗАД", startX + btnWidth + btnSpacing + 60, bottomOffset + 45);
        }

        // Кнопка "СБРОС" (мусорка)
        if (GameResources.buttonResetMarine != null) {
            game.batch.draw(GameResources.buttonResetMarine, resetBtnBounds.x, resetBtnBounds.y, 100, 100);
        } else {
            titleFont.setColor(oxygenCritical);
            titleFont.getData().setScale(GameSettings.size(0.9f));
            titleFont.draw(game.batch, "СБРОС", resetBtnBounds.x + 35, resetBtnBounds.y + 65);
        }

        game.batch.end();
    }

    @Override
    public void show() {
        MissionManager.loadProgress();

        // Автоматическая прокрутка к текущему заданию (только при первом открытии)
        if (!autoScrolled) {
            int completed = MissionManager.getCompletedMissions();
            float cardFullWidth = CARD_WIDTH + CARD_SPACING;
            float targetScroll = -(completed * cardFullWidth) + (GameSettings.SCREEN_WIDTH / 2 - CARD_WIDTH / 2);

            float maxScroll = (GameSettings.MISSIONS_COUNT - 3) * cardFullWidth;
            if (targetScroll > 150) targetScroll = 150;
            if (targetScroll < -maxScroll) targetScroll = -maxScroll;

            scrollX = targetScroll;
        }
    }

    @Override
    public void resize(int w, int h) {
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        titleFont.dispose();
        missionFont.dispose();
    }
}
