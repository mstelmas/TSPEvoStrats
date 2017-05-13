package org.pszt.graph;

import lombok.Getter;

public class Edge implements Comparable<Edge> {
    private final int v;
    private final int w;
    @Getter private final int weight;

    public Edge(final int v, final int w) {
        this(v, w, 0);
    }

    public Edge(final int v, final int w, final int weight) {
        validateVertexNonNegativity(v);
        validateVertexNonNegativity(w);
        validateEdgeWeight(weight);

        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    public int either() {
        return v;
    }

    public int other(final int vertex) {
        if (vertex == v) {
            return w;
        } else if (vertex == w) {
            return v;
        } else {
            throw new IllegalArgumentException("Edge does not contain vertex with id: " + vertex);
        }
    }

    public int first() {
        return either();
    }

    public int second() {
        return other(either());
    }

    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.getWeight());
    }

    private void validateVertexNonNegativity(final int v) {
        if (v < 0) {
            throw new IndexOutOfBoundsException("Vertex id cannot be negative");
        }
    }

    private void validateEdgeWeight(final int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Weight cannot be negative");
    }
}