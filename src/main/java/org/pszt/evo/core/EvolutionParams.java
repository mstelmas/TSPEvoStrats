package org.pszt.evo.core;

import lombok.Data;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.crossing.Crosser;
import org.pszt.evo.mutation.Mutator;
import org.pszt.evo.selection.Selector;
import org.pszt.evo.succession.SuccessionStrategy;

@Data
public class EvolutionParams<T extends Gene<?, T>, C extends Number & Comparable<? super C>> {
    private Integer nu;
    private Integer mi;
    private Mutator<T, C> mutator;
    private Crosser<T, C> crosser;
    private Selector<T, C> selector;
    private SuccessionStrategy<T, C> successionStrategy;
}
