package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Управление звуковыми эффектами и музыкой.
 * Загружает аудиофайлы из assets/sounds/ и сохраняет настройки через MemoryManager.
 */
public class AudioManager {
    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound collectSound;
    public Sound hitSound;
    public Sound victorySound;

    public AudioManager() {
        // Загрузка музыки
        try {
            if (Gdx.files.internal("sounds/background_music.mp3").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music.mp3"));
                backgroundMusic.setVolume(0.8f);
                backgroundMusic.setLooping(true);
            }
        } catch (Exception e) {
            // Файл не найден - музыка не будет проигрываться
        }

        // Загрузка звуковых эффектов
        loadSound("sounds/collect.ogg", "sounds/collect.wav");
        loadSound("sounds/hit.ogg", "sounds/hit.wav");
        loadSound("sounds/victory.ogg", "sounds/victory.wav");

        updateSoundFlag();
        updateMusicFlag();
    }

    private void loadSound(String oggPath, String wavPath) {
        try {
            if (Gdx.files.internal(oggPath).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(oggPath));
                assignSound(sound, oggPath);
                return;
            }
        } catch (Exception e) {
            // Пробуем WAV
        }

        try {
            if (Gdx.files.internal(wavPath).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(wavPath));
                assignSound(sound, wavPath);
            }
        } catch (Exception e) {
            // Файл не найден
        }
    }

    private void assignSound(Sound sound, String path) {
        if (path.contains("collect")) collectSound = sound;
        else if (path.contains("hit")) hitSound = sound;
        else if (path.contains("victory")) victorySound = sound;
    }

    public void updateSoundFlag() {
        isSoundOn = MemoryManager.loadIsSoundOn();
    }

    public void updateMusicFlag() {
        isMusicOn = MemoryManager.loadIsMusicOn();
        if (backgroundMusic != null) {
            if (isMusicOn) {
                backgroundMusic.play();
            } else {
                backgroundMusic.stop();
            }
        }
    }

    public void playCollect() {
        if (isSoundOn && collectSound != null) {
            collectSound.play(0.2f);
        }
    }

    public void playHit() {
        if (isSoundOn && hitSound != null) {
            hitSound.play(0.25f);
        }
    }

    public void playVictory() {
        if (isSoundOn && victorySound != null) {
            victorySound.play(0.3f);
        }
    }

    public void dispose() {
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (collectSound != null) collectSound.dispose();
        if (hitSound != null) hitSound.dispose();
        if (victorySound != null) victorySound.dispose();
    }
}
