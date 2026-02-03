package io.github.team3engine.interfaces;

/**
 * Objects that have a unique string id.
 * Enables generic lookup and duplicate checks without depending on a concrete type.
 */
public interface Identifiable {
    String getId();
}
