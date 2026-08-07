/*
 * CC-232 - Semana 11, viernes: Sparse Table para Range Minimum Query.
 * Preprocesamiento O(n log n), consulta O(1), sin actualizaciones.
 */
public class Semana11_SparseTable1 {
    static class SparseTable {
        private final int[][] table;
        private final int[] logs;
        private final int n;

        SparseTable(int[] values) {
            n = values.length;
            if (n == 0) throw new IllegalArgumentException("arreglo vacío");
            logs = new int[n + 1];
            for (int i = 2; i <= n; i++) logs[i] = logs[i / 2] + 1;
            int levels = logs[n] + 1;
            table = new int[levels][n];
            System.arraycopy(values, 0, table[0], 0, n);
            for (int k = 1; k < levels; k++) {
                int length = 1 << k;
                int half = length >> 1;
                for (int i = 0; i + length <= n; i++) {
                    table[k][i] = Math.min(table[k - 1][i], table[k - 1][i + half]);
                }
            }
        }

        // TODO(alumno): sea k=floor(log2(right-left+1)), comparar los dos
        // bloques de longitud 2^k que cubren los extremos del rango.
        int rangeMin(int left, int right) {
            throw new UnsupportedOperationException("TODO: implementar el método rangeMin");
        }

        // TODO(alumno): explicar mediante código por qué Sparse Table no admite
        // set eficiente. Retornar una nueva tabla construida con el cambio.
        SparseTable rebuiltWith(int[] currentValues, int index, int value) {
            throw new UnsupportedOperationException("TODO: implementar el método rebuiltWith");
        }
    }

    public static void main(String[] args) {
        int[] values = {7, 2, 3, 0, 5, 10, 3, 12, 18};
        SparseTable st = new SparseTable(values);
        try {
            System.out.println("Mínimo del rango [1, 4]: " + st.rangeMin(1, 4) + " (esperado: 0)");
            System.out.println("Mínimo del rango [4, 7]: " + st.rangeMin(4, 7) + " (esperado: 3)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
