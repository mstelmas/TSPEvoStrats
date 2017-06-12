package org.pszt.evo.core;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

@RequiredArgsConstructor
public class MiWithLambdaStrategy<T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements EvolutionStrategy<T, C> {

    private final EvolutionParams<T, C> evolutionParams;

    @Override
    public Population<T, C> evolve(Population<T, C> population) {
        Integer lambda = evolutionParams.getLambda();
        final Population<T, C> offspringPopulation = evolutionParams.getSelector().select(population, lambda);
        final Population<T, C> crossedOffspringPopulation = evolutionParams.getCrosser().cross(offspringPopulation, 0);
        final Population<T, C> mutatedAndCrossedOffspringPopulation = evolutionParams.getMutator().mutate(crossedOffspringPopulation);

        return evolutionParams.getSelector().select(mutatedAndCrossedOffspringPopulation, population.size());
    }
}
