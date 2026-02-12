package io.github.team3engine.engine.interfaces;

/**
 * Abstraction for reading and setting volume levels (0.0–1.0).
 * Allows UI (e.g. UIManager) to control volume without depending on AudioManager.
 */
public interface VolumeControl {
    float getMasterVolume();
    float getMusicVolume();
    float getSFXVolume();
    void setMasterVolume(float volume);
    void setMusicVolume(float volume);
    void setSFXVolume(float volume);
}
