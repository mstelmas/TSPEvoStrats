package org.pszt.evo.crossing;


import org.pszt.evo.EvoUtils;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class OrderBasedCrossOver<G extends Gene<?, G>> implements CrossOverStrategy<G> {

    private EvoUtils evoUtils = new EvoUtils();

    @Override
    public Chromosome<G> cross(final Chromosome<G> chromosome1, final Chromosome<G> chromosome2) {
        final List<G> genes2 = new ArrayList<>(chromosome2.getGenes());

        final int numberOfCitiesToReplace = ThreadLocalRandom.current().nextInt(1, genes2.size());

        final List<G> selectedElements = evoUtils.generateUniqueIndices(0, chromosome1.length(), numberOfCitiesToReplace).stream()
                .sorted()
                .map(chromosome1::getGeneAt)
                .collect(Collectors.toList());

        final Iterator<G> selectedElementsIterator = selectedElements.iterator();
        for (int i = 0; i < genes2.size(); i++) {
            if (!selectedElementsIterator.hasNext()) {
                break;
            }

            if (selectedElements.contains(chromosome2.getGeneAt(i))) {
                genes2.set(i, selectedElementsIterator.next());
            }
        }
        return chromosome1.newInstance(genes2);
    }
}
