### Ejercicios de la Semana 7

Estos ejercicios son opcionales y no requieren entrega obligatoria.

La parte de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1, 2, 3, 4, 5, 6 y 7.

El objetivo no es repetir la Actividad 7 ni volver a ejecutar los mismos ejemplos utilizados para introducir `bubbleUp`, `trickleDown` y `heapify`.

La mayoría de los problemas exige:

```text
reconstruir estados
razonar antes de ejecutar
dibujar representaciones
detectar código que compila pero es incorrecto
diseñar contraejemplos
preservar varios invariantes simultáneamente
separar ADT de implementación
separar estado lógico de almacenamiento físico
combinar estructuras estudiadas en semanas anteriores
justificar complejidad
comparar algoritmos correctos con costos diferentes
diseñar pruebas que revelen errores estructurales
```

Los temas centrales de la Semana 7 utilizados son:

```text
problema de mantener disponible el mínimo

ADT Priority Queue
add
peek
remove
size
Queue frente a Priority Queue
BST y AVL frente a heap

BinaryHeap
árbol binario completo
representación implícita mediante arreglo
a[0..n-1]
n y capacidad
left(i)
right(i)
parent(i)
altura O(log n)

min-heap
invariante padre <= hijos
mínimo en a[0]
heap no ordenado totalmente
heap no es BST
duplicados

inserción al final
preservación de la forma completa
bubbleUp
reparación hacia arriba
add O(log n)
peek O(1)

eliminación del mínimo
reemplazo por el último elemento
trickleDown
menor hijo válido
reparación hacia abajo
remove O(log n)

heapify
hojas ya válidas
último nodo interno n/2 - 1
procesamiento de abajo hacia arriba
heapify O(n)
construcción mediante inserciones O(n log n)

representación
invariante
correctitud
complejidad
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
arreglos dinámicos
tamaño y capacidad
resize
costo amortizado
listas enlazadas
referencias
localización frente a modificación
Stack
Queue
ArrayQueue circular
estado lógico frente a estado físico

BST
findLast
contains
add
inorder
altura h

eliminación en BST
splice
sucesor inorder

AVL
height
balanceFactor
rotaciones
forma y altura
búsqueda O(log n)
inserción O(log n)

trazas
invariantes
contraejemplos
complejidad
```

No se requiere utilizar `java.util.PriorityQueue`, `TreeSet`, `TreeMap`, `ArrayList`, `java.util.Stack`, `java.util.ArrayDeque` ni otras estructuras de Java Collections.

No se requiere estudiar heaps binomiales, Fibonacci heaps, pairing heaps, heaps d-arios, `decreaseKey`, heaps indexados ni otras variantes avanzadas.

No se requiere desarrollar heapsort como tema independiente. Las extracciones repetidas pueden utilizarse únicamente para razonar sobre el comportamiento de una Priority Queue mínima.

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir operaciones nuevas sobre `BinaryHeap`, pero deben resolverse utilizando únicamente las ideas disponibles hasta la Semana 7.

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir un BinaryHeap a partir de su estado físico

Se observa el siguiente estado de una estructura:

```text
índice       0    1    2    3    4    5    6    7    8    9   10   11
           +----+----+----+----+----+----+----+----+----+----+----+----+
a          |  3 |  7 |  5 | 12 |  9 | 11 |  8 | 20 | 14 |    |    |    |
           +----+----+----+----+----+----+----+----+----+----+----+----+

n = 9
a.length = 12
```

Sin ejecutar código:

1. identifica qué posiciones pertenecen al estado lógico,
2. dibuja el árbol binario completo representado por `a[0..n-1]`,
3. calcula `left(i)`, `right(i)` y `parent(i)` para `i = 0`, `i = 2`, `i = 4` e `i = 8`,
4. distingue en cada caso entre un índice calculable y un nodo realmente existente,
5. verifica todas las relaciones padre-hijo necesarias para decidir si el estado es un min-heap,
6. determina qué retorna `peek()`,
7. indica qué índice recibiría una nueva clave antes de ejecutar `bubbleUp`,
8. explica qué diferencia existe entre `n` y `a.length`,
9. explica por qué observar solamente que `a[0]` contiene el mínimo no basta para demostrar que toda la estructura es un min-heap,
10. determina si la misma forma y las mismas claves representan necesariamente un BST.

Después construye otro arreglo con las mismas nueve claves que:

```text
tenga el mismo mínimo en a[0]
mantenga la forma completa
pero viole el invariante min-heap
```

Identifica la primera relación padre-hijo que permite demostrar el error.

#### Ejercicio 2. Elegir estructura a partir del patrón de operaciones

Se deben diseñar cinco componentes independientes.

Sistema A:

```text
las solicitudes deben procesarse estrictamente
en orden de llegada
```

Sistema B:

```text
se consulta muchas veces
cuál es el valor mínimo actual
también se insertan nuevos valores
y se retira repetidamente el mínimo
```

Sistema C:

```text
se buscan claves arbitrarias con mucha frecuencia
se necesita además recorrer las claves en orden
```

Sistema D:

```text
se insertan claves continuamente
se necesita búsqueda ordenada
y se desea garantizar altura O(log n)
```

Sistema E:

```text
90 % de las operaciones son get(i)
10 % son inserciones al final
```

Elige para cada sistema una estructura entre:

```text
arreglo dinámico
Queue
BST
AVL
BinaryHeap
```

Para cada elección:

1. identifica el ADT o comportamiento necesario,
2. indica la representación relevante,
3. formula el invariante principal,
4. identifica la operación dominante,
5. justifica los costos relevantes,
6. explica qué operación importante no favorece tu elección,
7. propone una alternativa que podría funcionar,
8. explica por qué la primera elección se ajusta mejor al patrón dado.

Concluye respondiendo:

> ¿Por qué no tiene sentido afirmar que `BinaryHeap` es mejor que `AVL`, o que `AVL` es mejor que `BinaryHeap`, sin especificar primero las operaciones que deben realizarse?.

### B. Retos integradores

#### Reto 1. Un `bubbleUp` que repara un nivel y abandona demasiado pronto

Un estudiante propone:

```java
private void bubbleUp(int i) {
    if (i > 0 && a[i] < a[parent(i)]) {
        int p = parent(i);
        swap(i, p);
    }
}
```

El método compila y corrige algunos casos.

No lo ejecutes inicialmente.

1. explica qué propiedad intenta verificar,
2. construye el heap válido más pequeño posible donde una nueva inserción necesite subir más de un nivel,
3. elige una nueva clave que revele el defecto,
4. muestra el arreglo justo después de escribir la clave al final,
5. realiza la única corrección que ejecuta el método propuesto,
6. identifica la relación padre-hijo que queda todavía incorrecta,
7. dibuja el árbol producido,
8. explica por qué la forma completa permanece correcta aunque el min-heap sea incorrecto,
9. reemplaza únicamente la estructura de control necesaria para corregir el método,
10. justifica por qué la versión correcta sigue un único camino,
11. determina la complejidad de ambas versiones,
12. explica por qué tener una complejidad asintótica no garantiza correctitud.

Después construye un caso donde la versión incorrecta funcione por casualidad.

#### Reto 2. El padre correcto se calcula, pero la posición actual nunca cambia

Se propone:

```java
private void bubbleUp(int i) {
    while (i > 0 && a[i] < a[parent(i)]) {
        int p = parent(i);
        swap(i, p);
    }
}
```

Sin ejecutarlo:

1. identifica la diferencia con el algoritmo correcto,
2. analiza qué ocurre después del primer `swap`,
3. determina si el ciclo puede terminar,
4. determina si puede aparecer un ciclo infinito,
5. construye un ejemplo mínimo donde el problema sea observable,
6. traza los valores de `i`, `p`, `a[i]` y `a[p]`,
7. indica qué asignación falta,
8. explica qué significado algorítmico tiene esa asignación,
9. distingue entre mover un valor dentro del arreglo y mover la posición desde la que continúa el algoritmo,
10. explica por qué el defecto no se detecta observando solamente el tipo de retorno del método.

#### Reto 3. `trickleDown` siempre elige el hijo izquierdo

Un estudiante propone:

```java
private void trickleDown(int i) {
    while (left(i) < n && a[left(i)] < a[i]) {
        int l = left(i);
        swap(i, l);
        i = l;
    }
}
```

El método ignora el hijo derecho.

No lo ejecutes inicialmente.

1. construye el heap válido más pequeño donde, después de reemplazar la raíz por el último elemento, ambos hijos sean menores que la nueva raíz,
2. exige además que el hijo derecho sea menor que el izquierdo,
3. realiza la traza del método propuesto,
4. identifica la primera relación que queda incorrecta,
5. explica por qué no basta con encontrar un hijo menor,
6. formula la regla correcta para seleccionar el menor entre:
   - el nodo actual,
   - el hijo izquierdo válido,
   - el hijo derecho válido,
7. escribe una versión corregida de `trickleDown`,
8. explica por qué las comprobaciones `l < n` y `r < n` son parte de la correctitud,
9. justifica `O(log n)`,
10. diseña un caso independiente donde exista solamente hijo izquierdo y verifica que tu versión lo maneje correctamente.

#### Reto 4. Una comparación del hijo derecho usa el candidato equivocado

Se propone:

```java
private void trickleDown(int i) {
    while (true) {
        int smallest = i;
        int l = left(i);
        int r = right(i);

        if (l < n && a[l] < a[smallest]) {
            smallest = l;
        }

        if (r < n && a[r] < a[i]) {
            smallest = r;
        }

        if (smallest == i) {
            return;
        }

        swap(i, smallest);
        i = smallest;
    }
}
```

La segunda comparación utiliza:

```java
a[r] < a[i]
```

en lugar de comparar con el mejor candidato encontrado.

Analiza sin ejecutar.

1. construye valores para `a[i]`, `a[l]` y `a[r]` tales que ambos hijos sean menores que el nodo actual pero el izquierdo sea menor que el derecho,
2. muestra qué valor toma `smallest` después del primer `if`,
3. muestra qué valor toma después del segundo,
4. explica por qué se elige el hijo equivocado,
5. determina qué relación puede quedar violada después del intercambio,
6. corrige únicamente una expresión,
7. construye otro caso donde el error quede oculto,
8. explica por qué los casos frontera son importantes para revisar código,
9. justifica que la versión corregida no cambia la complejidad.

#### Reto 5. `remove` preserva prioridad pero rompe la forma

Un estudiante intenta eliminar el mínimo buscando primero cuál de los hijos debe reemplazar a la raíz.

Propone conceptualmente:

```text
1. guardar a[0]
2. copiar el menor hijo en a[0]
3. seguir copiando hacia arriba el menor hijo de cada posición
4. cuando se alcance una hoja, escribir null
5. n--
```

La intención es mantener siempre una clave pequeña cerca de la raíz.

No escribas código todavía.

1. analiza si el procedimiento preserva necesariamente la forma completa,
2. construye un heap donde la hoja alcanzada por el camino no sea `a[n-1]`,
3. muestra el hueco que aparecería,
4. explica por qué un árbol completo no permite eliminar una hoja arbitraria,
5. indica qué nodo es el único que puede desaparecer físicamente sin dejar huecos,
6. reconstruye el procedimiento correcto de `remove`,
7. separa claramente:
   - preservación de forma,
   - reparación de prioridad,
8. explica por qué el valor del último nodo puede ser mayor que ambos hijos de la raíz,
9. explica por qué `trickleDown` comienza en 0,
10. justifica `remove -> O(log n)`.

Concluye:

> ¿Por qué una estrategia que parece mantener valores pequeños arriba puede seguir siendo incorrecta como implementación de `BinaryHeap`?

#### Reto 6. Un `heapify` procesa de arriba hacia abajo una sola vez

Un estudiante propone:

```java
private void heapify() {
    for (int i = 0; i < n / 2; i++) {
        trickleDown(i);
    }
}
```

El ciclo visita todos los nodos internos, pero en orden ascendente.

No lo ejecutes inicialmente.

1. explica qué precondición implícita utiliza `trickleDown(i)` respecto de los subárboles hijos,
2. determina por qué esa precondición puede no cumplirse al comenzar en la raíz,
3. construye un arreglo pequeño donde una única pasada ascendente no produzca un min-heap válido,
4. muestra el arreglo después de cada llamada a `trickleDown`,
5. identifica la relación que queda incorrecta,
6. explica por qué procesar todos los nodos internos no basta si el orden es incorrecto,
7. corrige el sentido del recorrido,
8. explica por qué `n/2 - 1` es el primer índice que debe procesarse,
9. distingue entre:
   - orden de visita,
   - operación local utilizada,
10. justifica intuitivamente por qué la versión correcta es `O(n)`.

Después responde:

> ¿Qué semejanza existe entre procesar `heapify` de abajo hacia arriba y actualizar primero información de nodos inferiores antes de usarla en nodos superiores?

Relaciona tu respuesta con alguna idea trabajada en AVL.

#### Reto 7. Dos construcciones correctas producen heaps diferentes

Se tienen las claves:

```text
18, 7, 25, 3, 14, 20, 11, 9, 2, 30
```

Construye dos heaps mínimos independientes.

Construcción A:

```text
partir de un heap vacío
ejecutar add en el orden dado
```

Construcción B:

```text
copiar todo el arreglo
aplicar heapify de abajo hacia arriba
```

Sin ejecutar inicialmente:

1. realiza la traza de la Construcción A,
2. registra cada `bubbleUp`,
3. realiza la traza de la Construcción B,
4. registra cada `trickleDown` relevante,
5. dibuja ambos árboles finales,
6. verifica el invariante en ambos,
7. determina si los arreglos finales son necesariamente iguales,
8. explica por qué dos representaciones distintas pueden implementar correctamente la misma Priority Queue,
9. compara los costos asintóticos de ambas construcciones,
10. identifica cuál aprovecha mejor el hecho de conocer todos los valores desde el inicio,
11. ejecuta después ambas estrategias y compara con tu traza manual.

No se evalúa que los heaps finales sean idénticos. Se evalúa que ambos sean válidos y que la explicación de los costos sea correcta.

#### Reto 8. Una `ArrayQueue` alimenta a una `Priority Queue`

Una `ArrayQueue` circular tiene:

```text
a.length = 10
j = 7
n = 8
```

y el arreglo físico:

```text
índice      0    1    2    3    4    5    6    7    8    9
a          17    6   23   14   11    _    _   31    4   19
```

La vista lógica correcta es:

```text
[31, 4, 19, 17, 6, 23, 14, 11]
```

Se dispone además de un `BinaryHeap` mínimo inicialmente vacío.

Se ejecuta seis veces:

```text
x = q.remove()
heap.add(x)
```

Después se ejecuta:

```text
heap.remove()
heap.add(5)
heap.remove()
```

Sin ejecutar código:

1. verifica la vista lógica de la cola usando:
   ```text
   a[(j+k) % a.length]
   ```
2. indica qué posición física se retira en cada una de las seis eliminaciones,
3. registra la evolución de `j` y `n` de la cola,
4. registra el arreglo lógico del heap después de cada `add`,
5. identifica cada `bubbleUp`,
6. determina el primer valor retornado por `heap.remove()`,
7. traza el `trickleDown` correspondiente,
8. inserta 5 y traza su reparación,
9. determina el segundo valor retornado por `heap.remove()`,
10. dibuja el estado final de ambas estructuras,
11. separa qué invariantes pertenecen a `ArrayQueue` y cuáles a `BinaryHeap`,
12. analiza el costo total en términos del número `m` de elementos transferidos,
13. distingue el costo amortizado de la cola del costo logarítmico del heap.

Este reto conecta directamente Semanas 1, 3 y 7.

#### Reto 9. Extraer mínimos, almacenarlos en una pila y reconstruir otro orden

Se construye un `BinaryHeap` con:

```text
26, 8, 17, 3, 12, 21, 5, 15
```

Después se dispone de una `LinkedStack` inicialmente vacía.

Se repite hasta vaciar el heap:

```text
x = heap.remove()
stack.push(x)
```

Finalmente se vacía la pila mediante:

```text
stack.pop()
```

Sin ejecutar inicialmente:

1. construye el heap mediante `heapify`,
2. registra la secuencia producida por `heap.remove()`,
3. registra el contenido lógico de la pila después de cada `push`,
4. determina la secuencia producida por todos los `pop`,
5. explica por qué el heap genera orden no decreciente durante las extracciones,
6. explica por qué la pila invierte esa secuencia,
7. determina el orden final producido por la pila,
8. indica qué parte del problema utiliza prioridad y cuál utiliza LIFO,
9. analiza el tiempo de construir el heap,
10. analiza el tiempo de vaciarlo,
11. analiza el tiempo de todas las operaciones de la pila,
12. expresa el tiempo total en función de `n`,
13. explica por qué este ejercicio combina dos ADT sin que ninguno reemplace al otro.

No utilices arreglos auxiliares para simular la pila.

#### Reto 10. Las mismas claves en BST, AVL y BinaryHeap

Se utilizan exactamente estas claves:

```text
6, 13, 18, 24, 29, 37, 42, 55, 61
```

Construye tres estructuras independientes.

A. BST simple, insertando en el orden mostrado.

B. AVL, insertando también en el orden mostrado.

C. BinaryHeap mínimo, insertando también en el orden mostrado.

Responde:

1. dibuja las tres estructuras finales,
2. calcula la altura del BST,
3. calcula la altura del AVL,
4. calcula la altura del árbol completo representado por el heap,
5. identifica el invariante principal de cada estructura,
6. traza `contains(42)` en BST,
7. traza `contains(42)` en AVL,
8. explica por qué no existe una decisión de rama equivalente para buscar 42 en el heap,
9. determina el costo de `peek()` en el heap,
10. determina qué trabajo sería necesario para conocer el mínimo en BST si no se almacena una referencia adicional,
11. compara la búsqueda de una clave arbitraria,
12. compara el acceso al mínimo,
13. identifica qué estructuras mantienen orden global,
14. identifica cuál mantiene únicamente orden local de prioridad,
15. explica por qué las tres estructuras pueden contener las mismas claves y aun así responder de manera diferente a distintas consultas.

No implementes eliminación AVL.

#### Reto 11. Crecimiento y reducción de capacidad dentro del heap

Se utiliza la política del archivo de la semana:

```text
crecer cuando:
n + 1 > a.length

nueva capacidad:
max(1, 2*n)

después de remove, reducir cuando:
a.length >= 3*n

nueva capacidad:
max(1, 2*n)
```

Parte de:

```text
a.length = 1
n = 0
```

Diseña una secuencia que contenga:

```text
al menos cuatro crecimientos
al menos una reducción
después un nuevo crecimiento
```

La secuencia debe mezclar:

```text
add
remove
```

Para cada operación donde cambie la capacidad registra:

| Operación | `n` antes | capacidad antes | `n` después | capacidad después | elementos copiados |
|---|---:|---:|---:|---:|---:|

Además:

1. registra el mínimo antes de cada `remove`,
2. demuestra que un `resize` no cambia el orden lógico del heap,
3. explica por qué copiar el prefijo `a[0..n-1]` conserva las relaciones de índices,
4. verifica que el invariante min-heap siga siendo válido después de cada redimensionamiento,
5. explica por qué reducir capacidad no necesita `heapify`,
6. determina qué costo aporta cada redimensionamiento,
7. explica por qué el crecimiento del arreglo y la reparación del heap son problemas diferentes,
8. relaciona este análisis con el costo amortizado estudiado en la Semana 1.

#### Reto 12. Un verificador de heap acepta capacidad, pero solo debe revisar estado lógico

Se desea implementar:

```java
boolean isMinHeap()
```

Un estudiante propone:

```java
boolean isMinHeap() {
    for (int i = 1; i < a.length; i++) {
        if (a[parent(i)] > a[i]) {
            return false;
        }
    }
    return true;
}
```

No lo ejecutes inicialmente.

1. identifica por qué recorrer hasta `a.length` es conceptualmente incorrecto,
2. explica qué posiciones forman parte del heap,
3. construye un estado con capacidad mayor que `n` donde el método produzca una excepción o una comparación inválida,
4. decide si conviene recorrer:
   ```text
   i = 1 ... n-1
   ```
   o solamente los nodos internos,
5. diseña una versión correcta,
6. justifica su tiempo,
7. determina si `isMinHeap()` necesita comprobar explícitamente que el mínimo está en `a[0]`,
8. explica por qué el invariante padre-hijo ya implica esa propiedad,
9. compara este verificador local con la validación de un BST global,
10. explica por qué verificar un heap puede formularse usando relaciones locales mientras verificar un BST exige respetar restricciones provenientes de ancestros.

#### Reto 13. Un heap correcto puede tener un inorder aparentemente extraño

Considera el heap:

```text
índice     0    1    2    3    4    5    6    7    8
a          1    4    2   11    9    8    5   16   13
```

1. dibuja el árbol,
2. verifica el invariante min-heap,
3. escribe el recorrido inorder del árbol,
4. determina si el inorder está ordenado,
5. explica por qué no debería esperarse que lo esté,
6. escribe el recorrido preorder,
7. determina si alguno de esos recorridos caracteriza por sí solo la Priority Queue,
8. compara con el significado de inorder en un BST,
9. construye otro heap válido con las mismas claves y un arreglo diferente,
10. compara sus recorridos,
11. explica qué propiedades deben ser iguales entre ambos heaps para que implementen el mismo ADT,
12. distingue entre:
    ```text
    contenido lógico
    representación concreta
    comportamiento observable
    ```

#### Reto 14. Auditoría completa de una implementación de `remove`

Se propone:

```java
int remove() {
    if (n == 0) {
        throw new NoSuchElementException("el montículo está vacío");
    }

    int min = a[0];

    a[0] = a[n - 1];
    a[n - 1] = null;

    if (n > 0) {
        trickleDown(0);
    }

    n--;

    return min;
}
```

No lo ejecutes inicialmente.

Analiza cuidadosamente el orden de las instrucciones.

1. identifica con qué valor de `n` se ejecuta `trickleDown`,
2. determina si la antigua última posición sigue perteneciendo al estado lógico durante la reparación,
3. construye un heap donde esa diferencia permita a `trickleDown` considerar un hijo que debería haber desaparecido,
4. traza la ejecución,
5. identifica el instante exacto en que estado lógico y almacenamiento físico dejan de coincidir,
6. corrige solamente el orden de las actualizaciones necesarias,
7. explica por qué `n` debe disminuir antes de reparar,
8. explica por qué limpiar `a[n]` debe realizarse usando el nuevo tamaño lógico,
9. verifica el caso `n = 1`,
10. verifica el caso `n = 2`,
11. justifica la complejidad de la versión corregida,
12. explica por qué dos versiones con el mismo conjunto de instrucciones pueden diferir en correctitud solamente por su orden.

#### Reto 15. Diseñar un grupo mínimo de pruebas para `BinaryHeap`

Recibes una implementación desconocida de:

```text
BinaryHeap
```

Solo puedes utilizar públicamente:

```java
boolean add(int x)
Integer peek()
int remove()
int size()
String toString()
```

La implementación puede contener errores en:

```text
bubbleUp
trickleDown
heapify
manejo de duplicados
caso vacío
caso de un elemento
hijo izquierdo único
selección entre dos hijos
crecimiento
reducción
actualización de n
```

Diseña un grupo pequeña pero deliberada de pruebas.

Para cada prueba especifica:

```text
estado inicial
operación o secuencia
salida esperada
estado lógico esperado
propiedad que intenta verificar
defecto que podría revelar
```

Tu grupo debe contener al menos:

1. una inserción que no necesite `bubbleUp`,
2. una inserción que necesite exactamente un intercambio,
3. una inserción que necesite varios intercambios,
4. duplicados,
5. eliminación de un heap de un elemento,
6. eliminación con un único hijo válido durante `trickleDown`,
7. eliminación donde el hijo derecho sea menor que el izquierdo,
8. construcción desde un arreglo ya válido,
9. construcción desde un arreglo fuertemente desordenado,
10. construcción donde el mínimo inicial esté en una hoja,
11. una secuencia que fuerce crecimiento,
12. una secuencia que fuerce reducción,
13. extracciones hasta vaciar,
14. una llamada adicional a `remove()` sobre vacío.

Después responde:

> ¿Por qué probar únicamente que las extracciones salen ordenadas podría no detectar todos los errores de representación?.

### C. Ampliación opcional

#### Reto opcional 1. Eliminar una posición arbitraria del heap

Se desea agregar:

```java
int removeAt(int i)
```

La operación elimina el elemento lógico almacenado actualmente en `a[i]`.

No se busca este elemento por valor. El índice `i` ya es conocido.

No puedes reconstruir todo el heap mediante `heapify()` después de cada llamada.

Resuelve:

1. valida el índice,
2. identifica qué posición debe desaparecer físicamente para preservar la forma completa,
3. mueve el último elemento lógico a `i`,
4. reduce `n`,
5. explica por qué la nueva clave colocada en `i` puede violar el invariante:
   - con su padre,
   - o con sus hijos,
6. determina una condición que permita decidir entre `bubbleUp(i)` y `trickleDown(i)`,
7. analiza el caso `i == n - 1` antes de eliminar,
8. analiza el caso `i == 0`,
9. analiza el caso donde el valor de reemplazo es menor que su padre,
10. analiza otro donde es mayor que uno de sus hijos,
11. escribe la implementación,
12. justifica `O(log n)`.

Este reto combina en una sola operación las dos direcciones de reparación estudiadas en la semana.

#### Reto opcional 2. Fusionar dos heaps sin insertar uno por uno

Se tienen dos `BinaryHeap` mínimos válidos:

```text
H1 con n elementos
H2 con m elementos
```

Se desea construir un nuevo heap con todos sus elementos.

Estrategia A:

```text
crear un heap vacío
insertar los n + m elementos mediante add
```

Estrategia B:

```text
crear un arreglo de longitud n + m
copiar primero el contenido lógico de H1
copiar después el contenido lógico de H2
ejecutar heapify una sola vez
```

Responde:

1. explica por qué concatenar los arreglos no produce necesariamente un heap,
2. explica por qué sí produce inmediatamente la forma de árbol completo,
3. explica por qué `heapify` puede restaurar prioridad,
4. calcula el costo de las copias,
5. calcula el costo de `heapify`,
6. determina el costo total de la Estrategia B,
7. compara con la Estrategia A,
8. determina qué estrategia es asintóticamente mejor,
9. escribe pseudocódigo o Java para la Estrategia B,
10. verifica el resultado usando dos heaps pequeños construidos por ti,
11. explica por qué no es necesario conservar las formas originales de H1 y H2.

No modifiques H1 ni H2.

#### Reto opcional 3. Encontrar el k-ésimo menor sin modificar el heap original

Se dispone de un `BinaryHeap` mínimo válido con `n` elementos.

Se desea:

```java
Integer kthSmallest(int k)
```

con:

```text
1 <= k <= n
```

La estructura original no debe cambiar.

Puedes crear una copia del prefijo lógico y construir un segundo heap.

Responde:

1. explica por qué `peek()` resuelve solamente `k = 1`,
2. explica por qué el arreglo interno no puede leerse simplemente de izquierda a derecha para obtener los siguientes mínimos,
3. diseña una solución basada en una copia,
4. indica cuántas extracciones son necesarias,
5. justifica el costo de construir la copia,
6. justifica el costo de construir el heap auxiliar,
7. justifica el costo de las extracciones,
8. expresa el costo total usando `n` y `k`,
9. analiza el espacio adicional,
10. verifica que el heap original permanezca sin cambios,
11. explica por qué esta solución utiliza únicamente operaciones conocidas hasta la Semana 7.

No se requiere diseñar un algoritmo más avanzado.

#### Reto opcional 4. Probar o refutar propiedades de heaps

Para cada afirmación decide si es verdadera o falsa.

Si es verdadera, proporciona un argumento.

Si es falsa, construye el contraejemplo más pequeño que puedas.

1. Todo arreglo estrictamente creciente representa un min-heap.
2. Todo min-heap está ordenado de izquierda a derecha en su arreglo.
3. Todo min-heap tiene su mínimo en `a[0]`.
4. Todo subárbol de un min-heap es también un min-heap.
5. El prefijo `a[0..k-1]` de un min-heap válido representa siempre un min-heap válido para todo `1 <= k <= n`.
6. Si `a[0]` es el mínimo global, entonces el arreglo necesariamente es un min-heap.
7. Un min-heap puede contener duplicados.
8. Un min-heap es necesariamente un BST.
9. Dos min-heaps con las mismas claves deben tener el mismo arreglo interno.
10. Si el hijo izquierdo de un nodo es mayor que el nodo, entonces no es necesario comprobar el hijo derecho.
11. Después de `add`, la única posible violación nueva está sobre el camino desde la nueva hoja hasta la raíz.
12. Después de reemplazar la raíz por el último elemento en `remove`, la única posible violación nueva está sobre un camino descendente.
13. Ejecutar `heapify` sobre un heap que ya es válido debe conservar la propiedad de heap, aunque no se exija demostrar que conserve exactamente el mismo arreglo.
14. `heapify` y `n` inserciones implementan el mismo ADT aunque puedan construir representaciones diferentes.

Para las afirmaciones falsas no basta con responder "falso". Debes indicar exactamente qué parte del invariante no se deduce de la premisa.
