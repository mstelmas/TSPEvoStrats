package org.pszt.evo.selection;


import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface SelectionStrategy<G extends Gene<?, G>, C extends Number & Comparable<? super C>> {
    Population<G, C> select(final Population<G, C> population);
    Population<G, C> select(final Population<G, C> population, final int size);
}
