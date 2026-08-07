/*
 * CC-232 - Semana 5: eliminación y casos borde: eliminación en BinarySearchTree.
 * Adaptación didáctica de BinarySearchTree de Pat Morin.
 */
public class Semana05_BSTEliminacion1 {
    static class BinarySearchTree {
        static class Node {
            int x;
            Node left;
            Node right;
            Node parent;
            Node(int x) { this.x = x; }
        }

        private Node root;
        private int n;

        private Node findLast(int x) {
            Node w = root;
            Node prev = null;
            while (w != null) {
                prev = w;
                if (x < w.x) w = w.left;
                else if (x > w.x) w = w.right;
                else return w;
            }
            return prev;
        }

        boolean add(int x) {
            Node p = findLast(x);
            if (p == null) {
                root = new Node(x);
            } else if (x < p.x) {
                p.left = new Node(x);
                p.left.parent = p;
            } else if (x > p.x) {
                p.right = new Node(x);
                p.right.parent = p;
            } else {
                return false;
            }
            n++;
            return true;
        }

        // TODO(alumno): eliminar u suponiendo que tiene a lo más un hijo.
        // Reconectar el hijo con u.parent y actualizar root cuando corresponda.
        private void splice(Node u) {
            throw new UnsupportedOperationException("TODO: implementar el método splice");
        }

        // TODO(alumno): localizar x. Si tiene dos hijos, copiar el sucesor
        // inorder y aplicar splice al sucesor. Decrementar n una sola vez.
        boolean remove(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        int size() { return n; }

        void inorder() {
            inorder(root);
            System.out.println();
        }

        private void inorder(Node u) {
            if (u == null) return;
            inorder(u.left);
            System.out.print(u.x + " ");
            inorder(u.right);
        }
    }

    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        for (int x : new int[] {40, 20, 60, 10, 30, 50, 70, 55}) tree.add(x);
        System.out.print("Inicial: ");
        tree.inorder();
        try {
            System.out.println("Resultado de remove(10): " + tree.remove(10) + " (hoja)");
            System.out.println("Resultado de remove(50): " + tree.remove(50) + " (un hijo)");
            System.out.println("Resultado de remove(60): " + tree.remove(60) + " (dos hijos)");
            tree.inorder();
            System.out.println("Esperado: 20 30 40 55 70");
            System.out.println("Tamaño: " + tree.size() + " (esperado: 5)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
