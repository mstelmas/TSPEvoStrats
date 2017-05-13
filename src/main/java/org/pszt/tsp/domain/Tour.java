package org.pszt.tsp.domain;

import lombok.NonNull;
import lombok.ToString;
import org.pszt.evo.core.domain.AbstractChromosome;
import org.pszt.evo.core.domain.Chromosome;

import java.util.List;

@ToString(callSuper = true)
public class Tour extends AbstractChromosome<City> {

    private Tour(@NonNull final List<City> cities) {
        super(cities);
    }

    public static Tour of(@NonNull final List<City> tspTour) {
        return new Tour(tspTour);
    }

    @Override
    public Chromosome<City> copy() {
        return newInstance(genes);
    }

    @Override
    public Chromosome<City> newInstance(List<City> genes) {
        return new Tour(genes);
    }
}
