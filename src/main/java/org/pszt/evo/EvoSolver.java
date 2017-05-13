package org.pszt.evo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;
import org.pszt.evo.crossing.Crosser;
import org.pszt.evo.crossing.ModifiedCrossOver;
import org.pszt.evo.mutation.Mutator;
import org.pszt.evo.mutation.SwapMutation;
import org.pszt.evo.selection.RouletteWheelSelection;
import org.pszt.evo.selection.Selector;
import org.pszt.evo.succession.GenerationSuccession;
import org.pszt.evo.succession.SuccessionStrategy;

@AllArgsConstructor
public final class EvoSolver<T extends Gene<?, T>, C extends Number & Comparable<? super C>> {
    public static final double DEFAULT_MUTATION_RATE = 0.1;
    public static final double DEFAULT_CROSSING_RATE = 0.35;
    public static final int DEFAULT_EVOLUTION_ITERATIONS = 200;

    private int populationSize;
    private int evolutionIterations;

    private final Mutator<T, C> mutator;
    private final Crosser<T, C> crosser;
    private final Selector<T, C> selector;
    private final SuccessionStrategy<T, C> successionStrategy;
    private final PopulationProvider<T, C> populationGenerator;


    public Phenotype<T, C> solve() {
        Population<T, C> currentPopulation = populationGenerator.provide(populationSize);

        for (int i = 0; i < evolutionIterations; i++) {
            currentPopulation = evolve(currentPopulation, i);
//            evoSolverResults.getEvolutionsAggregator().addEvolutionAtStage(new EvolutionStage<>(i, currentPopulation, evolvedPopulation));
        }

        return currentPopulation.getFittest();
    }

    private Population<T, C> evolve(final Population<T, C> population, final int evolutionIteration) {
        final Population<T, C> offspringPopulation = selector.select(population);
        final Population<T, C> crossedOffspringPopulation = crosser.cross(offspringPopulation, evolutionIteration);
        final Population<T, C> mutatedAndCrossedOffspringPopulation = mutator.mutate(crossedOffspringPopulation);

        return successionStrategy.join(population, mutatedAndCrossedOffspringPopulation);
    }

    public static <T extends Gene<?, T>, C extends Number & Comparable<? super C>> builder<T, C> builder() {
        return new builder<>();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class builder<T extends Gene<?, T>, C extends Number & Comparable<? super C>> {
        private int populationSize = 100;
        private int evolutionIterations = DEFAULT_EVOLUTION_ITERATIONS;
        private Mutator<T, C> mutator = new Mutator<T, C>(new SwapMutation<>(), DEFAULT_MUTATION_RATE);
        private Crosser<T, C> crosser = new Crosser<T, C>(new ModifiedCrossOver<>(), DEFAULT_CROSSING_RATE);
        private Selector<T, C> selector = new Selector<T, C>(new RouletteWheelSelection<>());
        private SuccessionStrategy<T, C> successionStrategy = new GenerationSuccession<>();
        private PopulationProvider<T, C> populationGenerator = null;

        public builder<T, C> withPopulationSize(final int populationSize) {
            if (populationSize <= 0) {
                throw new IllegalArgumentException("Population size must be greater than 0");
            }
            this.populationSize = populationSize;
            return this;
        }

        public builder<T, C> withEvolutionIterations(final int evolutionIterations) {
            if (evolutionIterations <= 0) {
                throw new IllegalArgumentException("Number of evolution iterations must be greater than 0");
            }
            this.evolutionIterations = evolutionIterations;
            return this;
        }

        public builder<T, C> withMutation(@NonNull final Mutator<T, C> mutator) {
            this.mutator = mutator;
            return this;
        }

        public builder<T, C> withCrossover(@NonNull final Crosser<T, C> crosser) {
            this.crosser = crosser;
            return this;
        }

        public builder<T, C> withSelection(@NonNull final Selector<T, C> selection) {
            this.selector = selection;
            return this;
        }

        public builder<T, C> withSuccessionStrategy(@NonNull final SuccessionStrategy<T, C> successionStrategy) {
            this.successionStrategy = successionStrategy;
            return this;
        }

        public builder<T, C> withPopulationGenerator(@NonNull final PopulationProvider<T, C> populationGenerator) {
            this.populationGenerator = populationGenerator;
            return this;
        }

        public EvoSolver<T, C> build() {
            validateConstraints();

            return new EvoSolver<>(
                    populationSize,
                    evolutionIterations,
                    mutator,
                    crosser,
                    selector,
                    successionStrategy,
                    populationGenerator
            );
        }

        private void validateConstraints() {
            if (populationGenerator == null) {
                throw new IllegalStateException("Initial population generator has not been provided!");
            }
        }
    }
}
