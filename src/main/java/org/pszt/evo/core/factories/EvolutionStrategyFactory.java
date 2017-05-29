package org.pszt.evo.core.factories;

import org.pszt.evo.core.EvolutionParams;
import org.pszt.evo.core.EvolutionStrategy;
import org.pszt.evo.core.EvolutionType;
import org.pszt.evo.core.GAStrategy;
import org.pszt.evo.core.domain.Gene;

public class EvolutionStrategyFactory {
    public static <T extends Gene<?, T>, C extends Number & Comparable<? super C>> EvolutionStrategy build(final EvolutionType evolutionType, final EvolutionParams<T, C> evolutionParams) {
        switch (evolutionType) {
            case GENETIC_ALGORITHM:
                return new GAStrategy<>(evolutionParams);
            case MI_PLUS_NU:
            case MI_WITH_NU:
            default:
                throw new IllegalStateException("Evolution type: " + evolutionType + " not supported");
        }
    }
}
