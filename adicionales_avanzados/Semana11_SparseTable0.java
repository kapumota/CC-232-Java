/*
 * CC-232 - Semana 11, lunes: potencias de dos y logaritmos enteros.
 * Preparación para Sparse Table de mínimo idempotente.
 */
import java.util.Arrays;

public class Semana11_SparseTable0 {
    // TODO(alumno): logs[i]=floor(log2(i)) para i>=1, logs[0]=-1.
    static int[] buildLogs(int n) {
        throw new UnsupportedOperationException("TODO: implementar el método buildLogs");
    }

    // TODO(alumno): retornar floor(log2(n))+1 para n>0.
    static int levels(int n) {
        throw new UnsupportedOperationException("TODO: implementar el método levels");
    }

    public static void main(String[] args) {
        try {
            int[] logs = buildLogs(10);
            System.out.println(Arrays.toString(logs));
            System.out.println("Esperado: [-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3]");
            System.out.println("Resultado de levels(10): " + levels(10) + " (esperado: 4)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
