package com.deepdiver.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.deepdiver.game.components.TextView;
import com.deepdiver.game.managers.AudioManager;
import com.deepdiver.game.screens.*;

/**
 * Главный класс — входная точка игры.
 * LibGDX требует Game или ApplicationListener.
 * Здесь хранятся все экраны и глобальные компоненты (batch, камера, аудио).
 */
public class Main extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public TextView textView;
    public AudioManager audioManager;
    public Vector3 touch;                      // Для обработки касаний

    // Все экраны живут здесь, чтобы не создавать каждый раз заново
    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public SettingsScreen settingsScreen;
    public TutorialScreen tutorialScreen;
    public MissionBoardScreen missionBoardScreen;

    @Override
    public void create() {
        // Узнаём реальные размеры экрана и настраиваем масштабирование
        float realWidth = Gdx.graphics.getWidth();
        float realHeight = Gdx.graphics.getHeight();
        GameSettings.updateScreenSize(realWidth, realHeight);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, realWidth, realHeight);

        textView = new TextView();
        audioManager = new AudioManager();
        GameResources.load();      // Загружаем все текстуры
        GameSession.init();        // Загружаем сохранённый счёт

        // Создаём все экраны
        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        tutorialScreen = new TutorialScreen(this);
        missionBoardScreen = new MissionBoardScreen(this);

        // Стартуем с меню
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
