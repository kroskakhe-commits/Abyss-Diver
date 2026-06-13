package com.deepdiver.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Централизованная загрузка и хранение всех текстур игры.
 * Все текстуры доступны как статические поля.
 * Метод dispose() освобождает все ресурсы.
 */
public class GameResources {
    // Фоны
    public static Texture backgroundMenu;
    public static Texture backgroundGame;
    public static Texture backgroundReef;
    public static Texture backgroundDeep;
    public static Texture backgroundRuins;
    public static Texture backgroundVictory;
    public static Texture backgroundMission;
    public static Texture blackout;
    public static Texture bubble;

    // Кнопки
    public static Texture buttonStartMarine;
    public static Texture buttonQuestMarine;
    public static Texture buttonTutorialMarine;
    public static Texture buttonShopMarine;
    public static Texture buttonStartDiveMarine;
    public static Texture buttonBackMenuMarine;
    public static Texture buttonResetMarine;
    public static Texture buttonBackMarine;
    public static Texture buttonContinueMarine;
    public static Texture buttonExitMarine;
    public static Texture pauseIconMarine;
    public static Texture victoryResetBtn;
    public static Texture buttonRestartMarine;
    public static Texture buttonRecordsMarine;
    public static Texture settingsIcon;

    // Игровые объекты
    public static Texture crystal;
    public static Texture crystalGold;

    // Анимационные кадры врагов
    public static Texture[] jellyfishFrames;
    public static Texture[] sharkFrames;
    public static Texture[] octopusFrames;
    public static Texture[] rayFrames;

    // Бонусы
    public static Texture starPower;
    public static Texture shieldPower;
    public static Texture speedPower;

    // Скины дайвера
    public static Texture[] diverFrames0;
    public static Texture[] diverFrames1;
    public static Texture[] diverFrames2;

    /**
     * Загружает все текстуры из папки assets.
     */
    public static void load() {
        // Фоны
        backgroundMenu = new Texture(Gdx.files.internal("background_ocean.png"));
        backgroundGame = new Texture(Gdx.files.internal("background_game.png"));
        backgroundReef = new Texture(Gdx.files.internal("background_reef.png"));
        backgroundDeep = new Texture(Gdx.files.internal("background_deep.png"));
        backgroundRuins = new Texture(Gdx.files.internal("background_ruins.png"));
        backgroundVictory = new Texture(Gdx.files.internal("background_final.png"));
        backgroundMission = new Texture(Gdx.files.internal("background_mission.png"));
        blackout = new Texture(Gdx.files.internal("blackout_full.png"));
        bubble = new Texture(Gdx.files.internal("bubble.png"));

        // Кнопки
        buttonStartMarine = new Texture(Gdx.files.internal("button_play.png"));
        buttonQuestMarine = new Texture(Gdx.files.internal("button_quest_marine.png"));
        buttonTutorialMarine = new Texture(Gdx.files.internal("button_tutorial_marine.png"));
        buttonShopMarine = new Texture(Gdx.files.internal("button_shop_marine.png"));
        buttonStartDiveMarine = new Texture(Gdx.files.internal("button_start_dive_marine.png"));
        buttonBackMenuMarine = new Texture(Gdx.files.internal("button_back_menu_marine.png"));
        buttonResetMarine = new Texture(Gdx.files.internal("button_reset_marine.png"));
        buttonBackMarine = new Texture(Gdx.files.internal("button_back_marine.png"));
        buttonContinueMarine = new Texture(Gdx.files.internal("button_continue_marine.png"));
        buttonExitMarine = new Texture(Gdx.files.internal("button_exit_marine.png"));
        settingsIcon = new Texture(Gdx.files.internal("settings_icon.png"));

        try {
            pauseIconMarine = new Texture(Gdx.files.internal("pause_icon_marine.png"));
        } catch (Exception e) {
            pauseIconMarine = null;
        }

        try {
            victoryResetBtn = new Texture(Gdx.files.internal("victory_reset_btn.png"));
        } catch (Exception e) {
            victoryResetBtn = null;
        }

        try {
            buttonRestartMarine = new Texture(Gdx.files.internal("button_restart_marine.png"));
        } catch (Exception e) {
            buttonRestartMarine = null;
        }

        try {
            buttonRecordsMarine = new Texture(Gdx.files.internal("button_records_marine.png"));
        } catch (Exception e) {
            buttonRecordsMarine = null;
        }

        // Игровые объекты
        crystal = new Texture(Gdx.files.internal("crystal.png"));
        crystalGold = new Texture(Gdx.files.internal("crystal_gold.png"));

        // Скины дайвера
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

        // Медузы
        jellyfishFrames = new Texture[]{
            new Texture(Gdx.files.internal("jellyfish_1.png")),
            new Texture(Gdx.files.internal("jellyfish_2.png")),
            new Texture(Gdx.files.internal("jellyfish_3.png"))
        };

        // Акулы
        try {
            sharkFrames = new Texture[]{
                new Texture(Gdx.files.internal("shark_1.png")),
                new Texture(Gdx.files.internal("shark_2.png")),
                new Texture(Gdx.files.internal("shark_3.png"))
            };
        } catch (Exception e) {
            sharkFrames = null;
        }

        // Осьминоги
        try {
            octopusFrames = new Texture[]{
                new Texture(Gdx.files.internal("octopus_1.png")),
                new Texture(Gdx.files.internal("octopus_2.png")),
                new Texture(Gdx.files.internal("octopus_3.png"))
            };
        } catch (Exception e) {
            octopusFrames = null;
        }

        // Скаты
        try {
            rayFrames = new Texture[]{
                new Texture(Gdx.files.internal("ray_1.png")),
                new Texture(Gdx.files.internal("ray_2.png")),
                new Texture(Gdx.files.internal("ray_3.png"))
            };
        } catch (Exception e) {
            rayFrames = null;
        }

        // Бонусы
        try {
            starPower = new Texture(Gdx.files.internal("star_power.png"));
            shieldPower = new Texture(Gdx.files.internal("shield_power.png"));
            speedPower = new Texture(Gdx.files.internal("speed_power.png"));
        } catch (Exception e) {
            starPower = shieldPower = speedPower = null;
        }
    }

    /**
     * Возвращает текстуру фона в зависимости от локации.
     * @param location идентификатор локации
     * @return текстура фона
     */
    public static Texture getBackgroundForLocation(int location) {
        switch (location) {
            case GameSettings.LOCATION_REEF:
                if (backgroundReef != null) return backgroundReef;
                break;
            case GameSettings.LOCATION_DEEP:
                if (backgroundDeep != null) return backgroundDeep;
                break;
            case GameSettings.LOCATION_RUINS:
                if (backgroundRuins != null) return backgroundRuins;
                break;
        }
        return backgroundGame;
    }

    /**
     * Освобождает все загруженные текстуры.
     */
    public static void dispose() {
        if (backgroundMenu != null) backgroundMenu.dispose();
        if (backgroundGame != null) backgroundGame.dispose();
        if (backgroundReef != null) backgroundReef.dispose();
        if (backgroundDeep != null) backgroundDeep.dispose();
        if (backgroundRuins != null) backgroundRuins.dispose();
        if (backgroundVictory != null) backgroundVictory.dispose();
        if (backgroundMission != null) backgroundMission.dispose();
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
        if (buttonRestartMarine != null) buttonRestartMarine.dispose();
        if (buttonRecordsMarine != null) buttonRecordsMarine.dispose();
        if (settingsIcon != null) settingsIcon.dispose();

        if (crystal != null) crystal.dispose();
        if (crystalGold != null) crystalGold.dispose();

        if (diverFrames0 != null) for (Texture t : diverFrames0) if (t != null) t.dispose();
        if (diverFrames1 != null) for (Texture t : diverFrames1) if (t != null) t.dispose();
        if (diverFrames2 != null) for (Texture t : diverFrames2) if (t != null) t.dispose();

        if (jellyfishFrames != null) for (Texture t : jellyfishFrames) if (t != null) t.dispose();
        if (sharkFrames != null) for (Texture t : sharkFrames) if (t != null) t.dispose();
        if (octopusFrames != null) for (Texture t : octopusFrames) if (t != null) t.dispose();
        if (rayFrames != null) for (Texture t : rayFrames) if (t != null) t.dispose();

        if (starPower != null) starPower.dispose();
        if (shieldPower != null) shieldPower.dispose();
        if (speedPower != null) speedPower.dispose();
    }
}
