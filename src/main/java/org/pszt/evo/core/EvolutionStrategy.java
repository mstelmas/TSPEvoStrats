package org.pszt.evo.core;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface EvolutionStrategy<T extends Gene<?, T>, C extends Number & Comparable<? super C>> {
    Population<T, C> evolve(Population<T, C> population);
}
