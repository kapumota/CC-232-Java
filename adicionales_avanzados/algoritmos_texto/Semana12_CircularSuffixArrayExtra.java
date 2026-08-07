/*
 * CC-232 - Semana 12, ampliación: arreglo de sufijos circulares.
 *
 * Reescritura didáctica inspirada en CircularSuffixArray de Algorithms,
 * Sedgewick y Wayne. Solo se ordenan índices, no se crean todas las
 * rotaciones como objetos String.
 *
 * Complejidad de esta versión introductoria:
 * O(n log n) comparaciones y hasta O(n) por comparación,
 * por lo tanto O(n^2 log n) en el peor caso.
 */
import java.util.Arrays;

public class Semana12_CircularSuffixArrayExtra {
    static class CircularSuffixArray {
        private final String text;
        private final Integer[] order;

        CircularSuffixArray(String text) {
            if (text == null) throw new IllegalArgumentException("el texto no puede ser nulo");
            this.text = text;
            order = new Integer[text.length()];
            for (int i = 0; i < order.length; i++) order[i] = i;
            Arrays.sort(order, this::compareRotations);
        }

        int length() {
            return text.length();
        }

        int index(int i) {
            if (i < 0 || i >= order.length) {
                throw new IllegalArgumentException("el índice debe pertenecer a [0, n)");
            }
            return order[i];
        }

        /*
         * TODO(alumno): comparar las rotaciones que comienzan en first y
         * second. Examinar text.charAt((inicio+k) % n) para k=0..n-1.
         */
        private int compareRotations(int first, int second) {
            throw new UnsupportedOperationException("TODO: implementar el método compareRotations");
        }

        String rotation(int start) {
            StringBuilder out = new StringBuilder(text.length());
            for (int k = 0; k < text.length(); k++) {
                out.append(text.charAt((start + k) % text.length()));
            }
            return out.toString();
        }
    }

    public static void main(String[] args) {
        try {
            CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
            for (int i = 0; i < csa.length(); i++) {
                int start = csa.index(i);
                System.out.printf("%2d -> %s%n", start, csa.rotation(start));
            }
            System.out.println("El primer índice esperado es 11");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
