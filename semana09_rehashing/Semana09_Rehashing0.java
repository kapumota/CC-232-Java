/*
 * CC-232 - Semana 9, lunes: factor de carga y nueva capacidad.
 * Basado en el redimensionamiento de ChainedHashTable de Pat Morin.
 */
public class Semana09_Rehashing0 {
    static double loadFactor(int size, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("La capacidad debe ser positiva");
        return (double) size / capacity;
    }

    // TODO(alumno): retornar una potencia de dos estrictamente mayor que size.
    // La capacidad mínima debe ser 2.
    static int capacityFor(int size) {
        throw new UnsupportedOperationException("TODO: implementar el método capacityFor");
    }

    static boolean shouldGrow(int size, int capacity) {
        return loadFactor(size + 1, capacity) > 1.0;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Factor de carga para 6 de 8: " + loadFactor(6, 8) + " (esperado: 0.75)");
            System.out.println("Resultado de capacityFor(8): " + capacityFor(8) + " (esperado: 16)");
            System.out.println("Resultado de shouldGrow(8, 8): " + shouldGrow(8, 8) + " (esperado: true)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
