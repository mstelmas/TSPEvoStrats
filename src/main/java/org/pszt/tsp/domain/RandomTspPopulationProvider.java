package org.pszt.tsp.domain;

import lombok.NonNull;
import org.pszt.evo.PopulationProvider;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;
import org.pszt.graph.AbstractGraph;
import org.pszt.tsp.TspFitnessEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomTspPopulationProvider<T extends Gene<?, T>, C extends Comparable<? super C>> implements PopulationProvider<T, C> {
    private final Random random = new Random();

    private final AbstractGraph graph;
    private final TspFitnessEvaluator tspFitnessEvaluator;
    private final Function<Chromosome<City>, Double> fitnessFunction;

    public RandomTspPopulationProvider(@NonNull AbstractGraph graph) {
        this.graph = graph;
        this.tspFitnessEvaluator = new TspFitnessEvaluator(graph);
        this.fitnessFunction = tspFitnessEvaluator::evaluate;
    }

    @Override
    public Population<T, C> provide(final int size) {
        final List<City> initialTspTour = IntStream.range(0, graph.getV())
                .mapToObj(City::new)
                .collect(Collectors.toList());

        final Population<T, C> gPopulation = new Population<>();

        IntStream.rangeClosed(0, size).forEach(value -> {
            Collections.shuffle(initialTspTour, random);
            gPopulation.add(new Phenotype(fitnessFunction, Tour.of(new ArrayList<>(initialTspTour)), 0));
        });

        return gPopulation;
    }
}
