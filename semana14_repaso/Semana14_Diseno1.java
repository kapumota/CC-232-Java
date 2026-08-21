/*
 * CC-232 - Semana 14, lunes: diseño integrador.
 * Repaso final de selección de estructuras e invariantes coordinados.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Semana14_Diseno1 {
    static class Job {
        final int id;
        final int priority;
        final String owner;
        final int minutes;

        Job(int id, int priority, String owner, int minutes) {
            this.id = id;
            this.priority = priority;
            this.owner = owner;
            this.minutes = minutes;
        }

        @Override
        public String toString() {
            return "Trabajo{id=" + id + ", prioridad=" + priority
                    + ", responsable=" + owner + ", min=" + minutes + "}";
        }
    }

    static class JobSystem {
        private final List<Job> heap = new ArrayList<>();
        private final Set<Integer> pendingIds = new HashSet<>();
        private final Map<String, Integer> jobsByOwner = new HashMap<>();
        private int totalPendingMinutes;

        private boolean less(Job a, Job b) {
            if (a.priority != b.priority) return a.priority < b.priority;
            return a.id < b.id;
        }

        private void swap(int i, int j) {
            Job tmp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, tmp);
        }

        private void bubbleUp(int i) {
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (!less(heap.get(i), heap.get(parent))) return;
                swap(i, parent);
                i = parent;
            }
        }

        // TODO(alumno): agregar solo si el id no está pendiente y mantener
        // heap, pendingIds, jobsByOwner y totalPendingMinutes consistentes.
        boolean addJob(Job job) {
            throw new UnsupportedOperationException("TODO: implementar addJob");
        }

        // TODO(alumno): retirar el trabajo de mayor precedencia, restaurar
        // el heap y actualizar todas las estructuras auxiliares.
        Job removeNext() {
            throw new UnsupportedOperationException("TODO: implementar removeNext");
        }

        // TODO(alumno): restaurar el min-heap desde i hacia abajo.
        private void trickleDown(int i) {
            throw new UnsupportedOperationException("TODO: implementar trickleDown");
        }

        boolean containsPendingId(int id) { return pendingIds.contains(id); }
        int jobsForOwner(String owner) { return jobsByOwner.getOrDefault(owner, 0); }
        int pending() { return heap.size(); }
        int totalPendingMinutes() { return totalPendingMinutes; }
    }

    public static void main(String[] args) {
        JobSystem system = new JobSystem();
        try {
            System.out.println("Agregar 201: " + system.addJob(new Job(201, 3, "ire", 12)) + " (esperado: true)");
            System.out.println("Agregar 202: " + system.addJob(new Job(202, 1, "chalo", 8)) + " (esperado: true)");
            System.out.println("Agregar 203: " + system.addJob(new Job(203, 2, "ire", 15)) + " (esperado: true)");
            System.out.println("Duplicar 202: " + system.addJob(new Job(202, 4, "paulette", 20)) + " (esperado: false)");
            System.out.println("Pendientes de ire: " + system.jobsForOwner("ire") + " (esperado: 2)");
            System.out.println("Minutos pendientes: " + system.totalPendingMinutes() + " (esperado: 35)");
            System.out.println("Atender: " + system.removeNext() + " (se espera id 202)");
            System.out.println("Atender: " + system.removeNext() + " (se espera id 203)");
            System.out.println("¿202 sigue pendiente?: " + system.containsPendingId(202) + " (esperado: false)");
            System.out.println("Pendientes de ire: " + system.jobsForOwner("ire") + " (esperado: 1)");
            System.out.println("Minutos pendientes: " + system.totalPendingMinutes() + " (esperado: 12)");
            System.out.println("Cantidad pendiente: " + system.pending() + " (esperado: 1)");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}
