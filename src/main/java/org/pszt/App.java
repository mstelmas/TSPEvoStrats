package org.pszt;

import com.google.common.collect.ImmutableList;
import com.opencsv.CSVWriter;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.pszt.evo.EvoSolver;
import org.pszt.evo.core.EvolutionType;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.listeners.CsvWriterEvolutionListner;
import org.pszt.evo.core.listeners.LogBestEvolutionListener;
import org.pszt.evo.crossing.CrossOverStrategy;
import org.pszt.evo.crossing.ModifiedCrossOver;
import org.pszt.evo.crossing.OrderBasedCrossOver;
import org.pszt.evo.crossing.OrderCrossOver;
import org.pszt.evo.mutation.InversionMutation;
import org.pszt.evo.mutation.MutationStrategy;
import org.pszt.evo.mutation.SwapMutation;
import org.pszt.graph.AbstractGraph;
import org.pszt.graph.GraphImporter;
import org.pszt.gui.listeners.JTextAreaEvolutionCompletionListener;
import org.pszt.gui.listeners.JTextAreaEvolutionInitializationListener;
import org.pszt.gui.listeners.JTextAreaLogBestEvolutionListener;
import org.pszt.tsp.TspEvoHelper;
import org.pszt.tsp.domain.City;
import org.pszt.tsp.domain.RandomTspPopulationProvider;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Slf4j
public class App extends JFrame {

    private final static String APP_TITLE = "TSP Evolutionary Solver GUI";
    private final static String PREFERRED_LOOK_AND_FEEL = "Nimbus";
    private final static String DEFAULT_LOG_FILE_NAME = "results.csv";

    private final JPanel framePanel = new JPanel(new MigLayout("fill"));

    private final JButton solveButton = new JButton("Solve");
    private final JLabel bestScoreLabel = new JLabel("Best score: ");
    private final JTextField bestScoreTextField = new JTextField(10);
    private final JLabel bestTspSolutionLengthLabel = new JLabel("Best TSP length: ");
    private final JTextField bestTspSolutionLengthValue = new JTextField(10);
    private final JLabel solverLogLabel = new JLabel("Solver log: ");
    private final JTextArea bestTspSolutionLogTextArea = new JTextArea(4, 35);
    private final JScrollPane bestTspSolutionLogTextScrollPane = new JScrollPane(bestTspSolutionLogTextArea);
    private final JTextArea bestTspSolutionTextArea = new JTextArea(4, 35);
    private final JScrollPane bestTspSolutionTextScrollPane = new JScrollPane(bestTspSolutionTextArea);

    private final TitledBorder evolutionParametersPanelBorder = BorderFactory.createTitledBorder("Evolution parameters");
    private final JPanel evolutionParametersPanel = new JPanel(new MigLayout());
    private final JLabel evolutionIterationsLabel = new JLabel("# iterations: ");
    private final JTextField evolutionIterationsTextField = new JTextField(10);
    private final JLabel populationSizeLabel = new JLabel("Population size (mi): ");
    private final JTextField populationSizeTextField = new JTextField(10);
    private final JLabel lambdaSizeLabel = new JLabel("Offspring population size (lambda): ");
    private final JTextField lambdaSizeTextField = new JTextField(10);

    private final JPanel logFilePanel = new JPanel(new MigLayout());
    private final JLabel logFileNameLabel = new JLabel("Log to: ");
    private final JTextField logFileNameTextField = new JTextField(15);
    private final JButton logFileDirectorySelectorButton = new JButton("...");
    private final JFileChooser logFileDirectoryChooser = new JFileChooser();

    private final JPanel graphInfoPanel = new JPanel(new MigLayout());
    private final JLabel graphVerticesFromLabel = new JLabel("From ");

    private final JLabel evolutionTypeLabel = new JLabel("Algorithm: ");
    private final JComboBox<EvolutionType> evolutionTypeJComboBox = new JComboBox(
            new DefaultComboBoxModel<>(EvoSolverProvider.instance().list().toArray())
    );

    private final ImmutableList<Integer> AVAILABLE_GRAPH_SIZES = ImmutableList.of(10, 20, 30);

    private final JComboBox<EvolutionType> tspTourSizesJComboBox = new JComboBox(
            new DefaultComboBoxModel<>(AVAILABLE_GRAPH_SIZES.toArray())
    );

    private final ImmutableList<MutationStrategy<?>> MUTATION_STRATEGIES =
            ImmutableList.of(new InversionMutation<>(), new SwapMutation<>());

    private final JLabel mutationStrategyLabel = new JLabel("Mutation strategy: ");
    private final JComboBox<MutationStrategy<?>> mutationStrategyJComboBox = new JComboBox(
            new DefaultComboBoxModel<>(MUTATION_STRATEGIES.toArray())
    );

    private final ImmutableList<CrossOverStrategy<?>> CROSSOVER_STRATEGIES =
            ImmutableList.of(new ModifiedCrossOver<>(), new OrderBasedCrossOver<>(), new OrderCrossOver<>());

    private final JLabel crossoverStrategyLabel = new JLabel("Crossover strategy: ");
    private final JComboBox<CrossOverStrategy<?>> crossoverStrategyJComboBox = new JComboBox(
            new DefaultComboBoxModel<>(CROSSOVER_STRATEGIES.toArray())
    );

    private AbstractGraph baseGraph = null;
    private TspEvoHelper tspEvoHelper;
    private Map<City, String> TSP_CITY_MAPPINGS;

    private final CsvWriterEvolutionListner csvWriterEvolutionListner = new CsvWriterEvolutionListner();


    private App() {

        baseGraph = Try.of(() -> GraphImporter.fromFullMatrix("examples/cities_distance.txt"))
                .onFailure((ex) -> {
                            JOptionPane.showMessageDialog(this, "Could not load TSP cities. Exiting");
                            System.exit(1);
                        }).get();

        tspEvoHelper = TspEvoHelper.with(baseGraph);
        TSP_CITY_MAPPINGS = Try.of(() -> tspEvoHelper.loadCityStringMappings("examples/cidties_name.txt"))
                .onFailure((ex) -> JOptionPane.showMessageDialog(this, "Could not load TSP city names. Oh well..."))
                .getOrElseGet(throwable -> new HashMap<>());

        setUpLookAndFeel().onFailure((e) -> log.warn("Could not load system default theme! Oh well..."));

        buildGui();

        logFileDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        logFileDirectoryChooser.setAcceptAllFileFilterUsed(false);


        initializeSolvers();

        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationByPlatform(true);
        this.pack();
        this.setVisible(true);
    }

    private Supplier<EvoSolver<? extends City, ?>> currentSolver = () ->
            EvoSolverProvider.instance().get((EvolutionType) evolutionTypeJComboBox.getSelectedItem());

    public static void main(final String[] args) throws IOException {
        SwingUtilities.invokeLater(App::new);
    }

    private Try<Void> setUpLookAndFeel() {
        return loadLookAndFeelTheme(PREFERRED_LOOK_AND_FEEL)
                .onFailure((e) -> log.warn(String.format("Could not load theme: %s, loading system default theme...", PREFERRED_LOOK_AND_FEEL)))
                .orElse(this::loadSystemLookAndFeel);
    }

    private Try<Void> loadLookAndFeelTheme(@NonNull final String lookAndFeelTheme) {
        return Try.run(() -> UIManager.setLookAndFeel(
                Stream.of(UIManager.getInstalledLookAndFeels())
                        .filter(lookAndFeelInfo -> lookAndFeelInfo.getName().equals(lookAndFeelTheme))
                        .findAny()
                        .map(UIManager.LookAndFeelInfo::getClassName)
                        .orElseThrow(Exception::new)
        ));
    }

    private Try<Void> loadSystemLookAndFeel() {
        return Try.run(() -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
    }

    private void initializeSolvers() {
        EvoSolverProvider.instance().setGraph(baseGraph);
        EvoSolverProvider.instance().solvers().forEach(this::registerListeners);
    }

    private void registerListeners(final EvoSolver evoSolver) {
        evoSolver.registerEvolutionListener(new LogBestEvolutionListener());
        evoSolver.registerInitializationListener(new JTextAreaEvolutionInitializationListener(bestTspSolutionLogTextArea));
        evoSolver.registerEvolutionListener(new JTextAreaLogBestEvolutionListener(bestTspSolutionLogTextArea, tspEvoHelper));
        evoSolver.registerCompletionListener(new JTextAreaEvolutionCompletionListener(bestTspSolutionLogTextArea, tspEvoHelper));
//        evoSolver.registerEvolutionListener(csvWriterEvolutionListner);
    }

    private void buildGui() {

        final JPanel leftPanel = buildLeftPanel();
        final JPanel rightPanel = buildRightPanel();

        framePanel.add(leftPanel, "dock west");
        framePanel.add(rightPanel, "dock center");

        this.getContentPane().add(framePanel);
    }

    private JPanel buildLeftPanel() {
        final JPanel leftPanel = new JPanel();

        leftPanel.setLayout(new MigLayout());

        graphVerticesFromLabel.setText(graphVerticesFromLabel.getText() + baseGraph.getV() + " cities construct TSP paths of length: ");
        graphInfoPanel.add(graphVerticesFromLabel);
        graphInfoPanel.add(tspTourSizesJComboBox);

        solveButton.addActionListener(solveButtonActionListener);
        graphInfoPanel.add(solveButton, "split");

        leftPanel.add(graphInfoPanel, "wrap, pushx, grow");

        evolutionParametersPanel.setBorder(evolutionParametersPanelBorder);

        evolutionTypeLabel.setLabelFor(evolutionTypeJComboBox);
        evolutionTypeJComboBox.addActionListener(evolutionTypeComboBoxListener);
        evolutionParametersPanel.add(evolutionTypeLabel, "left, sg 1, split");
        evolutionParametersPanel.add(evolutionTypeJComboBox, "wrap");

        populationSizeLabel.setLabelFor(populationSizeTextField);
        evolutionParametersPanel.add(populationSizeLabel, "left, sg 1, split");
        populationSizeTextField.setText(String.valueOf(currentSolver.get().getPopulationSize()));
        evolutionParametersPanel.add(populationSizeTextField, "wrap");
        evolutionIterationsTextField.setText(String.valueOf(currentSolver.get().getEvolutionIterations()));

        lambdaSizeLabel.setLabelFor(lambdaSizeTextField);
        evolutionParametersPanel.add(lambdaSizeLabel, "left, sg 1, split");
        lambdaSizeTextField.setText(String.valueOf(currentSolver.get().getEvolutionParams().getLambda()));
        evolutionParametersPanel.add(lambdaSizeTextField, "wrap");

        evolutionIterationsLabel.setLabelFor(evolutionIterationsTextField);
        evolutionParametersPanel.add(evolutionIterationsLabel, "left, sg 1, split");
        evolutionParametersPanel.add(evolutionIterationsTextField, "wrap");

        mutationStrategyJComboBox.setRenderer(mutationStrategiesJComboBoxRenderer);
        mutationStrategyLabel.setLabelFor(mutationStrategyJComboBox);
        evolutionParametersPanel.add(mutationStrategyLabel, "left, sg 1, split");
        evolutionParametersPanel.add(mutationStrategyJComboBox, "wrap");

        crossoverStrategyJComboBox.setRenderer(crossoverStrategiesJComboBoxRenderer);
        crossoverStrategyLabel.setLabelFor(crossoverStrategyJComboBox);
        evolutionParametersPanel.add(crossoverStrategyLabel, "left, sg 1, split");
        evolutionParametersPanel.add(crossoverStrategyJComboBox, "wrap");

        leftPanel.add(evolutionParametersPanel, "pushx, growx, wrap");

        logFilePanel.add(logFileNameLabel);
        logFilePanel.add(logFileNameTextField, "pushx, growx");

        logFileDirectorySelectorButton.addActionListener(logFileDirectorySelectorButtonActionListener);
        logFilePanel.add(logFileDirectorySelectorButton);

        leftPanel.add(logFilePanel, "pushx, growx, wrap");

        return leftPanel;
    }

    private JPanel buildRightPanel() {
        final JPanel rightPanel = new JPanel();

        rightPanel.setLayout(new MigLayout());

        bestScoreTextField.setEditable(false);
        bestTspSolutionLengthValue.setEditable(false);

        rightPanel.add(bestScoreLabel, "left, sg 1, split");
        rightPanel.add(bestScoreTextField, "pushx, growx");
        rightPanel.add(bestTspSolutionLengthLabel);
        rightPanel.add(bestTspSolutionLengthValue, "pushx, growx, wrap");

        bestTspSolutionTextArea.setLineWrap(true);
        rightPanel.add(bestTspSolutionTextScrollPane, "span2, wrap, pushx, growx");

        solverLogLabel.setLabelFor(bestTspSolutionLogTextArea);
        bestTspSolutionLogTextArea.setLineWrap(true);
        rightPanel.add(solverLogLabel, "wrap");
        rightPanel.add(bestTspSolutionLogTextScrollPane, "span2, wrap, push, grow");

        return rightPanel;
    }

    private Consumer<Phenotype<? extends City, ?>> onSolutionCalculated = (solution) -> {
        this.bestScoreTextField.setText(solution.getFitness().toString());
        this.bestTspSolutionLengthValue.setText(Long.toString(tspEvoHelper.calculatePathLength(solution.getChromosome())));

        this.bestTspSolutionTextArea.append(
                solution.getChromosome().stream()
                    .map(city -> {
                        if (TSP_CITY_MAPPINGS.containsKey(city)) return TSP_CITY_MAPPINGS.get(city);
                        else return city.toString();
                    })
                    .collect(Collectors.joining(", "))
        );
    };

    private ActionListener solveButtonActionListener = actionEvent -> {

        Try.of(() -> new CSVWriter(new FileWriter(logFileNameTextField.getText(), true), ','))
                .onSuccess(csvWriterEvolutionListner::setCsvWriter);

        final Integer selectedTspGraphSize = (Integer) tspTourSizesJComboBox.getSelectedItem();
        EvoSolverProvider.instance().setPopulationGenerator(new RandomTspPopulationProvider(baseGraph, selectedTspGraphSize));

        final EvoSolver<? extends City, ?> evoSolver = currentSolver.get();

        final MutationStrategy selectedMutationStrategy = (MutationStrategy<?>) mutationStrategyJComboBox.getSelectedItem();
        evoSolver.getEvolutionParams().getMutator().setMutationStrategy(selectedMutationStrategy);

        final CrossOverStrategy selectedCrossoverStrategy = (CrossOverStrategy<?>) crossoverStrategyJComboBox.getSelectedItem();
        evoSolver.getEvolutionParams().getCrosser().setCrossOverStrategy(selectedCrossoverStrategy);

        Try.of(() -> Integer.valueOf(populationSizeTextField.getText())).onSuccess(evoSolver::setPopulationSize);
        Try.of(() -> Integer.valueOf(evolutionIterationsTextField.getText())).onSuccess(evoSolver::setEvolutionIterations);
        Try.of(() -> Integer.valueOf(lambdaSizeTextField.getText())).onSuccess(integer -> evoSolver.getEvolutionParams().setLambda(integer));

        this.bestTspSolutionTextArea.setText("");

        CompletableFuture.supplyAsync(() -> evoSolver.solve()).thenAccept(onSolutionCalculated);
    };

    private ActionListener logFileDirectorySelectorButtonActionListener = actionEvent -> {
        final int logFileDirectoryChooserReturnValue = logFileDirectoryChooser.showOpenDialog(this);

        if (logFileDirectoryChooserReturnValue == JFileChooser.APPROVE_OPTION) {
            logFileNameTextField.setText(Optional.ofNullable(logFileDirectoryChooser.getSelectedFile())
                    .map(file -> file.getAbsolutePath().concat("/").concat(DEFAULT_LOG_FILE_NAME)).orElse(""));
        }
    };

    private final ListCellRenderer mutationStrategiesJComboBoxRenderer = new DefaultListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                      final int index, final boolean isSelected,
                                                      final boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            API.Match(value).of(
                    Case($(instanceOf(MutationStrategy.class)), mutationStrategy -> run(() -> setText(mutationStrategy.getClass().getSimpleName()))));

            return this;
        }
    };

    private final ListCellRenderer crossoverStrategiesJComboBoxRenderer = new DefaultListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                      final int index, final boolean isSelected,
                                                      final boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            API.Match(value).of(
                    Case($(instanceOf(CrossOverStrategy.class)), crossOverStrategy -> run(() -> setText(crossOverStrategy.getClass().getSimpleName()))));

            return this;
        }
    };

    private Consumer<EvolutionType> loadConfigurationForEvolutionType = evolutionType -> {
        final EvoSolver<? extends City, ?> evoSolver = EvoSolverProvider.instance().get(evolutionType);

        this.populationSizeTextField.setText(String.valueOf(evoSolver.getPopulationSize()));
        this.lambdaSizeTextField.setText(String.valueOf(evoSolver.getEvolutionParams().getLambda()));

    };

    private final ActionListener evolutionTypeComboBoxListener = (actionEvent) ->
            Match(actionEvent.getSource()).of(
                    Case($(instanceOf(JComboBox.class)), comboBoxSource -> API.Match(comboBoxSource.getSelectedItem()).of(
                            Case($(instanceOf(EvolutionType.class)), evolutionType -> run(() -> loadConfigurationForEvolutionType.accept(evolutionType)))
                    ))
            );
}
