package org.pszt.evo.mutation;


import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

public interface MutationStrategy<G extends Gene<?, G>> {
    Chromosome<G> mutate(final Chromosome<G> solution);
}
