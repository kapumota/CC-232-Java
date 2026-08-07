/*
 * CC-232 - Semana 2, lunes: SLList como pila y cola.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante: n==0 implica head==null y tail==null.
 */
public class Semana02_ListasEnlazadas0 {
    static class SLList {
        static class Node {
            int x;
            Node next;

            Node(int x) {
                this.x = x;
            }
        }

        private Node head;
        private Node tail;
        private int n;

        int size() {
            return n;
        }

        boolean add(int x) {
            Node u = new Node(x);
            if (n == 0) {
                head = u;
            } else {
                tail.next = u;
            }
            tail = u;
            n++;
            return true;
        }

        // TODO(alumno): insertar x en la cabeza y actualizar tail si la lista
        // estaba vacía. Retornar x. Costo O(1).
        int push(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método push");
        }

        // TODO(alumno): retirar la cabeza, retornar null si está vacía y
        // actualizar tail cuando se elimina el último nodo. Costo O(1).
        Integer pop() {
            throw new UnsupportedOperationException("TODO: implementar el método pop");
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder("[");
            for (Node u = head; u != null; u = u.next) {
                if (out.length() > 1) out.append(", ");
                out.append(u.x);
            }
            return out.append("] tamaño=").append(n).toString();
        }
    }

    public static void main(String[] args) {
        SLList list = new SLList();
        list.add(20);
        list.add(30);
        System.out.println("Como cola: " + list + " (esperado: [20, 30])");
        try {
            list.push(10);
            System.out.println("Después de push(10): " + list);
            System.out.println("Resultado de pop(): " + list.pop() + " (esperado: 10)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
