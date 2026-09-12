### Lectura: BinaryHeap, cola de prioridad y mantenimiento eficiente del mínimo

Esta lectura consolida y amplía las ideas trabajadas en la Semana 7 de CC232. Durante las seis semanas anteriores hemos cambiado de representación o de invariante cada vez que cambió el tipo de operación que queríamos favorecer. En la Semana 1, un arreglo dinámico (arreglo + tamaño lógico + capacidad) permitió acceso directo por índice y nos obligó a distinguir estado lógico, almacenamiento físico y redimensionamiento. En la Semana 2, los nodos enlazados hicieron barata la modificación local cuando ya conocíamos las referencias apropiadas, aunque localizar una posición dejó de ser directo. En la Semana 3, Stack, Queue y Deque plantearon la pregunta de qué operaciones permite el ADT sobre la secuencia.

En las Semanas 4 y 5, el invariante `subárbol izquierdo < nodo < subárbol derecho` permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones. En la Semana 6, AVL añadió información de altura y un invariante de balance para impedir que el BST degenerara en una cadena.

La Semana 7 cambia nuevamente la pregunta: ¿qué ocurre si no necesitamos buscar cualquier clave eficientemente, sino mantener disponible el elemento mínimo? Esta pregunta conduce al ADT **Priority Queue** y a una de sus implementaciones clásicas, **BinaryHeap**.

La idea central de la semana puede anticiparse así: `Priority Queue` define qué comportamiento queremos; `BinaryHeap` representa el estado mediante un arreglo que codifica un árbol binario completo; la forma completa controla la altura; el invariante min-heap controla la prioridad; `bubbleUp` y `trickleDown` restauran el invariante después de modificar, y `heapify` construye eficientemente un heap desde un arreglo. El objetivo no es memorizar las fórmulas `2*i + 1`, `2*i + 2` y `(i - 1)/2`, ni repetir mecánicamente `bubbleUp`, `trickleDown` y `heapify`, sino continuar la misma cadena conceptual de siempre, qué **ADT** queremos ofrecer, qué **representación** almacena el estado, qué **invariante** debe permanecer verdadero, qué **algoritmo** modifica y repara esa representación, y cuánto trabajo exige, su **complejidad**.

### 1. El nuevo problema: mantener el elemento mínimo

Considera una aplicación que recibe trabajos con una prioridad entera, donde menor número significa mayor prioridad. Si en cada instante necesitamos atender el trabajo más urgente, queremos responder repetidamente cuál es el menor valor actual. Almacenar los valores sin ningún orden particular hace que encontrar el mínimo exija revisar todos los elementos, **O(n)** en el peor caso. Mantener el arreglo completamente ordenado hace que consultar el mínimo sea inmediato, pero insertar un nuevo valor puede requerir desplazar muchos elementos. Aparece un compromiso: estructura sin ordenar (inserción sencilla, mínimo costoso) frente a estructura totalmente ordenada (mínimo sencillo, inserción potencialmente costosa). La cola de prioridad intenta conservar únicamente el orden necesario para que el elemento extremo permanezca disponible eficientemente.

### 2. ADT Priority Queue

Una **Priority Queue** es un tipo abstracto de dato en el que los elementos se atienden según una prioridad; en esta semana usaremos una cola de prioridad mínima, con las operaciones `boolean add(int x)`, `Integer peek()`, `int remove()` e `int size()`. `add(x)` incorpora un nuevo elemento sin exigir que quede en una posición específica visible para el usuario, solo que la estructura siga representando correctamente la Priority Queue después de insertarlo. `peek()` consulta el mínimo actual sin retirarlo; si la estructura mantiene el mínimo en una posición conocida, esta consulta puede ser muy barata. `remove()` retira y retorna el mínimo actual, modificando el estado y conservando los invariantes. `size()` retorna la cantidad de elementos lógicos almacenados.

La diferencia entre ADT e implementación sigue siendo fundamental: `Priority Queue` es el comportamiento que observamos, `BinaryHeap` es una forma concreta de implementarlo. Podrían existir otras implementaciones; esta semana estudiamos `BinaryHeap` porque combina una representación compacta con operaciones eficientes.

### 3. Priority Queue no es Queue, ni BST, ni AVL

En la Semana 3, `Queue` seguía una política FIFO: si hacemos `add(8); add(3); add(6);`, el primero en salir es 8, porque fue el primero en llegar. En una Priority Queue mínima, los mismos valores producirían primero 3, porque es el menor. El criterio de salida es distinto, orden de llegada frente a prioridad, y la semejanza de algunos nombres de operaciones no debe ocultar esa diferencia semántica.

Un BST permite decidir por qué rama continuar gracias a un invariante global de orden (todas las claves de la izquierda menores, todas las de la derecha mayores); AVL mantiene ese mismo invariante y añade balance. Ambos son apropiados cuando queremos buscar una clave arbitraria, insertar, eliminar una clave específica o recorrer en orden. Un heap persigue un objetivo diferente, mantener disponible el mínimo, y no necesita ordenar completamente los subárboles como un BST. Esta diferencia será importante durante toda la semana.

### 4. Dos decisiones independientes: forma y prioridad

Un `BinaryHeap` combina dos propiedades distintas que no debemos mezclar. La primera controla la **forma**, un árbol binario completo. La segunda controla la **prioridad**, padre ≤ hijos. Podemos tener un árbol completo que no sea heap porque sus valores violan el orden padre-hijo, o imaginar un árbol que cumpla padre-hijo pero con una forma que no corresponda al `BinaryHeap` basado en arreglo. La estructura correcta necesita ambas propiedades simultáneamente: **forma completa + invariante min-heap = BinaryHeap válido**.

### 5. Árbol binario completo

Un **árbol binario completo** llena sus niveles de arriba hacia abajo y, en el último nivel, de izquierda a derecha. Por ejemplo,

```text
     2
   /   \
  5     4
 / \   / \
9   7 8   6
```

es completo. También lo es

```text
     2
   /   \
  5     4
 / \   /
9   7 8
```

donde el último nivel todavía no está lleno, pero sus posiciones existentes aparecen desde la izquierda. En cambio,

```text
   2
 /   \
5     4
 \   /
  7 8
```

no representa la forma completa que necesitamos, porque aparece un hueco antes de una posición ocupada.

En un árbol binario completo, los niveles contienen aproximadamente 1, 2, 4, 8, 16... nodos; la capacidad crece exponencialmente con el número de niveles, así que para almacenar `n` nodos necesitamos una cantidad de niveles proporcional a `log n`, es decir, `h = O(log n)`. Esta garantía no depende del orden de inserción de las claves, es una consecuencia directa de la forma completa. Aquí aparece una diferencia importante con AVL: AVL usa representación enlazada, forma no necesariamente completa, y controla la altura con balance y rotaciones; `BinaryHeap` usa representación implícita en arreglo, impone forma completa, y obtiene altura O(log n) directamente de esa forma.

### 6. Del árbol completo al arreglo

La forma completa permite representar el árbol sin almacenar explícitamente `left`, `right` ni `parent`. Considera de nuevo

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

La raíz está en `a[0]`. Los hijos de la raíz están en `a[1]` y `a[2]`. Los hijos de `a[1]` están en `a[3]` y `a[4]`. Los hijos de `a[2]` están en `a[5]` y `a[6]`. La posición física del arreglo también codifica la posición lógica dentro del árbol.

Igual que en la Semana 1, distinguimos tamaño y capacidad. Supongamos:

```text
índice      0   1   2   3   4   5   6   7   8   9
           +---+---+---+---+---+---+---+---+---+---+
a          | 2 | 5 | 4 | 9 | 7 | 8 | 6 |   |   |   |
           +---+---+---+---+---+---+---+---+---+---+

n = 7
a.length = 10
```

El estado lógico del heap está únicamente en `a[0..n-1]`, es decir, `a[0..6]`. Las posiciones `a[7]`, `a[8]` y `a[9]` pertenecen al almacenamiento físico, pero todavía no representan nodos del heap. La representación sigue necesitando `0 <= n <= a.length`.

### 7. Relación árbol-arreglo

Cuando la raíz se almacena en el índice 0, las relaciones jerárquicas pueden calcularse aritméticamente: `left(i) = 2*i + 1`, `right(i) = 2*i + 2`, y para un nodo distinto de la raíz, `parent(i) = (i - 1) / 2` con división entera. Por ejemplo, si `i = 2`, `left(2) = 5`, `right(2) = 6`, `parent(2) = 0`. Si `i = 5`, `left(5) = 11`, `right(5) = 12`, `parent(5) = 2`. Pero calcular `left(5) = 11` no significa que el hijo exista; para que exista lógicamente debe cumplirse `11 < n`. Índice calculable no implica nodo existente.

No conviene memorizar estas expresiones sin entender su origen. Los índices por nivel son:

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

Para un nodo `i`, las posiciones del nivel siguiente que corresponden a sus dos hijos aparecen consecutivamente. Ese patrón produce `hijo izquierdo = 2*i + 1`, `hijo derecho = 2*i + 2`; la fórmula del padre simplemente invierte esa relación.

### 8. El invariante min-heap

La forma completa todavía no dice nada sobre las prioridades; necesitamos un segundo invariante. Para todo índice lógico `i > 0`, `a[parent(i)] <= a[i]`, es decir, cada padre es menor o igual que cada hijo existente. Por ejemplo,

```text
     2
   /   \
  5     4
 / \   / \
9   7 8   6
```

cumple `2<=5`, `2<=4`, `5<=9`, `5<=7`, `4<=8`, `4<=6`, así que es un min-heap válido.

### 9. Por qué el mínimo está en a[0]

La raíz corresponde a `a[0]`. Tomemos cualquier nodo `u`: por el invariante, `parent(u) <= u`, y también `parent(parent(u)) <= parent(u)`. Repitiendo el argumento hasta llegar a la raíz obtenemos raíz ≤ ... ≤ padre ≤ nodo, así que la raíz es menor o igual que cualquier nodo alcanzable, y el mínimo global es `a[0]`. Esta propiedad explica directamente por qué `peek() -> O(1)`: `peek` no busca el mínimo, el trabajo para mantenerlo accesible ya fue realizado por las operaciones que modificaron la estructura.

### 10. Un heap no es un arreglo ordenado ni un BST

Considera `[2, 5, 4, 12, 9, 7, 6]`: no está ordenado de menor a mayor (`5 > 4`, `12 > 9`), pero puede ser un min-heap válido, porque solo debemos comprobar relaciones entre padres e hijos, no `a[0] <= a[1] <= a[2] <= ...`. Esta diferencia evita mantener más orden del que necesita la Priority Queue.

Tampoco es un BST. Un heap como

```text
     2
   /   \
  8     4
 / \   / \
9  10 7   6
```

puede satisfacer padre ≤ hijos, pero no es un BST:

```text
8 está en el subárbol izquierdo de 2,
pero 8 > 2;
por tanto no puede ser un BST.
```

La regla de un heap es local (padre ≤ hijos); la regla de un BST es global (todo el subárbol izquierdo menor que el nodo, todo el subárbol derecho mayor). Son invariantes diferentes para ADT diferentes.

El invariante usa `<=` y no `<`, así que un estado como

```text
   2
 /   \
2     5
```

es válido: el padre 2 es menor o igual que el hijo 2. Nuestra Priority Queue puede almacenar varias entradas con la misma prioridad sin necesitar buscar primero si el valor ya existe, a diferencia del BST y AVL didácticos de semanas anteriores, donde trabajamos con claves únicas.

### 11. Inserción: primero preservar la forma

Supongamos que el heap lógico es `[4, 7, 6, 12, 9, 10, 8]` y queremos insertar 2. La forma del árbol completo determina dónde debe aparecer el nuevo nodo, no elegimos la posición según el valor; la nueva clave se escribe en la primera posición libre, `a[n]`. El resultado es `[4, 7, 6, 12, 9, 10, 8, 2]`; la forma completa se conserva automáticamente, pero ahora puede haberse roto el invariante min-heap.

Antes de insertar, todas las relaciones padre-hijo eran válidas; la única relación nueva es entre la nueva hoja y su padre. En el ejemplo, `2 < 12` viola padre ≤ hijo. No necesitamos revisar todo el árbol, la única posible violación nueva comienza en el camino entre la nueva hoja y la raíz. Esta observación conduce a `bubbleUp`.

### 12. bubbleUp, reparar hacia arriba

Partimos de `[4, 7, 6, 12, 9, 10, 8, 2]`. La nueva clave 2 está en el índice 7. Su padre está en `parent(7) = 3`, y el valor del padre es 12. Como `2 < 12`, intercambiamos:

```text
[4, 7, 6, 2, 9, 10, 8, 12]
```

Ahora 2 está en el índice 3. Su padre está en el índice 1 y contiene 7. Como `2 < 7`, intercambiamos:

```text
[4, 2, 6, 7, 9, 10, 8, 12]
```

Ahora 2 está en el índice 1. Su padre está en la raíz y contiene 4. Como `2 < 4`, intercambiamos:

```text
[2, 4, 6, 7, 9, 10, 8, 12]
```

El elemento llegó a la raíz. La reparación termina.

```java
private void bubbleUp(int i) {
    while (i > 0 && a[i] < a[parent(i)]) {
        int p = parent(i);
        swap(i, p);
        i = p;
    }
}
```

La condición `i > 0` indica que el nodo todavía tiene padre; `a[i] < a[parent(i)]` indica que existe una violación; después de intercambiar, `i = p` continúa desde el nivel superior. La igualdad no exige intercambio porque padre == hijo sigue cumpliendo el min-heap.

Antes de insertar, todo el heap es válido; después de colocar la hoja, solo puede fallar la relación nueva hoja-padre. Después de un intercambio, la relación que acabamos de atravesar queda reparada, y si aparece otra violación, solo puede estar un nivel más arriba. Así, la violación se desplaza por un único camino hasta que el elemento llega a la raíz o su padre ya es menor o igual que él; al terminar, todas las relaciones padre-hijo vuelven a ser válidas.

### 13. Complejidad de add y de peek

Insertar físicamente al final utiliza el mismo principio del arreglo dinámico de la Semana 1: si existe capacidad, escribir en `a[n]` y actualizar `n` es trabajo constante, con un redimensionamiento ocasional analizado amortizadamente. Después, cada intercambio de `bubbleUp` sube exactamente un nivel; como `h = O(log n)`, `bubbleUp -> O(log n)` y `add -> O(log n)`, la cota logarítmica domina el trabajo de reparación.

`peek` solo necesita consultar `a[0]`, así que `peek -> O(1)`. No es contradictorio que `add` sea más costoso: la estructura invierte trabajo durante las modificaciones para conservar el mínimo en una posición conocida. `add` paga el costo de reparar; `peek` aprovecha el invariante ya mantenido.

### 14. Eliminar el mínimo: un problema de forma y prioridad

En un min-heap, el elemento que `remove()` debe retirar está en `a[0]`. Si simplemente hacemos `a[0] = null`, quedaría un hueco en la raíz, y el estado lógico ya no correspondería a un árbol completo almacenado en el prefijo del arreglo. La eliminación debe preservar primero la forma.

En un árbol completo almacenado en un arreglo, el último nodo lógico está en `a[n-1]`, y eliminar esa posición no deja huecos en el prefijo. Por ello, para retirar la raíz utilizamos la estrategia: guardar el mínimo, mover el último valor a la raíz, disminuir `n`, limpiar la posición liberada, y reparar prioridad desde la raíz. Con `[2, 5, 4, 9, 7, 8, 6, 12, 11]`, guardamos 2 y llevamos 11 a la raíz:

```text
[11, 5, 4, 9, 7, 8, 6, 12]
```

La forma completa está preservada; el problema ahora es el invariante de prioridad.

Antes de eliminar, el heap era válido, y todos los subárboles por debajo de la raíz siguen conteniendo las mismas relaciones padre-hijo. La única novedad es que el antiguo último elemento ocupa ahora la raíz, así que la posible violación comienza allí y puede propagarse hacia abajo. Esta observación conduce a `trickleDown`.

### 15. Por qué debemos elegir el menor hijo

En `[11, 5, 4, 9, 7, 8, 6, 12]`, la raíz 11 tiene hijos 5 y 4, ambos menores que 11. No basta con escoger cualquier hijo, debemos escoger el menor, 4. Después del intercambio:

```text
[4, 5, 11, 9, 7, 8, 6, 12]
```

Si hubiéramos intercambiado con 5, la raíz quedaría 5 mientras existiría un hijo 4, y la violación continuaría inmediatamente en la raíz. La selección del menor hijo garantiza que el valor que sube sea compatible con ambos hijos del nivel actual.

### 16. trickleDown, reparar hacia abajo

Continuando desde `[4, 5, 11, 9, 7, 8, 6, 12]`, el valor 11 está ahora en el índice 2. Sus hijos válidos son 8 y 6; el menor es 6. Como `6 < 11`, intercambiamos:

```text
[4, 5, 6, 9, 7, 8, 11, 12]
```

Ahora 11 está en el índice 6. Si no tiene hijos válidos, terminamos. El heap vuelve a satisfacer el invariante.

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

Las condiciones `l < n` y `r < n` comprueban que los hijos existan lógicamente. El hijo derecho se compara con `a[smallest]` y no siempre con `a[i]`, porque el hijo izquierdo puede haberse convertido ya en el mejor candidato.

Antes de mover el último elemento a la raíz, todo el heap era válido; después del reemplazo, solo la nueva raíz local puede ser demasiado grande respecto de sus hijos. Seleccionamos el menor entre el nodo actual y sus hijos válidos; si el nodo actual ya es el menor, terminamos, y si un hijo es menor, intercambiamos con el menor hijo. La posible violación se desplaza un nivel hacia abajo, y como los demás subárboles ya eran heaps válidos, no necesitamos explorar otras ramas.

### 17. Complejidad de remove y simetría con add

Cada intercambio de `trickleDown` baja exactamente un nivel, y la altura del árbol completo es O(log n), así que `trickleDown -> O(log n)` y `remove -> O(log n)`. La operación preserva la forma en tiempo constante y después paga como máximo un recorrido vertical logarítmico.

Las dos operaciones principales tienen una estructura muy parecida: `add` preserva la forma insertando al final, la posible violación es entre la nueva hoja y su padre, y repara con `bubbleUp`; `remove` preserva la forma reemplazando la raíz por el último elemento y retirando el último, la posible violación es entre la nueva raíz y sus hijos, y repara con `trickleDown`. En ambos casos, primero preservamos representación, luego restauramos invariante.

### 18. Construir un heap desde un arreglo arbitrario

Supongamos que recibimos `[14, 9, 7, 12, 5, 10, 3, 8]`. Una estrategia correcta consiste en empezar con un heap vacío y ejecutar `add` para cada elemento; cada inserción cuesta como máximo O(log n), así que para `n` elementos obtenemos **O(n log n)**. Esta estrategia funciona, pero no aprovecha que el arreglo ya contiene los `n` elementos en posiciones contiguas, es decir, la forma de árbol completo ya está presente; solo falta reparar el invariante de prioridad.

### 19. heapify, construir desde abajo hacia arriba

`heapify` transforma un arreglo arbitrario en un heap válido. La idea central es que las hojas ya son heaps válidos, porque una hoja no tiene hijos y por tanto no puede violar padre ≤ hijos dentro de su propio subárbol. No necesitamos procesar las hojas; debemos empezar por el último nodo que puede tener al menos un hijo.

En un arreglo de tamaño `n`, los índices `n/2, n/2+1, ..., n-1` corresponden a hojas, así que el último nodo interno está en `n/2 - 1` con división entera. Por ejemplo, si `n = 8`, `n/2 - 1 = 3`; los índices 4 a 7 son hojas, y los índices 0 a 3 pueden tener hijos.

```java
private void heapify() {
    for (int i = n / 2 - 1; i >= 0; i--) {
        trickleDown(i);
    }
}
```

El orden descendente es esencial: cuando procesamos un nodo `i`, sus hijos pertenecen a niveles inferiores y ya fueron procesados, así que `trickleDown(i)` puede asumir que los subárboles hijos ya son heaps válidos y solo necesita reparar la raíz local. Esta es una construcción de abajo hacia arriba.

### 20. Traza de heapify

Considera `[9, 4, 7, 1, 3, 6, 2]`, con `n = 7` y `n/2 - 1 = 2`.

Empezamos en `i = 2`. El valor 7 tiene hijos 6 y 2; el menor hijo es 2. Después de reparar:

```text
[9, 4, 2, 1, 3, 6, 7]
```

Ahora `i = 1`. El valor 4 tiene hijos 1 y 3; el menor hijo es 1. Después:

```text
[9, 1, 2, 4, 3, 6, 7]
```

Finalmente `i = 0`. La raíz 9 tiene hijos 1 y 2; el menor hijo es 1. Después de bajar 9 y continuar reparando:

```text
[1, 3, 2, 4, 9, 6, 7]
```

El arreglo final no está totalmente ordenado, pero sí es un min-heap:

```text
     1
   /   \
  3     2
 / \   / \
4   9 6   7
```

### 21. Por qué heapify no cuesta O(n log n)

Una llamada individual a `trickleDown` puede costar O(log n); multiplicar aproximadamente `n/2` nodos internos por O(log n) da **O(n log n)**, una cota superior válida pero que no describe ajustadamente el trabajo real. La mayoría de los nodos está muy cerca de las hojas:

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

El trabajo total tiene la forma aproximada `n/4 * 1 + n/8 * 2 + n/16 * 3 + ...`, que factorizando `n` da `n * (1/4 + 2/8 + 3/16 + ...)`; la serie entre paréntesis está acotada por una constante, así que **`heapify -> O(n)`**. La idea importante es que hay muchos nodos baratos y pocos nodos caros.

Tenemos entonces dos estrategias correctas para construir un heap desde `n` valores: insertar uno por uno con `add` desde un heap vacío, en O(n log n), o copiar los valores preservando directamente la forma completa y ejecutar `heapify` de abajo hacia arriba, en O(n). Ambas pueden producir heaps válidos diferentes, y eso no es un problema, el ADT no exige una única representación interna, solo que la estructura satisfaga sus invariantes.

### 22. Extracciones repetidas y por qué el heap no favorece búsqueda arbitraria

En un min-heap, `remove()` retorna siempre el mínimo actual. Partiendo de `[1, 3, 2, 4, 9, 6, 7]`, las extracciones sucesivas producen `1, 2, 3, 4, 6, 7, 9`, una secuencia no decreciente. Esto conecta naturalmente con la idea de ordenar mediante extracciones repetidas, aunque en esta semana el objetivo principal sigue siendo comprender `Priority Queue` y `BinaryHeap`.

Supongamos que queremos saber si existe el valor 70 en un min-heap cuya raíz vale 2. La comparación `70 > 2` no permite decidir entre subárbol izquierdo y derecho, porque ambos pueden contener valores mayores que 2; esto contrasta con un BST, donde `x < nodo` descarta todo el subárbol derecho y `x > nodo` descarta todo el izquierdo. En un heap no existe una regla equivalente para una clave arbitraria, así que buscar un valor cualquiera puede requerir revisar muchos elementos, **O(n)** en el peor caso. Esta limitación no es un defecto accidental, es la consecuencia de no mantener un orden global que la Priority Queue no necesita.

### 23. Representación, invariante y tabla de costos

Podemos reunir la representación de la semana: `Integer[] a` e `int n`, donde el prefijo lógico representa un árbol completo. El invariante de forma exige que los nodos ocupen exactamente `a[0..n-1]`, sin huecos lógicos internos. El invariante de prioridad exige `a[parent(i)] <= a[i]` para todo `i > 0`. De ahí se derivan el mínimo en `a[0]` y la altura O(log n).

| Operación | Qué hace | Costo |
|---|---|---|
| `size()` | retorna `n` | O(1) |
| `peek()` | retorna `a[0]` | O(1) |
| `add(x)` | inserta en `a[n]` y ejecuta `bubbleUp` | O(log n) |
| `remove()` | mueve `a[n-1]` a la raíz y ejecuta `trickleDown` | O(log n) |
| `bubbleUp(i)` | camino hacia arriba, un nivel por intercambio | O(log n) |
| `trickleDown(i)` | camino hacia abajo, un nivel por intercambio | O(log n) |
| `heapify()` | repara desde `n/2 - 1` hasta la raíz | O(n) |

La complejidad no aparece por casualidad, es consecuencia directa de las propiedades de la representación.

### 24. Errores conceptuales frecuentes

Creer que un heap es un arreglo ordenado es falso, solo se exige orden padre-hijo. Creer que un heap es un BST también es falso, el heap no mantiene orden global entre subárboles. Insertar en una posición elegida por el valor puede romper la forma completa, la nueva hoja debe ir al final lógico. Eliminar la raíz dejando un hueco rompe la representación implícita, el último elemento debe ocupar la raíz antes de reducir el heap lógico. Hacer `trickleDown` con cualquier hijo en vez del menor hijo válido rompe la reparación en un min-heap. Asumir que todo índice calculado representa un nodo es un error, siempre debe verificarse `childIndex < n`. Y afirmar que `heapify` es necesariamente O(n log n) ignora que la mayoría de nodos está cerca de las hojas y realiza poco trabajo.

### 25. Conexión con las semanas anteriores

La Semana 7 reutiliza directamente varias ideas del arreglo dinámico de la Semana 1 (`Integer[] a`, `int n`, capacidad, estado lógico `a[0..n-1]`, resize ocasional), pero el significado del arreglo cambia: antes representaba directamente una secuencia, ahora representa implícitamente un árbol binario completo. La misma herramienta física adquiere una semántica estructural diferente.

De la Semana 2 reaparece la idea de que una modificación local puede restaurar una propiedad global cuando sabemos dónde está la posible violación; aquí no hay referencias entre nodos, pero `bubbleUp` y `trickleDown` trabajan solo sobre un camino, no sobre toda la estructura, igual que una lista enlazada modifica solo las referencias necesarias.

De la Semana 3 reaparece la separación entre ADT e implementación: así como `Queue` es la política FIFO y `ArrayQueue` una implementación concreta, `Priority Queue` es la política de prioridad y `BinaryHeap` una implementación concreta. No debemos confundir el comportamiento abstracto con la estructura interna que lo implementa.

De las Semanas 4 y 5 reaparece que la estructura es jerárquica y que las operaciones deben preservar invariantes, aunque en BST el invariante era global de orden y en `BinaryHeap` es local de prioridad; también reaparece la idea de que no basta con retirar un valor, hay que conservar una representación válida, tal como reemplazar la raíz por el último elemento conserva la forma completa antes de reparar prioridad.

De la Semana 6 reaparece que la forma influye en la altura y la altura influye en el costo, pero resuelto de otra manera: AVL almacena alturas, calcula balance y aplica rotaciones; `BinaryHeap` impone forma completa y usa índices del arreglo, sin necesitar rotaciones para controlar la altura. En ambos casos obtenemos recorridos verticales de longitud O(log n), pero a partir de invariantes diferentes.

### 26. BinaryHeap como implementación de Priority Queue

Podemos reunir toda la estructura en una sola cadena de razonamiento: queremos consultar y retirar el mínimo eficientemente, por eso definimos una Priority Queue mínima, la implementamos con `BinaryHeap`, que usa arreglo + `n`; el arreglo codifica un árbol binario completo, cuya forma completa garantiza `h = O(log n)`, y cuyo invariante min-heap garantiza el mínimo en `a[0]`. `add` conserva la forma con inserción al final y repara con `bubbleUp`; `remove` conserva la forma con el último elemento en la raíz y repara con `trickleDown`; `heapify` aprovecha que las hojas ya son válidas y procesa de abajo hacia arriba. Si puedes reconstruir esta cadena, entiendes el diseño de la estructura.

Antes de usar Java deberías poder responder en papel por qué el mínimo está en `a[0]`, por qué el nuevo elemento debe entrar en `a[n]`, por qué la posible violación después de `add` está hacia arriba, por qué `remove` reemplaza la raíz con el último elemento, por qué `trickleDown` debe elegir el menor hijo válido, por qué los hijos deben comprobarse contra `n`, por qué `n/2 - 1` es el último nodo interno, por qué `heapify` trabaja de abajo hacia arriba y por qué es O(n) en vez de necesitar `n` inserciones, y por qué un heap no permite buscar una clave arbitraria como un BST. Estas preguntas son más importantes que recordar una línea aislada de código.

### 27. Limitaciones deliberadas y puente hacia hashing

El `BinaryHeap` estudiado esta semana favorece consultar el mínimo, insertar, retirar el mínimo y construir desde un arreglo. No está diseñado para favorecer directamente buscar cualquier clave, obtener el sucesor o predecesor de una clave, ni recorrer todas las claves en orden sin modificar la estructura. Esto refuerza una lección que aparece desde la Semana 1, ninguna representación es óptima para todas las operaciones.

La Semana 7 cierra el bloque inicial de árboles y prioridades con una idea general, representación + invariante + operaciones de mantenimiento producen costos específicos. En la siguiente etapa del curso cambiaremos nuevamente el problema: ya no preguntaremos cuál es el mínimo ni por qué rama ordenada descender, sino si podemos localizar una clave sin recorrer una secuencia y sin seguir la altura de un árbol. Esto conduce a estructuras basadas en **hashing**. El patrón de razonamiento será el mismo, qué operaciones queremos, qué representación escogemos, qué invariantes necesitamos, qué costos obtenemos; la estructura cambia, la forma de pensar permanece.

### 28. Síntesis

Una Priority Queue mínima mantiene un conjunto de elementos donde el menor tiene la máxima prioridad de salida. `BinaryHeap` implementa ese ADT usando un arreglo cuyo prefijo lógico representa un árbol binario completo. La forma completa permite calcular relaciones jerárquicas con `left(i) = 2*i + 1`, `right(i) = 2*i + 2`, `parent(i) = (i-1)/2`, y garantiza `h = O(log n)`. El invariante `a[parent(i)] <= a[i]` garantiza que el mínimo es `a[0]`, por lo que `peek -> O(1)`.

La inserción añade al final para preservar la forma y usa `bubbleUp` para reparar prioridad, con `add -> O(log n)`. La eliminación del mínimo reemplaza la raíz por el último elemento para preservar la forma y usa `trickleDown` para reparar prioridad, con `remove -> O(log n)`. Cuando todos los valores ya están disponibles en un arreglo, `heapify` aprovecha que las hojas son heaps válidos y procesa nodos internos de abajo hacia arriba, en `O(n)`, mejorando la construcción mediante inserciones repetidas (`O(n log n)`).

Las extracciones repetidas producen valores en orden no decreciente, pero el arreglo interno del heap no necesita estar totalmente ordenado. El heap tampoco es un BST y no favorece la búsqueda de una clave arbitraria.
