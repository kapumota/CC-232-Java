/*
 * CC-232 - Semana 2, viernes: DLList con nodo centinela.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante: dummy.next.prev==dummy y dummy.prev.next==dummy.
 */
public class Semana02_ListasEnlazadas1 {
    static class DLList {
        static class Node {
            int x;
            Node prev;
            Node next;

            Node(int x) {
                this.x = x;
            }
        }

        private final Node dummy = new Node(0);
        private int n;

        DLList() {
            dummy.next = dummy;
            dummy.prev = dummy;
        }

        int size() {
            return n;
        }

        // TODO(alumno): localizar el nodo i. Recorrer desde el extremo más
        // cercano. Precondición: 0 <= i < n.
        private Node getNode(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método getNode");
        }

        int get(int i) {
            return getNode(i).x;
        }

        private Node addBefore(Node w, int x) {
            Node u = new Node(x);
            u.prev = w.prev;
            u.next = w;
            u.next.prev = u;
            u.prev.next = u;
            n++;
            return u;
        }

        void add(int i, int x) {
            if (i < 0 || i > n) {
                throw new IndexOutOfBoundsException("posición=" + i + ", tamaño=" + n);
            }
            addBefore(i == n ? dummy : getNode(i), x);
        }

        // TODO(alumno): desconectar getNode(i), decrementar n y retornar x.
        int remove(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder("[");
            for (Node u = dummy.next; u != dummy; u = u.next) {
                if (out.length() > 1) out.append(", ");
                out.append(u.x);
            }
            return out.append("] tamaño=").append(n).toString();
        }
    }

    public static void main(String[] args) {
        DLList list = new DLList();
        try {
            list.add(0, 10);
            list.add(1, 30);
            list.add(1, 20);
            System.out.println(list + " (esperado: [10, 20, 30])");
            System.out.println("Resultado de get(1): " + list.get(1) + " (esperado: 20)");
            System.out.println("Resultado de remove(1): " + list.remove(1) + " (esperado: 20)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
