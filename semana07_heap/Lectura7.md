### Lectura: BinaryHeap, cola de prioridad y mantenimiento eficiente del mínimo

Esta lectura consolida y amplía las ideas trabajadas en la Semana 7 de CC232.

Durante las seis semanas anteriores hemos cambiado de representación o de invariante cada vez que cambió el tipo de operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico.

```text
arreglo + tamaño lógico + capacidad
```

Esa representación permitió acceso directo por índice y nos obligó a distinguir entre estado lógico, almacenamiento físico y redimensionamiento.

En la Semana 2 utilizamos nodos enlazados.

```text
nodo -> nodo -> nodo
```

La modificación local se volvió barata cuando ya conocíamos las referencias apropiadas, aunque localizar una posición dejó de ser directo.

En la Semana 3 estudiamos Stack, Queue y Deque.

La pregunta central pasó a ser:

```text
¿qué operaciones permite el ADT
sobre la secuencia?
```

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda.

```text
subárbol izquierdo < nodo < subárbol derecho
```

Ese invariante permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones.

En la Semana 6 estudiamos AVL.

El BST seguía siendo correcto, pero ahora añadimos información de altura y un invariante de balance para impedir que la estructura degenerara en una cadena.

La Semana 7 cambia nuevamente la pregunta.

```text
¿qué ocurre si no necesitamos
buscar cualquier clave eficientemente,
sino mantener disponible el elemento mínimo?
```

Esta pregunta conduce al ADT `Priority Queue` y a una de sus implementaciones clásicas, `BinaryHeap`.

La idea central de la semana puede anticiparse así:

```text
Priority Queue
    define qué comportamiento queremos

BinaryHeap
    representa el estado mediante un arreglo
    que codifica un árbol binario completo

forma completa
    controla la altura

invariante min-heap
    controla la prioridad

bubbleUp y trickleDown
    restauran el invariante después de modificar

heapify
    construye eficientemente un heap desde un arreglo
```

El objetivo no es memorizar las fórmulas `2*i + 1`, `2*i + 2` y `(i - 1)/2` ni repetir mecánicamente `bubbleUp`, `trickleDown` y `heapify`.

El objetivo es continuar la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    cómo almacenamos el estado

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo modificamos y reparamos la representación

complejidad
    cuánto trabajo exige cada operación
```

Al finalizar la lectura deberías poder reconstruir las operaciones fundamentales de un `BinaryHeap` a partir de su representación y de sus invariantes.

### 1. El nuevo problema: mantener el elemento mínimo

Considera una aplicación que recibe trabajos con una prioridad entera.

Usaremos la convención:

```text
menor número
    mayor prioridad
```

Por ejemplo:

```text
Trabajo A -> prioridad 8
Trabajo B -> prioridad 3
Trabajo C -> prioridad 6
Trabajo D -> prioridad 2
```

Si en cada instante necesitamos atender el trabajo más urgente, queremos responder repetidamente:

```text
¿cuál es el menor valor actual?
```

Una posibilidad sería almacenar todos los valores en un arreglo sin ningún orden particular.

```text
[8, 3, 6, 2]
```

Encontrar el mínimo exigiría revisar todos los elementos.

En el peor caso:

```text
buscar mínimo -> O(n)
```

Si esta consulta ocurre muchas veces, repetir una búsqueda lineal puede ser costoso.

Otra posibilidad sería mantener el arreglo completamente ordenado.

```text
[2, 3, 6, 8]
```

Ahora consultar el mínimo es inmediato, pero insertar un nuevo valor puede requerir desplazar muchos elementos para conservar el orden total.

Aparece un compromiso:

```text
estructura sin ordenar
    inserción sencilla
    mínimo costoso

estructura totalmente ordenada
    mínimo sencillo
    inserción potencialmente costosa
```

La cola de prioridad intenta conservar únicamente el orden necesario para que el elemento extremo permanezca disponible eficientemente.

### 2. ADT Priority Queue

Una **Priority Queue** es un tipo abstracto de dato en el que los elementos se atienden según una prioridad.

En esta semana utilizaremos una cola de prioridad mínima.

Conceptualmente queremos ofrecer:

```java
boolean add(int x)
Integer peek()
int remove()
int size()
```

Cada operación tiene una responsabilidad diferente.

#### add

```text
add(x)
```

incorpora un nuevo elemento a la cola de prioridad.

No exige que el nuevo elemento quede en una posición específica visible para el usuario.

Solo exige que, después de insertarlo, la estructura siga representando correctamente la Priority Queue.

#### peek

```text
peek()
```

consulta el mínimo actual sin retirarlo.

Si la estructura mantiene el mínimo en una posición conocida, esta consulta puede ser muy barata.

#### remove

```text
remove()
```

retira y retorna el mínimo actual.

Esta operación sí modifica el estado y deberá conservar los invariantes de la representación.

#### size

```text
size()
```

retorna la cantidad de elementos lógicos almacenados.

La diferencia entre ADT e implementación sigue siendo fundamental.

```text
Priority Queue
    qué comportamiento observamos

BinaryHeap
    una forma concreta de implementar ese comportamiento
```

Podrían existir otras implementaciones de Priority Queue. Esta semana estudiamos `BinaryHeap` porque combina una representación compacta con operaciones eficientes.

### 3. Queue y Priority Queue no tienen la misma política

En la Semana 3 estudiamos Queue.

Su política era:

```text
FIFO
First In, First Out
primero en entrar, primero en salir
```

Si hacemos:

```text
add(8)
add(3)
add(6)
```

en una Queue, el primer elemento retirado debe ser:

```text
8
```

porque fue el primero en llegar.

En una Priority Queue mínima, los mismos valores producirían primero:

```text
3
```

porque es el menor.

La diferencia puede resumirse así:

```text
Queue
    criterio de salida = orden de llegada

Priority Queue
    criterio de salida = prioridad
```

La semejanza de algunos nombres de operaciones no debe ocultar la diferencia semántica entre ambos ADT.

### 4. BST y AVL tampoco resuelven exactamente el mismo problema

Un BST permite decidir por qué rama continuar después de comparar una clave con el nodo actual.

```text
x < u.x
    buscar a la izquierda

x > u.x
    buscar a la derecha
```

Esto funciona porque mantiene un invariante global de orden.

```text
todas las claves de la izquierda < u.x

todas las claves de la derecha > u.x
```

AVL mantiene el mismo invariante de búsqueda y añade balance.

Por eso BST y AVL son apropiados cuando queremos operaciones como:

```text
buscar una clave arbitraria
insertar una clave
eliminar una clave específica
recorrer las claves en orden
```

Un heap persigue un objetivo diferente.

```text
mantener disponible el mínimo
```

No necesita ordenar completamente los subárboles como un BST.

Esta diferencia será importante durante toda la semana.

### 5. Dos decisiones independientes: forma y prioridad

Un `BinaryHeap` combina dos propiedades distintas.

La primera controla la **forma**.

```text
árbol binario completo
```

La segunda controla la **prioridad**.

```text
padre <= hijos
```

No debemos mezclar ambas ideas.

Podemos tener un árbol completo que no sea heap porque sus valores violan el orden padre-hijo.

También podemos imaginar un árbol que cumpla padre-hijo pero tenga una forma que no corresponda al `BinaryHeap` basado en arreglo que estudiaremos.

La estructura correcta necesita ambas propiedades simultáneamente.

```text
forma completa
+
invariante min-heap
=
BinaryHeap válido
```

### 6. Árbol binario completo

Un árbol binario completo llena sus niveles de arriba hacia abajo y, en el último nivel, de izquierda a derecha.

Por ejemplo:

```text
          2
        /   \
       5     4
      / \   / \
     9   7 8   6
```

es completo.

También lo es:

```text
          2
        /   \
       5     4
      / \   /
     9   7 8
```

El último nivel todavía no está lleno, pero sus posiciones existentes aparecen desde la izquierda.

En cambio:

```text
          2
        /   \
       5     4
        \   /
         7 8
```

no representa la forma completa que necesitamos porque aparece un hueco antes de una posición ocupada.

### 7. La forma completa controla la altura

En un árbol binario completo, los niveles pueden contener aproximadamente:

```text
nivel 0 -> 1 nodo
nivel 1 -> 2 nodos
nivel 2 -> 4 nodos
nivel 3 -> 8 nodos
nivel 4 -> 16 nodos
...
```

La capacidad crece exponencialmente con el número de niveles.

Por ello, para almacenar `n` nodos necesitamos una cantidad de niveles proporcional a:

```text
log n
```

Entonces:

```text
h = O(log n)
```

Esta garantía no depende del orden de inserción de las claves.

Es una consecuencia de la forma completa.

Aquí aparece una diferencia importante con AVL.

```text
AVL
    representación enlazada
    forma no necesariamente completa
    controla altura con balance y rotaciones

BinaryHeap
    representación implícita en arreglo
    impone forma completa
    obtiene altura O(log n) directamente de esa forma
```

### 8. Del árbol completo al arreglo

La forma completa permite representar el árbol sin almacenar explícitamente:

```text
left
right
parent
```

Considera:

```text
          2
        /   \
       5     4
      / \   / \
     9   7 8   6
```

Si guardamos los nodos por niveles obtenemos:

```text
índice     0   1   2   3   4   5   6
          +---+---+---+---+---+---+---+
a         | 2 | 5 | 4 | 9 | 7 | 8 | 6 |
          +---+---+---+---+---+---+---+
```

La raíz está en `a[0]`.

Los hijos de la raíz están en `a[1]` y `a[2]`.

Los hijos de `a[1]` están en `a[3]` y `a[4]`.

Los hijos de `a[2]` están en `a[5]` y `a[6]`.

La posición física del arreglo también codifica la posición lógica dentro del árbol.

### 9. Estado lógico `a[0..n-1]`

La Semana 1 distinguió tamaño y capacidad.

La misma idea reaparece aquí.

Supongamos:

```text
índice      0   1   2   3   4   5   6   7   8   9
           +---+---+---+---+---+---+---+---+---+---+
a          | 2 | 5 | 4 | 9 | 7 | 8 | 6 |   |   |   |
           +---+---+---+---+---+---+---+---+---+---+

n = 7
a.length = 10
```

El estado lógico del heap está únicamente en:

```text
a[0..n-1]
```

es decir:

```text
a[0..6]
```

Las posiciones `a[7]`, `a[8]` y `a[9]` pertenecen al almacenamiento físico, pero todavía no representan nodos del heap.

Por tanto:

```text
n
    cantidad de elementos lógicos

a.length
    capacidad física
```

La representación sigue necesitando:

```text
0 <= n <= a.length
```

### 10. Relación árbol-arreglo

Cuando la raíz se almacena en el índice 0, las relaciones jerárquicas pueden calcularse aritméticamente.

Para un nodo de índice `i`:

```text
left(i) = 2*i + 1
```

```text
right(i) = 2*i + 2
```

Para un nodo distinto de la raíz:

```text
parent(i) = (i - 1) / 2
```

utilizando división entera.

Por ejemplo, si:

```text
i = 2
```

entonces:

```text
left(2)  = 5
right(2) = 6
parent(2) = 0
```

Si:

```text
i = 5
```

entonces:

```text
left(5)  = 11
right(5) = 12
parent(5) = 2
```

Pero calcular `left(5) = 11` no significa que el hijo exista.

Para que exista lógicamente debe cumplirse:

```text
11 < n
```

Esta distinción es importante:

```text
índice calculable
no implica
nodo existente
```

### 11. Por qué aparecen esas fórmulas

No conviene memorizar las expresiones sin entender su origen.

Los índices por nivel son:

```text
nivel 0
    0

nivel 1
    1 2

nivel 2
    3 4 5 6

nivel 3
    7 8 9 10 11 12 13 14
```

Para un nodo `i`, las posiciones del nivel siguiente que corresponden a sus dos hijos aparecen consecutivamente.

El patrón produce:

```text
hijo izquierdo = 2*i + 1
hijo derecho   = 2*i + 2
```

La fórmula del padre invierte esa relación.

Comprender la correspondencia es más útil que recordar tres expresiones aisladas.

### 12. El invariante min-heap

La forma completa todavía no dice nada sobre las prioridades.

Necesitamos un segundo invariante.

Para todo índice lógico `i > 0`:

```text
a[parent(i)] <= a[i]
```

En palabras:

```text
cada padre es menor o igual que cada hijo existente
```

Por ejemplo:

```text
          2
        /   \
       5     4
      / \   / \
     9   7 8   6
```

cumple:

```text
2 <= 5
2 <= 4
5 <= 9
5 <= 7
4 <= 8
4 <= 6
```

Por tanto, es un min-heap válido.

### 13. Por qué el mínimo está en `a[0]`

La raíz del árbol corresponde a:

```text
a[0]
```

Tomemos cualquier nodo `u`.

Por el invariante:

```text
parent(u) <= u
```

y también:

```text
parent(parent(u)) <= parent(u)
```

Si repetimos el argumento hasta llegar a la raíz obtenemos una cadena:

```text
raíz <= ... <= padre <= nodo
```

Por tanto, la raíz es menor o igual que cualquier nodo alcanzable.

Así:

```text
mínimo global = a[0]
```

Esta propiedad explica directamente:

```text
peek() -> O(1)
```

`peek` no busca el mínimo.

El trabajo para mantenerlo accesible ya fue realizado por las operaciones que modificaron la estructura.

### 14. Un heap no es un arreglo ordenado

Considera:

```text
[2, 5, 4, 12, 9, 7, 6]
```

Este arreglo no está ordenado de menor a mayor porque:

```text
5 > 4
```

y:

```text
12 > 9
```

Sin embargo, puede ser un min-heap válido.

Lo que debemos comprobar es:

```text
2 <= 5 y 4
5 <= 12 y 9
4 <= 7 y 6
```

El invariante no exige:

```text
a[0] <= a[1] <= a[2] <= ...
```

Exige únicamente relaciones entre padres e hijos.

Esta diferencia evita mantener más orden del que necesita la Priority Queue.

### 15. Un heap no es un BST

Considera:

```text
          2
        /   \
       8     4
      / \   / \
     9  10 7   6
```

Es posible que sea un min-heap porque cada padre es menor o igual que sus hijos.

Pero no es un BST.

Respecto de la raíz 2, en un BST todas las claves del subárbol derecho serían mayores que 2, lo cual ocurre, pero también necesitaríamos que cada subárbol cumpliera una organización global de búsqueda.

En el subárbol con raíz 8 aparece 10 como hijo derecho, lo cual podría ser compatible, pero otras configuraciones heap válidas pueden colocar valores menores o mayores en ramas sin respetar el orden global BST.

La regla de un heap es solamente:

```text
padre <= hijos
```

La regla de un BST es:

```text
todo el subárbol izquierdo < nodo < todo el subárbol derecho
```

Son invariantes diferentes para ADT diferentes.

### 16. Duplicados permitidos

El invariante usa:

```text
<=
```

y no:

```text
<
```

Por tanto, un estado como:

```text
          2
        /   \
       2     5
```

es válido.

El padre 2 es menor o igual que el hijo 2.

Esto significa que nuestra Priority Queue puede almacenar varias entradas con la misma prioridad.

La estructura no necesita buscar primero si el valor ya existe.

Esta decisión contrasta con el BST y AVL didácticos de semanas anteriores, donde trabajamos con claves únicas.

### 17. Inserción: primero preservar la forma

Supongamos que el heap lógico es:

```text
[4, 7, 6, 12, 9, 10, 8]
```

Queremos insertar:

```text
2
```

La forma del árbol completo determina dónde debe aparecer el nuevo nodo.

No elegimos la posición según el valor.

La nueva clave se escribe en la primera posición libre:

```text
a[n]
```

Antes:

```text
[4, 7, 6, 12, 9, 10, 8]
```

Después de insertar físicamente al final:

```text
[4, 7, 6, 12, 9, 10, 8, 2]
```

La forma completa se conserva automáticamente.

Pero ahora puede haberse roto el invariante min-heap.

### 18. La violación después de insertar es local

Antes de insertar, todas las relaciones padre-hijo eran válidas.

La única relación nueva es:

```text
nueva hoja
con
su padre
```

En el ejemplo, 2 queda debajo de 12.

```text
2 < 12
```

Eso viola:

```text
padre <= hijo
```

No necesitamos revisar todo el árbol.

La única posible violación nueva comienza en el camino entre la nueva hoja y la raíz.

Esta observación conduce a `bubbleUp`.

### 19. `bubbleUp`: reparar hacia arriba

Partimos de:

```text
[4, 7, 6, 12, 9, 10, 8, 2]
```

La nueva clave 2 está en el índice 7.

Su padre está en:

```text
parent(7) = 3
```

El valor del padre es 12.

Como:

```text
2 < 12
```

intercambiamos:

```text
[4, 7, 6, 2, 9, 10, 8, 12]
```

Ahora 2 está en el índice 3.

Su padre está en el índice 1 y contiene 7.

Como:

```text
2 < 7
```

intercambiamos:

```text
[4, 2, 6, 7, 9, 10, 8, 12]
```

Ahora 2 está en el índice 1.

Su padre está en la raíz y contiene 4.

Como:

```text
2 < 4
```

intercambiamos:

```text
[2, 4, 6, 7, 9, 10, 8, 12]
```

El elemento llegó a la raíz.

La reparación termina.

### 20. La idea de `bubbleUp` en Java

La estructura conceptual del método es:

```java
private void bubbleUp(int i) {
    while (i > 0 && a[i] < a[parent(i)]) {
        int p = parent(i);
        swap(i, p);
        i = p;
    }
}
```

La condición:

```text
i > 0
```

indica que el nodo todavía tiene padre.

La condición:

```text
a[i] < a[parent(i)]
```

indica que existe una violación.

Después de intercambiar:

```text
i = p
```

continúa desde el nivel superior.

La igualdad no exige intercambio porque:

```text
padre == hijo
```

sigue cumpliendo el min-heap.

### 21. Correctitud intuitiva de `bubbleUp`

Antes de insertar:

```text
todo el heap es válido
```

Después de colocar la hoja:

```text
solo puede fallar la relación
nueva hoja -> padre
```

Después de un intercambio, la relación que acabamos de atravesar queda reparada.

Si aparece otra violación, solo puede estar un nivel más arriba.

Así, la violación se desplaza por un único camino hasta que:

```text
el elemento llega a la raíz
```

o:

```text
su padre ya es <= que él
```

Al terminar, todas las relaciones padre-hijo vuelven a ser válidas.

### 22. Complejidad de `add`

Insertar físicamente al final utiliza el mismo principio del arreglo dinámico de la Semana 1.

Si existe capacidad:

```text
escribir en a[n]
actualizar n
```

es trabajo constante.

Puede existir un redimensionamiento ocasional, analizado amortizadamente como en la Semana 1.

Después aparece `bubbleUp`.

Cada intercambio sube exactamente un nivel.

Como:

```text
h = O(log n)
```

entonces:

```text
bubbleUp -> O(log n)
```

y:

```text
add -> O(log n)
```

La cota logarítmica domina el trabajo de reparación.

### 23. `peek` es más barato que `add`

`peek` solo necesita consultar:

```text
a[0]
```

Por tanto:

```text
peek -> O(1)
```

No es contradictorio que `add` sea más costoso.

La estructura invierte trabajo durante modificaciones para conservar el mínimo en una posición conocida.

Podemos pensar:

```text
add
    paga el costo de reparar

peek
    aprovecha el invariante ya mantenido
```

### 24. Eliminar el mínimo: un problema de forma y prioridad

En un min-heap el elemento que `remove()` debe retirar está en:

```text
a[0]
```

Supongamos:

```text
[2, 5, 4, 9, 7, 8, 6, 12, 11]
```

Si simplemente hacemos:

```text
a[0] = null
```

quedaría un hueco en la raíz.

El estado lógico ya no correspondería a un árbol completo almacenado en el prefijo del arreglo.

La eliminación debe preservar primero la forma.

### 25. El último nodo es el único que puede desaparecer sin dejar huecos

En un árbol completo almacenado en un arreglo, el último nodo lógico está en:

```text
a[n-1]
```

Eliminar esa posición no deja huecos en el prefijo.

Por ello, para retirar la raíz utilizamos la estrategia:

```text
1. guardar el mínimo
2. mover el último valor a la raíz
3. disminuir n
4. limpiar la posición liberada
5. reparar prioridad desde la raíz
```

Con el ejemplo:

```text
antes
[2, 5, 4, 9, 7, 8, 6, 12, 11]
```

Guardamos 2 y llevamos 11 a la raíz:

```text
[11, 5, 4, 9, 7, 8, 6, 12]
```

La forma completa está preservada.

El problema ahora es el invariante de prioridad.

### 26. La violación después de eliminar comienza en la raíz

Antes de eliminar, el heap era válido.

Todos los subárboles por debajo de la raíz siguen conteniendo las mismas relaciones padre-hijo.

La única novedad es que el antiguo último elemento ocupa ahora la raíz.

Por tanto, la posible violación comienza allí y puede propagarse hacia abajo.

Esta observación conduce a `trickleDown`.

### 27. Por qué debemos elegir el menor hijo

En el estado:

```text
[11, 5, 4, 9, 7, 8, 6, 12]
```

la raíz 11 tiene hijos:

```text
5 y 4
```

Ambos son menores que 11.

No basta con escoger cualquier hijo.

Debemos escoger el menor:

```text
4
```

Después del intercambio:

```text
[4, 5, 11, 9, 7, 8, 6, 12]
```

Si hubiéramos intercambiado con 5, la raíz quedaría 5 mientras existiría un hijo 4, y la violación continuaría inmediatamente en la raíz.

La selección del menor hijo garantiza que el valor que sube sea compatible con ambos hijos del nivel actual.

### 28. `trickleDown`: reparar hacia abajo

Continuemos desde:

```text
[4, 5, 11, 9, 7, 8, 6, 12]
```

El valor 11 está ahora en el índice 2.

Sus hijos válidos son:

```text
8 y 6
```

El menor es 6.

Como:

```text
6 < 11
```

intercambiamos:

```text
[4, 5, 6, 9, 7, 8, 11, 12]
```

Ahora 11 está en el índice 6.

Si no tiene hijos válidos, terminamos.

El heap vuelve a satisfacer el invariante.

### 29. La idea de `trickleDown` en Java

Conceptualmente:

```java
private void trickleDown(int i) {
    while (true) {
        int smallest = i;
        int l = left(i);
        int r = right(i);

        if (l < n && a[l] < a[smallest]) {
            smallest = l;
        }

        if (r < n && a[r] < a[smallest]) {
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

La condición:

```text
l < n
```

comprueba que el hijo izquierdo existe lógicamente.

La condición:

```text
r < n
```

hace lo mismo con el hijo derecho.

El hijo derecho se compara con:

```text
a[smallest]
```

y no siempre con `a[i]`, porque el hijo izquierdo puede haberse convertido ya en el mejor candidato.

### 30. Correctitud intuitiva de `trickleDown`

Antes de mover el último elemento a la raíz:

```text
todo el heap era válido
```

Después del reemplazo:

```text
solo la nueva raíz local
puede ser demasiado grande
respecto de sus hijos
```

Seleccionamos el menor entre:

```text
nodo actual
hijo izquierdo válido
hijo derecho válido
```

Si el nodo actual ya es el menor, terminamos.

Si un hijo es menor, intercambiamos con el menor hijo.

La posible violación se desplaza un nivel hacia abajo.

Como los demás subárboles ya eran heaps válidos, no necesitamos explorar otras ramas.

### 31. Complejidad de `remove`

Cada intercambio de `trickleDown` baja exactamente un nivel.

La altura del árbol completo es:

```text
O(log n)
```

Por tanto:

```text
trickleDown -> O(log n)
```

y:

```text
remove -> O(log n)
```

La operación preserva la forma en tiempo constante y después paga como máximo un recorrido vertical logarítmico.

### 32. Simetría entre inserción y eliminación

Las dos operaciones principales tienen una estructura muy parecida.

```text
add
    preservar forma
        insertar al final
    posible violación
        nueva hoja con su padre
    reparar
        bubbleUp

remove
    preservar forma
        reemplazar raíz por último
        retirar último
    posible violación
        nueva raíz con sus hijos
    reparar
        trickleDown
```

Esta simetría es más importante que memorizar dos bloques de código.

En ambos casos:

```text
primero preservamos representación
luego restauramos invariante
```

### 33. Construir un heap mediante inserciones repetidas

Supongamos que recibimos un arreglo arbitrario:

```text
[14, 9, 7, 12, 5, 10, 3, 8]
```

Una estrategia correcta consiste en empezar con un heap vacío y ejecutar `add` para cada elemento.

Cada inserción cuesta como máximo:

```text
O(log n)
```

Para `n` elementos obtenemos una cota:

```text
O(n log n)
```

Esta estrategia funciona, pero no aprovecha toda la información disponible.

El arreglo ya contiene los `n` elementos en posiciones contiguas.

Por tanto, la forma de árbol completo ya está presente.

Solo falta reparar el invariante de prioridad.

### 34. `heapify`: construir desde abajo hacia arriba

`heapify` transforma un arreglo arbitrario en un heap válido.

La idea central es:

```text
las hojas ya son heaps válidos
```

Una hoja no tiene hijos.

Por tanto, no puede violar la relación:

```text
padre <= hijos
```

dentro de su propio subárbol.

No necesitamos procesar las hojas.

Debemos empezar por el último nodo que puede tener al menos un hijo.

### 35. Por qué el último nodo interno es `n/2 - 1`

En un arreglo de tamaño `n`, los índices:

```text
n/2, n/2 + 1, ..., n - 1
```

corresponden a hojas.

Por tanto, el último nodo interno está en:

```text
n/2 - 1
```

con división entera.

Por ejemplo, si:

```text
n = 8
```

entonces:

```text
n/2 - 1 = 3
```

Los índices 4, 5, 6 y 7 son hojas.

Los índices 0, 1, 2 y 3 pueden tener hijos.

### 36. Procesar de abajo hacia arriba

El esquema de `heapify` es:

```java
private void heapify() {
    for (int i = n / 2 - 1; i >= 0; i--) {
        trickleDown(i);
    }
}
```

El orden descendente es esencial.

Cuando procesamos un nodo `i`, sus hijos pertenecen a niveles inferiores.

Esos subárboles ya fueron procesados.

Por tanto, `trickleDown(i)` puede asumir que los subárboles hijos ya son heaps válidos y solo necesita reparar la raíz local.

Esta es una construcción de abajo hacia arriba.

### 37. Traza de `heapify`

Considera:

```text
[9, 4, 7, 1, 3, 6, 2]
```

Tenemos:

```text
n = 7
n/2 - 1 = 2
```

Empezamos en `i = 2`.

El valor 7 tiene hijos 6 y 2.

El menor hijo es 2.

Después de reparar:

```text
[9, 4, 2, 1, 3, 6, 7]
```

Ahora `i = 1`.

El valor 4 tiene hijos 1 y 3.

El menor hijo es 1.

Después:

```text
[9, 1, 2, 4, 3, 6, 7]
```

Finalmente `i = 0`.

La raíz 9 tiene hijos 1 y 2.

El menor hijo es 1.

Después de bajar 9 y continuar reparando:

```text
[1, 3, 2, 4, 9, 6, 7]
```

El arreglo final no está totalmente ordenado.

Pero sí es un min-heap.

### 38. Por qué `heapify` no cuesta `O(n log n)`

Sabemos que una llamada individual a `trickleDown` puede costar:

```text
O(log n)
```

Podríamos intentar multiplicar:

```text
aproximadamente n/2 nodos internos
por
O(log n)
```

y obtener:

```text
O(n log n)
```

Esa es una cota superior válida, pero no describe ajustadamente el trabajo real.

La mayoría de los nodos está muy cerca de las hojas.

Aproximadamente:

```text
n/2 nodos
    son hojas
    bajan 0 niveles

n/4 nodos
    están a altura aproximada 1
    pueden bajar como máximo 1 nivel

n/8 nodos
    están a altura aproximada 2
    pueden bajar como máximo 2 niveles

n/16 nodos
    están a altura aproximada 3
    pueden bajar como máximo 3 niveles
```

y así sucesivamente.

El trabajo total tiene la forma aproximada:

```text
n/4 * 1
+
n/8 * 2
+
n/16 * 3
+
...
```

Factorizando `n`:

```text
n * (1/4 + 2/8 + 3/16 + ...)
```

La serie entre paréntesis está acotada por una constante.

Por tanto:

```text
heapify -> O(n)
```

La idea importante es:

```text
hay muchos nodos baratos
hay pocos nodos caros
```

### 39. `heapify O(n)` frente a inserciones `O(n log n)`

Tenemos dos estrategias correctas para construir un heap desde `n` valores.

```text
Estrategia A
    heap inicialmente vacío
    insertar n veces con add
    O(n log n)

Estrategia B
    copiar los valores al arreglo
    preservar directamente la forma completa
    heapify de abajo hacia arriba
    O(n)
```

Ambas pueden producir heaps válidos diferentes.

Eso no es un problema.

El ADT no exige una única representación interna.

Solo exige que la estructura satisfaga sus invariantes y que las operaciones produzcan el comportamiento esperado.

### 40. Extracciones repetidas producen orden no decreciente

En un min-heap:

```text
remove()
```

retorna siempre el mínimo actual.

Supongamos que tenemos:

```text
[1, 3, 2, 4, 9, 6, 7]
```

La primera extracción retorna:

```text
1
```

Después de reparar el heap, la siguiente extracción retorna el menor de los restantes:

```text
2
```

Luego:

```text
3
```

Después:

```text
4
```

Continuando hasta vaciar:

```text
1 2 3 4 6 7 9
```

La secuencia es no decreciente.

Esto conecta naturalmente con la idea de ordenar mediante extracciones repetidas, aunque en esta semana el objetivo principal sigue siendo comprender `Priority Queue` y `BinaryHeap`.

### 41. Por qué el heap no favorece búsqueda arbitraria

Supongamos que queremos saber si existe:

```text
70
```

en un min-heap cuya raíz vale 2.

La comparación:

```text
70 > 2
```

no permite decidir entre subárbol izquierdo y derecho.

Ambos pueden contener valores mayores que 2.

Esto contrasta con BST.

En un BST:

```text
x < nodo
    descartar todo el subárbol derecho

x > nodo
    descartar todo el subárbol izquierdo
```

En un heap no existe una regla equivalente para una clave arbitraria.

Por tanto, buscar un valor cualquiera puede requerir revisar muchos elementos.

En el peor caso:

```text
búsqueda arbitraria -> O(n)
```

Esta limitación no es un defecto accidental.

Es la consecuencia de no mantener un orden global que la Priority Queue no necesita.

### 42. Comparación entre Queue, BST, AVL y BinaryHeap

Podemos resumir las estructuras recientes así:

```text
Queue
    ADT de acceso FIFO
    prioridad de salida = antigüedad

BST
    ADT de conjunto ordenado
    invariante global de búsqueda
    costo depende de h

AVL
    sigue siendo BST
    agrega balance
    mantiene h = O(log n)

BinaryHeap
    implementa Priority Queue
    forma = árbol completo
    invariante local = padre <= hijos
    mínimo en la raíz
```

Cada estructura favorece operaciones distintas porque representa y mantiene información distinta.

### 43. Representación, invariante y costo

La Semana 7 ofrece un ejemplo especialmente claro de la relación entre diseño y complejidad.

#### Representación

```text
Integer[] a
int n
```

El prefijo lógico representa un árbol completo.

#### Invariante de forma

```text
los nodos ocupan exactamente a[0..n-1]
```

No existen huecos lógicos internos.

#### Invariante de prioridad

```text
a[parent(i)] <= a[i]
```

para todo `i > 0`.

#### Consecuencias

```text
mínimo en a[0]
altura O(log n)
```

#### Algoritmos de reparación

```text
bubbleUp
    camino hacia arriba

trickleDown
    camino hacia abajo
```

#### Costos

```text
size()        -> O(1)
peek()        -> O(1)
add(x)        -> O(log n)
remove()      -> O(log n)
bubbleUp      -> O(log n)
trickleDown   -> O(log n)
heapify       -> O(n)
```

La complejidad no aparece por casualidad.

Es consecuencia directa de las propiedades de la representación.

### 44. Errores conceptuales frecuentes

#### Error 1: creer que un heap es un arreglo ordenado

Falso.

Solo se exige orden padre-hijo.

#### Error 2: creer que un heap es un BST

Falso.

El heap no mantiene orden global entre subárboles.

#### Error 3: insertar en una posición elegida por el valor

Eso puede romper la forma completa.

La nueva hoja debe ir al final lógico.

#### Error 4: eliminar la raíz dejando un hueco

Eso rompe la representación implícita.

El último elemento debe ocupar la raíz antes de reducir el heap lógico.

#### Error 5: hacer `trickleDown` con cualquier hijo

Debe elegirse el menor hijo válido en un min-heap.

#### Error 6: asumir que todo índice calculado representa un nodo

Debe verificarse:

```text
childIndex < n
```

#### Error 7: afirmar que `heapify` es necesariamente `O(n log n)`

Esa cota ignora que la mayoría de nodos está cerca de las hojas y realiza poco trabajo.

### 45. Conexión con la Semana 1

La Semana 7 reutiliza directamente varias ideas del arreglo dinámico.

```text
Integer[] a
int n
capacidad = a.length
estado lógico = a[0..n-1]
resize ocasional
```

Pero el significado del arreglo cambia.

En la Semana 1:

```text
el arreglo representaba directamente una secuencia
```

En la Semana 7:

```text
el arreglo representa implícitamente
un árbol binario completo
```

La misma herramienta física puede adquirir una semántica estructural diferente.

### 46. Conexión con la Semana 2

En listas enlazadas aprendimos que modificar unas pocas referencias puede ser barato si ya conocemos la región correcta.

En BinaryHeap no modificamos referencias entre nodos porque no existen nodos enlazados.

Pero conservamos una idea similar:

```text
una modificación local
puede restaurar una propiedad global
cuando sabemos dónde está la posible violación
```

`bubbleUp` y `trickleDown` trabajan solo sobre un camino, no sobre toda la estructura.

### 47. Conexión con la Semana 3

La Semana 3 separó claramente:

```text
ADT
```

de:

```text
implementación
```

Por ejemplo:

```text
Queue
    política FIFO

ArrayQueue
    una implementación concreta
```

Ahora aplicamos exactamente la misma idea:

```text
Priority Queue
    política de prioridad

BinaryHeap
    una implementación concreta
```

Esta conexión es fundamental.

No debemos confundir el comportamiento abstracto con la estructura interna que lo implementa.

### 48. Conexión con las Semanas 4 y 5

BST introdujo dos ideas que reaparecen aquí.

Primero:

```text
la estructura es jerárquica
```

Segundo:

```text
las operaciones deben preservar invariantes
```

En BST el invariante era global de orden.

En BinaryHeap el invariante es local de prioridad.

También reaparece una idea de la eliminación en BST:

```text
no basta con retirar un valor
hay que conservar una representación válida
```

En el heap, reemplazar la raíz por el último elemento es precisamente una decisión para conservar la forma completa antes de reparar prioridad.

### 49. Conexión con la Semana 6

AVL mostró que:

```text
forma
    influye en altura

altura
    influye en costo
```

BinaryHeap mantiene la misma preocupación, pero la resuelve de otra manera.

AVL:

```text
almacena alturas
calcula balance
aplica rotaciones
```

BinaryHeap:

```text
impone forma completa
usa índices del arreglo
no necesita rotaciones para controlar la altura
```

En ambos casos obtenemos recorridos verticales de longitud:

```text
O(log n)
```

pero a partir de invariantes diferentes.

### 50. BinaryHeap como implementación de Priority Queue

Podemos reunir toda la estructura en una sola cadena de razonamiento.

```text
queremos
    consultar y retirar el mínimo eficientemente

por eso definimos
    Priority Queue mínima

la implementamos con
    BinaryHeap

BinaryHeap usa
    arreglo + n

el arreglo codifica
    árbol binario completo

la forma completa garantiza
    h = O(log n)

el invariante min-heap garantiza
    mínimo en a[0]

add conserva forma con
    inserción al final

y repara con
    bubbleUp

remove conserva forma con
    último elemento en la raíz

y repara con
    trickleDown

heapify aprovecha
    hojas ya válidas
    procesamiento de abajo hacia arriba
```

Si puedes reconstruir esta cadena, entiendes el diseño de la estructura.

### 51. Qué debes poder justificar sin ejecutar el programa

Antes de usar Java deberías poder responder en papel:

```text
1. ¿Por qué el mínimo está en a[0]?

2. ¿Por qué el nuevo elemento debe entrar en a[n]?

3. ¿Por qué la posible violación después de add está hacia arriba?

4. ¿Por qué remove reemplaza la raíz con el último elemento?

5. ¿Por qué trickleDown debe elegir el menor hijo válido?

6. ¿Por qué los hijos deben comprobarse contra n?

7. ¿Por qué n/2 - 1 es el último nodo interno?

8. ¿Por qué heapify trabaja de abajo hacia arriba?

9. ¿Por qué heapify es O(n) y no necesita n inserciones?

10. ¿Por qué un heap no permite buscar una clave arbitraria como un BST?
```

Estas preguntas son más importantes que recordar una línea aislada de código.

### 52. Qué debes comprobar al ejecutar

La ejecución del programa debe servir para contrastar el razonamiento previo.

Al insertar valores debes observar que:

```text
la forma completa se conserva
el mínimo permanece en a[0]
```

Al eliminar repetidamente debes observar que:

```text
cada remove retorna el mínimo actual
```

Al construir desde un arreglo mediante `heapify` debes comprobar:

```text
el arreglo final puede no estar ordenado
pero cumple padre <= hijos
```

La ejecución es evidencia útil, pero no sustituye la explicación del invariante.

### 53. Limitaciones deliberadas del BinaryHeap

El `BinaryHeap` estudiado esta semana está diseñado para un conjunto concreto de operaciones.

Favorece:

```text
consultar mínimo
insertar
retirar mínimo
construir desde un arreglo
```

No está diseñado para favorecer directamente:

```text
buscar cualquier clave
obtener sucesor de una clave
obtener predecesor de una clave
recorrer todas las claves en orden sin modificar la estructura
```

Esto refuerza una lección que aparece desde la Semana 1:

```text
ninguna representación es óptima
para todas las operaciones
```

### 54. Puente hacia otras estructuras

La Semana 7 cierra el bloque inicial de árboles y prioridades con una idea general:

```text
representación
+
invariante
+
operaciones de mantenimiento
=
costos específicos
```

En la siguiente etapa del curso volveremos a cambiar el problema.

Ya no preguntaremos principalmente:

```text
¿cuál es el mínimo?
```

ni:

```text
¿por qué rama ordenada debo descender?
```

La nueva pregunta será más cercana a:

```text
¿podemos localizar una clave
sin recorrer una secuencia
y sin seguir la altura de un árbol?
```

Esto conduce a estructuras basadas en **hashing**.

El patrón de razonamiento será el mismo.

```text
qué operaciones queremos
qué representación escogemos
qué invariantes necesitamos
qué costos obtenemos
```

La estructura cambia.

La forma de pensar permanece.

### 55. Síntesis de la Semana 7

Una Priority Queue mínima mantiene un conjunto de elementos donde el menor tiene la máxima prioridad de salida.

`BinaryHeap` implementa ese ADT usando un arreglo cuyo prefijo lógico representa un árbol binario completo.

La forma completa permite calcular relaciones jerárquicas con:

```text
left(i)   = 2*i + 1
right(i)  = 2*i + 2
parent(i) = (i - 1) / 2
```

y garantiza:

```text
h = O(log n)
```

El invariante:

```text
a[parent(i)] <= a[i]
```

garantiza que:

```text
mínimo = a[0]
```

Por ello:

```text
peek -> O(1)
```

La inserción añade al final para preservar la forma y usa `bubbleUp` para reparar prioridad.

```text
add -> O(log n)
```

La eliminación del mínimo reemplaza la raíz por el último elemento para preservar la forma y usa `trickleDown` para reparar prioridad.

```text
remove -> O(log n)
```

Cuando todos los valores ya están disponibles en un arreglo, `heapify` aprovecha que las hojas son heaps válidos y procesa nodos internos de abajo hacia arriba.

```text
heapify -> O(n)
```

Esto mejora la construcción mediante inserciones repetidas:

```text
n veces add -> O(n log n)
```

Las extracciones repetidas producen valores en orden no decreciente, pero el arreglo interno del heap no necesita estar totalmente ordenado.

El heap tampoco es un BST y no favorece la búsqueda de una clave arbitraria.
