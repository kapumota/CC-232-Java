/*
 * CC-232 - Semana 8, lunes: prefix sums.
 * Implementación docente de un algoritmo estándar.
 */
import java.util.Arrays;

public class Semana08_FenwickTree0 {
    // TODO(alumno): construir prefix de longitud values.length+1 donde
    // prefix[i+1] = prefix[i] + values[i]. Costo O(n).
    static long[] buildPrefix(int[] values) {
        throw new UnsupportedOperationException("TODO: implementar el método buildPrefix");
    }

    // TODO(alumno): retornar la suma inclusiva [left,right] mediante dos
    // accesos al arreglo prefix. Validar los límites.
    static long rangeSum(long[] prefix, int left, int right) {
        throw new UnsupportedOperationException("TODO: implementar el método rangeSum");
    }

    public static void main(String[] args) {
        int[] values = {3, 2, 7, 1, 4, 6};
        try {
            long[] prefix = buildPrefix(values);
            System.out.println("Suma de prefijos: " + Arrays.toString(prefix));
            System.out.println("Esperado: [0, 3, 5, 12, 13, 17, 23]");
            System.out.println("Suma del rango [1, 4]: " + rangeSum(prefix, 1, 4) + " (esperado: 14)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
