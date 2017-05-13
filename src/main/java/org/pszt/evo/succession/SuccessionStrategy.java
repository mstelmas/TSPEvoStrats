package org.pszt.evo.succession;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface SuccessionStrategy<G extends Gene<?, G>, C extends Number & Comparable<? super C>> {
    Population<G, C> join(final Population<G, C> initialPopulation, final Population<G, C> offspringsPopulation);
}
