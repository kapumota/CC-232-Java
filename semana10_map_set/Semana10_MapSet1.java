/*
 * CC-232 - Semana 10, viernes: Set, duplicados y selección de implementación.
 */
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Semana10_MapSet1 {
    // TODO(alumno): retornar el primer valor repetido o null si no existe.
    // Complejidad esperada: O(n) usando HashSet.
    static Integer firstDuplicate(int[] values) {
        throw new UnsupportedOperationException("TODO: implementar el método firstDuplicate");
    }

    static Set<Integer> uniqueSorted(int[] values) {
        Set<Integer> result = new TreeSet<>();
        for (int value : values) result.add(value);
        return result;
    }

    static String chooseImplementation(boolean needsOrder, boolean needsMinimum) {
        if (needsOrder || needsMinimum) return "TreeSet, árbol balanceado";
        return "HashSet, tabla hash";
    }

    public static void main(String[] args) {
        int[] values = {7, 2, 9, 4, 2, 8};
        try {
            System.out.println("primer duplicado=" + firstDuplicate(values) + " (esperado: 2)");
            System.out.println("ordenados=" + uniqueSorted(values) + " (esperado: [2, 4, 7, 8, 9])");
            System.out.println(chooseImplementation(false, false));
            System.out.println(chooseImplementation(true, true));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
