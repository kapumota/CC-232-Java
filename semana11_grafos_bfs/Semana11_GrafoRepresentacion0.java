/*
 * CC-232 - Semana 11, lunes: representaciones de grafos.
 * Adaptación didáctica de Graph, AdjacencyLists y AdjacencyMatrix de Pat Morin.
 *
 * Los vértices se identifican con enteros 0, 1, ..., nVertices()-1.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Semana11_GrafoRepresentacion0 {
    interface Graph {
        int nVertices();
        void addEdge(int source, int destination);
        void removeEdge(int source, int destination);
        boolean hasEdge(int source, int destination);
        List<Integer> outEdges(int vertex);
        int outDegree(int vertex);
    }

    static class AdjacencyListGraph implements Graph {
        private final List<List<Integer>> adjacency;

        AdjacencyListGraph(int vertices) {
            if (vertices < 0) throw new IllegalArgumentException("El número de vértices no puede ser negativo");
            adjacency = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
        }

        public int nVertices() { return adjacency.size(); }

        private void checkVertex(int vertex) {
            if (vertex < 0 || vertex >= nVertices()) {
                throw new IndexOutOfBoundsException("Vértice fuera de rango: " + vertex);
            }
        }

        // TODO(alumno): validar ambos vértices y agregar destination una sola vez.
        public void addEdge(int source, int destination) {
            throw new UnsupportedOperationException("TODO: implementar el método addEdge");
        }

        public void removeEdge(int source, int destination) {
            checkVertex(source);
            checkVertex(destination);
            adjacency.get(source).remove(Integer.valueOf(destination));
        }

        public boolean hasEdge(int source, int destination) {
            checkVertex(source);
            checkVertex(destination);
            return adjacency.get(source).contains(destination);
        }

        public List<Integer> outEdges(int vertex) {
            checkVertex(vertex);
            return Collections.unmodifiableList(adjacency.get(vertex));
        }

        public int outDegree(int vertex) {
            return outEdges(vertex).size();
        }
    }

    static class AdjacencyMatrixGraph implements Graph {
        private final boolean[][] matrix;

        AdjacencyMatrixGraph(int vertices) {
            if (vertices < 0) throw new IllegalArgumentException("El número de vértices no puede ser negativo");
            matrix = new boolean[vertices][vertices];
        }

        public int nVertices() { return matrix.length; }

        private void checkVertex(int vertex) {
            if (vertex < 0 || vertex >= nVertices()) {
                throw new IndexOutOfBoundsException("Vértice fuera de rango: " + vertex);
            }
        }

        public void addEdge(int source, int destination) {
            checkVertex(source);
            checkVertex(destination);
            matrix[source][destination] = true;
        }

        public void removeEdge(int source, int destination) {
            checkVertex(source);
            checkVertex(destination);
            matrix[source][destination] = false;
        }

        public boolean hasEdge(int source, int destination) {
            checkVertex(source);
            checkVertex(destination);
            return matrix[source][destination];
        }

        public List<Integer> outEdges(int vertex) {
            checkVertex(vertex);
            List<Integer> result = new ArrayList<>();
            for (int destination = 0; destination < nVertices(); destination++) {
                if (matrix[vertex][destination]) result.add(destination);
            }
            return result;
        }

        public int outDegree(int vertex) {
            int degree = 0;
            for (boolean edge : matrix[vertex]) if (edge) degree++;
            return degree;
        }
    }

    public static void main(String[] args) {
        Graph listGraph = new AdjacencyListGraph(5);
        Graph matrixGraph = new AdjacencyMatrixGraph(5);
        try {
            for (Graph graph : new Graph[] {listGraph, matrixGraph}) {
                graph.addEdge(0, 1);
                graph.addEdge(0, 3);
                graph.addEdge(3, 4);
                System.out.println("vecinos de 0=" + graph.outEdges(0) + " (esperado: [1, 3])");
                System.out.println("grado de salida de 0=" + graph.outDegree(0) + " (esperado: 2)");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
