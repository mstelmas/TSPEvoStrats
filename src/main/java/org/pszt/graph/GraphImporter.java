package org.pszt.graph;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphImporter {
    public static WeightedUndirectedGraph fromFullMatrix(final String path) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get(path));

        final WeightedUndirectedGraph weightedUndirectedGraph = new WeightedUndirectedGraph(lines.size());

        for (int i = 0; i < lines.size(); i++) {
            final List<Integer> distances = numericalRow(lines.get(i));

            for (int j = i + 1; j < lines.size(); j++) {
                weightedUndirectedGraph.addEdge(new Edge(i, j, distances.get(j)));
            }
        }

        return weightedUndirectedGraph;
    }

    private static List<Integer> numericalRow(final String row) {
        return Arrays.stream(row.trim().replaceAll("\\s+", " ").split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
