package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * AudioManager - Handles background music and sound effects
 */
public class AudioManager {
    private static AudioManager instance;

    private Music backgroundMusic;
    private float musicVolume = 0.5f; // Default volume 50%
    private float sfxVolume = 0.8f; // Default SFX volume 80%
    private boolean isMuted = false;

    // Sound Effects
    private Sound runningSound;
    private Sound stabSound;
    private Sound wingsSound;

    // SFX playback tracking
    private long runningSoundId = -1;
    private long wingsSoundId = -1;
    private boolean isRunningPlaying = false;
    private boolean isWingsPlaying = false;

    private AudioManager() {
        loadSoundEffects();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Load all sound effects
     */
    private void loadSoundEffects() {
        try {
            runningSound = Gdx.audio.newSound(Gdx.files.internal("running sfx 1.mp3"));
            stabSound = Gdx.audio.newSound(Gdx.files.internal("stab sfx 1.mp3"));
            wingsSound = Gdx.audio.newSound(Gdx.files.internal("wings sfx.mp3"));
            Gdx.app.log("AUDIO", "Sound effects loaded");
        } catch (Exception e) {
            Gdx.app.error("AUDIO", "Failed to load sound effects: " + e.getMessage());
        }
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

    // ==================== SOUND EFFECTS ====================

    /**
     * Set SFX volume (0.0 to 1.0)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Get current SFX volume
     */
    public float getSfxVolume() {
        return sfxVolume;
    }

    /**
     * Play running sound (looping)
     */
    public void playRunningSound() {
        if (isMuted || runningSound == null) return;
        if (!isRunningPlaying) {
            runningSoundId = runningSound.loop(sfxVolume * 0.5f); // Lower volume for running
            isRunningPlaying = true;
        }
    }

    /**
     * Stop running sound
     */
    public void stopRunningSound() {
        if (runningSound != null && isRunningPlaying) {
            runningSound.stop(runningSoundId);
            isRunningPlaying = false;
            runningSoundId = -1;
        }
    }

    /**
     * Check if running sound is playing
     */
    public boolean isRunningPlaying() {
        return isRunningPlaying;
    }

    /**
     * Play stab/hit sound (one shot)
     */
    public void playStabSound() {
        if (isMuted || stabSound == null) return;
        stabSound.play(sfxVolume);
    }

    /**
     * Play ghost wings sound (looping)
     */
    public void playWingsSound() {
        if (isMuted || wingsSound == null) return;
        if (!isWingsPlaying) {
            wingsSoundId = wingsSound.loop(sfxVolume * 0.4f); // Lower volume for wings
            isWingsPlaying = true;
        }
    }

    /**
     * Stop ghost wings sound
     */
    public void stopWingsSound() {
        if (wingsSound != null && isWingsPlaying) {
            wingsSound.stop(wingsSoundId);
            isWingsPlaying = false;
            wingsSoundId = -1;
        }
    }

    /**
     * Check if wings sound is playing
     */
    public boolean isWingsPlaying() {
        return isWingsPlaying;
    }

    /**
     * Stop all SFX
     */
    public void stopAllSfx() {
        stopRunningSound();
        stopWingsSound();
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
        if (runningSound != null) {
            runningSound.dispose();
            runningSound = null;
        }
        if (stabSound != null) {
            stabSound.dispose();
            stabSound = null;
        }
        if (wingsSound != null) {
            wingsSound.dispose();
            wingsSound = null;
        }
    }
}

