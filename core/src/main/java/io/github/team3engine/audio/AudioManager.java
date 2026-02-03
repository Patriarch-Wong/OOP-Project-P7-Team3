package io.github.team3engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    // Volume levels (0.0 to 1.0)
    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private boolean isMuted = false;

    // Library for SFX and reference for current background music
    private Map<String, Sound> soundLibrary = new HashMap<>();
    private Music currentMusic;

    // + play(id: String): void
    public void play(String id) {
        Sound sound = findClip(id);
        if (sound != null && !isMuted) {
            // Plays sound at the specific SFX volume level
            sound.play(sfxVolume * masterVolume);
        }
    }

    // + playMusic(name: String, loop: boolean): void
    public void playMusic(String name, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        try {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/" + name));
            currentMusic.setLooping(loop);
            currentMusic.setVolume(isMuted ? 0 : musicVolume * masterVolume);
            currentMusic.play();
        } catch (Exception e) {
            System.err.println("Error loading music: " + name);
        }
    }

    // + stopMusic(): void
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    // Volume Setters
    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
        updateMusicVolume();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        updateMusicVolume();
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = volume;
    }

    // + toggleMute(): void
    public void toggleMute() {
        this.isMuted = !isMuted;
        updateMusicVolume();
    }

    private void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(isMuted ? 0 : musicVolume * masterVolume);
        }
    }

    // # findClip(name: String): Sound
    public Sound findClip(String name) {
        if (!soundLibrary.containsKey(name)) {
            try {
                Sound newSound = Gdx.audio.newSound(Gdx.files.internal("audio/" + name));
                soundLibrary.put(name, newSound);
            } catch (Exception e) {
                System.err.println("Could not load SFX: " + name + ". Check file format!");
                return null;
            }
        }
        return soundLibrary.get(name);
    }
}
