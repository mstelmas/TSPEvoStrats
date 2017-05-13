package org.pszt.tsp.domain;

import lombok.NonNull;
import org.pszt.graph.Edge;

import java.util.List;
import java.util.stream.Collectors;

public class TspTourConverter {
    public List<City> convert(@NonNull final List<Edge> tspEdges) {
        return tspEdges.stream()
                .mapToInt(Edge::first)
                .mapToObj(City::new)
                .collect(Collectors.toList());
    }
}
