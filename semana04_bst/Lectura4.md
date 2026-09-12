### Lectura: árboles binarios de búsqueda, representación jerárquica y costo de las operaciones

Esta lectura consolida y amplía las ideas trabajadas en la Semana 4 de CC232. Durante las tres primeras semanas estudiamos estructuras cuyo estado podía entenderse principalmente como una secuencia.

En la Semana 1 utilizamos un arreglo dinámico, donde la posición lógica coincidía con una posición física del arreglo. Esa representación ofrecía acceso directo por índice y obligaba a distinguir tamaño, capacidad y redimensionamiento.

En la Semana 2 utilizamos nodos enlazados (`head -> nodo -> nodo -> nodo`). La secuencia ya no dependía de posiciones contiguas; su orden se reconstruía siguiendo referencias.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`. La pregunta dejó de ser únicamente cómo representar una secuencia y pasó a ser qué operaciones permitimos sobre esa secuencia. Una pila restringe el acceso mediante **LIFO**, una cola mediante **FIFO**, y un deque permite operaciones por ambos extremos.

La Semana 4 introduce un cambio estructural más profundo: ¿qué ocurre si los elementos ya no se organizan en una sola línea? Pasaremos de una organización lineal a una organización jerárquica. En lugar de que un nodo conduzca esencialmente a un único siguiente elemento, un nodo podrá conducir a dos subestructuras diferentes, una izquierda y una derecha. Pero tener dos caminos no basta para buscar eficientemente.

La idea decisiva de esta semana será imponer un **invariante de orden** que permita decidir, después de cada comparación, por cuál camino continuar. El objetivo no es memorizar un dibujo de árbol ni una implementación particular. El objetivo es continuar la misma línea conceptual de las semanas anteriores: qué comportamiento queremos ofrecer (**ADT**), cómo almacenamos el estado (**representación**), qué propiedades deben permanecer verdaderas (**invariante**), cómo usamos y modificamos la representación (**algoritmo**), y cuánto trabajo exige cada operación (**complejidad**).

Al finalizar la lectura deberías poder explicar por qué un árbol binario de búsqueda permite implementar búsqueda e inserción siguiendo un solo camino desde la raíz, por qué ese camino depende del invariante de orden y por qué el costo real depende de la altura del árbol.

### 1. De estructuras lineales a estructuras jerárquicas

Considera la secuencia `10, 20, 30, 40, 50`. Una representación lineal con nodos podría ser `10 -> 20 -> 30 -> 40 -> 50 -> null`. Existe un camino natural desde el primer elemento hacia los siguientes; si partimos de 10 y queremos llegar a 50, la representación obliga a recorrer la cadena completa.

Ahora considera otra organización de los mismos valores:

```text
       30
     /    \
   20      40
  /          \
10            50
```

Aquí no existe una única relación de siguiente. Desde 30 aparecen dos posibilidades, izquierda o derecha. Desde 20 vuelve a aparecer una posibilidad izquierda, y desde 40 una posibilidad derecha. La estructura ya no es lineal. Es jerárquica.

#### Qué significa jerárquica

Una estructura jerárquica organiza elementos mediante relaciones de dependencia o descendencia. Un nodo puede tener nodos por debajo de él, y esos nodos pueden a su vez tener otros nodos. Por ejemplo:

```text
       40
     /    \
   20      60
  /  \    /  \
10   30  50   70
```

40 está por encima de 20 y 60; 20 está por encima de 10 y 30; 60 está por encima de 50 y 70. No interpretaremos "por encima" como una dirección física de memoria, sino como una relación estructural.

#### La representación sigue siendo enlazada

El cambio de lineal a jerárquico no significa abandonar las referencias Java. En la Semana 2 utilizábamos `Node next` o `Node prev; Node next`. En la Semana 4 utilizaremos `Node left; Node right; Node parent`. La idea de referencia sigue siendo la misma; lo que cambia es la forma de conectar los nodos, de una cadena a una bifurcación. La topología de la representación cambia, y al cambiar la representación vuelven a cambiar las operaciones y sus costos.

### 2. Qué es un árbol

Un **árbol** es una estructura jerárquica formada por nodos conectados sin ciclos. Para nuestro estudio utilizaremos árboles con una raíz, que proporciona el punto de entrada a toda la estructura. Considerando de nuevo:

```text
       40
     /    \
   20      60
  /  \    /  \
10   30  50   70
```

podemos describir este árbol mediante relaciones: 40 es la raíz; 20 y 60 son hijos de 40 y 40 es padre de ambos; 10 y 30 son hijos de 20; 50 y 70 son hijos de 60. La terminología no es decorativa, nos permitirá describir algoritmos con precisión.

### 3. Nodo, raíz, padre e hijo

Un **nodo** es una unidad de almacenamiento de la estructura. En el código de esta semana cada nodo contiene:

```java
static class Node {
    int x;
    Node left;
    Node right;
    Node parent;

    Node(int x) {
        this.x = x;
    }
}
```

El campo `x` almacena el valor lógico. Los campos `left`, `right` y `parent` describen la estructura. Esta distinción ya apareció en la Semana 2, entre contenido lógico e información estructural.

La **raíz** es el nodo desde el cual accedemos al árbol completo. En la implementación, `private Node root;`. Si `root == null`, el árbol está vacío; si `root != null`, `root` referencia al primer nodo de la jerarquía, y para un árbol no vacío debe cumplirse `root.parent == null`, porque la raíz no tiene padre.

Si un nodo `u` contiene `u.left == v` o `u.right == v`, entonces `u` es **padre** de `v`. Si mantenemos el campo `parent`, debe cumplirse también la relación inversa, `v.parent == u`.

Un nodo puede tener como máximo un **hijo izquierdo** y un **hijo derecho**. Esos hijos son diferentes estructuralmente; no podemos intercambiarlos arbitrariamente en un árbol binario de búsqueda, porque la posición izquierda o derecha tendrá significado debido al orden de los valores.

### 4. Nodo interno, hoja y subárbol

Una **hoja** es un nodo que no tiene hijos (`u.left == null` y `u.right == null`); en el árbol de ejemplo, las hojas son 10, 30, 50 y 70. Un **nodo interno** es un nodo que tiene al menos un hijo, como 40, 20 y 60 en ese mismo árbol.

El **subárbol enraizado en un nodo `u`** está formado por `u` y todos sus descendientes. En el árbol de ejemplo, el subárbol enraizado en 20 es `{10, 20, 30}` con 20 como raíz, y el subárbol enraizado en 60 es `{50, 60, 70}`. La noción de subárbol será esencial: el invariante del BST no habla solamente de un hijo inmediato, habla de todos los valores contenidos en un subárbol.

### 5. Árbol binario

Un **árbol binario** es un árbol en el que cada nodo tiene como máximo dos hijos, distinguidos como izquierdo y derecho. Un nodo puede tener 0, 1 o 2 hijos; un árbol binario no tiene que estar "lleno".

Este punto es fundamental: todo BST que estudiaremos esta semana es un árbol binario, pero no todo árbol binario es un BST. Por ejemplo, `40` con hijo izquierdo `50` y derecho `20` es un árbol binario válido (cada nodo tiene como máximo dos hijos), pero no satisface la regla de orden que utilizaremos para buscar. Por tanto, "árbol binario" describe una forma estructural, mientras que **BST** es un árbol binario más una propiedad de orden.

### 6. Qué es una clave

Para buscar y ordenar necesitamos comparar elementos. Llamaremos **clave** al valor que la estructura utiliza para realizar esas comparaciones. En el archivo Java de esta semana el nodo almacena únicamente `int x;`, así que `x` cumple dos papeles a la vez, dato almacenado y clave utilizada para ordenar. Si tuviéramos estudiantes con código, nombre y promedio, podríamos decidir que la clave fuera solamente el código y ordenar el árbol de acuerdo con ese campo; en esta semana evitamos esa complejidad y trabajamos directamente con enteros como clave.

Los enteros permiten comparar cualquier par de claves mediante `<`, `>` e `==` (por ejemplo, `20 < 40`, `60 > 40`, `40 == 40`). Ese **orden total** guiará la búsqueda.

### 7. BST como ADT de conjunto ordenado

BST significa *Binary Search Tree*, árbol binario de búsqueda, pero conviene separar nuevamente ADT e implementación.

Durante esta semana queremos una estructura que se comporte como un **conjunto ordenado de enteros distintos**, con operaciones relevantes `int size()`, `boolean contains(int x)` y `boolean add(int x)`. La palabra "conjunto" implica que una clave aparece como máximo una vez: `add(30)` seguido de otro `add(30)` no debe crear dos nodos con valor 30; la segunda inserción debe rechazarse.

Para implementar ese comportamiento utilizamos `root`, `n` y, en cada nodo, `x`, `left`, `right` y `parent`. El BST es la representación concreta que permite aprovechar el orden. Podemos resumir la separación: el ADT de conjunto ordenado ofrece `contains(x)`, `add(x)` y `size()`; la representación BST aporta `root`, `n`, `left`, `right`, `parent` y el invariante de orden. El usuario de `contains(30)` no necesita conocer qué nodos fueron visitados, pero nosotros, como implementadores de la estructura, sí necesitamos comprender ese recorrido.

### 8. Invariante de orden del BST

La propiedad central de un BST con claves distintas es que, para todo nodo `u`, todos los valores del subárbol izquierdo de `u` son menores que `u.x`, y todos los valores del subárbol derecho de `u` son mayores que `u.x`. En símbolos, **izquierda < nodo < derecha**, pero esa expresión debe entenderse sobre subárboles completos, no solo sobre hijos inmediatos.

En el árbol de ejemplo, respecto de 40 se cumple que 10, 20 y 30 son menores y 50, 60 y 70 son mayores; respecto de 20, 10 es menor y 30 es mayor; respecto de 60, 50 es menor y 70 es mayor. El invariante se cumple recursivamente.

### 9. Por qué comparar solamente padre e hijo no basta

Considera el árbol `40` con hijo izquierdo `20` y derecho `60`, y `60` con hijo izquierdo `30`. Si observamos solamente la relación inmediata `30 < 60`, podríamos pensar que el enlace local es correcto. Pero 30 pertenece al subárbol derecho de 40, y por tanto debería cumplirse `30 > 40`, lo cual no se cumple. El árbol no es un BST válido.

No basta verificar `u.left.x < u.x` y `u.right.x > u.x` en cada nodo; debemos pensar en todo el subárbol izquierdo y todo el subárbol derecho. Esta precisión es esencial para comprender por qué las decisiones de búsqueda son correctas: **el invariante es global respecto del subárbol**, no solo local respecto del hijo inmediato.

### 10. El invariante convierte una jerarquía en una estructura de búsqueda

Un árbol binario sin orden puede obligarnos a explorar ambos subárboles para encontrar un valor. Un BST permite tomar una decisión en cada nodo.

Supongamos que buscamos `x = 55` en el árbol `40` con hijo izquierdo `20` y derecho `60`, y `60` con hijo izquierdo `50`. Primero, `55 > 40`; el invariante nos dice que todo el subárbol izquierdo de 40 contiene valores menores que 40, así que 55 no puede estar allí y descartamos ese subárbol completo, continuando solo por la derecha. Después, `55 < 60`, así que descartamos el subárbol derecho de 60 y continuamos por la izquierda. Luego, `55 > 50`, así que continuamos por la derecha de 50, pero `50.right == null`. La búsqueda termina sin encontrar 55.

### 11. Camino de búsqueda

Llamaremos **camino de búsqueda** a la secuencia de nodos visitados desde `root` mientras comparamos la clave buscada. Para buscar 30 en el árbol completo de siete nodos, el camino es `40 -> 20 -> 30` (30 < 40 va a la izquierda, 30 > 20 va a la derecha, 30 == 30 encontrado).

Si buscamos una clave ausente, por ejemplo 55, el camino en el árbol de la sección anterior es `40 -> 60 -> 50`, y la clave no existe. Pero el camino no fue inútil: el último nodo visitado fue 50, y esa información será exactamente la que necesitaremos para insertar 55.

### 12. La decisión izquierda o derecha

En cada nodo `w` existen tres posibilidades. Si `x < w.x`, solo el subárbol izquierdo puede contener `x`, así que `w = w.left;`. Si `x > w.x`, solo el subárbol derecho puede contenerlo, así que `w = w.right;`. Si `x == w.x`, la búsqueda terminó, hemos encontrado la clave.

Esta decisión (menor va a la izquierda, mayor va a la derecha) no es una regla arbitraria: es correcta porque el BST garantiza `izquierda < nodo < derecha`. Si el invariante se rompiera, el algoritmo de búsqueda dejaría de ser confiable.

### 13. Del camino de búsqueda al último nodo visitado

Retomando el árbol `40` con hijo izquierdo `20` y derecho `60`, y `60` con hijo izquierdo `50`, buscar 55 produce el camino `40 -> 60 -> 50 -> null`; la referencia que intentamos seguir desde 50 fue `50.right`. Aunque 55 no exista, queremos recordar que el último nodo visitado fue 50, porque ese nodo nos proporciona dos resultados diferentes, primero que 55 no está en el árbol, y segundo que si quisiéramos insertar 55, debería quedar relacionado con 50. Esta idea motiva el método `findLast(int x)`.

### 14. Qué debe hacer findLast(int x)

La especificación que utilizaremos es: si `x` existe, retornar el nodo que contiene `x`; si `x` no existe, retornar el último nodo visitado; si el árbol está vacío, retornar `null`. Observa que no es exactamente lo mismo que `find` o `contains`; `findLast` devuelve información estructural sobre dónde terminó el camino.

### 15. Diseñar findLast antes de escribir Java

Necesitamos dos referencias temporales, `w` (el nodo que estamos examinando ahora) y `prev` (el último nodo real que ya examinamos). Al comenzar, `w = root` y `prev = null`. Mientras `w` no sea `null`, primero hacemos `prev = w` y luego decidimos el siguiente camino según `x` sea menor, mayor o igual a `w.x`. Si el ciclo termina sin haber encontrado la clave, retornamos `prev`.

Supongamos que `x` no existe: el ciclo termina cuando `w == null`. Si solamente conserváramos `w`, habríamos perdido la referencia al último nodo real; `prev` evita esa pérdida. Esta idea recuerda una lección de la Semana 2, antes de modificar o avanzar una referencia, hay que preguntarse qué información se necesita conservar.

### 16. Implementación de findLast(int x)

```java
private Node findLast(int x) {
    Node w = root;
    Node prev = null;

    while (w != null) {
        prev = w;

        if (x < w.x) {
            w = w.left;
        } else if (x > w.x) {
            w = w.right;
        } else {
            return w;
        }
    }

    return prev;
}
```

No conviene memorizar este método como una secuencia de líneas. Cada línea representa una decisión estructural.

### 17. Traza completa de findLast(55)

Sobre el árbol `40` con hijo izquierdo `20` y derecho `60`, y `60` con hijo izquierdo `50`, el estado inicial es `w = 40`, `prev = null`. En la primera iteración, `prev = 40`, y como `55 > 40`, `w = 60`. En la segunda, `prev = 60`, y como `55 < 60`, `w = 50`. En la tercera, `prev = 50`, y como `55 > 50`, `w = null`, y el ciclo termina. El método retorna `prev`, es decir, el nodo 50. Por tanto `findLast(55) -> nodo 50`, aunque 55 no exista.

### 18. Traza de una búsqueda exitosa

Busquemos 30 en el árbol `40` con hijo izquierdo `20` (que tiene hijos 10 y 30) y derecho `60`. Primera iteración, `w = 40`, `30 < 40`, `w = 20`. Segunda, `w = 20`, `30 > 20`, `w = 30`. Tercera, `30 == 30`, el método ejecuta `return w;`. Por tanto, `findLast(30) -> nodo 30`.

### 19. El caso del árbol vacío

Si `root == null`, entonces `w = null` y `prev = null` desde el inicio. El ciclo `while (w != null)` no se ejecuta, y el método retorna `null`. Por tanto, `findLast(x) -> null` cuando el árbol está vacío. Este resultado será necesario para reconocer la primera inserción.

### 20. contains(int x) como cliente de findLast

El archivo de esta semana utiliza:

```java
boolean contains(int x) {
    Node p = findLast(x);
    return p != null && p.x == x;
}
```

Esta implementación es pequeña porque la búsqueda ya está concentrada en `findLast`. No basta con `p != null`: supongamos `findLast(55) -> nodo 50`; entonces `p != null` es verdadero, pero 55 no existe, así que también necesitamos `p.x == x`. En nuestro ejemplo, `50 == 55` es falso, por tanto `contains(55) -> false`. En cambio, si `findLast(30) -> nodo 30`, entonces `p != null` y `p.x == 30`, así que `contains(30) -> true`.

Podemos resumir la separación de responsabilidades: `findLast(x)` responde dónde terminó el camino de búsqueda, mientras que `contains(x)` responde si `x` pertenece al conjunto. Un método estructural puede servir de base para varias operaciones del ADT.

### 21. La búsqueda fallida determina la posición de inserción

Esta es una de las ideas centrales de la Semana 4. Retomando el árbol `40` con hijo izquierdo `20` y derecho `60`, y `60` con hijo izquierdo `50`, buscamos 55 y ya sabemos que `findLast(55) = 50`. La última comparación fue `55 > 50`, y `50.right == null`. Entonces la posición de inserción es el hijo derecho de 50. La búsqueda fallida no necesita repetirse, el mismo camino que demuestra que 55 no existe determina dónde debe añadirse.

### 22. Inserción en un BST

Queremos implementar `boolean add(int x)`. La operación debe localizar la posición, rechazar un duplicado, crear el nuevo nodo, enlazarlo correctamente, mantener `parent`, mantener `root` y actualizar `n`. Podemos separar la operación en dos fases, localización (`findLast(x)`) y modificación estructural (conectar el nuevo nodo). Esta separación será importante también para analizar el costo.

### 23. Rechazo de duplicados

Nuestra estructura representa un conjunto, así que una clave debe aparecer como máximo una vez. Supongamos `findLast(30) -> nodo 30` y queremos `add(30)`; como `p.x == x`, la clave ya existe, y la operación debe retornar `false` sin modificar `root`, `left`, `right`, `parent` ni `n`. En particular, `n` no aumenta.

Podríamos crear un nodo nuevo inmediatamente y descubrir después que la clave ya existe, pero ese nodo no sería utilizado. Es más claro buscar primero y crear el nodo solo después de confirmar que la clave no existe.

### 24. Primera inserción y mantenimiento de root

El árbol vacío tiene `root = null` y `n = 0`. Si ejecutamos `add(40)`, obtenemos `findLast(40) = null`; ese `null` no representa un error, indica que todavía no existe ningún nodo, así que el nuevo nodo debe convertirse en raíz (`root = u;`), con `root.parent = null` y `n = 1`.

Para un árbol no vacío debe cumplirse siempre `root != null` y `root.parent == null`, porque el primer nodo no tiene padre.

### 25. Uso de parent durante la inserción

Supongamos que queremos insertar 55 y ya sabemos que `p = nodo 50`. Creamos `Node u = new Node(55);`. Como `55 > 50`, ejecutamos `p.right = u;`, pero esto solo establece una dirección de la relación; también debemos mantener `u.parent = p;`. El estado coherente es `50.right == u` y `u.parent == 50`.

El invariante local de `parent` exige que, para todo hijo izquierdo existente, `u.left.parent == u`, y para todo hijo derecho existente, `u.right.parent == u`. Este tipo de relación recuerda la lista doblemente enlazada de la Semana 2, donde teníamos `u.next.prev == u`; ahora tenemos `u.left.parent == u` y `u.right.parent == u`. Una estructura enlazada correcta debe ser coherente entre referencias relacionadas.

### 26. Decidir left o right al insertar

Después de `findLast(x)` tenemos un nodo `p`. Si la clave no existe, sabemos que uno de los enlaces adecuados de `p` es `null`. Si `x < p.x`, insertamos con `p.left = u;` y `u.parent = p;`. Si `x > p.x`, insertamos con `p.right = u;` y `u.parent = p;`. La comparación final de la búsqueda determina el lado de inserción.

### 27. Implementación de add(int x)

Una implementación completa para esta semana es:

```java
boolean add(int x) {
    Node p = findLast(x);

    if (p != null && p.x == x) {
        return false;
    }

    Node u = new Node(x);

    if (p == null) {
        root = u;
    } else {
        u.parent = p;

        if (x < p.x) {
            p.left = u;
        } else {
            p.right = u;
        }
    }

    n++;
    return true;
}
```

No son casos independientes sin relación entre sí. La lógica completa es: buscar; si ya existe, rechazar; si no existe, crear el nodo; si no había árbol, convertirlo en `root`; si ya había árbol, usar el último nodo visitado y la comparación para enlazar; finalmente, actualizar `n`.

### 28. Traza de inserciones

Partimos de un árbol vacío y ejecutamos `add(40)`, `add(20)`, `add(60)`, `add(10)`, `add(30)`, `add(50)`, `add(70)`.

`add(40)` encuentra el árbol vacío (`findLast(40) = null`), y 40 se convierte en raíz, con `n = 1`. `add(20)` compara `20 < 40` y encuentra `left == null`, dejando 20 como hijo izquierdo de 40 (`20.parent = 40`, `n = 2`). `add(60)` compara `60 > 40` y encuentra `right == null`, dejando 60 como hijo derecho de 40 (`n = 3`). `add(10)` sigue el camino `40 -> 20 -> left null` y queda como hijo izquierdo de 20. `add(30)` sigue `40 -> 20 -> right null` y queda como hijo derecho de 20. `add(50)` sigue `40 -> 60 -> left null` y queda como hijo izquierdo de 60. `add(70)` sigue `40 -> 60 -> right null` y queda como hijo derecho de 60.

El resultado final es:

```text
       40
     /    \
   20      60
  /  \    /  \
10   30  50   70
```

con `n = 7`.

### 29. Intentar insertar un duplicado

Ahora ejecutamos `add(30)`. El camino es `40 -> 20 -> 30`, y como `30 == 30`, `findLast(30)` retorna el nodo existente; entonces `p != null` y `p.x == x`, así que la operación retorna `false`. El árbol sigue siendo el mismo de siete nodos y `n = 7`; no aparece un segundo 30.

### 30. Mantenimiento de n

La variable `private int n;` almacena el número de nodos que pertenecen al árbol, así que `size() -> n` puede responderse en **O(1)**. `n` aumenta únicamente cuando un nuevo nodo se incorpora realmente, en una inserción aceptada; si un duplicado se rechaza, `n` no cambia.

Un invariante importante es que `n` debe ser exactamente el número de nodos alcanzables desde `root` siguiendo `left` y `right`. Un valor de `n` correcto no puede compensar enlaces incorrectos; la representación completa debe ser coherente.

### 31. Invariantes de representación del BST

Podemos reunir varias propiedades. Para el árbol vacío, `n == 0` y `root == null`. Para el árbol no vacío, `n > 0`, `root != null` y `root.parent == null`. Como coherencia padre-hijo, si `u.left != null` entonces `u.left.parent == u`, y si `u.right != null` entonces `u.right.parent == u`. Como tamaño, el número de nodos alcanzables desde `root` mediante `left` y `right` debe ser exactamente `n`. Como orden BST, para todo nodo `u`, todo valor del subárbol izquierdo es menor que `u.x` y todo valor del subárbol derecho es mayor. Finalmente, como claves distintas, no existen dos nodos diferentes con el mismo `x`. Una operación es correcta cuando produce el resultado esperado y además conserva estas propiedades.

### 32. Recorrer un árbol

En una secuencia ya existe un orden lineal natural, por ejemplo `10 -> 20 -> 30`, y podemos seguir `next`. En un árbol no existe un único "siguiente" definido por la representación; debemos decidir un **orden de recorrido**. Un recorrido especifica en qué momento visitamos el nodo actual, el subárbol izquierdo y el subárbol derecho. Durante esta semana solamente necesitamos desarrollar en profundidad el recorrido **inorder**, porque su relación con el invariante del BST es directa.

### 33. Recorrido inorder

El recorrido inorder utiliza el orden subárbol izquierdo, nodo, subárbol derecho (`left, node, right`). Para el árbol completo de siete nodos, el resultado es `10 20 30 40 50 60 70`.

### 34. Implementación recursiva de inorder

El archivo de esta semana contiene:

```java
void inorder() {
    inorder(root);
    System.out.println();
}

private void inorder(Node u) {
    if (u == null) return;

    inorder(u.left);
    System.out.print(u.x + " ");
    inorder(u.right);
}
```

La versión pública inicia desde `root`; la versión recursiva procesa un subárbol.

### 35. Por qué la recursión encaja naturalmente con árboles

Un árbol binario puede describirse recursivamente: un nodo tiene un subárbol izquierdo y un subárbol derecho, y cada uno de esos subárboles vuelve a ser un árbol binario. Por eso una operación sobre todo el árbol puede expresarse como resolver el subárbol izquierdo, resolver el nodo y resolver el subárbol derecho. El caso base es `u == null`, porque no existe ningún nodo que procesar.

### 36. Traza de inorder sobre un subárbol

Considera el subárbol `20` con hijo izquierdo 10 y derecho 30. Al ejecutar `inorder(20)`, primero se llama `inorder(10)`; dentro de 10, `inorder(null)` retorna, se imprime 10, y `inorder(null)` retorna de nuevo. Regresamos a 20 y se imprime 20. Luego se llama `inorder(30)`, que imprime 30. El resultado es `10 20 30`.

### 37. Por qué inorder produce claves ordenadas en un BST

Esta propiedad no surge por casualidad. Para cualquier nodo `u`, el invariante garantiza que todas las claves del subárbol izquierdo son menores que `u.x`. El recorrido inorder imprime primero ese subárbol, después imprime `u.x`, y luego el invariante garantiza que todas las claves del subárbol derecho son mayores que `u.x`, que el recorrido imprime al final. Por tanto, el orden es menores, nodo, mayores, y si la misma propiedad se cumple recursivamente dentro de ambos subárboles, el resultado completo está ordenado.

### 38. Inorder ordenado no verifica toda la representación

Supongamos `40.left == 20` y `40.right == 60`, pero accidentalmente `20.parent == 60`. El recorrido inorder utiliza `left` y `right`, no utiliza `parent`, así que podría seguir produciendo `20 40 60` aunque el campo `parent` sea incorrecto. La lección es que una prueba observable puede verificar parte de un invariante sin verificar toda la representación. Esta idea continúa el enfoque de las semanas anteriores.

### 39. Profundidad y altura

Para analizar el costo necesitamos describir qué tan largos pueden ser los caminos del árbol. La **profundidad** de un nodo mide qué tan lejos está de la raíz, usando la raíz como nivel inicial; por ejemplo, en `40` con hijo izquierdo `20` (que tiene hijo izquierdo 10) y derecho `60`, 40 tiene profundidad 0, sus hijos profundidad 1, y 10 profundidad 2.

Para mantener coherencia con las implementaciones Java posteriores del curso, utilizaremos durante esta secuencia la convención de que un árbol vacío tiene altura 0 y una hoja tiene altura 1. La **altura h** es el número de nodos del camino más largo desde la raíz hasta una hoja. El árbol completo de siete nodos tiene `h = 3`, porque un camino más largo contiene tres nodos, como `40 -> 20 -> 10` o `40 -> 60 -> 70`.

Algunos libros cuentan aristas en lugar de nodos; con esa convención el mismo árbol tendría altura 2. La diferencia es una constante, y para el análisis asintótico **O(h)** no cambia. Lo importante es usar una convención de manera consistente.

### 40. El costo de la búsqueda depende de h

`findLast(x)` sigue un solo camino; no recorre primero todo el subárbol izquierdo y después el derecho. En cada nodo decide exactamente una continuación, izquierda o derecha. En el peor caso puede recorrer un camino desde la raíz hasta una hoja, de longitud proporcional a `h`. Por tanto, `findLast(x) -> O(h)`. Como `contains(x)` llama a `findLast(x)` y luego realiza trabajo constante, `contains(x) -> O(h)`.

### 41. El costo de add también depende de h

La inserción tiene dos fases. La localización (`findLast(x)`) puede recorrer hasta `h` nodos, con costo **O(h)**. Una vez conocido `p`, la modificación (insertar) cambia una cantidad constante de referencias (`u.parent`, `p.left` o `p.right`, `n`), con costo **O(1)**. Por tanto, `O(h) + O(1)` se simplifica a `add(x) -> O(h)`; la localización domina el costo. Esta distinción recuerda la Semana 2, localizar puede ser costoso, mientras que modificar localmente puede ser barato.

### 42. Un BST no garantiza O(log n)

Esta es una de las ideas más importantes de la semana. Es frecuente escuchar "BST, búsqueda O(log n)", pero esa afirmación es incompleta. La afirmación correcta para un BST no balanceado es búsqueda e inserción en **O(h)**. La relación entre `h` y `n` depende de la forma del árbol.

### 43. Árbol de poca altura

Insertando `40, 20, 60, 10, 30, 50, 70` en ese orden obtenemos el árbol balanceado de siete nodos, con `n = 7` y `h = 3`. Buscar 70 visita solamente tres nodos, 40, 60 y 70. En un árbol cuya altura crece aproximadamente como `log n`, las operaciones basadas en un camino tienen costo **O(log n)**. No necesitamos demostrar todavía una fórmula exacta de balance; la idea importante es que poca altura significa caminos cortos.

### 44. Árbol degenerado

Ahora insertamos exactamente las mismas claves, pero ya ordenadas, `10, 20, 30, 40, 50, 60, 70`. El resultado es una cadena, `10 -> 20 -> 30 -> 40 -> 50 -> 60 -> 70`, cada uno como hijo derecho del anterior, con `n = 7` y `h = 7`. Buscar 70 ahora visita los siete nodos. El BST se comporta estructuralmente como una cadena. En general, si `h` es proporcional a `n`, entonces `findLast`, `contains` y `add` cuestan **O(n)**. Un BST simple no impide esta situación.

### 45. Mismas claves, diferente forma

Este contraste es fundamental. Con las mismas siete claves podemos obtener un árbol balanceado de altura 3 o una cadena de altura 7. El conjunto lógico de claves es el mismo, pero la representación concreta es distinta. Lo que cambió fue la historia de inserciones; eso produjo otra forma del árbol; la forma produjo otra altura `h`; la altura produjo otro costo de búsqueda e inserción. Podemos resumir la cadena causal como historia de operaciones, forma del árbol, altura `h`, longitud de los caminos, y finalmente costo.

### 46. Relación entre representación, forma y costo

Esta semana vuelve a aparecer el principio central del curso. En la Semana 1, el arreglo ofrecía acceso por índice en **O(1)**, pero la modificación interna podía exigir desplazamientos. En la Semana 2, la lista enlazada podía tener localización por índice en **O(n)**, pero modificación local en **O(1)**. En la Semana 3, restringimos las operaciones de `Stack` y `Queue` para elegir representaciones adecuadas a esa restricción. En la Semana 4, el BST impone un orden jerárquico para guiar la búsqueda, pero incluso dentro del mismo BST, la forma del árbol afecta el costo. La representación no es solamente el conjunto de campos de una clase; también importa cómo esos campos quedan conectados después de una historia de operaciones.

### 47. Qué significa que el BST sea una estructura ordenada

Un BST no almacena necesariamente las claves físicamente en orden lineal; la raíz puede ser 40 aunque el menor valor sea 10. El orden está distribuido en la jerarquía, con los menores a la izquierda y los mayores a la derecha. Ese orden permite buscar, insertar y recorrer en orden. El recorrido inorder convierte ese orden jerárquico en una secuencia creciente.

### 48. Correctitud de la búsqueda

Podemos justificar la búsqueda mediante el invariante. Supongamos que estamos en un nodo `w`. Si `x < w.x`, por el invariante todo valor del subárbol derecho es mayor que `w.x`, así que `x` no puede encontrarse allí, y continuar por `w.left` no descarta ninguna posición válida para `x`. Si `x > w.x`, por el invariante todo valor del subárbol izquierdo es menor que `w.x`, así que continuamos por `w.right`. Si `x == w.x`, la clave fue encontrada. Esta argumentación muestra que el algoritmo y el invariante no pueden estudiarse por separado.

### 49. Correctitud de la inserción

Supongamos que `x` no existe y `p = findLast(x)`. La búsqueda terminó al intentar seguir una referencia `null` desde `p`. Si `x < p.x`, el lugar libre compatible con el camino es `p.left`; si `x > p.x`, el lugar libre compatible es `p.right`. El nuevo nodo se coloca exactamente en la posición a la que la búsqueda de `x` habría intentado continuar, y por eso se preserva el orden.

### 50. Casos que debe distinguir add

Aunque no estamos estudiando eliminación todavía, la inserción ya exige distinguir algunos estados: árbol vacío (`p == null`, el nuevo nodo se convierte en `root`), clave existente (`p.x == x`, no se inserta), clave menor que `p.x` (`p.left = u; u.parent = p;`) y clave mayor que `p.x` (`p.right = u; u.parent = p;`). No es necesario introducir más casos estructurales esta semana.

### 51. Qué errores pueden romper el BST durante add

Podemos catalogar cinco errores típicos. Elegir el lado incorrecto, por ejemplo hacer `p.right = u;` cuando `x < p.x`, rompe el invariante de orden. No mantener `parent`, por ejemplo dejar `p.left == u` pero `u.parent == null`, deja la clave apareciendo correctamente en inorder mientras la representación sigue siendo inconsistente. Aumentar `n` antes de detectar duplicados hace que `n` deje de coincidir con el número real de nodos. Crear otra raíz cuando el árbol no está vacío rompe la garantía de que `root` sigue siendo el punto de entrada al árbol completo. E insertar duplicados rompería la especificación del ADT como conjunto de claves distintas.

### 52. Una batería mínima de razonamiento

Para comprobar una implementación no basta usar una única secuencia. Conviene probar al menos árbol vacío, primera inserción, inserción a la izquierda, inserción a la derecha, búsqueda de la raíz, búsqueda de una clave interna, búsqueda ausente, duplicado, inorder, `size`, una historia que produzca poca altura y una historia que produzca un árbol degenerado. Estas pruebas obligan a verificar diferentes partes de la representación.

### 53. Ejemplo integrado

Construimos el árbol insertando `40, 20, 60, 10, 30, 50, 70`, obteniendo el árbol balanceado de siete nodos con `root.x = 40` y `n = 7`. Las relaciones de `parent` son `20.parent = 40`, `60.parent = 40`, `10.parent = 20`, `30.parent = 20`, `50.parent = 60` y `70.parent = 60`. Las búsquedas dan `contains(30) -> true` y `contains(55) -> false`. La inserción duplicada `add(30)` retorna `false` y `n` sigue siendo 7. El recorrido inorder produce `10 20 30 40 50 60 70`. En cuanto a complejidad, `findLast`, `contains` y `add` cuestan **O(h)**, y para este árbol `h = 3`.

### 54. El archivo Java de la semana

La representación central es:

```java
static class BinarySearchTree {
    static class Node {
        int x;
        Node left;
        Node right;
        Node parent;

        Node(int x) {
            this.x = x;
        }
    }

    private Node root;
    private int n;
}
```

`Node.x` es la clave almacenada; `Node.left` es la raíz del subárbol izquierdo; `Node.right` es la raíz del subárbol derecho; `Node.parent` es el padre del nodo; `root` es la raíz del BST completo; `n` es el número de nodos. Los métodos principales de esta semana son `findLast`, `contains`, `add`, `inorder` y `size`. No necesitamos agregar más operaciones para alcanzar el objetivo conceptual de la Semana 4.

### 55. Cómo leer findLast y add juntos

No conviene estudiar `findLast` y `add` como dos algoritmos aislados. La relación correcta es que `findLast(x)` produce dos resultados posibles: si `x` existe, ese resultado alimenta directamente a `contains`; si `x` no existe, ese resultado es el lugar donde se insertaría, y alimenta directamente a `add`. El diseño reutiliza una sola lógica de búsqueda.

### 56. Cómo leer inorder junto al invariante

Tampoco conviene memorizar el orden `left, node, right` sin entender por qué. La relación correcta es que el invariante BST (izquierda < nodo < derecha) es precisamente lo que hace que recorrer en ese mismo orden, izquierda, nodo, derecha, produzca una salida creciente. El recorrido obtiene su significado gracias al invariante.

### 57. Cómo leer O(h) junto a la representación

La complejidad tampoco debe memorizarse como una tabla suelta. `findLast -> O(h)` porque el método sigue un único camino, y la longitud máxima de ese camino está limitada por `h`. `contains` reutiliza `findLast`, y `add` reutiliza `findLast` y después modifica unas pocas referencias. Por tanto, los tres, `findLast`, `contains` y `add`, cuestan **O(h)**, y la forma del árbol determina `h`.

### 58. Qué no significa O(h)

`O(h)` no afirma que siempre visitamos exactamente `h` nodos; una búsqueda puede terminar antes. Por ejemplo, `contains(40)` en un árbol con raíz 40 termina en la raíz misma. El costo de peor caso se expresa mediante la longitud máxima que podría tener el camino relevante, no mediante la longitud real de cada búsqueda particular.

### 59. Qué significa razonablemente balanceado

No estudiaremos todavía un algoritmo de balanceo. Usaremos la expresión **razonablemente balanceado** solamente de manera descriptiva, para distinguir árboles cuya altura es relativamente pequeña respecto de `n` de árboles que se aproximan a una cadena. El árbol de siete nodos con ramas de longitudes parecidas está razonablemente balanceado; una cadena que crece siempre hacia el mismo lado está claramente degenerada. No introducimos todavía rotaciones, factor de balance ni AVL; esos mecanismos corresponden a una etapa posterior.

### 60. Una pregunta que queda abierta

Sabemos insertar una nueva clave, por ejemplo 55, cuya búsqueda `40 -> 60 -> 50 -> right null` nos dice dónde insertarla. Pero ahora imagina que queremos retirar 60. No resolveremos todavía esa operación, solo observa el problema: 60 conecta dos subárboles, el de 50 y el de 70. Cualquier solución futura tendrá que preservar la alcanzabilidad, `parent`, `root` si fuera necesario, `n` y el orden BST. La Semana 5 partirá de esas obligaciones.

### 61. Síntesis total

La Semana 4 introduce la primera estructura jerárquica central del curso. Un BST combina un árbol binario, claves comparables y un invariante de orden. La representación utiliza `root`, `n`, y en cada nodo `x`, `left`, `right` y `parent`. El invariante establece que el subárbol izquierdo es menor que el nodo, que a su vez es menor que el subárbol derecho.

Gracias a ese invariante, una búsqueda no explora todo el árbol; en cada nodo decide ir a la izquierda si la clave es menor, a la derecha si es mayor, o detenerse si es igual. `findLast(x)` reutiliza ese camino y conserva el último nodo visitado, lo que permite construir `contains(x)` y también determinar la posición de `add(x)`. La inserción debe preservar `root`, `parent`, `n`, el orden BST y la unicidad de las claves.

El recorrido inorder visita izquierda, nodo, derecha, y produce las claves en orden creciente porque utiliza directamente la propiedad de orden del BST. Finalmente, `findLast`, `contains` y `add` cuestan todos **O(h)**. La altura depende de la forma del árbol, y la forma depende de cómo las inserciones construyeron la representación. Por eso la idea final de la semana es la cadena representación, forma, altura, camino, costo.

La siguiente semana conservará este mismo BST y planteará una operación más delicada, eliminar. Antes de estudiarla, la búsqueda, el invariante, `parent`, `root`, `n` e inorder deben estar completamente comprendidos.
