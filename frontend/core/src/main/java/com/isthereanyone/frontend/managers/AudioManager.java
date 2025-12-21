package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * AudioManager - Handles background music and sound effects
 */
public class AudioManager {
    private static AudioManager instance;

    private Music backgroundMusic;
    private float musicVolume = 0.5f; // Default volume 50%
    private boolean isMuted = false;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Initialize and play background music
     */
    public void playBackgroundMusic() {
        if (backgroundMusic == null) {
            try {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backsound.mp3"));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(musicVolume);
                backgroundMusic.play();
                Gdx.app.log("AUDIO", "Background music started");
            } catch (Exception e) {
                Gdx.app.error("AUDIO", "Failed to load background music: " + e.getMessage());
            }
        } else if (!backgroundMusic.isPlaying() && !isMuted) {
            backgroundMusic.play();
        }
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && !isMuted) {
            backgroundMusic.play();
        }
    }

    /**
     * Set music volume (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(musicVolume);
        }
    }

    /**
     * Get current music volume
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Toggle mute
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (backgroundMusic != null) {
            if (isMuted) {
                backgroundMusic.pause();
            } else {
                backgroundMusic.play();
            }
        }
    }

    /**
     * Check if muted
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Dispose resources
     */
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
}

