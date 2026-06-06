package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.GameSession;
import com.deepdiver.game.Main;
import com.deepdiver.game.components.OxygenBar;
import com.deepdiver.game.managers.ContactManager;
import com.deepdiver.game.managers.MissionManager;
import com.deepdiver.game.objects.BubbleObject;
import com.deepdiver.game.objects.CrystalObject;
import com.deepdiver.game.objects.DiverObject;
import com.deepdiver.game.objects.JellyfishObject;

/**
 * ГЛАВНЫЙ ИГРОВОЙ ЭКРАН.
 *
 * Сердце всей игры. Здесь спавнятся объекты, проверяются столкновения,
 * отслеживаются квесты, рисуется интерфейс и обрабатывается пауза.
 *
 * ВНИМАНИЕ: Этот класс — монстр на 600+ строк. Не пугайся, всё разбито на логические блоки.
 * Если что-то меняешь в логике квестов или спавна — тестируй ОЧЕНЬ тщательно.
 */
public class GameScreen implements Screen {
    // ========== ОСНОВНЫЕ КОМПОНЕНТЫ ==========
    private final Main game;           // Родитель, чтобы добраться до batch, камеры, аудио
    private DiverObject diver;         // Главный герой
    private final OxygenBar oxygenBar; // Полоска кислорода (отдельный компонент)

    // ========== ИГРОВЫЕ ОБЪЕКТЫ (списки) ==========
    private final Array<CrystalObject> crystals = new Array<>();   // Кристаллы (синие и золотые)
    private final Array<JellyfishObject> jellyfish = new Array<>(); // Медузы-враги
    private final Array<BubbleObject> bubbles = new Array<>();      // Пузырьки кислорода

    // ========== ИГРОВАЯ СТАТИСТИКА ==========
    private float oxygen = GameSettings.START_OXYGEN;   // Текущий кислород (0-100)
    private float distance = 0f;                        // Метров проплыли за эту сессию
    private int score = 0;                              // Очки за эту сессию
    private float spawnTimer = 0f;                      // Таймер для спавна объектов
    private float bgX1, bgX2;                           // Позиции двух копий фона

    // ========== СОСТОЯНИЯ ИГРЫ ==========
    private boolean isPaused = false;                   // Пауза (меню)
    private boolean isQuestCompletePaused = false;      // Окно "Задание выполнено"
    private Rectangle pauseBtnBounds;                   // Зона нажатия кнопки паузы
    private Rectangle continueBtnBounds;                // Кнопка "Продолжить"
    private Rectangle menuBtnBounds;                    // Кнопка "В меню"

    // ========== ПРОГРЕСС ТЕКУЩЕГО КВЕСТА ==========
    private int crystalsNormalCollected = 0;   // Синих кристаллов собрано
    private int crystalsGoldCollected = 0;     // Золотых кристаллов собрано
    private int bubblesCollected = 0;          // Пузырьков собрано
    private float missionStartDistance = 0f;   // Какая дистанция была в начале квеста
    private boolean missionCompleted = false;  // Выполнили ли текущий квест
    private String lastCompletedMessage = "";  // Текст поздравления
    private float questTextTimer = 0f;         // Сколько секунд показывать подсказку о квесте

    // ========== ГРАФИЧЕСКИЕ КОМПОНЕНТЫ ==========
    private ShapeRenderer shapeRenderer;       // Для рисования прямоугольников (шкалы, подложки)
    private BitmapFont uiFont;                 // Шрифт для интерфейса (отдельный от TextView)
    private GlyphLayout glyphLayout;           // Чтобы измерять ширину текста

    // ========== ЦВЕТА ДЛЯ ИНТЕРФЕЙСА ==========
    // Стабильные, приятные глазу цвета. Не меняй без дизайнера.
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);      // Бирюзовый (акценты)
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);         // Золотой (ценные вещи)
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);  // Бело-голубой (основной текст)
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);      // Зелёный (кислород >50)
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f); // Красный (кислород <25)

    // ========== КОНСТРУКТОР ==========
    public GameScreen(Main game) {
        this.game = game;
        this.oxygenBar = new OxygenBar();                    // Можно было не использовать, но пусть живёт
        this.shapeRenderer = new ShapeRenderer();
        this.glyphLayout = new GlyphLayout();

        // Создаём свой шрифт для UI. Почему не TextView? Потому что нужен прямой BitmapFont
        // для измерения ширины текста (GlyphLayout). TextView этого не умеет.
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.round(GameSettings.size(24));
        // ВСЕ символы, включая кириллицу. Без этого русский текст не нарисуется.
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюЯABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"´`'<>«» ";
        this.uiFont = generator.generateFont(parameter);
        generator.dispose();

        initGame();  // Заполняем начальные значения
    }

    /**
     * Инициализация игры (или сброс при перезапуске с экрана квестов).
     *
     * Очищает все списки, обнуляет статистику, сбрасывает флаги.
     * Вызывается при создании экрана и при show().
     */
    private void initGame() {
        diver = new DiverObject();
        crystals.clear();
        jellyfish.clear();
        bubbles.clear();

        oxygen = GameSettings.START_OXYGEN;
        distance = 0f;
        score = 0;
        spawnTimer = 0f;
        isPaused = false;
        isQuestCompletePaused = false;
        bgX1 = 0;
        bgX2 = GameSettings.SCREEN_WIDTH;

        crystalsNormalCollected = 0;
        crystalsGoldCollected = 0;
        bubblesCollected = 0;
        missionStartDistance = 0f;
        missionCompleted = false;
        lastCompletedMessage = "";

        // Показываем подсказку с текущим квестом на 3 секунды
        if (!MissionManager.isGameComplete()) {
            questTextTimer = 3.0f;
        }

        // КНОПКА ПАУЗЫ — иконка в правом верхнем углу
        float pauseSize = GameSettings.size(80);
        float offsetX = GameSettings.size(3);
        float offsetY = GameSettings.size(3);

        pauseBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - pauseSize - offsetX),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - pauseSize - offsetY),
            pauseSize, pauseSize
        );

        // Кнопки в окнах паузы и квеста — по центру экрана
        float btnW = GameSettings.size(280);
        float btnH = GameSettings.size(80);
        continueBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - btnW / 2),
            GameSettings.y(GameSettings.DESIGN_HEIGHT / 2 + 20),
            btnW, btnH
        );
        menuBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - btnW / 2),
            GameSettings.y(GameSettings.DESIGN_HEIGHT / 2 - 70),
            btnW, btnH
        );
    }

    /**
     * Проверяет, выполнен ли текущий квест.
     *
     * Вызывается ПОСЛЕ КАЖДОГО действия игрока:
     * - каждый кадр (для квестов на дистанцию)
     * - после сбора кристалла
     * - после сбора пузырька
     *
     * Если квест выполнен — останавливаем игру, показываем поздравление,
     * увеличиваем счётчик выполненных миссий.
     */
    private void checkMissionProgress() {
        if (missionCompleted || MissionManager.isGameComplete()) return;

        int missionType = MissionManager.getCurrentMissionType();
        int target = MissionManager.getCurrentMissionTarget();
        boolean completed = false;
        String newMessage = "";

        switch (missionType) {
            case GameSettings.MISSION_TYPE_DISTANCE:
                if (distance - missionStartDistance >= target) {
                    completed = true;
                    newMessage = "Проплыто " + target + " метров!";
                }
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_NORMAL:
                if (crystalsNormalCollected >= target) {
                    completed = true;
                    newMessage = "Собрано " + target + " синих кристаллов!";
                }
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_GOLD:
                if (crystalsGoldCollected >= target) {
                    completed = true;
                    newMessage = "Собрано " + target + " золотых кристаллов!";
                }
                break;
            case GameSettings.MISSION_TYPE_BUBBLES:
                if (bubblesCollected >= target) {
                    completed = true;
                    newMessage = "Собрано " + target + " пузырьков!";
                }
                break;
        }

        if (completed) {
            missionCompleted = true;
            lastCompletedMessage = newMessage;
            MissionManager.completeCurrentMission();
            game.audioManager.playVictory();   // Победный звук
            isQuestCompletePaused = true;      // Стоп-кадр, показываем окно
        }
    }

    /**
     * Показывает интерфейс текущего квеста:
     * - полоска прогресса внизу экрана
     * - текущее значение / цель
     * - всплывающая подсказка (3 секунды после старта квеста)
     *
     * Рисуется поверх игры, но под окнами паузы/победы.
     */
    private void showMissionUI() {
        if (MissionManager.isGameComplete()) return;

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        int missionType = MissionManager.getCurrentMissionType();
        int target = MissionManager.getCurrentMissionTarget();
        String description = MissionManager.getCurrentMissionDescription();

        // Получаем текущий прогресс в зависимости от типа квеста
        int currentValue = 0;
        switch (missionType) {
            case GameSettings.MISSION_TYPE_DISTANCE:
                currentValue = (int)(distance - missionStartDistance);
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_NORMAL:
                currentValue = crystalsNormalCollected;
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_GOLD:
                currentValue = crystalsGoldCollected;
                break;
            case GameSettings.MISSION_TYPE_BUBBLES:
                currentValue = bubblesCollected;
                break;
        }

        float progress = Math.min(1f, (float)currentValue / target);
        float barWidth = GameSettings.size(450);
        float barHeight = GameSettings.size(22);
        float barX = centerX - barWidth / 2;
        float barY = GameSettings.y(45);   // Почти у нижнего края

        // Рисуем полоску: тёмный фон + цветная заливка
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.2f, 0.85f));
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.setColor(bioCyan);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
        shapeRenderer.end();

        // Текст прогресса (цифры)
        game.batch.begin();
        game.textView.setScale(GameSettings.size(0.8f));
        String progressText = currentValue + " / " + target;
        game.textView.draw(game.batch, progressText,
            centerX - GameSettings.size(55),
            barY - GameSettings.size(5),
            primaryText);

        // Всплывающая подсказка с описанием квеста (только первые 3 секунды)
        if (questTextTimer > 0) {
            float alpha = Math.min(1f, questTextTimer);   // Плавное исчезание
            game.textView.setScale(GameSettings.size(0.7f));
            game.textView.draw(game.batch, "► " + description,
                centerX - GameSettings.size(200),
                barY + GameSettings.size(60),
                new Color(gold.r, gold.g, gold.b, alpha));
        }
        game.batch.end();
    }

    @Override
    public void show() {
        initGame();   // Перезапускаем игру каждый раз при показе экрана
    }

    @Override
    public void render(float delta) {
        // Настраиваем камеру и проекции для batch и shapeRenderer
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();   // Сначала обрабатываем касания (пауза, кнопки)

        // ========== ИГРОВАЯ ЛОГИКА (только если не на паузе и не в окне квеста) ==========
        if (!isPaused && !MissionManager.isGameComplete() && !isQuestCompletePaused) {
            // Основные обновления
            distance += GameSettings.MOVE_SPEED * delta;
            oxygen -= GameSettings.OXYGEN_DEPLETION_RATE * delta;
            checkMissionProgress();

            // Кислород кончился — игра окончена
            if (oxygen <= 0) {
                game.setScreen(game.menuScreen);
                return;
            }

            // Фон: двигаем две копии
            bgX1 -= GameSettings.MOVE_SPEED * delta;
            bgX2 -= GameSettings.MOVE_SPEED * delta;
            if (bgX1 <= -GameSettings.SCREEN_WIDTH) bgX1 = bgX2 + GameSettings.SCREEN_WIDTH;
            if (bgX2 <= -GameSettings.SCREEN_WIDTH) bgX2 = bgX1 + GameSettings.SCREEN_WIDTH;

            // Дайвер
            diver.update(delta);

            // Спавн новых объектов (каждые 0.8 секунды)
            spawnTimer += delta;
            if (spawnTimer > 0.8f) {
                if (MathUtils.random() < 0.7f) crystals.add(new CrystalObject());
                if (MathUtils.random() < 0.5f) jellyfish.add(new JellyfishObject());
                if (MathUtils.random() < 0.4f) bubbles.add(new BubbleObject());
                spawnTimer = 0;
            }

            // Обновляем позиции всех объектов
            for (CrystalObject c : crystals) c.update(delta);
            for (JellyfishObject j : jellyfish) j.update(delta);
            for (BubbleObject b : bubbles) b.update(delta);

            // Проверяем столкновения (удаление объектов происходит внутри)
            ContactManager.checkCollisions(diver, this);

            // Удаляем объекты, улетевшие за левый край экрана
            for (int i = crystals.size - 1; i >= 0; i--)
                if (crystals.get(i).isOutOfFrame()) crystals.removeIndex(i);
            for (int i = jellyfish.size - 1; i >= 0; i--)
                if (jellyfish.get(i).isOutOfFrame()) jellyfish.removeIndex(i);
            for (int i = bubbles.size - 1; i >= 0; i--)
                if (bubbles.get(i).isOutOfFrame()) bubbles.removeIndex(i);

            // Уменьшаем таймер подсказки
            if (questTextTimer > 0) {
                questTextTimer -= delta;
            }
        }

        // Если выполнены все квесты — переходим на экран победы
        if (MissionManager.isGameComplete()) {
            game.setScreen(new VictoryScreen(game, score, (int)distance,
                crystalsNormalCollected, crystalsGoldCollected, bubblesCollected));
            return;
        }

        // ========== ОТРИСОВКА ГРАФИКИ ==========
        game.batch.begin();

        // Фон
        if (GameResources.backgroundGame != null) {
            game.batch.draw(GameResources.backgroundGame, bgX1, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.draw(GameResources.backgroundGame, bgX2, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        // Кристаллы
        for (CrystalObject c : crystals) {
            game.batch.draw(c.isGold ? GameResources.crystalGold : GameResources.crystal,
                c.x, c.y, c.width, c.height);
        }

        // Медузы
        for (JellyfishObject j : jellyfish) {
            game.batch.draw(j.getTexture(), j.x, j.y, j.width, j.height);
        }

        // Пузырьки
        for (BubbleObject b : bubbles) {
            game.batch.draw(GameResources.bubble, b.x, b.y, b.width, b.height);
        }

        // Дайвер (поверх всего, чтобы был хорошо виден)
        diver.draw(game.batch);

        game.batch.end();

        // ========== ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ (рисуем поверх игры) ==========
        float topY = GameSettings.y(GameSettings.DESIGN_HEIGHT - 25);
        float currentX = GameSettings.x(20);

        // Шкала кислорода (рисуем ShapeRenderer'ом, чтобы не грузить текстурами)
        float barWidth = GameSettings.size(180);
        float barHeight = GameSettings.size(12);
        float barX = currentX;
        float barY = topY - barHeight;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        if (oxygen > 50) {
            shapeRenderer.setColor(oxygenOk);
        } else if (oxygen > 25) {
            shapeRenderer.setColor(gold);
        } else {
            shapeRenderer.setColor(oxygenCritical);
        }
        float fillWidth = (oxygen / GameSettings.START_OXYGEN) * barWidth;
        shapeRenderer.rect(barX, barY, fillWidth, barHeight);
        shapeRenderer.end();

        currentX += barWidth + GameSettings.size(60);

        // Текст "Путь: X метров"
        String distanceText = "Путь: " + (int) distance + "м";
        glyphLayout.setText(uiFont, distanceText);
        uiFont.setColor(bioCyan);
        uiFont.getData().setScale(GameSettings.size(1f));

        game.batch.begin();
        uiFont.draw(game.batch, distanceText, currentX, topY);
        game.batch.end();

        currentX += glyphLayout.width + GameSettings.size(60);

        // Текст "Счёт: X"
        String scoreText = "Счёт: " + score;
        glyphLayout.setText(uiFont, scoreText);
        uiFont.setColor(primaryText);

        game.batch.begin();
        uiFont.draw(game.batch, scoreText, currentX, topY);
        game.batch.end();

        // КНОПКА ПАУЗЫ (иконка в правом верхнем углу)
        game.batch.begin();
        if (GameResources.pauseIconMarine != null) {
            game.batch.draw(GameResources.pauseIconMarine,
                pauseBtnBounds.x, pauseBtnBounds.y,
                pauseBtnBounds.width, pauseBtnBounds.height);
        } else {
            // fallback, если текстуры нет
            uiFont.setColor(gold);
            uiFont.getData().setScale(GameSettings.size(1.2f));
            uiFont.draw(game.batch, "⏸",
                pauseBtnBounds.x + pauseBtnBounds.width/2 - GameSettings.size(10),
                pauseBtnBounds.y + pauseBtnBounds.height/2 + GameSettings.size(10));
        }
        game.batch.end();

        // Полоска прогресса квеста
        showMissionUI();

        // ============================================================
        // ОКНО ВЫПОЛНЕНИЯ КВЕСТА (поздравление с паузой)
        // ============================================================
        if (isQuestCompletePaused) {
            float cx = GameSettings.SCREEN_WIDTH / 2;
            float cy = GameSettings.SCREEN_HEIGHT / 2;

            float panelWidth = GameSettings.size(600);
            float panelHeight = GameSettings.size(320);
            float panelX = cx - panelWidth / 2;
            float panelY = cy - panelHeight / 2;

            // КНОПКА "ПРОДОЛЖИТЬ" (левая)
            float continueBtnW = GameSettings.size(230);
            float continueBtnH = GameSettings.size(80);

            // КНОПКА "В МЕНЮ" (правая)
            float menuBtnW = GameSettings.size(260);
            float menuBtnH = GameSettings.size(106);

            float btnSpacing = GameSettings.size(50);
            float btnY = cy - GameSettings.size(100);

            // Правая кнопка может иметь отдельный сдвиг (на случай кривых рук дизайнера)
            float menuBtnOffsetX = GameSettings.x(0);
            float menuBtnOffsetY = GameSettings.y(-10);

            Rectangle continueBtn = new Rectangle(
                cx - continueBtnW - btnSpacing/2,
                btnY,
                continueBtnW, continueBtnH
            );

            Rectangle menuBtn = new Rectangle(
                cx + btnSpacing/2 + menuBtnOffsetX,
                btnY + menuBtnOffsetY,
                menuBtnW, menuBtnH
            );

            float titleY = panelY + panelHeight - GameSettings.size(60);

            // Чёрное затемнение фона
            game.batch.begin();
            game.batch.setColor(0, 0, 0, 0.85f);
            game.batch.draw(GameResources.blackout, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.setColor(1, 1, 1, 1);
            game.batch.end();

            // Рамка подложки
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(oxygenOk);
            shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
            shapeRenderer.end();

            // Текст поздравления
            game.batch.begin();

            game.textView.setScale(GameSettings.size(1.0f));
            game.textView.draw(game.batch, "✓ ЗАДАНИЕ ВЫПОЛНЕНО!",
                cx - GameSettings.size(200), titleY, oxygenOk);

            // Кнопка "ПРОДОЛЖИТЬ" (левая)
            if (GameResources.buttonContinueMarine != null) {
                game.batch.draw(GameResources.buttonContinueMarine,
                    continueBtn.x, continueBtn.y, continueBtnW, continueBtnH);
            } else {
                game.textView.setScale(GameSettings.size(1.0f));
                game.textView.draw(game.batch, "ПРОДОЛЖИТЬ",
                    continueBtn.x + continueBtnW/2 - GameSettings.size(60),
                    continueBtn.y + continueBtnH/2 + GameSettings.size(15), bioCyan);
            }

            // Кнопка "В МЕНЮ" (правая)
            if (GameResources.buttonExitMarine != null) {
                game.batch.draw(GameResources.buttonExitMarine,
                    menuBtn.x, menuBtn.y, menuBtnW, menuBtnH);
            } else {
                game.textView.draw(game.batch, "В МЕНЮ",
                    menuBtn.x + menuBtnW/2 - GameSettings.size(50),
                    menuBtn.y + menuBtnH/2 + GameSettings.size(15), gold);
            }
            game.batch.end();
        }

        // ============================================================
        // ОКНО ПАУЗЫ
        // ============================================================
        if (isPaused) {
            float cx = GameSettings.SCREEN_WIDTH / 2;
            float cy = GameSettings.SCREEN_HEIGHT / 2;

            float btnW = GameSettings.size(340);
            float btnH = GameSettings.size(120);
            float btnSpacingY = GameSettings.size(120);

            continueBtnBounds = new Rectangle(cx - btnW/2, cy + btnSpacingY/2, btnW, btnH);
            menuBtnBounds = new Rectangle(cx - btnW/2, cy - btnSpacingY/2 - btnH, btnW, btnH);

            // Затемнение фона
            game.batch.begin();
            game.batch.setColor(0, 0, 0, 0.85f);
            game.batch.draw(GameResources.blackout, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.setColor(1, 1, 1, 1);
            game.batch.end();

            // Кнопки
            game.batch.begin();
            if (GameResources.buttonContinueMarine != null) {
                game.batch.draw(GameResources.buttonContinueMarine,
                    continueBtnBounds.x, continueBtnBounds.y, btnW, btnH);
            } else {
                game.textView.setScale(GameSettings.size(1.5f));
                game.textView.draw(game.batch, "ПРОДОЛЖИТЬ",
                    continueBtnBounds.x + GameSettings.size(60),
                    continueBtnBounds.y + GameSettings.size(70), bioCyan);
            }
            if (GameResources.buttonExitMarine != null) {
                game.batch.draw(GameResources.buttonExitMarine,
                    menuBtnBounds.x, menuBtnBounds.y, btnW, btnH);
            } else {
                game.textView.draw(game.batch, "ВЫЙТИ В МЕНЮ",
                    menuBtnBounds.x + GameSettings.size(70),
                    menuBtnBounds.y + GameSettings.size(70), gold);
            }
            game.batch.end();
        }
    }

    /**
     * Обрабатывает касания экрана.
     *
     * Приоритет (от высшего к низшему):
     * 1. Окно выполнения квеста (кнопки "Продолжить" и "В меню")
     * 2. Кнопка паузы
     * 3. Окно паузы (кнопки)
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touch);

            // Если открыто окно "Задание выполнено" — обрабатываем только его кнопки
            if (isQuestCompletePaused) {
                float cx = GameSettings.SCREEN_WIDTH / 2;
                float cy = GameSettings.SCREEN_HEIGHT / 2;
                float btnW = GameSettings.size(230);
                float btnH = GameSettings.size(80);
                float btnSpacing = GameSettings.size(50);
                float btnY = cy - GameSettings.size(100);

                Rectangle continueBtn = new Rectangle(
                    cx - btnW - btnSpacing/2,
                    btnY,
                    btnW, btnH
                );
                Rectangle menuBtn = new Rectangle(
                    cx + btnSpacing/2,
                    btnY,
                    btnW, btnH
                );

                if (continueBtn.contains(touch.x, touch.y)) {
                    isQuestCompletePaused = false;
                    missionCompleted = false;
                    missionStartDistance = distance;   // Сбрасываем дистанцию для следующего квеста
                    if (!MissionManager.isGameComplete()) {
                        questTextTimer = 3.0f;         // Показываем следующий квест
                    }
                    return;
                }
                if (menuBtn.contains(touch.x, touch.y)) {
                    game.setScreen(game.menuScreen);
                    return;
                }
                return;
            }

            // Кнопка паузы (иконка в углу)
            if (pauseBtnBounds.contains(touch.x, touch.y)) {
                isPaused = true;
                return;
            }

            // Если на паузе — обрабатываем кнопки паузы
            if (isPaused) {
                if (continueBtnBounds.contains(touch.x, touch.y)) {
                    isPaused = false;
                    return;
                }
                if (menuBtnBounds.contains(touch.x, touch.y)) {
                    game.setScreen(game.menuScreen);
                    return;
                }
                return;
            }
        }
    }

    // ========== ПУБЛИЧНЫЕ МЕТОДЫ ДЛЯ ContactManager ==========
    // Все они проверяют флаги isPaused и isQuestCompletePaused,
    // чтобы игрок не мог получать очки или терять кислород во время паузы.

    public void addScore(int points) {
        if (!isPaused && !isQuestCompletePaused) {
            score += points;
            GameSession.addScore(points);
        }
    }

    public void addCrystalNormal() {
        if (!isPaused && !isQuestCompletePaused) {
            crystalsNormalCollected++;
            checkMissionProgress();
        }
    }

    public void addCrystalGold() {
        if (!isPaused && !isQuestCompletePaused) {
            crystalsGoldCollected++;
            checkMissionProgress();
        }
    }

    public void addBubble() {
        if (!isPaused && !isQuestCompletePaused) {
            bubblesCollected++;
            checkMissionProgress();
        }
    }

    public void replenishOxygen(float amount) {
        if (!isPaused && !isQuestCompletePaused) {
            oxygen = Math.min(GameSettings.START_OXYGEN, oxygen + amount);
        }
    }

    public void damageOxygen(float amount) {
        if (!isPaused && !isQuestCompletePaused) {
            oxygen -= amount;
        }
    }

    // ========== ГЕТТЕРЫ ДЛЯ ContactManager ==========
    public float getOxygen() { return oxygen; }
    public DiverObject getDiver() { return diver; }
    public Array<CrystalObject> getCrystals() { return crystals; }
    public Array<JellyfishObject> getJellyfish() { return jellyfish; }
    public Array<BubbleObject> getBubbles() { return bubbles; }
    public Main getGame() { return game; }

    // ========== СТАНДАРТНЫЕ МЕТОДЫ SCREEN (большинство пустые) ==========
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
