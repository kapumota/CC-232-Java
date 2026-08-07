/*
 * CC-232 - Semana 8, viernes: Fenwick Tree (Binary Indexed Tree).
 * Invariante: tree[i] guarda la suma de un bloque que termina en i.
 */
import java.util.Arrays;

public class Semana08_FenwickTree1 {
    static class FenwickTree {
        private final long[] tree;
        private final long[] values;

        FenwickTree(int[] initial) {
            tree = new long[initial.length + 1];
            values = new long[initial.length];
            for (int i = 0; i < initial.length; i++) {
                add(i, initial[i]);
            }
        }

        private static int lowbit(int x) {
            return x & -x;
        }

        void add(int index, long delta) {
            checkIndex(index);
            values[index] += delta;
            for (int i = index + 1; i < tree.length; i += lowbit(i)) {
                tree[i] += delta;
            }
        }

        long prefixSum(int index) {
            if (index < -1 || index >= values.length) {
                throw new IndexOutOfBoundsException("índice=" + index);
            }
            long sum = 0;
            for (int i = index + 1; i > 0; i -= lowbit(i)) sum += tree[i];
            return sum;
        }

        // TODO(alumno): usar dos prefixSum para calcular [left,right].
        long rangeSum(int left, int right) {
            throw new UnsupportedOperationException("TODO: implementar el método rangeSum");
        }

        // TODO(alumno): calcular delta=newValue-values[index] y aplicar add.
        // Evitar modificar values dos veces.
        void set(int index, long newValue) {
            throw new UnsupportedOperationException("TODO: implementar el método set");
        }

        private void checkIndex(int index) {
            if (index < 0 || index >= values.length) {
                throw new IndexOutOfBoundsException("índice=" + index);
            }
        }

        @Override
        public String toString() {
            return "Árbol interno: " + Arrays.toString(tree) + " valores=" + Arrays.toString(values);
        }
    }

    public static void main(String[] args) {
        FenwickTree bit = new FenwickTree(new int[] {3, 2, 7, 1, 4, 6});
        System.out.println(bit);
        System.out.println("Resultado de prefixSum(3): " + bit.prefixSum(3) + " (esperado: 13)");
        try {
            System.out.println("Resultado de rangeSum(1, 4): " + bit.rangeSum(1, 4) + " (esperado: 14)");
            bit.set(2, 10);
            System.out.println("Después de set(2, 10), resultado de rangeSum(1, 4): "
                    + bit.rangeSum(1, 4) + " (esperado: 17)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
