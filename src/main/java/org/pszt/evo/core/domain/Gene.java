package org.pszt.evo.core.domain;


import org.pszt.evo.core.factories.CopyFactory;

public interface Gene<T, G extends Gene<T, G>> extends CopyFactory<G> {
    T getValue();
    G newInstance(final T value);
}
