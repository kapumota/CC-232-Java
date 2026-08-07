/*
 * CC-232 - Semana 7, viernes: trickleDown, remove y heapify.
 * Adaptación didáctica de BinaryHeap de Pat Morin.
 */
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Semana07_Heap1 {
    static class BinaryHeap {
        private Integer[] a;
        private int n;

        BinaryHeap() {
            a = new Integer[1];
        }

        BinaryHeap(Integer[] values) {
            a = Arrays.copyOf(values, Math.max(1, values.length));
            n = values.length;
            heapify();
        }

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

        private void bubbleUp(int i) {
            while (i > 0 && a[i] < a[parent(i)]) {
                int p = parent(i);
                swap(i, p);
                i = p;
            }
        }

        // TODO(alumno): seleccionar el menor entre i y sus hijos válidos,
        // intercambiar y repetir hasta restaurar el invariante.
        private void trickleDown(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método trickleDown");
        }

        // TODO(alumno): ejecutar trickleDown desde n/2-1 hasta 0. El costo
        // total debe ser O(n), no O(n log n).
        private void heapify() {
            throw new UnsupportedOperationException("TODO: implementar el método heapify");
        }

        boolean add(int x) {
            if (n + 1 > a.length) resize();
            a[n++] = x;
            bubbleUp(n - 1);
            return true;
        }

        int remove() {
            if (n == 0) throw new NoSuchElementException("el montículo está vacío");
            int min = a[0];
            a[0] = a[--n];
            a[n] = null;
            if (n > 0) trickleDown(0);
            if (a.length >= 3 * n) resize();
            return min;
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(a, n));
        }
    }

    public static void main(String[] args) {
        try {
            BinaryHeap heap = new BinaryHeap(new Integer[] {9, 4, 7, 1, 3, 6, 2});
            System.out.println("Montículo: " + heap);
            System.out.print("Orden de extracción: ");
            while (true) System.out.print(heap.remove() + " ");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("\nEsperado: 1 2 3 4 6 7 9");
        }
    }
}
