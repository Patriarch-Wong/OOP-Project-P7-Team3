package io.github.team3engine.entity;

public abstract class Audio {
    protected String id;
    protected float volume;
    protected float duration;

    public abstract void play();
    public abstract void stop();
    public abstract void pause();
    public abstract void setVolume(float volume);
}