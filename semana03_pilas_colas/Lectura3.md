### Lectura: pilas, colas y restricciones de acceso sobre secuencias

Esta lectura consolida y amplía las ideas trabajadas en la Semana 3 de CC232.

En las dos primeras semanas estudiamos dos formas de representar una secuencia. En la Semana 1 utilizamos un arreglo dinámico. Esa representación ofrecía acceso directo por índice y requería distinguir tamaño, capacidad y redimensionamiento. En la Semana 2 utilizamos nodos enlazados. Esa representación permitía modificaciones locales eficientes cuando ya disponíamos de las referencias apropiadas, aunque el acceso por índice dejaba de ser directo.

La Semana 3 introduce una pregunta distinta: ¿qué ocurre si no necesitamos todas las operaciones de una secuencia general? Una **pila**, una **cola** y un **deque** no se distinguen principalmente por el tipo de dato que almacenan. Se distinguen por las operaciones que permiten realizar sobre los extremos de la secuencia.

La idea central de esta semana puede anticiparse así. Una secuencia general admite muchas posiciones posibles de acceso. Un **Stack** inserta y elimina por un mismo extremo. Una **Queue** inserta por un extremo y elimina por el extremo opuesto. Un **Deque** inserta y elimina por ambos extremos. Estas restricciones producen tipos abstractos de datos más simples y permiten escoger representaciones especialmente adecuadas para sus operaciones.

El objetivo no es memorizar métodos aislados, sino comprender la relación entre el **ADT** (qué operaciones están permitidas), la **representación** (cómo se almacena el estado), el **invariante** (qué debe permanecer verdadero), el **algoritmo** (cómo se modifica el estado) y la **complejidad** (cuánto trabajo exige la operación).

### 1. De una secuencia general a un ADT restringido

Una lista general permite pensar en operaciones como `get(i)`, `add(i, x)` y `remove(i)`. El índice `i` puede referirse a muchas posiciones diferentes. Una pila o una cola restringen deliberadamente esa libertad. Esa restricción no es una desventaja accidental: es parte de la definición del ADT.

#### El ADT describe comportamiento

Un **tipo abstracto de dato** define qué operaciones ofrece y qué comportamiento deben observar los usuarios. Por ejemplo, una pila puede ofrecer:

```java
void push(char x)
char pop()
char peek()
boolean isEmpty()
int size()
```

La interfaz conceptual no exige que la implementación utilice un arreglo o una lista enlazada; podríamos implementar una pila con un arreglo dinámico o con una lista simplemente enlazada, y el comportamiento observable debe seguir siendo el mismo.

Esta separación es importante: `Stack` describe una política LIFO, mientras que `LinkedStack` o `ArrayStack` son implementaciones concretas de esa misma política. La misma idea se aplica a una cola: `Queue` describe una política FIFO, y `ArrayQueue` es una implementación concreta.

### 2. Pila y política LIFO

Una pila permite insertar y retirar elementos por un mismo extremo. Ese extremo suele llamarse **tope**. En nuestra implementación enlazada utilizaremos `head` como referencia al tope.

La política de la pila es **LIFO** (*Last In, First Out*, último en entrar, primero en salir). Considera `push('A')`, `push('B')`, `push('C')`. Después de esas operaciones, el último elemento insertado es `C`. Por tanto `pop() -> C`, después `pop() -> B` y finalmente `pop() -> A`. La secuencia de salida es la inversa de la secuencia de inserción.

#### Una traza conceptual

Podemos representar el estado inicial como una pila vacía. Después de `push(A)`:

```text
top
 |
 v
A
```

Después de `push(B)`:

```text
top
 |
 v
B
A
```

Después de `push(C)`:

```text
top
 |
 v
C
B
A
```

Ahora `pop() -> C` deja:

```text
top
 |
 v
B
A
```

La política LIFO no depende de la implementación; es una propiedad del ADT.

### 3. Operaciones fundamentales de Stack

Las operaciones básicas que interesan esta semana son `push(x)`, `pop()`, `peek()`, `isEmpty()` y `size()`.

`push(x)` inserta un elemento en el tope; la postcondición conceptual es que el nuevo elemento pasa a ser el primero disponible para `pop()`. `pop()` retira y retorna el elemento que se encuentra en el tope; si la pila está vacía, la implementación debe definir qué ocurre, y en el código de esta semana se utiliza una excepción para representar ese caso. `peek()` consulta el elemento del tope sin retirarlo, así que después de `x = peek()` el tamaño de la pila no cambia. `isEmpty()` responde si la pila contiene cero elementos; si mantenemos explícitamente un contador `n`, podemos expresar `isEmpty() <=> n == 0`. `size()` retorna el número de elementos lógicos almacenados, y si ese número ya está registrado en `n`, `size()` tiene costo **O(1)** sin necesitar recorrer los nodos.

### 4. Una pila enlazada como especialización de la Semana 2

En la Semana 2 trabajamos con una lista simplemente enlazada, cuya representación incluía `head` y nodos con `next`. Para una pila no necesitamos una estructura general que permita operar en posiciones arbitrarias; solo necesitamos operar sobre el tope. Podemos utilizar:

```java
static class Node {
    char x;
    Node next;

    Node(char x, Node next) {
        this.x = x;
        this.next = next;
    }
}
```

y mantener `private Node head; private int n;`. La representación conceptual es:

```text
head
 |
 v
+---+------+     +---+------+     +---+------+
| C | next | --> | B | next | --> | A | null |
+---+------+     +---+------+     +---+------+

n = 3
```

`head` cumple dos funciones conceptualmente relacionadas: es a la vez la cabecera de la lista enlazada y el tope de la pila. No necesitamos `tail` para las operaciones fundamentales de esta implementación porque `push()` y `pop()` trabajan únicamente en `head`.

### 5. Invariante de LinkedStack

La pila enlazada debe conservar relaciones coherentes entre `head`, `n` y la cadena de referencias `next`. Una formulación útil del invariante es que `n == 0` implica `head == null`, y `n > 0` implica `head != null`. Además, comenzando en `head` y siguiendo `next`, deben encontrarse exactamente `n` nodos.

Por ejemplo, `head -> C -> B -> A -> null` con `n = 3` es coherente. En cambio, la misma cadena con `n = 4` no representa un estado válido.

#### Por qué no basta con que pop() retorne un valor correcto

Supongamos que una implementación retorna correctamente `C`, pero deja `n = 3` cuando la cadena ahora contiene solamente `B -> A`. La salida inmediata podría parecer correcta, pero la representación quedó inconsistente. Por eso una operación debe cumplir dos responsabilidades: producir el comportamiento observable correcto y preservar el invariante interno.

### 6. Implementar push(char x)

Considera el estado `head -> B -> A -> null` con `n = 2`. Queremos ejecutar `push(C)`. El nuevo nodo debe apuntar al antiguo `head`, y después debe convertirse en el nuevo `head`. El constructor del nodo permite expresar ambas ideas: `head = new Node(x, head);`. Después actualizamos el tamaño con `n++;`. La operación completa es:

```java
void push(char x) {
    head = new Node(x, head);
    n++;
}
```

#### Qué ocurre en la asignación

Antes de modificar `head`, la parte derecha `new Node(x, head)` utiliza la referencia antigua. Si `head -> B`, el nuevo nodo queda inicialmente como `C -> B`. Después la asignación actualiza `head`, y queda `head -> C -> B -> A -> null`. No se pierde la estructura anterior.

#### Caso vacío y complejidad

Si `head = null` y `n = 0`, entonces `new Node(x, head)` crea un nodo cuyo `next` es `null`. Por tanto, la misma implementación sirve también para la pila vacía; no necesitamos un caso especial para insertar el primer elemento.

La operación crea un nodo, modifica `head` e incrementa `n`, sin recorrer la estructura. Por tanto, `push(x)` tiene costo **O(1)**.

### 7. Implementar pop()

Considera `head -> C -> B -> A -> null` con `n = 3`. Queremos retirar `C`. Primero debemos conservar el valor que se retornará con `char x = head.x;`. Después hacemos avanzar `head` con `head = head.next;`. Finalmente reducimos `n--;` y retornamos `x`. Una implementación es:

```java
char pop() {
    if (n == 0) {
        throw new java.util.NoSuchElementException("pila vacía");
    }

    char x = head.x;
    head = head.next;
    n--;

    return x;
}
```

#### Por qué comprobar primero si está vacía

Si `n = 0` y `head = null`, la expresión `head.x` no representa una operación válida. Por eso la precondición operativa debe comprobarse antes de acceder al nodo.

#### Estado después de pop() y complejidad

Antes teníamos `head -> C -> B -> A -> null` con `n = 3`. Después, `pop()` retorna `C` y deja `head -> B -> A -> null` con `n = 2`. No se realiza recorrido, así que `pop()` tiene costo **O(1)**. La representación enlazada se ajusta especialmente bien a la pila porque el lugar de trabajo siempre está localizado por `head`.

### 8. peek(), isEmpty() y size()

Aunque el archivo de la semana utiliza principalmente `push()`, `pop()`, `isEmpty()` y `size()`, conviene comprender también `peek()` como operación del ADT. Una implementación conceptual puede ser:

```java
char peek() {
    if (n == 0) {
        throw new java.util.NoSuchElementException("pila vacía");
    }

    return head.x;
}
```

La operación no modifica `head`, `next` ni `n`, así que `peek()` tiene costo **O(1)**. De la misma forma, `boolean isEmpty() { return n == 0; }` y `int size() { return n; }` tienen costo **O(1)** porque no necesitan recorrer los nodos.

### 9. Una aplicación de Stack, invertir una secuencia

Una pila puede utilizarse cuando necesitamos recuperar elementos en orden inverso al de inserción. El archivo de la semana contiene:

```java
static String reverse(String text) {
    LinkedStack stack = new LinkedStack();

    for (char c : text.toCharArray()) {
        stack.push(c);
    }

    StringBuilder out = new StringBuilder();

    while (!stack.isEmpty()) {
        out.append(stack.pop());
    }

    return out.toString();
}
```

El algoritmo tiene dos fases: primero inserta todos los caracteres, y luego los retira todos. Para `ABC`, la primera fase produce `push(A)`, `push(B)`, `push(C)`, y la pila queda `head -> C -> B -> A -> null`. La segunda fase produce `pop() -> C`, `pop() -> B`, `pop() -> A`, y la salida es `CBA`.

#### Qué demuestra este ejemplo

El algoritmo `reverse()` no necesita conocer `Node`, `next` ni `head`; solo necesita utilizar las operaciones del ADT. Esto muestra una separación importante: el **algoritmo cliente** usa `push`, `pop`, `isEmpty`, mientras que la **implementación** administra nodos y referencias. La política LIFO explica el resultado: la inversión es una consecuencia de la disciplina de acceso de la pila.

### 10. Cola y política FIFO

Una cola restringe una secuencia de forma diferente. Los elementos se insertan por un extremo y se retiran por el extremo opuesto. La política es **FIFO** (*First In, First Out*, primero en entrar, primero en salir).

Considera `add(10)`, `add(20)`, `add(30)`. La cola lógica es:

```text
frente
 |
 v
10, 20, 30
        ^
        |
       final
```

La siguiente eliminación debe retornar 10, después 20 y luego 30. La cola conserva el orden de llegada.

### 11. Operaciones fundamentales de Queue

Las operaciones que interesan esta semana son `add(x)`, `remove()`, `peek()` y `size()`. `add(x)` inserta un nuevo elemento al final lógico de la cola. `remove()` retira el elemento situado en el frente lógico. `peek()` consulta el elemento del frente sin eliminarlo. `size()` retorna el número de elementos almacenados.

Al igual que con `Stack`, estas operaciones describen comportamiento y no obligan por sí mismas a utilizar una representación concreta. Una cola podría implementarse mediante una `SLList` con `head` y `tail`, o mediante un **arreglo circular**. En esta semana utilizaremos la segunda alternativa porque permite integrar conceptos de las Semanas 1 y 3.

### 12. El problema de una cola basada en arreglo

Supongamos `a = [10, 20, 30, 40, _]` con `n = 4`. Si interpretamos siempre que el frente está en `a[0]`, entonces eliminar 10 y mantener la secuencia contigua podría requerir mover 20 a la posición 0, 30 a la posición 1 y 40 a la posición 2, dejando `a = [20, 30, 40, _, _]`. El problema es que cada `remove()` podría realizar una cantidad de desplazamientos proporcional a `n`, así que un `remove` ingenuo desde `a[0]` cuesta **O(n)**.

Podemos evitar esos desplazamientos si dejamos de exigir que el frente lógico permanezca físicamente en `a[0]`. En lugar de mover todos los elementos, movemos la interpretación del frente. Para eso introducimos un índice `j`.

### 13. Representación de ArrayQueue

El archivo de la semana utiliza:

```java
private Integer[] a = new Integer[4];
private int j;
private int n;
```

Cada campo tiene un significado distinto: `a` es el arreglo de respaldo, `j` es el índice físico del frente lógico, y `n` es el número de elementos lógicos.

Si `a.length = 8`, `j = 2` y `n = 3`, los elementos lógicos podrían estar en `a[2]`, `a[3]`, `a[4]`. Pero cuando `j` se aproxima al final del arreglo, la secuencia puede continuar físicamente desde la posición cero. Por ejemplo, con `a.length = 8`, `j = 6` y `n = 4`, los elementos lógicos ocupan `a[6]`, `a[7]`, `a[0]`, `a[1]`. La cola es circular desde el punto de vista de la interpretación de los índices; el arreglo de Java sigue siendo un arreglo ordinario.

### 14. Índice lógico frente a índice físico

Esta distinción es central en la Semana 3. El **índice lógico** describe la posición del elemento dentro de la cola: `k = 0` es el primer elemento lógico, `k = 1` el segundo, y así hasta `k = n - 1`, el último. El **índice físico** describe la celda concreta del arreglo donde se encuentra el elemento.

Si el frente está en `j`, la posición física correspondiente al elemento lógico `k` es `(j + k) % a.length`. Por ejemplo, con `a.length = 8`, `j = 6` y `n = 4`:

```text
posición lógica    posición física

0                  6
1                  7
2                  0
3                  1
```

El orden lógico continúa aunque el almacenamiento físico llegue al final del arreglo.

### 15. El invariante circular

La relación fundamental de `ArrayQueue` puede expresarse como: el elemento lógico `k` está en `a[(j + k) % a.length]`, para `0 <= k < n`. Este invariante conecta el orden lógico con el almacenamiento físico. También necesitamos `0 <= n <= a.length`, y, cuando `n > 0`, el frente lógico está en `a[j]`.

#### Un estado válido

Supongamos `a.length = 6`, `j = 4`, `n = 4`, con el arreglo físico:

```text
índice físico

0    1    2    3    4    5
30   40   _    _    10   20
```

La vista lógica es `10, 20, 30, 40`, porque `k=0 -> 4`, `k=1 -> 5`, `k=2 -> 0`, `k=3 -> 1`. El arreglo físico parece dividido, pero la cola lógica no lo está.

### 16. La operación peek() en ArrayQueue

Si `j` señala el frente, consultar el primer elemento es directo. El archivo utiliza:

```java
Integer peek() {
    return n == 0 ? null : a[j];
}
```

Si `n == 0`, retorna `null`; en otro caso, `a[j]` es exactamente el primer elemento lógico. No existe recorrido, así que `peek()` tiene costo **O(1)**.

### 17. Insertar al final de una cola circular

El método `add(Integer x)` ya está implementado en el archivo de la semana:

```java
boolean add(Integer x) {
    if (n + 1 > a.length) {
        resize();
    }

    a[(j + n) % a.length] = x;
    n++;

    return true;
}
```

La expresión central es `(j + n) % a.length`. ¿Por qué aparece `n`? Si existen `n` elementos lógicos, sus posiciones son `0, 1, 2, ..., n - 1`, así que la siguiente posición lógica disponible es `n`, y por tanto su posición física es `(j + n) % a.length`.

Por ejemplo, sin envoltura, con `a.length = 8`, `j = 2`, `n = 3`, la nueva posición es `(2 + 3) % 8 = 5`. Con envoltura, con `a.length = 8`, `j = 6`, `n = 3`, la nueva posición es `(6 + 3) % 8 = 1`. El nuevo elemento se almacena al inicio físico del arreglo, pero al final lógico de la cola.

### 18. Avance modular del frente

Después de retirar el elemento situado en `a[j]`, el siguiente elemento lógico debe convertirse en el nuevo frente. Podemos avanzar con `j = (j + 1) % a.length;`. Si `a.length = 8` y `j = 3`, el nuevo `j` es 4. Si `a.length = 8` y `j = 7`, el nuevo `j` es 0. El módulo permite regresar al inicio físico del arreglo.

#### Por qué no desplazar los elementos

Supongamos `j = 6` y la cola lógica es `10, 20, 30`, físicamente `a[6] = 10`, `a[7] = 20`, `a[0] = 30`. Después de eliminar 10 no necesitamos transformar físicamente la estructura en `20, 30` a partir de `a[0]`. Basta hacer `j = 7`. Ahora la interpretación lógica comienza en 20. La estructura cambia el significado del índice inicial en vez de mover todos los datos.

### 19. Implementar remove()

La operación debe realizar cinco tareas conceptuales: comprobar que exista un elemento, conservar el valor del frente, liberar la posición física anterior, avanzar `j` modularmente y disminuir `n`. El archivo además permite reducir la capacidad cuando el arreglo queda demasiado vacío. Una implementación es:

```java
Integer remove() {
    if (n == 0) {
        throw new java.util.NoSuchElementException("cola vacía");
    }

    Integer x = a[j];
    a[j] = null;

    j = (j + 1) % a.length;
    n--;

    if (a.length >= 3 * n) {
        resize();
    }

    return x;
}
```

#### Qué representa cada paso

Primero, `Integer x = a[j];` conserva el elemento FIFO que debemos retornar. Después, `a[j] = null;` deja de conservar una referencia al elemento eliminado en esa celda. Luego, `j = (j + 1) % a.length;` establece un nuevo frente, y `n--;` actualiza el tamaño lógico. Finalmente puede aparecer una reducción de capacidad.

#### Complejidad sin resize()

Ignorando el redimensionamiento, `remove()` ejecuta una cantidad constante de trabajo, así que `remove()` sin `resize` tiene costo **O(1)**. Pero algunas llamadas pueden ejecutar una copia lineal, por lo que necesitaremos nuevamente análisis amortizado.

### 20. Por qué resize() es diferente en una cola circular

En la Semana 1, si los elementos válidos estaban en `a[0..n-1]`, podíamos copiar `b[k] = a[k];`. En una cola circular esa instrucción puede ser incorrecta.

Supongamos `a.length = 8`, `j = 6`, `n = 4`, con valores lógicos `10, 20, 30, 40` y representación física:

```text
índice

0    1    2    3    4    5    6    7
30   40   _    _    _    _    10   20
```

Copiar `b[k] = a[k];` produciría un orden incorrecto. Debemos copiar por posición lógica.

### 21. resize() preservando el orden lógico

El nuevo arreglo puede reconstruirse de forma normalizada. Queremos transformar el arreglo antiguo (`j = 6`, `n = 4`, con los valores dispersos como arriba) en un arreglo nuevo donde `10, 20, 30, 40` ocupen `a[0..3]` y `j = 0`. La implementación puede ser:

```java
private void resize() {
    Integer[] b =
            new Integer[Math.max(1, 2 * n)];

    for (int k = 0; k < n; k++) {
        b[k] = a[(j + k) % a.length];
    }

    a = b;
    j = 0;
}
```

#### El ciclo copia elementos lógicos

La variable `k` representa una posición lógica. La expresión `(j + k) % a.length` encuentra la posición física correspondiente en el arreglo antiguo, y `b[k]` recibe el elemento lógico `k`. En el arreglo nuevo, el elemento lógico 0 va a `b[0]`, el elemento lógico 1 a `b[1]`, y así sucesivamente; por eso la nueva posición física del frente es `j = 0`.

`resize()` no agrega ni elimina elementos, así que `n` permanece igual. La operación cambia la capacidad, la distribución física y `j`, pero preserva el contenido lógico, el orden FIFO y el tamaño.

### 22. Costo de resize() en ArrayQueue

El ciclo `for (int k = 0; k < n; k++) { b[k] = a[(j + k) % a.length]; }` copia `n` elementos, así que `resize()` tiene costo **O(n)**. La aritmética modular dentro del ciclo es trabajo constante por elemento; la cantidad de iteraciones sigue siendo proporcional a `n`. El uso de circularidad no modifica la complejidad de la copia.

### 23. Costo amortizado de una cola circular

Una llamada particular a `add()` o `remove()` puede ejecutar `resize()`, así que una operación individual puede llegar a costar **O(n)**. Sin embargo, el redimensionamiento no ocurre en todas las operaciones.

Cuando la cola crece, la capacidad aumenta geométricamente, y después de una expansión deben ocurrir muchas inserciones antes de necesitar otra. Cuando la cola se reduce, la condición `a.length >= 3 * n` evita reducir la capacidad después de cada eliminación. Entre redimensionamientos aparecen muchas operaciones ordinarias. La idea ya fue introducida en la Semana 1: operaciones ordinarias en **O(1)**, `resize` ocasional en **O(n)**. Al estudiar una secuencia larga, el costo total de las copias puede distribuirse entre muchas operaciones. Por eso describimos `ArrayQueue.add(x)` y `ArrayQueue.remove()` como **O(1) amortizado**.

#### Qué significa amortizado esta semana

No significa que cada llamada individual cueste O(1); puede existir una llamada que copie muchos elementos. La afirmación es sobre el costo distribuido de una secuencia larga de operaciones. Para esta semana basta comprender esa idea; no es necesario utilizar método contable formal, método potencial ni una prueba formal completa.

### 24. Una traza de ArrayQueue

Considera el estado inicial `a.length = 4`, `j = 0`, `n = 0`. Ejecutamos `add(10)`, `add(20)`, `add(30)`. La representación física puede ser `a = [10, 20, 30, _]`, `j = 0`, `n = 3`, con vista lógica `[10, 20, 30]`.

Ahora `remove()` retorna 10 y puede dejar `a = [_, 20, 30, _]`, `j = 1`, `n = 2`, con vista lógica `[20, 30]`.

Ahora `add(40)` coloca el nuevo elemento en `(j + n) % 4 = (1 + 2) % 4 = 3`, dejando `a = [_, 20, 30, 40]`, `j = 1`, `n = 3`.

Después, `add(50)` calcula la posición lógica nueva 3 como `(1 + 3) % 4 = 0`, dejando `a = [50, 20, 30, 40]`, `j = 1`, `n = 4`. La vista lógica continúa siendo `[20, 30, 40, 50]`, aunque físicamente 50 está en `a[0]`.

Si ahora agregamos otro elemento, la cola está llena y deberá ejecutar `resize()` antes de insertar. El nuevo arreglo quedará normalizado con `j = 0`.

### 25. Vista lógica y representación física

Una estructura circular obliga a separar dos preguntas: ¿en qué orden se observan los elementos? y ¿en qué celdas físicas están almacenados? El método `String logicalView()` del código de la semana reconstruye el orden lógico mediante:

```java
for (int k = 0; k < n; k++) {
    view[k] = a[(j + k) % a.length];
}
```

Este método es útil para comprobar visualmente el invariante, pero no debe confundirse su complejidad con la de las operaciones fundamentales. `logicalView()` recorre `n` elementos, así que tiene costo **O(n)**. En cambio, `peek()` es **O(1)**, y `add()` y `remove()` son **O(1) amortizado**.

### 26. Stack y Queue como restricciones diferentes

Podemos comparar ahora ambas estructuras.

| Propiedad | Stack | Queue |
|---|---|---|
| política | LIFO | FIFO |
| inserción | tope | final |
| eliminación | tope | frente |
| elemento siguiente | más reciente | más antiguo |
| representación usada esta semana | lista enlazada | arreglo circular |

La diferencia esencial no es "nodos frente a arreglos". La diferencia esencial es la **política de acceso**. Las representaciones son decisiones de implementación elegidas para soportar eficientemente esa política.

### 27. Deque como generalización de los extremos

Un **deque** permite trabajar por ambos extremos. Conceptualmente ofrece operaciones equivalentes a insertar al frente, insertar al final, eliminar del frente y eliminar del final. Esto permite ver una relación: un `Stack` utiliza un extremo, una `Queue` utiliza extremos opuestos con disciplina FIFO, y un `Deque` permite operar por ambos extremos. Un deque no necesita permitir inserción o eliminación arbitraria en cualquier posición interna; su característica principal es el acceso eficiente a los dos extremos.

### 28. Una DLList como representación natural de Deque

En la Semana 2 estudiamos `dummy`, `prev` y `next`. En una lista doblemente enlazada con centinela, `dummy.next` permite localizar directamente el primer nodo, y `dummy.prev` permite localizar directamente el último nodo. Conceptualmente, `dummy <-> A <-> B <-> C <-> dummy`. Tenemos disponibles ambos extremos, así que una `DLList` es una representación natural para un deque.

Las operaciones locales sobre los extremos pueden modificar una cantidad constante de referencias: insertar al frente, eliminar del frente, insertar al final y eliminar del final son todas **O(1)** si los extremos ya están directamente representados mediante el centinela.

Esta semana no necesitamos implementar una clase `Deque` completa. El objetivo es reconocer qué representación ya estudiada permite soportar naturalmente sus operaciones.

### 29. Un deque también puede tener otras representaciones

Que una `DLList` sea natural para un deque no significa que sea la única posibilidad. También puede construirse una representación contigua circular. Sin embargo, implementar un deque circular completo exigiría estudiar más operaciones de movimiento y administración de ambos extremos, lo cual queda fuera del núcleo de esta semana.

La lección importante es que un ADT no determina una única representación. Al igual que `Stack` puede implementarse con arreglos o nodos, un `Deque` puede tener representaciones diferentes. La elección depende del patrón de operaciones y de los costos que queremos favorecer.

### 30. Elegir representación para una pila

Supongamos que necesitamos `push`, `pop`, `peek`, y todas las operaciones se concentran en el mismo extremo. Una lista simplemente enlazada con `head` proporciona `push`, `pop` y `peek` en **O(1)**. Un arreglo dinámico que trabaje en el extremo final también puede proporcionar `push` y `pop` en **O(1) amortizado**, y `peek` en **O(1)**.

La elección no se decide únicamente con una fórmula; también intervienen factores como la simplicidad de implementación, la sobrecarga de referencias, la capacidad y redimensionamiento, y el patrón de acceso esperado. Para el código de esta semana elegimos una `LinkedStack` porque conecta directamente con las operaciones de `SLList` de la Semana 2.

### 31. Elegir representación para una cola

Una cola necesita insertar al final y retirar del frente. Una `SLList` que mantiene `head` y `tail` puede realizar ambas operaciones en **O(1)**. También una `ArrayQueue` circular puede realizar `add` y `remove` en **O(1) amortizado**, sin desplazar todos los elementos en cada eliminación.

La representación circular tiene un requisito adicional: debemos preservar correctamente la relación `a[(j + k) % a.length]`. Por tanto, la cola circular intercambia desplazamientos por una administración más cuidadosa de índices.

### 32. Elegir representación para un deque

Un deque necesita eficiencia en ambos extremos. Una lista simplemente enlazada no es completamente simétrica; aunque puede conocer `tail`, el último nodo no proporciona directamente su predecesor, así que eliminar desde el final de una `SLList` puede requerir recorrido.

Una `DLList` conserva `prev` y `next`, y puede localizar ambos extremos mediante `dummy`. Esto la convierte en una representación especialmente adecuada para un deque. Otra implementación podría utilizar un arreglo circular diseñado para dos extremos, pero esa implementación completa no forma parte de esta semana.

### 33. Comparación de representaciones

La relación entre las tres semanas puede resumirse así.

| Representación | Información disponible directamente | Operaciones favorecidas |
|---|---|---|
| arreglo dinámico | índice, tamaño, capacidad | acceso por índice, inserción al final amortizada |
| SLList | cabecera, final si existe `tail`, siguiente | modificaciones en extremos adecuados |
| DLList | ambos vecinos y ambos extremos con `dummy` | modificaciones locales y ambos extremos |
| ArrayQueue circular | frente `j`, tamaño `n`, posición modular | `add` y `remove` FIFO amortizados |

No existe una representación universalmente mejor. Una estructura es adecuada cuando su información interna coincide con las operaciones que el ADT necesita realizar con frecuencia.

### 34. Síntesis

Una pila, una cola y un deque son ADT lineales cuya identidad depende de las restricciones que imponen sobre los extremos de una secuencia.

Una pila utiliza política **LIFO** y permite insertar y retirar por el mismo extremo. En la implementación `LinkedStack`, ese extremo está representado por `head`. Las operaciones `push`, `pop`, `peek`, `isEmpty` y `size` pueden ejecutarse en **O(1)** porque no necesitan recorrer la estructura. La aplicación `reverse()` muestra cómo un algoritmo puede utilizar el comportamiento LIFO sin conocer la representación interna de la pila.

Una cola utiliza política **FIFO** y separa el extremo de inserción del extremo de eliminación. La implementación `ArrayQueue` evita desplazar todos los elementos después de cada eliminación mediante una representación circular. El estado principal es `a`, `j` y `n`, y su invariante central es que el elemento lógico `k` está en `a[(j+k) % a.length]`. El índice `j` representa el frente físico; `add()` inserta en `a[(j+n) % a.length]`, y `remove()` avanza el frente mediante `j = (j+1) % a.length`. Cuando se necesita redimensionar, los elementos deben copiarse en orden lógico (`b[k] = a[(j+k) % a.length];`) y después `j = 0`. Una llamada individual a `resize()` cuesta **O(n)**, pero el redimensionamiento ocurre con suficiente separación para describir `add()` y `remove()` como operaciones **O(1) amortizado**.

Finalmente, un deque generaliza el trabajo sobre los extremos. Una `DLList` con `dummy`, `prev` y `next` es una representación natural porque permite localizar y modificar ambos extremos sin recorrido.

Las ideas de las tres primeras semanas pueden reunirse así. La Semana 1 aportó la representación basada en arreglo, con tamaño, capacidad, `resize` y costo amortizado. La Semana 2 aportó la representación enlazada, con `head` y `tail`, `prev` y `next`, invariantes, y la distinción entre localización y modificación. La Semana 3 aporta las restricciones sobre una secuencia: `Stack` con política LIFO e implementación `LinkedStack`, `Queue` con política FIFO e implementación `ArrayQueue` circular, y `Deque` con ambos extremos, usando `DLList` como representación natural.

La conclusión general es que el ADT determina qué operaciones necesitamos, la representación determina qué información tenemos disponible directamente, el invariante determina qué estados son válidos, y el trabajo necesario para preservar ese estado determina la complejidad.

Estudiar pilas, colas y deques no consiste solamente en aprender nuevos nombres. Consiste en comprender cómo una restricción sobre el uso de una secuencia puede simplificar el diseño, y cómo una representación adecuada puede convertir esas restricciones en operaciones eficientes.
