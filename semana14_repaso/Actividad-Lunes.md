### Actividad de la Semana 14 - Lunes

Esta actividad corresponde al repaso final de selección de estructuras e invariantes coordinados.

Se trabajará con `Semana14_Repaso0.java` como calentamiento y `Semana14_Diseno1.java` como actividad principal de codificación.

#### 1. Antes de programar

En `Semana14_Diseno1.java` se mantiene:

```text
List<Job> heap
Set<Integer> pendingIds
Map<String,Integer> jobsByOwner
int totalPendingMinutes
```

Para cada estado responde qué operación favorece, qué información representa y qué invariante debe conservar.

| Estado | Responsabilidad | Invariante |
|---|---|---|
| `heap` | | |
| `pendingIds` | | |
| `jobsByOwner` | | |
| `totalPendingMinutes` | | |

#### 2. `addJob(...)`

Antes de programar responde:

1. ¿qué debe ocurrir si `job.id` ya está en `pendingIds`?
2. ¿qué debe cambiar en `jobsByOwner`?
3. ¿qué debe ocurrir con `totalPendingMinutes`?
4. ¿qué invariante restaura `bubbleUp(...)`?
5. ¿cuál es la complejidad dominante?

Implementa el TODO y verifica primero las cuatro inserciones del `main()`.

#### 3. `trickleDown(...)`

Recuerda:

```text
left  = 2*i + 1
right = 2*i + 2
```

Responde por qué deben compararse ambos hijos, cuándo se detiene el algoritmo y cuál es su costo.

Implementa el TODO sin utilizar `PriorityQueue`.

#### 4. `removeNext(...)`

Diseña antes el pseudocódigo para:

```text
heap vacío
heap de tamaño 1
heap con más de un elemento
actualización de pendingIds
actualización de jobsByOwner
actualización de totalPendingMinutes
restauración del min-heap
```

Luego implementa el TODO.

#### 5. Casos borde obligatorios

Agrega pruebas para:

```text
removeNext() sobre sistema vacío
un único trabajo
dos trabajos con igual priority y distinto id
dos trabajos del mismo owner
intento de duplicar un id pendiente
eliminar el último trabajo de un owner
```

Escribe primero el resultado esperado.

#### 6. Debugging estructural

Analiza sin ejecutar:

```java
Job removeNext() {
    Job result = heap.get(0);
    heap.set(0, heap.remove(heap.size() - 1));
    trickleDown(0);
    return result;
}
```

1. ¿qué ocurre con un heap de tamaño 1?
2. ¿qué estados auxiliares no se actualizan?
3. ¿por qué el heap podría quedar correcto y el sistema global incorrecto?
4. diseña una prueba mínima para cada defecto.

#### 7. Complejidad final

Completa:

| Operación | Costo dominante | Tipo de cota |
|---|---:|---|
| `containsPendingId` | | |
| `jobsForOwner` | | |
| `addJob` | | |
| `removeNext` | | |
| `pending` | | |
| `totalPendingMinutes` | | |

Distingue peor caso, esperado y amortizado cuando corresponda.

#### 8. Cierre

Explica por qué el sistema no se resuelve adecuadamente usando solamente uno de estos elementos:

```text
ArrayList
HashSet
HashMap
BinaryHeap
```
