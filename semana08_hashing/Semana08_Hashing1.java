/*
 * CC-232 - Semana 8, viernes: tabla hash con encadenamiento separado.
 * Adaptación didáctica de ChainedHashTable de Pat Morin.
 *
 * Invariante: cada elemento se encuentra en el bucket indicada por hash(x)
 * y size coincide con el número total de elementos almacenados.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Semana08_Hashing1 {
    static class ChainedHashSet<T> {
        private final List<T>[] buckets;
        private int size;

        @SuppressWarnings({"unchecked", "rawtypes"})
        ChainedHashSet(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("La capacidad debe ser positiva");
            buckets = new ArrayList[capacity];
            for (int i = 0; i < capacity; i++) buckets[i] = new ArrayList<>();
        }

        private int hash(Object value) {
            return Math.floorMod(value.hashCode(), buckets.length);
        }

        int size() { return size; }

        boolean contains(Object value) {
            return buckets[hash(value)].contains(value);
        }

        boolean add(T value) {
            if (contains(value)) return false;
            buckets[hash(value)].add(value);
            size++;
            return true;
        }

        // TODO(alumno): buscar value en su bucket mediante Iterator, eliminarlo,
        // decrementar size y retornar true. Retornar false si no está presente.
        boolean remove(T value) {
            throw new UnsupportedOperationException("TODO: implementar el método remove");
        }

        void printBuckets() {
            for (int i = 0; i < buckets.length; i++) {
                System.out.println(i + ": " + buckets[i]);
            }
        }
    }

    public static void main(String[] args) {
        ChainedHashSet<String> set = new ChainedHashSet<>(5);
        set.add("arreglo");
        set.add("lista");
        set.add("heap");
        set.add("grafo");
        set.printBuckets();
        System.out.println("Resultado de contains para el heap: " + set.contains("heap") + " (esperado: true)");
        try {
            System.out.println("Resultado de remove para lista: " + set.remove("lista") + " (esperado: true)");
            System.out.println("Tamaño: " + set.size() + " (esperado: 3)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
