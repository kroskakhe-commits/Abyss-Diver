package com.deepdiver.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.GameResources;
import com.deepdiver.game.GameSettings;
import com.deepdiver.game.Main;
import com.deepdiver.game.managers.MemoryManager;

/**
 * Магазин скинов.
 * Позволяет покупать новые скины за общий счёт игрока.
 * Доступно 3 скина:
 * - Classic Diver (бесплатный, разблокирован по умолчанию)
 * - Golden Diver (5000 очков)
 * - Neon Diver (10000 очков)
 * Купленные скины можно экипировать.
 */
public class ShopScreen extends ScreenAdapter {
    private Main main;
    private Vector3 touch;

    private final int SKINS_COUNT = 3;
    private final String[] skinNames = {"Classic Diver", "Golden Diver", "Neon Diver"};
    private final int[] skinPrices = {0, 5000, 10000};
    private final Texture[] skinPreviews = new Texture[SKINS_COUNT];

    private Rectangle[] skinCards = new Rectangle[SKINS_COUNT];
    private Rectangle backButton;

    private int totalScore;
    private int equippedSkin;

    public ShopScreen(Main main) {
        this.main = main;
        this.touch = new Vector3();

        // Загрузка превью скинов (первый кадр анимации)
        skinPreviews[0] = GameResources.diverFrames0[0];
        skinPreviews[1] = GameResources.diverFrames1[0];
        skinPreviews[2] = GameResources.diverFrames2[0];

        // Кнопка "НАЗАД" (правый верхний угол)
        float backSize = GameSettings.size(60);
        backButton = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - 80),
            GameSettings.y(30),
            backSize,
            backSize
        );

        // Карточки скинов в горизонтальный ряд
        float cardWidth = GameSettings.size(350);
        float cardHeight = GameSettings.size(450);
        float cardSpacing = GameSettings.size(30);
        float totalWidth = SKINS_COUNT * cardWidth + (SKINS_COUNT - 1) * cardSpacing;
        float startX = (GameSettings.SCREEN_WIDTH - totalWidth) / 2;
        float cardY = GameSettings.SCREEN_HEIGHT / 2 - cardHeight / 2 - GameSettings.size(30);

        for (int i = 0; i < SKINS_COUNT; i++) {
            skinCards[i] = new Rectangle(
                startX + i * (cardWidth + cardSpacing),
                cardY,
                cardWidth,
                cardHeight
            );
        }

        loadData();
    }

    /**
     * Загружает актуальные данные: общий счёт и экипированный скин.
     */
    private void loadData() {
        totalScore = MemoryManager.loadTotalScore();
        equippedSkin = MemoryManager.getEquippedSkin();
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.07f, 0.09f, 0.17f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        main.camera.update();
        main.batch.setProjectionMatrix(main.camera.combined);

        main.batch.begin();

        // Фон
        main.batch.draw(GameResources.backgroundMenu, 0, 0,
            GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        // Заголовок
        main.textView.setScale(GameSettings.size(2.0f));
        main.textView.draw(main.batch, "МАГАЗИН СКИНОВ",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - 160),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 80), Color.GOLD);

        // Отображение общего счёта
        main.textView.setScale(GameSettings.size(1.2f));
        main.textView.draw(main.batch, "Твои очки: " + totalScore,
            GameSettings.x(50),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 80), Color.WHITE);

        // Отрисовка карточек скинов
        for (int i = 0; i < SKINS_COUNT; i++) {
            Rectangle card = skinCards[i];

            // Тёмный фон карточки
            main.batch.setColor(0.1f, 0.1f, 0.2f, 0.8f);
            main.batch.draw(GameResources.blackout,
                card.x, card.y,
                card.width, card.height);
            main.batch.setColor(1, 1, 1, 1);

            // Превью скина
            float previewSize = GameSettings.size(160);
            float previewX = card.x + (card.width - previewSize) / 2;
            float previewY = card.y + card.height - previewSize - GameSettings.size(40);
            main.batch.draw(skinPreviews[i], previewX, previewY, previewSize, previewSize);

            // Название скина
            main.textView.setScale(GameSettings.size(1.2f));
            float nameX = card.x + (card.width - skinNames[i].length() * GameSettings.size(20)) / 2;
            float nameY = previewY - GameSettings.size(15);
            main.textView.draw(main.batch, skinNames[i], nameX, nameY, Color.WHITE);

            // Статус или кнопка действия
            main.textView.setScale(GameSettings.size(1.1f));

            if (MemoryManager.isSkinUnlocked(i)) {
                if (equippedSkin == i) {
                    // Уже экипирован
                    main.textView.draw(main.batch, "✓ ВЫБРАН",
                        card.x + (card.width - GameSettings.size(100)) / 2,
                        card.y + GameSettings.size(60), Color.GREEN);
                } else {
                    // Разблокирован, можно выбрать
                    main.textView.draw(main.batch, "ВЫБРАТЬ",
                        card.x + (card.width - GameSettings.size(100)) / 2,
                        card.y + GameSettings.size(55), Color.CYAN);
                }
            } else {
                // Не разблокирован, показываем цену и кнопку покупки
                String priceText = skinPrices[i] + " очков";
                main.textView.draw(main.batch, priceText,
                    card.x + (card.width - priceText.length() * GameSettings.size(18)) / 2,
                    card.y + GameSettings.size(95), Color.GOLD);

                main.textView.draw(main.batch, "КУПИТЬ",
                    card.x + (card.width - GameSettings.size(80)) / 2,
                    card.y + GameSettings.size(50), Color.ORANGE);
            }
        }

        // Кнопка "НАЗАД"
        if (GameResources.buttonBackMarine != null) {
            main.batch.draw(GameResources.buttonBackMarine,
                backButton.x, backButton.y,
                backButton.width, backButton.height);
        } else {
            main.textView.setScale(GameSettings.size(1.5f));
            main.textView.draw(main.batch, "← НАЗАД",
                backButton.x + GameSettings.size(12),
                backButton.y + GameSettings.size(40), Color.CYAN);
        }

        main.batch.end();
    }

    /**
     * Обрабатывает нажатия на карточки скинов.
     * Логика:
     * - Если скин разблокирован и не выбран → выбрать его
     * - Если скин не разблокирован и хватает очков → купить
     * - Если очков не хватает → ничего не происходит
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);

            // Проверка нажатий на карточки скинов
            for (int i = 0; i < SKINS_COUNT; i++) {
                Rectangle card = skinCards[i];

                // Область нажатия в нижней части карточки (зона кнопки)
                Rectangle actionRect = new Rectangle(
                    card.x + (card.width - GameSettings.size(120)) / 2,
                    card.y + GameSettings.size(35),
                    GameSettings.size(120),
                    GameSettings.size(50)
                );

                if (actionRect.contains(touch.x, touch.y)) {
                    if (MemoryManager.isSkinUnlocked(i)) {
                        // Скин уже куплен - выбираем его
                        if (equippedSkin != i) {
                            MemoryManager.equipSkin(i);
                            equippedSkin = i;
                            loadData();
                            return;
                        }
                    } else {
                        // Скин не куплен - пробуем купить
                        if (totalScore >= skinPrices[i]) {
                            MemoryManager.unlockSkin(i);
                            totalScore -= skinPrices[i];
                            MemoryManager.saveTotalScore(totalScore);
                            loadData();
                            return;
                        }
                        // Не хватает очков - игнорируем
                    }
                }
            }

            // Кнопка "НАЗАД"
            if (backButton.contains(touch.x, touch.y)) {
                main.setScreen(main.menuScreen);
            }
        }
    }

    @Override
    public void dispose() {}
}
