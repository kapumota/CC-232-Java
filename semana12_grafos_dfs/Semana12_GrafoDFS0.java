/*
 * CC-232 - Semana 12, lunes: recorrido DFS con tres estados.
 * Adaptación didáctica del DFS recursivo de Pat Morin.
 */
import java.util.ArrayList;
import java.util.List;

public class Semana12_GrafoDFS0 {
    static final byte WHITE = 0;
    static final byte GRAY = 1;
    static final byte BLACK = 2;

    static class Graph {
        private final List<List<Integer>> adjacency;

        Graph(int vertices) {
            adjacency = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
        }

        int nVertices() { return adjacency.size(); }

        void addDirectedEdge(int source, int destination) {
            adjacency.get(source).add(destination);
        }

        List<Integer> outEdges(int vertex) {
            return adjacency.get(vertex);
        }
    }

    static List<Integer> dfsOrder(Graph graph, int source) {
        byte[] color = new byte[graph.nVertices()];
        List<Integer> order = new ArrayList<>();
        dfsVisit(graph, source, color, order);
        return order;
    }

    // TODO(alumno): marcar GRAY, registrar vertex, visitar vecinos WHITE y
    // finalmente marcar BLACK.
    static void dfsVisit(Graph graph, int vertex, byte[] color, List<Integer> order) {
        throw new UnsupportedOperationException("TODO: implementar el método dfsVisit");
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addDirectedEdge(0, 1);
        graph.addDirectedEdge(0, 2);
        graph.addDirectedEdge(1, 3);
        graph.addDirectedEdge(2, 4);
        graph.addDirectedEdge(4, 5);
        try {
            System.out.println("Orden de DFS: " + dfsOrder(graph, 0));
            System.out.println("esperado: [0, 1, 3, 2, 4, 5]");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
