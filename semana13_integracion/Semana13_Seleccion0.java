/*
 * CC-232 - Semana 13, lunes: selección de estructuras e invariantes.
 * Cada método exige elegir una estructura ya estudiada y justificar su costo.
 */
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class Semana13_Seleccion0 {
    // TODO(alumno): verificar balanceo de (), [] y {} mediante una pila.
    static boolean balanced(String expression) {
        throw new UnsupportedOperationException("TODO: implementar el método balanced");
    }

    // TODO(alumno): detectar el primer duplicado mediante hashing esperado O(n).
    // Retornar null si todos los valores son distintos.
    static Integer firstDuplicate(int[] values) {
        throw new UnsupportedOperationException("TODO: implementar el método firstDuplicate");
    }

    // TODO(alumno): retornar el k-ésimo menor usando un min-heap propio o una
    // estrategia explicada en clase. No ordenar todo el arreglo.
    static int kthSmallest(int[] values, int k) {
        throw new UnsupportedOperationException("TODO: implementar el método kthSmallest");
    }

    public static void main(String[] args) {
        try {
            System.out.println("Resultado de balanced para {[()]}: " + balanced("{[()]}") + " (esperado: true)");
            System.out.println("Resultado de balanced para ([)]: " + balanced("([)]") + " (esperado: false)");
            System.out.println("Primer duplicado: "
                    + firstDuplicate(new int[] {4, 1, 7, 2, 7, 4}) + " (esperado: 7)");
            System.out.println("3.er menor="
                    + kthSmallest(new int[] {9, 1, 8, 2, 7, 3}, 3) + " (esperado: 3)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
