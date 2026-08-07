/*
 * CC-232 - Semana 13, viernes: sistema integrador.
 * Combina cola de prioridad, hashing y consultas acumuladas.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Semana13_Integracion1 {
    static class Ticket {
        final int id;
        final int priority;
        final int minutes;

        Ticket(int id, int priority, int minutes) {
            this.id = id;
            this.priority = priority;
            this.minutes = minutes;
        }

        @Override
        public String toString() {
            return "Solicitud{id=" + id + ", prioridad=" + priority + ", min=" + minutes + "}";
        }
    }

    static class ServiceSystem {
        private final List<Ticket> heap = new ArrayList<>();
        private final Set<Integer> served = new HashSet<>();
        private int totalMinutes;

        boolean addTicket(Ticket ticket) {
            if (served.contains(ticket.id)) return false;
            heap.add(ticket);
            bubbleUp(heap.size() - 1);
            return true;
        }

        private boolean less(Ticket a, Ticket b) {
            if (a.priority != b.priority) return a.priority < b.priority;
            return a.id < b.id;
        }

        private void swap(int i, int j) {
            Ticket x = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, x);
        }

        private void bubbleUp(int i) {
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (!less(heap.get(i), heap.get(parent))) break;
                swap(i, parent);
                i = parent;
            }
        }

        // TODO(alumno): extraer el mínimo, aplicar trickleDown, registrar el id
        // en served y acumular minutes. Retornar null si no hay tickets.
        Ticket serveNext() {
            throw new UnsupportedOperationException("TODO: implementar el método serveNext");
        }

        // TODO(alumno): restaurar el min-heap desde i hacia abajo.
        private void trickleDown(int i) {
            throw new UnsupportedOperationException("TODO: implementar el método trickleDown");
        }

        boolean wasServed(int id) { return served.contains(id); }
        int totalMinutes() { return totalMinutes; }
        int pending() { return heap.size(); }
    }

    public static void main(String[] args) {
        ServiceSystem system = new ServiceSystem();
        system.addTicket(new Ticket(101, 3, 12));
        system.addTicket(new Ticket(102, 1, 8));
        system.addTicket(new Ticket(103, 2, 15));
        try {
            System.out.println(system.serveNext() + " (se espera el identificador 102)");
            System.out.println(system.serveNext() + " (se espera el identificador 103)");
            System.out.println("Resultado de wasServed(102): " + system.wasServed(102) + " (esperado: true)");
            System.out.println("Minutos totales: " + system.totalMinutes() + " (esperado: 23)");
            System.out.println("Solicitudes pendientes: " + system.pending() + " (esperado: 1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
