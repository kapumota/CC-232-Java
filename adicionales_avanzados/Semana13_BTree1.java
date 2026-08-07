/*
 * CC-232 - Semana 13, viernes: split e inserción en BTree simplificado.
 * Adaptación didáctica de BTree de Pat Morin, mantenida completamente en memoria.
 */
import java.util.Arrays;

public class Semana13_BTree1 {
    static class BTree {
        static class Node {
            final Integer[] keys;
            final Node[] children;

            Node(int b) {
                keys = new Integer[b];
                children = new Node[b + 1];
            }

            int size() {
                int i = 0;
                while (i < keys.length && keys[i] != null) i++;
                return i;
            }

            boolean isLeaf() { return children[0] == null; }
            boolean isFull() { return keys[keys.length - 1] != null; }
        }

        private final int b;
        private Node root;
        private int n;

        BTree(int b) {
            if (b < 3 || b % 2 == 0) throw new IllegalArgumentException("El valor de b debe ser impar y mayor o igual que 3");
            this.b = b;
            root = new Node(b);
        }

        private int findIt(Integer[] keys, int x) {
            int low = 0;
            int high = keys.length;
            while (low < high) {
                int middle = (low + high) / 2;
                int cmp = keys[middle] == null ? -1 : Integer.compare(x, keys[middle]);
                if (cmp < 0) high = middle;
                else if (cmp > 0) low = middle + 1;
                else return -middle - 1;
            }
            return low;
        }

        boolean contains(int x) {
            Node u = root;
            while (u != null) {
                int i = findIt(u.keys, x);
                if (i < 0) return true;
                u = u.children[i];
            }
            return false;
        }

        // TODO(alumno): insertar x recursivamente. Si un hijo devuelve un nodo
        // producido por split, subir su primera clave y enlazar el nuevo hijo.
        // Si la raíz se divide, crear una raíz nueva.
        boolean add(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método add");
        }

        // TODO(alumno): dividir u por la mitad. Mover las claves mayores y sus
        // hijos a un nuevo nodo, limpiar las posiciones movidas en u.
        private Node split(Node u) {
            throw new UnsupportedOperationException("TODO: implementar el método split");
        }

        int size() { return n; }

        void printRoot() {
            System.out.println("Raíz: " + Arrays.toString(root.keys));
            for (int i = 0; i < root.children.length; i++) {
                if (root.children[i] != null) {
                    System.out.println("  hijo " + i + ": "
                            + Arrays.toString(root.children[i].keys));
                }
            }
        }
    }

    public static void main(String[] args) {
        BTree tree = new BTree(5);
        try {
            for (int x : new int[] {10, 20, 5, 6, 12, 30, 7, 17}) tree.add(x);
            tree.printRoot();
            System.out.println("Resultado de contains(12): " + tree.contains(12) + " (esperado: true)");
            System.out.println("Resultado de contains(99): " + tree.contains(99) + " (esperado: false)");
            System.out.println("Tamaño: " + tree.size() + " (esperado: 8)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
