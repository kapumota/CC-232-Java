/*
 * CC-232 - Semana 13, lunes: nodo de BTree con arreglos de capacidad fija.
 * Adaptación didáctica de BTree de Pat Morin, sin BlockStore.
 */
import java.util.Arrays;

public class Semana13_BTree0 {
    static class Node {
        private final Integer[] keys;
        private final Node[] children;

        Node(int b) {
            if (b < 3 || b % 2 == 0) {
                throw new IllegalArgumentException("El valor de b debe ser impar y mayor o igual que 3");
            }
            keys = new Integer[b];       // incluye una posición de desborde
            children = new Node[b + 1];
        }

        // TODO(alumno): búsqueda binaria en un arreglo ordenado con null al
        // final. Retornar i para insertar o -i-1 cuando keys[i]==x.
        int findIt(int x) {
            throw new UnsupportedOperationException("TODO: implementar el método findIt");
        }

        int size() {
            int low = 0;
            int high = keys.length;
            while (low < high) {
                int middle = (low + high) / 2;
                if (keys[middle] == null) high = middle;
                else low = middle + 1;
            }
            return low;
        }

        boolean isFull() {
            return keys[keys.length - 1] != null;
        }

        // TODO(alumno): desplazar claves e hijos e insertar x. Retornar false
        // si x ya existe. child queda a la derecha de la nueva clave.
        boolean add(int x, Node child) {
            throw new UnsupportedOperationException("TODO: implementar el método add");
        }

        @Override
        public String toString() {
            return Arrays.toString(keys);
        }
    }

    public static void main(String[] args) {
        Node node = new Node(5);
        try {
            node.add(10, null);
            node.add(30, null);
            node.add(20, null);
            System.out.println(node + " (esperado: [10, 20, 30, null, null])");
            System.out.println("Resultado de findIt(20): " + node.findIt(20) + " (esperado: -2)");
            System.out.println("Resultado de findIt(25): " + node.findIt(25) + " (esperado: 2)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
