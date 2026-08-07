/*
 * CC-232 - Semana 9, viernes: Segment Tree para suma.
 * Invariante: tree[node] es la suma exacta del segmento [left,right].
 */
public class Semana09_SegmentTree1 {
    static class SegmentTree {
        private final int n;
        private final long[] tree;

        SegmentTree(int[] values) {
            n = values.length;
            if (n == 0) throw new IllegalArgumentException("arreglo vacío");
            tree = new long[4 * n];
            build(1, 0, n - 1, values);
        }

        private void build(int node, int left, int right, int[] values) {
            if (left == right) {
                tree[node] = values[left];
                return;
            }
            int middle = (left + right) / 2;
            build(2 * node, left, middle, values);
            build(2 * node + 1, middle + 1, right, values);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }

        long rangeSum(int queryLeft, int queryRight) {
            checkRange(queryLeft, queryRight);
            return rangeSum(1, 0, n - 1, queryLeft, queryRight);
        }

        // TODO(alumno): implementar los casos sin intersección, cobertura total
        // y cobertura parcial. Costo O(log n) para un rango típico.
        private long rangeSum(int node, int left, int right,
                              int queryLeft, int queryRight) {
            throw new UnsupportedOperationException("TODO: implementar la versión recursiva de rangeSum");
        }

        void set(int index, int value) {
            if (index < 0 || index >= n) throw new IndexOutOfBoundsException();
            set(1, 0, n - 1, index, value);
        }

        // TODO(alumno): descender hasta la hoja index y recomputar las sumas al
        // regresar de la recursión.
        private void set(int node, int left, int right, int index, int value) {
            throw new UnsupportedOperationException("TODO: implementar la versión recursiva de set");
        }

        private void checkRange(int left, int right) {
            if (left < 0 || right >= n || left > right) {
                throw new IllegalArgumentException("rango inválido");
            }
        }
    }

    public static void main(String[] args) {
        SegmentTree st = new SegmentTree(new int[] {3, 2, 7, 1, 4, 6});
        try {
            System.out.println("Suma del rango [1, 4]: " + st.rangeSum(1, 4) + " (esperado: 14)");
            st.set(2, 10);
            System.out.println("Después de set(2, 10), suma del rango [1, 4]: "
                    + st.rangeSum(1, 4) + " (esperado: 17)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
