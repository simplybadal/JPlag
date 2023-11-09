package de.jplag.hooks;

/**
 * Base interface for all JPlag hook types
 */
@FunctionalInterface
public interface Hook<T> {
    void invoke(T value);

    default Class<? extends Hook<?>> hookType() {
        throw new UnsupportedOperationException();
    }
}
