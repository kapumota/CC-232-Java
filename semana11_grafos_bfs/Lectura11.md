### Lectura: grafos, representaciones y BFS

Esta lectura consolida y amplía las ideas trabajadas en la Semana 11 de CC232.

Durante las diez semanas anteriores hemos cambiado de representación, de invariante o de ADT cuando cambió el tipo de operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico.

```text
arreglo + tamaño lógico + capacidad
```

La representación permitió acceso directo por índice y obligó a distinguir entre estado lógico, almacenamiento físico, crecimiento y costo amortizado.

En la Semana 2 utilizamos nodos enlazados.

```text
nodo -> nodo -> nodo
```

La modificación local se volvió barata cuando ya conocíamos las referencias apropiadas, aunque localizar una posición dejó de ser directo.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`.

La pregunta pasó a ser:

```text
¿qué operaciones permite el ADT sobre una secuencia?
```

La política FIFO de `Queue` será especialmente importante en esta semana.

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda.

```text
subárbol izquierdo < nodo < subárbol derecho
```

El invariante de orden permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones.

En la Semana 6 estudiamos AVL.

El BST seguía siendo correcto, pero añadimos altura y balance para impedir que una mala forma del árbol destruyera la eficiencia.

En la Semana 7 estudiamos `Priority Queue` y `BinaryHeap`.

La pregunta cambió a:

```text
¿cómo mantener disponible el elemento mínimo?
```

El heap respondió mediante:

```text
forma completa + invariante min-heap
```

En la Semana 8 estudiamos hashing con encadenamiento separado.

La pregunta fue:

```text
¿cómo localizar una clave exacta sin mantener orden global?
```

En la Semana 9 mantuvimos la misma tabla hash y estudiamos qué ocurre cuando aumenta la carga.

Aparecieron:

```text
factor de carga
crecimiento
resize
rehashing
```

En la Semana 10 reutilizamos esa búsqueda por clave para formular dos ADT:

```text
Map<K,V>
    clave -> valor

Set<T>
    elemento -> pertenece / no pertenece
```

La Semana 11 cambia nuevamente la pregunta.

Ya no queremos representar principalmente una secuencia, una jerarquía, una prioridad, una asociación o un conjunto.

Queremos representar:

```text
objetos + relaciones entre objetos
```

Esta necesidad conduce a los **grafos**.

La idea central de la semana puede anticiparse así:

```text
Graph
    define qué operaciones queremos ofrecer

lista de adyacencia
    guarda, para cada vértice, sus vecinos

matriz de adyacencia
    guarda una posición para cada par posible de vértices

Queue
    determina el orden FIFO de la exploración BFS

seen
    representa qué vértices ya fueron descubiertos

BFS
    recorre por niveles y permite calcular distancias
    en número de aristas en grafos no ponderados
```

El objetivo no es memorizar vocabulario de teoría de grafos ni aprender una colección extensa de algoritmos.

El objetivo es continuar la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    cómo almacenamos el estado

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo consultamos, modificamos y recorremos el estado

complejidad
    cuánto trabajo exige cada operación
```

Al finalizar la lectura deberías poder explicar qué representa `G = (V,E)`, distinguir grafos dirigidos y no dirigidos, reconstruir una lista y una matriz de adyacencia, justificar sus costos, explicar por qué BFS utiliza una `Queue`, formular el invariante de los vértices descubiertos, calcular distancias mínimas en número de aristas y relacionar el costo del recorrido con la representación utilizada.

### 1. El nuevo problema: representar relaciones

Considera un sistema con cinco computadoras:

```text
0
1
2
3
4
```

Saber que existen cinco computadoras no describe cómo pueden comunicarse.

Necesitamos representar relaciones como:

```text
0 se comunica con 1
0 se comunica con 3
1 se comunica con 2
3 se comunica con 4
```

Una secuencia:

```text
[0, 1, 2, 3, 4]
```

solo conserva los objetos.

No conserva las relaciones.

Una tabla:

```text
0 -> 1
0 -> 3
1 -> 2
3 -> 4
```

sí empieza a expresar conexiones.

El problema general es:

```text
dado un conjunto de objetos,
¿qué pares de objetos están relacionados?
```

Este patrón aparece en muchas aplicaciones.

Por ejemplo:

```text
ciudad -> ciudades conectadas por una carretera
computadora -> computadoras conectadas por una red
curso -> cursos con conflicto de horario
persona -> personas relacionadas
archivo -> archivos que dependen de él
página -> páginas enlazadas
```

Un grafo abstrae el significado concreto de la relación y conserva la estructura de conexiones.

### 2. Grafo G = (V,E)

Un grafo puede describirse como:

```text
G = (V, E)
```

donde:

```text
V
    conjunto de vértices

E
    conjunto de aristas
```

Los **vértices** representan los objetos.

Las **aristas** representan las relaciones.

Por ejemplo:

```text
V = {0, 1, 2, 3}

E = {
    (0,1),
    (0,3),
    (1,2)
}
```

Podemos dibujarlo conceptualmente como:

```text
0 ----> 1 ----> 2
|
+----> 3
```

El dibujo es útil para razonar.

La representación dentro del programa será distinta.

Esta separación ya es conocida:

```text
objeto matemático/lógico != representación concreta en memoria
```

### 3. Vértices y aristas

Un vértice no necesita ser físicamente un círculo.

El círculo pertenece al dibujo.

Dentro de un programa puede representarse mediante:

```text
un entero
una cadena
un objeto
un registro
```

En los archivos de la Semana 11 utilizaremos enteros.

Una arista tampoco necesita existir como un objeto Java independiente.

Puede representarse mediante:

```text
una posición true en una matriz
```

o:

```text
la presencia de un entero dentro de una lista de vecinos
```

Por tanto:

```text
vértice
    concepto lógico

arista
    concepto lógico

entero, lista, boolean[][]
    decisiones de representación
```

### 4. Vértices identificados por 0..n-1

Los dos archivos de la semana utilizan vértices:

```text
0, 1, 2, ..., n - 1
```

Si:

```text
n = 5
```

los vértices válidos son:

```text
0
1
2
3
4
```

No son válidos:

```text
-1
5
7
```

Esta decisión permite reutilizar acceso directo por índice.

Por ejemplo:

```text
adjacency.get(3)
```

puede seleccionar la colección de vecinos del vértice `3`.

También:

```text
seen[3]
```

puede indicar directamente si el vértice `3` ya fue descubierto.

Por tanto, la identificación compacta:

```text
0..n-1
```

conecta grafos con la Semana 1.

### 5. Invariante de rango

Si los vértices son:

```text
0..nVertices()-1
```

una propiedad fundamental es:

```text
0 <= vertex < nVertices()
```

Esta propiedad debe cumplirse cada vez que una operación recibe un vértice.

En `Semana11_GrafoRepresentacion0.java` aparece un método:

```java
private void checkVertex(int vertex)
```

Su función no es algorítmicamente profunda.

Su función es proteger el invariante de rango.

Una operación como:

```text
addEdge(source, destination)
```

debe validar ambos extremos:

```text
source
destination
```

porque ambos representan vértices.

### 6. Grafo dirigido

Una arista dirigida posee orientación.

Escribimos:

```text
u -> v
```

y distinguimos:

```text
u
    origen/source

v
    destino/destination
```

La existencia de:

```text
u -> v
```

no implica:

```text
v -> u
```

Por ejemplo:

```text
0 -> 1
```

puede existir sin:

```text
1 -> 0
```

Esto resulta natural para relaciones como:

```text
página A enlaza a página B
curso A es prerrequisito de curso B
proceso A envía un mensaje a proceso B
```

El primer archivo de la semana modela directamente aristas dirigidas.

### 7. Grafo no dirigido

En otras aplicaciones la relación es simétrica.

Por ejemplo:

```text
0 -- 1
```

significa que `0` está relacionado con `1` y `1` está relacionado con `0`.

Una forma común de representar esta relación utilizando una estructura para aristas dirigidas es guardar:

```text
0 -> 1
1 -> 0
```

Por tanto:

```text
arista lógica no dirigida {u,v}
```

puede convertirse en:

```text
u -> v
v -> u
```

Esta es la idea utilizada por:

```java
addUndirectedEdge(a, b)
```

en `Semana11_GrafoBFS1.java`.

### 8. Una relación simétrica también es un invariante

Si una estructura afirma representar un grafo no dirigido mediante dos aristas dirigidas, debe conservar:

```text
v pertenece a vecinos[u]
si y solo si
u pertenece a vecinos[v]
```

La simetría no aparece automáticamente.

Debe ser preservada por las operaciones.

Por eso:

```java
addUndirectedEdge(a, b)
```

realiza conceptualmente dos modificaciones.

Esto ilustra nuevamente una idea del curso:

```text
una operación pública
puede requerir varias modificaciones internas
para conservar un invariante
```

### 9. Camino y alcanzabilidad

Para comprender BFS necesitamos una cantidad mínima de vocabulario adicional.

Un **camino** desde `u` hasta `v` es una secuencia de vértices conectados por aristas consecutivas.

Por ejemplo:

```text
0 -> 1 -> 3 -> 5
```

es un camino desde `0` hasta `5`.

Su longitud en número de aristas es:

```text
3
```

Decimos que `v` es **alcanzable** desde `u` si existe algún camino desde `u` hasta `v`.

La pregunta:

```text
¿v es alcanzable desde source?
```

será una de las preguntas fundamentales de un recorrido.

### 10. Ciclos y por qué importan durante un recorrido

Un grafo puede contener ciclos.

Por ejemplo:

```text
0 -> 1
^    |
|    v
3 <- 2
```

contiene:

```text
0 -> 1 -> 2 -> 3 -> 0
```

Si recorremos vecinos sin recordar qué vértices ya fueron descubiertos, podríamos repetir indefinidamente:

```text
0
1
2
3
0
1
2
3
...
```

Por tanto, en grafos aparece una necesidad que no era obligatoria en el recorrido de un árbol:

```text
recordar qué vértices ya fueron descubiertos
```

Esta necesidad conducirá a `seen`.

### 11. El ADT Graph

Antes de decidir cómo almacenar las aristas, podemos definir qué operaciones deseamos ofrecer.

El primer archivo de la semana declara:

```java
interface Graph {
    int nVertices();
    void addEdge(int source, int destination);
    void removeEdge(int source, int destination);
    boolean hasEdge(int source, int destination);
    List<Integer> outEdges(int vertex);
    int outDegree(int vertex);
}
```

Esta interfaz describe el ADT que utilizaremos durante la semana.

No menciona:

```text
ArrayList
boolean[][]
HashSet
LinkedList
```

porque esas son decisiones de implementación.

### 12. nVertices()

La operación:

```java
int nVertices()
```

responde:

```text
¿cuántos vértices existen?
```

Si:

```text
V = {0,1,2,3,4}
```

entonces:

```text
nVertices() -> 5
```

En nuestras representaciones, este valor también determina el rango válido:

```text
0 <= vertex < nVertices()
```

### 13. addEdge(source,destination)

La operación:

```java
addEdge(source, destination)
```

representa la intención lógica:

```text
agregar source -> destination
```

El ADT no obliga a guardar una arista de una forma específica.

En una matriz puede significar:

```text
matrix[source][destination] = true
```

En una lista puede significar:

```text
destination debe pertenecer a adjacency[source]
```

Mismo comportamiento lógico.

Distinta representación.

### 14. removeEdge(source,destination)

La operación:

```java
removeEdge(source, destination)
```

hace que la relación:

```text
source -> destination
```

deje de pertenecer al grafo.

En una matriz:

```text
true -> false
```

En una lista:

```text
destination deja de aparecer
en la colección de vecinos de source
```

La operación lógica es la misma.

El costo puede ser diferente.

### 15. hasEdge(source,destination)

La operación:

```java
hasEdge(source, destination)
```

responde:

```text
¿existe source -> destination?
```

Esta consulta permitirá observar con claridad el compromiso entre las dos representaciones.

En una matriz la posición es directa.

En una lista puede ser necesario buscar dentro de los vecinos de `source`.

### 16. outEdges(vertex)

La operación:

```java
outEdges(vertex)
```

devuelve los destinos de las aristas salientes.

Si tenemos:

```text
0 -> 1
0 -> 3
0 -> 4
```

entonces:

```text
outEdges(0) -> [1,3,4]
```

El orden de esa lista no pertenece necesariamente al ADT `Graph`.

Lo esencial es qué vecinos aparecen.

Esta operación será especialmente importante para BFS:

```text
procesar vertex
    ->
recorrer outEdges(vertex)
```

### 17. outDegree(vertex)

El grado de salida de `u` es la cantidad de aristas que salen de `u`.

Escribiremos conceptualmente:

```text
deg(u)
```

o, cuando queramos enfatizar dirección:

```text
outdeg(u)
```

Si:

```text
outEdges(0) = [1,3,4]
```

entonces:

```text
outDegree(0) = 3
```

El grado será útil tanto para describir el grafo como para expresar complejidad.

Por ejemplo:

```text
buscar un vecino dentro de adjacency[u]
    -> O(deg(u))
```

### 18. ADT frente a representación

Podemos resumir:

```text
Graph
    comportamiento

AdjacencyListGraph
    una implementación

AdjacencyMatrixGraph
    otra implementación
```

Ambas deben poder responder:

```text
nVertices
addEdge
removeEdge
hasEdge
outEdges
outDegree
```

pero pueden hacerlo mediante estados internos distintos.

Esta comparación es una continuación directa de:

```text
Set
    -> HashSet
    -> TreeSet
```

de la Semana 10.

### 19. Primera representación: lista de adyacencia

La clase:

```java
AdjacencyListGraph
```

mantiene:

```java
private final List<List<Integer>> adjacency;
```

La colección exterior tiene una posición por vértice.

La colección interior guarda los vecinos salientes.

Por ejemplo:

```text
adjacency

0 -> [1,3]
1 -> [2]
2 -> []
3 -> [4]
4 -> []
```

significa:

```text
0 -> 1
0 -> 3
1 -> 2
3 -> 4
```

### 20. Lista externa indexada por vértice

La colección exterior puede pensarse como:

```text
adjacency[0]
adjacency[1]
adjacency[2]
...
adjacency[V-1]
```

Por tanto:

```java
adjacency.get(u)
```

localiza directamente la colección asociada a `u`.

Esto reutiliza:

```text
índice -> posición
```

de la Semana 1.

El costo de localizar la lista no depende del número total de aristas.

Lo que puede costar más es trabajar dentro de esa lista.

### 21. Lista de vecinos

La lista interior representa:

```text
destinos de las aristas salientes
```

Para:

```text
adjacency[3] = [1,4,7]
```

interpretamos:

```text
3 -> 1
3 -> 4
3 -> 7
```

No debemos interpretar la lista como:

```text
3 -> 1 -> 4 -> 7
```

Eso sería un camino.

La lista representa tres relaciones independientes que tienen el mismo origen.

### 22. Invariante de adyacencia

La propiedad principal puede formularse así:

```text
v pertenece a adjacency[u]
si y solo si
el grafo contiene la arista u -> v
```

Este invariante conecta:

```text
estado físico
```

con:

```text
significado lógico
```

Si una operación agrega una arista, debe hacer verdadero ese enunciado.

Si elimina una arista, debe hacerlo falso.

### 23. Aristas sin duplicados en AdjacencyListGraph

En `Semana11_GrafoRepresentacion0.java` se adopta una política adicional:

```text
una misma arista dirigida
aparece como máximo una vez
```

Por tanto:

```text
addEdge(0,1)
addEdge(0,1)
```

no debe producir:

```text
adjacency[0] = [1,1]
```

sino:

```text
adjacency[0] = [1]
```

Este requisito no es una propiedad universal de todo objeto llamado grafo.

Es una decisión del modelo estudiado en este archivo.

El estudiante debe distinguir:

```text
propiedad del ADT que estamos usando
```

de:

```text
política particular de una implementación
```

### 24. Reconstruir addEdge a partir de invariantes

El TODO del primer archivo no debe resolverse por ensayo y error.

Antes de escribir Java podemos derivar los pasos.

Queremos preservar:

```text
rango válido
+
adyacencia correcta
+
ausencia de aristas duplicadas
```

Por tanto, conceptualmente:

```text
1. validar source
2. validar destination
3. localizar la lista de vecinos de source
4. preguntar si destination ya pertenece
5. agregarlo solamente si no pertenece
```

La secuencia aparece como consecuencia de los invariantes.

No necesitamos memorizar una solución.

Necesitamos poder reconstruirla.

### 25. Costo de hasEdge con lista de adyacencia

Para ejecutar:

```text
hasEdge(u,v)
```

hacemos conceptualmente:

```text
localizar adjacency[u]
buscar v dentro de esa colección
```

Si:

```text
deg(u) = k
```

la búsqueda puede examinar hasta `k` vecinos.

Por tanto:

```text
hasEdge(u,v) -> O(deg(u))
```

La complejidad es más informativa que escribir simplemente:

```text
O(V)
```

porque solo recorremos los vecinos del origen.

### 26. Costo de addEdge en nuestra implementación

Una implementación que permitiera duplicados podría simplemente agregar al final.

Pero nuestro archivo exige:

```text
destination una sola vez
```

Por ello debemos comprobar primero si ya existe.

Entonces:

```text
búsqueda de duplicado
    O(deg(source))

inserción al final
    O(1) amortizado
```

El costo dominante es:

```text
addEdge(source,destination)
    -> O(deg(source))
```

Esta observación es importante porque una lectura externa puede analizar otra política de implementación y obtener otro costo.

La complejidad debe derivarse del código y de los invariantes concretos que realmente utilizamos.

### 27. Costo de removeEdge y outDegree con lista

Para eliminar:

```text
source -> destination
```

debemos encontrar `destination` dentro de:

```text
adjacency[source]
```

Por tanto:

```text
removeEdge -> O(deg(source))
```

En cambio, si la lista mantiene internamente su tamaño:

```java
adjacency.get(vertex).size()
```

puede responder directamente la cantidad de vecinos.

Por ello, en esta implementación:

```text
outDegree(vertex) -> O(1)
```

Recorrer todos los vecinos sigue costando:

```text
O(deg(vertex))
```

porque hay que procesarlos.

### 28. Espacio de una lista de adyacencia

Necesitamos:

```text
V listas
```

una por vértice.

Además almacenamos referencias a los destinos de las aristas.

Para un grafo dirigido:

```text
cada arista lógica se almacena una vez
```

Por tanto:

```text
espacio -> O(V + E)
```

Para un grafo no dirigido representado con dos direcciones:

```text
u -- v
```

se almacena como:

```text
u -> v
v -> u
```

y una arista lógica puede producir dos entradas.

Pero:

```text
2E -> O(E)
```

así que el espacio sigue siendo:

```text
O(V + E)
```

### 29. Segunda representación: matriz de adyacencia

La clase:

```java
AdjacencyMatrixGraph
```

mantiene:

```java
private final boolean[][] matrix;
```

Para `V` vértices crea una matriz:

```text
V x V
```

Cada fila representa un origen.

Cada columna representa un destino.

### 30. Invariante de matriz

La propiedad fundamental es:

```text
matrix[u][v] == true
si y solo si
existe la arista u -> v
```

Por ejemplo:

```text
      destino
       0 1 2 3
     +--------
0    | 0 1 0 1
1    | 0 0 1 0
2    | 0 0 0 0
3    | 0 0 0 0
```

representa:

```text
0 -> 1
0 -> 3
1 -> 2
```

La posición:

```text
matrix[0][3]
```

responde directamente a la pregunta:

```text
¿existe 0 -> 3?
```

### 31. Operaciones directas sobre una matriz

Agregar:

```text
u -> v
```

puede realizarse conceptualmente como:

```text
matrix[u][v] = true
```

Eliminar:

```text
matrix[u][v] = false
```

Consultar:

```text
matrix[u][v]
```

Por tanto:

```text
addEdge(u,v)     -> O(1)
removeEdge(u,v)  -> O(1)
hasEdge(u,v)     -> O(1)
```

Estas operaciones favorecen la consulta de un par concreto de vértices.

### 32. outEdges con matriz

La matriz tiene una dificultad distinta.

Para encontrar todos los vecinos de `u` debemos examinar:

```text
matrix[u][0]
matrix[u][1]
...
matrix[u][V-1]
```

Incluso si `u` tiene solamente un vecino, la fila contiene `V` posiciones posibles.

Por tanto:

```text
outEdges(u) -> O(V)
```

En la implementación de la semana, calcular el grado de salida también recorre la fila.

Entonces:

```text
outDegree(u) -> O(V)
```

Esta diferencia será importante al analizar BFS.

### 33. Espacio de una matriz

La matriz reserva una posición para cada par posible:

```text
(u,v)
```

Hay:

```text
V * V
```

posiciones.

Por tanto:

```text
espacio -> O(V^2)
```

Incluso si:

```text
E = 0
```

la matriz sigue reservando `V^2` posiciones.

Esta es la principal diferencia espacial con la lista de adyacencia.

### 34. Grafo disperso y grafo denso

Un grafo es **disperso** cuando contiene relativamente pocas aristas frente al número de pares posibles.

Conceptualmente:

```text
E mucho menor que V^2
```

Un grafo es **denso** cuando posee muchas de las aristas posibles.

Conceptualmente:

```text
E cercano al orden de V^2
```

No necesitamos una frontera numérica universal para esta semana.

La idea importante es:

```text
disperso
    muchas no-aristas

denso
    muchas aristas presentes
```

Una matriz paga memoria por ambas.

Una lista paga principalmente por las aristas existentes.

### 35. Lista frente a matriz

Podemos resumir las implementaciones de la semana.

```text
lista de adyacencia

espacio
    O(V + E)

hasEdge(u,v)
    O(deg(u))

addEdge sin duplicados
    O(deg(u))

removeEdge
    O(deg(u))

recorrer vecinos de u
    O(deg(u))

outDegree
    O(1) en nuestra implementación
```

En cambio:

```text
matriz de adyacencia

espacio
    O(V^2)

hasEdge(u,v)
    O(1)

addEdge
    O(1)

removeEdge
    O(1)

recorrer vecinos de u
    O(V)

outDegree
    O(V) en nuestra implementación
```

No existe una representación universalmente mejor.

### 36. Elegir la representación según las operaciones

Supongamos que el problema realiza continuamente:

```text
¿existe exactamente u -> v?
```

y el grafo es suficientemente pequeño o denso.

Una matriz puede ser razonable.

Supongamos que el problema realiza continuamente:

```text
dame los vecinos de u
```

en un grafo disperso.

Una lista de adyacencia evita examinar muchas no-aristas.

La pregunta correcta es:

```text
¿qué operaciones dominan el problema?
```

Esta es la misma disciplina aplicada anteriormente a:

```text
AVL
BinaryHeap
HashSet
TreeSet
```

### 37. El orden de los vecinos no es el contenido lógico del grafo

Considera:

```text
adjacency[0] = [1,3,4]
```

y:

```text
adjacency[0] = [4,1,3]
```

Si las demás listas representan las mismas relaciones, ambos estados pueden describir el mismo grafo lógico.

El orden dentro de la lista puede afectar:

```text
el orden concreto producido por un recorrido
```

pero no necesariamente:

```text
qué aristas existen
```

Esta distinción será visible en BFS.

### 38. El segundo archivo utiliza una representación más simple

`Semana11_GrafoBFS1.java` define una clase `Graph` pequeña con:

```java
private final List<List<Integer>> adjacency;
```

y:

```java
void addUndirectedEdge(int a, int b)
```

Su objetivo no es volver a estudiar toda la interfaz `Graph`.

Su objetivo es proporcionar una representación suficiente para concentrarse en BFS.

Por tanto, los dos archivos tienen funciones pedagógicas distintas:

```text
Semana11_GrafoRepresentacion0.java
    comparar representaciones y operaciones

Semana11_GrafoBFS1.java
    estudiar recorrido BFS y distancias
```

### 39. Una diferencia deliberada: duplicados entre los dos archivos

El primer archivo exige:

```text
agregar destination una sola vez
```

El segundo archivo utiliza:

```text
adjacency.get(a).add(b)
adjacency.get(b).add(a)
```

sin comprobar duplicados.

Esto no significa que los conceptos se contradigan.

Significa que las dos clases tienen objetivos didácticos distintos.

En el primer archivo estudiamos explícitamente el invariante de unicidad de aristas.

En el segundo nos concentramos en BFS.

Si aparecieran vecinos repetidos, el mecanismo de descubrimiento de BFS seguiría evitando que un vértice sea encolado repetidamente después de marcarlo, aunque recorreríamos entradas redundantes.

Por tanto:

```text
correctitud del descubrimiento
    puede mantenerse

eficiencia de la representación
    puede empeorar por redundancia
```

### 40. Del almacenamiento al recorrido

Hasta ahora hemos respondido preguntas como:

```text
¿existe u -> v?
¿cuáles son los vecinos de u?
¿cuántos vecinos salientes tiene u?
```

Ahora queremos responder otra clase de preguntas:

```text
¿qué vértices puedo alcanzar desde source?
```

o:

```text
¿en qué orden puedo explorar lo alcanzable?
```

o:

```text
¿cuántas aristas como mínimo separan source de v?
```

Estas preguntas requieren un **recorrido**.

### 41. Estados conceptuales durante un recorrido

Desde un origen `source`, un vértice puede estar conceptualmente en diferentes situaciones.

```text
no descubierto
```

Nunca hemos encontrado todavía un camino desde el origen hasta él.

```text
descubierto y pendiente
```

Ya sabemos cómo alcanzarlo, pero todavía no hemos procesado todos sus vecinos.

```text
procesado
```

Ya examinamos los vecinos que correspondían al recorrido.

Para el BFS sencillo de la semana no necesitamos materializar tres estados distintos.

Pero distinguirlos conceptualmente ayuda a entender el algoritmo.

### 42. seen como representación de pertenencia

Necesitamos responder repetidamente:

```text
¿v ya fue descubierto?
```

La Semana 10 formuló esta clase de pregunta mediante un `Set`.

Conceptualmente podríamos pensar:

```text
Set<Integer> discovered
```

y preguntar:

```text
contains(v)
```

Pero nuestros vértices son:

```text
0..V-1
```

Por tanto podemos utilizar una representación más directa:

```java
boolean[] seen;
```

con significado:

```text
seen[v] == false
    v todavía no fue descubierto

seen[v] == true
    v ya fue descubierto
```

### 43. ADT lógico frente a representación de seen

Este punto merece separarse.

La necesidad lógica es:

```text
pertenencia al conjunto de descubiertos
```

Una representación posible sería:

```text
HashSet<Integer>
```

Otra representación posible es:

```text
boolean[]
```

Como el universo es:

```text
0..V-1
```

el arreglo permite acceso directo:

```text
seen[v] -> O(1)
```

Esta decisión conecta tres semanas:

```text
Semana 1
    acceso directo por índice

Semana 10
    pertenencia

Semana 11
    pertenencia de vértices descubiertos
```

### 44. Queue vuelve a aparecer

Descubrir un vértice no significa procesarlo inmediatamente.

BFS necesita mantener una colección de vértices:

```text
ya descubiertos pero todavía pendientes
```

La política de selección es:

```text
FIFO
```

El primero descubierto entre los pendientes debe ser el primero procesado.

Por eso utilizamos el ADT:

```text
Queue
```

En Java el archivo declara:

```java
Queue<Integer> queue = new ArrayDeque<>();
```

La parte esencial es:

```text
Queue
```

`ArrayDeque` es una implementación concreta.

### 45. Por qué FIFO produce un BFS

Supongamos:

```text
        0
       / \
      1   2
      |   |
      3   4
          |
          5
```

Desde `0`:

```text
0
```

es descubierto primero.

Después descubrimos:

```text
1, 2
```

y ambos quedan delante de cualquier vértice que se descubra a partir de ellos.

Cuando procesamos `1`, podemos descubrir `3`.

La Queue contiene conceptualmente:

```text
[2, 3]
```

Por tanto `2`, que estaba a la misma distancia que `1`, se procesa antes que `3`.

Luego `2` puede descubrir `4`.

Así se forman niveles:

```text
nivel 0
    0

nivel 1
    1, 2

nivel 2
    3, 4

nivel 3
    5
```

FIFO es la política que preserva este avance por capas.

### 46. Esquema conceptual de BFS

Una versión conceptual de BFS puede escribirse así:

```text
marcar source como descubierto
encolar source

mientras la Queue no esté vacía:
    retirar el vértice frontal u

    para cada vecino v de u:
        si v todavía no fue descubierto:
            marcar v
            encolar v
```

El algoritmo combina dos ADT o ideas previas:

```text
Queue
    decide qué pendiente procesar después

seen
    decide si un vecino es nuevo
```

### 47. Inicialización desde source

BFS comienza en un vértice:

```text
source
```

Antes del bucle:

```text
source ya ha sido descubierto
```

Por tanto debemos establecer:

```text
seen[source] = true
```

y:

```text
source debe entrar en la Queue
```

Conceptualmente:

```text
descubierto
    ->
pendiente de procesar
```

### 48. Marcar antes de encolar

El orden de estas dos operaciones es fundamental.

La política correcta de esta semana es:

```text
marcar
    ->
encolar
```

No:

```text
encolar
    ->
marcar mucho después al retirar
```

Considera:

```text
    0
   / \
  1   2
   \ /
    3
```

Después de procesar `0`:

```text
Queue = [1,2]
```

Si `1` descubre `3` pero no lo marca todavía, podemos tener:

```text
Queue = [2,3]
```

Cuando `2` examine también a `3`, podría creer que sigue no descubierto y volver a encolarlo:

```text
Queue = [3,3]
```

Marcar al descubrir evita esta duplicación.

### 49. Invariante de seen

Podemos formular un invariante útil:

```text
seen[v] == true
si v ya fue descubierto por el recorrido
```

Una consecuencia operacional es:

```text
todo vértice que está en la Queue
ya tiene seen[v] == true
```

Y una consecuencia más fuerte:

```text
cada vértice entra en la Queue
como máximo una vez
```

Este invariante será central en la justificación de correctitud y complejidad.

### 50. Invariante de la Queue

La Queue contiene:

```text
vértices ya descubiertos cuyos vecinos todavía deben procesarse
```

No contiene vértices completamente desconocidos.

Además, debido a FIFO:

```text
los vértices descubiertos antes se procesan antes
```

Esta propiedad es la que produce los niveles de BFS.

### 51. Los ciclos dejan de producir repetición infinita

Supongamos:

```text
0 -- 1
|    |
3 -- 2
```

Comenzamos en `0`.

Cuando eventualmente procesemos una arista que regresa a un vértice conocido:

```text
seen[v] == true
```

y no volvemos a encolarlo.

Por tanto, aunque el grafo tenga ciclos:

```text
el recorrido termina
```

si el grafo tiene un número finito de vértices y cada vértice se encola como máximo una vez.

### 52. bfsOrder

El archivo proporciona:

```java
static List<Integer> bfsOrder(Graph graph, int source)
```

El método registra:

```text
orden en que los vértices son retirados y procesados por BFS
```

Para el grafo del archivo se espera:

```text
[0, 1, 2, 3, 4, 5]
```

El vértice:

```text
6
```

queda aislado.

Por tanto, desde `0` no es alcanzable y no aparece en el resultado.

### 53. El orden exacto de BFS puede depender de las listas de adyacencia

BFS garantiza exploración por niveles.

Pero dentro de un mismo nivel puede haber más de un orden válido.

Si:

```text
outEdges(0) = [1,2]
```

podemos descubrir:

```text
1 antes que 2
```

Si la misma información lógica estuviera almacenada como:

```text
outEdges(0) = [2,1]
```

podríamos descubrir:

```text
2 antes que 1
```

Por tanto:

```text
BFS por niveles
    propiedad algorítmica

orden exacto dentro de un nivel
    puede depender del orden de los vecinos
```

Esto evita confundir una salida particular con la definición del algoritmo.

### 54. Vértices alcanzables y no alcanzables

BFS iniciado en `source` no promete visitar todos los vértices del grafo.

Visita:

```text
los vértices alcanzables desde source
```

Si existe un vértice aislado:

```text
6
```

y comenzamos en `0`:

```text
6 no será descubierto
```

Esto no significa que BFS falló.

Significa que no existe un camino desde `0` hasta `6`.

### 55. Grafo desconectado y componente alcanzable

En un grafo no dirigido puede ocurrir:

```text
0 -- 1 -- 2

3 -- 4

5
```

Un BFS desde `0` alcanza:

```text
{0,1,2}
```

pero no:

```text
{3,4,5}
```

Para recorrer todo el grafo habría que iniciar recorridos adicionales desde vértices todavía no descubiertos.

La Semana 11 no necesita desarrollar todavía un algoritmo completo de componentes conexas.

Pero esta observación prepara naturalmente la Semana 12.

### 56. BFS como exploración por niveles

Definimos:

```text
nivel 0
    source

nivel 1
    vértices alcanzables con una arista

nivel 2
    nuevos vértices alcanzables con dos aristas

nivel 3
    nuevos vértices alcanzables con tres aristas
```

BFS procesa estos niveles en orden creciente.

Esta propiedad permite interpretar el nivel como:

```text
distancia mínima en número de aristas
```

cuando el grafo no tiene pesos.

### 57. Distancia en número de aristas

La distancia desde `source` hasta `v` es el mínimo número de aristas entre todos los caminos desde `source` hasta `v`.

Por ejemplo:

```text
0 -- 1 -- 3
|
2 -- 4 -- 5
```

desde `0`:

```text
distance[0] = 0
distance[1] = 1
distance[2] = 1
distance[3] = 2
distance[4] = 2
distance[5] = 3
```

Si un vértice no es alcanzable:

```text
no existe una distancia finita dentro de este recorrido
```

El archivo utiliza:

```text
-1
```

como marca de no alcanzable.

### 58. Inicialización de distance

Para representar que inicialmente no conocemos ninguna distancia:

```text
distance[v] = -1
para todo v
```

Después:

```text
distance[source] = 0
```

porque el origen se encuentra a cero aristas de sí mismo.

La marca:

```text
-1
```

se elige porque ninguna distancia válida en número de aristas es negativa.

### 59. Regla de actualización de distancia

Si estamos procesando `u` y descubrimos por primera vez a un vecino `v`, entonces:

```text
distance[v] = distance[u] + 1
```

La arista:

```text
u -> v
```

añade exactamente una arista al camino utilizado para descubrir `v`.

Por ejemplo:

```text
distance[u] = 2
```

entonces un vecino nuevo descubierto desde `u` obtiene:

```text
distance[v] = 3
```

### 60. distance puede representar también el estado de descubrimiento

En `bfsOrder` utilizamos:

```java
boolean[] seen;
```

En `bfsDistances` existe otra posibilidad.

Si inicializamos:

```text
distance[v] = -1
```

entonces:

```text
distance[v] == -1
```

puede significar:

```text
v todavía no fue descubierto
```

y:

```text
distance[v] >= 0
```

puede significar:

```text
v ya fue descubierto
```

Por tanto, el mismo arreglo puede almacenar:

```text
distancia + estado de descubrimiento
```

Esto es una decisión de representación.

No cambia el comportamiento lógico de BFS.

### 61. Invariante de distance

Podemos formular:

```text
cuando un vértice v entra en la Queue, distance[v] ya está asignada
```

y:

```text
distance[v] corresponde al nivel en el que v fue descubierto
```

Como el vértice se marca o se reconoce como descubierto en ese mismo instante, la distancia no se vuelve a asignar por otra arista posterior.

### 62. Por qué BFS obtiene distancia mínima en un grafo no ponderado

BFS procesa primero:

```text
distancia 0
```

después:

```text
distancia 1
```

después:

```text
distancia 2
```

y así sucesivamente.

Supongamos que `v` se descubre por primera vez desde un vértice `u` con:

```text
distance[u] = d
```

Entonces asignamos:

```text
distance[v] = d + 1
```

Si existiera un camino con menos aristas hasta `v`, algún vértice de un nivel menor habría tenido que descubrirlo antes.

Pero BFS procesa esos niveles menores primero.

Por tanto, la primera distancia asignada es mínima.

Esta es la intuición de correctitud que necesitamos en un curso inicial.

### 63. BFS no resuelve caminos mínimos ponderados

La afirmación:

```text
BFS encuentra distancia mínima
```

necesita una condición.

La distancia que estamos contando es:

```text
número de aristas
```

Todas las aristas se consideran equivalentes.

Si las aristas tuvieran pesos distintos:

```text
0 --10--> 1
0 --1---> 2 --1--> 1
```

el camino con menos aristas no necesariamente tendría menor peso total.

Por tanto:

```text
BFS
    distancia mínima en número de aristas
    para el caso no ponderado
```

No estudiaremos todavía Dijkstra.

### 64. Reconstruir bfsDistances a partir de invariantes

El TODO de:

```java
static int[] bfsDistances(Graph graph, int source)
```

puede derivarse conceptualmente.

Necesitamos:

```text
un arreglo distance
una marca para no descubierto
una Queue
una distancia inicial para source
```

El patrón es:

```text
inicializar todas las distancias como no alcanzables
asignar 0 al origen
encolar el origen

mientras haya pendientes:
    retirar u

    para cada vecino v:
        si v todavía no fue descubierto:
            asignar distance[u] + 1
            encolar v
```

La implementación Java debe reconstruirse a partir de este razonamiento.

### 65. Traza conceptual de bfsDistances

Considera el grafo del archivo:

```text
0 -- 1
|
2

1 -- 3
2 -- 4 -- 5

6 aislado
```

Inicialmente:

```text
distance = [0, -1, -1, -1, -1, -1, -1]
Queue    = [0]
```

Procesamos `0`.

Descubrimos:

```text
1
2
```

Entonces:

```text
distance = [0, 1, 1, -1, -1, -1, -1]
Queue    = [1, 2]
```

Procesamos `1`.

Descubrimos:

```text
3
```

Entonces:

```text
distance = [0, 1, 1, 2, -1, -1, -1]
Queue    = [2, 3]
```

Procesamos `2`.

Descubrimos:

```text
4
```

Entonces:

```text
distance = [0, 1, 1, 2, 2, -1, -1]
Queue    = [3, 4]
```

`3` no produce vértices nuevos.

Después `4` descubre `5`:

```text
distance = [0, 1, 1, 2, 2, 3, -1]
```

El vértice `6` nunca es alcanzado.

Por tanto conserva:

```text
-1
```

### 66. Complejidad de BFS con lista de adyacencia

Analicemos el algoritmo en dos partes.

#### Trabajo sobre vértices

Por el invariante de descubrimiento:

```text
cada vértice se encola como máximo una vez
```

Agregar y retirar de la Queue cuesta tiempo constante por operación.

Por tanto, el trabajo asociado a vértices es:

```text
O(V)
```

#### Trabajo sobre aristas

Cuando procesamos un vértice `u`, recorremos:

```text
outEdges(u)
```

Con lista de adyacencia procesamos las aristas almacenadas en sus listas.

La suma de las longitudes de todas las listas es proporcional a:

```text
E
```

para grafos dirigidos, o a:

```text
2E
```

para la representación habitual de grafos no dirigidos.

Asintóticamente:

```text
O(E)
```

Por tanto:

```text
BFS -> O(V + E)
```

con listas de adyacencia.

### 67. Espacio auxiliar de BFS

Además del grafo, BFS puede necesitar:

```text
seen
distance
Queue
order
```

Cada una de estas estructuras puede contener información para hasta `V` vértices.

Por tanto, según la variante:

```text
espacio auxiliar -> O(V)
```

No debemos sumar:

```text
O(V) + O(V) + O(V)
```

y concluir algo distinto.

Los factores constantes desaparecen:

```text
3V -> O(V)
```

### 68. BFS con matriz de adyacencia

El algoritmo conceptual de BFS no cambia.

Seguimos necesitando:

```text
Queue
estado de descubrimiento
procesar vecinos
```

Pero cambia el costo de:

```text
obtener/recorrer vecinos
```

Con matriz, para cada vértice procesado debemos examinar una fila de longitud `V`.

En el peor caso procesamos `V` vértices.

Por tanto:

```text
V filas x V posiciones por fila
= O(V^2)
```

Así:

```text
BFS con lista
    O(V + E)

BFS con matriz
    O(V^2)
```

Esta comparación muestra que la complejidad de un algoritmo depende también de la representación sobre la que opera.

### 69. Representación, invariante y costo forman una sola decisión

Podemos reconstruir toda la semana.

```text
Graph
    define operaciones

lista de adyacencia
    representa solo vecinos almacenados

invariante
    v pertenece a adjacency[u]
    si y solo si existe u -> v

BFS
    recorre outEdges(u)

costo
    suma de grados -> O(V + E)
```

En cambio:

```text
Graph
    mismo ADT

matriz de adyacencia
    representa cada par posible

invariante
    matrix[u][v] indica si existe u -> v

BFS
    debe revisar filas completas

costo
    O(V^2)
```

El algoritmo no vive separado de la estructura de datos.

### 70. outDegree y suma de grados

En un grafo dirigido:

```text
sumatoria de outDegree(v) sobre todos los vértices = E
```

porque cada arista tiene exactamente un origen y aparece una vez entre las listas salientes.

En un grafo no dirigido representado simétricamente:

```text
sumatoria de degree(v) = 2E
```

porque cada arista lógica toca dos extremos y suele almacenarse dos veces.

Esta observación ayuda a justificar:

```text
recorrer todas las listas de adyacencia
    -> O(V + E)
```

No necesitamos estudiar todavía resultados más profundos de teoría de grafos.

### 71. Lista de adyacencia y grafos dispersos

Supongamos:

```text
V = 1000
```

pero cada vértice tiene solamente unos pocos vecinos.

Una matriz reserva conceptualmente:

```text
1 000 000
```

posiciones booleanas.

Una lista de adyacencia mantiene:

```text
1000 listas + las aristas que realmente existen
```

Por eso la lista suele ser una elección natural para:

```text
grafos dispersos + recorridos de vecinos
```

La conclusión no es:

```text
siempre usar listas
```

La conclusión es:

```text
la representación debe seguir las operaciones y la estructura del problema
```

### 72. Matriz y consulta exacta de arista

Supongamos ahora que el problema realiza millones de consultas:

```text
hasEdge(u,v)
```

y `V` es suficientemente pequeño.

La matriz proporciona:

```text
O(1)
```

para cada consulta.

La lista debe buscar entre:

```text
deg(u)
```

vecinos.

Por tanto, la matriz puede ser razonable aunque utilice más memoria.

Nuevamente aparece:

```text
tiempo
    frente a
espacio
```

### 73. Encapsulación de outEdges en el primer archivo

La implementación por lista del primer archivo utiliza:

```java
Collections.unmodifiableList(...)
```

al devolver vecinos.

No necesitamos estudiar internamente esta utilidad.

La intención conceptual es evitar que un usuario haga algo como:

```text
obtener la lista interna
modificarla directamente
evitar las validaciones del Graph
```

La encapsulación protege los invariantes.

Si toda modificación pasa por:

```text
addEdge
removeEdge
```

la clase tiene un lugar controlado donde validar y preservar propiedades.

### 74. Casos borde de representación

Una implementación correcta debe considerar algo más que el ejemplo principal.

#### Cero vértices

```text
Graph(0)
```

puede representar un grafo vacío.

No existe ningún identificador válido.

#### Vértice aislado

```text
outEdges(v) = []
outDegree(v) = 0
```

#### Vértice sin aristas salientes en un grafo dirigido

Puede tener aristas entrantes y aun así:

```text
outDegree(v) = 0
```

#### Arista repetida en AdjacencyListGraph

Debe conservar una sola presencia lógica bajo la política del primer archivo.

#### Eliminación de una arista ausente

La operación no debe inventar cambios en otras aristas.

Estos casos obligan a pensar en invariantes, no solamente en una ejecución típica.

### 75. Casos borde de BFS

También debemos razonar sobre recorridos pequeños o desconectados.

#### Grafo de un solo vértice

Desde `0`:

```text
bfsOrder -> [0]
distance -> [0]
```

#### Origen aislado

Si existen más vértices pero `source` está aislado:

```text
solo source es alcanzable
```

#### Grafo desconectado

Los vértices de otras componentes conservan:

```text
distance = -1
```

en `bfsDistances`.

#### Ciclo

El recorrido termina porque cada vértice se descubre una sola vez.

#### Varias rutas hacia el mismo vértice

Solo la primera ruta que lo descubre provoca su entrada en la Queue.

### 76. Errores conceptuales frecuentes

#### Error 1

```text
un grafo es solamente un dibujo de puntos y líneas
```

Corrección:

```text
el dibujo es una visualización
el grafo es una abstracción de vértices y aristas
```

#### Error 2

```text
u -> v implica v -> u
```

Corrección:

```text
solo en una relación no dirigida
o si ambas aristas dirigidas están presentes
```

#### Error 3

```text
List<List<Integer>> representa caminos
```

Corrección:

```text
cada lista interior representa los vecinos salientes de un vértice
```

#### Error 4

```text
la lista de adyacencia siempre da hasEdge en O(1)
```

Corrección:

```text
con las listas de esta semana
hasEdge(u,v) -> O(deg(u))
```

#### Error 5

```text
addEdge con lista siempre es O(1)
```

Corrección:

```text
en nuestra implementación se evita duplicar la arista
la búsqueda previa cuesta O(deg(u))
```

#### Error 6

```text
la matriz siempre es peor porque usa O(V^2)
```

Corrección:

```text
ofrece hasEdge, addEdge y removeEdge en O(1) puede ser apropiada según el problema
```

#### Error 7

```text
BFS necesita HashSet obligatoriamente
```

Corrección:

```text
la necesidad lógica es pertenencia

con vértices 0..V-1 boolean[] es una representación directa
```

#### Error 8

```text
un vértice se marca cuando sale de la Queue
```

Corrección:

```text
en el BFS de esta semana se marca al descubrirlo y antes de encolarlo
```

#### Error 9

```text
BFS siempre visita todos los vértices
```

Corrección:

```text
desde source visita los vértices alcanzables desde source
```

#### Error 10

```text
BFS siempre produce un único orden
```

Corrección:

```text
los niveles están determinados por distancia

el orden dentro de un nivel puede depender del orden de las adyacencias
```

#### Error 11

```text
BFS encuentra el camino de menor peso
```

Corrección:

```text
esta semana encuentra distancia mínima en número de aristas
para grafos no ponderados
```

#### Error 12

```text
O(V+E) pertenece a BFS sin importar la representación
```

Corrección:

```text
O(V+E) corresponde al recorrido cuando los vecinos se procesan mediante
listas de adyacencia con matriz puede aparecer O(V^2)
```

### 77. Relación con Semana11_GrafoRepresentacion0.java

El primer archivo concentra tres ideas:

```text
1. Graph como ADT
2. dos representaciones del mismo comportamiento
3. costos diferentes
```

La interfaz es:

```java
interface Graph {
    int nVertices();
    void addEdge(int source, int destination);
    void removeEdge(int source, int destination);
    boolean hasEdge(int source, int destination);
    List<Integer> outEdges(int vertex);
    int outDegree(int vertex);
}
```

El estudiante debe identificar:

```text
qué pertenece al ADT
```

y:

```text
qué pertenece a cada implementación
```

El TODO de `AdjacencyListGraph.addEdge(...)` debe reconstruirse desde:

```text
rango
+
invariante de adyacencia
+
unicidad de arista en esta implementación
```

### 78. Relación con Semana11_GrafoBFS1.java

El segundo archivo concentra:

```text
grafo no dirigido
+
lista de adyacencia
+
Queue
+
seen
+
BFS
+
distancias
```

El método:

```java
bfsOrder(...)
```

sirve como implementación de referencia para comprender el patrón de BFS.

El TODO:

```java
bfsDistances(...)
```

no necesita inventar un algoritmo nuevo.

Reutiliza la misma exploración y agrega información:

```text
distance
```

La transición es:

```text
BFS para visitar
    ->
BFS para medir niveles
```


### 79. Cómo verificar una implementación sin depender solo del ejemplo

Para `AdjacencyListGraph` conviene preguntar:

```text
¿qué ocurre al agregar dos veces la misma arista?
¿qué ocurre al eliminarla?
¿qué ocurre con un vértice aislado?
¿se rechazan índices fuera de rango?
¿listGraph y matrixGraph representan las mismas aristas?
```

Para BFS conviene preguntar:

```text
¿source recibe distancia 0?
¿cada vecino inicial recibe 1?
¿un vértice aislado conserva -1?
¿un ciclo provoca repetición?
¿un vértice con varias rutas se encola una sola vez?
```

Estas preguntas verifican invariantes.

No solo salidas particulares.


### 80. Un patrón general: una estructura auxiliar cambia el costo del recorrido

Sin recordar vértices descubiertos, un recorrido puede volver repetidamente a los mismos vértices.

Con:

```text
seen
```

cada vértice puede quedar identificado como descubierto.

Esto transforma el comportamiento del algoritmo.

La estructura auxiliar utiliza:

```text
O(V)
```

memoria.

A cambio permite garantizar:

```text
cada vértice se encola como máximo una vez
```

Este patrón ya apareció en la Semana 10:

```text
memoria auxiliar
    ->
evitar búsquedas o trabajo repetido
```

y seguirá apareciendo en algoritmos posteriores.

### 81. Otro patrón general: el ADT determina una política de exploración

En BFS no utilizamos `Queue` por conveniencia sintáctica.

Utilizamos `Queue` porque:

```text
FIFO
```

es parte de la estrategia.

Podemos pensar:

```text
estructura de datos auxiliar
    ->
orden en que se procesa el trabajo pendiente
    ->
comportamiento del algoritmo
```

Esta idea prepara la comparación futura con otros recorridos.

No desarrollaremos todavía DFS.

Solo observamos el puente:

```text
BFS
    Queue / FIFO

DFS
    otra política de exploración
```

### 82. Puente hacia DFS y componentes conexas

La Semana 12 podrá preguntar:

```text
¿qué cambia si exploramos profundamente antes de regresar?
```

También podrá preguntar:

```text
¿cómo identificar todas las regiones desconectadas de un grafo?
```

La Semana 11 ya deja preparadas las piezas:

```text
Graph
representación de vecinos
estado de visitado
recorrido desde un origen
alcanzabilidad
```

No necesitamos desarrollar todavía:

```text
DFS
componentes conexas completas
clasificación avanzada de aristas
ordenamiento topológico
```

El objetivo del puente es mostrar continuidad, no adelantar la siguiente semana.


### 83. Síntesis

- Un grafo representa objetos y relaciones entre objetos.
- `G = (V,E)` separa el conjunto de vértices del conjunto de aristas.
- Una arista dirigida `u -> v` distingue origen y destino.
- En un grafo no dirigido, una relación `{u,v}` puede representarse mediante `u -> v` y `v -> u`.
- Los archivos de la semana identifican los vértices mediante `0..n-1`.
- El rango válido es un invariante de representación.
- `Graph` es un ADT y no obliga a una única representación.
- `nVertices`, `addEdge`, `removeEdge`, `hasEdge`, `outEdges` y `outDegree` describen comportamiento.
- Una lista de adyacencia guarda, para cada vértice, sus vecinos salientes.
- En `List<List<Integer>>`, la lista externa se indexa por vértice y las listas internas almacenan vecinos.
- El invariante principal es que `v` pertenece a `adjacency[u]` si y solo si existe `u -> v`.
- El primer archivo mantiene además una sola copia de cada arista dirigida.
- En esa implementación, `hasEdge`, `removeEdge` y `addEdge` sin duplicados cuestan `O(deg(u))`.
- `outDegree` puede ser `O(1)` con la lista utilizada.
- El espacio de la lista de adyacencia es `O(V+E)`.
- Una matriz de adyacencia utiliza `boolean[][]`.
- `matrix[u][v]` indica directamente si existe `u -> v`.
- `addEdge`, `removeEdge` y `hasEdge` son `O(1)` con la matriz.
- `outEdges` y `outDegree` requieren `O(V)` en la implementación estudiada.
- La matriz utiliza `O(V^2)` espacio.
- La lista suele ser natural para grafos dispersos y recorridos de vecinos.
- La matriz puede ser apropiada cuando dominan las consultas directas de aristas o el grafo es denso.
- El orden de una lista de vecinos puede afectar el orden concreto de BFS, pero no las aristas que existen.
- Un camino es una secuencia de aristas consecutivas y permite definir alcanzabilidad.
- Los ciclos hacen necesario recordar qué vértices ya fueron descubiertos.
- `seen` representa la pertenencia al conjunto lógico de vértices descubiertos.
- Con vértices `0..V-1`, `boolean[]` es una representación directa de esa pertenencia.
- BFS utiliza `Queue` porque necesita política FIFO.
- El origen se marca y se encola antes de comenzar el bucle.
- Un vecino nuevo se marca antes de encolarlo.
- Cada vértice se encola como máximo una vez.
- BFS explora por niveles.
- Un BFS desde `source` visita los vértices alcanzables desde `source`, no necesariamente todo el grafo.
- `distance[source] = 0`.
- Al descubrir `v` desde `u`, se asigna `distance[v] = distance[u] + 1`.
- `-1` puede representar un vértice todavía no alcanzado.
- El mismo arreglo `distance` puede representar también el estado de descubrimiento.
- BFS obtiene distancia mínima en número de aristas en grafos no ponderados.
- Con lista de adyacencia, BFS cuesta `O(V+E)`.
- Su espacio auxiliar es `O(V)`.
- Con matriz de adyacencia, recorrer vecinos puede llevar el BFS a `O(V^2)`.
- La complejidad del algoritmo depende de la representación sobre la que opera.
- `Semana11_GrafoRepresentacion0.java` estudia el ADT y las representaciones.
- `Semana11_GrafoBFS1.java` estudia la exploración, el orden BFS y las distancias.
- La Semana 11 prepara, pero no desarrolla todavía, DFS y componentes conexas.

Estas ideas pueden resumirse así:

```text
Graph
    vértices + aristas

lista de adyacencia
    vértice -> vecinos presentes

matriz de adyacencia
    par (u,v) -> existe / no existe

BFS
    Queue + pertenencia de descubiertos

distancia
    nivel de descubrimiento
```

y, de manera más general:

```text
ADT
    define comportamiento

representación
    determina qué información se guarda

invariante
    conecta el estado con su significado

algoritmo
    utiliza esa representación

complejidad
    depende del trabajo que la representación exige
```

### 84. Alcance de la semana

Para esta semana no se requiere estudiar en profundidad:

```text
DFS
componentes conexas como algoritmo completo
componentes fuertemente conexas
clasificación de aristas de DFS
tiempos de descubrimiento y finalización
ordenamiento topológico
DAG en profundidad
árboles de expansión mínima
Prim
Kruskal
DSU aplicado a grafos
Dijkstra
Bellman-Ford
Floyd-Warshall
A*
redes de flujo
caminos eulerianos
caminos hamiltonianos
coloración de grafos
```

Tampoco se requiere estudiar internamente:

```text
GraphStream
JGraphT
estructuras avanzadas de bibliotecas de grafos
matrices dispersas
compresión de grafos
representaciones CSR/CSC
```

Estos temas pueden ser valiosos más adelante.
