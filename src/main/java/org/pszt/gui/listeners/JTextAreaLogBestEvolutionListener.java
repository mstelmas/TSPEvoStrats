package org.pszt.gui.listeners;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;
import org.pszt.evo.core.listeners.EvolutionListener;
import org.pszt.tsp.TspEvoHelper;
import org.pszt.tsp.domain.City;

import javax.swing.*;

@RequiredArgsConstructor
public class JTextAreaLogBestEvolutionListener<T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements EvolutionListener<City, C> {

    private final JTextArea jTextArea;
    private final TspEvoHelper tspEvoHelper;

    @Override
    public void onBeforeEvolution(int iteration, Population<City, C> population) {

    }

    @Override
    public void onAfterEvolution(int iteration, Population<City, C> population) {
        final Phenotype<City, C> fittest = population.getFittest();

        jTextArea.append(String.format("Iteration %d, best solution: (fitness: %s, TSP path length: %d)\n",
                iteration + 1, fittest.getFitness(), tspEvoHelper.calculatePathLength(fittest.getChromosome())));
    }
}
