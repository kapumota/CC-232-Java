/*
 * CC-232 - Semana 7, lunes: BinaryHeap y bubbleUp.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante min-heap: a[parent(i)] <= a[i] para todo i>0.
 */
import java.util.Arrays;

public class Semana07_Heap0 {
    static class BinaryHeap {
        private Integer[] a = new Integer[1];
        private int n;

        private int left(int i) { return 2 * i + 1; }
        private int right(int i) { return 2 * i + 2; }
        private int parent(int i) { return (i - 1) / 2; }

        private void resize() {
            a = Arrays.copyOf(a, Math.max(1, 2 * n));
        }

        private void swap(int i, int j) {
            int x = a[i];
            a[i] = a[j];
            a[j] = x;
        }

        // TODO(alumno): mientras i>0 y a[i] sea menor que su padre,
        // intercambiar ambos y continuar desde el padre.
        private void bubbleUp(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método bubbleUp");
        }

        boolean add(int x) {
            if (n + 1 > a.length) resize();
            a[n++] = x;
            bubbleUp(n - 1);
            return true;
        }

        int size() { return n; }
        Integer peek() { return n == 0 ? null : a[0]; }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(a, n));
        }
    }

    public static void main(String[] args) {
        BinaryHeap heap = new BinaryHeap();
        try {
            for (int x : new int[] {8, 3, 10, 1, 6}) {
                heap.add(x);
                System.out.println("Después de add(" + x + "): " + heap);
            }
            System.out.println("Mínimo actual: " + heap.peek() + " (esperado: 1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
