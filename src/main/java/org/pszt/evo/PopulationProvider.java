package org.pszt.evo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;
import org.pszt.graph.AbstractGraph;

@RequiredArgsConstructor
public abstract class PopulationProvider<G extends Gene<?, G>, C extends Comparable<? super C>> {
    protected final AbstractGraph graph;
    @Getter protected final int N;

    public abstract Population<G, C> provide(final int size);
}
