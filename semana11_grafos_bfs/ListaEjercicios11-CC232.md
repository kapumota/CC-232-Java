### Ejercicios de la Semana 11

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La sección de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 11.

El objetivo no es repetir la actividad de clase ni volver a resolver exactamente los mismos ejemplos utilizados para introducir:

```text
Graph
AdjacencyListGraph
AdjacencyMatrixGraph
addEdge(...)
bfsOrder(...)
bfsDistances(...)
```

Tampoco se busca volver a pedir únicamente:

```text
dibujar una lista de adyacencia de un ejemplo ya visto
dibujar una matriz del mismo ejemplo
trazar el BFS utilizado en clase
obtener nuevamente [0, 1, 2, 3, 4, 5]
obtener nuevamente [0, 1, 1, 2, 2, 3, -1]
implementar literalmente los dos TODO de los archivos de la semana
```

Esos problemas ya cumplen su función introductoria en la lectura, la clase y los archivos de la Semana 11.

La mayoría de los problemas de esta lista exige:

```text
razonar antes de ejecutar
reconstruir grafos a partir de estados físicos parciales
separar ADT de implementación
formular invariantes
detectar código que compila pero es incorrecto
diseñar contraejemplos
comparar representaciones del mismo grafo lógico
analizar orden lógico frente a orden de almacenamiento
razonar sobre dirigido frente a no dirigido
combinar arreglos, listas, Queue, Map y Set con grafos
combinar estructuras de semanas anteriores
analizar compromisos entre tiempo y memoria
distinguir costo por vértice de costo global
usar deg(u) cuando proporciona una cota más informativa que V
justificar O(V+E) a partir del trabajo realmente realizado
comparar O(V+E) frente a O(V^2)
formular invariantes de seen y de la Queue
razonar sobre alcanzabilidad y ciclos
diseñar pruebas que revelen errores de representación
diseñar pruebas que revelen errores de recorrido
distinguir correctitud de eficiencia
distinguir orden BFS de distancia BFS
distinguir camino de menor número de aristas de camino de menor peso
elegir una estructura a partir del patrón de operaciones
```

Los temas centrales de la Semana 11 utilizados son:

```text
problema de representar relaciones entre objetos

grafo G = (V,E)
vértices
aristas
grafo dirigido
grafo no dirigido
source
destination
vértices 0..n-1
rango válido

ADT Graph
nVertices
addEdge
removeEdge
hasEdge
outEdges
outDegree

lista de adyacencia
List<List<Integer>>
lista externa indexada por vértice
lista de vecinos
invariante de rango
invariante de adyacencia
unicidad de aristas en la representación estudiada
grado de salida
deg(u)
O(deg(u))
espacio O(V+E)

matriz de adyacencia
boolean[][]
matrix[u][v]
invariante de matriz
addEdge O(1)
removeEdge O(1)
hasEdge O(1)
outEdges O(V)
outDegree O(V) en la implementación estudiada
espacio O(V^2)

lista frente a matriz
grafo disperso
grafo denso
tiempo frente a espacio
elección de representación según las operaciones

grafo no dirigido como relación simétrica
addUndirectedEdge

camino
alcanzabilidad
ciclo

vértice origen
vértices descubiertos
vértices pendientes
vértices procesados

seen
pertenencia de vértices descubiertos
boolean[] como representación directa

Queue
FIFO

BFS
inicialización
marcar source
encolar source
retirar de la cola
explorar vecinos
descubrir vecinos no vistos
marcar antes de encolar
cada vértice se encola como máximo una vez
exploración por niveles

distance
distance[source] = 0
distance[v] = distance[u] + 1
-1 para no alcanzables
distancia mínima en número de aristas
grafo no ponderado

O(V+E) con listas de adyacencia
O(V^2) con matriz
espacio auxiliar O(V)

relación entre representación, invariante y costo
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
Semana 1
arreglos dinámicos
acceso por índice
tamaño y capacidad
resize
costo amortizado
boolean[]
int[]

Semana 2
listas
recorridos
colecciones locales
modificación local

Semana 3
Stack
Queue
Deque
FIFO
LIFO

Semanas 4 y 5
BST
orden
find
add
remove
altura
invariantes

Semana 6
AVL
balance
O(log n)
orden global

Semana 7
Priority Queue
BinaryHeap
mínimo
peek
búsqueda arbitraria O(n)

Semana 8
hashing
buckets
colisiones
búsqueda local
O(k)

Semana 9
capacidad
crecimiento
rehashing
esperado frente a amortizado

Semana 10
Map
Set
HashSet
TreeSet
pertenencia
unicidad
clave -> valor
memoria auxiliar para evitar trabajo repetido

trazas
contraejemplos
correctitud
complejidad
```

Para los ejercicios de esta semana se pueden utilizar, cuando el enunciado lo permita:

```java
List<T>
ArrayList<T>
Queue<T>
ArrayDeque<T>
Map<K,V>
HashMap<K,V>
Set<T>
HashSet<T>
boolean[]
int[]
```

`TreeSet`, `AVL` y `BinaryHeap` pueden aparecer en comparaciones o diseños integradores cuando sus propiedades ya hayan sido estudiadas.

No se requiere utilizar:

```text
Streams
Collectors
GraphStream
JGraphT
TreeMap
LinkedHashSet
PriorityQueue como sustitución automática de un algoritmo de grafos
matrices dispersas especializadas
CSR
CSC
```

Tampoco se requiere estudiar ni implementar:

```text
DFS
componentes conexas como algoritmo completo
componentes fuertemente conexas
clasificación de aristas de DFS
ordenamiento topológico
Prim
Kruskal
Dijkstra
Bellman-Ford
Floyd-Warshall
A*
flujos
caminos eulerianos
caminos hamiltonianos
coloración de grafos
```

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir una operación auxiliar nueva, pero deben poder razonarse a partir de los conceptos disponibles hasta la Semana 11.

### A. Consolidación esencial

#### Ejercicio 1. Un mismo grafo lógico, dos estados físicos

Se afirma que dos objetos implementan el mismo `Graph` dirigido sobre:

```text
V = {0,1,2,3,4,5}
```

La representación por lista muestra:

```text
0 -> [1,4]
1 -> [2]
2 -> []
3 -> [1,5]
4 -> [5]
5 -> []
```

La representación por matriz muestra:

```text
      0 1 2 3 4 5
    +------------
0   | 0 1 0 0 1 0
1   | 0 0 1 0 0 0
2   | 0 0 0 0 0 0
3   | 0 1 0 0 0 1
4   | 0 0 0 0 0 1
5   | 0 0 0 0 0 0
```

Sin ejecutar código:

1. escribe el conjunto lógico `E`,
2. verifica si ambas representaciones describen exactamente el mismo grafo,
3. calcula `outDegree(v)` para cada vértice,
4. identifica los vértices con grado de salida cero,
5. determina `hasEdge(3,5)`,
6. determina `hasEdge(5,3)`,
7. explica por qué esas dos consultas no expresan lo mismo,
8. formula el invariante de rango,
9. formula el invariante de adyacencia para la lista,
10. formula el invariante de matriz,
11. determina el costo de `hasEdge(3,5)` en ambas implementaciones,
12. determina el costo de enumerar los vecinos de `3` en ambas,
13. explica qué parte del estado es lógica,
14. explica qué parte pertenece únicamente a la representación,
15. cambia el orden de `adjacency[0]` a `[4,1]` y decide si el grafo lógico cambió,
16. explica qué algoritmo posterior sí podría observar ese cambio de orden.

Concluye distinguiendo:

```text
mismo Graph
misma colección de aristas
distinto estado físico
distinto costo de operaciones
```

#### Ejercicio 2. Elegir representación antes de escribir BFS

Se deben construir cinco sistemas distintos sobre grafos con vértices `0..V-1`.

Sistema A:

```text
V = 500
el grafo cambia poco
se realizan millones de consultas hasEdge(u,v)
el grafo es relativamente denso
```

Sistema B:

```text
V = 100000
cada vértice tiene en promedio 3 vecinos
se recorren vecinos constantemente
```

Sistema C:

```text
V = 2000
se ejecutan BFS repetidamente
el grafo es disperso
```

Sistema D:

```text
V = 200
se consulta hasEdge con mucha frecuencia
también se recorren vecinos ocasionalmente
la memoria no es un problema importante
```

Sistema E:

```text
V = 50000
E es del mismo orden que V
se necesita calcular alcanzabilidad desde distintos orígenes
```

Para cada sistema:

1. elige lista de adyacencia o matriz,
2. justifica la decisión,
3. indica la operación dominante,
4. expresa el espacio de la representación,
5. expresa el costo de `hasEdge`,
6. expresa el costo de recorrer vecinos,
7. estima el costo de un BFS,
8. identifica qué decisión cambiarías si el patrón de operaciones cambiara,
9. explica por qué "la lista de adyacencia siempre es mejor" es una afirmación incorrecta,
10. explica por qué "la matriz es más rápida" también es una afirmación incompleta.

### B. Retos integradores

#### Reto 1. `addEdge` valida solo el origen

Un estudiante implementa:

```java
public void addEdge(int source, int destination) {
    checkVertex(source);

    if (!adjacency.get(source).contains(destination)) {
        adjacency.get(source).add(destination);
    }
}
```

Se ejecuta sobre un grafo de 5 vértices:

```text
addEdge(0, 2)
addEdge(1, 4)
addEdge(3, 7)
```

1. identifica qué invariante intenta proteger `checkVertex(source)`,
2. explica qué invariante deja sin proteger,
3. reconstruye el estado físico después de las tres llamadas si `ArrayList` acepta el entero `7`,
4. explica por qué el programa puede no fallar inmediatamente,
5. muestra una operación posterior donde el defecto sí pueda hacerse observable,
6. explica por qué una estructura puede contener un valor físicamente almacenable pero lógicamente inválido,
7. corrige la secuencia conceptual de validaciones,
8. diseña el contraejemplo mínimo que detecte el defecto,
9. explica por qué una prueba que solo use vértices válidos no puede detectar este error,
10. relaciona el problema con la validación de índices de la Semana 1.

#### Reto 2. Una arista duplicada no rompe todo, pero sí cambia la representación

Se utiliza una lista de adyacencia que permite:

```text
0 -> [1,1,1,2]
1 -> []
2 -> []
```

El contenido lógico esperado era:

```text
0 -> 1
0 -> 2
```

Analiza:

1. qué propiedad del `Graph` estudiado se ha roto,
2. si `hasEdge(0,1)` puede seguir retornando correctamente `true`,
3. si `outDegree(0)` seguirá representando el grado lógico esperado,
4. qué resultado podría producir `outEdges(0)`,
5. si BFS con `seen` seguirá descubriendo al vértice `1` más de una vez,
6. cuántas veces puede examinarse la entrada redundante `1`,
7. por qué la correctitud del descubrimiento puede mantenerse aunque la representación sea peor,
8. por qué el costo real de BFS puede aumentar por redundancia,
9. formula un invariante que elimine este problema,
10. compara este caso con duplicados dentro de un `Set` de la Semana 10.

Concluye distinguiendo:

```text
correctitud de una consulta
correctitud completa de la representación
eficiencia
```

#### Reto 3. Dos implementaciones de `outDegree`, mismo resultado, costo distinto

Se proponen dos diseños para una lista de adyacencia.

Diseño A:

```java
int outDegree(int vertex) {
    return adjacency.get(vertex).size();
}
```

Diseño B mantiene además:

```java
int[] degree;
```

y retorna:

```java
int outDegree(int vertex) {
    return degree[vertex];
}
```

En el Diseño B, `addEdge` y `removeEdge` deben actualizar `degree`.

1. compara el costo de `outDegree` en ambos diseños,
2. explica por qué ambos pueden ser `O(1)`,
3. formula el nuevo invariante necesario en el Diseño B,
4. construye una ejecución donde olvidar `degree[source]++` deje el grafo físicamente conectado pero la metadata sea incorrecta,
5. construye otra donde se actualice `degree` pero no la lista,
6. explica por qué almacenar información redundante puede acelerar consultas y aumentar el número de invariantes,
7. indica qué diseño elegirías si `size()` de la lista ya es `O(1)`,
8. relaciona este problema con mantener altura almacenada en AVL.

#### Reto 4. Convertir lista a matriz sin cambiar el Graph lógico

Se recibe una lista de adyacencia dirigida:

```text
0 -> [2,5]
1 -> [0]
2 -> [3]
3 -> [1,4]
4 -> []
5 -> [4]
```

Diseña conceptualmente:

```java
boolean[][] toMatrix(List<List<Integer>> adjacency)
```

No escribas primero el código.

1. determina `V`,
2. formula el invariante que debe cumplir la matriz resultante,
3. construye manualmente la matriz,
4. diseña el algoritmo,
5. analiza el tiempo en función de `V` y `E`,
6. analiza el espacio de salida,
7. explica por qué no es necesario probar todas las `V^2` parejas para construir la matriz a partir de las aristas existentes,
8. determina si el algoritmo cambia si las listas contienen vecinos en otro orden,
9. explica qué debe ocurrir si aparece un vecino fuera de rango,
10. diseña una prueba que compare sistemáticamente ambos grafos mediante `hasEdge`.

#### Reto 5. Convertir matriz a lista y explicar por qué el costo cambia

Se recibe una matriz de `V x V`.

Diseña:

```java
List<List<Integer>> toAdjacencyLists(boolean[][] matrix)
```

1. formula el invariante de salida,
2. explica por qué debes examinar las `V^2` posiciones de entrada,
3. analiza el tiempo,
4. analiza el espacio adicional en función de `V` y `E`,
5. determina qué ocurre con filas sin ningún `true`,
6. explica por qué el resultado puede ser mucho más pequeño que la matriz,
7. compara este proceso con el reto anterior,
8. construye un ejemplo disperso donde la diferencia de memoria sea conceptualmente grande,
9. explica por qué crear una matriz completa puede implicar trabajo relacionado con `V^2` aunque solo existan pocas aristas,
10. distingue costo de inicializar la representación de costo de recorrer las aristas existentes.

#### Reto 6. Mantener lista y matriz simultáneamente

Un sistema decide mantener las dos representaciones al mismo tiempo:

```java
List<List<Integer>> adjacency;
boolean[][] matrix;
```

El objetivo es obtener:

```text
hasEdge O(1)
+
recorrido eficiente de vecinos
```

1. formula un invariante que relacione ambas representaciones,
2. diseña conceptualmente `addEdge`,
3. diseña conceptualmente `removeEdge`,
4. explica qué debe ocurrir si la arista ya existe,
5. construye un error donde se actualiza la matriz pero no la lista,
6. construye otro donde se actualiza la lista pero no la matriz,
7. identifica qué operaciones revelarían cada inconsistencia,
8. compara el espacio con usar solo lista,
9. compara el espacio con usar solo matriz,
10. explica por qué mejorar varias operaciones puede exigir más memoria y más invariantes,
11. relaciona el diseño con problemas anteriores donde se mantenían dos estructuras para propiedades diferentes.

#### Reto 7. Representar vértices con nombres sin abandonar 0..n-1 internamente

La entrada de una aplicación utiliza nombres:

```text
"UNI"
"FC"
"CC"
"MAT"
"FIS"
```

Las aristas llegan como:

```text
"UNI" -> "FC"
"FC"  -> "CC"
"FC"  -> "MAT"
"UNI" -> "FIS"
```

Pero se desea que el grafo interno continúe utilizando vértices compactos:

```text
0..n-1
```

Puedes utilizar:

```java
Map<String,Integer>
List<String>
Graph
```

Diseña la representación completa.

1. explica qué información almacena el `Map<String,Integer>`,
2. explica qué información almacena la `List<String>`,
3. explica qué información almacena `Graph`,
4. asigna identificadores concretos a los cinco nombres,
5. convierte las aristas de entrada a enteros,
6. formula el invariante entre el Map y la List,
7. formula el invariante entre los ids y `Graph`,
8. explica cómo convertir un resultado BFS de enteros nuevamente a nombres,
9. analiza el costo esperado de localizar un id por nombre usando hashing,
10. explica qué idea de Semana 10 se combina con Semana 11,
11. explica por qué usar directamente cadenas como índices de `boolean[]` no es posible.

#### Reto 8. Lista de adyacencia con `HashSet` en cada vértice

Se propone reemplazar:

```java
List<List<Integer>>
```

por conceptualmente:

```java
List<Set<Integer>>
```

con `HashSet<Integer>` como colección de vecinos.

Analiza esta alternativa.

1. qué invariante de unicidad queda incorporado naturalmente,
2. cuál sería el costo esperado de `hasEdge(u,v)`,
3. cuál sería el costo esperado de evitar duplicados en `addEdge`,
4. cuál sería el costo esperado de `removeEdge`,
5. cuál sería el costo de recorrer todos los vecinos,
6. cómo cambia el espacio respecto de una lista simple,
7. qué propiedad de orden podría perderse,
8. cómo podría cambiar el orden concreto producido por BFS,
9. qué partes del Graph lógico no cambian,
10. explica por qué esta alternativa no es automáticamente mejor,
11. relaciona la decisión con `HashSet` frente a `TreeSet` de Semana 10.

#### Reto 9. `addUndirectedEdge` actualiza solo un extremo

Se implementa:

```java
void addUndirectedEdge(int a, int b) {
    adjacency.get(a).add(b);
}
```

Sobre un grafo inicialmente vacío se ejecuta:

```text
addUndirectedEdge(0,1)
addUndirectedEdge(1,2)
addUndirectedEdge(2,3)
```

1. construye el estado físico resultante,
2. dibuja el grafo dirigido que realmente representa,
3. dibuja el grafo no dirigido que se pretendía representar,
4. formula el invariante de simetría faltante,
5. determina `outEdges(1)`,
6. determina si `0` es alcanzable desde `3` en la representación defectuosa,
7. determina si `3` es alcanzable desde `0`,
8. explica por qué esas respuestas revelan que ya no se representa una relación no dirigida,
9. corrige conceptualmente la operación,
10. diseña una prueba mínima que detecte inmediatamente el defecto.

#### Reto 10. Un BFS que marca al retirar

Un estudiante escribe conceptualmente:

```java
queue.add(source);

while (!queue.isEmpty()) {
    int u = queue.remove();

    if (seen[u]) continue;

    seen[u] = true;

    for (int v : graph.outEdges(u)) {
        if (!seen[v]) {
            queue.add(v);
        }
    }
}
```

Considera el grafo:

```text
    0
   / \
  1   2
   \ /
    3
```

1. traza completamente la Queue,
2. determina cuántas veces puede entrar `3`,
3. explica por qué el algoritmo puede seguir terminando,
4. explica por qué no mantiene el invariante de la Semana 11,
5. compara el número de inserciones a la Queue con la versión que marca antes de encolar,
6. construye un grafo donde el exceso de inserciones sea mayor,
7. modifica conceptualmente el orden de instrucciones,
8. formula el invariante corregido,
9. explica por qué "termina y produce los mismos vértices" no basta para considerar ambas implementaciones equivalentes.

#### Reto 11. BFS sin `seen` sobre un árbol y sobre un grafo

Un estudiante afirma:

> "Si Queue ya contiene los pendientes, `seen` es redundante."

Analiza dos entradas.

Entrada A:

```text
árbol dirigido desde la raíz
0 -> 1
0 -> 2
1 -> 3
1 -> 4
2 -> 5
```

Entrada B:

```text
0 -> 1
1 -> 2
2 -> 0
```

1. traza un BFS sin `seen` sobre A,
2. explica por qué podría parecer correcto,
3. traza el comportamiento sobre B,
4. identifica la propiedad estructural que hace distinto al grafo,
5. explica por qué una Queue no responde por sí sola "¿ya descubrí v?",
6. relaciona `seen` con el ADT Set,
7. explica por qué el recorrido de grafos necesita memoria de descubrimiento aunque algunos árboles particulares no la necesiten.

#### Reto 12. Mismo grafo, diferente orden BFS

Se consideran dos listas de adyacencia para el mismo grafo no dirigido.

Representación A:

```text
0 -> [1,2,3]
1 -> [0,4]
2 -> [0,5]
3 -> [0,6]
4 -> [1]
5 -> [2]
6 -> [3]
```

Representación B:

```text
0 -> [3,1,2]
1 -> [4,0]
2 -> [5,0]
3 -> [6,0]
4 -> [1]
5 -> [2]
6 -> [3]
```

Desde `source = 0`:

1. verifica que ambas representan las mismas aristas lógicas,
2. calcula un orden BFS para A,
3. calcula un orden BFS para B,
4. calcula las distancias en A,
5. calcula las distancias en B,
6. explica por qué los órdenes pueden cambiar,
7. explica por qué las distancias no cambian,
8. identifica qué propiedad está garantizada por BFS,
9. identifica qué detalle depende de la representación,
10. explica por qué una prueba que exige un único orden exacto puede ser demasiado fuerte para una implementación general.

#### Reto 13. `distance` reemplaza a `seen`

Se comparan dos variantes.

Versión A:

```text
boolean[] seen
int[] distance
```

Versión B:

```text
int[] distance
inicializado a -1
```

En B:

```text
distance[v] == -1
```

significa que `v` no fue descubierto.

1. formula el invariante de A,
2. formula el invariante de B,
3. explica qué información está duplicada en A,
4. explica cuándo B puede usar `distance` como marca de descubrimiento,
5. indica por qué `-1` es una buena marca,
6. construye una situación donde usar `0` como marca de no descubierto sería ambiguo,
7. compara espacio asintótico,
8. explica por qué ambas siguen siendo `O(V)`,
9. decide cuál usarías para `bfsOrder`,
10. decide cuál usarías para `bfsDistances`,
11. relaciona el problema con evitar estado redundante innecesario.

#### Reto 14. La distancia correcta con un orden de vecinos distinto

Se tiene el grafo no dirigido:

```text
0 -- 1
0 -- 2
1 -- 4
2 -- 3
3 -- 5
4 -- 5
```

Desde `0`:

1. enumera al menos dos órdenes BFS posibles cambiando el orden de vecinos,
2. calcula `distance[]`,
3. determina la distancia mínima hasta `5`,
4. muestra al menos dos caminos desde `0` hasta `5`,
5. identifica cuáles tienen longitud mínima,
6. explica por qué BFS puede escoger un padre distinto para `5` sin cambiar su distancia,
7. explica qué resultado es estable y cuál puede depender del orden,
8. indica por qué este ejercicio no requiere un algoritmo distinto a BFS.

#### Reto 15. Distancia en aristas no es costo ponderado

Se muestra conceptualmente:

```text
0 --10--> 1
 \        ^
  1      1
   \    /
     2
```

Interpreta los números como pesos.

Existen:

```text
0 -> 1
```

con una arista de peso `10`, y:

```text
0 -> 2 -> 1
```

con dos aristas de pesos `1` y `1`.

1. ¿qué distancia en número de aristas asignaría BFS a `1` desde `0`?,
2. ¿qué camino tiene menor peso total?,
3. explica por qué esas dos respuestas no se contradicen,
4. formula exactamente qué minimiza el BFS de Semana 11,
5. explica por qué no debe presentarse este BFS como solución general de caminos mínimos ponderados,
6. identifica qué información adicional tendría que almacenar una arista ponderada,
7. sin implementar otro algoritmo, explica por qué la política FIFO ya no basta para ordenar necesariamente por costo total.

El objetivo es delimitar correctamente el alcance del algoritmo estudiado.

#### Reto 16. Contar vértices por nivel combinando BFS y Map

Se desea producir, para un origen `source`, una estructura:

```text
distancia -> cantidad de vértices
```

Por ejemplo:

```text
0 -> 1
1 -> 3
2 -> 5
3 -> 2
```

Puedes utilizar:

```text
bfsDistances(...)
Map<Integer,Integer>
```

Diseña:

```java
Map<Integer,Integer> levelHistogram(Graph graph, int source)
```

1. explica qué información produce primero BFS,
2. explica qué información debe ignorarse para vértices no alcanzables,
3. formula el significado de cada clave del Map,
4. formula el significado de cada valor,
5. diseña el algoritmo sin usar Streams,
6. analiza el costo del BFS,
7. analiza el costo esperado de construir el Map,
8. expresa el costo total,
9. explica qué ideas de Semana 10 reaparecen,
10. explica por qué un `Set` no sería suficiente para almacenar cantidades.

#### Reto 17. Encontrar todos los vértices a distancia exactamente k

Se desea:

```java
List<Integer> verticesAtDistance(
    Graph graph,
    int source,
    int k)
```

Condiciones:

```text
grafo no ponderado
k >= 0
el orden puede seguir el orden BFS
```

1. diseña una solución utilizando `bfsDistances`,
2. analiza su tiempo total,
3. analiza su espacio,
4. explica qué ocurre si `k = 0`,
5. explica qué ocurre si `k` es mayor que toda distancia alcanzable,
6. explica qué ocurre con vértices `-1`,
7. diseña otra solución que recoja vértices durante BFS sin construir después un segundo recorrido del arreglo,
8. compara ambas soluciones,
9. explica cuál es más simple de verificar,
10. explica por qué ambas siguen basándose en la misma propiedad de niveles.

#### Reto 18. Dos representaciones, mismo BFS lógico, costos concretos muy distintos

Considera:

```text
V = 10000
E = 20000
```

y un BFS que alcanza todos los vértices.

No calcules bytes exactos de Java.

Compara conceptualmente:

```text
lista de adyacencia
matriz de adyacencia
```

1. expresa el espacio asintótico de cada una,
2. expresa el tiempo asintótico del BFS de cada una,
3. estima cuántas posiciones de una matriz deben examinarse en orden de magnitud,
4. compara con la cantidad de aristas almacenadas,
5. explica por qué el mismo pseudocódigo de BFS puede tener costos distintos,
6. explica por qué `O(V+E)` no es una propiedad aislada de Queue,
7. explica qué operación del `Graph` determina la diferencia principal,
8. decide qué representación usarías y justifica.

#### Reto 19. Una batería de pruebas para una implementación desconocida de Graph

Se recibe una clase desconocida con:

```java
int nVertices()
void addEdge(int u, int v)
void removeEdge(int u, int v)
boolean hasEdge(int u, int v)
List<Integer> outEdges(int u)
int outDegree(int u)
```

No puedes observar su representación.

Diseña una batería pequeña pero potente para detectar errores en:

```text
número de vértices
rango negativo
rango superior
agregar una arista
agregar una arista repetida
aristas con mismo origen
aristas con mismo destino
dirección de la arista
eliminar una arista presente
eliminar una arista ausente
outEdges
outDegree
vértice aislado
```

Para cada prueba indica:

```text
estado inicial
operaciones
resultado observable esperado
invariante o propiedad verificada
defecto que podría revelar
```

Después:

1. diseña una secuencia que compare `outDegree(u)` con `outEdges(u).size()`,
2. explica por qué esa igualdad es una buena prueba cruzada,
3. explica por qué verificar solo `hasEdge` es insuficiente,
4. explica por qué verificar solo `outEdges` también es insuficiente,
5. indica qué defectos podrían permanecer ocultos si nunca se prueban aristas repetidas.

#### Reto 20. Una batería de pruebas para BFS

Se recibe una implementación desconocida:

```java
List<Integer> bfsOrder(Graph graph, int source)
int[] bfsDistances(Graph graph, int source)
```

Diseña grafos pequeños que permitan probar independientemente:

```text
un solo vértice
origen aislado
una cadena
una estrella
un ciclo
un vértice con dos caminos de llegada
grafo desconectado
varias rutas de igual longitud
```

Para cada grafo indica:

```text
qué propiedad intentas probar
qué vértices deben ser alcanzables
qué distancias son obligatorias
qué partes del orden pueden variar
qué error típico detectaría
```

Después:

1. diseña un grafo que detecte marcar al retirar en vez de marcar al encolar,
2. diseña un grafo que detecte ausencia de `seen`,
3. diseña un grafo que detecte un error `distance[v] = distance[u]`,
4. diseña un grafo que detecte inicializar todos los vértices con distancia `0`,
5. explica por qué una buena batería debe verificar propiedades y no solo una salida textual.

### C. Ampliaciones opcionales

#### Ampliación 1. Reconstruir un camino mínimo con `parent[]`

Además de:

```text
distance[]
```

se desea mantener:

```text
parent[]
```

Cuando `v` se descubre por primera vez desde `u`:

```text
parent[v] = u
```

Para `source` puede utilizarse:

```text
parent[source] = -1
```

1. formula el significado de `parent[v]`,
2. explica por qué debe asignarse únicamente cuando `v` se descubre por primera vez,
3. modifica conceptualmente BFS para mantener `parent`,
4. diseña cómo reconstruir un camino desde `source` hasta un vértice alcanzable,
5. explica por qué el camino debe reconstruirse inicialmente en sentido inverso,
6. analiza el costo de reconstruir un camino de longitud `d`,
7. explica qué ocurre para un vértice no alcanzable,
8. relaciona `parent` con árboles estudiados anteriormente sin afirmar que el grafo original sea un árbol.

No se requiere desarrollar todavía árboles BFS en profundidad.

#### Ampliación 2. Grafo inverso de un grafo dirigido

Para cada arista:

```text
u -> v
```

el grafo inverso contiene:

```text
v -> u
```

Diseña:

```java
Graph reverse(Graph graph)
```

utilizando una nueva lista de adyacencia.

1. formula el invariante del resultado,
2. diseña el algoritmo usando `outEdges`,
3. analiza el tiempo con lista de adyacencia,
4. analiza el espacio,
5. construye manualmente el inverso de un ejemplo,
6. explica qué ocurre con una arista `u -> u`,
7. explica por qué invertir dos veces debe recuperar el mismo conjunto lógico de aristas,
8. diseña una prueba basada en esa propiedad.

#### Ampliación 3. BFS con varias fuentes

Se desea calcular la distancia hasta la fuente más cercana de un conjunto:

```text
sources = {s1, s2, ..., sk}
```

Idea inicial:

```text
todas las fuentes tienen distancia 0
todas se encolan inicialmente
```

1. explica por qué esta inicialización es coherente con los niveles,
2. formula el invariante de la Queue,
3. diseña el algoritmo,
4. explica por qué cada vértice sigue encolándose como máximo una vez,
5. analiza el tiempo,
6. analiza el espacio,
7. construye un ejemplo pequeño con dos fuentes,
8. compara el resultado con ejecutar BFS separado desde cada fuente,
9. explica qué trabajo puede evitar la versión multifuente.

No se requiere utilizar este patrón en los archivos de clase.

#### Ampliación 4. Elegir entre `boolean[]`, HashSet y una representación ordenada para visitados

Compara tres posibles representaciones de vértices descubiertos:

```text
boolean[]
HashSet<Integer>
TreeSet<Integer>
```

en tres escenarios:

```text
A. vértices compactos 0..V-1
B. identificadores enteros muy dispersos
C. se necesita además recorrer visitados en orden
```

Para cada escenario:

1. identifica el ADT lógico requerido,
2. elige una representación,
3. analiza la pertenencia,
4. analiza el espacio conceptual,
5. explica qué propiedad extra mantiene TreeSet,
6. explica por qué esa propiedad puede ser innecesaria para BFS,
7. relaciona la elección con la Semana 10.

#### Ampliación 5. Diseñar una interfaz Graph ligeramente distinta

Se propone añadir:

```java
int nEdges();
```

y mantener un contador interno de aristas.

Para grafo dirigido:

```text
cada u -> v cuenta una arista
```

Para un grafo no dirigido representado mediante dos entradas internas:

```text
una relación u -- v debe decidirse
si cuenta lógica o físicamente como 1 o como 2
```

1. especifica claramente qué debería significar `nEdges()`,
2. formula el invariante del contador para un grafo dirigido,
3. indica cuándo debe incrementarse,
4. indica cuándo no debe incrementarse por una inserción repetida,
5. indica cuándo debe decrementarse,
6. construye un error donde el contador y la representación diverjan,
7. explica por qué añadir una operación aparentemente simple puede añadir estado e invariantes,
8. compara mantener el contador con calcular el número de aristas recorriendo todas las listas,
9. analiza el costo de ambas alternativas.
