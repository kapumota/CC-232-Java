/*
 * CC-232 - Semana 1, lunes: ArrayStack y crecimiento dinámico.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante: 0 <= n <= a.length y los elementos ocupan a[0..n-1].
 */
import java.util.Arrays;

public class Semana01_ArraysDinamicos0 {
    static class ArrayStack {
        private Integer[] a = new Integer[1];
        private int n = 0;

        int size() {
            return n;
        }

        int capacity() {
            return a.length;
        }

        Integer get(int i) {
            checkElementIndex(i);
            return a[i];
        }

        // TODO(alumno): crear un arreglo de capacidad max(1, 2*n), copiar
        // a[0..n-1] y reemplazar la referencia a.
        private void resize() {
            throw new UnsupportedOperationException("TODO: implementar el método resize");
        }

        // TODO(alumno): insertar x al final. Si n+1 supera la capacidad,
        // llamar primero a resize(). Costo amortizado esperado: O(1).
        boolean add(Integer x) {
            throw new UnsupportedOperationException("TODO: implementar el método add");
        }

        private void checkElementIndex(int i) {
            if (i < 0 || i >= n) {
                throw new IndexOutOfBoundsException("índice=" + i + ", tamaño=" + n);
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(a, n))
                    + " tamaño=" + n + " capacidad=" + a.length;
        }
    }

    public static void main(String[] args) {
        ArrayStack s = new ArrayStack();
        System.out.println("Inicial: " + s);
        try {
            for (int x : new int[] {4, 7, 1, 9}) {
                s.add(x);
                System.out.println("Después de add(" + x + "): " + s);
            }
            System.out.println("Resultado de get(2): " + s.get(2) + " (esperado: 1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
