### Lectura: listas enlazadas, referencias y costo de las operaciones

Esta lectura continúa las ideas desarrolladas en la Semana 1 de CC232. En la semana anterior una secuencia se representó mediante un arreglo de respaldo y un tamaño lógico. Esa representación proporcionó acceso directo por índice, pero también mostró una limitación importante, insertar o eliminar en posiciones internas puede exigir desplazar muchos elementos.

La Semana 2 estudia otra forma de representar una secuencia.

En lugar de exigir que los elementos ocupen posiciones contiguas de un arreglo, cada elemento se almacena dentro de un nodo y la secuencia se reconstruye mediante referencias entre nodos.

El objetivo no es memorizar una implementación particular, sino comprender cómo una decisión de representación modifica:

```text
la forma de localizar un elemento
las referencias que deben conservarse
los invariantes de la estructura
el costo de insertar y eliminar
el costo de acceder por posición
```

La idea central de la semana puede anticiparse así:

```text
arreglo dinámico
    localización por índice barata
    modificación interna puede ser costosa

lista enlazada
    localización por índice puede ser costosa
    modificación local puede ser barata
```

Esta diferencia será el punto de partida para estudiar listas simplemente enlazadas y listas doblemente enlazadas.

### 1. Cambiar la representación cambia el costo

Considera la secuencia:

```text
10, 20, 30, 40
```

En la Semana 1 una posible representación era:

```text
índice      0    1    2    3    4    5    6    7
          +----+----+----+----+----+----+----+----+
a         | 10 | 20 | 30 | 40 |    |    |    |    |
          +----+----+----+----+----+----+----+----+

n = 4
```

Esta representación hace que obtener el elemento de índice 2 sea directo:

```java
a[2]
```

No es necesario examinar `a[0]` ni `a[1]`.

Por eso:

```text
get(i) -> O(1)
```

Sin embargo, supongamos que queremos insertar `5` al inicio.

Para conservar el orden lógico debemos transformar:

```text
[10, 20, 30, 40]
```

en:

```text
[5, 10, 20, 30, 40]
```

En un arreglo debemos abrir espacio:

```text
40 se desplaza
30 se desplaza
20 se desplaza
10 se desplaza
```

El número de desplazamientos depende de `n`.

Por ello, insertar al inicio puede costar:

```text
O(n)
```

Esta observación motiva una pregunta.

```text
¿es posible representar la misma secuencia sin exigir almacenamiento contiguo?
```

Sí.

Podemos almacenar cada elemento dentro de un objeto separado y conectar esos objetos mediante referencias.

```text
[10] -> [20] -> [30] -> [40]
```

Ahora insertar delante del primer nodo no requiere desplazar físicamente 10, 20, 30 y 40.

Solo necesitamos modificar algunas referencias.

La operación puede volverse más barata. Pero esta nueva representación también pierde algo. Ya no existe una posición física calculable directamente como `a[i]`.

Para localizar un nodo interno debemos seguir enlaces.

Esta es una de las lecciones más importantes del estudio de estructuras de datos:

```text
una representación no es mejor en términos absolutos una representación favorece ciertas operaciones y hace más costosas otras
```

### 2. Orden lógico y almacenamiento

Una secuencia tiene un orden lógico.

Por ejemplo:

```text
10 antes de 20
20 antes de 30
30 antes de 40
```

En un arreglo, ese orden lógico se refleja directamente en posiciones consecutivas:

```text
a[0] = 10
a[1] = 20
a[2] = 30
a[3] = 40
```

En una lista enlazada no necesitamos esa correspondencia física.

Podemos pensar conceptualmente:

```text
10 -> 20 -> 30 -> 40 -> null
```

Lo importante es que cada nodo permita encontrar al siguiente.

El orden se conserva mediante enlaces, no mediante contigüidad.

#### Continuidad física y continuidad lógica

En una representación basada en arreglos existe una fuerte relación entre:

```text
posición lógica
posición física
```

En una lista enlazada esa relación desaparece.

Dos nodos consecutivos desde el punto de vista lógico no necesitan encontrarse juntos físicamente.

Para esta semana no necesitamos estudiar direcciones reales de memoria. Basta trabajar con referencias Java.

La secuencia queda definida por la posibilidad de seguir correctamente esas referencias.

### 3. Nodo, dato y referencia

Una lista simplemente enlazada puede utilizar un nodo como:

```java
static class Node {
    int x;
    Node next;

    Node(int x) {
        this.x = x;
    }
}
```

Este objeto contiene dos tipos de información.

```text
x
    dato lógico

next
    información estructural
```

El campo `x` pertenece al contenido que queremos almacenar.

El campo `next` permite construir la estructura.

Si tenemos:

```text
head
 |
 v
+----+------+     +----+------+     +----+------+
| 10 | next | --> | 20 | next | --> | 30 | null |
+----+------+     +----+------+     +----+------+
```

los valores lógicos son:

```text
10, 20, 30
```

y los enlaces determinan su orden.

#### Qué significa una referencia

Si una variable:

```java
Node u;
```

contiene una referencia a un nodo, podemos acceder a sus campos:

```java
u.x
u.next
```

Si `u.next` es otro nodo, podemos continuar:

```java
u.next.next
```

Si `u.next` es `null`, hemos alcanzado el final de una lista simplemente enlazada.

**La referencia permite llegar a otro objeto**. No significa que ese objeto esté necesariamente colocado físicamente al lado del anterior.

### 4. Representación de una lista simplemente enlazada

Una lista simplemente enlazada puede mantener:

```java
private Node head;
private Node tail;
private int n;
```

Cada campo tiene una función diferente.

```text
head
    referencia al primer nodo

tail
    referencia al último nodo

n
    número de elementos lógicos
```

Por ejemplo:

```text
head
 |
 v
+----+------+     +----+------+     +----+------+
| 10 | next | --> | 20 | next | --> | 30 | null |
+----+------+     +----+------+     +----+------+
                                      ^
                                      |
                                     tail

n = 3
```

El tamaño es:

```text
3
```

No necesitamos recorrer la lista para responder `size()` porque `n` ya almacena esa información.

Por tanto:

```text
size() -> O(1)
```

#### Por qué guardar tail

Una implementación podría almacenar solamente `head` y `n`.

En ese caso, para encontrar el último nodo habría que comenzar en `head` y seguir `next` hasta llegar al nodo cuyo `next` sea `null`.

Ese recorrido sería lineal.

Guardar `tail` introduce una referencia adicional, pero permite conocer directamente el último nodo.

Esta pequeña decisión de representación cambia el costo de insertar al final.

### 5. Invariantes de SLList

Una estructura enlazada necesita reglas que permitan reconocer cuándo su estado interno es válido.

Para una lista vacía:

```text
n = 0
head = null
tail = null
```

Podemos expresar una parte del invariante como:

```text
n == 0
    implica
head == null
tail == null
```

Cuando la lista contiene elementos:

```text
n > 0
```

esperamos:

```text
head != null
tail != null
tail.next == null
```

Además, si comenzamos en `head` y seguimos `next`, debemos encontrar exactamente los `n` nodos que pertenecen a la secuencia.

#### El tamaño debe coincidir con la cadena

Supongamos:

```text
head -> 10 -> 20 -> null

n = 3
```

El estado no es coherente.

La variable `n` afirma que existen tres elementos, pero solamente dos nodos son alcanzables desde `head`.

Un invariante útil no controla solamente variables individuales. También expresa relaciones entre ellas.

En este caso:

```text
head
tail
n
cadena de referencias next
```

deben describir la misma estructura lógica.

#### Casos frontera

Los casos que más fácilmente rompen un invariante son:

```text
insertar en una lista vacía
eliminar de una lista de un elemento
pasar de vacía a no vacía
pasar de no vacía a vacía
```

Por eso esos casos deben razonarse explícitamente.

### 6. Inserción al final con tail

Supongamos:

```text
head
 |
 v
10 -> 20 -> null
      ^
      |
     tail

n = 2
```

Queremos agregar:

```text
30
```

Creamos:

```java
Node u = new Node(30);
```

Después el antiguo `tail` debe apuntar al nuevo nodo:

```java
tail.next = u;
```

y finalmente:

```java
tail = u;
n++;
```

El resultado conceptual es:

```text
head
 |
 v
10 -> 20 -> 30 -> null
            ^
            |
           tail

n = 3
```

#### El caso de la lista vacía

Si:

```text
n = 0
head = null
tail = null
```

no podemos ejecutar:

```java
tail.next = u;
```

porque no existe un nodo referenciado por `tail`.

El primer nodo debe convertirse simultáneamente en cabecera y cola.

Una implementación completa puede ser:

```java
boolean add(int x) {
    Node u = new Node(x);

    if (n == 0) {
        head = u;
    } else {
        tail.next = u;
    }

    tail = u;
    n++;
    return true;
}
```

Después de insertar el primer elemento:

```text
head
 |
 v
10 -> null
^
|
tail

n = 1
```

El mismo nodo es primero y último.

#### Complejidad

La operación no recorre los `n` nodos. Modifica una cantidad constante de referencias.

Por tanto:

```text
add(x) al final con tail -> O(1)
```

Aquí aparece una diferencia interesante con el arreglo dinámico.

```text
arreglo dinámico
add al final -> O(1) amortizado

SLList con tail
add al final -> O(1)
```

La lista no necesita `resize()` porque cada nuevo elemento crea su propio nodo.

### 7. Insertar en la cabecera con push

Ahora queremos insertar un nodo al inicio.

Estado inicial:

```text
head
 |
 v
20 -> 30 -> null
      ^
      |
     tail
```

Queremos obtener:

```text
head
 |
 v
10 -> 20 -> 30 -> null
            ^
            |
           tail
```

Creamos:

```java
Node u = new Node(10);
```

El nuevo nodo debe apuntar a la antigua cabecera:

```java
u.next = head;
```

y luego debe convertirse en la nueva cabecera:

```java
head = u;
```

#### El orden de las actualizaciones

El orden importa.

Primero:

```java
u.next = head;
```

conserva el acceso a la antigua lista.

Después:

```java
head = u;
```

cambia el punto de entrada.

La idea conceptual es:

```text
conectar primero
cambiar la entrada después
```

Si modificamos referencias sin comprender qué parte de la estructura necesitamos conservar, podemos perder acceso a nodos que todavía pertenecen a la lista.

#### Caso vacío

Si la lista estaba vacía, el nuevo nodo también debe convertirse en `tail`.

Una implementación es:

```java
int push(int x) {
    Node u = new Node(x);

    u.next = head;
    head = u;

    if (n == 0) {
        tail = u;
    }

    n++;
    return x;
}
```

El número de operaciones estructurales no depende de `n`.

Por tanto:

```text
push(x) -> O(1)
```

### 8. Eliminar desde la cabecera con pop

Supongamos:

```text
head
 |
 v
10 -> 20 -> 30 -> null
            ^
            |
           tail

n = 3
```

Queremos retirar el primer nodo.

Primero debemos conservar el valor:

```java
int x = head.x;
```

Después hacemos avanzar la cabecera:

```java
head = head.next;
```

El resultado es:

```text
head
 |
 v
20 -> 30 -> null
      ^
      |
     tail
```

No se han desplazado 20 ni 30.

Solamente cambió la referencia que indica dónde empieza la lista.

#### Caso vacío

Si no existen nodos:

```text
n = 0
```

no podemos leer:

```java
head.x
```

porque `head` es `null`.

La implementación debe resolver primero ese caso.

#### Eliminar el único nodo

Supongamos:

```text
head
 |
 v
10 -> null
^
|
tail

n = 1
```

Después de eliminar 10 queremos:

```text
head = null
tail = null
n = 0
```

Si hacemos:

```java
head = head.next;
n--;
```

obtendremos:

```text
head = null
n = 0
```

pero `tail` todavía podría apuntar al antiguo nodo.

Eso violaría el invariante. Por ello debemos actualizar `tail` cuando la eliminación deja vacía la lista.

Una implementación es:

```java
Integer pop() {
    if (n == 0) {
        return null;
    }

    int x = head.x;
    head = head.next;
    n--;

    if (n == 0) {
        tail = null;
    }

    return x;
}
```

La operación modifica una cantidad constante de referencias.

Por tanto:

```text
pop() -> O(1)
```

### 9. SLList y operaciones en los extremos

Las operaciones anteriores muestran que una lista simplemente enlazada puede trabajar eficientemente sobre ciertos extremos.

En la cabecera:

```text
insertar -> O(1)
eliminar -> O(1)
```

En el final, si almacenamos `tail`:

```text
insertar -> O(1)
```

Esta propiedad permite entender por qué una `SLList` puede ser una representación apropiada para comportamientos de pila o de cola.

#### Pila

Una pila utiliza el mismo extremo para insertar y retirar.

Conceptualmente:

```text
push
pop
```

pueden trabajar sobre `head`.

#### Cola

Una cola inserta por un extremo y elimina por el otro.

Conceptualmente:

```text
insertar por tail
eliminar por head
```

puede realizarse en tiempo constante si ambas referencias se mantienen correctamente.

La idea importante no es estudiar todavía todas las operaciones formales de `Stack` y `Queue`.

La idea es observar que una representación puede ser especialmente adecuada cuando el patrón de operaciones coincide con los puntos que la estructura mantiene localizados.

### 10. La limitación fundamental de SLList

Guardar `tail` permite conocer directamente el último nodo.

Pero conocer el último nodo no significa conocer su predecesor.

Considera:

```text
head
 |
 v
10 -> 20 -> 30 -> 40 -> null
                 ^
                 |
                tail
```

Queremos eliminar 40.

Después de la eliminación, `tail` debería apuntar a 30.

El problema es que el nodo 40 no almacena información sobre 30.

Tenemos:

```text
30.next -> 40
```

pero no tenemos:

```text
40.prev -> 30
```

Para encontrar el nodo anterior a `tail` debemos comenzar en `head`:

```text
10
20
30
```

hasta encontrar el nodo cuyo `next` sea `tail`.

En el peor caso, ese recorrido examina una cantidad de nodos proporcional a `n`.

Por tanto:

```text
eliminar desde tail en SLList -> O(n)
```

#### La limitación proviene de la representación

No se trata de un error de programación.

La estructura simplemente no almacena la información necesaria para retroceder.

Cada nodo conoce:

```text
siguiente
```

pero no conoce:

```text
anterior
```

Esta observación motiva una nueva representación.

### 11. Lista doblemente enlazada

Una lista doblemente enlazada agrega una referencia hacia el nodo anterior.

```java
static class Node {
    int x;
    Node prev;
    Node next;

    Node(int x) {
        this.x = x;
    }
}
```

Ahora cada nodo mantiene:

```text
x
    dato

next
    siguiente nodo

prev
    nodo anterior
```

Conceptualmente:

```text
10 <-> 20 <-> 30
```

Podemos avanzar siguiendo `next` y retroceder siguiendo `prev`.

#### Más información, más responsabilidad

Agregar `prev` proporciona más flexibilidad. Pero también aumenta el número de relaciones que deben mantenerse correctamente.

En una lista simple bastaba preservar el recorrido hacia adelante.

En una lista doble debemos mantener coherencia en ambos sentidos.

Si:

```text
u.next == v
```

esperamos que:

```text
v.prev == u
```

La nueva representación hace algunas operaciones más fáciles, pero también introduce más referencias y más condiciones de correctitud.

### 12. El problema de los extremos

Una lista doble sin una técnica adicional debe tratar varios casos especiales.

```text
lista vacía
insertar el primer nodo
insertar antes del primero
insertar después del último
eliminar el primero
eliminar el último
eliminar el único nodo
```

Muchos de estos casos aparecen porque el primer nodo no tiene un predecesor real y el último no tiene un sucesor real.

Una forma de uniformizar la representación consiste en introducir un nodo centinela.

### 13. Nodo centinela dummy

La implementación estudiada utiliza:

```java
private final Node dummy = new Node(0);
```

El nodo `dummy` es estructural.

Su valor no pertenece a la secuencia lógica.

En una lista vacía:

```java
dummy.next = dummy;
dummy.prev = dummy;
```

Conceptualmente:

```text
     +-------+
     |       |
     v       |
   dummy ----+
```

En ambos sentidos se vuelve al mismo nodo.

Si la secuencia contiene:

```text
10, 20, 30
```

podemos imaginar:

```text
dummy <-> 10 <-> 20 <-> 30 <-> dummy
```

El primer nodo real tiene como predecesor a `dummy`.

El último nodo real tiene como sucesor a `dummy`.

#### Qué gana la representación

Con el centinela:

```text
dummy.next
```

representa el primer nodo real cuando la lista no está vacía.

También:

```text
dummy.prev
```

representa el último nodo real.

Cuando está vacía ambos vuelven a `dummy`.

Esto reduce el número de casos especiales porque todos los nodos reales tienen un predecesor y un sucesor dentro de la estructura enlazada.

### 14. Invariantes bidireccionales

La lista doblemente enlazada necesita invariantes más fuertes.

La implementación puede exigir:

```text
dummy.next.prev == dummy
dummy.prev.next == dummy
```

Estas condiciones expresan coherencia en los extremos.

Para un nodo real `u` podemos razonar de forma general:

```text
u.next.prev == u
u.prev.next == u
```

La primera condición dice:

```text
avanzo por next
regreso por prev
vuelvo al mismo nodo
```

La segunda dice:

```text
retrocedo por prev
avanzo por next
vuelvo al mismo nodo
```

#### Una estructura puede romperse en una sola dirección

Supongamos que modificamos:

```java
u.next = v;
```

pero olvidamos actualizar una referencia `prev`.

Es posible que un recorrido hacia adelante todavía parezca correcto. Sin embargo, un recorrido hacia atrás puede producir una secuencia distinta.

Por eso una lista doble debe comprobar consistencia bidireccional.

La correctitud no consiste solamente en que `toString()` produzca una salida aparentemente correcta.

También deben preservarse los invariantes internos.

### 15. Acceso por índice en una lista enlazada

En un arreglo:

```java
a[i]
```

permite localizar directamente una posición.

En una lista enlazada el índice no contiene una referencia al nodo.

Si queremos el elemento de índice 3 debemos recorrer nodos.

En una lista simple:

```text
head
 |
 v
0 -> 1 -> 2 -> 3
```

para llegar a 3 comenzamos desde `head`.

El acceso por índice puede ser lineal.

#### Dos extremos en DLList

Una lista doble tiene una ventaja.

Podemos comenzar desde el inicio o desde el final.

Supongamos:

```text
índices

0 <-> 1 <-> 2 <-> 3 <-> 4 <-> 5 <-> 6
```

Si queremos `i = 1`, conviene comenzar por el inicio.

Si queremos `i = 5`, conviene comenzar por el final.

La localización puede aprovechar:

```text
distancia desde el inicio
i

distancia desde el final
n - 1 - i
```

y recorrer desde el extremo más cercano.

### 16. getNode(i) y localización

Un método auxiliar puede convertir un índice en una referencia a un nodo.

Una implementación posible es:

```java
private Node getNode(int i) {
    if (i < 0 || i >= n) {
        throw new IndexOutOfBoundsException(
                "índice=" + i + ", tamaño=" + n);
    }

    Node p;

    if (i < n / 2) {
        p = dummy.next;

        for (int j = 0; j < i; j++) {
            p = p.next;
        }
    } else {
        p = dummy.prev;

        for (int j = n - 1; j > i; j--) {
            p = p.prev;
        }
    }

    return p;
}
```

El método primero valida el índice. Después decide desde qué extremo comenzar.

Finalmente avanza o retrocede hasta alcanzar el nodo.

#### Costo de localización

El número de pasos depende de la distancia al extremo elegido.

Podemos describirlo mediante:

```text
getNode(i) -> O(1 + min(i, n - i))
```

La constante adicional no es la idea principal.

La idea importante es:

```text
cerca del inicio
    pocos pasos desde el inicio

cerca del final
    pocos pasos desde el final

cerca del centro
    puede requerir un número de pasos proporcional a n
```

Por tanto, una `DLList` no recupera el acceso directo del arreglo.

```text
arreglo
get(i) -> O(1)

DLList
get(i) -> O(1 + min(i, n - i))
```

### 17. Localizar no es lo mismo que modificar

Esta distinción organiza gran parte del análisis de las listas enlazadas.

Una operación puede dividirse conceptualmente en dos fases:

```text
1. localizar dónde operar

2. modificar los enlaces
```

En un arreglo dinámico ocurre algo diferente.

Localizar el índice es barato:

```text
O(1)
```

pero insertar puede exigir desplazar:

```text
O(n)
```

En una lista doble puede ocurrir lo contrario.

Localizar el nodo puede costar:

```text
O(n)
```

pero una vez que tenemos la referencia correcta, modificar enlaces puede costar:

```text
O(1)
```

Esta diferencia explica por qué no debemos afirmar simplemente:

```text
las listas insertan en O(1)
```

Esa afirmación solo es correcta si el lugar de inserción ya está localizado mediante una referencia apropiada.

### 18. Insertar antes de un nodo conocido

Supongamos:

```text
A <-> W
```

Queremos insertar un nuevo nodo `U` antes de `W`.

El resultado debe ser:

```text
A <-> U <-> W
```

Primero creamos:

```java
Node u = new Node(x);
```

Luego definimos los vecinos de `u`:

```java
u.prev = w.prev;
u.next = w;
```

En ese momento `u` ya conoce a `A` y `W`.

Pero todavía falta que `A` y `W` reconozcan a `u`.

Por eso:

```java
u.next.prev = u;
u.prev.next = u;
```

Finalmente se incrementa el tamaño.

Una implementación es:

```java
private Node addBefore(Node w, int x) {
    Node u = new Node(x);

    u.prev = w.prev;
    u.next = w;

    u.next.prev = u;
    u.prev.next = u;

    n++;
    return u;
}
```

#### Cuatro relaciones locales

Antes:

```text
A.next = W
W.prev = A
```

Después:

```text
A.next = U
U.prev = A
U.next = W
W.prev = U
```

La cantidad de enlaces modificados no depende de `n`.

Por tanto, si `W` ya está localizado:

```text
addBefore(W, x) -> O(1)
```

### 19. Inserción por índice

Una operación como:

```java
add(i, x)
```

recibe un índice, no una referencia a nodo.

Por ello debe resolver dos problemas.

```text
localizar el nodo correspondiente a i

insertar el nuevo nodo
```

Si `i == n`, queremos insertar al final.

Con `dummy` podemos utilizar el propio centinela como posición estructural posterior al último elemento.

Conceptualmente:

```text
insertar antes de dummy
```

equivale a insertar al final.

Una implementación puede seguir la forma:

```java
void add(int i, int x) {
    if (i < 0 || i > n) {
        throw new IndexOutOfBoundsException(
                "posición=" + i + ", tamaño=" + n);
    }

    addBefore(i == n ? dummy : getNode(i), x);
}
```

Esto muestra otra ventaja del centinela.

El mismo procedimiento local `addBefore` puede utilizarse para posiciones internas y para el extremo final.

### 20. Eliminar un nodo conocido

Supongamos:

```text
A <-> W <-> B
```

Queremos eliminar `W`.

No necesitamos desplazar `A` ni `B`.

Queremos producir:

```text
A <------> B
```

Para ello:

```java
w.prev.next = w.next;
w.next.prev = w.prev;
```

La primera asignación hace:

```text
A.next = B
```

La segunda:

```text
B.prev = A
```

El nodo `W` deja de formar parte de la cadena lógica.

Si ya conocemos la referencia `W`, la modificación es local.

```text
desconectar nodo conocido -> O(1)
```

### 21. remove(i) y costo total

La operación:

```java
remove(i)
```

no recibe directamente el nodo.

Recibe un índice.

Por eso primero debe ejecutar una localización equivalente a:

```java
Node w = getNode(i);
```

Después guarda el valor:

```java
int x = w.x;
```

y desconecta:

```java
w.prev.next = w.next;
w.next.prev = w.prev;
```

Finalmente:

```java
n--;
```

Una implementación puede ser:

```java
int remove(int i) {
    Node w = getNode(i);
    int x = w.x;

    w.prev.next = w.next;
    w.next.prev = w.prev;

    n--;
    return x;
}
```

#### Separar los costos

La operación tiene dos componentes:

```text
localizar w
    O(1 + min(i, n - i))

desconectar w
    O(1)
```

Por tanto:

```text
remove(i) -> O(1 + min(i, n - i))
```

El costo total está dominado por la localización.

Este ejemplo resume una de las ideas centrales de la semana:

```text
una modificación local puede ser O(1)

pero una operación por índice puede no ser O(1)
porque primero hay que encontrar el nodo
```

### 22. El orden de actualización de referencias importa

Las referencias son parte del estado de la estructura.

Por ello, actualizar enlaces no debe verse como una colección arbitraria de asignaciones.

Cada asignación modifica qué nodos siguen siendo alcanzables.

Supongamos una lista simple:

```text
U -> V -> W
```

Si queremos insertar un nuevo nodo `X` después de `U`, necesitamos conservar el acceso a `V`.

Una estrategia conceptual correcta es:

```text
X.next = U.next
U.next = X
```

Primero `X` conserva la referencia a la continuación de la lista.

Después `U` pasa a apuntar a `X`.

Si se sobrescribe una referencia importante antes de conservarla, una parte de la estructura podría volverse inaccesible.

#### Pensar antes, durante y después

Para cada operación enlazada conviene razonar mediante tres estados:

```text
antes
    qué nodos existen
    qué referencias son válidas

cambio
    qué enlaces deben modificarse
    en qué orden pueden modificarse

después
    qué secuencia debe observarse
    qué invariantes deben seguir siendo verdaderos
```

Esta forma de razonamiento es más importante que memorizar líneas específicas de Java.

### 23. Casos frontera y correctitud

Las listas enlazadas concentran muchos errores en estados pequeños.

Por ejemplo:

```text
lista vacía
lista con un nodo
lista con dos nodos
inserción en la cabecera
inserción en la cola
eliminación de la cabecera
eliminación de la cola
eliminación del único nodo
```

Una implementación que funciona con diez nodos puede fallar con uno.

#### Por qué los estados pequeños son importantes

Considera `SLList.pop()`.

Con tres nodos:

```text
10 -> 20 -> 30
```

hacer avanzar `head` parece suficiente.

Pero con un nodo:

```text
10
```

además debemos actualizar `tail`.

Ese caso no es una excepción irrelevante.

Forma parte de la especificación de la estructura.

Los invariantes ayudan a descubrirlo.

Si después de una operación:

```text
n == 0
```

pero:

```text
tail != null
```

sabemos que el estado es inválido.

### 24. Complejidad de las operaciones fundamentales

Podemos resumir varias operaciones de la semana.

| Operación | Representación | Costo |
|---|---|---|
| `size()` | `SLList` o `DLList` con `n` | O(1) |
| `SLList.add(x)` con `tail` | lista simple | O(1) |
| `SLList.push(x)` | lista simple | O(1) |
| `SLList.pop()` | lista simple | O(1) |
| eliminar desde `tail` sin `prev` | lista simple | O(n) |
| `DLList.getNode(i)` | lista doble | O(1 + min(i, n - i)) |
| `DLList.get(i)` | lista doble | O(1 + min(i, n - i)) |
| insertar antes de nodo conocido | lista doble | O(1) |
| eliminar nodo conocido | lista doble | O(1) |
| `DLList.remove(i)` | lista doble | O(1 + min(i, n - i)) |

Esta tabla no debe memorizarse sin explicación.

Cada costo debe relacionarse con el trabajo realizado.

```text
O(1)
    cantidad constante de referencias o accesos

O(n)
    recorrido potencialmente proporcional a n
```

### 25. Comparación con el arreglo dinámico

Ahora podemos comparar las dos semanas.

#### Acceso por índice

Arreglo dinámico:

```text
get(i) -> O(1)
```

Lista doble:

```text
get(i) -> O(1 + min(i, n - i))
```

El arreglo favorece claramente el acceso por posición.

#### Inserción al inicio

Arreglo dinámico:

```text
desplazar elementos
O(n)
```

Lista simple con `head`:

```text
modificar pocas referencias
O(1)
```

La lista favorece esta modificación.

#### Inserción al final

Arreglo dinámico:

```text
O(1) amortizado
```

porque ocasionalmente ejecuta `resize()`.

`SLList` con `tail`:

```text
O(1)
```

porque crea un nuevo nodo y modifica referencias.

#### Eliminación de un nodo conocido

En un arreglo no basta con eliminar conceptualmente una posición.

Los elementos posteriores suelen desplazarse para mantener la representación compacta.

En una lista doble, si ya tenemos la referencia al nodo:

```text
A <-> W <-> B
```

podemos conectar:

```text
A <-> B
```

modificando una cantidad constante de enlaces.

#### La pregunta correcta

No debemos preguntar:

```text
¿qué estructura es mejor?
```

sin especificar el problema.

Debemos preguntar:

```text
¿qué operaciones serán frecuentes?,

¿necesitamos acceso por índice?,

¿operaremos principalmente en extremos?,

¿ya tendremos referencias a los nodos?,

¿qué invariantes estamos dispuestos a mantener?.
```

La respuesta determina qué representación resulta más apropiada.

### 26. Tiempo, espacio y complejidad estructural

La comparación no se limita al tiempo.

Un arreglo almacena sus elementos dentro de un bloque de respaldo.

Una lista simple necesita, además del dato, una referencia `next` por nodo.

Una lista doble necesita:

```text
dato
prev
next
```

Por tanto, la mayor flexibilidad de la lista doble tiene un costo estructural.

```text
más referencias
más enlaces que mantener
más posibilidades de inconsistencia
```

Esto no significa que la lista doble sea una mala estructura.

Significa que cada ventaja tiene un costo asociado.

#### No existe una representación universalmente superior

- Una lista simple puede ser suficiente si las operaciones se concentran en la cabecera y la cola apropiadas.
- Una lista doble puede ser preferible cuando necesitamos recorrer en ambas direcciones o modificar eficientemente alrededor de nodos ya localizados.
- Un arreglo dinámico puede ser preferible cuando el acceso por índice es frecuente y las modificaciones internas son poco comunes.

La estructura debe elegirse según el patrón de uso.

### 27. Referencia conocida frente a índice conocido

Esta distinción merece atención especial.

Supongamos que queremos eliminar un elemento de una `DLList`.

#### Caso A, conocemos el índice

```text
i = 500
```

Primero debemos convertir ese índice en una referencia.

Eso requiere recorrido.

#### Caso B, ya conocemos el nodo

```text
Node w
```

Ahora podemos modificar directamente:

```java
w.prev.next = w.next;
w.next.prev = w.prev;
```

La diferencia es fundamental.

```text
índice conocido
    todavía hay que localizar

referencia conocida
    podemos modificar localmente
```

Por eso frases como:

```text
insertar en una lista enlazada es O(1)
```

deben utilizarse con precisión.

Una formulación mejor es:

```text
insertar o eliminar alrededor de un nodo ya localizado
puede realizarse en O(1)
```

### 28. La representación como herramienta de razonamiento

Hasta este punto hemos estudiado dos formas de representar una secuencia.

Semana 1:

```text
a
n
```

Semana 2, lista simple:

```text
head
tail
n
nodos con next
```

Semana 2, lista doble:

```text
dummy
n
nodos con prev y next
```

Cada representación introduce:

```text
un estado interno
un conjunto de invariantes
operaciones naturales
operaciones costosas
```

Esta perspectiva es más importante que cualquier clase concreta.

Cuando aparezca una nueva estructura de datos conviene comenzar siempre por las mismas preguntas.

```text
¿qué representa cada campo?

¿qué estados son válidos?

¿qué debe permanecer verdadero?

¿cómo se localiza el lugar de trabajo?

¿qué cambia una operación?

¿cuánto trabajo realiza?
```

### 29. Síntesis

Una lista enlazada representa una secuencia mediante nodos conectados por referencias. En una `SLList` cada nodo conoce al siguiente mediante `next`.

La estructura puede mantener:

```text
head
tail
n
```

para localizar directamente los extremos y conocer el tamaño.

Las operaciones `push(x)` y `pop()` sobre la cabecera pueden realizarse en O(1). Agregar al final también puede ser O(1) cuando se mantiene `tail`. Sin embargo, `tail` no permite conocer directamente su predecesor.

Eliminar desde el final de una lista simplemente enlazada puede requerir un recorrido O(n).

Una `DLList` agrega `prev` y permite recorrer la secuencia en ambos sentidos.

El nodo centinela `dummy` uniformiza el tratamiento de los extremos y reduce casos especiales.

Los enlaces de una lista doble deben mantener consistencia bidireccional.

La localización por índice no es directa.

`getNode(i)` puede comenzar desde el extremo más cercano y tiene costo:

```text
O(1 + min(i, n - i))
```

Una vez conocido un nodo, insertar o eliminar localmente puede realizarse en O(1).

Por eso debemos distinguir:

```text
localización
```

de:

```text
modificación
```

La comparación con los arreglos dinámicos puede resumirse así:

```text
arreglo dinámico
    acceso por índice barato
    actualización interna puede mover elementos

lista enlazada
    acceso por índice requiere recorrido
    actualización local puede cambiar pocas referencias
```

La idea final de la Semana 2 es:

```text
la representación determina
qué información está disponible directamente

esa información determina
cómo se implementan las operaciones

las operaciones deben preservar invariantes

y el trabajo necesario determina
la complejidad
```

Una estructura de datos no debe estudiarse como una colección de métodos aislados.

Debe estudiarse como una relación entre:

```text
representación
operaciones
invariantes
localización
modificación
complejidad
```
