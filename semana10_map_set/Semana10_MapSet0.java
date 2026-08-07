/*
 * CC-232 - Semana 10, lunes: Map para contar frecuencias.
 * No se introduce una estructura nueva: se usa una tabla hash como Map.
 */
import java.util.LinkedHashMap;
import java.util.Map;

public class Semana10_MapSet0 {
    // TODO(alumno): contar cuántas veces aparece cada palabra.
    // LinkedHashMap conserva el orden de la primera aparición para una salida determinista.
    static Map<String, Integer> frequencies(String[] words) {
        throw new UnsupportedOperationException("TODO: implementar el método frequencies");
    }

    static String mostFrequent(Map<String, Integer> frequencies) {
        String best = null;
        int bestCount = -1;
        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            if (entry.getValue() > bestCount) {
                best = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return best;
    }

    public static void main(String[] args) {
        String[] words = {"montículo", "grafo", "tabla hash", "grafo", "montículo", "grafo"};
        try {
            Map<String, Integer> result = frequencies(words);
            System.out.println(result + " (esperado: {montículo=2, grafo=3, tabla hash=1})");
            System.out.println("más frecuente=" + mostFrequent(result) + " (esperado: grafo)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
