package org.pszt.tsp;

import lombok.NonNull;
import org.pszt.evo.FitnessEvaluator;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.graph.AbstractGraph;
import org.pszt.tsp.domain.City;

public class TspFitnessEvaluator implements FitnessEvaluator<City> {
    private final TspEvoHelper tspEvoHelper;

    public TspFitnessEvaluator(@NonNull final AbstractGraph graph) {
        this.tspEvoHelper = TspEvoHelper.with(graph);
    }

    @Override
    public double evaluate(final Chromosome<City> chromosome) {
        return 1.0 / tspEvoHelper.calculatePathLength(chromosome);
    }
}
