package io.github.team3engine.engine.audio;

import com.badlogic.gdx.Gdx;
import io.github.team3engine.engine.interfaces.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the game's audio system.
 */
public class AudioManager implements Disposable {
    // Volume levels (0.0 to 1.0)
    private float masterVolume = 1.0f;
    private float musicVolume = 0.3f;
    private float sfxVolume = 1.0f;
    private boolean isMuted = false;

    private Map<String, Audio> soundLibrary = new HashMap<>();
    private Audio currentMusic;

    /**
     * Preload sounds by id. Which assets to load is decided by the game layer;
     * call this with the sound ids your game uses (e.g. from Main).
     */
    public void preload(String... ids) {
        for (String id : ids) {
            findAudio(id);
        }
    }

    // --- Getters
    public float getMasterVolume() { return this.masterVolume; }
    public float getMusicVolume() { return this.musicVolume; }
    public float getSFXVolume() { return this.sfxVolume; }

    // --- Setters ---
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

    // --- Core Logic ---
    public void play(String id) {
        Audio audio = findAudio(id);
        if (audio != null && !isMuted) {
            audio.setVolume(sfxVolume * masterVolume);
            audio.play();
        }
    }

    public void playMusic(String name, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        try {
            com.badlogic.gdx.audio.Music rawMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/" + name));
            MusicAudio musicWrap = new MusicAudio(name, rawMusic);
            musicWrap.setLooping(loop);
            currentMusic = musicWrap;
            updateMusicVolume();
            currentMusic.play();
        } catch (Exception e) {
            System.err.println("Error loading music: " + name);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
    }

    public void toggleMute() {
        this.isMuted = !isMuted;
        updateMusicVolume();
    }

    private void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(isMuted ? 0 : musicVolume * masterVolume);
        }
    }

    protected Audio findAudio(String name) {
        if (!soundLibrary.containsKey(name)) {
            try {
                com.badlogic.gdx.audio.Sound rawSound = Gdx.audio.newSound(Gdx.files.internal("audio/" + name));
                soundLibrary.put(name, new SfxAudio(name, rawSound));
            } catch (Exception e) {
                System.err.println("Could not load SFX: " + name);
                return null;
            }
        }
        return soundLibrary.get(name);
    }

    @Override
    public void dispose() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        soundLibrary.clear();
    }


    private static class SfxAudio extends Audio {
        private final com.badlogic.gdx.audio.Sound gdxSound;
        public SfxAudio(String id, com.badlogic.gdx.audio.Sound sound) {
            this.id = id;
            this.gdxSound = sound;
        }
        @Override public void play() { gdxSound.play(volume); }
        @Override public void stop() { gdxSound.stop(); }
        @Override public void pause() { gdxSound.pause(); }
        @Override public void setVolume(float v) { this.volume = v; }
    }

    private static class MusicAudio extends Audio {
        private final com.badlogic.gdx.audio.Music gdxMusic;
        public MusicAudio(String id, com.badlogic.gdx.audio.Music music) {
            this.id = id;
            this.gdxMusic = music;
        }
        public void setLooping(boolean loop) { gdxMusic.setLooping(loop); }
        @Override public void play() { gdxMusic.play(); }
        @Override public void stop() { gdxMusic.stop(); }
        @Override public void pause() { gdxMusic.pause(); }
        @Override public void setVolume(float v) { 
            this.volume = v; 
            gdxMusic.setVolume(v);
        }
    }
}