package io.github.team3engine.entity;

/**
 * Objects that have a unique string id.
 * Enables generic lookup (e.g. {@link EntityManager#getById}) and duplicate checks without depending on {@link Entity}.
 */
public interface Identifiable {
    String getId();
}
