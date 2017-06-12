package org.pszt.evo.core.listeners;

import lombok.extern.slf4j.Slf4j;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

@Slf4j
public class LogBestEvolutionListener<T extends Gene<?, T>, C extends Comparable<? super C>> implements EvolutionListener<T, C> {

    @Override
    public void onBeforeEvolution(int iteration, Population<T, C> population) {

    }

    @Override
    public void onAfterEvolution(int iteration, Population<T, C> population) {
        log.info("Iteration {}, best: {}", iteration, population.getFittest().getFitness());
    }
}
