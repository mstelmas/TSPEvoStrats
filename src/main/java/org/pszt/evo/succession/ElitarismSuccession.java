package org.pszt.evo.succession;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ElitarismSuccession<T extends Gene<?, T>, C extends Number & Comparable<? super C>> implements SuccessionStrategy<T, C> {
    private final int k;

    @Override
    public Population<T, C> join(final Population<T, C> initialPopulation, final Population<T, C> offspringsPopulation) {

        final List<Phenotype<T, C>> kBestFromInitialPopulation = initialPopulation.stream()
                .sorted()
                .limit(k)
                .collect(Collectors.toList());

        final List<Phenotype<T, C>> offspringsWithoutKWorst = offspringsPopulation.stream()
                .sorted()
                .limit(initialPopulation.size() - k)
                .collect(Collectors.toList());


        kBestFromInitialPopulation.addAll(offspringsWithoutKWorst);

        return initialPopulation.newInstance(kBestFromInitialPopulation);
    }
}
