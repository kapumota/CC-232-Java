/*
 * CC-232 - Semana 10, viernes: actualización de rango con Lazy Propagation.
 * Invariante: tree[node] incluye el efecto de lazy[node] sobre su segmento.
 */
public class Semana10_SegmentTreeLazy1 {
    static class LazySegmentTree {
        private final int n;
        private final long[] tree;
        private final long[] lazy;

        LazySegmentTree(int[] values) {
            n = values.length;
            if (n == 0) throw new IllegalArgumentException("arreglo vacío");
            tree = new long[4 * n];
            lazy = new long[4 * n];
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
            pull(node);
        }

        private void pull(int node) {
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }

        private void apply(int node, int left, int right, long delta) {
            tree[node] += delta * (right - left + 1L);
            lazy[node] += delta;
        }

        private void push(int node, int left, int right) {
            if (lazy[node] == 0 || left == right) return;
            int middle = (left + right) / 2;
            apply(2 * node, left, middle, lazy[node]);
            apply(2 * node + 1, middle + 1, right, lazy[node]);
            lazy[node] = 0;
        }

        void addRange(int queryLeft, int queryRight, long delta) {
            checkRange(queryLeft, queryRight);
            addRange(1, 0, n - 1, queryLeft, queryRight, delta);
        }

        // TODO(alumno): manejar no intersección, cobertura total y cobertura
        // parcial. En la parcial, ejecutar push antes de descender y pull al volver.
        private void addRange(int node, int left, int right,
                              int queryLeft, int queryRight, long delta) {
            throw new UnsupportedOperationException("TODO: implementar la versión recursiva de addRange");
        }

        long rangeSum(int queryLeft, int queryRight) {
            checkRange(queryLeft, queryRight);
            return rangeSum(1, 0, n - 1, queryLeft, queryRight);
        }

        // TODO(alumno): no propagar si no hay intersección ni si existe cobertura
        // total. Usar push únicamente antes de descender parcialmente.
        private long rangeSum(int node, int left, int right,
                              int queryLeft, int queryRight) {
            throw new UnsupportedOperationException("TODO: implementar la versión recursiva de rangeSum");
        }

        private void checkRange(int left, int right) {
            if (left < 0 || right >= n || left > right) {
                throw new IllegalArgumentException("rango inválido");
            }
        }
    }

    public static void main(String[] args) {
        LazySegmentTree st = new LazySegmentTree(new int[] {2, 1, 3, 4, 5});
        try {
            System.out.println("Suma del rango [0, 4]: " + st.rangeSum(0, 4) + " (esperado: 15)");
            st.addRange(1, 3, 10);
            System.out.println("Suma del rango [0, 4]: " + st.rangeSum(0, 4) + " (esperado: 45)");
            System.out.println("Suma del rango [2, 4]: " + st.rangeSum(2, 4) + " (esperado: 32)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
