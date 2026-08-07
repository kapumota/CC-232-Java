/*
 * CC-232 - Semana 12, viernes: componentes conexas mediante DFS.
 * Cada llamada nueva a markComponent descubre exactamente un componente.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Semana12_Componentes1 {
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

    // TODO(alumno): recorrer todos los vértices. Cada vértice no visitado
    // inicia un DFS y aumenta el contador de componentes.
    static int countConnectedComponents(Graph graph) {
        throw new UnsupportedOperationException("TODO: implementar el método countConnectedComponents");
    }

    static void markComponent(Graph graph, int source, boolean[] seen) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (seen[vertex]) continue;
            seen[vertex] = true;
            for (int neighbor : graph.outEdges(vertex)) {
                if (!seen[neighbor]) stack.push(neighbor);
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph(8);
        graph.addUndirectedEdge(0, 1);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(3, 4);
        graph.addUndirectedEdge(5, 6);
        // El vértice 7 queda aislado: {0,1,2}, {3,4}, {5,6}, {7}.
        try {
            System.out.println("Componentes conexas: " + countConnectedComponents(graph) + " (esperado: 4)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
