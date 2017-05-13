package org.pszt.graph;

import lombok.Getter;

public abstract class AbstractGraph {
    @Getter protected final int V;
    @Getter protected int E;

    protected AbstractGraph(final int V) {
        if (V < 0) {
            throw new IllegalArgumentException("Number of vertices cannot be negative");
        }
        this.V = V;
    }

    public abstract void addEdge(final Edge edge);
    public abstract Iterable<Edge> adj(final int v);

    protected void validateVertexId(final int v) {
        if (v < 0 || v >= V) {
            throw new IndexOutOfBoundsException("Vertex id should be between 0 and " + (V - 1));
        }
    }
}