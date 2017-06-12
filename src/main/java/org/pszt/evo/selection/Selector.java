package org.pszt.evo.selection;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

@RequiredArgsConstructor
public class Selector<G extends Gene<?, G>, C extends Number & Comparable<? super C>> {
    private final SelectionStrategy<G, C> selectionStrategy;

    public Population<G, C> select(final Population<G, C> population) {
        return selectionStrategy.select(population);
    }

    public Population<G, C> select(final Population<G, C> population, final int size) {
        return selectionStrategy.select(population, size);
    }
}
