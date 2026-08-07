/*
 * CC-232 - Semana 3, lunes: pila enlazada.
 * Basado en las operaciones push/pop de SLList de Pat Morin.
 */
public class Semana03_PilasColas0 {
    static class LinkedStack {
        static class Node {
            char x;
            Node next;
            Node(char x, Node next) { this.x = x; this.next = next; }
        }

        private Node head;
        private int n;

        int size() { return n; }
        boolean isEmpty() { return n == 0; }

        // TODO(alumno): insertar en la cabeza. Costo O(1).
        void push(char x) {
            throw new UnsupportedOperationException("TODO: implementar el método push");
        }

        // TODO(alumno): retirar y retornar la cabeza. Lanzar una excepción
        // si la pila está vacía. Costo O(1).
        char pop() {
            throw new UnsupportedOperationException("TODO: implementar el método pop");
        }
    }

    static String reverse(String text) {
        LinkedStack stack = new LinkedStack();
        for (char c : text.toCharArray()) stack.push(c);
        StringBuilder out = new StringBuilder();
        while (!stack.isEmpty()) out.append(stack.pop());
        return out.toString();
    }

    public static void main(String[] args) {
        try {
            System.out.println("Resultado de reverse para estructura: " + reverse("estructura"));
            System.out.println("Esperado: arutcurtse");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
