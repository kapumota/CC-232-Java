/*
 * CC-232 - Semana 3, ampliación: cola aleatoria.
 *
 * Reescritura didáctica inspirada en RandomizedQueue de Algorithms,
 * Sedgewick y Wayne. Solo usa la biblioteca estándar de Java.
 *
 * Invariante:
 * - los elementos válidos ocupan a[0..n-1],
 * - a[n..a.length-1] contiene referencias nulas,
 * - 0 <= n <= a.length.
 */
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class Semana03_RandomizedQueueExtra {
    static class RandomizedQueue<T> implements Iterable<T> {
        private Object[] a = new Object[1];
        private int n;
        private final Random random;

        RandomizedQueue(long seed) {
            random = new Random(seed);
        }

        int size() {
            return n;
        }

        boolean isEmpty() {
            return n == 0;
        }

        private void resize(int capacity) {
            a = Arrays.copyOf(a, Math.max(1, capacity));
        }

        void enqueue(T item) {
            if (item == null) {
                throw new IllegalArgumentException("el elemento no puede ser nulo");
            }
            if (n == a.length) resize(2 * a.length);
            a[n++] = item;
        }

        @SuppressWarnings("unchecked")
        T sample() {
            if (isEmpty()) {
                throw new NoSuchElementException("la cola aleatoria está vacía");
            }
            return (T) a[random.nextInt(n)];
        }

        /*
         * TODO(alumno):
         * 1. Elegir una posición aleatoria entre 0 y n-1.
         * 2. Guardar el elemento de esa posición.
         * 3. Mover a esa posición el último elemento válido.
         * 4. Anular la referencia del último espacio y reducir n.
         * 5. Reducir la capacidad cuando n sea un cuarto de a.length.
         *
         * Costo esperado: O(1) amortizado.
         */
        T dequeue() {
            throw new UnsupportedOperationException("TODO: implementar el método dequeue");
        }

        // TODO(alumno): aplicar Fisher-Yates sobre la copia antes de iterar.
        private void shuffle(Object[] copy) {
            throw new UnsupportedOperationException("TODO: implementar el método shuffle");
        }

        boolean checkInvariant() {
            if (n < 0 || n > a.length) return false;
            for (int i = 0; i < n; i++) {
                if (a[i] == null) return false;
            }
            for (int i = n; i < a.length; i++) {
                if (a[i] != null) return false;
            }
            return true;
        }

        @Override
        public Iterator<T> iterator() {
            Object[] copy = Arrays.copyOf(a, n);
            shuffle(copy);
            return new Iterator<>() {
                private int i;

                @Override
                public boolean hasNext() {
                    return i < copy.length;
                }

                @Override
                @SuppressWarnings("unchecked")
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("no quedan elementos");
                    }
                    return (T) copy[i++];
                }
            };
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(a, n))
                    + " cantidad=" + n + " capacidad=" + a.length;
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>(232L);
        queue.enqueue("AVL");
        queue.enqueue("Montículo");
        queue.enqueue("Tabla hash");
        queue.enqueue("Árbol B");

        System.out.println("Estado interno: " + queue);
        System.out.println("Muestra sin retirar: " + queue.sample());
        System.out.println("Invariante válido: " + queue.checkInvariant());

        try {
            System.out.println("Elemento retirado: " + queue.dequeue());
            System.out.print("Iteración aleatoria: ");
            for (String item : queue) System.out.print(item + " ");
            System.out.println();
            System.out.println("Estado final: " + queue);
            System.out.println("Invariante válido: " + queue.checkInvariant());
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
