package org.pszt.evo.core.listeners;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface EvolutionListener<T extends Gene<?, T>, C extends Comparable<? super C>> {
    void onBeforeEvolution(final int iteration, final Population<T, C> population);
    void onAfterEvolution(final int iteration, final Population<T, C> population);
}
