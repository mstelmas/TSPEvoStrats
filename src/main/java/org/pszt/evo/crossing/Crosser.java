package org.pszt.evo.crossing;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.pszt.evo.core.domain.Chromosome;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class Crosser<G extends Gene<?, G>, C extends Comparable<? super C>> {
    private final CrossOverStrategy<G> crossOverStrategy;
    private final double crossRate;

    public Population<G, C> cross(final Population<G, C> population, final long evolutionIteration) {
        final Population<G, C> newPopulation = population.copy();
        final List<Phenotype<G, C>> phenotypes = newPopulation.getPopulation();

        Collections.shuffle(phenotypes, ThreadLocalRandom.current());

        final Iterator<Phenotype<G, C>> iterator = phenotypes.iterator();
        while (iterator.hasNext()) {
            Phenotype<G, C> parent1 = iterator.next();

            if (iterator.hasNext()) {
                Phenotype<G, C> parent2 = iterator.next();
                crossIfRatio(parent1, parent2, evolutionIteration);
            }
        }

        return population.newInstance(phenotypes);
    }

    private void crossIfRatio(Phenotype<G, C> parent1, Phenotype<G, C> parent2, final long evolutionIteration) {
        if (Math.random() <= crossRate) {
            final Pair<Chromosome<G>, Chromosome<G>> childrenChromosomes = doubleCrossParentChromosomes(parent1.getChromosome(), parent2.getChromosome());
            parent1 = parent1.newInstance(childrenChromosomes.getLeft(), evolutionIteration);
            parent2 = parent2.newInstance(childrenChromosomes.getRight(), evolutionIteration);
        }
    }

    private Pair<Chromosome<G>, Chromosome<G>> doubleCrossParentChromosomes(final Chromosome<G> parentChromosome1, final Chromosome<G> parentChromosome2) {
        return Pair.of(
                crossOverStrategy.cross(parentChromosome1, parentChromosome2),
                crossOverStrategy.cross(parentChromosome2, parentChromosome1)
        );
    }
}
