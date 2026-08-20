### Ejercicios de la Semana 12

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La sección de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 12.

El objetivo no es repetir la actividad de clase ni volver a resolver exactamente los mismos ejemplos utilizados para introducir:

```text
dfsOrder(...)
dfsVisit(...)
WHITE
GRAY
BLACK
markComponent(...)
countConnectedComponents(...)
```

Tampoco se busca volver a pedir únicamente:

```text
copiar la implementación de dfsVisit(...)
trazar otra vez el grafo del archivo de clase
obtener nuevamente [0, 1, 3, 2, 4, 5]
copiar countConnectedComponents(...)
obtener nuevamente 4 componentes
```

La mayoría de los problemas exige:

```text
razonar antes de ejecutar
formular invariantes
diseñar contraejemplos
detectar código que compila pero es incorrecto
comparar BFS y DFS
comparar Queue y Stack
razonar sobre WHITE/GRAY/BLACK
analizar recursión y pila de llamadas
distinguir descubrimiento de finalización
analizar componentes conexas
distinguir correctitud de eficiencia
combinar Graph con Map, Set, arreglos y Deque
diseñar pruebas
justificar O(V+E) global
```

Los temas centrales utilizados son:

```text
Graph y lista de adyacencia
source y alcanzabilidad
BFS frente a DFS
Queue/FIFO
Stack/LIFO
DFS recursivo
pila de llamadas
retroceso
WHITE -> GRAY -> BLACK
invariante de color
vecinos WHITE
ciclos
orden de descubrimiento
orden de finalización
DFS iterativo
Deque/push/pop
seen
componentes conexas
vértices aislados
markComponent
countConnectedComponents
O(V+E)
espacio auxiliar O(V)
```

También se reutilizan ideas anteriores:

```text
Semana 1
    boolean[]
    int[]
    acceso por índice

Semana 2
    listas y recorridos

Semana 3
    Queue
    Stack
    Deque
    FIFO
    LIFO

Semana 7
    Priority Queue como contraste de política

Semana 10
    Map
    Set
    HashMap
    HashSet
    TreeSet
    pertenencia

Semana 11
    Graph
    lista de adyacencia
    BFS
    alcanzabilidad
```

No se requiere estudiar ni implementar:

```text
ordenamiento topológico
componentes fuertemente conexas
Kosaraju
Tarjan
bridges
puntos de articulación
componentes biconexas
Prim
Kruskal
Dijkstra
Bellman-Ford
Floyd-Warshall
A*
flujos
```

### A. Consolidación esencial

#### Ejercicio 1. Mismo Graph, dos políticas de recorrido

Se tiene el grafo dirigido:

```text
0 -> [1, 2]
1 -> [3, 4]
2 -> [4]
3 -> [5]
4 -> [5]
5 -> []
```

Sin ejecutar código:

1. dibuja el grafo lógico,
2. realiza un BFS desde `0`,
3. registra la Queue después de cada extracción,
4. realiza un DFS recursivo desde `0`,
5. registra la pila de llamadas activa en cada descenso,
6. indica el orden de descubrimiento de BFS,
7. indica un orden de descubrimiento DFS compatible con las listas dadas,
8. indica un posible orden de finalización DFS,
9. identifica en qué momento `4` puede ser alcanzado por más de una ruta,
10. explica por qué no debe procesarse dos veces,
11. explica qué estructura auxiliar determina el orden de BFS,
12. explica qué mecanismo determina el orden de DFS,
13. compara `seen` con `color`,
14. expresa el costo de ambos recorridos con lista de adyacencia,
15. explica por qué tener el mismo costo asintótico no implica recorrer en el mismo orden.

Concluye distinguiendo:

```text
representación del Graph
política de pendientes
estado auxiliar
orden de recorrido
complejidad
```

#### Ejercicio 2. Estados de DFS sin escribir el método

Se tiene:

```text
0 -> [1, 2]
1 -> [2, 3]
2 -> [0, 4]
3 -> [4]
4 -> []
```

Se ejecuta DFS recursivo desde `0`.

Construye una tabla con:

```text
evento
vértice
color antes
color después
pila de llamadas
order
```

Registra al menos:

```text
entrada a dfsVisit
descubrimiento de vecino WHITE
encuentro de vecino GRAY
encuentro de vecino BLACK
retorno de llamada
```

Después:

1. identifica un ciclo,
2. explica por qué un vecino `GRAY` no provoca otra llamada,
3. identifica cuándo un vértice se vuelve `BLACK`,
4. distingue orden de descubrimiento y orden de finalización,
5. formula el invariante de `WHITE`,
6. formula el invariante de `GRAY`,
7. formula el invariante de `BLACK`,
8. explica qué información adicional aportan tres estados frente a un booleano,
9. relaciona la pila de llamadas con LIFO de Semana 3.

### B. Retos integradores

#### Reto 1. Marcar BLACK demasiado pronto

Un estudiante escribe:

```java
static void dfsVisit(
        Graph graph,
        int vertex,
        byte[] color,
        List<Integer> order) {

    color[vertex] = GRAY;
    order.add(vertex);
    color[vertex] = BLACK;

    for (int neighbor : graph.outEdges(vertex)) {
        if (color[neighbor] == WHITE) {
            dfsVisit(graph, neighbor, color, order);
        }
    }
}
```

1. identifica qué significado de `BLACK` se rompe,
2. construye un grafo donde un ancestro aparezca `BLACK` mientras todavía se procesa un descendiente,
3. explica por qué `order` podría parecer correcto en algunos casos,
4. identifica qué propiedad interna sigue siendo incorrecta,
5. explica qué observación sobre ciclos deja de ser válida,
6. corrige el orden conceptual de operaciones,
7. diseña una prueba que no mire solamente la lista `order`,
8. explica por qué una salida correcta no garantiza que el invariante esté bien mantenido.

#### Reto 2. Marcar GRAY demasiado tarde

Se propone:

```java
static void dfsVisit(Graph graph, int vertex, byte[] color) {
    for (int neighbor : graph.outEdges(vertex)) {
        if (color[neighbor] == WHITE) {
            dfsVisit(graph, neighbor, color);
        }
    }
    color[vertex] = GRAY;
    color[vertex] = BLACK;
}
```

Usa:

```text
0 -> 1
1 -> 2
2 -> 0
```

1. traza las llamadas,
2. explica por qué `0` sigue `WHITE` cuando `2` lo examina,
3. determina qué ocurre con la recursión,
4. identifica qué estado debe establecerse antes de explorar vecinos,
5. relaciona este problema con marcar al descubrir en BFS,
6. diseña el contraejemplo mínimo,
7. corrige conceptualmente la función.

#### Reto 3. DFS recursivo e iterativo con órdenes distintos

Se tiene:

```text
0 -> [1, 2, 3]
1 -> [4]
2 -> [5]
3 -> [6]
4 -> []
5 -> []
6 -> []
```

La versión recursiva recorre vecinos de izquierda a derecha.

La iterativa hace:

```java
for (int neighbor : graph.outEdges(vertex)) {
    if (!seen[neighbor]) {
        stack.push(neighbor);
    }
}
```

1. calcula el orden recursivo,
2. calcula el orden iterativo,
3. explica el efecto de LIFO,
4. modifica únicamente el orden de `push` para reproducir el orden recursivo,
5. explica por qué ambos siguen siendo DFS,
6. identifica qué propiedad es esencial y cuál depende del orden de almacenamiento,
7. diseña una prueba que permita varios órdenes válidos.

#### Reto 4. Marcar al `push` frente a marcar al `pop`

Considera:

```text
0 -- 1
0 -- 2
1 -- 3
2 -- 3
1 -- 4
2 -- 4
```

Compara:

```text
A. marcar al push
B. marcar al pop y descartar repetidos al extraer
```

1. traza las pilas,
2. cuenta los `push`,
3. cuenta cuántas veces se procesa realmente cada vértice,
4. identifica qué ids pueden repetirse en B,
5. explica por qué B puede seguir siendo correcta,
6. formula un invariante para A,
7. formula un invariante para B,
8. compara trabajo redundante,
9. explica por qué "procesado una vez" no equivale a "apilado una vez".

#### Reto 5. Reemplazar `color` por un `Set`

Se propone:

```java
Set<Integer> visited = new HashSet<>();
```

sin `WHITE`, `GRAY`, `BLACK`.

1. determina si basta para alcanzabilidad,
2. determina si basta para `dfsOrder`,
3. determina si evita ciclos infinitos,
4. explica qué información de `GRAY` se pierde,
5. explica qué información de `BLACK` se pierde,
6. decide si bastaría para contar componentes,
7. decide si bastaría para reconocer un vecino activo,
8. compara `HashSet<Integer>` con `boolean[]` para ids `0..V-1`,
9. concluye cuándo dos estados bastan y cuándo tres estados aportan información necesaria.

#### Reto 6. Profundidad de recursión y forma del grafo

Compara:

```text
Grafo A
0 -> 1 -> 2 -> ... -> V-1

Grafo B
0 -> 1
0 -> 2
0 -> 3
...
0 -> V-1
```

1. calcula la profundidad máxima de recursión en ambos,
2. compara el número de vértices visitados,
3. compara el tiempo asintótico,
4. explica por qué ambos pueden ser `O(V+E)` y consumir distinta profundidad de pila,
5. identifica cuál tiene mayor riesgo de `StackOverflowError`,
6. explica cómo una versión iterativa cambia la representación de la pila,
7. distingue complejidad asintótica de limitaciones prácticas.

#### Reto 7. Reiniciar `seen` en cada iteración

Un estudiante escribe:

```java
static int countConnectedComponents(Graph graph) {
    int components = 0;

    for (int v = 0; v < graph.nVertices(); v++) {
        boolean[] seen = new boolean[graph.nVertices()];

        if (!seen[v]) {
            components++;
            markComponent(graph, v, seen);
        }
    }

    return components;
}
```

1. identifica el error,
2. explica por qué `seen[v]` vuelve a ser `false`,
3. determina el resultado para un grafo conectado de `V` vértices,
4. determina el resultado para `V` aislados,
5. formula el invariante global que `seen` debe conservar,
6. corrige la ubicación conceptual de la inicialización,
7. explica por qué el estado debe sobrevivir entre llamadas a `markComponent`.

#### Reto 8. Etiquetar componentes, no solo contarlas

Diseña:

```java
int[] connectedComponentIds(Graph graph)
```

donde:

```text
componentId[v]
```

indica la componente de `v`.

Para:

```text
0 -- 1 -- 2

3 -- 4

5
```

una salida válida puede ser:

```text
[0,0,0,1,1,2]
```

1. elige un valor inicial,
2. explica cuándo aumenta el identificador,
3. modifica conceptualmente `markComponent`,
4. formula el invariante de `componentId`,
5. explica cómo obtener el número total de componentes,
6. obtiene tamaños de componentes con `Map<Integer,Integer>` o un arreglo,
7. analiza tiempo y espacio,
8. explica por qué el problema sigue siendo de componentes conexas.

#### Reto 9. Aristas mínimas para conectar el grafo

Un grafo no dirigido tiene `c` componentes.

1. demuestra informalmente por qué se necesitan al menos `c-1` aristas nuevas para conectarlo,
2. muestra una estrategia que use exactamente `c-1`,
3. aplica la idea a:

```text
{0,1,2}
{3,4}
{5,6,7}
{8}
```

4. propone aristas concretas,
5. explica cómo cambia el número de componentes tras cada inserción,
6. indica qué información adicional al contador necesitas para construir las aristas,
7. diseña cómo guardar un representante por componente.

No utilices DSU.

#### Reto 10. Nombres externos e ids internos

Una red utiliza:

```text
"ana"
"beto"
"carla"
"diego"
"elena"
"fabio"
```

Se desea mantener:

```java
Map<String,Integer> nameToId;
List<String> idToName;
Graph graph;
```

y producir grupos de nombres por componente.

1. asigna ids compactos,
2. formula el invariante entre `nameToId` e `idToName`,
3. construye el grafo,
4. diseña el recorrido de componentes,
5. decide entre `boolean[]` y `HashSet<Integer>` para visitados,
6. convierte los ids de cada componente nuevamente a nombres,
7. analiza el costo esperado del mapeo,
8. analiza el costo del recorrido,
9. explica qué ideas de Semanas 1, 10, 11 y 12 se combinan.

#### Reto 11. Elegir BFS o DFS según la pregunta

Para cada caso elige:

```text
BFS
DFS
cualquiera
ninguno por sí solo
```

Casos:

```text
A. saber si t es alcanzable desde s
B. distancia mínima en número de aristas
C. marcar una componente
D. contar componentes
E. explorar por niveles
F. profundizar antes de regresar
G. evitar recursión profunda
H. reconocer un vecino actualmente activo
I. obtener alcanzables sin importar orden
J. camino de menor peso con pesos distintos
```

Para cada uno:

1. identifica la propiedad requerida,
2. indica la estructura de pendientes,
3. indica el estado auxiliar,
4. expresa el costo con lista cuando corresponda,
5. explica por qué una alternativa puede ser correcta pero no adecuada.

#### Reto 12. Lista frente a matriz para componentes

Se tienen:

```text
V = 20000
E = 30000
```

Se desea contar componentes.

1. expresa el espacio de lista y matriz,
2. expresa el costo del recorrido con lista,
3. expresa el costo con matriz,
4. identifica la operación que causa la diferencia,
5. explica por qué el grafo es disperso,
6. elige una representación,
7. relaciona la decisión con Semana 11,
8. explica por qué reemplazar DFS por BFS no elimina la diferencia principal de representación.

#### Reto 13. Componentes después de agregar aristas

Estado inicial:

```text
0 -- 1

2 -- 3

4

5 -- 6
```

Se agregan:

```text
(1,2)
(4,5)
(3,4)
```

Después de cada arista:

1. determina las componentes,
2. determina el contador,
3. identifica si la arista une regiones diferentes,
4. explica por qué una arista interna no cambia el contador,
5. explica por qué una arista entre componentes lo reduce en uno,
6. diseña conceptualmente:

```java
boolean connectsDifferentComponents(Graph graph, int a, int b)
```

usando recorridos ya estudiados,
7. analiza su costo.

#### Reto 14. Probar `dfsOrder` mediante propiedades

Diseña grafos pequeños para probar:

```text
origen aislado
cadena
ramificación
ciclo
varias rutas hacia el mismo vértice
grafo desconectado
self-loop
```

Para cada caso indica:

1. propiedad verificada,
2. vértices que deben aparecer,
3. vértices que no deben aparecer,
4. órdenes que podrían ser válidos,
5. estado final esperado de los alcanzables,
6. error típico detectado.

Después diseña una verificación general de:

```text
cada vértice alcanzable aparece exactamente una vez
```

sin depender de un único orden exacto.

#### Reto 15. Probar `countConnectedComponents`

Diseña casos mínimos para:

```text
grafo vacío
un vértice
todos aislados
una sola componente
dos componentes
componente con ciclo
varios tamaños de componente
aislado junto a componente grande
```

Para cada uno especifica:

```text
V
aristas
resultado esperado
defecto que intenta detectar
```

Después diseña pruebas para detectar:

1. `seen` reinicializado dentro del bucle,
2. contador incrementado por cada vértice,
3. aislados ignorados,
4. `markComponent` que solo marca vecinos inmediatos,
5. una prueba excesivamente dependiente del orden.

### C. Ampliaciones opcionales

#### Ampliación 1. `parent[]` y árbol DFS

Añade:

```java
int[] parent;
```

Cuando `v` se descubre desde `u`:

```text
parent[v] = u
```

1. formula el invariante,
2. construye el árbol DFS de un ejemplo,
3. explica por qué no contiene todas las aristas del grafo,
4. reconstruye el camino en el árbol desde un vértice a `source`,
5. analiza el costo,
6. explica por qué ese camino no tiene por qué ser mínimo,
7. compara con `parent` producido por BFS.

#### Ampliación 2. Detectar ciclo dirigido usando GRAY

Diseña conceptualmente:

```java
boolean hasDirectedCycle(Graph graph)
```

usando:

```text
WHITE
GRAY
BLACK
```

La única idea adicional permitida es:

```text
una arista hacia un vértice GRAY
regresa al camino activo
```

1. diseña el algoritmo,
2. prueba un grafo acíclico,
3. prueba un ciclo de tres vértices,
4. prueba un self-loop,
5. explica por qué no debes clasificar todas las aristas,
6. analiza tiempo y espacio.

#### Ampliación 3. Tres políticas de pendientes

Compara conceptualmente:

```text
Queue
Stack
Priority Queue
```

sobre un conjunto de trabajo pendiente.

1. explica qué elemento sale primero en cada estructura,
2. relaciona Queue con BFS,
3. relaciona Stack con DFS,
4. da un ejemplo donde BFS y DFS produzcan órdenes muy distintos,
5. explica qué información adicional necesitaría una Priority Queue para decidir prioridades,
6. relaciona la comparación con Semana 7,
7. concluye cómo la estructura auxiliar cambia el comportamiento global sin modificar el Graph.
