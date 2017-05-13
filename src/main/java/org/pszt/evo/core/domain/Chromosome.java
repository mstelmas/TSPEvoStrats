package org.pszt.evo.core.domain;


import org.pszt.evo.core.factories.CopyFactory;

import java.util.List;
import java.util.stream.Stream;

public interface Chromosome<T extends Gene<?, T>> extends CopyFactory<Chromosome<T>>, Iterable<T> {
    int length();
    List<T> getGenes();
    T getGeneAt(final int index);
    Chromosome<T> newInstance(final List<T> genes);

    default Stream<T> stream() {
        return getGenes().stream();
    }
}
