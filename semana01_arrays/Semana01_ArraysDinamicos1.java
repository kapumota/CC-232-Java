/*
 * CC-232 - Semana 1, viernes: ArrayStack indexado.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Complejidades: get/set O(1), add/remove O(n) por desplazamientos.
 */
import java.util.Arrays;

public class Semana01_ArraysDinamicos1 {
    static class ArrayStack {
        private Integer[] a = new Integer[1];
        private int n = 0;

        int size() {
            return n;
        }

        Integer get(int i) {
            checkElementIndex(i);
            return a[i];
        }

        Integer set(int i, Integer x) {
            checkElementIndex(i);
            Integer old = a[i];
            a[i] = x;
            return old;
        }

        private void resize() {
            Integer[] b = new Integer[Math.max(1, 2 * n)];
            for (int i = 0; i < n; i++) {
                b[i] = a[i];
            }
            a = b;
        }

        void add(int i, Integer x) {
            checkPositionIndex(i);
            if (n + 1 > a.length) {
                resize();
            }
            for (int j = n; j > i; j--) {
                a[j] = a[j - 1];
            }
            a[i] = x;
            n++;
        }

        boolean add(Integer x) {
            add(n, x);
            return true;
        }

        // TODO(alumno): guardar a[i], desplazar a la izquierda, decrementar n
        // y reducir capacidad cuando a.length >= 3*n. Retornar lo eliminado.
        Integer remove(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        // TODO(alumno): retornar el primer índice cuyo elemento equals(x),
        // o -1 si no existe. Costo esperado: O(n).
        int indexOf(Integer x) {
            throw new UnsupportedOperationException("TODO: implementar el método indexOf");
        }

        private void checkElementIndex(int i) {
            if (i < 0 || i >= n) {
                throw new IndexOutOfBoundsException("índice=" + i + ", tamaño=" + n);
            }
        }

        private void checkPositionIndex(int i) {
            if (i < 0 || i > n) {
                throw new IndexOutOfBoundsException("posición=" + i + ", tamaño=" + n);
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
        s.add(10);
        s.add(30);
        s.add(1, 20);
        s.add(40);
        System.out.println(s + " (esperado: [10, 20, 30, 40])");
        System.out.println("El método set(2, 35) reemplazó " + s.set(2, 35));
        System.out.println(s);
        try {
            System.out.println("Resultado de remove(1): " + s.remove(1) + " (esperado: 20)");
            System.out.println("Resultado de indexOf(35): " + s.indexOf(35) + " (esperado: 1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
