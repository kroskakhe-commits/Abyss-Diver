package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;
import com.deepdiver.game.managers.MissionManager;

/**
 * ЭКРАН КВЕСТОВ (ДОСКА ЗАДАНИЙ).
 *
 * Показывает текущее задание, его описание и цель.
 * Если игра пройдена — показывает победный экран.
 *
 * Кнопки:
 * - НАЧАТЬ ПОГРУЖЕНИЕ — запускает игру
 * - НАЗАД В МЕНЮ — возврат в главное меню
 * - СБРОС (маленькая кнопка справа вверху) — сбрасывает прогресс всех квестов
 */
public class MissionBoardScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle startBtnBounds, backBtnBounds, resetBtnBounds;
    private Vector3 touchPoint;

    // Цвета для красивого отображения
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    public MissionBoardScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.touchPoint = new Vector3();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        // ========== КНОПКИ ВНИЗУ ЭКРАНА ==========
        float btnWidth = GameSettings.size(240);
        float btnHeight = GameSettings.size(80);
        float btnSpacing = GameSettings.size(30);
        float bottomOffset = GameSettings.y(60);
        float buttonsShiftRight = GameSettings.x(60);   // Сдвиг вправо (на случай кривого дизайна)

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float totalWidth = btnWidth * 2 + btnSpacing;
        float startX = centerX - totalWidth / 2 + buttonsShiftRight;

        startBtnBounds = new Rectangle(startX, bottomOffset, btnWidth, btnHeight);
        backBtnBounds = new Rectangle(startX + btnWidth + btnSpacing, bottomOffset, btnWidth, btnHeight);

        // КНОПКА СБРОСА — маленькая иконка в правом верхнем углу
        float resetSize = GameSettings.size(70);
        resetBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - resetSize - 20),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - resetSize - 20),
            resetSize, resetSize
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();

        batch.begin();

        // ФОН
        if (GameResources.backgroundMenu != null) {
            batch.draw(GameResources.backgroundMenu, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        // ========== ЕСЛИ ВСЕ КВЕСТЫ ВЫПОЛНЕНЫ ==========
        if (MissionManager.isGameComplete()) {
            // ПОБЕДНЫЙ ЭКРАН
            game.textView.setScale(GameSettings.size(2.2f));
            game.textView.draw(batch, "★ ИГРА ПРОЙДЕНА ★",
                centerX - GameSettings.size(150),
                GameSettings.y(GameSettings.DESIGN_HEIGHT - 90), gold);

            game.textView.setScale(GameSettings.size(1.3f));
            game.textView.draw(batch, "Ты выполнил все 6 заданий!",
                centerX - GameSettings.size(140),
                centerY + GameSettings.size(80), oxygenOk);
            game.textView.draw(batch, "Ты стал Хранителем Океана!",
                centerX - GameSettings.size(150),
                centerY + GameSettings.size(30), bioCyan);

            // КНОПКИ внизу
            if (GameResources.buttonStartDiveMarine != null) {
                batch.draw(GameResources.buttonStartDiveMarine,
                    startBtnBounds.x, startBtnBounds.y,
                    startBtnBounds.width, startBtnBounds.height);
            }
            if (GameResources.buttonBackMenuMarine != null) {
                batch.draw(GameResources.buttonBackMenuMarine,
                    backBtnBounds.x, backBtnBounds.y,
                    backBtnBounds.width, backBtnBounds.height);
            }
        }
        // ========== ОБЫЧНЫЙ ЭКРАН КВЕСТОВ (НЕ ВСЕ ВЫПОЛНЕНЫ) ==========
        else {
            // ЗАГОЛОВОК
            float titleY = GameSettings.y(GameSettings.DESIGN_HEIGHT - 90);
            game.textView.setScale(GameSettings.size(1.8f));
            game.textView.draw(batch, "КВЕСТ-ЛИНИЯ",
                centerX - GameSettings.size(180), titleY, gold);

            // НОМЕР ЗАДАНИЯ
            int currentMissionNum = MissionManager.getCurrentMission() + 1;
            game.textView.setScale(GameSettings.size(1.3f));
            String missionNumText = "ЗАДАНИЕ " + currentMissionNum + " ИЗ " + GameSettings.MISSIONS_COUNT;
            float missionNumberY = centerY + GameSettings.size(160);
            game.textView.draw(batch, missionNumText,
                centerX - missionNumText.length() * GameSettings.size(10),
                missionNumberY, bioCyan);

            // ОПИСАНИЕ ЗАДАНИЯ
            String description = MissionManager.getCurrentMissionDescription();
            game.textView.setScale(GameSettings.size(1.6f));
            float descriptionY = missionNumberY - GameSettings.size(80);
            game.textView.draw(batch, description,
                centerX - description.length() * GameSettings.size(15),
                descriptionY, oxygenOk);

            // ЦЕЛЬ (цифры)
            int target = MissionManager.getCurrentMissionTarget();
            int missionType = MissionManager.getCurrentMissionType();
            String targetText = "";
            switch (missionType) {
                case GameSettings.MISSION_TYPE_DISTANCE:
                    targetText = target + " метров";
                    break;
                case GameSettings.MISSION_TYPE_CRYSTALS_NORMAL:
                    targetText = target + " кристаллов";
                    break;
                case GameSettings.MISSION_TYPE_CRYSTALS_GOLD:
                    targetText = target + " кристаллов";
                    break;
                case GameSettings.MISSION_TYPE_BUBBLES:
                    targetText = target + " пузырьков";
                    break;
            }

            String goalText = "Цель: " + targetText;
            game.textView.setScale(GameSettings.size(1.3f));
            float goalY = descriptionY - GameSettings.size(180);
            game.textView.draw(batch, goalText,
                centerX - goalText.length() * GameSettings.size(10),
                goalY, gold);

            // КНОПКИ ВНИЗУ
            if (GameResources.buttonStartDiveMarine != null) {
                batch.draw(GameResources.buttonStartDiveMarine,
                    startBtnBounds.x, startBtnBounds.y,
                    startBtnBounds.width, startBtnBounds.height);
            }
            if (GameResources.buttonBackMenuMarine != null) {
                batch.draw(GameResources.buttonBackMenuMarine,
                    backBtnBounds.x, backBtnBounds.y,
                    backBtnBounds.width, backBtnBounds.height);
            }
        }

        // КНОПКА СБРОСА (всегда видна)
        if (GameResources.buttonResetMarine != null) {
            batch.draw(GameResources.buttonResetMarine,
                resetBtnBounds.x, resetBtnBounds.y,
                resetBtnBounds.width, resetBtnBounds.height);
        } else {
            game.textView.setScale(GameSettings.size(0.8f));
            game.textView.draw(batch, "СБРОС",
                resetBtnBounds.x + GameSettings.size(22),
                resetBtnBounds.y + GameSettings.size(28), oxygenCritical);
        }

        batch.end();
    }

    /**
     * Обрабатываем нажатия на кнопки.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint);

            // Кнопка "НАЧАТЬ ПОГРУЖЕНИЕ"
            if (startBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.gameScreen = new GameScreen(game);
                game.setScreen(game.gameScreen);
                return;
            }

            // Кнопка "НАЗАД В МЕНЮ"
            if (backBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(game.menuScreen);
                return;
            }

            // Кнопка "СБРОС" — сбрасываем весь прогресс квестов
            if (resetBtnBounds.contains(touchPoint.x, touchPoint.y)) {
                MissionManager.resetProgress();
            }
        }
    }

    @Override
    public void show() {
        MissionManager.loadProgress();   // Подгружаем актуальный прогресс при показе
    }

    @Override
    public void resize(int w, int h) {
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); }
}
