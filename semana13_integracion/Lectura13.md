### Lectura: selección de estructuras e integración de invariantes

Esta lectura consolida las ideas trabajadas en la Semana 13 de CC232.

Durante las semanas anteriores, cada tema apareció porque cambió la operación que queríamos favorecer.

En la Semana 1 usamos un arreglo dinámico.

```text
índice -> posición
```

En la Semana 2 usamos nodos enlazados.

```text
nodo -> siguiente
```

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`.

```text
LIFO/FIFO
```

En las Semanas 4 y 5 usamos el orden de un BST.

```text
comparación -> rama
```

En la Semana 6 añadimos balance AVL.

```text
altura + balance -> O(log n)
```

En la Semana 7 estudiamos `Priority Queue` y `BinaryHeap`.

```text
prioridad -> siguiente elemento
```

En las Semanas 8 y 9 estudiamos hashing, colisiones, carga y rehashing.

```text
clave -> hash -> bucket
```

En la Semana 10 formulamos dos ADT sobre búsqueda por clave.

```text
Map<K,V>
    clave -> valor

Set<T>
    elemento -> pertenencia
```

En las Semanas 11 y 12 representamos relaciones y cambiamos la política de recorrido.

```text
Graph
    representación

BFS
    Queue + FIFO

DFS
    Stack/recursión + LIFO
```

La Semana 13 cambia la pregunta nuevamente.

Ya no preguntamos principalmente:

```text
¿cómo funciona esta estructura?
```

Ahora preguntamos:

```text
dado un problema, ¿qué estructura ya estudiada conviene utilizar?
```

y después:

```text
¿cómo coordinamos varias estructuras dentro de un mismo sistema?
```

La idea central de la semana es:

```text
problema
    ->
operaciones dominantes
    ->
ADT
    ->
representación
    ->
invariante
    ->
algoritmo
    ->
complejidad
```

El objetivo no es memorizar tres soluciones independientes ni copiar una implementación de heap.

El objetivo es aprender a justificar una elección y a mantener varios invariantes compatibles cuando una sola estructura no resuelve todo el problema.

### 1. Elegir una estructura a partir de las operaciones

Una estructura de datos es adecuada cuando sus operaciones coinciden con las necesidades del problema.

Considera tres preguntas.

```text
¿qué apertura debo cerrar ahora?

¿este valor ya apareció?

¿cuál es el siguiente elemento con mayor prioridad?
```

Las tres almacenan datos, pero requieren comportamientos diferentes.

```text
último pendiente
    -> Stack

pertenencia
    -> Set/hashing

mínimo repetido
    -> Priority Queue/min-heap
```

Por tanto, la selección comienza antes del código.

Una estrategia útil es identificar primero la operación dominante.

```text
último elemento insertado
pertenencia
mínimo
búsqueda exacta
orden
vecinos
```

Después elegimos una representación que favorezca esa operación.

### 2. `balanced(...)`: cuando el problema exige LIFO

`Semana13_Seleccion0.java` comienza con:

```java
static boolean balanced(String expression)
```

El problema consiste en verificar delimitadores:

```text
()
[]
{}
```

Una expresión balanceada debe respetar dos condiciones.

Primero, ningún cierre puede aparecer sin una apertura pendiente.

Segundo, cada cierre debe corresponder a la apertura pendiente más reciente.

Por ejemplo:

```text
{[()]}
```

es válida.

En cambio:

```text
([)]
```

no es válida.

La razón no es que falte una apertura.

El problema es que `)` intenta cerrar `(` cuando la apertura pendiente más reciente es `[`.

La expresión:

```text
más reciente
```

conduce directamente a:

```text
LIFO
```

y por tanto a:

```text
Stack
```

En Java podemos utilizar:

```java
ArrayDeque<Character>
```

con operaciones de pila.

```text
push
pop
peek
```

### 3. Invariante de la pila de delimitadores

Después de procesar cualquier prefijo de la expresión debe cumplirse:

```text
la pila contiene exactamente las aperturas todavía no cerradas
```

Además:

```text
el tope contiene la apertura pendiente más reciente
```

Por tanto, cuando aparece un cierre:

```text
stack vacía
    -> error

tipo del tope incompatible
    -> error

tipo compatible
    -> pop
```

Al terminar toda la cadena también debemos exigir:

```text
stack.isEmpty()
```

porque una expresión como:

```text
(([
```

no contiene cierres incorrectos, pero deja aperturas pendientes.

Cada carácter se examina una vez.

```text
tiempo
    O(n)

espacio auxiliar
    O(n) en el peor caso
```

### 4. `firstDuplicate(...)`: pertenencia antes que orden

El segundo problema es:

```java
static Integer firstDuplicate(int[] values)
```

Para:

```text
[4, 1, 7, 2, 7, 4]
```

la respuesta es:

```text
7
```

La operación dominante es:

```text
¿este valor ya apareció antes?
```

No necesitamos mantener los valores ordenados.

Tampoco necesitamos buscar mínimo o máximo.

Necesitamos pertenencia.

Por ello utilizamos:

```text
Set<Integer>
```

y una representación con hashing:

```text
HashSet<Integer>
```

El invariante antes de procesar `values[i]` es:

```text
seen contiene exactamente los valores de values[0..i-1]
```

Entonces:

```text
value no pertenece a seen
    -> agregar

value ya pertenece a seen
    -> primer duplicado
```

Con hashing bien distribuido:

```text
contains/add
    O(1) esperado
```

y para `n` valores:

```text
O(n) esperado
```

El espacio adicional es:

```text
O(n)
```

### 5. Por qué no ordenar para hallar el primer duplicado

Ordenar puede agrupar valores iguales, pero modifica la información de orden que define la palabra:

```text
primer
```

Además:

```text
ordenar
    O(n log n)
```

mientras una tabla hash permite un recorrido esperado:

```text
O(n)
```

La elección correcta no depende solamente del costo.

También depende de conservar la semántica de la consulta.

### 6. Selección frente a ordenamiento

El tercer problema es:

```java
static int kthSmallest(int[] values, int k)
```

Queremos el `k`-ésimo menor.

Por ejemplo:

```text
values = [9, 1, 8, 2, 7, 3]
k = 3
```

La respuesta es:

```text
3
```

Podríamos ordenar todo:

```text
[1, 2, 3, 7, 8, 9]
```

pero el archivo pide evitar esa estrategia.

La razón conceptual es importante.

```text
ordenamiento
    necesita establecer el orden total

selección
    necesita identificar una posición de orden
```

No siempre debemos calcular más información de la necesaria.

### 7. Selección con un min-heap

Una estrategia coherente con la Semana 7 es:

```text
1. construir un min-heap
2. extraer el mínimo k veces
3. devolver el último extraído
```

Si construimos el heap con `heapify`:

```text
heapify
    O(n)
```

y cada extracción cuesta:

```text
O(log n)
```

entonces:

```text
k extracciones
    O(k log n)
```

Costo total:

```text
O(n + k log n)
```

El invariante sigue siendo el de la Semana 7.

```text
para cada nodo:

padre <= hijos
```

No mantenemos orden total.

Mantenemos solamente el orden suficiente para que el mínimo esté disponible en la raíz.

### 8. Ampliación: un max-heap de tamaño k

Existe otra estrategia útil.

Podemos mantener solamente los `k` menores vistos hasta el momento dentro de un max-heap de tamaño máximo `k`.

La raíz representa:

```text
el mayor de los k menores actuales
```

Cuando llega un valor menor que esa raíz, reemplazamos la raíz y restauramos el heap.

El costo es:

```text
O(n log k)
```

con espacio:

```text
O(k)
```

Esta estrategia muestra una idea importante de selección:

```text
el tamaño de la estructura auxiliar puede depender de k y no de n
```

No es obligatorio implementarla en los archivos de la semana, pero es una comparación útil.

### 9. Ampliación: Quickselect

También existe `Quickselect`.

La idea es utilizar particiones semejantes a Quicksort, pero continuar solamente por la región que puede contener la posición buscada.

Su costo esperado puede ser:

```text
O(n)
```

aunque una elección desfavorable de pivotes puede producir:

```text
O(n^2)
```

en el peor caso.

En esta semana interesa solamente reconocer que:

```text
selección
```

es un problema propio y no simplemente:

```text
ordenar y tomar una posición
```

No se requiere implementar Quickselect como contenido central de Semana 13.

### 10. De una estructura a varias estructuras coordinadas

`Semana13_Integracion1.java` presenta un problema distinto.

Ahora tenemos un sistema de atención con tickets.

Cada `Ticket` posee:

```text
id
priority
minutes
```

El sistema necesita responder varias preguntas.

```text
¿qué ticket debe atenderse ahora?

¿un id ya fue atendido?

¿cuántos minutos se han acumulado?

¿cuántos tickets siguen pendientes?
```

Una sola estructura no favorece todas estas operaciones.

Por eso `ServiceSystem` combina:

```text
List<Ticket> heap
Set<Integer> served
int totalMinutes
```

Cada componente tiene una responsabilidad distinta.

### 11. Min-heap como política de atención

El heap responde:

```text
¿qué ticket sigue?
```

El método:

```java
less(Ticket a, Ticket b)
```

define el criterio de prioridad.

Conceptualmente:

```text
menor priority
    se atiende primero

si priority empata
    menor id se atiende primero
```

El comparador forma parte del invariante.

No basta afirmar:

```text
esto es un min-heap
```

Debemos preguntar:

```text
mínimo según qué relación?
```

El invariante correcto es:

```text
ningún hijo debe preceder
a su padre según less(...)
```

### 12. `bubbleUp` y `trickleDown` reparan violaciones locales

Cuando insertamos un elemento al final del heap, la forma de árbol binario completo se conserva.

La única posible violación del orden está en el camino hacia el padre.

Por eso usamos:

```text
bubbleUp
```

Cuando extraemos la raíz, movemos el último elemento a la posición `0`.

La posible violación queda hacia abajo.

Por eso usamos:

```text
trickleDown
```

Ambas operaciones recorren como máximo la altura del heap.

```text
altura
    O(log n)

bubbleUp
    O(log n)

trickleDown
    O(log n)
```

Los índices de la representación implícita son:

```text
parent(i) = (i - 1)/2
left(i)   = 2*i + 1
right(i)  = 2*i + 2
```

Las fórmulas no son el objetivo.

El objetivo es entender por qué permiten navegar un árbol binario completo almacenado en un arreglo.

### 13. `serveNext(...)` como operación compuesta

Atender un ticket no modifica solamente el heap.

Conceptualmente debe realizar:

```text
1. obtener el mínimo
2. retirarlo del heap
3. restaurar el min-heap
4. registrar su id como atendido
5. acumular sus minutes
6. retornar el ticket
```

Si no hay tickets:

```text
return null
```

Después de la operación deben mantenerse simultáneamente varios invariantes.

```text
heap
    sigue siendo min-heap

served
    contiene los ids atendidos

totalMinutes
    suma los minutos atendidos

pending()
    coincide con heap.size()
```

Este es el punto central de la integración.

La correctitud ya no depende de una sola estructura.

Depende de que varias representaciones del estado permanezcan consistentes.

### 14. Invariantes coordinados y fuente de verdad

Una variable auxiliar puede hacer una consulta muy barata, pero crea una obligación.

Por ejemplo:

```text
totalMinutes
```

permite responder:

```text
O(1)
```

sin recorrer todos los tickets atendidos.

Pero cada vez que atendemos un ticket debemos actualizar correctamente:

```text
totalMinutes += ticket.minutes
```

La optimización de una consulta suele introducir un nuevo invariante.

La idea general es:

```text
estado derivado almacenado
    -> consulta más barata
    -> más responsabilidad al actualizar
```

Este patrón aparece en muchas estructuras de datos.

AVL almacena altura.

Una tabla hash mantiene tamaño y capacidad.

Un sistema integrador puede mantener contadores, índices o conjuntos auxiliares.

### 15. Un detalle de diseño: ids atendidos e ids pendientes

En el archivo actual:

```java
if (served.contains(ticket.id)) return false;
```

impide volver a insertar un id que ya fue atendido.

Sin embargo, `served` no representa:

```text
ids actualmente pendientes
```

Por tanto, dos tickets con el mismo `id` podrían estar pendientes antes de que uno de ellos sea atendido.

Esto no implica que el código esté necesariamente incorrecto.

Significa que debemos leer con precisión el contrato representado por el estado.

```text
served
    ids atendidos

no necesariamente
    todos los ids existentes en el sistema
```

Si el requisito fuera:

```text
ningún id puede repetirse
ni pendiente ni atendido
```

necesitaríamos otro invariante o un conjunto adicional.

La lección es:

```text
una estructura solo puede garantizar
la propiedad que realmente representa
```

### 16. Complejidad por operación

Semana 13 integra estructuras, por lo que conviene analizar cada operación por separado.

| Operación | Estructura dominante | Costo |
|---|---|---:|
| `balanced` | Stack/`ArrayDeque` | `O(n)` |
| `firstDuplicate` | `HashSet` | `O(n)` esperado |
| `kthSmallest` con min-heap | `BinaryHeap` | `O(n + k log n)` |
| `addTicket` | min-heap | `O(log n)` |
| `serveNext` | min-heap + estado auxiliar | `O(log n)` |
| `wasServed` | `HashSet` | `O(1)` esperado |
| `totalMinutes` | acumulador | `O(1)` |
| `pending` | tamaño del heap | `O(1)` |

No existe una única complejidad para todo el sistema.

Cada operación tiene un costo determinado por la representación que utiliza.

### 17. Casos borde que revelan el invariante

Los casos borde ayudan a comprobar si entendemos la estructura.

Para `balanced`:

```text
""
    true

"("
    false

")"
    false

"([])"
    true

"([)]"
    false
```

Para `firstDuplicate`:

```text
[]
    null

[1,2,3]
    null

[5,5]
    5
```

Para `kthSmallest`:

```text
k < 1
k > n
    entrada inválida

k = 1
    mínimo

k = n
    máximo
```

Para `ServiceSystem`:

```text
heap vacío
    serveNext() -> null

un solo ticket
    después de atenderlo:
        pending() == 0

dos prioridades iguales
    decide el menor id
```

Los casos borde no son pruebas decorativas.

Obligan a identificar exactamente qué afirma cada invariante.

### 18. Probar propiedades, no solamente ejemplos

Un `main()` con una salida esperada es útil, pero una implementación correcta debe conservar propiedades generales.

Para `balanced`:

```text
al finalizar true
    no quedan aperturas pendientes
```

Para `firstDuplicate`:

```text
si retorna x
    x apareció antes
    y ningún duplicado apareció antes que esa segunda aparición
```

Para `kthSmallest`:

```text
exactamente k-1 elementos
son menores o iguales según multiplicidad
antes de la posición seleccionada
```

Para `ServiceSystem`:

```text
después de cada addTicket/serveNext
    el heap sigue válido
    served sigue consistente
    totalMinutes sigue consistente
```

La Semana 13 conecta así implementación con razonamiento de correctitud.

### 19. Selección e integración como síntesis del curso

La semana puede resumirse mediante dos preguntas.

Primera:

```text
¿qué estructura favorece la operación dominante?
```

Segunda:

```text
si necesito varias operaciones distintas,
¿cómo coordino varias estructuras
sin romper sus invariantes?
```

La respuesta vuelve a la misma cadena utilizada desde la Semana 1.

```text
problema
    qué queremos resolver

ADT
    qué comportamiento necesitamos

representación
    dónde vive el estado

invariante
    qué debe permanecer verdadero

algoritmo
    cómo modificamos el estado

complejidad
    cuánto cuesta

integración
    cómo coordinamos varias representaciones
```

La habilidad final no consiste en reconocer una palabra clave y copiar una estructura.

Consiste en poder justificar:

```text
por qué esta estructura
por qué este invariante
por qué este algoritmo
por qué este costo
```

### 20. Qué queda como ampliación y no como requisito central

Es útil conocer que existen alternativas como:

```text
max-heap de tamaño k
Quickselect
java.util.PriorityQueue
comparadores más generales
estructuras auxiliares para ids pendientes
```

Pero el contenido central de Semana 13 sigue siendo:

```text
selección de estructuras
Stack para LIFO
HashSet para pertenencia
min-heap para prioridad y selección
invariantes coordinados
análisis de complejidad por operación
```

No es necesario introducir como contenido obligatorio:

```text
Fibonacci Heap
Binomial Heap
Median of Medians
implementación completa de Quickselect
```

Estos temas pueden estudiarse posteriormente.

### Referencias

#### Texto principal

Pat Morin, *Open Data Structures (in Java)*.

Revisar de manera selectiva los capítulos sobre:

```text
ArrayStack/ArrayDeque
hash tables
BinaryHeap
```

#### Referencias complementarias

Michael T. Goodrich, Roberto Tamassia y Michael H. Goldwasser, *Data Structures and Algorithms in Java*.

Revisar selectivamente:

```text
Stacks, Queues and Deques
Priority Queues
Maps, Hash Tables and Sets
Sorting and Selection
```

Robert Sedgewick y Kevin Wayne, *Algorithms*.

Consultar como apoyo:

```text
Stacks and Queues
Priority Queues
Hash Tables
```

### Cierre

La Semana 13 no añade una estructura completamente nueva.

Añade una capacidad de diseño.

```text
problema
    ->
operaciones
    ->
estructura adecuada
```

y después:

```text
varias operaciones
    ->
varias estructuras
    ->
invariantes coordinados
```

Ese paso convierte el estudio de estructuras de datos en una herramienta para diseñar soluciones.
