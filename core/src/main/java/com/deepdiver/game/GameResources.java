package com.deepdiver.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Статический склад всех текстур игры.
 * Загружаем всё при старте, выгружаем при выходе.
 * Без этого никак — LibGDX любит явное управление памятью.
 */
public class GameResources {
    // Фоны под разные экраны
    public static Texture backgroundMenu;
    public static Texture backgroundGame;
    public static Texture backgroundVictory;
    public static Texture blackout;      // Полупрозрачное затемнение для UI-окон
    public static Texture bubble;

    // ========== КНОПКИ ==========
    // Меню
    public static Texture buttonStartMarine;
    public static Texture buttonQuestMarine;
    public static Texture buttonTutorialMarine;
    public static Texture buttonShopMarine;

    // Квесты
    public static Texture buttonStartDiveMarine;
    public static Texture buttonBackMenuMarine;
    public static Texture buttonResetMarine;

    // Настройки
    public static Texture buttonBackMarine;

    // Пауза
    public static Texture buttonContinueMarine;
    public static Texture buttonExitMarine;
    public static Texture pauseIconMarine;

    // Игровые объекты
    public static Texture crystal;
    public static Texture crystalGold;
    public static Texture settingsIcon;

    // Специальная кнопка на экране победы
    public static Texture victoryResetBtn;

    // Анимация дайвера (три набора под разные скины)
    public static Texture[] diverFrames0;
    public static Texture[] diverFrames1;
    public static Texture[] diverFrames2;

    // Анимация врагов
    public static Texture[] jellyfishFrames;

    public static void load() {
        // Фоны
        backgroundMenu = new Texture(Gdx.files.internal("background_ocean.png"));
        backgroundGame = new Texture(Gdx.files.internal("background_game.png"));
        backgroundVictory = new Texture(Gdx.files.internal("background_final.png"));
        blackout = new Texture(Gdx.files.internal("blackout_full.png"));
        bubble = new Texture(Gdx.files.internal("bubble.png"));

        // Кнопки меню
        buttonStartMarine = new Texture(Gdx.files.internal("button_play.png"));
        buttonQuestMarine = new Texture(Gdx.files.internal("button_quest_marine.png"));
        buttonTutorialMarine = new Texture(Gdx.files.internal("button_tutorial_marine.png"));
        buttonShopMarine = new Texture(Gdx.files.internal("button_shop_marine.png"));

        // Кнопки квестов
        buttonStartDiveMarine = new Texture(Gdx.files.internal("button_start_dive_marine.png"));
        buttonBackMenuMarine = new Texture(Gdx.files.internal("button_back_menu_marine.png"));
        buttonResetMarine = new Texture(Gdx.files.internal("button_reset_marine.png"));

        // Кнопки настроек
        buttonBackMarine = new Texture(Gdx.files.internal("button_back_marine.png"));

        // Кнопки паузы
        buttonContinueMarine = new Texture(Gdx.files.internal("button_continue_marine.png"));
        buttonExitMarine = new Texture(Gdx.files.internal("button_exit_marine.png"));
        pauseIconMarine = new Texture(Gdx.files.internal("pause_icon_marine.png"));

        // Игровые объекты
        crystal = new Texture(Gdx.files.internal("crystal.png"));
        crystalGold = new Texture(Gdx.files.internal("crystal_gold.png"));
        settingsIcon = new Texture(Gdx.files.internal("settings_icon.png"));

        // Победа — кнопка сброса прогресса (может отсутствовать, проверяем)
        try {
            victoryResetBtn = new Texture(Gdx.files.internal("victory_reset_btn.png"));
            System.out.println("✅ victory_reset_btn.png загружена");
        } catch (Exception e) {
            System.out.println("❌ victory_reset_btn.png НЕ НАЙДЕНА! Будет использован текст");
            victoryResetBtn = null;
        }

        // Анимация дайвера — 3 скина по 3 кадра
        diverFrames0 = new Texture[]{
            new Texture(Gdx.files.internal("diver_1.png")),
            new Texture(Gdx.files.internal("diver_2.png")),
            new Texture(Gdx.files.internal("diver_3.png"))
        };

        diverFrames1 = new Texture[]{
            new Texture(Gdx.files.internal("diver_1_1.png")),
            new Texture(Gdx.files.internal("diver_1_2.png")),
            new Texture(Gdx.files.internal("diver_1_3.png"))
        };

        diverFrames2 = new Texture[]{
            new Texture(Gdx.files.internal("diver_2_1.png")),
            new Texture(Gdx.files.internal("diver_2_2.png")),
            new Texture(Gdx.files.internal("diver_2_3.png"))
        };

        // Анимация медуз
        jellyfishFrames = new Texture[]{
            new Texture(Gdx.files.internal("jellyfish_1.png")),
            new Texture(Gdx.files.internal("jellyfish_2.png")),
            new Texture(Gdx.files.internal("jellyfish_3.png"))
        };

        System.out.println("✅ Все текстуры загружены!");
    }

    /**
     * Освобождаем память.
     * Каждый Texture обязан быть уничтожен, иначе утечка.
     */
    public static void dispose() {
        if (backgroundMenu != null) backgroundMenu.dispose();
        if (backgroundGame != null) backgroundGame.dispose();
        if (backgroundVictory != null) backgroundVictory.dispose();
        if (blackout != null) blackout.dispose();
        if (bubble != null) bubble.dispose();

        if (buttonStartMarine != null) buttonStartMarine.dispose();
        if (buttonQuestMarine != null) buttonQuestMarine.dispose();
        if (buttonTutorialMarine != null) buttonTutorialMarine.dispose();
        if (buttonShopMarine != null) buttonShopMarine.dispose();
        if (buttonStartDiveMarine != null) buttonStartDiveMarine.dispose();
        if (buttonBackMenuMarine != null) buttonBackMenuMarine.dispose();
        if (buttonResetMarine != null) buttonResetMarine.dispose();
        if (buttonBackMarine != null) buttonBackMarine.dispose();
        if (buttonContinueMarine != null) buttonContinueMarine.dispose();
        if (buttonExitMarine != null) buttonExitMarine.dispose();
        if (pauseIconMarine != null) pauseIconMarine.dispose();

        if (victoryResetBtn != null) victoryResetBtn.dispose();

        if (crystal != null) crystal.dispose();
        if (crystalGold != null) crystalGold.dispose();
        if (settingsIcon != null) settingsIcon.dispose();

        // Массивы текстур — чистим каждый элемент
        if (diverFrames0 != null) {
            for (Texture t : diverFrames0) if (t != null) t.dispose();
        }
        if (diverFrames1 != null) {
            for (Texture t : diverFrames1) if (t != null) t.dispose();
        }
        if (diverFrames2 != null) {
            for (Texture t : diverFrames2) if (t != null) t.dispose();
        }

        if (jellyfishFrames != null) {
            for (Texture t : jellyfishFrames) if (t != null) t.dispose();
        }
    }
}
