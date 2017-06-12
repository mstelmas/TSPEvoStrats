package org.pszt.gui.listeners;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;
import org.pszt.evo.core.listeners.CompletionListener;
import org.pszt.tsp.TspEvoHelper;
import org.pszt.tsp.domain.City;

import javax.swing.*;

@RequiredArgsConstructor
public class JTextAreaEvolutionCompletionListener<T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements CompletionListener<City, C> {

    private final JTextArea jTextArea;
    private final TspEvoHelper tspEvoHelper;

    @Override
    public void onFinish(final Population<City, C> population) {
        final Phenotype<City, C> fittest = population.getFittest();
        jTextArea.append(String.format("\n\nFinished with best solution: (fitness: %s, TSP path length: %d)\n",
               fittest.getFitness(), tspEvoHelper.calculatePathLength(fittest.getChromosome())));
    }
}
