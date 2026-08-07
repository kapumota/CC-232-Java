/*
 * CC-232 - Semana 9, lunes: representación de Segment Tree.
 * Implementación docente de un algoritmo estándar.
 */
public class Semana09_SegmentTree0 {
    static int leftChild(int node) { return 2 * node; }
    static int rightChild(int node) { return 2 * node + 1; }

    // TODO(alumno): retornar la menor potencia de dos mayor o igual que n.
    static int nextPowerOfTwo(int n) {
        throw new UnsupportedOperationException("TODO: implementar el método nextPowerOfTwo");
    }

    // TODO(alumno): para índices desde 1, reservar posiciones 0..2*p-1,
    // donde p=nextPowerOfTwo(n). La longitud es 2*p, no 2*p-1.
    static int storageLength(int n) {
        throw new UnsupportedOperationException("TODO: implementar el método storageLength");
    }

    public static void main(String[] args) {
        try {
            System.out.println("Hijos del nodo 3: " + leftChild(3) + ", " + rightChild(3)
                    + " (esperado: 6, 7)");
            System.out.println("Resultado de nextPowerOfTwo(6): " + nextPowerOfTwo(6) + " (esperado: 8)");
            System.out.println("Resultado de storageLength(6): " + storageLength(6) + " (esperado: 16)");
            System.out.println("Se usan 15 nodos, pero el arreglo 1-indexado requiere longitud 16.");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
