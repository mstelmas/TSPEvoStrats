package org.pszt.evo;

import lombok.*;
import org.pszt.evo.core.EvolutionParams;
import org.pszt.evo.core.EvolutionStrategy;
import org.pszt.evo.core.EvolutionType;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;
import org.pszt.evo.core.factories.EvolutionStrategyFactory;
import org.pszt.evo.core.listeners.CompletionListener;
import org.pszt.evo.core.listeners.EvolutionListener;
import org.pszt.evo.core.listeners.InitializationListener;
import org.pszt.evo.crossing.Crosser;
import org.pszt.evo.crossing.ModifiedCrossOver;
import org.pszt.evo.mutation.Mutator;
import org.pszt.evo.mutation.SwapMutation;
import org.pszt.evo.selection.RouletteWheelSelection;
import org.pszt.evo.selection.Selector;
import org.pszt.evo.succession.GenerationSuccession;
import org.pszt.evo.succession.SuccessionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
public final class EvoSolver<T extends Gene<?, T>, C extends Number & Comparable<? super C>> {
    public static final double DEFAULT_MUTATION_RATE = 0.1;
    public static final double DEFAULT_CROSSING_RATE = 0.35;
    public static final int DEFAULT_EVOLUTION_ITERATIONS = 200;

    private int populationSize;
    private int evolutionIterations;

    private final EvolutionType evolutionType;
    private final EvolutionParams<T, C> evolutionParams;

    private PopulationProvider<T, C> populationGenerator;
    private final EvolutionStrategy<T, C> evolutionStrategy;

    private final List<EvolutionListener> evolutionListeners = new ArrayList<>();
    private final List<InitializationListener> initializationListeners = new ArrayList<>();
    private final List<CompletionListener> completionListeners = new ArrayList<>();

    public EvoSolver(final int populationSize, final int evolutionIterations, EvolutionType evolutionType, EvolutionParams<T, C> evolutionParams, PopulationProvider<T, C> populationGenerator, EvolutionStrategy<T, C> evolutionStrategy) {
        this.populationSize = populationSize;
        this.evolutionIterations = evolutionIterations;
        this.evolutionType = evolutionType;
        this.evolutionParams = evolutionParams;
        this.populationGenerator = populationGenerator;
        this.evolutionStrategy = evolutionStrategy;
    }

    public Phenotype<T, C> solve() {

        onInitialization.accept(this);

        Population<T, C> currentPopulation = populationGenerator.provide(populationSize);

        for (int i = 0; i < evolutionIterations; i++) {

            onBeforeEvolutionListeners.accept(i, currentPopulation);

            currentPopulation = evolutionStrategy.evolve(currentPopulation);

            onAfterEvolutionListeners.accept(i, currentPopulation);
        }

        onCompletion.accept(currentPopulation);

        return currentPopulation.getFittest();
    }

    private BiConsumer<Integer, Population<T, C>> onBeforeEvolutionListeners = (i, population) ->
            evolutionListeners.forEach(evolutionListener -> evolutionListener.onBeforeEvolution(i, population));

    private BiConsumer<Integer, Population<T, C>> onAfterEvolutionListeners = (i, population) ->
            evolutionListeners.forEach(evolutionListener -> evolutionListener.onAfterEvolution(i, population));

    private Consumer<EvoSolver> onInitialization = evoSolver ->
            initializationListeners.forEach(initializationListener -> initializationListener.onInit(evoSolver));

    private Consumer<Population<T, C>> onCompletion = population ->
            completionListeners.forEach(completionListener -> completionListener.onFinish(population));

    public void registerEvolutionListener(@NonNull final EvolutionListener evolutionListener) {
        evolutionListeners.add(evolutionListener);
    }

    public void registerInitializationListener(@NonNull final InitializationListener initializationListener) {
        initializationListeners.add(initializationListener);
    }

    public void registerCompletionListener(@NonNull final CompletionListener completionListener) {
        completionListeners.add(completionListener);
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
        private EvolutionType evolutionType = EvolutionType.GENETIC_ALGORITHM;
        private Integer mi = null;
        private Integer lambda = null;

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

        public builder<T, C> withEvolutionType(@NonNull final EvolutionType evolutionType) {
            this.evolutionType = evolutionType;
            return this;
        }

        public builder<T, C> withMi(@NonNull final Integer mi) {
            this.mi = mi;
            return this;
        }

        public builder<T, C> withLambda(@NonNull final Integer lambda) {
            this.lambda = lambda;
            return this;
        }

        public EvoSolver<T, C> build() {
            final EvolutionParams<T, C> evolutionParams = evolutionParamsSupplier.get();

            return new EvoSolver<>(
                    populationSize,
                    evolutionIterations,
                    evolutionType,
                    evolutionParams,
                    populationGenerator,
                    EvolutionStrategyFactory.build(evolutionType, evolutionParams)
            );
        }

        private Supplier<EvolutionParams<T, C>> evolutionParamsSupplier = () -> {
            final EvolutionParams<T, C> evolutionParams = new EvolutionParams<>();
            evolutionParams.setCrosser(crosser);
            evolutionParams.setMutator(mutator);
            evolutionParams.setSelector(selector);
            evolutionParams.setSuccessionStrategy(successionStrategy);
            evolutionParams.setMi(mi);
            evolutionParams.setLambda(lambda);
            return evolutionParams;
        };
    }
}
