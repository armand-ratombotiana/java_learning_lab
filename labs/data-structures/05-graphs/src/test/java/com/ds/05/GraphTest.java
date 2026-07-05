package com.ds05;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

public class GraphTest {

    private GraphAdjList<String> graph;
    private GraphAdjList<String> dag;

    @BeforeEach
    void setUp() {
        graph = new GraphAdjList<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        dag = new GraphAdjList<>(true);
        dag.addEdge("A", "B");
        dag.addEdge("A", "C");
        dag.addEdge("B", "D");
        dag.addEdge("C", "D");
        dag.addEdge("D", "E");
    }

    @Test
    void vertexCount() {
        assertEquals(4, graph.vertexCount());
    }

    @Test
    void edgeCount() {
        assertEquals(4, graph.edgeCount());
    }

    @Test
    void hasVertex() {
        assertTrue(graph.hasVertex("A"));
        assertFalse(graph.hasVertex("Z"));
    }

    @Test
    void hasEdge() {
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "A"));
        assertFalse(graph.hasEdge("A", "D"));
    }

    @Test
    void removeEdge() {
        graph.removeEdge("A", "B");
        assertFalse(graph.hasEdge("A", "B"));
    }

    @Test
    void bfs() {
        List<String> bfs = graph.bfs("A");
        assertEquals(4, bfs.size());
        assertTrue(bfs.containsAll(Arrays.asList("A","B","C","D")));
    }

    @Test
    void dfs() {
        List<String> dfs = graph.dfs("A");
        assertEquals(4, dfs.size());
        assertTrue(dfs.containsAll(Arrays.asList("A","B","C","D")));
    }

    @Test
    void bfsDisconnectedVertex() {
        GraphAdjList<Integer> g = new GraphAdjList<>();
        g.addVertex(1);
        g.addVertex(2);
        List<Integer> bfs = g.bfs(1);
        assertEquals(1, bfs.size());
    }

    @Test
    void topologicalSort() {
        List<String> topo = dag.topologicalSort();
        assertEquals(5, topo.size());
        assertTrue(topo.indexOf("A") < topo.indexOf("B"));
        assertTrue(topo.indexOf("A") < topo.indexOf("C"));
        assertTrue(topo.indexOf("B") < topo.indexOf("D"));
        assertTrue(topo.indexOf("C") < topo.indexOf("D"));
        assertTrue(topo.indexOf("D") < topo.indexOf("E"));
    }

    @Test
    void topologicalSortCycleThrows() {
        GraphAdjList<String> cyclic = new GraphAdjList<>(true);
        cyclic.addEdge("A", "B");
        cyclic.addEdge("B", "C");
        cyclic.addEdge("C", "A");
        assertThrows(IllegalStateException.class, cyclic::topologicalSort);
    }

    @Test
    void directedEdgeCount() {
        GraphAdjList<String> d = new GraphAdjList<>(true);
        d.addEdge("X", "Y");
        d.addEdge("Y", "Z");
        assertEquals(2, d.edgeCount());
    }

    @Test
    void dijkstraShortestPath() {
        GraphAdjList<String> wg = new GraphAdjList<>();
        wg.addEdge("A", "B", 4);
        wg.addEdge("A", "C", 2);
        wg.addEdge("B", "C", 1);
        wg.addEdge("B", "D", 5);
        wg.addEdge("C", "D", 8);
        wg.addEdge("C", "E", 10);
        wg.addEdge("D", "E", 2);
        wg.addEdge("D", "F", 6);
        wg.addEdge("E", "F", 3);

        Dijkstra<String> dijkstra = new Dijkstra<>(wg);
        Map<String, Integer> dists = dijkstra.shortestDistances("A");
        assertEquals(0, (int) dists.get("A"));
        assertEquals(4, (int) dists.get("B"));
        assertEquals(2, (int) dists.get("C"));
        assertEquals(9, (int) dists.get("D"));
        assertEquals(11, (int) dists.get("E"));
        assertEquals(14, (int) dists.get("F"));
    }

    @Test
    void dijkstraPath() {
        GraphAdjList<String> wg = new GraphAdjList<>();
        wg.addEdge("A", "B", 4);
        wg.addEdge("A", "C", 2);
        wg.addEdge("B", "C", 1);
        wg.addEdge("B", "D", 5);
        wg.addEdge("C", "D", 8);
        wg.addEdge("C", "E", 10);
        wg.addEdge("D", "E", 2);
        wg.addEdge("D", "F", 6);
        wg.addEdge("E", "F", 3);

        Dijkstra<String> dijkstra = new Dijkstra<>(wg);
        List<String> path = dijkstra.shortestPath("A", "F");
        assertEquals(Arrays.asList("A", "B", "D", "F"), path);
    }

    @Test
    void dijkstraNoPath() {
        GraphAdjList<String> wg = new GraphAdjList<>();
        wg.addVertex("A");
        wg.addVertex("B");
        Dijkstra<String> dijkstra = new Dijkstra<>(wg);
        List<String> path = dijkstra.shortestPath("A", "B");
        assertTrue(path.isEmpty());
    }
}
