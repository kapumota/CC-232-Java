/*
 * CC-232 - Semana 3, ampliación: deque enlazado.
 *
 * Reescritura didáctica inspirada en el ejercicio Deque de Algorithms,
 * Sedgewick y Wayne. No usa algs4.jar ni dependencias externas.
 *
 * Invariante:
 * - header.next es el primer nodo real o trailer,
 * - trailer.prev es el último nodo real o header,
 * - para cada enlace u.next=v se cumple v.prev=u,
 * - n coincide con la cantidad de nodos reales.
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Semana03_DequeExtra {
    static class Deque<T> implements Iterable<T> {
        private static class Node<T> {
            T item;
            Node<T> prev;
            Node<T> next;

            Node(T item) {
                this.item = item;
            }
        }

        private final Node<T> header;
        private final Node<T> trailer;
        private int n;

        Deque() {
            header = new Node<>(null);
            trailer = new Node<>(null);
            header.next = trailer;
            trailer.prev = header;
        }

        int size() {
            return n;
        }

        boolean isEmpty() {
            return n == 0;
        }

        private void insertBetween(T item, Node<T> before, Node<T> after) {
            if (item == null) {
                throw new IllegalArgumentException("el elemento no puede ser nulo");
            }
            Node<T> node = new Node<>(item);
            node.prev = before;
            node.next = after;
            before.next = node;
            after.prev = node;
            n++;
        }

        private T removeNode(Node<T> node) {
            if (node == header || node == trailer) {
                throw new NoSuchElementException("el deque está vacío");
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
            n--;
            T item = node.item;
            node.item = null;
            node.prev = null;
            node.next = null;
            return item;
        }

        void addFirst(T item) {
            insertBetween(item, header, header.next);
        }

        // TODO(alumno): insertar antes del centinela trailer. Costo O(1).
        void addLast(T item) {
            throw new UnsupportedOperationException("TODO: implementar el método addLast");
        }

        T removeFirst() {
            return removeNode(header.next);
        }

        // TODO(alumno): retirar trailer.prev usando removeNode. Costo O(1).
        T removeLast() {
            throw new UnsupportedOperationException("TODO: implementar el método removeLast");
        }

        boolean checkInvariant() {
            if (header.prev != null || trailer.next != null) return false;
            if (header.next == null || trailer.prev == null) return false;
            if (header.next.prev != header || trailer.prev.next != trailer) return false;

            int count = 0;
            Node<T> current = header.next;
            Node<T> previous = header;
            while (current != trailer) {
                if (current == null || current.prev != previous) return false;
                previous = current;
                current = current.next;
                count++;
                if (count > n) return false;
            }
            return previous == trailer.prev && count == n;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                private Node<T> current = header.next;

                @Override
                public boolean hasNext() {
                    return current != trailer;
                }

                @Override
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("no quedan elementos");
                    }
                    T item = current.item;
                    current = current.next;
                    return item;
                }
            };
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder("[");
            for (T item : this) {
                if (out.length() > 1) out.append(", ");
                out.append(item);
            }
            return out.append(']').toString();
        }
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(20);
        deque.addFirst(10);
        System.out.println("Estado inicial: " + deque + " (esperado: [10, 20])");
        System.out.println("Invariante válido: " + deque.checkInvariant());

        try {
            deque.addLast(30);
            deque.addLast(40);
            System.out.println("Después de addLast: " + deque);
            System.out.println("removeFirst: " + deque.removeFirst());
            System.out.println("removeLast: " + deque.removeLast());
            System.out.println("Estado final: " + deque + " (esperado: [20, 30])");
            System.out.println("Invariante válido: " + deque.checkInvariant());
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
