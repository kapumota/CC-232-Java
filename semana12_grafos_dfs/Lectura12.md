### Lectura: DFS, estados de recorrido y componentes conexas

Esta lectura consolida y amplía las ideas trabajadas en la Semana 12 de CC232.

Durante las once semanas anteriores hemos cambiado de representación, de invariante, de ADT o de política de procesamiento cuando cambió el tipo de operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico.

```text
índice -> posición
```

La representación permitió acceso directo y obligó a distinguir entre tamaño lógico, capacidad física, crecimiento y costo amortizado.

En la Semana 2 utilizamos nodos enlazados.

```text
nodo -> siguiente
```

La modificación local podía ser barata cuando ya conocíamos las referencias apropiadas, aunque localizar una posición dejaba de ser directo.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`.

La pregunta pasó a ser:

```text
¿qué política de acceso debe seguir una secuencia?
```

Aparecieron dos disciplinas fundamentales:

```text
LIFO
    último en entrar, primero en salir

FIFO
    primero en entrar, primero en salir
```

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda.

```text
comparación -> rama
```

En la Semana 6 agregamos balance AVL.

```text
altura + balance -> mantener O(log n)
```

En la Semana 7 estudiamos `Priority Queue` y `BinaryHeap`.

```text
prioridad -> siguiente elemento disponible
```

En las Semanas 8 y 9 estudiamos hashing, buckets, colisiones, carga, crecimiento y rehashing.

```text
clave -> hash -> bucket
```

En la Semana 10 formulamos:

```text
Map<K,V>
    clave -> valor

Set<T>
    elemento -> pertenencia
```

En la Semana 11 introdujimos grafos.

```text
vértice -> vecinos
```

También aparecieron:

```text
Graph
lista de adyacencia
matriz de adyacencia
Queue
seen
BFS
distancia en número de aristas
```

La Semana 12 no cambia la estructura básica `Graph`.

Cambia principalmente la **política de recorrido** y la información de estado que mantenemos durante la exploración.

La transición conceptual es:

```text
Semana 11

Queue
    FIFO
        |
        v
BFS
    anchura
    niveles
    distancia mínima no ponderada
```

frente a:

```text
Semana 12

Stack / recursión
    LIFO
        |
        v
DFS
    profundidad
    camino activo
    retroceso
```

Después utilizaremos un recorrido DFS para resolver un segundo problema:

```text
¿cuántas regiones desconectadas existen
en un grafo no dirigido?
```

Esto conduce a:

```text
componentes conexas
```

El objetivo de la semana no es memorizar una función recursiva ni aprender una lista extensa de algoritmos de grafos.

El objetivo es continuar la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento necesitamos

representación
    cómo almacenamos el grafo y el estado auxiliar

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    qué política de exploración utilizamos

complejidad
    cuánto trabajo total realiza el recorrido
```

Al finalizar la lectura deberías poder explicar la diferencia entre BFS y DFS, reconstruir un DFS recursivo a partir de sus estados, relacionar la recursión con una pila, explicar una versión iterativa con `Deque`, justificar `O(V+E)` con listas de adyacencia, definir una componente conexa y explicar por qué recorrer todos los vértices e iniciar un nuevo DFS desde cada vértice no visto permite contar componentes en tiempo global `O(V+E)`.

### 1. Continuidad desde la Semana 11

La Semana 11 dejó resuelto el problema de representar un grafo mediante listas o matrices de adyacencia.

Para recorrer un grafo desde `source` utilizamos:

```text
seen
    qué vértices ya fueron descubiertos

Queue
    qué vértices descubiertos siguen pendientes

FIFO
    cuál de ellos debe procesarse después
```

El patrón fundamental de BFS fue:

```text
descubrir
    ->
marcar
    ->
encolar
```

y después:

```text
retirar
    ->
examinar vecinos
    ->
descubrir nuevos
```

La propiedad más importante fue:

```text
cada vértice se encola como máximo una vez
```

cuando se marca en el momento de descubrirlo.

La Semana 12 conserva varias de estas ideas:

```text
Graph
lista de adyacencia
source
alcanzabilidad
estado de descubierto
evitar repetir vértices
O(V+E)
```

Lo que cambia es el orden de exploración.

### 2. La nueva pregunta: ¿anchura o profundidad?

Considera:

```text
        0
       / \
      1   2
      |   |
      3   4
          |
          5
```

Desde `0`, BFS tiende a procesar:

```text
0
1, 2
3, 4
5
```

porque trabaja por niveles.

DFS utiliza otra idea:

```text
si descubro un vecino,
continúo desde ese vecino
antes de regresar
```

Un orden posible es:

```text
0
1
3
2
4
5
```

La diferencia esencial no está en las aristas del grafo.

Está en la política utilizada para administrar trabajo pendiente.

### 3. BFS frente a DFS

Podemos resumir:

```text
BFS
    Breadth-First Search
    búsqueda en anchura

DFS
    Depth-First Search
    búsqueda en profundidad
```

BFS pregunta conceptualmente:

```text
¿qué vértice pendiente fue descubierto antes?
```

y utiliza:

```text
Queue
FIFO
```

DFS pregunta conceptualmente:

```text
¿qué trabajo más reciente debo continuar?
```

y utiliza:

```text
Stack
LIFO
```

o una estructura equivalente proporcionada por la recursión.

### 4. El ADT auxiliar modifica el algoritmo

Este punto conecta directamente Semana 3 y Semana 12.

Podemos pensar:

```text
mismo Graph
mismos vecinos
mismo source
```

pero:

```text
Queue
    ->
BFS
```

mientras:

```text
Stack
    ->
DFS
```

La estructura auxiliar no es un detalle de sintaxis.

Determina el orden de procesamiento.

### 5. DFS como estrategia

La estrategia de DFS puede describirse así:

```text
visitar el vértice actual

buscar un vecino todavía no descubierto

continuar inmediatamente desde ese vecino

seguir profundizando mientras sea posible

cuando no quede un vecino nuevo:
    regresar
```

Esta última acción:

```text
regresar
```

es fundamental.

DFS no abandona permanentemente los vértices anteriores.

Los mantiene implícitamente pendientes mientras explora una rama.

### 6. Profundizar antes de regresar

Considera:

```text
0 -> 1 -> 3
|
+-> 2 -> 4 -> 5
```

Desde `0` descubrimos `1`.

En lugar de volver inmediatamente a `0` para explorar `2`, DFS continúa desde `1` y descubre `3`.

Solo cuando `3` ya no ofrece un vecino nuevo, regresamos.

```text
3
    termina
1
    continúa o termina
0
    continúa
```

Después DFS puede explorar:

```text
0 -> 2 -> 4 -> 5
```

Esta es la idea de profundidad.

### 7. DFS y árboles

DFS se parece al recorrido recursivo de un árbol.

En un árbol, si entramos a un subárbol, normalmente lo procesamos antes de regresar al nodo padre.

En un grafo existe una diferencia importante:

```text
un vértice puede tener varias rutas de llegada
```

y puede existir:

```text
un ciclo
```

Por eso DFS necesita recordar el estado de cada vértice.

### 8. Por qué un booleano puede ser insuficiente para estudiar DFS

En BFS utilizamos:

```java
boolean[] seen;
```

Eso permite distinguir:

```text
false
    no descubierto

true
    descubierto
```

Para muchas aplicaciones DFS también bastaría un booleano.

Pero el primer archivo de la Semana 12 quiere distinguir algo adicional:

```text
descubierto
pero todavía activo

frente a

descubierto
y completamente procesado
```

Por ello utiliza tres estados.

### 9. WHITE, GRAY y BLACK

`Semana12_GrafoDFS0.java` define:

```java
static final byte WHITE = 0;
static final byte GRAY = 1;
static final byte BLACK = 2;
```

Su significado conceptual es:

```text
WHITE
    no descubierto

GRAY
    descubierto
    exploración todavía activa

BLACK
    exploración terminada
```

Estos colores no son decoración.

Representan estados del algoritmo.

### 10. Transición de estados

Durante un mismo DFS, el recorrido válido es:

```text
WHITE
   |
   v
GRAY
   |
   v
BLACK
```

Un vértice no debería volver a `WHITE` después de haber sido descubierto.

Tampoco debería volver de `BLACK` a `GRAY` durante el mismo recorrido.

La transición es monotónica:

```text
no descubierto
    ->
activo
    ->
terminado
```

### 11. WHITE significa todavía no iniciado

Si:

```text
color[v] == WHITE
```

entonces DFS todavía no ha comenzado a explorar `v`.

En particular:

```text
v todavía puede ser elegido
como nuevo vértice de expansión
```

La condición fundamental del DFS recursivo será:

```text
visitar recursivamente
solo vecinos WHITE
```

### 12. GRAY significa llamada activa

Si:

```text
color[v] == GRAY
```

entonces `v` ya fue descubierto, pero todavía no hemos terminado de procesar todos sus vecinos.

En la versión recursiva puede interpretarse como:

```text
dfsVisit(v)
todavía no ha retornado
```

Por tanto, `GRAY` tiene una relación directa con la pila de llamadas.

### 13. BLACK significa procesamiento terminado

Si:

```text
color[v] == BLACK
```

entonces DFS ya terminó de considerar los vecinos correspondientes a `v`.

Así, `BLACK` no significa únicamente:

```text
alguna vez vi v
```

sino:

```text
ya terminé de explorar desde v
```

### 14. Invariante de color

Una formulación útil es:

```text
WHITE
    vértice todavía no descubierto

GRAY
    vértice descubierto cuya exploración está activa

BLACK
    vértice cuya exploración terminó
```

Una operación correcta debe conservar esta interpretación.

El arreglo de colores no es solamente almacenamiento.

Es una representación del estado lógico del recorrido.

### 15. `dfsOrder(...)`

El primer archivo ofrece:

```java
static List<Integer> dfsOrder(Graph graph, int source)
```

Su trabajo conceptual es:

```text
crear color[]
crear order
iniciar DFS desde source
retornar el orden de descubrimiento
```

La función no intenta visitar necesariamente todos los vértices del grafo.

Visita:

```text
los alcanzables desde source
```

igual que el BFS de la Semana 11.

### 16. `dfsVisit(...)` como núcleo recursivo

El método central es:

```java
static void dfsVisit(
    Graph graph,
    int vertex,
    byte[] color,
    List<Integer> order)
```

El TODO indica cuatro responsabilidades conceptuales:

```text
1. marcar GRAY
2. registrar vertex
3. visitar vecinos WHITE
4. marcar BLACK
```

La solución debe poder reconstruirse a partir de esos estados.

### 17. Por qué marcar GRAY antes de recorrer vecinos

Supongamos:

```text
0 -> 1
1 -> 0
```

Si empezamos a explorar `0` pero no lo marcamos antes de entrar en `1`, entonces desde `1` podríamos volver a tratar `0` como nuevo.

La recursión podría repetir indefinidamente.

Por tanto, al comenzar la exploración de `vertex` debemos cambiar inmediatamente:

```text
WHITE -> GRAY
```

Esto hace visible que el vértice ya está activo.

### 18. Registrar el orden al descubrir

Si `order` representa:

```text
orden de descubrimiento
```

debemos registrar `vertex` cuando se descubre.

Conceptualmente:

```text
marcar GRAY
registrar vertex
```

La lista `order` no representa el orden de finalización.

Representa el momento de entrada.

### 19. Visitar únicamente vecinos WHITE

Para cada:

```text
neighbor in graph.outEdges(vertex)
```

la condición es:

```text
si neighbor está WHITE
    continuar recursivamente
```

Si está `GRAY`, ya forma parte de una exploración activa.

Si está `BLACK`, ya terminó.

En ninguno de esos casos debemos iniciar otra llamada recursiva como si fuera nuevo.

### 20. Marcar BLACK al final

Solo después de examinar todos los vecinos corresponde:

```text
color[vertex] = BLACK
```

Este orden es importante.

Si marcáramos `BLACK` antes de procesar los vecinos, perderíamos la distinción entre:

```text
activo
```

y:

```text
terminado
```

que precisamente justifica los tres estados.

### 21. Orden de descubrimiento y orden de finalización

En DFS existen al menos dos órdenes interesantes.

Considera:

```text
0 -> 1 -> 3
```

El orden de descubrimiento puede ser:

```text
0, 1, 3
```

Pero el orden de finalización es:

```text
3, 1, 0
```

No son el mismo orden.

### 22. Preorden y postorden como analogía

Sin convertir esta semana en teoría avanzada de recorridos, podemos hacer una analogía con árboles.

```text
descubrimiento DFS
    parecido a preorden

finalización DFS
    parecido a postorden
```

La analogía ayuda a interpretar la recursión.

No necesitamos todavía mantener tiempos formales de descubrimiento o finalización.

### 23. La pila de llamadas

Cuando Java ejecuta recursión, mantiene información sobre llamadas todavía activas.

Si la ejecución es:

```text
dfsVisit(0)
    dfsVisit(1)
        dfsVisit(3)
```

podemos imaginar una pila:

```text
tope
 |
 v
3
1
0
```

La llamada más reciente es la primera que debe terminar.

Eso es `LIFO`.

### 24. DFS recursivo reutiliza Stack sin declararlo

El método recursivo no escribe explícitamente:

```java
Stack<Integer>
```

pero el entorno de ejecución mantiene una pila de llamadas.

Por eso:

```text
DFS recursivo
    ->
Stack implícita
    ->
LIFO
```

La Semana 3 reaparece dentro de un algoritmo de grafos.

### 25. Retorno y retroceso

Supongamos que estamos en:

```text
dfsVisit(3)
```

y `3` no tiene vecinos `WHITE`.

Entonces:

```text
3 -> BLACK
```

y la llamada retorna.

Volvemos a `dfsVisit(1)`.

Este regreso se denomina frecuentemente `backtracking` o retroceso.

En esta semana basta comprenderlo como:

```text
terminar la rama actual
y continuar desde la llamada anterior
```

### 26. Camino activo

Durante una ejecución recursiva simple:

```text
0 -> 1 -> 3
```

la pila de llamadas mantiene conceptualmente el camino actualmente explorado.

Mientras las llamadas estén activas, los vértices correspondientes pueden estar `GRAY`.

Cuando `3` termina:

```text
3 -> BLACK
```

y deja el camino activo.

Después puede terminar `1`.

### 27. GRAY y la pila no son exactamente el mismo objeto

Es importante no confundir:

```text
color[]
```

con:

```text
call stack
```

`color[]` es información lógica mantenida por el algoritmo.

La pila de llamadas es un mecanismo de ejecución.

Se relacionan porque un vértice activo suele estar `GRAY`, pero son estructuras conceptualmente distintas.

### 28. Ciclos y DFS

Considera:

```text
0 -> 1 -> 2
^         |
|         |
+---------+
```

Existe un ciclo:

```text
0 -> 1 -> 2 -> 0
```

Cuando DFS llega a `2`, el vecino `0` ya no está `WHITE`.

Por tanto, no hacemos otra llamada sobre `0` y evitamos recursión infinita.

### 29. Arista hacia GRAY como indicio de ciclo dirigido

En un grafo dirigido, si durante DFS desde `v` examinamos:

```text
v -> u
```

y `u` está `GRAY`, entonces `u` sigue activo.

En el DFS recursivo básico esto puede interpretarse como una arista que regresa hacia un vértice del camino activo.

Eso permite detectar un ciclo dirigido.

Para esta semana basta con la idea:

```text
vecino GRAY
    puede revelar ciclo
```

No necesitamos todavía clasificar formalmente todas las aristas.

### 30. Lo que no estudiaremos sobre clasificación de aristas

No se requiere aprender como contenido central:

```text
TREE
BACKWARD
FORWARD
CROSS
```

Tampoco:

```text
dTime
fTime
intervalos activos
```

Estas ideas existen en desarrollos más avanzados de DFS.

La Semana 12 utiliza solo lo necesario para comprender:

```text
WHITE
GRAY
BLACK
profundidad
retroceso
componentes
```

### 31. Orden DFS y orden de vecinos

DFS no tiene necesariamente un único orden concreto.

Si:

```text
outEdges(0) = [1,2]
```

podemos entrar primero a `1`.

Si:

```text
outEdges(0) = [2,1]
```

podemos entrar primero a `2`.

Por tanto:

```text
ser DFS
    no implica
un orden único de vértices
```

### 32. El orden esperado del primer archivo

El grafo de `Semana12_GrafoDFS0.java` contiene:

```text
0 -> 1
0 -> 2
1 -> 3
2 -> 4
4 -> 5
```

Con las listas en el orden construido, el ejemplo espera:

```text
[0, 1, 3, 2, 4, 5]
```

La salida depende de:

```text
estrategia DFS + orden de las listas de adyacencia
```

No debe memorizarse como definición universal de DFS.

### 33. Traza conceptual del primer archivo

Inicialmente:

```text
color = [WHITE, WHITE, WHITE, WHITE, WHITE, WHITE]
order = []
```

Entramos a `0`:

```text
0 -> GRAY
order = [0]
```

Primer vecino `1`:

```text
1 -> GRAY
order = [0,1]
```

Desde `1` descubrimos `3`:

```text
3 -> GRAY
order = [0,1,3]
```

`3` termina:

```text
3 -> BLACK
```

Después `1 -> BLACK`.

Regresamos a `0` y continuamos con `2`, después `4` y `5`.

Finalmente todos los alcanzables quedan `BLACK`.

### 34. Correctitud local de `dfsVisit`

Una forma sencilla de razonar sobre la función es:

```text
precondición:
    vertex está WHITE

action:
    lo marca GRAY
    procesa recursivamente sus vecinos WHITE
    lo marca BLACK

postcondición:
    vertex queda completamente procesado
    y los vértices descubiertos desde él
    han sido visitados según DFS
```

El análisis completo puede ser más formal, pero esta formulación es suficiente para un curso inicial.

### 35. Complejidad temporal de DFS

Con lista de adyacencia:

```text
cada vértice se descubre una vez
```

porque solo se llama recursivamente sobre vértices `WHITE`.

Además, cuando un vértice se procesa, recorremos su lista de vecinos.

La suma de las longitudes de todas las listas es proporcional a `E` para un grafo dirigido, o a `2E` para la representación habitual de un grafo no dirigido.

Por tanto:

```text
DFS -> O(V + E)
```

### 36. El mismo orden asintótico que BFS

Semana 11:

```text
BFS -> O(V+E)
```

Semana 12:

```text
DFS -> O(V+E)
```

con listas de adyacencia.

La diferencia está principalmente en:

```text
orden de exploración
```

no en el orden asintótico del recorrido completo.

### 37. El costo depende de la representación del Graph

Si utilizáramos una matriz de adyacencia y para cada vértice tuviéramos que revisar `V` posiciones para encontrar vecinos, un recorrido completo podría costar:

```text
O(V^2)
```

La idea de la Semana 11 continúa:

```text
algoritmo + representación = costo real
```

### 38. Espacio auxiliar del DFS recursivo

El arreglo `color[]` utiliza:

```text
O(V)
```

La lista `order`, si se almacena, también puede crecer hasta `O(V)`.

Además existe la pila de llamadas.

En el peor caso puede alcanzar profundidad:

```text
O(V)
```

Por tanto, el espacio auxiliar se mantiene en:

```text
O(V)
```

### 39. Un caso de profundidad máxima

Considera una cadena:

```text
0 -> 1 -> 2 -> 3 -> ... -> V-1
```

El DFS recursivo puede producir llamadas anidadas hasta `V` niveles.

Este caso ayuda a comprender por qué la recursión consume pila proporcional a la profundidad del recorrido.

### 40. Riesgo de `StackOverflowError`

En grafos muy grandes o muy profundos, la pila de llamadas del lenguaje tiene capacidad finita.

Una cadena suficientemente profunda puede producir:

```text
StackOverflowError
```

Esto no significa que DFS sea incorrecto.

Significa que una representación recursiva del algoritmo puede chocar con límites prácticos de la pila de ejecución.

Por eso también existe DFS iterativo.

### 41. DFS iterativo

La idea de profundidad puede implementarse sin recursión.

En lugar de:

```text
call stack implícita
```

utilizamos:

```text
Stack explícita
```

En Java podemos usar:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

y operar mediante:

```java
push(...)
pop()
```

### 42. Por qué `Deque` puede representar Stack

`Deque` permite operaciones en ambos extremos.

Si utilizamos `push(x)` y `pop()` sobre el mismo extremo, obtenemos una política:

```text
LIFO
```

Por tanto:

```text
Deque
    implementación disponible en Java

Stack
    comportamiento que estamos utilizando
```

### 43. DFS recursivo frente a DFS iterativo

Podemos comparar:

```text
DFS recursivo

pendientes
    pila de llamadas

avance
    llamada recursiva

retroceso
    return
```

frente a:

```text
DFS iterativo

pendientes
    Deque usado como Stack

avance
    push

siguiente pendiente
    pop
```

Ambos explotan una política LIFO.

### 44. El segundo archivo ya proporciona `markComponent(...)`

`Semana12_Componentes1.java` contiene:

```java
static void markComponent(
    Graph graph,
    int source,
    boolean[] seen)
```

Esta función utiliza:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

y marca vértices alcanzables desde `source`.

Su objetivo es utilizar un DFS iterativo para marcar una región alcanzable.

### 45. `seen` vuelve a ser suficiente

Para contar componentes conexas no necesitamos distinguir:

```text
activo
```

de:

```text
terminado
```

Solo necesitamos saber:

```text
¿este vértice ya pertenece a alguna componente que hemos marcado?
```

Por eso el segundo archivo vuelve a utilizar:

```java
boolean[] seen;
```

La necesidad lógica es nuevamente:

```text
pertenencia
```

### 46. Dos representaciones de estado para dos problemas

Primer archivo:

```text
byte[] color
```

porque queremos distinguir:

```text
WHITE
GRAY
BLACK
```

Segundo archivo:

```text
boolean[] seen
```

porque solo necesitamos distinguir:

```text
no marcado
marcado
```

La representación auxiliar cambia porque cambia la información requerida.

### 47. Una diferencia importante del DFS iterativo proporcionado

`markComponent(...)` hace conceptualmente:

```text
push(source)

mientras stack no esté vacía:
    vertex = pop()

    si ya está visto:
        continuar

    marcar visto

    insertar vecinos no vistos
```

Es decir:

```text
marca al retirar
```

y no necesariamente:

```text
marca al insertar
```

como hicimos en BFS.

### 48. Consecuencia de marcar al retirar

Supongamos que dos vértices pendientes pueden apuntar a un mismo vecino todavía no procesado.

Ese vecino podría ser insertado más de una vez antes de su primer `pop`.

Después de la primera extracción:

```text
seen[v] = true
```

y una extracción duplicada posterior será descartada por:

```java
if (seen[vertex]) continue;
```

La implementación sigue evitando reprocesar completamente el vértice.

### 49. Correctitud frente a inserciones redundantes

Esta variante permite distinguir:

```text
procesar un vértice varias veces
```

de:

```text
insertar el mismo id varias veces en la pila
```

El primero se evita.

El segundo puede ocurrir.

Por tanto:

```text
correctitud
    puede mantenerse

cantidad de operaciones auxiliares
    puede variar según la política de marcado
```

### 50. Otra variante: marcar al insertar

También podríamos diseñar un DFS iterativo que marque en el momento de `push`.

Eso puede evitar inserciones duplicadas.

Pero no necesitamos modificar el archivo actual para comprender el concepto.

Lo importante es saber qué invariante mantiene realmente cada implementación.

### 51. El orden del DFS iterativo puede diferir del recursivo

Supongamos:

```text
outEdges(0) = [1,2,3]
```

El DFS recursivo puede procesar `1` primero.

Una pila explícita que haga:

```text
push(1)
push(2)
push(3)
```

dejará `3` en el tope.

Entonces podría procesar `3` primero.

Por tanto:

```text
mismo concepto DFS
```

no implica necesariamente:

```text
mismo orden exacto
```

### 52. Cómo igualar un orden recursivo con una pila explícita

Si deseamos que una pila explícita procese vecinos en el mismo orden que una versión recursiva, puede ser necesario insertarlos en orden inverso.

Si queremos procesar:

```text
1
2
3
```

podemos hacer:

```text
push(3)
push(2)
push(1)
```

para que:

```text
pop() -> 1
```

primero.

Esta es una aplicación directa de LIFO.

### 53. Del recorrido desde `source` al recorrido de todo el grafo

Hasta ahora preguntábamos:

```text
desde source, ¿qué vértices son alcanzables?
```

Pero un grafo puede tener varias regiones desconectadas.

Por ejemplo:

```text
0 -- 1 -- 2

3 -- 4

5 -- 6

7
```

Desde `0` no alcanzamos `3`, `4`, `5`, `6` ni `7`.

Un solo DFS no basta para recorrer todo el grafo.

### 54. Grafo no dirigido y alcanzabilidad mutua

En el segundo archivo trabajamos con:

```java
addUndirectedEdge(a, b)
```

La arista lógica:

```text
a -- b
```

se almacena simétricamente.

En un grafo no dirigido, si existe un camino de `u` a `v`, también puede recorrerse en sentido inverso.

Por eso hablamos de conectividad mutua dentro de una misma región.

### 55. Componente conexa

En un grafo no dirigido, una componente conexa es un conjunto máximo de vértices conectados mediante caminos.

En:

```text
0 -- 1 -- 2

3 -- 4

5 -- 6

7
```

las componentes son:

```text
{0,1,2}
{3,4}
{5,6}
{7}
```

### 56. Qué significa máximo

Si:

```text
{0,1}
```

está conectado, pero `2` también está conectado con `1`, entonces `{0,1}` no es una componente máxima.

La componente es:

```text
{0,1,2}
```

porque incluye toda la región alcanzable conectada.

### 57. Un vértice aislado es una componente

El vértice `7`, aunque no tenga aristas, sigue formando una componente por sí solo.

Por tanto:

```text
{7}
```

es una componente conexa.

El segundo archivo incluye deliberadamente un vértice aislado para reforzar esta idea.

### 58. `markComponent(...)` descubre exactamente una componente

Supongamos que:

```text
seen[v] == false
```

y llamamos:

```text
markComponent(graph, v, seen)
```

La función recorre todos los vértices alcanzables desde `v`.

En un grafo no dirigido, esos vértices constituyen exactamente la componente que contiene `v`.

Por tanto:

```text
una llamada nueva a markComponent
    ->
una componente nueva
```

Este es el invariante central del segundo archivo.

### 59. El problema de contar componentes

Queremos implementar conceptualmente:

```java
static int countConnectedComponents(Graph graph)
```

No necesitamos conocer de antemano dónde comienza cada componente.

Podemos recorrer:

```text
v = 0..V-1
```

y utilizar `seen` como memoria global.

### 60. Esquema conceptual de `countConnectedComponents`

La idea es:

```text
seen = false para todos
components = 0

para cada vértice v:
    si v no está visto:
        components++
        markComponent(v)
```

Cada nueva llamada marca una región completa.

Los siguientes vértices de esa misma región aparecerán luego con `seen == true` y no aumentarán el contador.

### 61. Por qué no incrementamos por cada vértice

Considera:

```text
{0,1,2}
```

como una componente.

Cuando llegamos a `0`:

```text
seen[0] == false
```

por tanto incrementamos el contador y `markComponent(0)` marca `0`, `1` y `2`.

Cuando el bucle llega después a `1` o `2`, ya están vistos.

La unidad contada es:

```text
nuevo recorrido iniciado
```

no:

```text
vértice encontrado
```

### 62. Invariante del bucle exterior

Antes de examinar un vértice `v` en el bucle exterior podemos pensar:

```text
seen
    contiene los vértices de todas
    las componentes ya descubiertas

components
    cuenta exactamente esas componentes
```

Entonces existen dos casos.

```text
seen[v] == true
    v pertenece a una componente ya contada
```

o:

```text
seen[v] == false
    v pertenece a una componente todavía no contada
```

Este invariante explica el algoritmo.

### 63. Traza del segundo archivo

El grafo contiene:

```text
{0,1,2}
{3,4}
{5,6}
{7}
```

Inicialmente:

```text
components = 0
seen = todos false
```

En `v = 0` iniciamos una componente y quedan vistos `0`, `1` y `2`.

En `v = 3` iniciamos la segunda y quedan vistos `3` y `4`.

En `v = 5` iniciamos la tercera y quedan vistos `5` y `6`.

Finalmente `7` sigue no visto, por lo que inicia la cuarta.

Resultado:

```text
4
```

### 64. Por qué varias llamadas DFS no producen `O(V(V+E))`

Una primera impresión incorrecta puede ser:

```text
si llamamos DFS varias veces, repetimos O(V+E) muchas veces
```

Pero no ocurre así.

El arreglo global `seen` impide volver a expandir componentes ya procesadas.

A lo largo de todas las llamadas, cada vértice y sus adyacencias se procesan como parte de una sola región nueva.

Por tanto, el trabajo total sigue siendo:

```text
O(V+E)
```

### 65. Análisis global frente a análisis por llamada

No debemos hacer:

```text
número de llamadas x peor costo aislado de una llamada
```

si las llamadas no trabajan sobre entradas independientes.

Aquí comparten `seen` y cada llamada procesa una parte distinta del grafo.

La suma de todo el trabajo es lineal en la representación completa.

### 66. Complejidad de `countConnectedComponents`

El bucle exterior visita `V` identificadores:

```text
O(V)
```

La suma de todas las exploraciones `markComponent` procesa vértices y aristas en:

```text
O(V+E)
```

Por tanto:

```text
countConnectedComponents -> O(V+E)
```

con listas de adyacencia.

### 67. Espacio auxiliar para componentes

Necesitamos:

```text
seen -> O(V)
```

y una pila explícita.

En la variante proporcionada pueden existir inserciones repetidas antes de que un vértice sea marcado.

Para esta semana interesa conservar la idea global:

```text
estado de visitados + estructura de pendientes
```

sin convertir el análisis fino del tamaño exacto de la pila en el objetivo principal.

### 68. BFS también puede marcar una componente

Una componente conexa no pertenece exclusivamente a DFS.

Podemos hacer:

```text
si !seen[v]:
    components++
    BFS(v)
```

y también marcaríamos la componente completa de `v`.

Por tanto:

```text
propiedad buscada
    componente conexa

recorrido elegido
    BFS o DFS
```

son decisiones distintas.

### 69. Por qué estudiar componentes con DFS

Porque Semana 12 quiere consolidar DFS y mostrar una aplicación natural.

Además, el patrón:

```text
recorrer todos los vértices

si uno no está descubierto:
    iniciar un nuevo recorrido
```

es una idea general que aparecerá en otros algoritmos.

### 70. Árbol DFS y bosque DFS

Cuando DFS descubre un vértice nuevo desde otro vértice, podemos imaginar una relación:

```text
padre -> hijo descubierto
```

Las aristas responsables de nuevos descubrimientos forman un árbol de recorrido dentro de la región alcanzada.

Si el grafo tiene varias componentes y empezamos varios DFS, obtenemos varios árboles.

Conceptualmente:

```text
varios árboles DFS -> bosque DFS
```

Para esta semana esta es una interpretación útil, no una estructura adicional que debamos implementar.

### 71. `parent[]` como ampliación conceptual

Podríamos mantener:

```java
int[] parent;
```

y cuando descubrimos `v` desde `u` registrar:

```text
parent[v] = u
```

Eso permitiría reconstruir el árbol DFS.

Sin embargo, los archivos actuales no necesitan `parent[]`.

Por tanto, debe considerarse una ampliación, no requisito central.

### 72. BFS y DFS responden preguntas diferentes con el mismo Graph

Con una lista de adyacencia podemos ejecutar ambos algoritmos.

```text
Graph
    misma representación
```

pero:

```text
BFS
    Queue
    niveles
    distancia mínima no ponderada
```

mientras:

```text
DFS
    Stack/recursión
    profundidad
    estructura activa de llamadas
```

El Graph no cambia.

La política del algoritmo sí.

### 73. Alcanzabilidad con BFS o DFS

Para responder:

```text
¿v es alcanzable desde source?
```

BFS y DFS pueden servir.

Ambos visitan los vértices alcanzables si se ejecutan correctamente.

Con listas de adyacencia:

```text
O(V+E)
```

en el peor caso de recorrido completo.

La elección depende de lo que queramos obtener además de alcanzabilidad.

### 74. BFS para distancia, DFS para estructura de exploración

BFS tiene una propiedad especial:

```text
procesa por niveles
```

y por eso permite obtener directamente distancia mínima en número de aristas.

DFS no ofrece esa garantía.

Un DFS puede encontrar primero un camino largo aunque exista otro más corto.

Por tanto:

```text
alcanzabilidad
    BFS o DFS

distancia mínima no ponderada
    BFS
```

### 75. DFS no significa camino más corto

Considera:

```text
0 -> 1 -> 2 -> 3
|
+-----------> 3
```

Un DFS que explore primero la cadena puede encontrar un camino de tres aristas.

Pero existe `0 -> 3` de una sola arista.

Por tanto:

```text
primer camino hallado por DFS
```

no implica:

```text
camino mínimo
```

Esta diferencia debe quedar clara después de Semana 11.

### 76. Errores conceptuales frecuentes

#### Error 1

```text
DFS usa Queue
```

Corrección:

```text
DFS utiliza política LIFO mediante recursión o Stack explícita
```

#### Error 2

```text
GRAY significa visitado completamente
```

Corrección:

```text
GRAY significa descubierto y todavía activo
```

#### Error 3

```text
BLACK significa no visitado
```

Corrección:

```text
BLACK significa procesamiento terminado
```

#### Error 4

```text
hay que llamar recursivamente a todos los vecinos
```

Corrección:

```text
solo a vecinos WHITE
```

#### Error 5

```text
un ciclo hace que DFS nunca termine
```

Corrección:

```text
el estado evita volver a iniciar DFS sobre vértices ya descubiertos
```

#### Error 6

```text
orden de descubrimiento = orden de finalización
```

Corrección:

```text
pueden ser distintos
```

#### Error 7

```text
DFS siempre produce un único orden
```

Corrección:

```text
el orden depende también del orden de las adyacencias
```

#### Error 8

```text
DFS iterativo siempre produce el mismo orden que DFS recursivo
```

Corrección:

```text
LIFO puede invertir el efecto del orden en que se hacen push
```

#### Error 9

```text
una componente conexa necesita al menos dos vértices
```

Corrección:

```text
un vértice aislado también es una componente
```

#### Error 10

```text
components aumenta por cada vértice
```

Corrección:

```text
aumenta por cada nuevo recorrido iniciado desde un vértice no visto
```

#### Error 11

```text
varias llamadas DFS implican O(V(V+E))
```

Corrección:

```text
seen evita repetir componentes

y el costo acumulado es O(V+E)
```

#### Error 12

```text
solo DFS puede encontrar componentes
```

Corrección:

```text
BFS también puede marcar una componente completa
```

#### Error 13

```text
DFS encuentra automáticamente distancia mínima
```

Corrección:

```text
esa es una propiedad de BFS en grafos no ponderados
```

#### Error 14

```text
WHITE/GRAY/BLACK son propiedades permanentes del Graph
```

Corrección:

```text
son estado auxiliar del recorrido
```

### 77. Casos borde para DFS

#### Un solo vértice

Desde `0`:

```text
order = [0]
```

y la transición es:

```text
WHITE -> GRAY -> BLACK
```

#### Origen aislado

Si `source` no tiene vecinos:

```text
solo source se descubre
```

#### Grafo con ciclo

Debe terminar sin repetir recursión indefinida.

#### Grafo desconectado

`dfsOrder(graph, source)` visita solamente la región alcanzable desde `source`.

#### Varias rutas hacia el mismo vértice

Solo la primera llegada mientras sigue `WHITE` produce una nueva llamada recursiva.

### 78. Casos borde para componentes

#### Grafo vacío

```text
0 componentes
```

#### Un solo vértice

```text
1 componente
```

#### Todos los vértices aislados

Con `V` vértices:

```text
V componentes
```

#### Grafo completamente conectado

```text
1 componente
```

#### Varias regiones

El contador debe corresponder al número de nuevos recorridos iniciados.

### 79. Cómo verificar `dfsVisit(...)`

No basta comprobar una sola salida.

Conviene verificar:

```text
¿source aparece primero?

¿un ciclo termina?

¿un vecino alcanzable aparece?

¿un vértice no alcanzable queda fuera?

¿el orden sigue las listas de adyacencia?

¿todos los alcanzados terminan BLACK?

¿ningún vértice se agrega dos veces a order?
```

Estas preguntas verifican propiedades del algoritmo.

### 80. Cómo verificar `countConnectedComponents(...)`

Conviene probar:

```text
grafo vacío

un vértice aislado

varios aislados

una cadena conectada

dos componentes

varias componentes

componente con ciclo
```

También es útil comprobar que agregar una arista entre dos componentes puede reducir el contador en uno cuando esa arista las conecta.

### 81. Relación con `Semana12_GrafoDFS0.java`

El primer archivo concentra:

```text
Graph dirigido
lista de adyacencia
source
DFS recursivo
WHITE
GRAY
BLACK
order
```

Su TODO debe resolverse a partir de:

```text
transición de color
+
orden de descubrimiento
+
recursión solo sobre WHITE
+
finalización BLACK
```

La pregunta esencial no es:

```text
¿qué líneas de Java faltan?
```

sino:

```text
¿qué invariante debe mantener
cada etapa de dfsVisit?
```

### 82. Relación con `Semana12_Componentes1.java`

El segundo archivo concentra:

```text
grafo no dirigido
lista de adyacencia
DFS iterativo
Deque usado como Stack
seen
componentes conexas
recorrido global de vértices
```

`markComponent(...)` ya está implementado.

El TODO de `countConnectedComponents(...)` debe reconstruirse desde:

```text
cada vértice no visto
    inicia exactamente una nueva componente
```

### 83. Un patrón general: política de pendientes

Las Semanas 3, 7, 11 y 12 permiten ver un patrón más general.

```text
Queue
    FIFO
    procesa lo más antiguo

Stack
    LIFO
    procesa lo más reciente

Priority Queue
    procesa según prioridad
```

La colección de pendientes y la regla para elegir el siguiente elemento pueden determinar el comportamiento de un algoritmo completo.

En Semana 11:

```text
Queue -> BFS
```

En Semana 12:

```text
Stack -> DFS
```

Esta observación conecta estructuras de datos con diseño de algoritmos.

### 84. Un patrón general: análisis acumulado

En componentes conexas no analizamos cada llamada DFS como si comenzara sobre el grafo completo.

Analizamos:

```text
el trabajo total de todas las llamadas
```

porque comparten `seen`.

Esta forma de razonamiento se relaciona con ideas anteriores del curso:

```text
analizar una secuencia de operaciones en lugar de multiplicar ciegamente costos aislados
```

La complejidad depende de cuánto trabajo nuevo realiza realmente cada etapa.

### 92. Síntesis

- La Semana 12 continúa directamente el bloque de grafos de la Semana 11.
- `Graph` y la lista de adyacencia pueden permanecer iguales mientras cambia la política de recorrido.
- BFS utiliza `Queue` y FIFO.
- DFS utiliza una política LIFO mediante recursión o Stack explícita.
- DFS profundiza por una rama antes de regresar.
- `WHITE` significa no descubierto.
- `GRAY` significa descubierto y todavía activo.
- `BLACK` significa procesamiento terminado.
- La transición correcta es `WHITE -> GRAY -> BLACK`.
- El arreglo `color` representa estado auxiliar del recorrido, no información permanente del `Graph`.
- El DFS recursivo utiliza implícitamente una pila de llamadas.
- El retorno de una llamada implementa el retroceso.
- Solo se llama recursivamente a vecinos `WHITE`.
- El estado evita repetición infinita en ciclos.
- El orden de descubrimiento y el orden de finalización pueden ser distintos.
- El orden concreto de DFS puede depender del orden de las listas de adyacencia.
- Con lista de adyacencia, DFS cuesta `O(V+E)`.
- La pila de recursión puede alcanzar `O(V)` profundidad.
- Una implementación iterativa puede utilizar `Deque` como Stack.
- `push` y `pop` expresan política LIFO.
- Un DFS iterativo puede producir un orden distinto del recursivo si los vecinos se insertan en otro orden.
- `Semana12_Componentes1.java` utiliza `boolean[] seen` porque para contar componentes basta saber si un vértice ya fue marcado.
- Una componente conexa de un grafo no dirigido es una región máxima de vértices conectados por caminos.
- Un vértice aislado también constituye una componente.
- Una llamada nueva a `markComponent` desde un vértice no visto descubre exactamente una componente.
- El bucle exterior recorre todos los vértices.
- Cada vértice no visto inicia un nuevo DFS y aumenta el contador.
- El costo global de contar componentes sigue siendo `O(V+E)`.
- No se debe multiplicar mecánicamente el costo de DFS por el número de componentes.
- BFS también podría utilizarse para marcar componentes.
- La propiedad de conectividad y la política de recorrido son conceptos distintos.
