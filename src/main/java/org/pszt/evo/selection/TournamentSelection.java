package org.pszt.evo.selection;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TournamentSelection <T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements SelectionStrategy<T, C> {

    @Override
    public Population<T, C> select(final Population<T, C> population) {
        return select(population, population.size());
    }

    @Override
    public Population<T, C> select(final Population<T, C> population, final int size) {
        final Population<T, C> copiedPopulation = population.copy();

        final List<Phenotype<T, C>> offspringPopulation = IntStream.range(0, size)
                .mapToObj(i -> tournamentPick(copiedPopulation))
                .collect(Collectors.toList());

        return population.newInstance(offspringPopulation);
    }

    private Phenotype<T, C> tournamentPick(final Population<T, C> population) {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        final Phenotype<T, C> phenotype1 = population.getPhenotypeAt(localRandom.nextInt(population.size()));
        final Phenotype<T, C> phenotype2 = population.getPhenotypeAt(localRandom.nextInt(population.size()));

        return phenotype1.getFitness().compareTo(phenotype2.getFitness()) >= 0 ? phenotype1 : phenotype2;
    }
}
