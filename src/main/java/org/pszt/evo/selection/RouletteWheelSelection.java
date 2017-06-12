package org.pszt.evo.selection;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RouletteWheelSelection<T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements SelectionStrategy<T, C> {

    @Override
    public Population<T, C> select(final Population<T, C> population) {
        return select(population, population.size());
    }

    @Override
    public Population<T, C> select(final Population<T, C> population, final int size) {
        final Population<T, C> copiedPopulation = population.copy();
        final double cumulativeFitnessTable[] = buildCumulativeFitnessTable(population);

        final List<Phenotype<T, C>> offspringPopulation = IntStream.range(0, size)
                .mapToObj(i -> rouletteWheelPick(copiedPopulation, cumulativeFitnessTable))
                .collect(Collectors.toList());

        return population.newInstance(offspringPopulation);
    }

    private double[] buildCumulativeFitnessTable(final Population<T, C> population) {
        final double cumulativeFitnessTable[] = new double[population.size()];

        cumulativeFitnessTable[0] = population.getPhenotypeAt(0).getFitness().doubleValue();

        for (int i = 1; i < population.size(); i++) {
            cumulativeFitnessTable[i] = cumulativeFitnessTable[i - 1] + population.getPhenotypeAt(i).getFitness().doubleValue();
        }

        return cumulativeFitnessTable;
    }

    private Phenotype<T, C> rouletteWheelPick(final Population<T, C> population, final double[] cumulativeFitnessTable) {
        final double randomFitness = ThreadLocalRandom.current().nextDouble() * cumulativeFitnessTable[cumulativeFitnessTable.length - 1];

        int index = Arrays.binarySearch(cumulativeFitnessTable, randomFitness);

        if (index < 0) {
            index = Math.abs(index + 1);
        }

        return population.getPhenotypeAt(index);
    }
}
