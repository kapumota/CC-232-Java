/*
 * CC-232 - Semana 11, viernes: recorrido BFS y distancias no ponderadas.
 * Adaptación didáctica del BFS de Algorithms.java de Pat Morin.
 *
 * Invariante: un vértice se marca al entrar en la cola, por lo que se
 * encola como máximo una vez.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Semana11_GrafoBFS1 {
    static class Graph {
        private final List<List<Integer>> adjacency;

        Graph(int vertices) {
            adjacency = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
        }

        int nVertices() { return adjacency.size(); }

        void addUndirectedEdge(int a, int b) {
            adjacency.get(a).add(b);
            adjacency.get(b).add(a);
        }

        List<Integer> outEdges(int vertex) {
            return adjacency.get(vertex);
        }
    }

    // TODO(alumno): calcular la distancia mínima en número de aristas desde source.
    // Usar -1 para vértices no alcanzables y marcar al encolar.
    static int[] bfsDistances(Graph graph, int source) {
        throw new UnsupportedOperationException("TODO: implementar el método bfsDistances");
    }

    static List<Integer> bfsOrder(Graph graph, int source) {
        boolean[] seen = new boolean[graph.nVertices()];
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        queue.add(source);
        seen[source] = true;
        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            order.add(vertex);
            for (int neighbor : graph.outEdges(vertex)) {
                if (!seen[neighbor]) {
                    seen[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return order;
    }

    public static void main(String[] args) {
        Graph graph = new Graph(7);
        graph.addUndirectedEdge(0, 1);
        graph.addUndirectedEdge(0, 2);
        graph.addUndirectedEdge(1, 3);
        graph.addUndirectedEdge(2, 4);
        graph.addUndirectedEdge(4, 5);
        // El vértice 6 queda aislado.
        System.out.println("Orden de BFS: " + bfsOrder(graph, 0) + " (esperado: [0, 1, 2, 3, 4, 5])");
        try {
            System.out.println("Distancias: " + Arrays.toString(bfsDistances(graph, 0)));
            System.out.println("esperado: [0, 1, 1, 2, 2, 3, -1]");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
