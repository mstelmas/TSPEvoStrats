package org.pszt.evo;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface PopulationProvider<G extends Gene<?, G>, C extends Comparable<? super C>> {
    Population<G, C> provide(final int size);
}
