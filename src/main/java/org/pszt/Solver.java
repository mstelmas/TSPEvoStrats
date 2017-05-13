package org.pszt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.pszt.evo.EvoSolver;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.crossing.Crosser;
import org.pszt.evo.crossing.ModifiedCrossOver;
import org.pszt.evo.mutation.Mutator;
import org.pszt.evo.mutation.SwapMutation;
import org.pszt.evo.selection.Selector;
import org.pszt.evo.selection.TournamentSelection;
import org.pszt.evo.succession.ElitarismSuccession;
import org.pszt.graph.AbstractGraph;
import org.pszt.graph.GraphImporter;
import org.pszt.tsp.TspEvoHelper;
import org.pszt.tsp.domain.City;
import org.pszt.tsp.domain.RandomTspPopulationProvider;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class Solver {
    public static final int PARALLEL_SOLVERS = 6;

    public static void main(String[] args) throws Exception {
        final AbstractGraph graph = GraphImporter.fromFullMatrix("examples/bays29.txt");
        final TspEvoHelper tspEvoHelper = TspEvoHelper.with(graph);

        final List<CompletableFuture<Phenotype<City, Double>>> solutionFutures = IntStream.range(0, PARALLEL_SOLVERS)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> solve(graph)))
                .collect(toList());

        final Pair<Phenotype<City, Double>, Long> optimalSolution = asProcessedSequence(solutionFutures)
                .thenApply(phenotypes ->
                        phenotypes.stream()
                        .map(solution -> Pair.of(solution, tspEvoHelper.calculatePathLength(solution.getChromosome())))
                        .sorted(Comparator.comparing(Pair::getValue))
                        .findFirst()
                        .get()
                ).get();

        log.info("Optimal TSP path has length: {}", optimalSolution.getRight());
        log.info("{}", optimalSolution.getKey());
    }

    private static<T> CompletableFuture<List<T>> asProcessedSequence(final List<CompletableFuture<T>> taskFutures) {
        return CompletableFuture.allOf(taskFutures.toArray(new CompletableFuture[taskFutures.size()]))
                .thenApply(v -> taskFutures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

    private static Phenotype<City, Double> solve(@NonNull final AbstractGraph graph) {
        return EvoSolver.<City, Double>builder()
                .withPopulationSize(2000)
                .withEvolutionIterations(600)
                .withSuccessionStrategy(new ElitarismSuccession<>(2))
                .withSelection(new Selector<>(new TournamentSelection<City, Double>()))
                .withCrossover(new Crosser<>(new ModifiedCrossOver<City>(), 0.85))
                .withMutation(new Mutator<>(new SwapMutation<City>(), 0.10))
                .withPopulationGenerator(new RandomTspPopulationProvider<>(graph))
                .build()
                .solve();
    }
}
