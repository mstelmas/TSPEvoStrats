package org.pszt.evo.mutation;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Mutator<G extends Gene<?, G>, C extends Comparable<? super C>> {
    private final MutationStrategy<G> mutationStrategy;
    private final double mutationRate;

    public Population<G, C> mutate(final Population<G, C> population) {
        return new Population<>(
                population.stream()
                        .map(this::mutateIfRatio)
                        .collect(Collectors.toList())
        );
    }

    private Phenotype<G, C> mutateIfRatio(final Phenotype<G, C> phenotype) {
        if (Math.random() <= mutationRate) {
            final Chromosome<G> mutatedChromosome = mutationStrategy.mutate(phenotype.getChromosome());
            return phenotype.newInstance(mutatedChromosome);
        }
        return phenotype;
    }
}
