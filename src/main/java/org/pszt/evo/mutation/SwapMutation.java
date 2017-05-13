package org.pszt.evo.mutation;

import org.apache.commons.lang3.tuple.Pair;
import org.pszt.evo.EvoUtils;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwapMutation<G extends Gene<?, G>> implements MutationStrategy<G> {

    private EvoUtils evoUtils = new EvoUtils();

    @Override
    public Chromosome<G> mutate(final Chromosome<G> solution) {
        final List<G> genes = new ArrayList<>(solution.getGenes());

        final Pair<Integer, Integer> indicesToSwap = evoUtils.generatePairOfUniqueIndices(0, genes.size());

        Collections.swap(genes, indicesToSwap.getLeft(), indicesToSwap.getRight());

        return solution.newInstance(genes);
    }
}
