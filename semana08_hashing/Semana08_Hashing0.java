/*
 * CC-232 - Semana 8, lunes: hashing multiplicativo.
 * Adaptación didáctica de ChainedHashTable de Pat Morin.
 */
public class Semana08_Hashing0 {
    static final int W = 32;
    static final int Z = 0x9E3779B9; // entero impar fijo para reproducibilidad

    // TODO(alumno): calcular ((Z * hashCode) mod 2^W) div 2^(W-d).
    // En Java se obtiene con desplazamiento lógico >>>.
    static int hash(int hashCode, int d) {
        throw new UnsupportedOperationException("TODO: implementar el método hash");
    }

    // TODO(alumno): retornar el menor d>=1 tal que 2^d > n.
    static int dimensionFor(int n) {
        throw new UnsupportedOperationException("TODO: implementar el método dimensionFor");
    }

    public static void main(String[] args) {
        try {
            int d = 3;
            for (String key : new String[] {"AVL", "montículo", "tabla hash", "BTree"}) {
                int index = hash(key.hashCode(), d);
                System.out.println(key + " -> " + index + " (rango esperado: 0..7)");
            }
            System.out.println("dimensionFor(8)=" + dimensionFor(8) + " (esperado: 4)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
