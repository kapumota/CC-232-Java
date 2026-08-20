### Ejercicios de la Semana 13

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La sección de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 13.

La Semana 13 funciona como una semana de **selección e integración**. Por ello, esta lista no busca repetir los ejercicios desarrollados en clase ni volver a implementar literalmente:

```text
balanced(...)
firstDuplicate(...)
kthSmallest(...)
serveNext(...)
trickleDown(...)
```

Tampoco se busca repetir de manera aislada:

```text
una inserción de arreglo dinámico ya vista
una traza elemental de Stack o Queue
una eliminación estándar de BST
una rotación AVL conocida
una extracción mínima idéntica a la Semana 7
un rehashing idéntico a la Semana 9
un primer duplicado idéntico a la Semana 10
el BFS utilizado en la Semana 11
el DFS y las componentes de la Semana 12
```

Esos problemas ya cumplieron su función introductoria en las lecturas, las actividades y los archivos de cada semana.

La mayoría de los ejercicios de esta lista exige:

```text
razonar antes de ejecutar
elegir una estructura a partir del patrón de operaciones
separar ADT de implementación
formular invariantes
coordinar varios invariantes simultáneamente
reconstruir estados lógicos y físicos
detectar código que compila pero es incorrecto
diseñar contraejemplos mínimos
comparar representaciones
analizar costos concretos y asintóticos
distinguir peor caso, costo esperado y costo amortizado
distinguir correctitud de eficiencia
analizar tiempo frente a memoria
diseñar operaciones compuestas
diseñar pruebas que revelen errores estructurales
combinar estructuras de distintas semanas
justificar por qué una estructura es apropiada y otra no.
```

### Temas acumulados utilizados

#### Semana 1

```text
arreglos dinámicos
tamaño lógico
capacidad
acceso por índice
resize
crecimiento geométrico
inserción
eliminación
desplazamientos
búsqueda secuencial
costo amortizado
```

#### Semana 2

```text
listas enlazadas
nodos
referencias
recorridos
modificación local
costo de localizar frente a costo de modificar
```

#### Semana 3

```text
Stack
Queue
Deque
LIFO
FIFO
operaciones por extremos
ArrayDeque
```

#### Semanas 4 y 5

```text
BST
orden
búsqueda
inserción
eliminación
sucesor
predecesor
altura
casos de eliminación
invariantes de orden
```

#### Semana 6

```text
AVL
altura almacenada
factor de balance
rotaciones
preservación del orden BST
O(log n)
```

#### Semana 7

```text
Priority Queue
BinaryHeap
min-heap
arreglo como árbol implícito
parent
left
right
bubbleUp
trickleDown
heapify
extracción del mínimo
```

#### Semanas 8 y 9

```text
hashing
hashCode
función hash
bucket
colisiones
encadenamiento
factor de carga
resize
rehashing
costo esperado
costo amortizado
```

#### Semana 10

```text
Map
Set
HashMap
HashSet
TreeSet
clave -> valor
pertenencia
unicidad
orden como propiedad adicional
elección de representación
```

#### Semana 11

```text
Graph
lista de adyacencia
matriz de adyacencia
grafo dirigido
grafo no dirigido
outEdges
outDegree
BFS
Queue
seen
alcanzabilidad
distancias mínimas en número de aristas
O(V+E)
```

#### Semana 12

```text
DFS
Stack
recursión
WHITE
GRAY
BLACK
pila de llamadas
camino activo
retroceso
DFS iterativo
componentes conexas
seen global
O(V+E)
```

#### Semana 13

```text
selección de estructuras
operaciones dominantes
invariantes coordinados
problemas de selección
min-heap para selección
max-heap de tamaño k como alternativa
Quickselect como ampliación conceptual
sistemas que combinan varias estructuras
estado derivado almacenado
tiempo frente a memoria
complejidad por operación
```

### Herramientas permitidas

Cuando el enunciado lo permita pueden utilizarse:

```java
int[]
boolean[]
List<T>
ArrayList<T>
Deque<T>
ArrayDeque<T>
Map<K,V>
HashMap<K,V>
Set<T>
HashSet<T>
TreeSet<T>
```

También pueden reutilizarse las implementaciones conceptuales estudiadas de:

```text
lista enlazada
BST
AVL
BinaryHeap
Graph con lista de adyacencia
Graph con matriz de adyacencia
```

No se requiere utilizar:

```text
Streams
Collectors
Collections.sort como sustitución automática de un problema de selección
PriorityQueue como sustitución automática cuando el ejercicio pide razonar sobre el heap
TreeMap
LinkedHashSet
ConcurrentHashMap
GraphStream
JGraphT
```

Tampoco se requiere estudiar ni implementar como contenido obligatorio:

```text
Red-Black Tree
B-Tree
Fibonacci Heap
Binomial Heap
Median of Medians
componentes fuertemente conexas
ordenamiento topológico
Dijkstra
Bellman-Ford
Prim
Kruskal
Union-Find
```

Los ejercicios de la sección C son ampliaciones opcionales. Introducen ideas nuevas únicamente cuando pueden analizarse utilizando los conceptos disponibles hasta la Semana 13.

### A. Consolidación esencial

#### Ejercicio 1. Elegir la estructura antes de escribir código

Se deben diseñar diez componentes independientes.

Sistema A:

```text
se reciben eventos
deben procesarse exactamente en orden de llegada
```

Sistema B:

```text
se reciben comandos
se necesita deshacer siempre el comando más reciente
```

Sistema C:

```text
se almacenan códigos únicos
se consulta pertenencia con mucha frecuencia
el orden no importa
```

Sistema D:

```text
se almacenan códigos únicos
se necesita recorrerlos de menor a mayor
```

Sistema E:

```text
se reciben trabajos con prioridad
se elimina repetidamente el de menor prioridad numérica
```

Sistema F:

```text
se almacenan pares
código -> nota
se realizan muchas búsquedas exactas por código
```

Sistema G:

```text
se almacenan claves ordenadas
se necesita sucesor y predecesor
y se exige altura O(log n)
```

Sistema H:

```text
se conocen referencias directas a nodos
y se realizan muchas modificaciones locales
```

Sistema I:

```text
se representan 100000 vértices
cada vértice tiene en promedio 4 vecinos
se recorren vecinos constantemente
```

Sistema J:

```text
V = 400
el grafo es denso
se realizan millones de consultas hasEdge(u,v)
```

Para cada sistema:

1. identifica la operación dominante,
2. indica el ADT requerido,
3. elige una representación concreta,
4. formula el invariante principal,
5. indica el costo de la operación dominante,
6. indica una propiedad que tu elección mantiene,
7. indica una propiedad que deliberadamente no mantiene,
8. propone una alternativa razonable,
9. explica qué costo o propiedad empeora con la alternativa,
10. indica si tu argumento utiliza peor caso, costo esperado o costo amortizado.

Concluye:

> ¿Por qué preguntar "cuál es la mejor estructura de datos" no tiene sentido sin conocer antes el patrón de operaciones?.

#### Ejercicio 2. Una tabla de costos no sustituye el razonamiento

Considera estas operaciones conceptuales:

```text
ArrayList.get(i)
ArrayList.add(0,x)
DLList.addAfter(node,x)
Stack.push(x)
Queue.remove()
AVL.find(x)
BinaryHeap.add(x)
BinaryHeap.removeMin()
HashSet.contains(x)
TreeSet.contains(x)
GraphList.outEdges(v)
GraphMatrix.hasEdge(u,v)
BFS(source)
DFS(source)
```

Construye una tabla con:

| Operación | Invariante que utiliza | Mejor caso relevante | Peor caso | Esperado/amortizado si aplica | Memoria auxiliar |
|---|---|---:|---:|---|---|

Después responde:

1. ¿qué operaciones dependen de la altura de una estructura?,
2. ¿qué operaciones dependen del grado de un vértice?,
3. ¿qué operaciones dependen de una política FIFO o LIFO?,
4. ¿cuáles dependen de hashing?,
5. ¿cuáles tienen una cota amortizada relevante?,
6. ¿cuáles tienen una cota esperada relevante?,
7. ¿qué dos operaciones pueden tener el mismo `O(log n)` pero utilizar invariantes completamente distintos?,
8. ¿qué dos recorridos tienen el mismo `O(V+E)` pero producen órdenes diferentes?,
9. ¿por qué dos operaciones con la misma notación asintótica pueden realizar cantidades concretas de trabajo muy distintas?,
10. ¿por qué una tabla de complejidades memorizada no permite elegir correctamente una estructura sin conocer las operaciones del problema?.

### B. Retos integradores

#### Reto 1. Agenda de trabajos con cuatro estructuras coordinadas

Se desea implementar un sistema de trabajos con:

```text
id
priority
owner
minutes
```

Requisitos:

```text
1. agregar un trabajo nuevo
2. extraer el trabajo con menor priority
3. si priority empata, atender menor id
4. consultar en O(1) esperado si un id existe
5. consultar cuántos trabajos pertenecen a cada owner
6. listar los ids de un owner no necesita estar ordenado
```

Se propone mantener:

```text
BinaryHeap<Ticket> heap
HashMap<Integer,Ticket> byId
HashMap<String,Integer> countByOwner
int totalPendingMinutes
```

No escribas primero el código.

1. explica qué consulta resuelve cada estructura,
2. formula el invariante del heap,
3. formula el invariante entre `heap` y `byId`,
4. formula el invariante de `countByOwner`,
5. formula el invariante de `totalPendingMinutes`,
6. diseña conceptualmente `addTicket`,
7. diseña conceptualmente `removeNext`,
8. identifica todas las estructuras que deben actualizarse en cada operación,
9. construye un ejemplo donde actualizar el heap pero olvidar `byId` deje el sistema inconsistente,
10. construye otro donde `countByOwner` quede incorrecto,
11. determina el costo de cada operación,
12. identifica qué parte es O(1) esperado y qué parte es O(log n),
13. explica por qué mantener información redundante puede acelerar consultas,
14. explica qué nuevos riesgos de correctitud introduce esa redundancia,
15. diseña tres invariantes que podrían comprobarse mediante assertions en una versión de depuración.

No utilices `java.util.PriorityQueue`.

#### Reto 2. El índice rápido quedó desincronizado

Se mantiene una colección de estudiantes:

```java
List<Student> students;
Map<Integer,Integer> positionById;
```

La intención es que:

```text
positionById[id]
```

indique la posición actual del estudiante dentro de `students`.

Un estudiante implementa:

```java
void removeAt(int i) {
    students.remove(i);
}
```

y no actualiza el Map.

1. formula el invariante entre la lista y el Map,
2. construye el ejemplo mínimo donde el error sea observable,
3. explica por qué `students` puede seguir siendo una lista válida,
4. explica por qué el sistema completo ya es incorrecto,
5. determina qué posiciones cambian al eliminar en el medio,
6. diseña una reparación correcta,
7. analiza su costo,
8. explica por qué el Map no convierte mágicamente la eliminación indexada en `O(1)`,
9. propone una representación alternativa si las eliminaciones arbitrarias fueran extremadamente frecuentes,
10. relaciona el problema con:
   - desplazamientos de la Semana 1,
   - referencias de la Semana 2,
   - información redundante almacenada en AVL.

#### Reto 3. Un historial con deshacer, rehacer y unicidad

Se diseña un editor con:

```text
undo
redo
documentos abiertos
```

Requisitos:

```text
undo
    revierte la operación más reciente

redo
    reaplica la operación más recientemente deshecha

open(id)
    no permite abrir dos veces el mismo documento

close(id)
    elimina el documento del conjunto de abiertos
```

1. elige estructuras para `undo`, `redo` y `openDocuments`,
2. justifica cada elección,
3. formula un invariante para cada estructura,
4. explica qué debe ocurrir con `redo` después de ejecutar una operación nueva,
5. construye una secuencia:
   ```text
   edit A
   edit B
   undo
   edit C
   ```
   y determina los estados de ambas pilas,
6. explica por qué Queue sería incorrecta para `undo`,
7. explica por qué TreeSet sería innecesario si el orden de documentos abiertos no importa,
8. determina costos esperados,
9. diseña una versión donde además se necesite listar documentos en orden creciente de id,
10. identifica qué estructura cambiarías y qué costo aparece.

#### Reto 4. Un sistema de frecuencias con memoria limitada

Se recibe una secuencia de enteros.

Se desea mantener en todo momento:

```text
frecuencia de cada valor visto
cantidad de valores distintos
valor con mayor frecuencia actual
```

Condiciones:

```text
no se permite recorrer toda la historia después de cada inserción
el orden de llegada no necesita conservarse
```

Diseña una solución usando estructuras estudiadas.

1. identifica qué información puede mantener un `Map<Integer,Integer>`,
2. determina cómo obtener la cantidad de valores distintos en `O(1)`,
3. explica por qué recalcular el máximo recorriendo todo el Map después de cada inserción puede ser costoso,
4. propone una estrategia adicional para mantener el máximo,
5. si eliges un heap, explica qué ocurre cuando cambia la frecuencia de una clave ya presente,
6. identifica el problema de claves obsoletas si insertas una nueva pareja `(frequency,value)` en cada actualización,
7. diseña una estrategia para detectar entradas obsoletas al extraer,
8. formula los invariantes coordinados,
9. analiza tiempo y memoria,
10. compara con una solución que simplemente recorra el Map al final.

No es necesario implementar una operación de `decreaseKey` o `increaseKey`.

#### Reto 5. AVL, HashSet o TreeSet bajo tres cargas de trabajo

Se almacenan identificadores distintos.

Carga A:

```text
90 % contains
10 % add
no importa el orden
```

Carga B:

```text
40 % contains
20 % add
20 % predecessor
20 % successor
```

Carga C:

```text
20 % contains
10 % add
70 % recorrido completo ordenado
```

Compara:

```text
HashSet
TreeSet
AVL propio
```

Para cada carga:

1. elige una estructura,
2. justifica el costo dominante,
3. indica qué propiedad adicional ofrece TreeSet/AVL,
4. explica cuándo esa propiedad no compensa su costo,
5. distingue O(1) esperado de O(log n) garantizado bajo el modelo estudiado,
6. explica por qué HashSet no puede responder sucesor eficientemente,
7. explica por qué un heap tampoco sustituye a un árbol ordenado,
8. propone qué cambiaría si se necesitara solamente el mínimo.

#### Reto 6. El heap es correcto, pero el comparador no representa el requisito

Un sistema de tareas necesita:

```text
mayor prioridad numérica primero
si hay empate:
    menor id primero
```

Un estudiante reutiliza:

```java
boolean less(Task a, Task b) {
    if (a.priority != b.priority) {
        return a.priority < b.priority;
    }
    return a.id < b.id;
}
```

y construye un min-heap sobre ese comparador.

1. explica qué orden implementa realmente,
2. construye tres tareas que hagan visible el error,
3. distingue:
   - heap estructuralmente válido,
   - política de prioridad incorrecta,
4. corrige conceptualmente el comparador,
5. explica por qué `bubbleUp` y `trickleDown` no necesitan cambiar si usan siempre el comparador,
6. formula el invariante correcto en términos de la relación elegida,
7. explica por qué la correctitud de una estructura depende también del contrato semántico del comparador,
8. relaciona esta idea con el invariante de orden de BST.

#### Reto 7. Rehashing correcto, metadata incorrecta

Una tabla hash con encadenamiento mantiene:

```text
table
size
capacity
```

Durante `resize()` se reconstruyen correctamente todos los buckets usando la nueva capacidad, pero el estudiante ejecuta:

```text
size = 0
```

y nunca vuelve a incrementarlo porque considera que las entradas "ya existían".

1. explica qué parte física queda correcta,
2. explica qué parte lógica queda incorrecta,
3. construye un ejemplo con cinco claves,
4. muestra el estado antes y después de resize,
5. formula el invariante entre `size` y el contenido lógico,
6. determina qué operaciones posteriores pueden fallar aunque `contains` siga funcionando,
7. explica por qué reinserción física no equivale a nueva inserción lógica,
8. relaciona el defecto con:
   - tamaño/capacidad de Semana 1,
   - rehashing de Semana 9,
   - invariantes coordinados de Semana 13,
9. analiza el costo del resize,
10. indica qué parte del costo global es amortizada.

#### Reto 8. Nombres externos, ids internos y dos recorridos de grafo

Una red de colaboración utiliza nombres:

```text
"ana"
"beto"
"carla"
"diego"
"elena"
"fabio"
"gina"
```

Se desea mantener:

```java
Map<String,Integer> nameToId;
List<String> idToName;
Graph graph;
```

Requisitos:

```text
A. saber si dos personas están conectadas por algún camino
B. obtener el número mínimo de relaciones desde una persona hasta otra
C. listar las componentes conexas
D. presentar los resultados nuevamente con nombres
```

1. formula el invariante entre `nameToId` e `idToName`,
2. decide lista o matriz de adyacencia para un grafo disperso,
3. elige BFS o DFS para A,
4. elige BFS o DFS para B,
5. elige BFS o DFS para C,
6. explica por qué una misma representación admite ambos recorridos,
7. diseña el estado auxiliar de cada operación,
8. analiza el costo del mapeo de nombres,
9. analiza el costo de cada recorrido,
10. explica por qué `HashMap` no sustituye al `Graph`,
11. explica por qué BFS y DFS pueden responder A,
12. explica por qué DFS no garantiza la respuesta correcta para B si se toma el primer camino encontrado,
13. construye un grafo pequeño donde DFS encuentre primero un camino más largo.

#### Reto 9. Grafo dinámico: ¿recalcular o mantener información?

Se tiene un grafo no dirigido de `V` vértices.

Inicialmente se calcula:

```text
componentId[v]
```

para todos los vértices.

Después se agregan aristas nuevas, pero nunca se eliminan.

Un estudiante propone volver a ejecutar un recorrido completo después de cada nueva arista.

1. determina el costo de una recomputación completa,
2. explica cuándo una nueva arista no cambia el número de componentes,
3. explica cuándo puede reducirlo en uno,
4. identifica qué información de `componentId` permitiría detectar conceptualmente ambos casos,
5. analiza el problema de actualizar todos los ids de una componente después de fusionar dos componentes,
6. compara:
   - recomputar siempre,
   - mantener metadata incremental simple,
7. explica qué compromiso tiempo/memoria aparece,
8. indica por qué una solución óptima para muchas actualizaciones conduciría a una estructura no estudiada todavía,
9. sin nombrar ni implementar esa estructura, explica qué operaciones abstractas necesitaríamos.

El objetivo es reconocer el límite de las estructuras disponibles, no introducir un algoritmo nuevo.

#### Reto 10. DFS recursivo frente a iterativo bajo restricciones de memoria

Se tienen dos grafos con el mismo número de vértices y aristas.

Grafo A:

```text
0 -> 1 -> 2 -> ... -> V-1
```

Grafo B:

```text
0 -> 1
0 -> 2
...
0 -> V-1
```

Se exige recorrer todos los alcanzables.

1. compara la profundidad máxima del DFS recursivo,
2. compara el tiempo asintótico,
3. compara el espacio lógico de visitados,
4. explica por qué el riesgo de `StackOverflowError` es muy distinto,
5. diseña un DFS iterativo con `Deque`,
6. explica qué parte de la pila pasa de implícita a explícita,
7. compara marcar al `push` frente a marcar al `pop`,
8. construye un grafo donde marcar al `pop` produzca inserciones duplicadas,
9. explica por qué el algoritmo puede seguir siendo correcto,
10. distingue:
   - memoria asintótica,
   - límite práctico de la pila de ejecución,
   - trabajo redundante.

#### Reto 11. Un sistema parece O(1), pero esconde una operación lineal

Se mantiene:

```java
Map<Integer,Student> byId;
List<Student> orderedByArrival;
```

Un método:

```java
boolean removeStudent(int id) {
    Student s = byId.remove(id);
    if (s == null) return false;
    orderedByArrival.remove(s);
    return true;
}
```

Supón que `ArrayList.remove(Object)` debe buscar el objeto.

1. analiza el costo de `byId.remove(id)`,
2. analiza el costo de localizar `s` en la lista,
3. analiza el costo de desplazar después de eliminar,
4. obtiene el costo total,
5. explica por qué "usar HashMap hace remove O(1)" es una afirmación incorrecta para la operación compuesta,
6. propone una representación alternativa si conservar el orden de llegada fuera obligatorio,
7. propone otra si el orden de llegada dejara de importar,
8. identifica los nuevos invariantes de cada alternativa,
9. explica por qué la complejidad de un método debe analizar todas las estructuras que modifica.

#### Reto 12. La batería de pruebas debe revelar invariantes rotos

Se recibe un repositorio desconocido que implementa:

```text
DynamicArray
AVL
BinaryHeap
HashSet conceptual
Graph con lista de adyacencia
```

Solo puedes ejecutar un número pequeño de pruebas.

Diseña una batería que revele al menos un posible defecto en cada categoría:

```text
DynamicArray
    resize pierde un elemento

AVL
    una rotación conserva BST pero deja altura incorrecta

BinaryHeap
    removeMin devuelve el mínimo pero deja el heap roto

HashSet
    resize conserva size pero ubica una clave en bucket incorrecto

Graph
    addUndirectedEdge agrega solamente una dirección

BFS
    marca al retirar y encola repetidamente

DFS
    marca BLACK antes de terminar vecinos
```

Para cada prueba indica:

1. estado inicial mínimo,
2. secuencia mínima de operaciones,
3. resultado observable,
4. invariante que la prueba intenta falsar,
5. por qué un `main()` que solo verifica una salida final podría no detectar el defecto.

Concluye:

> ¿Por qué probar propiedades internas mediante estados pequeños puede ser más informativo que probar solamente entradas grandes?.

#### Reto 13. Mismo orden asintótico, distinto trabajo real

Compara estas situaciones:

```text
A. add(0,x) en un ArrayList con n = 100000
B. add(n,x) con capacidad disponible
C. AVL.find(x) con altura 17
D. BinaryHeap.add(x) cuando no sube
E. BinaryHeap.add(x) cuando sube hasta la raíz
F. HashSet.contains(x) en bucket de longitud 1
G. HashSet.contains(x) en bucket de longitud 25
H. BFS sobre un grafo con V=100000, E=100005
I. BFS sobre V=100000, E=9000000
```

1. indica la cota asintótica relevante,
2. estima qué trabajo concreto domina,
3. identifica casos cercanos al mejor comportamiento,
4. identifica casos cercanos al peor comportamiento,
5. explica por qué `O(log n)` no significa realizar exactamente `log n` intercambios,
6. explica por qué `O(V+E)` puede describir recorridos con cantidades muy diferentes de trabajo,
7. distingue análisis asintótico de traza concreta,
8. explica qué información adicional necesitas para estimar tiempo real.

#### Reto 14. Diseñar un índice doble sin inconsistencias

Se almacenan objetos:

```text
Book {
    isbn
    year
    title
}
```

Requisitos:

```text
buscar por isbn rápidamente
recorrer por year de menor a mayor
no permitir dos libros con el mismo isbn
```

Se propone:

```text
HashMap<String,Book> byIsbn
AVL<Book> byYear
```

donde el AVL ordena por:

```text
(year, isbn)
```

1. explica por qué hacen falta dos índices,
2. formula el invariante entre ambos,
3. diseña conceptualmente `addBook`,
4. diseña `removeByIsbn`,
5. explica qué ocurre si se actualiza el `year` de un libro,
6. demuestra por qué modificar `year` en el objeto sin reubicarlo en AVL rompe el invariante,
7. determina el costo de cada operación,
8. construye una ejecución donde un índice se actualiza y el otro no,
9. diseña una estrategia de recuperación si la segunda actualización falla,
10. explica por qué la integración de estructuras introduce problemas parecidos a mantener índices en un sistema real.

No es necesario implementar transacciones.

#### Reto 15. Diseño final: elegir una arquitectura de estructuras

Debes diseñar el núcleo de una plataforma académica que mantiene:

```text
estudiantes
cursos
solicitudes
prerrequisitos
```

Operaciones:

```text
1. buscar estudiante por código
2. impedir códigos duplicados
3. mantener solicitudes de matrícula por prioridad
4. conocer si una solicitud ya fue atendida
5. obtener cantidad total de minutos de atención
6. representar prerrequisitos entre cursos
7. determinar qué cursos son alcanzables desde un curso dado
8. detectar grupos desconectados en una versión no dirigida de una red de colaboración
9. listar ciertos códigos en orden creciente
10. permitir deshacer la última modificación administrativa
```

Solo puedes elegir entre estructuras estudiadas en las Semanas 1 a 13.

Entrega un diseño, no una implementación completa.

Para cada operación:

1. elige un ADT,
2. elige una representación,
3. justifica la elección,
4. formula el invariante relevante,
5. determina la complejidad,
6. indica si la cota es peor caso, esperada o amortizada.

Después:

7. identifica qué estructuras contienen información redundante,
8. formula al menos tres invariantes que relacionen estructuras distintas,
9. construye un ejemplo de inconsistencia entre dos índices,
10. identifica qué operación sería la más costosa,
11. indica qué requisito cambiaría tu elección de representación,
12. explica qué parte del sistema reutiliza BFS,
13. explica qué parte reutiliza DFS,
14. explica dónde HashSet es preferible a TreeSet,
15. explica dónde AVL/TreeSet es preferible a HashSet,
16. explica dónde un BinaryHeap es preferible a un BST,
17. explica por qué ninguna estructura estudiada es universalmente mejor que las demás.

Concluye con una tabla:

| Necesidad | ADT | Representación | Invariante | Costo |
|---|---|---|---|---|

### C. Ampliaciones opcionales

#### Ampliación 1. Selección con max-heap de tamaño `k`

Sin utilizar la solución de `kthSmallest(...)` trabajada en clase, diseña otra estrategia para obtener el `k`-ésimo menor de un flujo de `n` enteros.

Restricciones:

```text
solo puedes conservar O(k) elementos auxiliares
no puedes ordenar todo el flujo
```

Utiliza conceptualmente un:

```text
max-heap de tamaño k
```

1. explica qué representa la raíz,
2. indica qué hacer con los primeros `k` elementos,
3. indica qué hacer cuando llega un valor mayor o igual que la raíz,
4. indica qué hacer cuando llega un valor menor,
5. formula el invariante del heap,
6. demuestra por qué al final la raíz es el `k`-ésimo menor,
7. analiza tiempo,
8. analiza espacio,
9. compara con un min-heap de los `n` elementos,
10. determina para qué relaciones entre `k` y `n` puede ser atractiva esta estrategia.

#### Ampliación 2. Quickselect como selección parcial

Se desea obtener el elemento de rango `k` sin ordenar completamente.

Considera el arreglo:

```text
[11, 4, 8, 2, 15, 7, 3, 10]
```

y una operación conceptual:

```text
partition(a, left, right, pivot)
```

que deja el pivote en su posición final y coloca:

```text
menores a la izquierda
mayores a la derecha
```

1. realiza manualmente una partición con pivote `7`,
2. determina la posición final del pivote,
3. si buscamos el 3.er menor, indica qué región puede descartarse,
4. explica por qué no necesitamos ordenar la región descartada,
5. repite conceptualmente hasta localizar la posición,
6. compara con Quicksort,
7. explica por qué Quickselect puede tener O(n) esperado,
8. construye intuitivamente una secuencia de pivotes desfavorables,
9. explica por qué puede degradarse a O(n^2),
10. compara con la estrategia basada en heap.

No se requiere implementar Quickselect completo.

#### Ampliación 3. Tres índices para una misma colección

Se mantienen objetos:

```text
Task {
    id
    priority
    owner
}
```

Requisitos:

```text
consultar por id
extraer por prioridad
listar owners ordenados sin repetidos
```

Se propone mantener simultáneamente:

```text
HashMap<Integer,Task>
BinaryHeap<Task>
TreeSet<String>
```

1. explica la responsabilidad de cada estructura,
2. formula un invariante entre Map y heap,
3. formula un invariante entre Map y TreeSet,
4. identifica un problema: un owner puede tener varias tareas,
5. demuestra por qué eliminar una tarea no siempre permite eliminar su owner del TreeSet,
6. propone metadata adicional para saber cuántas tareas tiene cada owner,
7. formula el nuevo invariante,
8. diseña `addTask`,
9. diseña conceptualmente `removeNext`,
10. analiza los costos,
11. identifica cuántas estructuras deben cambiar en la peor operación,
12. explica por qué agregar un índice acelera una consulta pero aumenta el costo y la complejidad de las actualizaciones.

