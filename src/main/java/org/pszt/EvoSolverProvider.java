package org.pszt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.pszt.evo.EvoSolver;
import org.pszt.evo.PopulationProvider;
import org.pszt.evo.core.EvolutionType;
import org.pszt.evo.crossing.Crosser;
import org.pszt.evo.crossing.ModifiedCrossOver;
import org.pszt.evo.mutation.Mutator;
import org.pszt.evo.mutation.SwapMutation;
import org.pszt.evo.selection.Selector;
import org.pszt.evo.selection.TournamentSelection;
import org.pszt.evo.succession.ElitarismSuccession;
import org.pszt.graph.AbstractGraph;
import org.pszt.tsp.domain.City;
import org.pszt.tsp.domain.RandomTspPopulationProvider;

import java.util.stream.Stream;

public class EvoSolverProvider {
    private final int DEFAULT_POPULATION_SIZE = 2000;
    private final int DEFAULT_LAMBDA_SIZE = 1000;
    private final int DEFAULT_ITERATION_NUMBER = 20;

    private final ImmutableMap<EvolutionType, EvoSolver> EVOLUTION_SOLVERS_MAP;

    private static EvoSolverProvider instance;

    private EvoSolverProvider() {
        EVOLUTION_SOLVERS_MAP = new ImmutableMap.Builder<EvolutionType, EvoSolver>()
//                .put(EvolutionType.GENETIC_ALGORITHM, buildGAEvoSolver())
                .put(EvolutionType.MI_PLUS_LAMBDA, buildMiPlusLambdaEvoSolver())
                .put(EvolutionType.MI_WITH_LAMBDA, buildMiWithLambdaEvoSolver())
                .build();
    }

    public static synchronized EvoSolverProvider instance() {
        if (instance == null) {
            instance = new EvoSolverProvider();
        }
        return instance;
    }

    @NonNull
    public EvoSolver<? extends City, ?> get(@NonNull EvolutionType evolutionType) {
        return EVOLUTION_SOLVERS_MAP.get(evolutionType);
    }

    public ImmutableSet<EvolutionType> list() {
        return EVOLUTION_SOLVERS_MAP.keySet();
    }

    public Stream<EvoSolver> solvers() {
        return EVOLUTION_SOLVERS_MAP.values().stream();
    }

    public void setGraph(@NonNull AbstractGraph graph) {
        solvers().forEach(evoSolver -> evoSolver.setPopulationGenerator(new RandomTspPopulationProvider(graph)));
    }

    public void setPopulationGenerator(@NonNull final PopulationProvider populationProvider) {
        solvers().forEach(evoSolver -> evoSolver.setPopulationGenerator(populationProvider));
    }

    private EvoSolver buildGAEvoSolver() {
        return EvoSolver.<City, Double>builder()
                .withPopulationSize(DEFAULT_POPULATION_SIZE)
                .withEvolutionIterations(DEFAULT_ITERATION_NUMBER)
                .withSuccessionStrategy(new ElitarismSuccession<>(2))
                .withSelection(new Selector<>(new TournamentSelection<City, Double>()))
                .withCrossover(new Crosser<>(new ModifiedCrossOver<City>(), 0.85))
                .withMutation(new Mutator<>(new SwapMutation<City>(), 0.10))
                .build();
    }

    private EvoSolver buildMiPlusLambdaEvoSolver() {
        return EvoSolver.<City, Double>builder()
                .withPopulationSize(DEFAULT_POPULATION_SIZE)
                .withEvolutionIterations(DEFAULT_ITERATION_NUMBER)
                .withSuccessionStrategy(new ElitarismSuccession<>(2))
                .withSelection(new Selector<>(new TournamentSelection<City, Double>()))
                .withCrossover(new Crosser<>(new ModifiedCrossOver<City>(), 1))
                .withMutation(new Mutator<>(new SwapMutation<City>(), 1))
                .withEvolutionType(EvolutionType.MI_PLUS_LAMBDA)
                .withLambda(DEFAULT_LAMBDA_SIZE)
                .build();
    }

    private EvoSolver buildMiWithLambdaEvoSolver() {
        return EvoSolver.<City, Double>builder()
                .withPopulationSize(DEFAULT_POPULATION_SIZE)
                .withEvolutionIterations(DEFAULT_ITERATION_NUMBER)
                .withSuccessionStrategy(new ElitarismSuccession<>(2))
                .withSelection(new Selector<>(new TournamentSelection<City, Double>()))
                .withCrossover(new Crosser<>(new ModifiedCrossOver<City>(), 1))
                .withMutation(new Mutator<>(new SwapMutation<City>(), 1))
                .withEvolutionType(EvolutionType.MI_WITH_LAMBDA)
                .withLambda(DEFAULT_LAMBDA_SIZE)
                .build();
    }
}
