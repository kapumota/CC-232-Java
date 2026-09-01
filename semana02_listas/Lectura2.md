### Lectura: listas enlazadas, referencias y costo de las operaciones

Esta lectura continúa las ideas desarrolladas en la Semana 1 de CC232. En la semana anterior una secuencia se representó mediante un arreglo de respaldo y un tamaño lógico. Esa representación proporcionó acceso directo por índice, pero también mostró una limitación importante: insertar o eliminar en posiciones internas puede exigir desplazar muchos elementos.

La Semana 2 estudia otra forma de representar una secuencia. En lugar de exigir que los elementos ocupen posiciones contiguas de un arreglo, cada elemento se almacena dentro de un **nodo** y la secuencia se reconstruye mediante **referencias** entre nodos.

El objetivo no es memorizar una implementación particular, sino comprender cómo una decisión de representación modifica la forma de localizar un elemento, las referencias que deben conservarse, los **invariantes** de la estructura, el costo de insertar y eliminar, y el costo de acceder por posición.

La idea central de la semana puede anticiparse así. En un **arreglo dinámico** la localización por índice es barata, pero la modificación interna puede ser costosa. En una **lista enlazada** la localización por índice puede ser costosa, pero la modificación local puede ser barata. Esta diferencia será el punto de partida para estudiar listas simplemente enlazadas y listas doblemente enlazadas.

### 1. Cambiar la representación cambia el costo

Considera la secuencia `10, 20, 30, 40`. En la Semana 1 una posible representación era:

```text
índice      0    1    2    3    4    5    6    7
          +----+----+----+----+----+----+----+----+
a         | 10 | 20 | 30 | 40 |    |    |    |    |
          +----+----+----+----+----+----+----+----+

n = 4
```

Esta representación hace que obtener el elemento de índice 2 sea directo (`a[2]`); no es necesario examinar `a[0]` ni `a[1]`. Por eso `get(i)` tiene costo **O(1)**.

Sin embargo, supongamos que queremos insertar `5` al inicio. Para conservar el orden lógico debemos transformar `[10, 20, 30, 40]` en `[5, 10, 20, 30, 40]`. En un arreglo esto exige abrir espacio desplazando 40, luego 30, luego 20 y luego 10. El número de desplazamientos depende de `n`, así que insertar al inicio puede costar **O(n)**.

Esta observación motiva una pregunta: ¿es posible representar la misma secuencia sin exigir almacenamiento contiguo? Sí. Podemos almacenar cada elemento dentro de un objeto separado y conectar esos objetos mediante referencias:

```text
[10] -> [20] -> [30] -> [40]
```

Ahora insertar delante del primer nodo no requiere desplazar físicamente 10, 20, 30 y 40. Solo necesitamos modificar algunas referencias, y la operación puede volverse más barata. Pero esta nueva representación también pierde algo: ya no existe una posición física calculable directamente como `a[i]`. Para localizar un nodo interno debemos seguir enlaces.

Esta es una de las lecciones más importantes del estudio de estructuras de datos: **una representación no es mejor en términos absolutos**; una representación favorece ciertas operaciones y hace más costosas otras.

### 2. Orden lógico y almacenamiento

Una secuencia tiene un orden lógico. Por ejemplo, 10 va antes que 20, 20 antes que 30 y 30 antes que 40. En un arreglo, ese orden lógico se refleja directamente en posiciones consecutivas:

```text
a[0] = 10
a[1] = 20
a[2] = 30
a[3] = 40
```

En una lista enlazada no necesitamos esa correspondencia física. Podemos pensar conceptualmente en `10 -> 20 -> 30 -> 40 -> null`. Lo importante es que cada nodo permita encontrar al siguiente. El orden se conserva mediante enlaces, no mediante contigüidad.

#### Continuidad física y continuidad lógica

En una representación basada en arreglos existe una fuerte relación entre la posición lógica y la posición física. En una lista enlazada esa relación desaparece: dos nodos consecutivos desde el punto de vista lógico no necesitan encontrarse juntos físicamente.

Para esta semana no necesitamos estudiar direcciones reales de memoria. Basta trabajar con referencias Java. La secuencia queda definida por la posibilidad de seguir correctamente esas referencias.

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

Este objeto contiene dos tipos de información: `x` es el **dato lógico** que pertenece al contenido que queremos almacenar, y `next` es **información estructural** que permite construir la estructura.

Si tenemos:

```text
head
 |
 v
+----+------+     +----+------+     +----+------+
| 10 | next | --> | 20 | next | --> | 30 | null |
+----+------+     +----+------+     +----+------+
```

los valores lógicos son 10, 20 y 30, y los enlaces determinan su orden.

#### Qué significa una referencia

Si una variable `Node u;` contiene una referencia a un nodo, podemos acceder a sus campos con `u.x` y `u.next`. Si `u.next` es otro nodo, podemos continuar con `u.next.next`. Si `u.next` es `null`, hemos alcanzado el final de una lista simplemente enlazada.

**La referencia permite llegar a otro objeto.** No significa que ese objeto esté necesariamente colocado físicamente al lado del anterior.

### 4. Representación de una lista simplemente enlazada

Una lista simplemente enlazada (**SLList**) puede mantener tres campos estructurales:

```java
private Node head;
private Node tail;
private int n;
```

Cada campo tiene una función diferente: `head` es la referencia al primer nodo, `tail` es la referencia al último nodo, y `n` es el número de elementos lógicos. Por ejemplo:

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

El tamaño es 3. No necesitamos recorrer la lista para responder `size()` porque `n` ya almacena esa información. Por tanto, `size()` tiene costo **O(1)**.

#### Por qué guardar tail

Una implementación podría almacenar solamente `head` y `n`. En ese caso, para encontrar el último nodo habría que comenzar en `head` y seguir `next` hasta llegar al nodo cuyo `next` sea `null`. Ese recorrido sería lineal. Guardar `tail` introduce una referencia adicional, pero permite conocer directamente el último nodo. Esta pequeña decisión de representación cambia el costo de insertar al final.

### 5. Invariantes de SLList

Una estructura enlazada necesita reglas que permitan reconocer cuándo su estado interno es válido. Para una lista vacía esperamos `n = 0`, `head = null` y `tail = null`; es decir, `n == 0` implica `head == null` y `tail == null`.

Cuando la lista contiene elementos (`n > 0`) esperamos `head != null`, `tail != null` y `tail.next == null`. Además, si comenzamos en `head` y seguimos `next`, debemos encontrar exactamente los `n` nodos que pertenecen a la secuencia.

#### El tamaño debe coincidir con la cadena

Supongamos `head -> 10 -> 20 -> null` con `n = 3`. El estado no es coherente: la variable `n` afirma que existen tres elementos, pero solamente dos nodos son alcanzables desde `head`. Un invariante útil no controla solamente variables individuales; también expresa relaciones entre ellas. En este caso, `head`, `tail`, `n` y la cadena de referencias `next` deben describir la misma estructura lógica.

#### Casos frontera

Los casos que más fácilmente rompen un invariante son insertar en una lista vacía, eliminar de una lista de un elemento, pasar de vacía a no vacía y pasar de no vacía a vacía. Por eso esos casos deben razonarse explícitamente.

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

Queremos agregar 30. Creamos `Node u = new Node(30);`. Después el antiguo `tail` debe apuntar al nuevo nodo con `tail.next = u;`, y finalmente `tail = u; n++;`. El resultado conceptual es:

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

Si `n = 0`, `head = null` y `tail = null`, no podemos ejecutar `tail.next = u;` porque no existe un nodo referenciado por `tail`. El primer nodo debe convertirse simultáneamente en cabecera y cola. Una implementación completa puede ser:

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

Después de insertar el primer elemento, el mismo nodo es primero y último (`head` y `tail` apuntan a 10, con `n = 1`).

#### Complejidad

La operación no recorre los `n` nodos; modifica una cantidad constante de referencias. Por tanto, `add(x)` al final con `tail` tiene costo **O(1)**. Aquí aparece una diferencia interesante con el arreglo dinámico: mientras `add` al final en un arreglo dinámico es **O(1) amortizado** (porque ocasionalmente ejecuta `resize()`), en una `SLList` con `tail` es **O(1)** sin amortización, porque cada nuevo elemento crea su propio nodo.

### 7. Insertar en la cabecera con push

Ahora queremos insertar un nodo al inicio. Estado inicial: `head -> 20 -> 30 -> null` con `tail` en 30. Queremos obtener `head -> 10 -> 20 -> 30 -> null` con `tail` en 30.

Creamos `Node u = new Node(10);`. El nuevo nodo debe apuntar a la antigua cabecera con `u.next = head;`, y luego debe convertirse en la nueva cabecera con `head = u;`.

#### El orden de las actualizaciones importa

Primero `u.next = head;` conserva el acceso a la antigua lista. Después `head = u;` cambia el punto de entrada. La idea conceptual es conectar primero y cambiar la entrada después. Si modificamos referencias sin comprender qué parte de la estructura necesitamos conservar, podemos perder acceso a nodos que todavía pertenecen a la lista.

#### Caso vacío

Si la lista estaba vacía, el nuevo nodo también debe convertirse en `tail`. Una implementación es:

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

El número de operaciones estructurales no depende de `n`, así que `push(x)` tiene costo **O(1)**.

### 8. Eliminar desde la cabecera con pop

Supongamos `head -> 10 -> 20 -> 30 -> null` con `tail` en 30 y `n = 3`. Queremos retirar el primer nodo. Primero debemos conservar el valor con `int x = head.x;`, y después hacemos avanzar la cabecera con `head = head.next;`. El resultado es `head -> 20 -> 30 -> null`. No se han desplazado 20 ni 30; solamente cambió la referencia que indica dónde empieza la lista.

Si no existen nodos (`n = 0`), no podemos leer `head.x` porque `head` es `null`; la implementación debe resolver primero ese caso.

#### Eliminar el único nodo

Supongamos `head -> 10 -> null` con `tail` también en 10 y `n = 1`. Después de eliminar 10 queremos `head = null`, `tail = null` y `n = 0`. Si hacemos solamente `head = head.next; n--;`, obtenemos `head = null` y `n = 0`, pero `tail` todavía podría apuntar al antiguo nodo. Eso violaría el invariante. Por ello debemos actualizar `tail` cuando la eliminación deja vacía la lista. Una implementación es:

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

La operación modifica una cantidad constante de referencias, así que `pop()` tiene costo **O(1)**.

### 9. SLList y operaciones en los extremos

Las operaciones anteriores muestran que una lista simplemente enlazada puede trabajar eficientemente sobre ciertos extremos: en la cabecera, insertar y eliminar cuestan **O(1)**; en el final, si almacenamos `tail`, insertar también cuesta **O(1)**. Esta propiedad permite entender por qué una `SLList` puede ser una representación apropiada para comportamientos de **pila** o de **cola**.

Una pila utiliza el mismo extremo para insertar y retirar: `push` y `pop` pueden trabajar sobre `head`. Una cola inserta por un extremo y elimina por el otro: insertar por `tail` y eliminar por `head` puede realizarse en tiempo constante si ambas referencias se mantienen correctamente.

La idea importante no es estudiar todavía todas las operaciones formales de `Stack` y `Queue`, sino observar que una representación puede ser especialmente adecuada cuando el patrón de operaciones coincide con los puntos que la estructura mantiene localizados.

### 10. La limitación fundamental de SLList

Guardar `tail` permite conocer directamente el último nodo, pero conocer el último nodo no significa conocer su predecesor. Considera `head -> 10 -> 20 -> 30 -> 40 -> null` con `tail` en 40. Queremos eliminar 40. Después de la eliminación, `tail` debería apuntar a 30, pero el nodo 40 no almacena información sobre 30: tenemos `30.next -> 40`, pero no tenemos `40.prev -> 30`.

Para encontrar el nodo anterior a `tail` debemos comenzar en `head` y recorrer 10, 20, 30 hasta encontrar el nodo cuyo `next` sea `tail`. En el peor caso, ese recorrido examina una cantidad de nodos proporcional a `n`. Por tanto, eliminar desde `tail` en una `SLList` cuesta **O(n)**.

#### La limitación proviene de la representación

No se trata de un error de programación: la estructura simplemente no almacena la información necesaria para retroceder. Cada nodo conoce a su siguiente, pero no conoce a su anterior. Esta observación motiva una nueva representación.

### 11. Lista doblemente enlazada

Una lista doblemente enlazada (**DLList**) agrega una referencia hacia el nodo anterior:

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

Ahora cada nodo mantiene tres campos: `x` (dato), `next` (siguiente nodo) y `prev` (nodo anterior). Conceptualmente, `10 <-> 20 <-> 30`. Podemos avanzar siguiendo `next` y retroceder siguiendo `prev`.

#### Más información, más responsabilidad

Agregar `prev` proporciona más flexibilidad, pero también aumenta el número de relaciones que deben mantenerse correctamente. En una lista simple bastaba preservar el recorrido hacia adelante; en una lista doble debemos mantener coherencia en ambos sentidos. Si `u.next == v`, esperamos que `v.prev == u`. La nueva representación hace algunas operaciones más fáciles, pero también introduce más referencias y más condiciones de correctitud.

### 12. El problema de los extremos

Una lista doble sin una técnica adicional debe tratar varios casos especiales: lista vacía, insertar el primer nodo, insertar antes del primero, insertar después del último, eliminar el primero, eliminar el último y eliminar el único nodo. Muchos de estos casos aparecen porque el primer nodo no tiene un predecesor real y el último no tiene un sucesor real. Una forma de uniformizar la representación consiste en introducir un **nodo centinela**.

### 13. Nodo centinela dummy

La implementación estudiada utiliza `private final Node dummy = new Node(0);`. El nodo `dummy` es estructural; su valor no pertenece a la secuencia lógica. En una lista vacía, `dummy.next = dummy;` y `dummy.prev = dummy;`. Conceptualmente:

```text
     +-------+
     |       |
     v       |
   dummy ----+
```

En ambos sentidos se vuelve al mismo nodo. Si la secuencia contiene 10, 20, 30, podemos imaginar `dummy <-> 10 <-> 20 <-> 30 <-> dummy`. El primer nodo real tiene como predecesor a `dummy`, y el último nodo real tiene como sucesor a `dummy`.

#### Qué gana la representación

Con el centinela, `dummy.next` representa el primer nodo real cuando la lista no está vacía, y `dummy.prev` representa el último nodo real. Cuando la lista está vacía, ambos vuelven a `dummy`. Esto reduce el número de casos especiales, porque todos los nodos reales tienen un predecesor y un sucesor dentro de la estructura enlazada.

### 14. Invariantes bidireccionales

La lista doblemente enlazada necesita invariantes más fuertes. La implementación puede exigir `dummy.next.prev == dummy` y `dummy.prev.next == dummy`; estas condiciones expresan coherencia en los extremos. Para un nodo real `u` podemos razonar de forma general: `u.next.prev == u` (avanzo por `next`, regreso por `prev`, vuelvo al mismo nodo) y `u.prev.next == u` (retrocedo por `prev`, avanzo por `next`, vuelvo al mismo nodo).

#### Una estructura puede romperse en una sola dirección

Supongamos que modificamos `u.next = v;` pero olvidamos actualizar una referencia `prev`. Es posible que un recorrido hacia adelante todavía parezca correcto, pero un recorrido hacia atrás puede producir una secuencia distinta. Por eso una lista doble debe comprobar consistencia bidireccional: la correctitud no consiste solamente en que `toString()` produzca una salida aparentemente correcta, también deben preservarse los invariantes internos.

### 15. Acceso por índice en una lista enlazada

En un arreglo, `a[i]` permite localizar directamente una posición. En una lista enlazada el índice no contiene una referencia al nodo. Si queremos el elemento de índice 3 debemos recorrer nodos empezando desde `head`, así que el acceso por índice puede ser lineal.

#### Dos extremos en DLList

Una lista doble tiene una ventaja: podemos comenzar desde el inicio o desde el final. Si queremos el índice 1 de una secuencia de siete elementos, conviene comenzar por el inicio; si queremos el índice 5, conviene comenzar por el final. La localización puede aprovechar la distancia desde el inicio (`i`) o la distancia desde el final (`n - 1 - i`) y recorrer desde el extremo más cercano.

### 16. getNode(i) y localización

Un método auxiliar puede convertir un índice en una referencia a un nodo. Una implementación posible es:

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

El método primero valida el índice, después decide desde qué extremo comenzar, y finalmente avanza o retrocede hasta alcanzar el nodo.

#### Costo de localización

El número de pasos depende de la distancia al extremo elegido, así que `getNode(i)` tiene costo **O(1 + min(i, n - i))**. La idea importante es que cerca del inicio se necesitan pocos pasos desde el inicio, cerca del final se necesitan pocos pasos desde el final, y cerca del centro puede requerirse un número de pasos proporcional a `n`. Por tanto, una `DLList` no recupera el acceso directo del arreglo: mientras `get(i)` en un arreglo es **O(1)**, en una `DLList` es **O(1 + min(i, n - i))**.

### 17. Localizar no es lo mismo que modificar

Esta distinción organiza gran parte del análisis de las listas enlazadas. Una operación puede dividirse conceptualmente en dos fases: localizar dónde operar y modificar los enlaces.

En un arreglo dinámico ocurre algo diferente: localizar el índice es barato (**O(1)**), pero insertar puede exigir desplazar (**O(n)**). En una lista doble puede ocurrir lo contrario: localizar el nodo puede costar **O(n)**, pero una vez que tenemos la referencia correcta, modificar enlaces puede costar **O(1)**.

Esta diferencia explica por qué no debemos afirmar simplemente que "las listas insertan en O(1)". Esa afirmación solo es correcta si el lugar de inserción ya está localizado mediante una referencia apropiada.

### 18. Insertar antes de un nodo conocido

Supongamos `A <-> W`. Queremos insertar un nuevo nodo `U` antes de `W`, para obtener `A <-> U <-> W`. Primero creamos `Node u = new Node(x);`. Luego definimos los vecinos de `u` con `u.prev = w.prev; u.next = w;`. En ese momento `u` ya conoce a `A` y `W`, pero todavía falta que `A` y `W` reconozcan a `u`, por eso `u.next.prev = u; u.prev.next = u;`. Finalmente se incrementa el tamaño. Una implementación es:

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

Antes teníamos `A.next = W` y `W.prev = A`. Después tenemos `A.next = U`, `U.prev = A`, `U.next = W` y `W.prev = U`. La cantidad de enlaces modificados no depende de `n`. Por tanto, si `W` ya está localizado, `addBefore(W, x)` tiene costo **O(1)**.

### 19. Inserción por índice

Una operación como `add(i, x)` recibe un índice, no una referencia a nodo. Por ello debe resolver dos problemas: localizar el nodo correspondiente a `i` e insertar el nuevo nodo. Si `i == n`, queremos insertar al final. Con `dummy` podemos utilizar el propio centinela como posición estructural posterior al último elemento: insertar antes de `dummy` equivale a insertar al final. Una implementación puede seguir la forma:

```java
void add(int i, int x) {
    if (i < 0 || i > n) {
        throw new IndexOutOfBoundsException(
                "posición=" + i + ", tamaño=" + n);
    }

    addBefore(i == n ? dummy : getNode(i), x);
}
```

Esto muestra otra ventaja del centinela: el mismo procedimiento local `addBefore` puede utilizarse para posiciones internas y para el extremo final.

### 20. Eliminar un nodo conocido

Supongamos `A <-> W <-> B`. Queremos eliminar `W` sin desplazar `A` ni `B`, para producir `A <-> B`. Para ello, `w.prev.next = w.next;` hace `A.next = B`, y `w.next.prev = w.prev;` hace `B.prev = A`. El nodo `W` deja de formar parte de la cadena lógica. Si ya conocemos la referencia `W`, la modificación es local, así que desconectar un nodo conocido tiene costo **O(1)**.

### 21. remove(i) y costo total

La operación `remove(i)` no recibe directamente el nodo, sino un índice. Por eso primero debe ejecutar una localización equivalente a `Node w = getNode(i);`. Después guarda el valor con `int x = w.x;` y desconecta con `w.prev.next = w.next; w.next.prev = w.prev;`. Finalmente `n--;`. Una implementación puede ser:

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

La operación tiene dos componentes: localizar `w`, con costo **O(1 + min(i, n - i))**, y desconectar `w`, con costo **O(1)**. Por tanto, `remove(i)` tiene costo **O(1 + min(i, n - i))**; el costo total está dominado por la localización. Este ejemplo resume una de las ideas centrales de la semana: una modificación local puede ser **O(1)**, pero una operación por índice puede no serlo, porque primero hay que encontrar el nodo.

### 22. El orden de actualización de referencias importa

Las referencias son parte del estado de la estructura, así que actualizar enlaces no debe verse como una colección arbitraria de asignaciones. Cada asignación modifica qué nodos siguen siendo alcanzables.

Supongamos una lista simple `U -> V -> W`. Si queremos insertar un nuevo nodo `X` después de `U`, necesitamos conservar el acceso a `V`. Una estrategia conceptual correcta es `X.next = U.next` primero, y luego `U.next = X`. Primero `X` conserva la referencia a la continuación de la lista; después `U` pasa a apuntar a `X`. Si se sobrescribe una referencia importante antes de conservarla, una parte de la estructura podría volverse inaccesible.

#### Pensar antes, durante y después

Para cada operación enlazada conviene razonar mediante tres estados: antes (qué nodos existen y qué referencias son válidas), cambio (qué enlaces deben modificarse y en qué orden pueden modificarse) y después (qué secuencia debe observarse y qué invariantes deben seguir siendo verdaderos). Esta forma de razonamiento es más importante que memorizar líneas específicas de Java.

### 23. Casos frontera y correctitud

Las listas enlazadas concentran muchos errores en estados pequeños, por ejemplo lista vacía, lista con un nodo, lista con dos nodos, inserción en la cabecera, inserción en la cola, eliminación de la cabecera, eliminación de la cola y eliminación del único nodo. Una implementación que funciona con diez nodos puede fallar con uno.

#### Por qué los estados pequeños son importantes

Considera `SLList.pop()`. Con tres nodos (`10 -> 20 -> 30`), hacer avanzar `head` parece suficiente. Pero con un nodo (`10`), además debemos actualizar `tail`. Ese caso no es una excepción irrelevante; forma parte de la especificación de la estructura. Los invariantes ayudan a descubrirlo: si después de una operación `n == 0` pero `tail != null`, sabemos que el estado es inválido.

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

Esta tabla no debe memorizarse sin explicación; cada costo debe relacionarse con el trabajo realizado. **O(1)** significa una cantidad constante de referencias o accesos, mientras que **O(n)** significa un recorrido potencialmente proporcional a `n`.

### 25. Comparación con el arreglo dinámico

Ahora podemos comparar las dos semanas.

**Acceso por índice.** En un arreglo dinámico, `get(i)` es **O(1)**. En una lista doble, `get(i)` es **O(1 + min(i, n - i))**. El arreglo favorece claramente el acceso por posición.

**Inserción al inicio.** En un arreglo dinámico hay que desplazar elementos, con costo **O(n)**. En una lista simple con `head` basta modificar pocas referencias, con costo **O(1)**. La lista favorece esta modificación.

**Inserción al final.** En un arreglo dinámico el costo es **O(1) amortizado**, porque ocasionalmente ejecuta `resize()`. En una `SLList` con `tail` el costo es **O(1)**, porque crea un nuevo nodo y modifica referencias.

**Eliminación de un nodo conocido.** En un arreglo no basta con eliminar conceptualmente una posición; los elementos posteriores suelen desplazarse para mantener la representación compacta. En una lista doble, si ya tenemos la referencia al nodo (`A <-> W <-> B`), podemos conectar `A <-> B` modificando una cantidad constante de enlaces.

#### La pregunta correcta

No debemos preguntar "¿qué estructura es mejor?" sin especificar el problema. Debemos preguntar qué operaciones serán frecuentes, si necesitamos acceso por índice, si operaremos principalmente en extremos, si ya tendremos referencias a los nodos, y qué invariantes estamos dispuestos a mantener. La respuesta determina qué representación resulta más apropiada.

### 26. Tiempo, espacio y complejidad estructural

La comparación no se limita al tiempo. Un arreglo almacena sus elementos dentro de un bloque de respaldo. Una lista simple necesita, además del dato, una referencia `next` por nodo. Una lista doble necesita dato, `prev` y `next`. Por tanto, la mayor flexibilidad de la lista doble tiene un costo estructural: más referencias, más enlaces que mantener y más posibilidades de inconsistencia. Esto no significa que la lista doble sea una mala estructura; significa que cada ventaja tiene un costo asociado.

#### No existe una representación universalmente superior

Una lista simple puede ser suficiente si las operaciones se concentran en la cabecera y la cola apropiadas. Una lista doble puede ser preferible cuando necesitamos recorrer en ambas direcciones o modificar eficientemente alrededor de nodos ya localizados. Un arreglo dinámico puede ser preferible cuando el acceso por índice es frecuente y las modificaciones internas son poco comunes. La estructura debe elegirse según el patrón de uso.

### 27. Referencia conocida frente a índice conocido

Esta distinción merece atención especial. Supongamos que queremos eliminar un elemento de una `DLList`.

**Caso A, conocemos el índice** (por ejemplo `i = 500`). Primero debemos convertir ese índice en una referencia, lo cual requiere recorrido.

**Caso B, ya conocemos el nodo** `Node w`. Ahora podemos modificar directamente `w.prev.next = w.next; w.next.prev = w.prev;`.

La diferencia es fundamental: con un índice conocido todavía hay que localizar; con una referencia conocida podemos modificar localmente. Por eso frases como "insertar en una lista enlazada es O(1)" deben utilizarse con precisión. Una formulación mejor es que insertar o eliminar alrededor de un nodo ya localizado puede realizarse en **O(1)**.

### 28. La representación como herramienta de razonamiento

Hasta este punto hemos estudiado dos formas de representar una secuencia. En la Semana 1 teníamos esencialmente `a` y `n`. En la Semana 2, una lista simple mantiene `head`, `tail`, `n` y nodos con `next`; una lista doble mantiene `dummy`, `n` y nodos con `prev` y `next`.

Cada representación introduce un estado interno, un conjunto de invariantes, operaciones naturales y operaciones costosas. Esta perspectiva es más importante que cualquier clase concreta. Cuando aparezca una nueva estructura de datos conviene comenzar siempre por las mismas preguntas: ¿qué representa cada campo?, ¿qué estados son válidos?, ¿qué debe permanecer verdadero?, ¿cómo se localiza el lugar de trabajo?, ¿qué cambia una operación? y ¿cuánto trabajo realiza?

### 29. Síntesis

Una lista enlazada representa una secuencia mediante nodos conectados por referencias. En una `SLList` cada nodo conoce al siguiente mediante `next`. La estructura puede mantener `head`, `tail` y `n` para localizar directamente los extremos y conocer el tamaño.

Las operaciones `push(x)` y `pop()` sobre la cabecera pueden realizarse en **O(1)**. Agregar al final también puede ser **O(1)** cuando se mantiene `tail`. Sin embargo, `tail` no permite conocer directamente su predecesor, y eliminar desde el final de una lista simplemente enlazada puede requerir un recorrido **O(n)**.

Una `DLList` agrega `prev` y permite recorrer la secuencia en ambos sentidos. El nodo centinela `dummy` uniformiza el tratamiento de los extremos y reduce casos especiales. Los enlaces de una lista doble deben mantener consistencia bidireccional.

La localización por índice no es directa: `getNode(i)` puede comenzar desde el extremo más cercano y tiene costo **O(1 + min(i, n - i))**. Una vez conocido un nodo, insertar o eliminar localmente puede realizarse en **O(1)**. Por eso debemos distinguir entre **localización** y **modificación**.

La comparación con los arreglos dinámicos puede resumirse así: en un arreglo dinámico el acceso por índice es barato pero la actualización interna puede mover elementos; en una lista enlazada el acceso por índice requiere recorrido pero la actualización local puede cambiar pocas referencias.

La idea final de la Semana 2 es que la representación determina qué información está disponible directamente; esa información determina cómo se implementan las operaciones; las operaciones deben preservar invariantes; y el trabajo necesario determina la complejidad.

Una estructura de datos no debe estudiarse como una colección de métodos aislados. Debe estudiarse como una relación entre representación, operaciones, invariantes, localización, modificación y complejidad.
