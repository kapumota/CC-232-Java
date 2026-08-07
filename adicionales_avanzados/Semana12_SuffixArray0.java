/*
 * CC-232 - Semana 12, lunes: orden lexicográfico de sufijos.
 * Implementación introductoria O(n^2 log n), no SA-IS.
 */
import java.util.Arrays;

public class Semana12_SuffixArray0 {
    // TODO(alumno): crear los índices 0..text.length-1 y ordenarlos comparando
    // text.substring(i). Esta versión prioriza claridad, no eficiencia.
    static int[] buildSuffixArray(String text) {
        throw new UnsupportedOperationException("TODO: implementar el método buildSuffixArray");
    }

    static void printSuffixes(String text, int[] suffixArray) {
        for (int index : suffixArray) {
            System.out.println(index + ": " + text.substring(index));
        }
    }

    public static void main(String[] args) {
        String text = "banana";
        try {
            int[] sa = buildSuffixArray(text);
            System.out.println("Arreglo de sufijos: " + Arrays.toString(sa));
            System.out.println("Esperado: [5, 3, 1, 0, 4, 2]");
            printSuffixes(text, sa);
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
