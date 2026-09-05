/*
 * CC-232 - Semana 12, ampliación: transformación de Burrows-Wheeler.
 *
 * Reescritura didáctica inspirada en el proyecto Burrows-Wheeler de
 * Algorithms, Sedgewick y Wayne. Trabaja con String y arreglos normales,
 * no requiere BinaryStdIn, BinaryStdOut ni algs4.jar.
 */
import java.util.Arrays;

public class Semana12_BurrowsWheelerExtra {
    static final class Result {
        private final int first;
        private final String lastColumn;

        Result(int first, String lastColumn) {
            this.first = first;
            this.lastColumn = lastColumn;
        }

        int first() {
            return first;
        }

        String lastColumn() {
            return lastColumn;
        }

        @Override
        public String toString() {
            return "Índice inicial: " + first + ", última columna=\"" + lastColumn + "\"";
        }
    }

    static Result transform(String text) {
        if (text == null) throw new IllegalArgumentException("el texto no puede ser nulo");
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        Arrays.sort(order, (first, second) -> compareRotations(text, first, second));
        int originalRow = 0;
        StringBuilder last = new StringBuilder(n);

        for (int row = 0; row < n; row++) {
            int start = order[row];
            if (start == 0) originalRow = row;
            int previous = (start - 1 + n) % n;
            last.append(text.charAt(previous));
        }
        return new Result(originalRow, last.toString());
    }

    private static int compareRotations(String text, int first, int second) {
        for (int k = 0; k < text.length(); k++) {
            char a = text.charAt((first + k) % text.length());
            char b = text.charAt((second + k) % text.length());
            if (a != b) return Character.compare(a, b);
        }
        return 0;
    }

    /*
     * TODO(alumno): reconstruir el texto mediante ordenamiento estable por
     * conteo. Construir firstColumn y next, iniciar en result.first() y
     * seguir next durante n pasos.
     *
     * Pista: para un alfabeto de 256 símbolos se necesita count[257].
     */
    static String inverseTransform(Result result) {
        throw new UnsupportedOperationException("TODO: implementar el método inverseTransform");
    }

    public static void main(String[] args) {
        String text = "ABRACADABRA!";
        Result result = transform(text);
        System.out.println("Texto original: " + text);
        System.out.println("Resultado de la transformación: " + result);
        System.out.println("Esperado, índice inicial 3 y última columna \"ARD!RCAAAABB\"");

        try {
            System.out.println("Reconstrucción: " + inverseTransform(result));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
