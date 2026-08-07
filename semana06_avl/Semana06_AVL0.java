/*
 * CC-232 - Semana 6, lunes: alturas y rotaciones AVL.
 * Implementación docente basada en la bibliografía de árboles AVL.
 * Invariante AVL: para cada nodo, |height(left)-height(right)| <= 1.
 */
public class Semana06_AVL0 {
    static class Node {
        int x;
        int height = 1;
        Node left;
        Node right;
        Node(int x) { this.x = x; }
    }

    static int height(Node u) {
        return u == null ? 0 : u.height;
    }

    static void updateHeight(Node u) {
        u.height = 1 + Math.max(height(u.left), height(u.right));
    }

    static int balanceFactor(Node u) {
        return u == null ? 0 : height(u.left) - height(u.right);
    }

    // TODO(alumno): realizar la rotación derecha alrededor de y, actualizar
    // primero la altura de y y luego la de x, y retornar la nueva raíz.
    static Node rotateRight(Node y) {
        throw new UnsupportedOperationException("TODO: implementar el método rotateRight");
    }

    // TODO(alumno): operación simétrica a rotateRight.
    static Node rotateLeft(Node x) {
        throw new UnsupportedOperationException("TODO: implementar el método rotateLeft");
    }

    static void preorder(Node u) {
        if (u == null) return;
        System.out.print(u.x + "(h=" + u.height + ") ");
        preorder(u.left);
        preorder(u.right);
    }

    public static void main(String[] args) {
        Node y = new Node(30);
        y.left = new Node(20);
        y.left.left = new Node(10);
        updateHeight(y.left);
        updateHeight(y);
        System.out.println("Factor de balance de 30: " + balanceFactor(y) + " (esperado: 2)");
        try {
            Node root = rotateRight(y);
            preorder(root);
            System.out.println("\nEsperado: 20(h=2) 10(h=1) 30(h=1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
