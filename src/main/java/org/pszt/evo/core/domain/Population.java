package org.pszt.evo.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pszt.evo.core.PopulationStrategy;
import org.pszt.evo.core.factories.CopyFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Population<T extends Gene<?, T>, C extends Comparable<? super C>> implements PopulationStrategy<T, C>, CopyFactory<Population<T, C>> {

    private final List<Phenotype<T, C>> population;

    public Population() {
        this.population = new ArrayList<>();
    }

    @Override
    public Phenotype<T, C> getFittest() {
        return population.stream().max(Comparator.comparing(Phenotype::getFitness)).get();
    }

    @Override
    public void add(@NonNull final Phenotype<T, C> phenotype) {
        this.population.add(phenotype);
    }

    @Override
    public Phenotype<T, C> getPhenotypeAt(final int index) {
        return population.get(index);
    }

    @Override
    public Stream<Phenotype<T, C>> stream() {
        return population.stream();
    }

    @Override
    public void sort() {
        sort(Comparator.naturalOrder());
    }

    @Override
    public void sort(final Comparator<C> comparator) {
        population.sort((o1, o2) -> comparator.compare(o1.getFitness(), o2.getFitness()));
    }

    @Override
    public int size() {
        return population.size();
    }

    @Override
    public Population<T, C> newInstance(final List<Phenotype<T, C>> phenotypes) {
        return new Population<>(phenotypes);
    }

    @Override
    public Population<T, C> copy() {
        return new Population<>(population);
    }
}
