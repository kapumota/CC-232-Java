/*
 * CC-232 - Semana 14, viernes: repaso integrador de grafos.
 * Se completa una traza breve, no una aplicación nueva.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Semana14_Repaso1 {
    static int[] shortestHops(List<List<Integer>> graph, int source) {
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, -1);
        Queue<Integer> queue = new ArrayDeque<>();
        distance[source] = 0;
        queue.add(source);
        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            for (int neighbor : graph.get(vertex)) {
                if (distance[neighbor] == -1) {
                    distance[neighbor] = distance[vertex] + 1;
                    queue.add(neighbor);
                }
            }
        }
        return distance;
    }

    static List<List<Integer>> graph(int vertices, int[][] edges) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < vertices; i++) adjacency.add(new ArrayList<>());
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        return adjacency;
    }

    public static void main(String[] args) {
        List<List<Integer>> graph = graph(6, new int[][] {
                {0, 1}, {0, 2}, {1, 3}, {2, 4}, {4, 5}
        });
        System.out.println(Arrays.toString(shortestHops(graph, 0)));
        System.out.println("Esperado: [0, 1, 1, 2, 2, 3]");
    }
}
