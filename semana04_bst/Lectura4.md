### Lectura: árboles binarios de búsqueda, representación jerárquica y costo de las operaciones

Esta lectura consolida y amplía las ideas trabajadas en la Semana 4 de CC232. Durante las tres primeras semanas estudiamos estructuras cuyo estado podía entenderse principalmente como una secuencia.

En la Semana 1 utilizamos un arreglo dinámico.

```text
posición lógica
        ->
posición física del arreglo
```

Esa representación ofrecía acceso directo por índice y obligaba a distinguir tamaño, capacidad y redimensionamiento.

En la Semana 2 utilizamos nodos enlazados.

```text
head -> nodo -> nodo -> nodo
```

La secuencia ya no dependía de posiciones contiguas. Su orden se reconstruía siguiendo referencias.

En la Semana 3 estudiamos Stack, Queue y Deque.

La pregunta dejó de ser únicamente cómo representar una secuencia y pasó a ser:

```text
¿qué operaciones permitimos
sobre esa secuencia?
```

Una pila restringe el acceso mediante LIFO. Una cola restringe el acceso mediante FIFO. Un deque permite operaciones por ambos extremos.

La Semana 4 introduce un cambio estructural más profundo.

```text
¿qué ocurre si los elementos
ya no se organizan en una sola línea?
```

Pasaremos de una organización lineal a una organización jerárquica.

En lugar de que un nodo conduzca esencialmente a un único siguiente elemento, un nodo podrá conducir a dos subestructuras diferentes.

```text
            nodo
           /    \
      izquierda  derecha
```

Pero tener dos caminos no basta para buscar eficientemente.

La idea decisiva de esta semana será imponer un **invariante de orden** que permita decidir, después de cada comparación, por cuál camino continuar. El objetivo no es memorizar un dibujo de árbol ni una implementación particular.

El objetivo es continuar la misma línea conceptual de las semanas anteriores:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    cómo almacenamos el estado

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo usamos y modificamos la representación

complejidad
    cuánto trabajo exige cada operación
```

Al finalizar la lectura deberías poder explicar por qué un árbol binario de búsqueda permite implementar búsqueda e inserción siguiendo un solo camino desde la raíz, por qué ese camino depende del invariante de orden y por qué el costo real depende de la altura del árbol.

### 1. De estructuras lineales a estructuras jerárquicas

Considera la secuencia:

```text
10, 20, 30, 40, 50
```

Una representación lineal con nodos podría ser:

```text
10 -> 20 -> 30 -> 40 -> 50 -> null
```

Existe un camino natural desde el primer elemento hacia los siguientes.

Si partimos del nodo 10 y queremos llegar a 50, seguimos sucesivamente:

```text
10
20
30
40
50
```

La representación obliga a recorrer la cadena.

Ahora considera otra organización de valores:

```text
          30
        /    \
      20      40
     /          \
   10            50
```

Aquí no existe una única relación de siguiente.

Desde 30 aparecen dos posibilidades:

```text
izquierda
derecha
```

Desde 20 vuelve a aparecer una posibilidad izquierda. Desde 40 aparece una posibilidad derecha.

La estructura ya no es lineal. Es jerárquica.

#### Qué significa jerárquica

Una estructura jerárquica organiza elementos mediante relaciones de dependencia o descendencia.

Un nodo puede tener nodos por debajo de él. Esos nodos pueden, a su vez, tener otros nodos.

Por ejemplo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

40 está por encima de 20 y 60.

20 está por encima de 10 y 30.

60 está por encima de 50 y 70.

No interpretaremos "por encima" como una dirección física de memoria.

Es una relación estructural.

#### La representación sigue siendo enlazada

El cambio de lineal a jerárquico no significa abandonar las referencias Java.

En Semana 2 utilizábamos:

```java
Node next;
```

o:

```java
Node prev;
Node next;
```

En Semana 4 utilizaremos:

```java
Node left;
Node right;
Node parent;
```

La idea de referencia sigue siendo la misma. Lo que cambia es la forma de conectar los nodos.

```text
Semana 2

nodo -> siguiente


Semana 4

          nodo
         /    \
     left      right
```

La topología de la representación cambia. Y al cambiar la representación vuelven a cambiar las operaciones y sus costos.

### 2. Qué es un árbol

Un **árbol** es una estructura jerárquica formada por nodos conectados sin ciclos.

Para nuestro estudio utilizaremos árboles con una raíz.

La raíz proporciona el punto de entrada a toda la estructura.

Considera:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Podemos describir este árbol mediante relaciones.

```text
40 es la raíz

20 y 60 son hijos de 40

40 es padre de 20 y 60

10 y 30 son hijos de 20

50 y 70 son hijos de 60
```

La terminología no es decorativa.

Nos permitirá describir algoritmos con precisión.

### 3. Nodo, raíz, padre e hijo

#### Nodo

Un **nodo** es una unidad de almacenamiento de la estructura.

En el código de esta semana cada nodo contiene:

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

El campo:

```java
int x;
```

almacena el valor lógico.

Los campos:

```java
left
right
parent
```

describen la estructura.

Esta distinción ya apareció en Semana 2.

```text
x
    contenido lógico

left, right, parent
    información estructural
```

#### Raíz

La **raíz** es el nodo desde el cual accedemos al árbol completo.

En la implementación:

```java
private Node root;
```

Si:

```text
root == null
```

el árbol está vacío.

Si:

```text
root != null
```

`root` referencia al primer nodo de la jerarquía.

Para un árbol no vacío debe cumplirse:

```text
root.parent == null
```

porque la raíz no tiene padre.

#### Padre

Si un nodo `u` contiene:

```text
u.left == v
```

o:

```text
u.right == v
```

entonces `u` es padre de `v`.

Si mantenemos el campo `parent`, debe cumplirse también la relación inversa:

```text
v.parent == u
```

#### Hijo izquierdo e hijo derecho

Un nodo puede tener como máximo:

```text
un hijo izquierdo
un hijo derecho
```

Esos hijos son diferentes estructuralmente.

No podemos intercambiarlos arbitrariamente en un árbol binario de búsqueda.

La posición izquierda o derecha tendrá significado debido al orden de los valores.

### 4. Nodo interno, hoja y subárbol

#### Hoja

Una **hoja** es un nodo que no tiene hijos.

En Java:

```text
u.left == null
u.right == null
```

Por ejemplo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

las hojas son:

```text
10
30
50
70
```

#### Nodo interno

Un **nodo interno** es un nodo que tiene al menos un hijo.

En el ejemplo:

```text
40
20
60
```

son nodos internos.

#### Subárbol

El **subárbol enraizado en un nodo `u`** está formado por `u` y todos sus descendientes.

En:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

el subárbol enraizado en 20 es:

```text
      20
     /  \
   10   30
```

El subárbol enraizado en 60 es:

```text
      60
     /  \
   50   70
```

La noción de subárbol será esencial.

El invariante del BST no habla solamente de un hijo inmediato.Habla de **todos los valores contenidos en un subárbol**.

### 5. Árbol binario

Un **árbol binario** es un árbol en el que cada nodo tiene como máximo dos hijos.

Los distinguimos como:

```text
hijo izquierdo
hijo derecho
```

Por ejemplo:

```text
      A
     / \
    B   C
```

es un árbol binario.

También lo es:

```text
      A
       \
        B
         \
          C
```

Un árbol binario no tiene que estar "lleno".

Un nodo puede tener:

```text
0 hijos
1 hijo
2 hijos
```

#### Árbol binario no significa BST

Este punto es fundamental. Todo BST que estudiaremos esta semana es un árbol binario.

Pero no todo árbol binario es un BST.

Por ejemplo:

```text
      40
     /  \
   50    20
```

es un árbol binario.

Cada nodo tiene como máximo dos hijos. Pero no satisface la regla de orden que utilizaremos para buscar.

Por tanto:

```text
árbol binario
    describe una forma estructural

BST
    árbol binario
    +
    propiedad de orden
```

### 6. Qué es una clave

Para buscar y ordenar necesitamos comparar elementos.

Llamaremos **clave** al valor que la estructura utiliza para realizar esas comparaciones.

En el archivo Java de esta semana el nodo almacena:

```java
int x;
```

No tenemos un objeto complejo con varios campos.

Por eso `x` cumple dos papeles simultáneamente:

```text
dato almacenado
clave utilizada para ordenar
```

Si tuviéramos estudiantes con:

```text
código
nombre
promedio
```

podríamos decidir que la clave fuera solamente:

```text
código
```

y ordenar el árbol de acuerdo con ese campo.

En esta semana evitamos esa complejidad.

Trabajamos con enteros y usamos directamente:

```text
x
```

como clave.

#### Orden total

Los enteros permiten comparar cualquier par de claves mediante:

```text
<
>
==
```

Por ejemplo:

```text
20 < 40
60 > 40
40 == 40
```

Esas comparaciones guiarán la búsqueda.

### 7. BST como ADT de conjunto ordenado

BST significa:

```text
Binary Search Tree
árbol binario de búsqueda
```

Pero conviene separar nuevamente ADT e implementación.

#### El comportamiento que queremos

Durante esta semana queremos una estructura que se comporte como un **conjunto ordenado de enteros distintos**.

Operaciones relevantes:

```java
int size()
boolean contains(int x)
boolean add(int x)
```

La palabra **conjunto** implica aquí que una clave aparece como máximo una vez.

Por tanto:

```text
add(30)
add(30)
```

no debe crear dos nodos con valor 30.

La segunda inserción debe rechazarse.

#### La representación elegida

Para implementar ese comportamiento utilizamos:

```text
root
n
Node.x
Node.left
Node.right
Node.parent
```

El BST es la representación concreta que permitirá aprovechar el orden.

#### ADT frente a representación

Podemos resumir:

```text
ADT de conjunto ordenado

contains(x)
add(x)
size()


representación BST

root
n
left
right
parent
invariante de orden
```

El usuario de `contains(30)` no necesita conocer qué nodos fueron visitados. Pero nosotros, como implementadores de la estructura, sí necesitamos comprender ese recorrido.

### 8. Invariante de orden del BST

La propiedad central de un BST con claves distintas es:

```text
para todo nodo u

todos los valores
del subárbol izquierdo de u
son menores que u.x

todos los valores
del subárbol derecho de u
son mayores que u.x
```

En símbolos:

```text
izquierda < nodo < derecha
```

pero esa expresión debe entenderse sobre subárboles completos.

#### Ejemplo válido

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Respecto de 40:

```text
10, 20, 30 < 40

50, 60, 70 > 40
```

Respecto de 20:

```text
10 < 20

30 > 20
```

Respecto de 60:

```text
50 < 60

70 > 60
```

El invariante se cumple recursivamente.

### 9. Por qué comparar solamente padre e hijo no basta

Considera:

```text
          40
        /    \
      20      60
               \
                70
```

Todas las relaciones inmediatas son correctas.

Ahora cambia el árbol:

```text
          40
        /    \
      20      60
             /
            30
```

Si observamos solamente:

```text
30 < 60
```

podríamos pensar que el enlace local es correcto. Pero 30 pertenece al subárbol derecho de 40.

Por tanto debería cumplirse:

```text
30 > 40
```

y no se cumple.

El árbol no es un BST válido.

#### El invariante es global respecto del subárbol

No basta verificar:

```text
u.left.x < u.x
u.right.x > u.x
```

Debemos pensar:

```text
todo el subárbol izquierdo

todo el subárbol derecho
```

Esta precisión es esencial para comprender por qué las decisiones de búsqueda son correctas.

### 10. El invariante convierte una jerarquía en una estructura de búsqueda

Un árbol binario sin orden puede obligarnos a explorar ambos subárboles para encontrar un valor. Un BST permite tomar una decisión.

Supongamos que buscamos:

```text
x = 55
```

en:

```text
          40
        /    \
      20      60
             /
            50
```

Primera comparación:

```text
55 > 40
```

El invariante nos dice que todo el subárbol izquierdo de 40 contiene valores menores que 40.

Entonces 55 no puede estar allí.

Podemos descartar:

```text
subárbol izquierdo de 40
```

y continuar solamente por la derecha.

Después:

```text
55 < 60
```

Descartamos el subárbol derecho de 60.

Continuamos por la izquierda.

Después:

```text
55 > 50
```

continuamos por la derecha de 50.

Pero:

```text
50.right == null
```

La búsqueda termina.

No encontramos 55.

### 11. Camino de búsqueda

Llamaremos **camino de búsqueda** a la secuencia de nodos visitados desde `root` mientras comparamos la clave buscada.

Para buscar 30:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

tenemos:

```text
30 < 40
ir a left

30 > 20
ir a right

30 == 30
encontrado
```

Camino:

```text
40 -> 20 -> 30
```

#### Buscar una clave ausente

Busquemos 55:

```text
55 > 40
derecha

55 < 60
izquierda

55 > 50
derecha

null
```

Camino:

```text
40 -> 60 -> 50
```

La clave no existe.

Pero el camino no fue inútil.

El último nodo visitado fue:

```text
50
```

y esa información será exactamente la que necesitaremos para insertar 55.

### 12. La decisión izquierda o derecha

En cada nodo `w` existen tres posibilidades.

#### Caso 1. La clave es menor

```text
x < w.x
```

Solo el subárbol izquierdo puede contener `x`.

Por tanto:

```java
w = w.left;
```

#### Caso 2. La clave es mayor

```text
x > w.x
```

Solo el subárbol derecho puede contener `x`.

Por tanto:

```java
w = w.right;
```

#### Caso 3. La clave es igual

```text
x == w.x
```

La búsqueda terminó.

Hemos encontrado la clave.

#### El algoritmo depende del invariante

La decisión:

```text
menor -> izquierda
mayor -> derecha
```

no es una regla arbitraria.

Es correcta porque el BST garantiza:

```text
izquierda < nodo < derecha
```

Si el invariante se rompiera, el algoritmo de búsqueda dejaría de ser confiable.

### 13. Del camino de búsqueda al último nodo visitado

Considera otra vez:

```text
          40
        /    \
      20      60
             /
            50
```

Buscar 55 produce:

```text
40 -> 60 -> 50 -> null
```

La referencia que intentamos seguir desde 50 fue:

```text
50.right
```

Aunque 55 no exista, queremos recordar:

```text
último nodo visitado = 50
```

¿Por qué?

Porque ese nodo nos proporciona dos resultados diferentes.

Primero:

```text
55 no está en el árbol
```

Segundo:

```text
si quisiéramos insertar 55,
debería quedar relacionado con 50
```

Esta idea motiva el método:

```java
findLast(int x)
```

### 14. Qué debe hacer findLast(int x)

La especificación que utilizaremos es:

```text
si x existe
    retornar el nodo que contiene x

si x no existe
    retornar el último nodo visitado

si el árbol está vacío
    retornar null
```

Observa que no es exactamente lo mismo que:

```text
find
```

o:

```text
contains
```

`findLast` devuelve información estructural sobre dónde terminó el camino.

### 15. Diseñar findLast antes de escribir Java

Necesitamos dos referencias temporales.

```text
w
    nodo que estamos examinando ahora

prev
    último nodo real que ya examinamos
```

Al comenzar:

```text
w = root
prev = null
```

Mientras `w` no sea `null`:

```text
prev = w
```

y luego decidimos el siguiente camino.

Pseudocódigo:

```text
findLast(x)

w = root
prev = null

mientras w != null
    prev = w

    si x < w.x
        w = w.left
    en otro caso si x > w.x
        w = w.right
    en otro caso
        retornar w

retornar prev
```

#### Por qué necesitamos prev

Supongamos que `x` no existe.

El ciclo termina cuando:

```text
w == null
```

Si solamente conserváramos `w`, habríamos perdido la referencia al último nodo real.

`prev` evita esa pérdida.

Esta idea recuerda una lección de Semana 2:

```text
antes de modificar o avanzar una referencia,
pregunta qué información necesitas conservar
```

### 16. Implementación de findLast(int x)

En Java:

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

Árbol:

```text
          40
        /    \
      20      60
             /
            50
```

Estado inicial:

```text
w = 40
prev = null
```

Primera iteración:

```text
prev = 40

55 > 40

w = 60
```

Segunda iteración:

```text
prev = 60

55 < 60

w = 50
```

Tercera iteración:

```text
prev = 50

55 > 50

w = null
```

Termina el ciclo.

Resultado:

```text
return prev

return nodo 50
```

Por tanto:

```text
findLast(55) -> nodo 50
```

aunque:

```text
55 no existe
```

### 18. Traza de una búsqueda exitosa

Busquemos 30:

```text
          40
        /    \
      20      60
     /  \
   10   30
```

Primera iteración:

```text
w = 40

30 < 40

w = 20
```

Segunda:

```text
w = 20

30 > 20

w = 30
```

Tercera:

```text
30 == 30
```

El método ejecuta:

```java
return w;
```

Por tanto:

```text
findLast(30) -> nodo 30
```

### 19. El caso del árbol vacío

Si:

```text
root == null
```

entonces:

```text
w = null
prev = null
```

El ciclo:

```java
while (w != null)
```

no se ejecuta.

El método retorna:

```text
null
```

Por tanto:

```text
findLast(x) -> null
```

cuando el árbol está vacío.

Este resultado será necesario para reconocer la primera inserción.

### 20. contains(int x) como cliente de findLast

El archivo de esta semana utiliza:

```java
boolean contains(int x) {
    Node p = findLast(x);
    return p != null && p.x == x;
}
```

Esta implementación es pequeña porque la búsqueda ya está concentrada en `findLast`.

#### Por qué no basta p != null

Supongamos:

```text
findLast(55) -> nodo 50
```

Entonces:

```text
p != null
```

es verdadero.

Pero 55 no existe.

Por eso también necesitamos:

```text
p.x == x
```

En nuestro ejemplo:

```text
50 == 55
```

es falso.

Por tanto:

```text
contains(55) -> false
```

#### Caso existente

Si:

```text
findLast(30) -> nodo 30
```

entonces:

```text
p != null
p.x == 30
```

y:

```text
contains(30) -> true
```

#### Separación de responsabilidades

Podemos resumir:

```text
findLast(x)
    responde dónde terminó
    el camino de búsqueda

contains(x)
    responde si x
    pertenece al conjunto
```

Un método estructural puede servir de base para varias operaciones del ADT.

### 21. La búsqueda fallida determina la posición de inserción

Esta es una de las ideas centrales de Semana 4.

Supongamos:

```text
          40
        /    \
      20      60
             /
            50
```

Buscamos 55.

Ya sabemos:

```text
findLast(55) = 50
```

La última comparación fue:

```text
55 > 50
```

y:

```text
50.right == null
```

Entonces la posición de inserción es:

```text
hijo derecho de 50
```

Resultado:

```text
          40
        /    \
      20      60
             /
            50
              \
               55
```

La búsqueda fallida no necesita repetirse.

El mismo camino que demuestra que 55 no existe determina dónde debe añadirse.

### 22. Inserción en un BST

Queremos implementar:

```java
boolean add(int x)
```

La operación debe:

```text
localizar la posición

rechazar un duplicado

crear el nuevo nodo

enlazarlo correctamente

mantener parent

mantener root

actualizar n
```

Podemos separar la operación en dos fases.

```text
Fase 1
localización
    findLast(x)

Fase 2
modificación estructural
    conectar el nuevo nodo
```

Esta separación será importante también para analizar el costo.

### 23. Rechazo de duplicados

Nuestra estructura representa un conjunto.

Por tanto una clave debe aparecer como máximo una vez.

Supongamos:

```text
findLast(30) -> nodo 30
```

y queremos:

```text
add(30)
```

Como:

```text
p.x == x
```

la clave ya existe.

La operación debe retornar:

```text
false
```

y no modificar:

```text
root
left
right
parent
n
```

En particular:

```text
n no aumenta
```

#### Por qué es mejor comprobar antes de crear

Podríamos crear un nodo nuevo inmediatamente. Pero si descubrimos que la clave ya existe, ese nodo no será utilizado.

Es más claro:

```text
buscar primero

crear después de confirmar
que la clave no existe
```

### 24. Primera inserción y mantenimiento de root

El árbol vacío tiene:

```text
root = null
n = 0
```

Si ejecutamos:

```text
add(40)
```

obtenemos:

```text
findLast(40) = null
```

Ese `null` no representa un error. Indica que todavía no existe ningún nodo.

Por tanto el nuevo nodo debe convertirse en raíz:

```java
root = u;
```

Después:

```text
root.x = 40
root.parent = null
n = 1
```

Representación:

```text
root
 |
 v
40
```

#### Invariante de root

Para un árbol no vacío:

```text
root != null
root.parent == null
```

El primer nodo no tiene padre.

### 25. Uso de parent durante la inserción

Supongamos que queremos insertar 55 y ya sabemos:

```text
p = nodo 50
```

Creamos:

```java
Node u = new Node(55);
```

Como:

```text
55 > 50
```

ejecutamos:

```java
p.right = u;
```

Pero esto solo establece una dirección de la relación.

También debemos mantener:

```java
u.parent = p;
```

El estado coherente es:

```text
50.right == u
u.parent == 50
```

#### Invariante local de parent

Para todo hijo izquierdo existente:

```text
u.left.parent == u
```

Para todo hijo derecho existente:

```text
u.right.parent == u
```

Este tipo de relación recuerda la lista doblemente enlazada de Semana 2.

Allí teníamos relaciones como:

```text
u.next.prev == u
```

Ahora tenemos:

```text
u.left.parent == u
u.right.parent == u
```

Una estructura enlazada correcta debe ser coherente entre referencias relacionadas.

### 26. Decidir left o right al insertar

Después de `findLast(x)` tenemos un nodo `p`.

Si la clave no existe, sabemos que uno de los enlaces adecuados de `p` es `null`.

#### Si x es menor

```text
x < p.x
```

insertamos:

```java
p.left = u;
```

y:

```java
u.parent = p;
```

#### Si x es mayor

```text
x > p.x
```

insertamos:

```java
p.right = u;
```

y:

```java
u.parent = p;
```

La comparación final de la búsqueda determina el lado de inserción.

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

#### Qué debe entenderse de este código

No son casos independientes sin relación.

La lógica es:

```text
buscar

si ya existe
    rechazar

si no existe
    crear nodo

si no había árbol
    convertirlo en root

si ya había árbol
    usar el último nodo visitado
    y la comparación para enlazar

actualizar n
```

### 28. Traza de inserciones

Partimos de:

```text
vacío
```

Ejecutamos:

```text
add(40)
add(20)
add(60)
add(10)
add(30)
add(50)
add(70)
```

#### add(40)

Árbol vacío:

```text
findLast(40) = null
```

40 se convierte en raíz:

```text
40
```

`n = 1`.

#### add(20)

```text
20 < 40
left == null
```

Resultado:

```text
    40
   /
 20
```

```text
20.parent = 40
n = 2
```

#### add(60)

```text
60 > 40
right == null
```

Resultado:

```text
    40
   /  \
 20    60
```

`n = 3`.

#### add(10)

Camino:

```text
40 -> 20 -> left null
```

Resultado:

```text
      40
     /  \
   20    60
  /
10
```

#### add(30)

Camino:

```text
40 -> 20 -> right null
```

Resultado:

```text
      40
     /  \
   20    60
  / \
10  30
```

#### add(50)

Camino:

```text
40 -> 60 -> left null
```

#### add(70)

Camino:

```text
40 -> 60 -> right null
```

Resultado final:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

`n = 7`.

### 29. Intentar insertar un duplicado

Ahora ejecutamos:

```text
add(30)
```

Camino:

```text
40 -> 20 -> 30
```

Como:

```text
30 == 30
```

`findLast(30)` retorna el nodo existente.

Entonces:

```text
p != null
p.x == x
```

La operación retorna:

```text
false
```

y el árbol sigue siendo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

También:

```text
n = 7
```

No aparece un segundo 30.

### 30. Mantenimiento de n

La variable:

```java
private int n;
```

almacena el número de nodos que pertenecen al árbol.

Por tanto:

```text
size() -> n
```

puede responderse en O(1).

#### Cuándo aumentar n

`n` aumenta únicamente cuando un nuevo nodo se incorpora realmente.

```text
inserción aceptada
    n++

duplicado rechazado
    n no cambia
```

#### Coherencia de n

Un invariante importante es:

```text
n =
número de nodos alcanzables
desde root siguiendo left y right
```

Un valor de `n` correcto no puede compensar enlaces incorrectos. La representación completa debe ser coherente.

### 31. Invariantes de representación del BST

Podemos reunir varias propiedades.

#### Árbol vacío

```text
n == 0
root == null
```

#### Árbol no vacío

```text
n > 0
root != null
root.parent == null
```

#### Coherencia padre-hijo

Si:

```text
u.left != null
```

entonces:

```text
u.left.parent == u
```

Si:

```text
u.right != null
```

entonces:

```text
u.right.parent == u
```

#### Tamaño

El número de nodos alcanzables desde `root` mediante `left` y `right` debe ser exactamente:

```text
n
```

#### Orden BST

Para todo nodo `u`:

```text
todo valor del subárbol izquierdo < u.x

todo valor del subárbol derecho > u.x
```

#### Claves distintas

No existen dos nodos diferentes con el mismo `x`. Una operación es correcta cuando produce el resultado esperado y además conserva estas propiedades.

### 32. Recorrer un árbol

En una secuencia ya existe un orden lineal natural.

Por ejemplo:

```text
10 -> 20 -> 30
```

podemos seguir `next`.

En un árbol:

```text
          40
        /    \
      20      60
```

no existe un único "siguiente" definido por la representación.

Debemos decidir un **orden de recorrido**.

Un recorrido especifica en qué momento visitamos:

```text
el nodo actual
el subárbol izquierdo
el subárbol derecho
```

Durante esta semana solamente necesitamos desarrollar en profundidad:

```text
inorder
```

porque su relación con el invariante del BST es directa.

### 33. Recorrido inorder

El recorrido inorder utiliza el orden:

```text
subárbol izquierdo
nodo
subárbol derecho
```

También puede escribirse:

```text
left
node
right
```

Para:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

el resultado es:

```text
10 20 30 40 50 60 70
```

### 34. Implementación recursiva de inorder

El archivo de esta semana contiene:

```java
void inorder() {
    inorder(root);
    System.out.println();
}
```

y:

```java
private void inorder(Node u) {
    if (u == null) return;

    inorder(u.left);
    System.out.print(u.x + " ");
    inorder(u.right);
}
```

La versión pública inicia desde:

```text
root
```

La versión recursiva procesa un subárbol.

### 35. Por qué la recursión encaja naturalmente con árboles

Un árbol binario puede describirse recursivamente.

Un nodo tiene:

```text
un subárbol izquierdo
un subárbol derecho
```

y cada uno de esos subárboles vuelve a ser un árbol binario.

Por eso una operación sobre todo el árbol puede expresarse como:

```text
resolver el subárbol izquierdo

resolver el nodo

resolver el subárbol derecho
```

El caso base es:

```text
u == null
```

porque no existe ningún nodo que procesar.

### 36. Traza de inorder sobre un subárbol

Considera:

```text
      20
     /  \
   10   30
```

Ejecutamos:

```text
inorder(20)
```

Primero:

```text
inorder(10)
```

Dentro de 10:

```text
inorder(null)
    retorna

imprime 10

inorder(null)
    retorna
```

Regresamos a 20.

Después:

```text
imprime 20
```

Luego:

```text
inorder(30)
```

que imprime:

```text
30
```

Resultado:

```text
10 20 30
```

### 37. Por qué inorder produce claves ordenadas en un BST

Esta propiedad no surge por casualidad.

Para cualquier nodo `u`, el invariante garantiza:

```text
todas las claves
del subárbol izquierdo
son menores que u.x
```

El recorrido inorder imprime primero ese subárbol.

Después imprime:

```text
u.x
```

Luego el invariante garantiza:

```text
todas las claves
del subárbol derecho
son mayores que u.x
```

y el recorrido imprime ese subárbol al final.

Por tanto:

```text
menores
nodo
mayores
```

Si la misma propiedad se cumple recursivamente dentro de ambos subárboles, el resultado completo está ordenado.

### 38. Inorder ordenado no verifica toda la representación

Supongamos:

```text
          40
        /    \
      20      60
```

y que:

```text
40.left == 20
40.right == 60
```

pero accidentalmente:

```text
20.parent == 60
```

El recorrido inorder utiliza:

```text
left
right
```

No utiliza:

```text
parent
```

Por tanto podría seguir produciendo:

```text
20 40 60
```

aunque el campo `parent` sea incorrecto.

Lección:

```text
una prueba observable
puede verificar parte de un invariante
sin verificar toda la representación
```

Esta idea continúa el enfoque de las semanas anteriores.

### 39. Profundidad y altura

Para analizar el costo necesitamos describir qué tan largos pueden ser los caminos del árbol.

#### Profundidad de un nodo

La **profundidad** mide qué tan lejos está un nodo de la raíz.

Utilizaremos la raíz como nivel inicial.

Por ejemplo:

```text
          40
        /    \
      20      60
     /
   10
```

podemos pensar:

```text
40
    profundidad 0

20 y 60
    profundidad 1

10
    profundidad 2
```

#### Altura h del árbol

Para mantener coherencia con las implementaciones Java posteriores del curso, utilizaremos durante esta secuencia la convención:

```text
árbol vacío
    altura 0

una hoja
    altura 1
```

La altura `h` es el número de nodos del camino más largo desde la raíz hasta una hoja.

Por ejemplo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

tiene:

```text
h = 3
```

porque un camino más largo contiene:

```text
40 -> 20 -> 10
```

o:

```text
40 -> 60 -> 70
```

tres nodos.

#### Otra convención posible

Algunos libros cuentan aristas en lugar de nodos. Con esa convención el mismo árbol tendría altura 2.

La diferencia es una constante.

Para el análisis asintótico:

```text
O(h)
```

no cambia.

Lo importante es usar una convención de manera consistente.

### 40. El costo de la búsqueda depende de h

`findLast(x)` sigue un solo camino. No recorre primero todo el subárbol izquierdo y después el derecho.

En cada nodo decide exactamente una continuación:

```text
left
o
right
```

En el peor caso puede recorrer un camino desde la raíz hasta una hoja. Ese camino tiene longitud proporcional a:

```text
h
```

Por tanto:

```text
findLast(x) -> O(h)
```

Como:

```text
contains(x)
```

llama a `findLast(x)` y luego realiza trabajo constante:

```text
contains(x) -> O(h)
```

### 41. El costo de add también depende de h

La inserción tiene dos fases.

#### Localización

```text
findLast(x)
```

puede recorrer hasta `h` nodos.

Costo:

```text
O(h)
```

#### Modificación

Una vez conocido `p`, insertar modifica una cantidad constante de referencias:

```text
u.parent
p.left o p.right
n
```

Costo:

```text
O(1)
```

Por tanto:

```text
O(h) + O(1)
```

se simplifica a:

```text
add(x) -> O(h)
```

La localización domina el costo.

Esta distinción recuerda Semana 2:

```text
localizar
puede ser costoso

modificar localmente
puede ser barato
```

### 42. Un BST no garantiza O(log n)

Esta es una de las ideas más importantes de la semana.

Es frecuente escuchar:

```text
BST
búsqueda O(log n)
```

Esa afirmación es incompleta.

La afirmación correcta para un BST no balanceado es:

```text
búsqueda -> O(h)
inserción -> O(h)
```

La relación entre `h` y `n` depende de la forma del árbol.

### 43. Árbol de poca altura

Insertamos:

```text
40, 20, 60, 10, 30, 50, 70
```

Resultado:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Tenemos:

```text
n = 7
h = 3
```

Buscar 70 visita:

```text
40
60
70
```

Tres nodos.

En un árbol cuya altura crece aproximadamente como:

```text
log n
```

las operaciones basadas en un camino tienen costo:

```text
O(log n)
```

No necesitamos demostrar todavía una fórmula exacta de balance.

La idea importante es:

```text
poca altura
    caminos cortos
```

### 44. Árbol degenerado

Ahora insertamos exactamente las mismas claves ordenadas:

```text
10, 20, 30, 40, 50, 60, 70
```

Resultado:

```text
10
  \
   20
     \
      30
        \
         40
           \
            50
              \
               60
                 \
                  70
```

Tenemos:

```text
n = 7
h = 7
```

Buscar 70 visita:

```text
10
20
30
40
50
60
70
```

El BST se comporta estructuralmente como una cadena.

En general, si:

```text
h es proporcional a n
```

entonces:

```text
findLast -> O(n)
contains -> O(n)
add -> O(n)
```

Un BST simple no impide esta situación.

### 45. Mismas claves, diferente forma

Este contraste es fundamental.

Con las mismas siete claves podemos obtener:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

o:

```text
10
  \
   20
     \
      30
        \
         40
           \
            50
              \
               60
                 \
                  70
```

El conjunto lógico de claves es el mismo:

```text
10
20
30
40
50
60
70
```

Pero la representación concreta es distinta.

#### Qué cambió

Cambió:

```text
la historia de inserciones
```

Eso produjo otra:

```text
forma del árbol
```

La forma produjo otra:

```text
altura h
```

La altura produjo otro:

```text
costo de búsqueda e inserción
```

Podemos resumir:

```text
historia de operaciones
        ->
forma del árbol
        ->
altura h
        ->
longitud de los caminos
        ->
costo
```

### 46. Relación entre representación, forma y costo

Esta semana vuelve a aparecer el principio central del curso.

#### Semana 1

```text
arreglo
    acceso por índice O(1)
    modificación interna puede exigir desplazamientos
```

#### Semana 2

```text
lista enlazada
    localización por índice puede ser O(n)
    modificación local puede ser O(1)
```

#### Semana 3

```text
Stack y Queue
    restringimos operaciones
    para elegir representaciones adecuadas
```

#### Semana 4

```text
BST
    imponemos un orden jerárquico
    para guiar la búsqueda
```

Pero incluso dentro del mismo BST:

```text
la forma del árbol
```

afecta el costo.

La representación no es solamente el conjunto de campos de una clase.

También importa cómo esos campos quedan conectados después de una historia de operaciones.

### 47. Qué significa que el BST sea una estructura ordenada

Un BST no almacena necesariamente las claves físicamente en orden lineal.

La raíz puede ser:

```text
40
```

aunque el menor valor sea:

```text
10
```

El orden está distribuido en la jerarquía.

```text
menores
a la izquierda

mayores
a la derecha
```

Ese orden permite:

```text
buscar
insertar
recorrer en orden
```

El recorrido inorder convierte ese orden jerárquico en una secuencia creciente.

### 48. Correctitud de la búsqueda

Podemos justificar la búsqueda mediante el invariante.

Supongamos que estamos en un nodo `w`.

#### Si x < w.x

Por el invariante:

```text
todo valor del subárbol derecho > w.x
```

Entonces `x` no puede encontrarse allí.

Continuar por:

```text
w.left
```

no descarta ninguna posición válida para `x`.

#### Si x > w.x

Por el invariante:

```text
todo valor del subárbol izquierdo < w.x
```

Entonces `x` no puede estar allí.

Continuamos por:

```text
w.right
```

#### Si x == w.x

La clave fue encontrada.

Esta argumentación muestra que el algoritmo y el invariante no pueden estudiarse por separado.

### 49. Correctitud de la inserción

Supongamos que `x` no existe y:

```text
p = findLast(x)
```

La búsqueda terminó al intentar seguir una referencia `null` desde `p`.

#### Si x < p.x

El lugar libre compatible con el camino es:

```text
p.left
```

Insertamos allí.

#### Si x > p.x

El lugar libre compatible es:

```text
p.right
```

Insertamos allí.

El nuevo nodo se coloca exactamente en la posición a la que la búsqueda de `x` habría intentado continuar.

Por eso se preserva el orden.

### 50. Casos que debe distinguir add

Aunque no estamos estudiando eliminación todavía, la inserción ya exige distinguir algunos estados.

#### Árbol vacío

```text
p == null
```

El nuevo nodo se convierte en:

```text
root
```

#### Clave existente

```text
p.x == x
```

No se inserta.

#### Clave menor que p.x

```text
p.left = u
u.parent = p
```

#### Clave mayor que p.x

```text
p.right = u
u.parent = p
```

No es necesario introducir más casos estructurales esta semana.

### 51. Qué errores pueden romper el BST durante add

#### Error 1. Elegir el lado incorrecto

Si:

```text
x < p.x
```

pero hacemos:

```java
p.right = u;
```

rompemos el invariante de orden.

#### Error 2. No mantener parent

Podríamos obtener:

```text
p.left == u
```

pero:

```text
u.parent == null
```

La clave podría aparecer correctamente en inorder, pero la representación seguiría siendo inconsistente.

#### Error 3. Aumentar n antes de detectar duplicados

Entonces:

```text
n
```

dejaría de coincidir con el número real de nodos.

#### Error 4. Crear otra raíz cuando el árbol no está vacío

`root` debe seguir siendo el punto de entrada al árbol completo.

#### Error 5. Insertar duplicados

Eso rompería la especificación del ADT como conjunto de claves distintas.

### 52. Una batería mínima de razonamiento

Para comprobar una implementación no basta usar una única secuencia.

Conviene probar al menos:

```text
árbol vacío

primera inserción

inserción a la izquierda

inserción a la derecha

búsqueda de raíz

búsqueda de clave interna

búsqueda ausente

duplicado

inorder

size

historia que produce poca altura

historia que produce árbol degenerado
```

Estas pruebas obligan a verificar diferentes partes de la representación.

### 53. Ejemplo integrado

Construimos:

```text
40, 20, 60, 10, 30, 50, 70
```

Obtenemos:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Estado:

```text
root.x = 40
n = 7
```

Relaciones:

```text
20.parent = 40
60.parent = 40

10.parent = 20
30.parent = 20

50.parent = 60
70.parent = 60
```

Búsquedas:

```text
contains(30) -> true

contains(55) -> false
```

Inserción duplicada:

```text
add(30) -> false
n sigue siendo 7
```

Inorder:

```text
10 20 30 40 50 60 70
```

Complejidad:

```text
findLast -> O(h)

contains -> O(h)

add -> O(h)
```

Para este árbol:

```text
h = 3
```

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

La relación conceptual es:

```text
Node.x
    clave almacenada

Node.left
    raíz del subárbol izquierdo

Node.right
    raíz del subárbol derecho

Node.parent
    padre del nodo

root
    raíz del BST completo

n
    número de nodos
```

Los métodos principales de esta semana son:

```text
findLast
contains
add
inorder
size
```

No necesitamos agregar más operaciones para alcanzar el objetivo conceptual de Semana 4.

### 55. Cómo leer findLast y add juntos

No conviene estudiar:

```text
findLast
```

y:

```text
add
```

como dos algoritmos aislados.

La relación correcta es:

```text
             findLast(x)
             /         \
            /           \
      x existe        x no existe
          |                |
          v                v
      contains         lugar donde
      puede ser true    se insertaría
                           |
                           v
                         add
```

El diseño reutiliza una sola lógica de búsqueda.

### 56. Cómo leer inorder junto al invariante

Tampoco conviene memorizar:

```text
left
node
right
```

sin entender por qué.

La relación correcta es:

```text
invariante BST

izquierda < nodo < derecha
            |
            v
inorder

izquierda
nodo
derecha
            |
            v
salida creciente
```

El recorrido obtiene significado gracias al invariante.

### 57. Cómo leer O(h) junto a la representación

La complejidad tampoco debe memorizarse como una tabla.

```text
findLast -> O(h)
```

porque el método sigue:

```text
un único camino
```

La longitud máxima de ese camino está limitada por:

```text
h
```

`contains` reutiliza `findLast`.

`add` reutiliza `findLast` y después modifica unas pocas referencias.

Por tanto:

```text
findLast -> O(h)
contains -> O(h)
add      -> O(h)
```

La forma del árbol determina `h`.

### 58. Qué no significa O(h)

`O(h)` no afirma que siempre visitamos exactamente `h` nodos.

Una búsqueda puede terminar antes.

Por ejemplo:

```text
contains(40)
```

en:

```text
          40
        /    \
      20      60
```

termina en la raíz.

El costo de peor caso se expresa mediante la longitud máxima que podría tener el camino relevante.

### 59. Qué significa razonablemente balanceado

No estudiaremos todavía un algoritmo de balanceo. Usaremos la expresión **razonablemente balanceado** solamente de manera descriptiva.

Queremos distinguir árboles cuya altura es relativamente pequeña respecto de `n` de árboles que se aproximan a una cadena.

Por ejemplo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

tiene ramas de longitudes parecidas.

En cambio:

```text
10
  \
   20
     \
      30
        \
         40
```

está claramente degenerado hacia un lado.

No introducimos todavía:

```text
rotaciones
factor de balance
AVL
```

Esos mecanismos corresponden a una etapa posterior.

### 60. Una pregunta que queda abierta

Considera:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Sabemos insertar una nueva clave.

Por ejemplo:

```text
55
```

La búsqueda:

```text
40 -> 60 -> 50 -> right null
```

nos dice dónde insertarla.

Pero ahora imagina que queremos retirar:

```text
60
```

No resolveremos todavía esa operación.

Solo observa el problema.

60 conecta dos subárboles:

```text
50
70
```

Cualquier solución futura tendrá que preservar:

```text
alcanzabilidad

parent

root si fuera necesario

n

orden BST
```

La Semana 5 partirá de esas obligaciones.

### 61. Síntesis total

La Semana 4 introduce la primera estructura jerárquica central del curso.

Un BST combina:

```text
árbol binario
+
claves comparables
+
invariante de orden
```

La representación utiliza:

```text
root
n

Node.x
Node.left
Node.right
Node.parent
```

El invariante establece:

```text
subárbol izquierdo < nodo < subárbol derecho
```

Gracias a ese invariante, una búsqueda no explora todo el árbol.

En cada nodo decide:

```text
menor
    izquierda

mayor
    derecha

igual
    encontrado
```

`findLast(x)` reutiliza ese camino y conserva el último nodo visitado.

Eso permite construir:

```text
contains(x)
```

y también determinar la posición de:

```text
add(x)
```

La inserción debe preservar:

```text
root
parent
n
orden BST
unicidad de claves
```

El recorrido inorder visita:

```text
left
nodo
right
```

y produce las claves en orden creciente porque utiliza directamente la propiedad de orden del BST.

Finalmente:

```text
findLast -> O(h)
contains -> O(h)
add      -> O(h)
```

La altura depende de la forma del árbol.

La forma depende de cómo las inserciones construyeron la representación.

Por eso la idea final de la semana es:

```text
representación
        ->
forma
        ->
altura
        ->
camino
        ->
costo
```

La siguiente semana conservará este mismo BST y planteará una operación más delicada:

```text
eliminar
```

Antes de estudiarla, la búsqueda, el invariante, `parent`, `root`, `n` e inorder deben estar completamente comprendidos.
