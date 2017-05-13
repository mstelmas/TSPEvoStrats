package org.pszt.evo;

import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

public interface FitnessEvaluator<G extends Gene<?, G>> {
    double evaluate(final Chromosome<G> chromosome);
}
