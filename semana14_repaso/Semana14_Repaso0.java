/*
 * CC-232 - Semana 14, lunes: repaso de invariantes y complejidades.
 * No introduce una estructura nueva.
 */
public class Semana14_Repaso0 {
    static String expectedCost(String operation) {
        return switch (operation) {
            case "arreglo-acceso" -> "O(1)";
            case "lista-búsqueda" -> "O(n)";
            case "avl-búsqueda" -> "O(log n)";
            case "heap-mínimo" -> "O(1)";
            case "hash-búsqueda" -> "O(1) esperado";
            case "BFS" -> "O(V + E)";
            default -> "operación no registrada";
        };
    }

    // TODO(alumno): indicar una estructura apropiada según la operación dominante.
    static String chooseStructure(boolean needsIndex, boolean needsMinimum,
                                  boolean needsOrder, boolean modelsConnections) {
        throw new UnsupportedOperationException("TODO: implementar el método chooseStructure");
    }

    public static void main(String[] args) {
        System.out.println("Costo de BFS: " + expectedCost("BFS"));
        try {
            System.out.println(chooseStructure(false, false, false, true) + " (esperado: grafo)");
            System.out.println(chooseStructure(false, true, false, false) + " (esperado: heap)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
