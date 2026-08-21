### Actividad de la Semana 14 - Viernes

Esta actividad cierra el curso integrando `Map`, listas, `Graph`, BFS, DFS/Stack y estado auxiliar.

Se trabajará con `Semana14_Repaso1.java` como calentamiento y `Semana14_Integracion2.java` como actividad principal.

#### 1. Calentamiento

En `Semana14_Repaso1.java` identifica:

1. representación del grafo,
2. estructura FIFO,
3. significado de `distance[v] == -1`,
4. invariante de la Queue,
5. justificación de `O(V+E)`.

#### 2. Nombres externos e ids internos

Formula los invariantes:

```text
nameToId.get(idToName.get(i)) == i
idToName.size() == adjacency.size()
para u--v:
    v aparece en adjacency[u]
    u aparece en adjacency[v]
```

Explica por qué el `Map` no sustituye al grafo.

#### 3. `shortestDistance(...)`

Implementa BFS para:

```text
source inexistente -> -1
target inexistente -> -1
source == target -> 0
target no alcanzable -> -1
caso alcanzable -> mínimo número de aristas
```

Antes de programar explica por qué Queue/FIFO es la política apropiada y por qué marcar al descubrir evita encolados repetidos.

#### 4. `shortestPath(...)`

Mantén:

```text
parent[v]
```

como el vértice desde el cual `v` fue descubierto.

Reconstruye:

```text
target -> parent[target] -> ... -> source
```

y luego invierte el orden. Puedes usar `Deque<Integer>` como Stack temporal.

Prueba:

```text
source == target
nombre inexistente
target no alcanzable
dos caminos de longitudes distintas
```

#### 5. `connectedGroups(...)`

Recorre todos los ids.

```text
si id no está visto
    inicia un recorrido
    recoge exactamente una componente
```

Puedes usar BFS o DFS iterativo.

Explica:

1. por qué `seen` debe vivir fuera del bucle exterior,
2. por qué un aislado forma una componente,
3. por qué el costo global es `O(V+E)`.

#### 6. Casos de prueba

Agrega pruebas para:

```text
una sola persona
dos personas desconectadas
ciclo
vértice aislado
dos componentes
source == target
nombre inexistente
dos caminos de distinta longitud
```

Escribe primero el resultado esperado.

#### 7. Debugging

Analiza:

```java
while (!queue.isEmpty()) {
    int u = queue.remove();
    for (int v : adjacency.get(u)) {
        if (distance[v] == -1) {
            queue.add(v);
        }
    }
    distance[u] = distance[u] + 1;
}
```

1. identifica al menos dos errores,
2. construye un grafo mínimo donde un vecino se encole varias veces,
3. explica qué debe establecerse al descubrir un vecino,
4. escribe el orden conceptual correcto.

#### 8. Comparación BFS/DFS

Completa:

| Pregunta | BFS | DFS | Ambos | Ninguno por sí solo |
|---|---|---|---|---|
| alcanzabilidad | | | | |
| menor número de aristas | | | | |
| componentes conexas | | | | |
| exploración por niveles | | | | |
| profundizar antes de regresar | | | | |
| reconocer camino activo con tres colores | | | | |

#### 9. Cierre

Explica cómo el archivo reutiliza ideas de las Semanas 1, 3, 10, 11, 12 y 13 y conéctalas con representación, invariante, algoritmo y complejidad.
