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
 * МАГАЗИН СКИНОВ.
 *
 * Здесь игрок может покупать новые скины для дайвера за общий счёт.
 * Скины разблокируются навсегда, выбранный скин применяется сразу.
 *
 * Система простая: 3 скина, у каждого своя цена.
 * Скин 0 (Classic Diver) — бесплатный, разблокирован по умолчанию.
 *
 * ВАЖНО: разблокированные скины хранятся в битовой маске в MemoryManager.
 */
public class ShopScreen extends ScreenAdapter {
    private Main main;
    private Vector3 touch;

    private final int SKINS_COUNT = 3;
    private final String[] skinNames = {"Classic Diver", "Golden Diver", "Neon Diver"};
    private final int[] skinPrices = {0, 5000, 10000};
    private final Texture[] skinPreviews = new Texture[SKINS_COUNT];   // Превью скинов

    private Rectangle[] skinCards = new Rectangle[SKINS_COUNT];   // Карточки скинов (кнопки)
    private Rectangle backButton;

    private int totalScore;      // Общий счёт игрока (из GameSession)
    private int equippedSkin;    // Какой скин сейчас надет

    public ShopScreen(Main main) {
        this.main = main;
        this.touch = new Vector3();

        // Превью — первый кадр анимации каждого скина
        skinPreviews[0] = GameResources.diverFrames0[0];
        skinPreviews[1] = GameResources.diverFrames1[0];
        skinPreviews[2] = GameResources.diverFrames2[0];

        // КНОПКА НАЗАД (правый верхний угол)
        float backSize = GameSettings.size(60);
        backButton = new Rectangle(
            GameSettings.x(GameSettings.DESIGN_WIDTH - 80),
            GameSettings.y(30),
            backSize,
            backSize
        );

        // ========== КАРТОЧКИ СКИНОВ — ГОРИЗОНТАЛЬНО ==========
        float cardWidth = GameSettings.size(350);
        float cardHeight = GameSettings.size(450);
        float cardSpacing = GameSettings.size(30);
        float totalWidth = SKINS_COUNT * cardWidth + (SKINS_COUNT - 1) * cardSpacing;
        float startX = (GameSettings.SCREEN_WIDTH - totalWidth) / 2;

        // Карточки опущены ниже центра, чтобы не перекрывать заголовок
        float cardY = GameSettings.SCREEN_HEIGHT / 2 - cardHeight / 2 - GameSettings.size(30);

        for (int i = 0; i < SKINS_COUNT; i++) {
            skinCards[i] = new Rectangle(
                startX + i * (cardWidth + cardSpacing),
                cardY,
                cardWidth,
                cardHeight
            );
        }

        loadData();   // Загружаем текущие данные
    }

    /**
     * Загружаем актуальные данные: общий счёт и выбранный скин.
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

        // ФОН
        main.batch.draw(GameResources.backgroundMenu, 0, 0,
            GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        // ЗАГОЛОВОК
        main.textView.setScale(GameSettings.size(2.0f));
        main.textView.draw(main.batch, "МАГАЗИН СКИНОВ",
            GameSettings.x(GameSettings.DESIGN_WIDTH / 2 - 160),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 80), Color.GOLD);

        // ОБЩИЙ СЧЁТ (показываем, сколько очков есть у игрока)
        main.textView.setScale(GameSettings.size(1.2f));
        main.textView.draw(main.batch, "Твои очки: " + totalScore,
            GameSettings.x(50),
            GameSettings.y(GameSettings.DESIGN_HEIGHT - 80), Color.WHITE);

        // ========== ОТРИСОВКА КАРТОЧЕК СКИНОВ ==========
        for (int i = 0; i < SKINS_COUNT; i++) {
            Rectangle card = skinCards[i];

            // Тёмный фон карточки
            main.batch.setColor(0.1f, 0.1f, 0.2f, 0.8f);
            main.batch.draw(GameResources.blackout,
                card.x, card.y,
                card.width, card.height);
            main.batch.setColor(1, 1, 1, 1);

            // ПРЕВЬЮ СКИНА (картинка)
            float previewSize = GameSettings.size(160);
            float previewX = card.x + (card.width - previewSize) / 2;
            float previewY = card.y + card.height - previewSize - GameSettings.size(40);
            main.batch.draw(skinPreviews[i], previewX, previewY, previewSize, previewSize);

            // НАЗВАНИЕ СКИНА
            main.textView.setScale(GameSettings.size(1.2f));
            float nameX = card.x + (card.width - skinNames[i].length() * GameSettings.size(20)) / 2;
            float nameY = previewY - GameSettings.size(15);
            main.textView.draw(main.batch, skinNames[i], nameX, nameY, Color.WHITE);

            // ========== КНОПКИ/СТАТУС ==========
            main.textView.setScale(GameSettings.size(1.1f));

            if (MemoryManager.isSkinUnlocked(i)) {
                if (equippedSkin == i) {
                    // Уже выбран — зелёная галочка
                    main.textView.draw(main.batch, "✓ ВЫБРАН",
                        card.x + (card.width - GameSettings.size(100)) / 2,
                        card.y + GameSettings.size(60), Color.GREEN);
                } else {
                    // Разблокирован, но не выбран — кнопка "ВЫБРАТЬ"
                    main.textView.draw(main.batch, "ВЫБРАТЬ",
                        card.x + (card.width - GameSettings.size(100)) / 2,
                        card.y + GameSettings.size(55), Color.CYAN);
                }
            } else {
                // Не разблокирован — показываем цену и кнопку "КУПИТЬ"
                String priceText = skinPrices[i] + " очков";
                main.textView.draw(main.batch, priceText,
                    card.x + (card.width - priceText.length() * GameSettings.size(18)) / 2,
                    card.y + GameSettings.size(95), Color.GOLD);

                main.textView.draw(main.batch, "КУПИТЬ",
                    card.x + (card.width - GameSettings.size(80)) / 2,
                    card.y + GameSettings.size(50), Color.ORANGE);
            }
        }

        // КНОПКА НАЗАД
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
     * Обрабатываем нажатия на карточки скинов.
     *
     * Логика:
     * - Если скин разблокирован и не выбран → выбираем его
     * - Если скин не разблокирован и хватает очков → покупаем
     * - Если очков не хватает — ничего не делаем (нет денег — не покупай)
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);

            // Проверяем нажатие на каждую карточку
            for (int i = 0; i < SKINS_COUNT; i++) {
                Rectangle card = skinCards[i];

                // Область нажатия — нижняя часть карточки (где находится кнопка)
                Rectangle actionRect = new Rectangle(
                    card.x + (card.width - GameSettings.size(120)) / 2,
                    card.y + GameSettings.size(35),
                    GameSettings.size(120),
                    GameSettings.size(50)
                );

                if (actionRect.contains(touch.x, touch.y)) {
                    if (MemoryManager.isSkinUnlocked(i)) {
                        // Скин уже куплен — просто выбираем
                        if (equippedSkin != i) {
                            MemoryManager.equipSkin(i);
                            equippedSkin = i;
                            System.out.println("✅ Выбран скин: " + skinNames[i]);
                            loadData();
                            return;
                        }
                    } else {
                        // Скин не куплен — пробуем купить
                        if (totalScore >= skinPrices[i]) {
                            MemoryManager.unlockSkin(i);
                            totalScore -= skinPrices[i];
                            MemoryManager.saveTotalScore(totalScore);
                            System.out.println("✅ Куплен скин: " + skinNames[i]);
                            loadData();
                            return;
                        }
                        // Не хватает очков — молча игнорируем (можно было бы показать сообщение)
                    }
                }
            }

            // Кнопка "Назад"
            if (backButton.contains(touch.x, touch.y)) {
                main.setScreen(main.menuScreen);
            }
        }
    }

    @Override
    public void dispose() {}
}
