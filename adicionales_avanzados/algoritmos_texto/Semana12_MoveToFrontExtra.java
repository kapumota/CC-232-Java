/*
 * CC-232 - Semana 12, ampliación: codificación Move-to-Front.
 *
 * Reescritura didáctica inspirada en MoveToFront de Algorithms,
 * Sedgewick y Wayne. Opera sobre texto y posiciones enteras para evitar
 * dependencias de entrada/salida binaria.
 */
import java.util.Arrays;

public class Semana12_MoveToFrontExtra {
    private static final int ALPHABET_SIZE = 256;

    private static char[] initialAlphabet() {
        char[] alphabet = new char[ALPHABET_SIZE];
        for (int i = 0; i < alphabet.length; i++) alphabet[i] = (char) i;
        return alphabet;
    }

    private static void moveToFront(char[] alphabet, int position) {
        char value = alphabet[position];
        for (int i = position; i > 0; i--) alphabet[i] = alphabet[i - 1];
        alphabet[0] = value;
    }

    static int[] encode(String text) {
        if (text == null) throw new IllegalArgumentException("el texto no puede ser nulo");
        char[] alphabet = initialAlphabet();
        int[] output = new int[text.length()];

        for (int i = 0; i < text.length(); i++) {
            char value = text.charAt(i);
            if (value >= ALPHABET_SIZE) {
                throw new IllegalArgumentException("esta versión admite caracteres de 8 bits");
            }
            int position = 0;
            while (alphabet[position] != value) position++;
            output[i] = position;
            moveToFront(alphabet, position);
        }
        return output;
    }

    // TODO(alumno): recuperar cada carácter a partir de su posición y moverlo
    // al frente antes de procesar la siguiente posición.
    static String decode(int[] positions) {
        throw new UnsupportedOperationException("TODO: implementar el método decode");
    }

    public static void main(String[] args) {
        String text = "banana";
        int[] encoded = encode(text);
        System.out.println("Texto: " + text);
        System.out.println("Posiciones de Move-to-Front: " + Arrays.toString(encoded));

        try {
            System.out.println("Texto reconstruido: " + decode(encoded));
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
