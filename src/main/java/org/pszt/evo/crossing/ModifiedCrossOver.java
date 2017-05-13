package org.pszt.evo.crossing;


import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModifiedCrossOver<G extends Gene<?, G>> implements CrossOverStrategy<G> {

    @Override
    public Chromosome<G> cross(final Chromosome<G> chromosome1, final Chromosome<G> chromosome2) {
        final int cutpoint = ThreadLocalRandom.current().nextInt(chromosome1.length() - 1);

        final List<G> collect = Stream.concat(
                chromosome1.stream().limit(cutpoint + 1),
                chromosome2.stream()
        ).distinct().collect(Collectors.toList());

        return chromosome1.newInstance(collect);
    }
}
