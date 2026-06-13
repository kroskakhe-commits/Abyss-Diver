package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;

/**
 * Экран обучения.
 * Показывает 5 шагов с основными механиками игры:
 * 1. Управление дайвером
 * 2. Кристаллы и бонусы
 * 3. Система кислорода
 * 4. Опасности (враги)
 * 5. Квесты и локации
 * Перелистывание по касанию экрана.
 */
public class TutorialScreen implements Screen {
    private final Main game;
    private int step = 1;

    private final Color bioCyan = new Color(0.24f, 0.95f, 1f, 1f);
    private final Color gold = new Color(1f, 0.78f, 0.23f, 1f);
    private final Color primaryText = new Color(0.88f, 0.94f, 1f, 1f);
    private final Color oxygenOk = new Color(0f, 0.9f, 0.46f, 1f);
    private final Color oxygenCritical = new Color(1f, 0.09f, 0.26f, 1f);

    /**
     * Внутренний класс для хранения данных каждого шага обучения.
     */
    private static class TutorialStep {
        String title;
        String[] texts;
        float titleScale;
        float titleX;
        float titleY;
        Color titleColor;
        float textScale;
        float textX;
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

    /**
     * Массив шагов обучения.
     */
    private final TutorialStep[] steps = {
        // ШАГ 1: УПРАВЛЕНИЕ
        new TutorialStep(
            "УПРАВЛЕНИЕ",
            new String[]{
                "Тапай по экрану - дайвер плывёт вверх",
                "Отпусти - дайвер опускается вниз",
                "Дайвер автоматически движется вправо"
            },
            2.0f, -50f, 180f, bioCyan,
            1.2f, -300f, 50f, 60f, primaryText
        ),

        // ШАГ 2: КРИСТАЛЛЫ, БАЛЛЫ И БОНУСЫ
        new TutorialStep(
            "КРИСТАЛЛЫ И БОНУСЫ",
            new String[]{
                "Синие кристаллы -> +100 очков",
                "Золотые кристаллы -> +300 очков",
                "",
                "БОНУСЫ:",
                "   Звезда -> +500 очков, восстанавливает кислород",
                "   Щит -> +300 очков, даёт временную неуязвимость",
                "   Молния -> +200 очков, увеличивает скорость"
            },
            1.8f, -100f, 180f, gold,
            1.1f, -200f, 70f, 45f, primaryText
        ),

        // ШАГ 3: КИСЛОРОД
        new TutorialStep(
            "КИСЛОРОД",
            new String[]{
                "Кислород постоянно расходуется",
                "Пузырьки воздуха восстанавливают +20% кислорода",
                "Следи за шкалой в левом верхнем углу"
            },
            2.2f, -50f, 180f, oxygenOk,
            1.2f, -300f, 50f, 60f, primaryText
        ),

        // ШАГ 4: ОПАСНОСТИ
        new TutorialStep(
            "ОПАСНОСТИ",
            new String[]{
                "Медузы - укус отнимает 25% кислорода",
                "Акулы - опасны, отнимают 35% кислорода",
                "Осьминоги - хаотично двигаются, урон 20%",
                "Электрические скаты - самый опасный враг, урон 40%"
            },
            2.0f, -50f, 180f, oxygenCritical,
            1.0f, -280f, 50f, 55f, primaryText
        ),

        // ШАГ 5: СИСТЕМА КВЕСТОВ И ЛОКАЦИЙ
        new TutorialStep(
            "КВЕСТЫ И ЛОКАЦИИ",
            new String[]{
                "Выполняй 30 квестов в определённом порядке",
                "Каждая новая локация открывается после квестов:",
                "-> 6 квестов: Коралловый риф (появляются акулы)",
                "-> 16 квестов: Тёмные глубины (появляются осьминоги)",
                "-> 26 квестов: Затонувший город (появляются скаты)"
            },
            1.7f, -50f, 180f, gold,
            0.9f, -250f, 50f, 45f, primaryText
        )
    };

    public TutorialScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        step = 1;
        game.camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Переход к следующему шагу по касанию
        if (Gdx.input.justTouched()) {
            step++;
            if (step > 5) {
                game.setScreen(game.menuScreen);
                return;
            }
        }

        game.batch.begin();

        // Фон
        if (GameResources.backgroundMenu != null) {
            game.batch.draw(GameResources.backgroundMenu, 0, 0,
                GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }

        float centerX = GameSettings.SCREEN_WIDTH / 2;
        float centerY = GameSettings.SCREEN_HEIGHT / 2;

        TutorialStep currentStep = steps[step - 1];

        // Номер шага
        game.textView.setScale(GameSettings.size(1.3f));
        game.textView.draw(game.batch, "ОБУЧЕНИЕ (" + step + "/5)",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - 80),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 100), gold);

        // Заголовок шага
        game.textView.setScale(GameSettings.size(currentStep.titleScale));
        float titleDrawX = centerX + GameSettings.x(currentStep.titleX)
            - (currentStep.title.length() * GameSettings.size(10));
        float titleDrawY = centerY + GameSettings.y(currentStep.titleY);
        game.textView.draw(game.batch, currentStep.title, titleDrawX, titleDrawY, currentStep.titleColor);

        // Текст шага
        game.textView.setScale(GameSettings.size(currentStep.textScale));

        float textStartX = centerX + GameSettings.x(currentStep.textX);
        float textStartY = centerY + GameSettings.y(currentStep.textStartY);

        for (int i = 0; i < currentStep.texts.length; i++) {
            String line = currentStep.texts[i];
            Color lineColor = currentStep.textColor;

            // Специальная обработка цвета для определённых строк
            if (step == 5) {
                if (line.contains("6 квестов") || line.contains("16 квестов") || line.contains("26 квестов")) {
                    lineColor = gold;
                } else {
                    lineColor = primaryText;
                }
            }

            if (step == 2 && line.equals("БОНУСЫ:")) {
                lineColor = gold;
            }

            game.textView.draw(game.batch, (line.isEmpty() ? "" : "• ") + line,
                textStartX,
                textStartY - (i * GameSettings.size(currentStep.textLineSpacing)),
                lineColor);
        }

        // Подпись внизу экрана
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
