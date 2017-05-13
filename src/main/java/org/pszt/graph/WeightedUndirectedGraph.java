package org.pszt.graph;

import java.util.ArrayList;
import java.util.List;

public class WeightedUndirectedGraph extends AbstractGraph {
    private List<Edge>[] adj;

    public WeightedUndirectedGraph(final int V) {
        super(V);

        this.E = 0;
        adj = (List<Edge>[]) new List[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<Edge>();
        }
    }

    public void addEdge(final Edge edge) {
        final int v = edge.either();
        final int w = edge.other(v);

        validateVertexId(v);
        validateVertexId(w);

        adj[v].add(edge);
        adj[w].add(edge);
        E++;
    }

    public Iterable<Edge> adj(final int v) {
        validateVertexId(v);
        return adj[v];
    }

    @Override
    public String toString() {
        final StringBuilder graphBuffer = new StringBuilder();

        graphBuffer.append(String.format("%d %d\n", this.getV(), this.getE()));

        for (int v = 0; v < getV(); v++) {
            for (Edge e : adj[v]) {
                graphBuffer.append(String.format("%d %d %d\n", e.either(), e.other(e.either()), e.getWeight()));
            }
        }

        return graphBuffer.toString();
    }
}