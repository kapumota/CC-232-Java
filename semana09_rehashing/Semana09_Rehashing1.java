/*
 * CC-232 - Semana 9, viernes: rehashing: ChainedHashTable y redimensionamiento.
 * Adaptación didáctica de Open Data Structures, Pat Morin.
 * Invariante: t.length==2^d y n es la suma de tamaños de los buckets.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Semana09_Rehashing1 {
    static class ChainedHashTable<T> {
        private List<T>[] t;
        private int d;
        private int n;
        private final int z = 0x9E3779B9;
        private static final int W = 32;

        ChainedHashTable() {
            d = 3; // ocho buckets para que el main no dependa todavía de resize
            t = allocateTable(1 << d);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private List<T>[] allocateTable(int size) {
            List<T>[] table = new ArrayList[size];
            for (int i = 0; i < size; i++) table[i] = new ArrayList<>();
            return table;
        }

        private int hash(Object x) {
            return (z * x.hashCode()) >>> (W - d);
        }

        int size() { return n; }

        T find(Object x) {
            for (T y : t[hash(x)]) {
                if (y.equals(x)) return y;
            }
            return null;
        }

        boolean add(T x) {
            if (find(x) != null) return false;
            if (n + 1 > t.length) resize();
            t[hash(x)].add(x);
            n++;
            return true;
        }

        // TODO(alumno): recorrer con Iterator el bucket de x, eliminar la
        // coincidencia, decrementar n y retornar el objeto almacenado.
        T remove(T x) {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        // TODO(alumno): elegir d con 2^d > n, crear nuevos buckets y reinsertar
        // todos los elementos sin perder ni duplicar el contador n.
        private void resize() {
            throw new UnsupportedOperationException("TODO: implementar el método resize");
        }

        void printBuckets() {
            for (int i = 0; i < t.length; i++) {
                System.out.println(i + ": " + t[i]);
            }
        }
    }

    public static void main(String[] args) {
        ChainedHashTable<String> table = new ChainedHashTable<>();
        table.add("arreglo");
        table.add("lista");
        table.add("heap");
        table.add("tabla hash");
        table.printBuckets();
        System.out.println("Resultado de find parael heap: " + table.find("heap") + " (esperado: heap)");
        try {
            System.out.println("Resultado de remove para lista: " + table.remove("lista") + " (esperado: lista)");
            System.out.println("Tamaño: " + table.size() + " (esperado: 3)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
