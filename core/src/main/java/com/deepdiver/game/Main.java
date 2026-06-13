package com.deepdiver.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.components.TextView;
import com.deepdiver.game.managers.AudioManager;
import com.deepdiver.game.screens.GameScreen;
import com.deepdiver.game.screens.MenuScreen;
import com.deepdiver.game.screens.MissionBoardScreen;
import com.deepdiver.game.screens.SettingsScreen;
import com.deepdiver.game.screens.TutorialScreen;

/**
 * Главный класс приложения.
 * Точка входа в игру, наследуется от LibGDX Game.
 * Содержит глобальные компоненты и управляет переключением между экранами.
 */
public class Main extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public TextView textView;
    public AudioManager audioManager;
    public Vector3 touch;

    // Экранные компоненты
    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;
    public TutorialScreen tutorialScreen;
    public MissionBoardScreen missionBoardScreen;

    @Override
    public void create() {
        // Настройка размеров экрана и масштабирования
        float realWidth = Gdx.graphics.getWidth();
        float realHeight = Gdx.graphics.getHeight();
        GameSettings.updateScreenSize(realWidth, realHeight);

        // Инициализация глобальных компонентов
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, realWidth, realHeight);

        textView = new TextView();
        audioManager = new AudioManager();
        GameResources.load();
        GameSession.init();

        // Создание экранов
        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        tutorialScreen = new TutorialScreen(this);
        missionBoardScreen = new MissionBoardScreen(this);

        // Старт с главного меню
        setScreen(menuScreen);
    }

    @Override
    public void resize(int width, int height) {
        GameSettings.updateScreenSize(width, height);
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        textView.dispose();
        if (audioManager != null) audioManager.dispose();
        GameResources.dispose();
    }
}
