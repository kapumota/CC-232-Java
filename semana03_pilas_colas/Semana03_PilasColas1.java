/*
 * CC-232 - Semana 3, viernes: ArrayQueue circular.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante: el elemento lógico k está en a[(j+k) % a.length].
 */
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Semana03_PilasColas1 {
    static class ArrayQueue {
        private Integer[] a = new Integer[4];
        private int j;
        private int n;

        int size() { return n; }
        Integer peek() { return n == 0 ? null : a[j]; }

        // TODO(alumno): copiar los n elementos en orden lógico a un arreglo
        // de capacidad max(1, 2*n), asignarlo a a y fijar j=0.
        private void resize() {
            throw new UnsupportedOperationException("TODO: implementar el método resize");
        }

        boolean add(Integer x) {
            if (n + 1 > a.length) resize();
            a[(j + n) % a.length] = x;
            n++;
            return true;
        }

        // TODO(alumno): retirar a[j], avanzar j modularmente, reducir n y
        // llamar resize cuando a.length >= 3*n. Costo amortizado O(1).
        Integer remove() {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        String logicalView() {
            Integer[] view = new Integer[n];
            for (int k = 0; k < n; k++) view[k] = a[(j + k) % a.length];
            return Arrays.toString(view) + " j=" + j + " capacidad=" + a.length;
        }
    }

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue();
        q.add(10);
        q.add(20);
        q.add(30);
        System.out.println(q.logicalView() + " (esperado: [10, 20, 30])");
        try {
            System.out.println("Resultado de remove(): " + q.remove() + " (esperado: 10)");
            q.add(40);
            q.add(50);
            q.add(60); // obliga a revisar resize y circularidad
            System.out.println(q.logicalView() + " (esperado: [20, 30, 40, 50, 60])");
        } catch (UnsupportedOperationException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }
}
