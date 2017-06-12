package org.pszt.gui.listeners;

import lombok.RequiredArgsConstructor;
import org.pszt.evo.EvoSolver;
import org.pszt.evo.core.listeners.InitializationListener;

import javax.swing.*;

@RequiredArgsConstructor
public class JTextAreaEvolutionInitializationListener implements InitializationListener {

    private final JTextArea jTextArea;

    @Override
    public void onInit(EvoSolver evoSolver) {
        jTextArea.setText("");
        jTextArea.setText(String.format("Solving using: %s with parameters: \n", evoSolver.getEvolutionStrategy().getClass().getCanonicalName()));
        jTextArea.append(String.format("Mutation strategy: %s, Crossover strategy: %s\n",
                evoSolver.getEvolutionParams().getMutator().getMutationStrategy().getClass().getSimpleName(),
                evoSolver.getEvolutionParams().getCrosser().getCrossOverStrategy().getClass().getSimpleName()));
        jTextArea.append(String.format("Graph size: %d, population size: %d, lambda: %d\n", evoSolver.getPopulationGenerator().getN(), evoSolver.getPopulationSize(),
                evoSolver.getEvolutionParams().getLambda()));
        jTextArea.append(String.format("For a total of %d iterations\n\n", evoSolver.getEvolutionIterations()));
    }
}
