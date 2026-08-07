/*
 * CC-232 - Semana 12, viernes: búsqueda y LCP sobre un Suffix Array.
 * Construcción simple para concentrar la sesión en las consultas.
 */
import java.util.Arrays;
import java.util.Comparator;

public class Semana12_SuffixArray1 {
    static class SuffixArray {
        private final String text;
        private final int[] sa;

        SuffixArray(String text) {
            this.text = text;
            Integer[] indices = new Integer[text.length()];
            for (int i = 0; i < indices.length; i++) indices[i] = i;
            Arrays.sort(indices, Comparator.comparing(text::substring));
            sa = new int[indices.length];
            for (int i = 0; i < sa.length; i++) sa[i] = indices[i];
        }

        // TODO(alumno): usar búsqueda binaria sobre sa. Comparar pattern con el
        // prefijo del sufijo sin construir todos los sufijos en un arreglo nuevo.
        boolean contains(String pattern) {
            throw new UnsupportedOperationException("TODO: implementar el método contains");
        }

        // TODO(alumno): lcp[i] es la longitud del prefijo común entre los
        // sufijos sa[i] y sa[i+1]. Implementación directa O(n^2) aceptada aquí.
        int[] buildLcp() {
            throw new UnsupportedOperationException("TODO: implementar el método buildLcp");
        }

        int[] indices() {
            return Arrays.copyOf(sa, sa.length);
        }
    }

    public static void main(String[] args) {
        SuffixArray suffixes = new SuffixArray("banana");
        System.out.println("Arreglo de sufijos: " + Arrays.toString(suffixes.indices()));
        try {
            System.out.println("Resultado de contains para ana: " + suffixes.contains("ana") + " (esperado: true)");
            System.out.println("Resultado de contains para app: " + suffixes.contains("app") + " (esperado: false)");
            System.out.println("Arreglo de prefijos comunes: " + Arrays.toString(suffixes.buildLcp()));
            System.out.println("Esperado: [1, 3, 0, 0, 2]");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
