package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import com.deepdiver.game.managers.MemoryManager;
import com.deepdiver.game.managers.MissionManager;
import com.deepdiver.game.objects.BubbleObject;
import com.deepdiver.game.objects.CrystalObject;
import com.deepdiver.game.objects.DiverObject;
import com.deepdiver.game.objects.JellyfishObject;
import com.deepdiver.game.objects.PowerUpObject;
import com.deepdiver.game.objects.SharkObject;
import com.deepdiver.game.objects.OctopusObject;
import com.deepdiver.game.objects.RayObject;

/**
 * Главный игровой экран.
 * Управляет всеми аспектами игрового процесса: дайвер, враги, кристаллы, бонусы,
 * система кислорода, прогресс заданий, пауза и переход на экраны окончания игры.
 */
public class GameScreen implements Screen {
    private final Main game;
    private DiverObject diver;
    private final OxygenBar oxygenBar;

    // Коллекции игровых объектов
    private final Array<CrystalObject> crystals = new Array<>();
    private final Array<JellyfishObject> jellyfish = new Array<>();
    private final Array<BubbleObject> bubbles = new Array<>();
    private final Array<SharkObject> sharks = new Array<>();
    private final Array<OctopusObject> octopuses = new Array<>();
    private final Array<RayObject> rays = new Array<>();
    private final Array<PowerUpObject> powerUps = new Array<>();

    // Позиции фона для бесконечного скроллинга
    private float bgX1 = 0;
    private float bgX2 = 0;
    private Texture currentBackground;

    // Игровые показатели
    private float oxygen = GameSettings.START_OXYGEN;
    private float distance = 0f;
    private int score = 0;

    // Таймеры спавна объектов
    private float spawnTimer = 0f;
    private float powerUpSpawnTimer = 0f;

    // Состояния игры
    private boolean isPaused = false;
    private boolean isQuestCompletePaused = false;
    private boolean isGameEnding = false;

    // Области кнопок UI
    private Rectangle pauseBtnBounds;
    private Rectangle continueBtnBounds;
    private Rectangle menuBtnBounds;

    // Активные бонусы
    private boolean hasShield = false;
    private float shieldTimer = 0f;
    private boolean hasSpeedBoost = false;
    private float speedBoostTimer = 0f;
    private float currentSpeedMultiplier = 1f;

    // Счётчики для заданий
    private int crystalsNormalCollected = 0;
    private int crystalsGoldCollected = 0;
    private int bubblesCollected = 0;
    private float missionStartDistance = 0f;
    private boolean missionCompleted = false;
    private float questTextTimer = 0f;

    // Графические компоненты
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;
    private GlyphLayout glyphLayout;

    // Цветовые константы для UI
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    public GameScreen(Main game) {
        this.game = game;
        this.oxygenBar = new OxygenBar();
        this.shapeRenderer = new ShapeRenderer();
        this.glyphLayout = new GlyphLayout();

        // Настройка шрифта для UI
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.round(GameSettings.size(24));
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюЯABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|/?-+=()*&.;:,{}\"´`'<>«» ";
        this.uiFont = generator.generateFont(parameter);
        generator.dispose();

        initGame();
    }

    /**
     * Инициализирует или сбрасывает состояние игры.
     * Вызывается при создании экрана и при старте новой игры.
     */
    private void initGame() {
        diver = new DiverObject();
        crystals.clear();
        jellyfish.clear();
        bubbles.clear();
        sharks.clear();
        octopuses.clear();
        rays.clear();
        powerUps.clear();

        // Получаем бонусы от прокачки текущего скина
        int skinLevel = MemoryManager.getSkinLevel(MemoryManager.getEquippedSkin());
        float oxygenBonus = MemoryManager.getOxygenBonus(skinLevel);
        oxygen = GameSettings.START_OXYGEN + oxygenBonus;

        distance = 0f;
        score = 0;
        spawnTimer = 0f;
        powerUpSpawnTimer = 0f;
        isPaused = false;
        isQuestCompletePaused = false;
        isGameEnding = false;
        hasShield = false;
        hasSpeedBoost = false;
        currentSpeedMultiplier = 1f;
        shieldTimer = 0f;
        speedBoostTimer = 0f;

        crystalsNormalCollected = 0;
        crystalsGoldCollected = 0;
        bubblesCollected = 0;
        missionStartDistance = 0f;
        missionCompleted = false;

        updateBackground();
        bgX1 = 0;
        bgX2 = GameSettings.SCREEN_WIDTH;

        // Показываем описание текущего задания, если игра не пройдена
        if (!MissionManager.isGameComplete()) {
            questTextTimer = 3.0f;
        }

        // Настройка области кнопки паузы
        float pauseSize = GameSettings.size(80);
        float offsetX = GameSettings.size(3);
        float offsetY = GameSettings.size(3);
        pauseBtnBounds = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - pauseSize - offsetX),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - pauseSize - offsetY),
            pauseSize, pauseSize
        );

        // Настройка областей кнопок паузы
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
     * Обновляет фон в зависимости от текущей локации.
     * Локация определяется по количеству выполненных заданий.
     */
    private void updateBackground() {
        int currentLocation = GameSettings.getLocationByMission(MissionManager.getCompletedMissions());
        currentBackground = GameResources.getBackgroundForLocation(currentLocation);
    }

    /**
     * Сохраняет текущую дистанцию в таблицу рекордов.
     * Вызывается только один раз при завершении игры.
     */
    private void saveCurrentDistanceToRecords() {
        if ((int)distance > 0 && !isGameEnding) {
            MemoryManager.addRecord((int)distance);
            isGameEnding = true;
        }
    }

    /**
     * Проверяет прогресс выполнения текущего задания.
     * При достижении цели завершает задание и показывает окно успеха.
     */
    private void checkMissionProgress() {
        if (missionCompleted || MissionManager.isGameComplete()) return;

        int missionType = MissionManager.getCurrentMissionType();
        int target = MissionManager.getCurrentMissionTarget();
        boolean completed = false;

        switch (missionType) {
            case GameSettings.MISSION_TYPE_DISTANCE:
                if (distance - missionStartDistance >= target) completed = true;
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_NORMAL:
                if (crystalsNormalCollected >= target) completed = true;
                break;
            case GameSettings.MISSION_TYPE_CRYSTALS_GOLD:
                if (crystalsGoldCollected >= target) completed = true;
                break;
            case GameSettings.MISSION_TYPE_BUBBLES:
                if (bubblesCollected >= target) completed = true;
                break;
        }

        if (completed) {
            missionCompleted = true;
            MissionManager.completeCurrentMission();
            game.audioManager.playVictory();
            isQuestCompletePaused = true;
            updateBackground();
        }
    }

    /**
     * Активирует бонус в зависимости от его типа.
     * @param type тип подобранного бонуса
     */
    public void activatePowerUp(PowerUpObject.PowerUpType type) {
        switch (type) {
            case STAR:
                float maxOxygen = GameSettings.START_OXYGEN +
                    MemoryManager.getOxygenBonus(MemoryManager.getSkinLevel(MemoryManager.getEquippedSkin()));
                oxygen = Math.min(maxOxygen, oxygen + 50);
                diver.activateStarEffect(1.5f);
                game.audioManager.playCollect();
                break;
            case SHIELD:
                hasShield = true;
                shieldTimer = GameSettings.SHIELD_DURATION;
                diver.activateShieldEffect(GameSettings.SHIELD_DURATION);
                game.audioManager.playCollect();
                break;
            case SPEED:
                hasSpeedBoost = true;
                speedBoostTimer = GameSettings.SPEED_BOOST_DURATION;
                currentSpeedMultiplier = GameSettings.SPEED_BOOST_MULTIPLIER;
                game.audioManager.playCollect();
                break;
        }
    }

    /**
     * Обновляет таймеры активных бонусов.
     * @param delta время между кадрами
     */
    private void updatePowerUpTimers(float delta) {
        if (hasShield) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) {
                hasShield = false;
            }
        }
        if (hasSpeedBoost) {
            speedBoostTimer -= delta;
            if (speedBoostTimer <= 0) {
                hasSpeedBoost = false;
                currentSpeedMultiplier = 1f;
            }
        }
    }

    /**
     * Отрисовывает UI текущего задания: полосу прогресса и описание.
     */
    private void showMissionUI() {
        if (MissionManager.isGameComplete()) return;

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        int missionType = MissionManager.getCurrentMissionType();
        int target = MissionManager.getCurrentMissionTarget();
        String description = MissionManager.getCurrentMissionDescription();

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
        float barY = GameSettings.y(45);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.2f, 0.85f));
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.setColor(bioCyan);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
        shapeRenderer.end();

        game.batch.begin();
        game.textView.setScale(GameSettings.size(0.8f));
        game.textView.draw(game.batch, currentValue + " / " + target,
            centerX - GameSettings.size(55), barY - GameSettings.size(5), primaryText);

        if (questTextTimer > 0) {
            float alpha = Math.min(1f, questTextTimer);
            game.textView.setScale(GameSettings.size(0.7f));
            game.textView.draw(game.batch, "► " + description,
                centerX - GameSettings.size(200), barY + GameSettings.size(60),
                new Color(gold.r, gold.g, gold.b, alpha));
        }
        game.batch.end();
    }

    /**
     * Проверяет, пересекается ли область с каким-либо врагом или кристаллом.
     * Используется для предотвращения спавна объектов друг в друге.
     */
    private boolean collidesWithAnyEnemy(float newX, float newY, float width, float height, String enemyType) {
        Rectangle newBounds = new Rectangle(newX, newY, width, height);

        if (!enemyType.equals("jellyfish")) {
            for (JellyfishObject j : jellyfish) {
                if (newBounds.overlaps(j.bounds)) return true;
            }
        }
        if (!enemyType.equals("shark")) {
            for (SharkObject s : sharks) {
                if (newBounds.overlaps(s.bounds)) return true;
            }
        }
        if (!enemyType.equals("octopus")) {
            for (OctopusObject o : octopuses) {
                if (newBounds.overlaps(o.bounds)) return true;
            }
        }
        if (!enemyType.equals("ray")) {
            for (RayObject r : rays) {
                if (newBounds.overlaps(r.bounds)) return true;
            }
        }
        for (CrystalObject c : crystals) {
            if (newBounds.overlaps(c.bounds)) return true;
        }
        return false;
    }

    // Методы спавна объектов с проверкой коллизий
    private void trySpawnJellyfish() {
        for (int attempt = 0; attempt < 5; attempt++) {
            JellyfishObject test = new JellyfishObject();
            if (!collidesWithAnyEnemy(test.x, test.y, test.width, test.height, "jellyfish")) {
                jellyfish.add(test);
                return;
            }
        }
        jellyfish.add(new JellyfishObject());
    }

    private void trySpawnShark() {
        for (int attempt = 0; attempt < 5; attempt++) {
            float newY = MathUtils.random(80f, GameSettings.SCREEN_HEIGHT - 130f);
            SharkObject test = new SharkObject(GameSettings.SCREEN_WIDTH + 50, newY);
            if (!collidesWithAnyEnemy(test.x, test.y, test.width, test.height, "shark")) {
                sharks.add(test);
                return;
            }
        }
        sharks.add(new SharkObject());
    }

    private void trySpawnOctopus() {
        for (int attempt = 0; attempt < 5; attempt++) {
            OctopusObject test = new OctopusObject();
            if (!collidesWithAnyEnemy(test.x, test.y, test.width, test.height, "octopus")) {
                octopuses.add(test);
                return;
            }
        }
        octopuses.add(new OctopusObject());
    }

    private void trySpawnRay() {
        for (int attempt = 0; attempt < 5; attempt++) {
            RayObject test = new RayObject();
            if (!collidesWithAnyEnemy(test.x, test.y, test.width, test.height, "ray")) {
                rays.add(test);
                return;
            }
        }
        rays.add(new RayObject());
    }

    /**
     * Генерирует врагов в зависимости от текущей локации.
     * Вероятность появления каждого типа врага зависит от локации.
     */
    private void spawnEnemiesByLocation() {
        int currentLocation = GameSettings.getLocationByMission(MissionManager.getCompletedMissions());
        float rand = MathUtils.random();

        switch (currentLocation) {
            case GameSettings.LOCATION_SHALLOWS:
                if (rand < 0.45f) trySpawnJellyfish();
                break;
            case GameSettings.LOCATION_REEF:
                if (rand < 0.3f) trySpawnJellyfish();
                if (rand < 0.22f) trySpawnShark();
                break;
            case GameSettings.LOCATION_DEEP:
                if (rand < 0.2f) trySpawnJellyfish();
                if (rand < 0.18f) trySpawnShark();
                if (rand < 0.12f) trySpawnOctopus();
                break;
            case GameSettings.LOCATION_RUINS:
                if (rand < 0.15f) trySpawnJellyfish();
                if (rand < 0.12f) trySpawnShark();
                if (rand < 0.08f) trySpawnOctopus();
                if (rand < 0.06f) trySpawnRay();
                break;
        }
    }

    @Override
    public void show() {
        initGame();
    }

    @Override
    public void render(float delta) {
        // Настройка камеры и очистка экрана
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        shapeRenderer.setProjectionMatrix(game.camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // Обновление игровой логики (только если игра активна)
        if (!isPaused && !MissionManager.isGameComplete() && !isQuestCompletePaused && !isGameEnding) {
            float worldSpeed = GameSettings.MOVE_SPEED * currentSpeedMultiplier;

            // Движение фона
            bgX1 -= worldSpeed * delta;
            bgX2 -= worldSpeed * delta;
            if (bgX1 <= -GameSettings.SCREEN_WIDTH) bgX1 = bgX2 + GameSettings.SCREEN_WIDTH;
            if (bgX2 <= -GameSettings.SCREEN_WIDTH) bgX2 = bgX1 + GameSettings.SCREEN_WIDTH;

            // Обновление игровых показателей
            distance += worldSpeed * delta;
            oxygen -= GameSettings.OXYGEN_DEPLETION_RATE * delta;
            checkMissionProgress();
            updatePowerUpTimers(delta);

            // Проверка на смерть
            if (oxygen <= 0 && !isGameEnding) {
                isGameEnding = true;
                game.setScreen(new GameOverScreen(game, score, (int)distance));
                return;
            }

            diver.update(delta);

            // Спавн объектов
            spawnTimer += delta;
            if (spawnTimer > 0.7f) {
                if (MathUtils.random() < 0.6f) crystals.add(new CrystalObject());
                spawnEnemiesByLocation();
                if (MathUtils.random() < 0.4f) bubbles.add(new BubbleObject());
                spawnTimer = 0;
            }

            // Спавн бонусов (раз в 20 секунд с шансом 25%)
            powerUpSpawnTimer += delta;
            if (powerUpSpawnTimer > 20f && MathUtils.random() < 0.25f) {
                powerUps.add(new PowerUpObject(PowerUpObject.getRandomType()));
                powerUpSpawnTimer = 0;
            }

            // Обновление позиций всех объектов
            for (CrystalObject c : crystals) { c.x -= worldSpeed * delta; c.bounds.setPosition(c.x, c.y); }
            for (BubbleObject b : bubbles) { b.x -= (worldSpeed * 1.5f) * delta; b.bounds.setPosition(b.x, b.y); }
            for (JellyfishObject j : jellyfish) { j.x -= worldSpeed * delta; j.update(delta); }
            for (SharkObject s : sharks) { s.x -= worldSpeed * delta; s.update(delta); }
            for (OctopusObject o : octopuses) { o.x -= worldSpeed * o.getSpeedX() * delta; o.update(delta); }
            for (RayObject r : rays) { r.x -= worldSpeed * 0.9f * delta; r.update(delta); }
            for (PowerUpObject p : powerUps) { p.x -= worldSpeed * delta; p.update(delta); }

            // Обработка столкновений
            ContactManager.checkCollisions(diver, this);

            // Удаление объектов за пределами экрана
            for (int i = crystals.size - 1; i >= 0; i--) if (crystals.get(i).isOutOfFrame()) crystals.removeIndex(i);
            for (int i = jellyfish.size - 1; i >= 0; i--) if (jellyfish.get(i).isOutOfFrame()) jellyfish.removeIndex(i);
            for (int i = bubbles.size - 1; i >= 0; i--) if (bubbles.get(i).isOutOfFrame()) bubbles.removeIndex(i);
            for (int i = sharks.size - 1; i >= 0; i--) if (sharks.get(i).isOutOfFrame()) sharks.removeIndex(i);
            for (int i = octopuses.size - 1; i >= 0; i--) if (octopuses.get(i).isOutOfFrame()) octopuses.removeIndex(i);
            for (int i = rays.size - 1; i >= 0; i--) if (rays.get(i).isOutOfFrame()) rays.removeIndex(i);
            for (int i = powerUps.size - 1; i >= 0; i--) if (powerUps.get(i).isOutOfFrame()) powerUps.removeIndex(i);

            if (questTextTimer > 0) questTextTimer -= delta;
        }

        // Проверка полного прохождения игры
        if (MissionManager.isGameComplete() && !isGameEnding) {
            isGameEnding = true;
            game.setScreen(new VictoryScreen(game, score, (int)distance,
                crystalsNormalCollected, crystalsGoldCollected, bubblesCollected));
            return;
        }

        // ========== ОТРИСОВКА ==========
        game.batch.begin();
        // Фон
        if (currentBackground != null) {
            game.batch.draw(currentBackground, bgX1, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.draw(currentBackground, bgX2, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        // Игровые объекты
        for (CrystalObject c : crystals) {
            game.batch.draw(c.isGold ? GameResources.crystalGold : GameResources.crystal, c.x, c.y, c.width, c.height);
        }
        for (JellyfishObject j : jellyfish) game.batch.draw(j.getTexture(), j.x, j.y, j.width, j.height);
        for (SharkObject s : sharks) game.batch.draw(s.getTexture(), s.x, s.y, s.width, s.height);
        for (OctopusObject o : octopuses) game.batch.draw(o.getTexture(), o.x, o.y, o.width, o.height);
        for (RayObject r : rays) game.batch.draw(r.getTexture(), r.x, r.y, r.width, r.height);
        for (BubbleObject b : bubbles) game.batch.draw(GameResources.bubble, b.x, b.y, b.width, b.height);
        for (PowerUpObject p : powerUps) game.batch.draw(p.getTexture(), p.x, p.y, p.width, p.height);
        diver.draw(game.batch);
        game.batch.end();

        // Визуальные эффекты дайвера (щит, звёзды)
        diver.drawEffects(shapeRenderer, game.batch);

        // Отрисовка UI
        float topY = GameSettings.y(GameSettings.DESIGN_HEIGHT - 25);
        float currentX = GameSettings.x(20);

        // Шкала кислорода
        float barWidth = GameSettings.size(180);
        float barHeight = GameSettings.size(12);
        float barX = currentX;
        float barY = topY - barHeight;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        if (oxygen > 50) shapeRenderer.setColor(oxygenOk);
        else if (oxygen > 25) shapeRenderer.setColor(gold);
        else shapeRenderer.setColor(oxygenCritical);
        float fillWidth = (oxygen / GameSettings.START_OXYGEN) * barWidth;
        shapeRenderer.rect(barX, barY, fillWidth, barHeight);
        shapeRenderer.end();

        currentX += barWidth + GameSettings.size(60);

        // Дистанция
        String distanceText = "Путь: " + (int) distance + "м";
        glyphLayout.setText(uiFont, distanceText);
        uiFont.setColor(bioCyan);
        uiFont.getData().setScale(GameSettings.size(1f));
        game.batch.begin();
        uiFont.draw(game.batch, distanceText, currentX, topY);
        game.batch.end();
        currentX += glyphLayout.width + GameSettings.size(60);

        // Счёт (с учётом множителя от прокачки скина)
        float scoreMultiplier = MemoryManager.getScoreMultiplier(MemoryManager.getSkinLevel(MemoryManager.getEquippedSkin()));
        String scoreText = "Счёт: " + (int)(score * scoreMultiplier);
        glyphLayout.setText(uiFont, scoreText);
        uiFont.setColor(primaryText);
        game.batch.begin();
        uiFont.draw(game.batch, scoreText, currentX, topY);
        game.batch.end();

        // Кнопка паузы
        game.batch.begin();
        if (GameResources.pauseIconMarine != null) {
            game.batch.draw(GameResources.pauseIconMarine, pauseBtnBounds.x, pauseBtnBounds.y,
                pauseBtnBounds.width, pauseBtnBounds.height);
        }
        game.batch.end();

        // Иконки активных бонусов
        game.batch.begin();
        float bonusX = GameSettings.x(20);
        if (hasShield) {
            uiFont.setColor(oxygenOk);
            uiFont.draw(game.batch, "🛡", bonusX, topY - 40);
            bonusX += 50;
        }
        if (hasSpeedBoost) {
            uiFont.setColor(gold);
            uiFont.draw(game.batch, "⚡", bonusX, topY - 40);
        }
        game.batch.end();

        showMissionUI();
        drawPauseAndQuestWindows();
    }

    /**
     * Отрисовывает окна паузы и завершения задания.
     */
    private void drawPauseAndQuestWindows() {
        if (isQuestCompletePaused) {
            float cx = GameSettings.SCREEN_WIDTH / 2;
            float cy = GameSettings.SCREEN_HEIGHT / 2;
            float panelWidth = GameSettings.size(600);
            float panelHeight = GameSettings.size(320);
            float panelX = cx - panelWidth / 2;
            float panelY = cy - panelHeight / 2;

            float continueBtnW = GameSettings.size(230);
            float continueBtnH = GameSettings.size(80);
            float menuBtnW = GameSettings.size(260);
            float menuBtnH = GameSettings.size(106);
            float btnSpacing = GameSettings.size(50);
            float btnY = cy - GameSettings.size(100);

            Rectangle continueBtn = new Rectangle(cx - continueBtnW - btnSpacing/2, btnY, continueBtnW, continueBtnH);
            Rectangle menuBtn = new Rectangle(cx + btnSpacing/2, btnY, menuBtnW, menuBtnH);

            // Полупрозрачный фон
            game.batch.begin();
            game.batch.setColor(0, 0, 0, 0.85f);
            game.batch.draw(GameResources.blackout, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.setColor(1, 1, 1, 1);
            game.batch.end();

            // Рамка окна
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(oxygenOk);
            shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
            shapeRenderer.end();

            game.batch.begin();
            game.textView.setScale(GameSettings.size(1.0f));
            game.textView.draw(game.batch, "✓ ЗАДАНИЕ ВЫПОЛНЕНО!",
                cx - GameSettings.size(200), panelY + panelHeight - GameSettings.size(60), oxygenOk);
            if (GameResources.buttonContinueMarine != null) {
                game.batch.draw(GameResources.buttonContinueMarine, continueBtn.x, continueBtn.y, continueBtnW, continueBtnH);
            }
            if (GameResources.buttonExitMarine != null) {
                game.batch.draw(GameResources.buttonExitMarine, menuBtn.x, menuBtn.y, menuBtnW, menuBtnH);
            }
            game.batch.end();
        }

        if (isPaused) {
            float cx = GameSettings.SCREEN_WIDTH / 2;
            float cy = GameSettings.SCREEN_HEIGHT / 2;
            float btnW = GameSettings.size(340);
            float btnH = GameSettings.size(120);
            float btnSpacingY = GameSettings.size(120);

            continueBtnBounds = new Rectangle(cx - btnW/2, cy + btnSpacingY/2, btnW, btnH);
            menuBtnBounds = new Rectangle(cx - btnW/2, cy - btnSpacingY/2 - btnH, btnW, btnH);

            game.batch.begin();
            game.batch.setColor(0, 0, 0, 0.85f);
            game.batch.draw(GameResources.blackout, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            game.batch.setColor(1, 1, 1, 1);
            if (GameResources.buttonContinueMarine != null) {
                game.batch.draw(GameResources.buttonContinueMarine, continueBtnBounds.x, continueBtnBounds.y, btnW, btnH);
            }
            if (GameResources.buttonExitMarine != null) {
                game.batch.draw(GameResources.buttonExitMarine, menuBtnBounds.x, menuBtnBounds.y, btnW, btnH);
            }
            game.batch.end();
        }
    }

    /**
     * Обрабатывает ввод пользователя (касания экрана).
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touch);

            // Обработка окна завершения задания
            if (isQuestCompletePaused) {
                float cx = GameSettings.SCREEN_WIDTH / 2;
                float cy = GameSettings.SCREEN_HEIGHT / 2;
                float btnW = GameSettings.size(230);
                float btnH = GameSettings.size(80);
                float btnSpacing = GameSettings.size(50);
                float btnY = cy - GameSettings.size(100);

                Rectangle continueBtn = new Rectangle(cx - btnW - btnSpacing/2, btnY, btnW, btnH);
                Rectangle menuBtn = new Rectangle(cx + btnSpacing/2, btnY, btnW, btnH);

                if (continueBtn.contains(touch.x, touch.y)) {
                    isQuestCompletePaused = false;
                    missionCompleted = false;
                    missionStartDistance = distance;
                    if (!MissionManager.isGameComplete()) questTextTimer = 3.0f;
                    return;
                }
                if (menuBtn.contains(touch.x, touch.y)) {
                    saveCurrentDistanceToRecords();
                    game.setScreen(game.menuScreen);
                    return;
                }
                return;
            }

            // Кнопка паузы
            if (pauseBtnBounds.contains(touch.x, touch.y)) {
                isPaused = true;
                return;
            }

            // Обработка окна паузы
            if (isPaused) {
                if (continueBtnBounds.contains(touch.x, touch.y)) {
                    isPaused = false;
                    return;
                }
                if (menuBtnBounds.contains(touch.x, touch.y)) {
                    saveCurrentDistanceToRecords();
                    game.setScreen(game.menuScreen);
                    return;
                }
            }
        }
    }

    // ========== ПУБЛИЧНЫЕ МЕТОДЫ ДЛЯ ContactManager ==========

    public void addScore(int points) {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            float multiplier = MemoryManager.getScoreMultiplier(MemoryManager.getSkinLevel(MemoryManager.getEquippedSkin()));
            score += (int)(points * multiplier);
            GameSession.addScore(points);
        }
    }

    public void addCrystalNormal() {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            crystalsNormalCollected++;
            checkMissionProgress();
        }
    }

    public void addCrystalGold() {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            crystalsGoldCollected++;
            checkMissionProgress();
        }
    }

    public void addBubble() {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            bubblesCollected++;
            checkMissionProgress();
        }
    }

    public void replenishOxygen(float amount) {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            float maxOxygen = GameSettings.START_OXYGEN +
                MemoryManager.getOxygenBonus(MemoryManager.getSkinLevel(MemoryManager.getEquippedSkin()));
            oxygen = Math.min(maxOxygen, oxygen + amount);
        }
    }

    public void damageOxygen(float amount) {
        if (!isPaused && !isQuestCompletePaused && !isGameEnding) {
            if (hasShield) return;
            oxygen -= amount;
        }
    }

    public boolean hasShield() { return hasShield; }
    public float getOxygen() { return oxygen; }
    public DiverObject getDiver() { return diver; }
    public Array<CrystalObject> getCrystals() { return crystals; }
    public Array<JellyfishObject> getJellyfish() { return jellyfish; }
    public Array<BubbleObject> getBubbles() { return bubbles; }
    public Array<SharkObject> getSharks() { return sharks; }
    public Array<OctopusObject> getOctopuses() { return octopuses; }
    public Array<RayObject> getRays() { return rays; }
    public Array<PowerUpObject> getPowerUps() { return powerUps; }
    public Main getGame() { return game; }

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
