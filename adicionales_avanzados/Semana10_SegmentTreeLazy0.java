/*
 * CC-232 - Semana 10, lunes: idea de Lazy Propagation.
 * Invariante: lazy[node] representa un incremento pendiente para todo el nodo.
 */
import java.util.Arrays;

public class Semana10_SegmentTreeLazy0 {
    // TODO(alumno): aplicar delta a un nodo que representa [left,right].
    // Actualizar tree[node] con delta*longitud y acumular lazy[node].
    static void apply(long[] tree, long[] lazy, int node,
                      int left, int right, long delta) {
        throw new UnsupportedOperationException("TODO: implementar el método apply");
    }

    // TODO(alumno): si lazy[node]!=0 y no es hoja, transferir el pendiente a
    // ambos hijos mediante apply. Al final, limpiar lazy[node].
    static void push(long[] tree, long[] lazy, int node, int left, int right) {
        throw new UnsupportedOperationException("TODO: implementar el método push");
    }

    public static void main(String[] args) {
        long[] tree = new long[16];
        long[] lazy = new long[16];
        tree[1] = 20; // suma del segmento [0,3]
        try {
            apply(tree, lazy, 1, 0, 3, 5);
            System.out.println("Valor de tree[1]: " + tree[1] + " (esperado: 40)");
            System.out.println("Valor de lazy[1]: " + lazy[1] + " (esperado: 5)");
            push(tree, lazy, 1, 0, 3);
            System.out.println("Arreglo de pendientes: " + Arrays.toString(Arrays.copyOf(lazy, 8)));
            System.out.println("El valor de lazy[1] es 0 y los valores de lazy[2] y lazy[3] son 5");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
