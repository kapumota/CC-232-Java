/*
 * CC-232 - Semana 6, viernes: inserción y rebalanceo AVL.
 * Implementación docente. Los cuatro casos son LL, RR, LR y RL.
 */
public class Semana06_AVL1 {
    static class AVL {
        static class Node {
            int x;
            int height = 1;
            Node left;
            Node right;
            Node(int x) { this.x = x; }
        }

        private Node root;

        private int height(Node u) { return u == null ? 0 : u.height; }
        private int balanceFactor(Node u) {
            return u == null ? 0 : height(u.left) - height(u.right);
        }
        private void updateHeight(Node u) {
            u.height = 1 + Math.max(height(u.left), height(u.right));
        }

        private Node rotateRight(Node y) {
            Node x = y.left;
            Node middle = x.right;
            x.right = y;
            y.left = middle;
            updateHeight(y);
            updateHeight(x);
            return x;
        }

        private Node rotateLeft(Node x) {
            Node y = x.right;
            Node middle = y.left;
            y.left = x;
            x.right = middle;
            updateHeight(x);
            updateHeight(y);
            return y;
        }

        void add(int x) {
            root = add(root, x);
        }

        private Node add(Node u, int x) {
            if (u == null) return new Node(x);
            if (x < u.x) u.left = add(u.left, x);
            else if (x > u.x) u.right = add(u.right, x);
            else return u;
            updateHeight(u);
            return rebalance(u);
        }

        // TODO(alumno): usar balanceFactor y reconocer LL, RR, LR y RL.
        // Retornar la nueva raíz local después de la rotación necesaria.
        private Node rebalance(Node u) {
            throw new UnsupportedOperationException("TODO: implementar el método rebalance");
        }

        void preorder() {
            preorder(root);
            System.out.println();
        }

        private void preorder(Node u) {
            if (u == null) return;
            System.out.print(u.x + "(fb=" + balanceFactor(u) + ") ");
            preorder(u.left);
            preorder(u.right);
        }
    }

    public static void main(String[] args) {
        int[][] cases = {
            {30, 20, 10}, // LL
            {10, 20, 30}, // RR
            {30, 10, 20}, // LR
            {10, 30, 20}  // RL
        };
        for (int[] values : cases) {
            AVL tree = new AVL();
            try {
                for (int x : values) tree.add(x);
                tree.preorder();
                System.out.println("La raíz esperada es 20");
            } catch (UnsupportedOperationException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }
}
