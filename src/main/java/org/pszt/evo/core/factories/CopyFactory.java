package org.pszt.evo.core.factories;

@FunctionalInterface
public interface CopyFactory<T> {
    T copy();
}
