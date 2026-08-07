/*
 * CC-232 - Semana 4, lunes: búsqueda e inserción en un BST.
 * Adaptación didáctica de BinarySearchTree de Pat Morin.
 */
public class Semana04_BST0 {
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

        int size() { return n; }

        // TODO(alumno): seguir el camino de búsqueda y retornar el último nodo
        // visitado, null cuando el árbol está vacío.
        private Node findLast(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método findLast");
        }

        boolean contains(int x) {
            Node p = findLast(x);
            return p != null && p.x == x;
        }

        // TODO(alumno): usar findLast y enlazar un nuevo nodo como hijo de p.
        // Rechazar duplicados y mantener parent y n.
        boolean add(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método add");
        }

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
        try {
            for (int x : new int[] {40, 20, 60, 10, 30, 50, 70}) tree.add(x);
            tree.inorder();
            System.out.println("Esperado: 10 20 30 40 50 60 70");
            System.out.println("Resultado de contains(30): " + tree.contains(30) + " (esperado: true)");
            System.out.println("Tamaño: " + tree.size() + " (esperado: 7)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
