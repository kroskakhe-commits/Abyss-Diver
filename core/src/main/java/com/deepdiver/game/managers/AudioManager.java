package com.deepdiver.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Управляет звуками и музыкой.
 * Загружает файлы из assets/sounds/, если их нет — тихо пропускает (игра не падает).
 * Сохраняет настройки (вкл/выкл) через MemoryManager.
 */
public class AudioManager {
    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound collectSound;   // Собрал кристалл или пузырёк
    public Sound hitSound;       // Ударился о медузу
    public Sound victorySound;   // Выполнил квест

    public AudioManager() {
        // Пытаемся загрузить музыку, если файл существует
        try {
            if (Gdx.files.internal("sounds/background_music.mp3").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music.mp3"));
                backgroundMusic.setVolume(0.8f);
                backgroundMusic.setLooping(true);
                System.out.println("✅ Музыка загружена");
            } else {
                System.out.println("❌ Музыка не найдена");
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки музыки: " + e.getMessage());
        }

        // Загружаем звуки: сначала пробуем OGG, потом WAV
        loadSound("sounds/collect.ogg", "sounds/collect.wav");
        loadSound("sounds/hit.ogg", "sounds/hit.wav");
        loadSound("sounds/victory.ogg", "sounds/victory.wav");

        // Применяем сохранённые настройки
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
        } catch (Exception e) { /* пробуем WAV */ }

        try {
            if (Gdx.files.internal(wavPath).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(wavPath));
                assignSound(sound, wavPath);
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки звука " + wavPath);
        }
    }

    private void assignSound(Sound sound, String path) {
        if (path.contains("collect")) collectSound = sound;
        else if (path.contains("hit")) hitSound = sound;
        else if (path.contains("victory")) victorySound = sound;
        System.out.println("✅ Звук загружен: " + path);
    }

    public void updateSoundFlag() {
        isSoundOn = MemoryManager.loadIsSoundOn();
        System.out.println("Sound ON: " + isSoundOn);
    }

    public void updateMusicFlag() {
        isMusicOn = MemoryManager.loadIsMusicOn();
        System.out.println("Music ON: " + isMusicOn);

        if (backgroundMusic != null) {
            if (isMusicOn) {
                backgroundMusic.play();
            } else {
                backgroundMusic.stop();
            }
        }
    }

    // Воспроизведение с низкой громкостью, чтобы не раздражало
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
