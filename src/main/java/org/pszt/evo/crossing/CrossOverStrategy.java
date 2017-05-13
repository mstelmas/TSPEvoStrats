package org.pszt.evo.crossing;


import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

public interface CrossOverStrategy<G extends Gene<?, G>> {
    Chromosome<G> cross(final Chromosome<G> chromosome1, final Chromosome<G> chromosome2);
}
