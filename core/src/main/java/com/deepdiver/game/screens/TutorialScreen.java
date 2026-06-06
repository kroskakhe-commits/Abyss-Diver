package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;

/**
 * ОБУЧЕНИЕ (5 шагов).
 *
 * Простой пошаговый туториал, объясняющий основы игры.
 * Игрок тапает по экрану, чтобы перейти к следующему шагу.
 *
 * ОСОБЕННОСТЬ: каждый шаг имеет свои настройки расположения текста.
 * Это сделано, чтобы красиво размещать текст разной длины.
 */
public class TutorialScreen implements Screen {
    private final Main game;
    private int step = 1;   // Текущий шаг (1-5)

    // Цветовая схема
    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    /**
     * Внутренний класс для хранения настроек одного шага обучения.
     * Позволяет гибко настраивать расположение и размер текста для каждого шага.
     */
    private static class TutorialStep {
        String title;           // Текст заголовка
        String[] texts;         // Массив строк текста

        // Настройки заголовка
        float titleScale;
        float titleX;           // Смещение от центра (пиксели)
        float titleY;
        Color titleColor;

        // Настройки текста
        float textScale;
        float textX;            // Смещение от центра (пиксели)
        float textStartY;
        float textLineSpacing;
        Color textColor;

        TutorialStep(String title, String[] texts,
                     float titleScale, float titleX, float titleY, Color titleColor,
                     float textScale, float textX, float textStartY, float textLineSpacing, Color textColor) {
            this.title = title;
            this.texts = texts;
            this.titleScale = titleScale;
            this.titleX = titleX;
            this.titleY = titleY;
            this.titleColor = titleColor;
            this.textScale = textScale;
            this.textX = textX;
            this.textStartY = textStartY;
            this.textLineSpacing = textLineSpacing;
            this.textColor = textColor;
        }
    }

    // ============================================================
    // 🎯 НАСТРОЙКИ КАЖДОГО ШАГА (меняй здесь!)
    // ============================================================
    private final TutorialStep[] steps = {

        // ШАГ 1: УПРАВЛЕНИЕ
        new TutorialStep(
            "УПРАВЛЕНИЕ",
            new String[]{
                "Тапай по экрану — дайвер плывёт вверх",
                "Отпусти — дайвер опускается вниз",
                "Дайвер автоматически движется вправо"
            },
            2.0f, -50f, 180f, bioCyan,   // Заголовок: крупный, слегка влево, высоко
            1.2f, -300f, 50f, 60f, primaryText  // Текст: слева
        ),

        // ШАГ 2: КРИСТАЛЛЫ И БАЛЛЫ
        new TutorialStep(
            "КРИСТАЛЛЫ И БАЛЛЫ",
            new String[]{
                "Синие кристаллы → +100 очков",
                "Золотые кристаллы → +300 очков",
                "Собирай их для прохождения квестов!"
            },
            1.8f, -100f, 180f, gold,
            1.1f, -200f, 50f, 60f, primaryText
        ),

        // ШАГ 3: КИСЛОРОД
        new TutorialStep(
            "КИСЛОРОД",
            new String[]{
                "Кислород постоянно расходуется",
                "Пузырьки воздуха восстанавливают +20%",
                "Следи за шкалой в левом верхнем углу"
            },
            2.2f, -50f, 180f, oxygenOk,
            1.2f, -300f, 50f, 60f, primaryText
        ),

        // ШАГ 4: ОПАСНОСТИ
        new TutorialStep(
            "ОПАСНОСТИ",
            new String[]{
                "Избегай ядовитых медуз!",
                "Укус медузы отнимает 25% кислорода",
                "После укуса дайвер краснеет на секунду"
            },
            2.0f, -50f, 180f, oxygenCritical,   // Красный заголовок!
            1.2f, -300f, 50f, 60f, primaryText
        ),

        // ШАГ 5: СИСТЕМА КВЕСТОВ
        new TutorialStep(
            "СИСТЕМА КВЕСТОВ",
            new String[]{
                "Выполняй квесты в определённом порядке",
                "После выполнения квеста игра останавливается",
                "Выполни все 6 заданий и стань Хранителем Океана!"
            },
            1.7f, -50f, 180f, gold,
            1.0f, -300f, 50f, 60f, gold
        )
    };

    public TutorialScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        step = 1;   // Начинаем с первого шага
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Переход к следующему шагу по тапу
        if (Gdx.input.justTouched()) {
            step++;
            if (step > 5) {
                game.setScreen(game.menuScreen);   // Обучение закончено → в меню
                return;
            }
        }

        game.batch.begin();

        // ФОН
        if (GameResources.backgroundMenu != null) {
            game.batch.draw(GameResources.backgroundMenu, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        TutorialStep currentStep = steps[step - 1];

        // ========== НОМЕР ШАГА (всегда вверху по центру) ==========
        game.textView.setScale(GameSettings.size(1.3f));
        game.textView.draw(game.batch, "ОБУЧЕНИЕ (" + step + "/5)",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - 80),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100), gold);

        // ========== ЗАГОЛОВОК (индивидуальные настройки) ==========
        game.textView.setScale(GameSettings.size(currentStep.titleScale));
        float titleDrawX = centerX + GameSettings.x(currentStep.titleX)
            - (currentStep.title.length() * GameSettings.size(10));
        float titleDrawY = centerY + GameSettings.y(currentStep.titleY);
        game.textView.draw(game.batch, currentStep.title, titleDrawX, titleDrawY, currentStep.titleColor);

        // ========== ТЕКСТ (индивидуальные настройки) ==========
        game.textView.setScale(GameSettings.size(currentStep.textScale));

        float textStartX = centerX + GameSettings.x(currentStep.textX);
        float textStartY = centerY + GameSettings.y(currentStep.textStartY);

        for (int i = 0; i < currentStep.texts.length; i++) {
            // Выбор цвета для строки (можно переопределить для опасностей)
            Color textColor = currentStep.textColor;

            // Шаг 4 (опасности): первая строка красная для акцента
            if (step == 4 && i == 0) {
                textColor = oxygenCritical;
            }
            // Шаг 5: весь текст золотой
            if (step == 5 && currentStep.textColor != gold) {
                textColor = gold;
            }

            game.textView.draw(game.batch, "• " + currentStep.texts[i],
                textStartX,
                textStartY - (i * GameSettings.size(currentStep.textLineSpacing)),
                textColor);
        }

        // ========== ПОДПИСЬ ВНИЗУ (всегда одинаковая) ==========
        game.textView.setScale(GameSettings.size(0.9f));
        game.textView.draw(game.batch, "[ Коснись экрана для продолжения ]",
            centerX - GameSettings.size(200),
            GameSettings.y(60), oxygenOk);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
