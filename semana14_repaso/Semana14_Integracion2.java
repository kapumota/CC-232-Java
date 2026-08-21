/*
 * CC-232 - Semana 14, viernes: integración final con Map y Graph.
 * Se combinan nombres externos, ids internos, BFS y componentes conexas.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Semana14_Integracion2 {
    static class SocialGraph {
        private final Map<String, Integer> nameToId = new HashMap<>();
        private final List<String> idToName = new ArrayList<>();
        private final List<List<Integer>> adjacency = new ArrayList<>();

        void addPerson(String name) {
            if (nameToId.containsKey(name)) return;
            int id = idToName.size();
            nameToId.put(name, id);
            idToName.add(name);
            adjacency.add(new ArrayList<>());
        }

        void addConnection(String a, String b) {
            Integer u = nameToId.get(a);
            Integer v = nameToId.get(b);
            if (u == null || v == null) {
                throw new IllegalArgumentException("Ambas personas deben existir");
            }
            if (!adjacency.get(u).contains(v)) {
                adjacency.get(u).add(v);
                adjacency.get(v).add(u);
            }
        }

        // TODO(alumno): usar BFS para retornar la distancia mínima en aristas.
        // Retornar -1 si alguna persona no existe o si no hay camino.
        int shortestDistance(String source, String target) {
            throw new UnsupportedOperationException("TODO: implementar shortestDistance");
        }

        // TODO(alumno): usar BFS y parent[] para reconstruir un camino mínimo.
        // Retornar lista vacía si alguna persona no existe o no hay camino.
        List<String> shortestPath(String source, String target) {
            throw new UnsupportedOperationException("TODO: implementar shortestPath");
        }

        // TODO(alumno): retornar todas las componentes conexas con nombres.
        List<List<String>> connectedGroups() {
            throw new UnsupportedOperationException("TODO: implementar connectedGroups");
        }

        int size() { return idToName.size(); }
    }

    public static void main(String[] args) {
        SocialGraph graph = new SocialGraph();

        for (String name : List.of(
                "ire", "chalo", "paulette", "clau", "aco", "kapu", "coconi")) {
            graph.addPerson(name);
        }

        graph.addConnection("ire", "chalo");
        graph.addConnection("ire", "paulette");
        graph.addConnection("chalo", "clau");
        graph.addConnection("paulette", "aco");
        graph.addConnection("aco", "kapu");

        try {
            System.out.println("Personas: " + graph.size() + " (esperado: 7)");
            System.out.println("Distancia ire -> kapu: "
                    + graph.shortestDistance("ire", "kapu") + " (esperado: 3)");
            System.out.println("Camino ire -> kapu: "
                    + graph.shortestPath("ire", "kapu")
                    + " (esperado: [ire, paulette, aco, kapu])");
            System.out.println("Distancia ire -> coconi: "
                    + graph.shortestDistance("ire", "coconi") + " (esperado: -1)");
            System.out.println("Cantidad de grupos: "
                    + graph.connectedGroups().size() + " (esperado: 2)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
