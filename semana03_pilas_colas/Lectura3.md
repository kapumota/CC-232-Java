### Lectura: pilas, colas y restricciones de acceso sobre secuencias

Esta lectura consolida y amplía las ideas trabajadas en la Semana 3 de CC232.

En las dos primeras semanas estudiamos dos formas de representar una secuencia.

En la Semana 1 utilizamos un arreglo dinámico. Esa representación ofrecía acceso directo por índice y requería distinguir tamaño, capacidad y redimensionamiento.

En la Semana 2 utilizamos nodos enlazados. Esa representación permitía modificaciones locales eficientes cuando ya disponíamos de las referencias apropiadas, aunque el acceso por índice dejaba de ser directo.

La Semana 3 introduce una pregunta distinta.

```text
¿qué ocurre si no necesitamos todas las operaciones
de una secuencia general?
```

Una pila, una cola y un deque no se distinguen principalmente por el tipo de dato que almacenan. Se distinguen por las operaciones que permiten realizar sobre los extremos de la secuencia.

La idea central de esta semana puede anticiparse así:

```text
secuencia general
    muchas posiciones posibles de acceso

Stack
    inserta y elimina por un mismo extremo

Queue
    inserta por un extremo
    elimina por el extremo opuesto

Deque
    inserta y elimina por ambos extremos
```

Estas restricciones producen tipos abstractos de datos más simples y permiten escoger representaciones especialmente adecuadas para sus operaciones.

El objetivo no es memorizar métodos aislados.

El objetivo es comprender la relación:

```text
ADT
    qué operaciones están permitidas

representación
    cómo se almacena el estado

invariante
    qué debe permanecer verdadero

algoritmo
    cómo se modifica el estado

complejidad
    cuánto trabajo exige la operación
```

### 1. De una secuencia general a un ADT restringido

Una lista general permite pensar en operaciones como:

```text
get(i)
add(i, x)
remove(i)
```

El índice `i` puede referirse a muchas posiciones diferentes. Una pila o una cola restringen deliberadamente esa libertad. Esa restricción no es una desventaja accidental.

Es parte de la definición del ADT.

#### El ADT describe comportamiento

Un tipo abstracto de dato define qué operaciones ofrece y qué comportamiento deben observar los usuarios.

Por ejemplo, una pila puede ofrecer:

```java
void push(char x)
char pop()
char peek()
boolean isEmpty()
int size()
```

La interfaz conceptual no exige que la implementación utilice un arreglo o una lista enlazada.

Podríamos implementar una pila con:

```text
arreglo dinámico
```

o con:

```text
lista simplemente enlazada
```

El comportamiento observable debe seguir siendo el mismo.

Esta separación es importante:

```text
Stack
    describe una política LIFO

LinkedStack
    es una implementación concreta

ArrayStack
    podría ser otra implementación concreta
```

La misma idea se aplica a una cola.

```text
Queue
    describe una política FIFO

ArrayQueue
    es una implementación concreta
```

### 2. Pila y política LIFO

Una pila permite insertar y retirar elementos por un mismo extremo.

Ese extremo suele llamarse tope.

En nuestra implementación enlazada utilizaremos:

```text
head
```

como referencia al tope.

La política de la pila es:

```text
LIFO
Last In, First Out
último en entrar, primero en salir
```

Considera:

```text
push('A')
push('B')
push('C')
```

Después de esas operaciones, el último elemento insertado es:

```text
C
```

Por tanto:

```text
pop() -> C
```

Después:

```text
pop() -> B
```

y finalmente:

```text
pop() -> A
```

La secuencia de salida es la inversa de la secuencia de inserción.

#### Una traza conceptual

Podemos representar el estado así:

```text
inicial

vacía
```

Después:

```text
push(A)

top
 |
 v
A
```

Después:

```text
push(B)

top
 |
 v
B
A
```

Después:

```text
push(C)

top
 |
 v
C
B
A
```

Ahora:

```text
pop() -> C
```

deja:

```text
top
 |
 v
B
A
```

La política LIFO no depende de la implementación.

Es una propiedad del ADT.

### 3. Operaciones fundamentales de Stack

Las operaciones básicas que interesan esta semana son:

```text
push(x)
pop()
peek()
isEmpty()
size()
```

#### push(x)

Inserta un elemento en el tope.

Postcondición conceptual:

```text
el nuevo elemento pasa a ser
el primero disponible para pop()
```

#### pop()

Retira y retorna el elemento que se encuentra en el tope.

Si la pila está vacía, la implementación debe definir qué ocurre.

En el código de esta semana se utiliza una excepción para representar ese caso.

#### peek()

Consulta el elemento del tope sin retirarlo.

Después de:

```text
x = peek()
```

el tamaño de la pila no cambia.

#### isEmpty()

Responde si la pila contiene cero elementos.

Si mantenemos explícitamente un contador `n`, podemos expresar:

```text
isEmpty() <=> n == 0
```

#### size()

Retorna el número de elementos lógicos almacenados.

Si ese número ya está registrado en `n`:

```text
size() -> O(1)
```

No se necesita recorrer los nodos.

### 4. Una pila enlazada como especialización de la Semana 2

En la Semana 2 trabajamos con una lista simplemente enlazada.

Una parte de esa representación era:

```text
head
nodos con next
```

Para una pila no necesitamos una estructura general que permita operar en posiciones arbitrarias.

Solo necesitamos operar sobre el tope.

Podemos utilizar:

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

y mantener:

```java
private Node head;
private int n;
```

La representación conceptual es:

```text
head
 |
 v
+---+------+     +---+------+     +---+------+
| C | next | --> | B | next | --> | A | null |
+---+------+     +---+------+     +---+------+

n = 3
```

`head` cumple dos funciones conceptualmente relacionadas.

```text
cabecera de la lista enlazada

y

tope de la pila
```

No necesitamos `tail` para las operaciones fundamentales de esta implementación porque `push()` y `pop()` trabajan únicamente en `head`.

### 5. Invariante de LinkedStack

La pila enlazada debe conservar relaciones coherentes entre:

```text
head
n
cadena de referencias next
```

Una formulación útil del invariante es:

```text
n == 0
    implica
head == null
```

y:

```text
n > 0
    implica
head != null
```

Además, comenzando en `head` y siguiendo `next`, deben encontrarse exactamente `n` nodos.

Por ejemplo:

```text
head -> C -> B -> A -> null
n = 3
```

es coherente.

En cambio:

```text
head -> C -> B -> A -> null
n = 4
```

no representa un estado válido.

#### Por qué no basta con que pop() retorne un valor correcto

Supongamos que una implementación retorna correctamente `C`, pero deja:

```text
n = 3
```

cuando la cadena ahora contiene solamente:

```text
B -> A
```

La salida inmediata podría parecer correcta.

Sin embargo, la representación quedó inconsistente.

Por eso una operación debe cumplir dos responsabilidades:

```text
producir el comportamiento observable correcto

y

preservar el invariante interno
```

### 6. Implementar push(char x)

Considera el estado:

```text
head
 |
 v
B -> A -> null

n = 2
```

Queremos ejecutar:

```text
push(C)
```

El nuevo nodo debe apuntar al antiguo `head`.

Después debe convertirse en el nuevo `head`.

El constructor del nodo permite expresar ambas ideas:

```java
head = new Node(x, head);
```

Después actualizamos el tamaño:

```java
n++;
```

La operación completa es:

```java
void push(char x) {
    head = new Node(x, head);
    n++;
}
```

#### Qué ocurre en la asignación

Antes de modificar `head`, la parte derecha:

```java
new Node(x, head)
```

utiliza la referencia antigua.

Si:

```text
head -> B
```

el nuevo nodo queda inicialmente como:

```text
C -> B
```

Después la asignación actualiza `head`:

```text
head -> C -> B -> A -> null
```

No se pierde la estructura anterior.

#### Caso vacío

Si:

```text
head = null
n = 0
```

entonces:

```java
new Node(x, head)
```

crea un nodo cuyo `next` es `null`.

Por tanto, la misma implementación sirve también para la pila vacía.

No necesitamos un caso especial para insertar el primer elemento.

#### Complejidad

La operación:

```text
crea un nodo
modifica head
incrementa n
```

No recorre la estructura.

Por tanto:

```text
push(x) -> O(1)
```

### 7. Implementar pop()

Considera:

```text
head
 |
 v
C -> B -> A -> null

n = 3
```

Queremos retirar `C`.

Primero debemos conservar el valor que se retornará:

```java
char x = head.x;
```

Después hacemos avanzar `head`:

```java
head = head.next;
```

Finalmente reducimos:

```java
n--;
```

y retornamos:

```java
return x;
```

Una implementación es:

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

Si:

```text
n = 0
head = null
```

la expresión:

```java
head.x
```

no representa una operación válida.

Por eso la precondición operativa debe comprobarse antes de acceder al nodo.

#### Estado después de pop()

Antes:

```text
head -> C -> B -> A -> null
n = 3
```

Después:

```text
retorno = C

head -> B -> A -> null
n = 2
```

#### Complejidad

No se realiza recorrido.

Por tanto:

```text
pop() -> O(1)
```

La representación enlazada se ajusta especialmente bien a la pila porque el lugar de trabajo siempre está localizado por `head`.

### 8. peek(), isEmpty() y size()

Aunque el archivo de la semana utiliza principalmente `push()`, `pop()`, `isEmpty()` y `size()`, conviene comprender también `peek()` como operación del ADT.

Una implementación conceptual puede ser:

```java
char peek() {
    if (n == 0) {
        throw new java.util.NoSuchElementException("pila vacía");
    }

    return head.x;
}
```

La operación no modifica:

```text
head
next
n
```

Por tanto:

```text
peek() -> O(1)
```

También:

```java
boolean isEmpty() {
    return n == 0;
}
```

y:

```java
int size() {
    return n;
}
```

tienen costo:

```text
O(1)
```

porque no necesitan recorrer los nodos.

### 9. Una aplicación de Stack, invertir una secuencia

Una pila puede utilizarse cuando necesitamos recuperar elementos en orden inverso al de inserción.

El archivo de la semana contiene:

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

El algoritmo tiene dos fases.

```text
fase 1
insertar todos los caracteres

fase 2
retirarlos todos
```

Para:

```text
ABC
```

la primera fase produce:

```text
push(A)
push(B)
push(C)
```

La pila queda:

```text
head -> C -> B -> A -> null
```

La segunda fase produce:

```text
pop() -> C
pop() -> B
pop() -> A
```

y la salida es:

```text
CBA
```

#### Qué demuestra este ejemplo

El algoritmo `reverse()` no necesita conocer:

```text
Node
next
head
```

Solo necesita utilizar las operaciones del ADT.

Esto muestra una separación importante:

```text
algoritmo cliente
    usa push, pop, isEmpty

implementación
    administra nodos y referencias
```

La política LIFO explica el resultado.

La inversión es una consecuencia de la disciplina de acceso de la pila.

### 10. Cola y política FIFO

Una cola restringe una secuencia de forma diferente.

Los elementos se insertan por un extremo y se retiran por el extremo opuesto.

La política es:

```text
FIFO
First In, First Out
primero en entrar, primero en salir
```

Considera:

```text
add(10)
add(20)
add(30)
```

La cola lógica es:

```text
frente
 |
 v
10, 20, 30
        ^
        |
       final
```

La siguiente eliminación debe retornar:

```text
10
```

Después:

```text
20
```

y luego:

```text
30
```

La cola conserva el orden de llegada.

### 11. Operaciones fundamentales de Queue

Las operaciones que interesan esta semana son:

```text
add(x)
remove()
peek()
size()
```

#### add(x)

Inserta un nuevo elemento al final lógico de la cola.

#### remove()

Retira el elemento situado en el frente lógico.

#### peek()

Consulta el elemento del frente sin eliminarlo.

#### size()

Retorna el número de elementos almacenados.

Al igual que con Stack, estas operaciones describen comportamiento. No obligan por sí mismas a utilizar una representación concreta.

Una cola podría implementarse mediante:

```text
SLList con head y tail
```

o mediante:

```text
arreglo circular
```

En esta semana utilizaremos la segunda alternativa porque permite integrar conceptos de las Semanas 1 y 3.

### 12. El problema de una cola basada en arreglo

Supongamos:

```text
a = [10, 20, 30, 40, _]
n = 4
```

Si interpretamos siempre que el frente está en:

```text
a[0]
```

entonces eliminar 10 y mantener la secuencia contigua podría requerir:

```text
20 se mueve a 0
30 se mueve a 1
40 se mueve a 2
```

Después:

```text
a = [20, 30, 40, _, _]
```

El problema es que cada `remove()` podría realizar una cantidad de desplazamientos proporcional a `n`.

```text
remove ingenuo desde a[0] -> O(n)
```

Podemos evitar esos desplazamientos si dejamos de exigir que el frente lógico permanezca físicamente en `a[0]`.

En lugar de mover todos los elementos:

```text
movemos la interpretación del frente
```

Para eso introducimos un índice:

```text
j
```

### 13. Representación de ArrayQueue

El archivo de la semana utiliza:

```java
private Integer[] a = new Integer[4];
private int j;
private int n;
```

Cada campo tiene un significado distinto.

```text
a
    arreglo de respaldo

j
    índice físico del frente lógico

n
    número de elementos lógicos
```

Si:

```text
a.length = 8
j = 2
n = 3
```

los elementos lógicos podrían estar en:

```text
a[2]
a[3]
a[4]
```

Pero cuando `j` se aproxima al final del arreglo, la secuencia puede continuar físicamente desde la posición cero.

Por ejemplo:

```text
a.length = 8
j = 6
n = 4
```

Los elementos lógicos ocupan:

```text
a[6]
a[7]
a[0]
a[1]
```

La cola es circular desde el punto de vista de la interpretación de los índices.

El arreglo de Java sigue siendo un arreglo ordinario.

### 14. Índice lógico frente a índice físico

Esta distinción es central en la Semana 3.

#### Índice lógico

Describe la posición del elemento dentro de la cola.

```text
k = 0
    primer elemento lógico

k = 1
    segundo elemento lógico

...

k = n - 1
    último elemento lógico
```

#### Índice físico

Describe la celda concreta del arreglo donde se encuentra el elemento.

Si el frente está en `j`, la posición física correspondiente al elemento lógico `k` es:

```text
(j + k) % a.length
```

Por ejemplo:

```text
a.length = 8
j = 6
n = 4
```

Entonces:

```text
k = 0
(6 + 0) % 8 = 6

k = 1
(6 + 1) % 8 = 7

k = 2
(6 + 2) % 8 = 0

k = 3
(6 + 3) % 8 = 1
```

Así:

```text
posición lógica    posición física

0                  6
1                  7
2                  0
3                  1
```

El orden lógico continúa aunque el almacenamiento físico llegue al final del arreglo.

### 15. El invariante circular

La relación fundamental de `ArrayQueue` puede expresarse como:

```text
el elemento lógico k
está en

a[(j + k) % a.length]
```

para:

```text
0 <= k < n
```

Este invariante conecta:

```text
orden lógico

con

almacenamiento físico
```

También necesitamos:

```text
0 <= n <= a.length
```

y, cuando:

```text
n > 0
```

el frente lógico está en:

```text
a[j]
```

#### Un estado válido

Supongamos:

```text
a.length = 6
j = 4
n = 4
```

y:

```text
índice físico

0    1    2    3    4    5
30   40   _    _    10   20
```

La vista lógica es:

```text
10, 20, 30, 40
```

porque:

```text
k=0 -> 4
k=1 -> 5
k=2 -> 0
k=3 -> 1
```

El arreglo físico parece dividido.

La cola lógica no lo está.

### 16. La operación peek() en ArrayQueue

Si `j` señala el frente, consultar el primer elemento es directo.

El archivo utiliza:

```java
Integer peek() {
    return n == 0 ? null : a[j];
}
```

Si:

```text
n == 0
```

retorna:

```text
null
```

En otro caso:

```text
a[j]
```

es exactamente el primer elemento lógico.

No existe recorrido.

Por tanto:

```text
peek() -> O(1)
```

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

La expresión central es:

```java
(j + n) % a.length
```

¿Por qué aparece `n`?

Si existen `n` elementos lógicos, sus posiciones son:

```text
0
1
2
...
n - 1
```

La siguiente posición lógica disponible es:

```text
n
```

Por tanto, su posición física es:

```text
(j + n) % a.length
```

#### Ejemplo sin envoltura

Si:

```text
a.length = 8
j = 2
n = 3
```

la nueva posición es:

```text
(2 + 3) % 8 = 5
```

#### Ejemplo con envoltura

Si:

```text
a.length = 8
j = 6
n = 3
```

la nueva posición es:

```text
(6 + 3) % 8 = 1
```

El nuevo elemento se almacena al inicio físico del arreglo, pero al final lógico de la cola.

### 18. Avance modular del frente

Después de retirar el elemento situado en:

```text
a[j]
```

el siguiente elemento lógico debe convertirse en el nuevo frente.

Podemos avanzar:

```java
j = (j + 1) % a.length;
```

Si:

```text
a.length = 8
j = 3
```

entonces:

```text
nuevo j = 4
```

Si:

```text
a.length = 8
j = 7
```

entonces:

```text
nuevo j = 0
```

El módulo permite regresar al inicio físico del arreglo.

#### Por qué no desplazar los elementos

Supongamos:

```text
j = 6
```

y la cola lógica es:

```text
10, 20, 30
```

físicamente:

```text
a[6] = 10
a[7] = 20
a[0] = 30
```

Después de eliminar 10 no necesitamos transformar físicamente la estructura en:

```text
20, 30
```

a partir de `a[0]`.

Basta hacer que:

```text
j = 7
```

Ahora la interpretación lógica comienza en 20.

La estructura cambia el significado del índice inicial en vez de mover todos los datos.

### 19. Implementar remove()

La operación debe realizar cinco tareas conceptuales:

```text
1. comprobar que exista un elemento

2. conservar el valor del frente

3. liberar la posición física anterior

4. avanzar j modularmente

5. disminuir n
```

El archivo además permite reducir la capacidad cuando el arreglo queda demasiado vacío.

Una implementación es:

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

Primero:

```java
Integer x = a[j];
```

conserva el elemento FIFO que debemos retornar.

Después:

```java
a[j] = null;
```

deja de conservar una referencia al elemento eliminado en esa celda.

Luego:

```java
j = (j + 1) % a.length;
```

establece un nuevo frente.

Después:

```java
n--;
```

actualiza el tamaño lógico.

Finalmente puede aparecer una reducción de capacidad.

#### Complejidad sin resize()

Ignorando el redimensionamiento, `remove()` ejecuta una cantidad constante de trabajo.

Por tanto:

```text
remove() sin resize -> O(1)
```

Pero algunas llamadas pueden ejecutar una copia lineal.

Por eso necesitaremos nuevamente análisis amortizado.

### 20. Por qué resize() es diferente en una cola circular

En la Semana 1, si los elementos válidos estaban en:

```text
a[0..n-1]
```

podíamos copiar:

```java
b[k] = a[k];
```

En una cola circular esa instrucción puede ser incorrecta.

Supongamos:

```text
a.length = 8
j = 6
n = 4
```

y los valores lógicos son:

```text
10, 20, 30, 40
```

La representación física podría ser:

```text
índice

0    1    2    3    4    5    6    7
30   40   _    _    _    _    10   20
```

Copiar:

```java
b[k] = a[k];
```

produciría un orden incorrecto.

Debemos copiar por posición lógica.

### 21. resize() preservando el orden lógico

El nuevo arreglo puede reconstruirse de forma normalizada.

Queremos transformar:

```text
arreglo antiguo

índice

0    1    2    3    4    5    6    7
30   40   _    _    _    _    10   20

j = 6
n = 4
```

en:

```text
arreglo nuevo

índice

0    1    2    3    4    5    6    7
10   20   30   40   _    _    _    _

j = 0
n = 4
```

La implementación puede ser:

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

La variable:

```text
k
```

representa una posición lógica.

La expresión:

```java
(j + k) % a.length
```

encuentra la posición física correspondiente en el arreglo antiguo.

Entonces:

```java
b[k]
```

recibe el elemento lógico `k`.

#### Por qué j termina en cero

En el arreglo nuevo:

```text
elemento lógico 0 -> b[0]
elemento lógico 1 -> b[1]
...
```

Por tanto, la nueva posición física del frente es:

```text
j = 0
```

#### Qué no cambia

`resize()` no agrega ni elimina elementos.

Por eso:

```text
n
```

permanece igual.

La operación cambia:

```text
capacidad
distribución física
j
```

pero preserva:

```text
contenido lógico
orden FIFO
tamaño
```

### 22. Costo de resize() en ArrayQueue

El ciclo:

```java
for (int k = 0; k < n; k++) {
    b[k] = a[(j + k) % a.length];
}
```

copia `n` elementos.

Por tanto:

```text
resize() -> O(n)
```

La aritmética modular dentro del ciclo es trabajo constante por elemento. La cantidad de iteraciones sigue siendo proporcional a `n`.

El uso de circularidad no modifica la complejidad de la copia.

### 23. Costo amortizado de una cola circular

Una llamada particular a:

```text
add()
```

o:

```text
remove()
```

puede ejecutar `resize()`.

Por tanto, una operación individual puede llegar a costar:

```text
O(n)
```

Sin embargo, el redimensionamiento no ocurre en todas las operaciones.

Cuando la cola crece, la capacidad aumenta geométricamente.

Después de una expansión deben ocurrir muchas inserciones antes de necesitar otra.

Cuando la cola se reduce, la condición:

```text
a.length >= 3 * n
```

evita reducir la capacidad después de cada eliminación.

Entre redimensionamientos aparecen muchas operaciones ordinarias.

La idea ya fue introducida en la Semana 1.

```text
operaciones ordinarias
    O(1)

resize ocasional
    O(n)
```

Al estudiar una secuencia larga, el costo total de las copias puede distribuirse entre muchas operaciones.

Por eso describimos:

```text
ArrayQueue.add(x) -> O(1) amortizado

ArrayQueue.remove() -> O(1) amortizado
```

#### Qué significa amortizado esta semana

No significa que cada llamada individual cueste `O(1)`. Puede existir una llamada que copie muchos elementos.

La afirmación es sobre el costo distribuido de una secuencia larga de operaciones. Para esta semana basta comprender esa idea.

No es necesario utilizar:

```text
método contable formal
método potencial
prueba formal completa
```

### 24. Una traza de ArrayQueue

Considera el estado inicial:

```text
a.length = 4
j = 0
n = 0
```

Ejecutamos:

```text
add(10)
add(20)
add(30)
```

La representación física puede ser:

```text
a = [10, 20, 30, _]
j = 0
n = 3
```

La vista lógica es:

```text
[10, 20, 30]
```

Ahora:

```text
remove()
```

retorna:

```text
10
```

y puede dejar:

```text
a = [_, 20, 30, _]
j = 1
n = 2
```

La vista lógica es:

```text
[20, 30]
```

Ahora:

```text
add(40)
```

coloca el nuevo elemento en:

```text
(j + n) % 4

(1 + 2) % 4 = 3
```

Entonces:

```text
a = [_, 20, 30, 40]
j = 1
n = 3
```

Después:

```text
add(50)
```

la posición lógica nueva es 3:

```text
(1 + 3) % 4 = 0
```

Entonces:

```text
a = [50, 20, 30, 40]
j = 1
n = 4
```

La vista lógica continúa siendo:

```text
[20, 30, 40, 50]
```

Aunque físicamente 50 está en `a[0]`.

Si ahora agregamos otro elemento, la cola está llena y deberá ejecutar `resize()` antes de insertar.

El nuevo arreglo quedará normalizado con:

```text
j = 0
```

### 25. Vista lógica y representación física

Una estructura circular obliga a separar dos preguntas.

```text
¿en qué orden se observan los elementos?

¿en qué celdas físicas están almacenados?
```

El método:

```java
String logicalView()
```

del código de la semana reconstruye el orden lógico mediante:

```java
for (int k = 0; k < n; k++) {
    view[k] = a[(j + k) % a.length];
}
```

Este método es útil para comprobar visualmente el invariante.

Sin embargo, no debe confundirse su complejidad con la de las operaciones fundamentales.

`logicalView()` recorre `n` elementos.

Por tanto:

```text
logicalView() -> O(n)
```

En cambio:

```text
peek() -> O(1)

add() -> O(1) amortizado

remove() -> O(1) amortizado
```

### 26. Stack y Queue como restricciones diferentes

Podemos comparar ahora ambas estructuras.

| Propiedad | Stack | Queue |
|---|---|---|
| política | LIFO | FIFO |
| inserción | tope | final |
| eliminación | tope | frente |
| elemento siguiente | más reciente | más antiguo |
| representación usada esta semana | lista enlazada | arreglo circular |

La diferencia esencial no es:

```text
nodos frente a arreglos
```

La diferencia esencial es:

```text
política de acceso
```

Las representaciones son decisiones de implementación elegidas para soportar eficientemente esa política.

### 27. Deque como generalización de los extremos

Un deque permite trabajar por ambos extremos.

Conceptualmente ofrece operaciones equivalentes a:

```text
insertar al frente
insertar al final
eliminar del frente
eliminar del final
```

Esto permite ver una relación:

```text
Stack
    utiliza un extremo

Queue
    utiliza extremos opuestos
    con disciplina FIFO

Deque
    permite operar por ambos extremos
```

Un deque no necesita permitir inserción o eliminación arbitraria en cualquier posición interna.

Su característica principal es el acceso eficiente a los dos extremos.

### 28. Una DLList como representación natural de Deque

En la Semana 2 estudiamos:

```text
dummy
prev
next
```

En una lista doblemente enlazada con centinela:

```text
dummy.next
```

permite localizar directamente el primer nodo.

También:

```text
dummy.prev
```

permite localizar directamente el último nodo.

Conceptualmente:

```text
dummy <-> A <-> B <-> C <-> dummy
```

Tenemos disponibles ambos extremos.

Por eso una `DLList` es una representación natural para un deque.

Las operaciones locales sobre los extremos pueden modificar una cantidad constante de referencias.

Por ejemplo:

```text
insertar al frente -> O(1)

eliminar del frente -> O(1)

insertar al final -> O(1)

eliminar del final -> O(1)
```

si los extremos ya están directamente representados mediante el centinela.

Esta semana no necesitamos implementar una clase `Deque` completa.

El objetivo es reconocer qué representación ya estudiada permite soportar naturalmente sus operaciones.

### 29. Un deque también puede tener otras representaciones

Que una `DLList` sea natural para un deque no significa que sea la única posibilidad. También puede construirse una representación contigua circular.

Sin embargo, implementar un deque circular completo exigiría estudiar más operaciones de movimiento y administración de ambos extremos.

Eso queda fuera del núcleo de esta semana.

La lección importante es:

```text
un ADT no determina una única representación
```

Al igual que:

```text
Stack
```

puede implementarse con arreglos o nodos, un:

```text
Deque
```

puede tener representaciones diferentes.

La elección depende del patrón de operaciones y de los costos que queremos favorecer.

### 30. Elegir representación para una pila

Supongamos que necesitamos:

```text
push
pop
peek
```

y todas las operaciones se concentran en el mismo extremo.

Una lista simplemente enlazada con `head` proporciona:

```text
push -> O(1)
pop  -> O(1)
peek -> O(1)
```

Un arreglo dinámico que trabaje en el extremo final también puede proporcionar:

```text
push -> O(1) amortizado
pop  -> O(1) amortizado
peek -> O(1)
```

La elección no se decide únicamente con una fórmula.

También intervienen factores como:

```text
simplicidad de implementación
sobrecarga de referencias
capacidad y redimensionamiento
patrón de acceso esperado
```

Para el código de esta semana elegimos una `LinkedStack` porque conecta directamente con las operaciones de `SLList` de la Semana 2.

### 31. Elegir representación para una cola

Una cola necesita:

```text
insertar al final
retirar del frente
```

Una `SLList` que mantiene:

```text
head
tail
```

puede realizar ambas operaciones en O(1).

También una `ArrayQueue` circular puede realizar:

```text
add -> O(1) amortizado
remove -> O(1) amortizado
```

sin desplazar todos los elementos en cada eliminación.

La representación circular tiene un requisito adicional.

Debemos preservar correctamente la relación:

```text
a[(j + k) % a.length]
```

Por tanto, la cola circular intercambia desplazamientos por una administración más cuidadosa de índices.

### 32. Elegir representación para un deque

Un deque necesita eficiencia en ambos extremos.

Una lista simplemente enlazada no es completamente simétrica.

Aunque puede conocer `tail`, el último nodo no proporciona directamente su predecesor.

Por eso eliminar desde el final de una `SLList` puede requerir recorrido.

Una `DLList` conserva:

```text
prev
next
```

y puede localizar ambos extremos mediante `dummy`.

Esto la convierte en una representación especialmente adecuada para un deque.

Otra implementación podría utilizar un arreglo circular diseñado para dos extremos, pero esa implementación completa no forma parte de esta semana.

### 33. Comparación de representaciones

La relación entre las tres semanas puede resumirse así.

| Representación | Información disponible directamente | Operaciones favorecidas |
|---|---|---|
| arreglo dinámico | índice, tamaño, capacidad | acceso por índice, inserción al final amortizada |
| SLList | cabecera, final si existe `tail`, siguiente | modificaciones en extremos adecuados |
| DLList | ambos vecinos y ambos extremos con `dummy` | modificaciones locales y ambos extremos |
| ArrayQueue circular | frente `j`, tamaño `n`, posición modular | `add` y `remove` FIFO amortizados |

No existe una representación universalmente mejor.

Una estructura es adecuada cuando su información interna coincide con las operaciones que el ADT necesita realizar con frecuencia.

### 34. Síntesis

Una pila, una cola y un deque son ADT lineales cuya identidad depende de las restricciones que imponen sobre los extremos de una secuencia.

Una pila utiliza política:

```text
LIFO
```

y permite insertar y retirar por el mismo extremo.

En la implementación `LinkedStack`, ese extremo está representado por:

```text
head
```

Las operaciones:

```text
push
pop
peek
isEmpty
size
```

pueden ejecutarse en O(1) porque no necesitan recorrer la estructura.

La aplicación `reverse()` muestra cómo un algoritmo puede utilizar el comportamiento LIFO sin conocer la representación interna de la pila.

Una cola utiliza política:

```text
FIFO
```

y separa el extremo de inserción del extremo de eliminación.

La implementación `ArrayQueue` evita desplazar todos los elementos después de cada eliminación mediante una representación circular.

El estado principal es:

```text
a
j
n
```

y su invariante central es:

```text
el elemento lógico k
está en

a[(j+k) % a.length]
```

El índice `j` representa el frente físico.

La operación `add()` inserta en:

```text
a[(j+n) % a.length]
```

y `remove()` avanza el frente mediante:

```text
j = (j+1) % a.length
```

Cuando se necesita redimensionar, los elementos deben copiarse en orden lógico:

```java
b[k] = a[(j+k) % a.length];
```

y después:

```text
j = 0
```

Una llamada individual a `resize()` cuesta O(n), pero el redimensionamiento ocurre con suficiente separación para describir `add()` y `remove()` como operaciones `O(1)` amortizado.

Finalmente, un deque generaliza el trabajo sobre los extremos.

Una `DLList` con `dummy`, `prev` y `next` es una representación natural porque permite localizar y modificar ambos extremos sin recorrido.

Las ideas de las tres primeras semanas pueden reunirse así:

```text
Semana 1

representación basada en arreglo
tamaño y capacidad
resize
costo amortizado


Semana 2

representación enlazada
head y tail
prev y next
invariantes
localización frente a modificación


Semana 3

restricciones sobre una secuencia

Stack
    LIFO
    LinkedStack

Queue
    FIFO
    ArrayQueue circular

Deque
    ambos extremos
    DLList como representación natural
```

La conclusión general es:

```text
el ADT determina qué operaciones necesitamos

la representación determina
qué información tenemos disponible directamente

el invariante determina
qué estados son válidos

y el trabajo necesario para preservar
ese estado determina la complejidad
```

Estudiar pilas, colas y deques no consiste solamente en aprender nuevos nombres.

Consiste en comprender cómo una restricción sobre el uso de una secuencia puede simplificar el diseño y cómo una representación adecuada puede convertir esas restricciones en operaciones eficientes.
