### Ejercicios de la Semana 3

Estos ejercicios son opcionales y no requieren entrega obligatoria. El objetivo no es repetir la Actividad 3 ni reproducir de memoria `push()`, `pop()`, `resize()` o `remove()` tal como fueron desarrollados en clase.

La mayoría de los problemas exige combinar varias ideas de las Semanas 1, 2 y 3, reconstruir estados, detectar implementaciones incorrectas, preservar invariantes, separar comportamiento abstracto de representación y justificar la complejidad sin depender de prueba y error.

Los temas centrales utilizados son:

```text
ADT restringidos sobre secuencias
Stack y política LIFO
push, pop, peek, isEmpty y size
LinkedStack
head y n
invariantes de una pila enlazada
uso algorítmico de una pila

Queue y política FIFO
add, remove, peek y size
ArrayQueue circular
j, n y capacidad
índice lógico frente a índice físico
a[(j+k) % a.length]
avance modular
resize preservando orden lógico
costo amortizado

Deque
operaciones por ambos extremos
DLList como representación natural
elección de representación
comparación con arreglos dinámicos y listas enlazadas
```

No se requiere utilizar `java.util.Stack`, `java.util.ArrayDeque`, `Queue<E>` ni otras estructuras de Java Collections.

Los ejercicios de la sección C son de ampliación opcional. Introducen aplicaciones o variantes que no forman parte del núcleo evaluable de la Semana 3.

### A. Consolidación esencial

#### Ejercicio 1. Identificar el ADT a partir del comportamiento

Tres estructuras desconocidas, `A`, `B` y `C`, reciben los valores:

```text
10
20
30
40
```

en ese orden.

Después se realizan cuatro eliminaciones y se observan estas salidas:

```text
A -> 40, 30, 20, 10
B -> 10, 20, 30, 40
C -> 10, 40, 20, 30
```

Sin asumir ninguna implementación concreta:

1. identifica cuál comportamiento es compatible con una pila,
2. identifica cuál comportamiento es compatible con una cola,
3. explica por qué el comportamiento de `C` no puede describirse únicamente como LIFO o FIFO,
4. indica qué operaciones adicionales tendría que permitir un ADT para hacer posible el comportamiento de `C`,
5. explica por qué conocer el orden de salida permite razonar sobre el ADT, pero no permite determinar si internamente se utilizaron arreglos o nodos,
6. propone dos representaciones diferentes para `A`,
7. propone dos representaciones diferentes para `B`,
8. para cada representación propuesta, identifica una información interna que deba mantenerse.

Finalmente responde:

> ¿Cuál es la diferencia entre identificar el comportamiento de un ADT e identificar su representación?.

#### Ejercicio 2. Reconstruir una cola circular a partir de información parcial

Se sabe que una `ArrayQueue` cumple:

```text
a.length = 8
j = 6
n = 5
```

y su vista lógica es:

```text
[11, 22, 33, 44, 55]
```

Sin ejecutar código:

1. determina la posición física de cada elemento lógico,
2. dibuja el arreglo completo indicando las posiciones que no pertenecen actualmente a la cola,
3. determina qué retornaría `peek()`,
4. determina en qué posición física debería almacenarse un nuevo elemento `66` si existe capacidad,
5. después de agregar `66`, indica `j`, `n` y la vista lógica,
6. después de una eliminación, indica el valor retirado y el nuevo `j`,
7. explica qué parte del estado pertenece al orden lógico y qué parte a la representación física,
8. construye un segundo estado físico diferente que represente exactamente la misma secuencia lógica `[11, 22, 33, 44, 55]`,
9. explica por qué dos arreglos físicamente distintos pueden representar la misma cola.

No escribas `resize()` ni `remove()` completos. El objetivo es trabajar con el invariante:

```text
elemento lógico k
    está en
a[(j+k) % a.length]
```

### B. Retos integradores

#### Reto 1. Una traza posible y dos trazas imposibles

Una estructura comienza vacía y solo ofrece estas operaciones:

```text
insert(x)
inspect()
remove()
size()
```

Se desconoce si el ADT es Stack, Queue o Deque.

Se observan tres historiales independientes.

Historial A:

```text
insert(4)
insert(7)
insert(9)
inspect() -> 9
remove()  -> 9
inspect() -> 7
```

Historial B:

```text
insert(4)
insert(7)
insert(9)
inspect() -> 4
remove()  -> 4
inspect() -> 7
```

Historial C:

```text
insert(4)
insert(7)
insert(9)
inspect() -> 7
remove()  -> 9
inspect() -> 4
```

Para cada historial:

1. determina si puede corresponder a Stack,
2. determina si puede corresponder a Queue,
3. determina si podría corresponder a un Deque con una política adicional,
4. identifica la primera observación que descarta cada ADT incompatible,
5. explica qué significado tendría `inspect()` en cada ADT,
6. indica qué información falta para determinar una representación concreta.

Después diseña un historial mínimo que permita distinguir sin ambigüedad entre Stack y Queue usando solamente tres inserciones y una eliminación.

#### Reto 2. Una LinkedStack aparentemente correcta tiene dos estados inválidos

Se implementa una pila enlazada con:

```java
private Node head;
private int n;
```

Un depurador muestra estos estados.

Estado A:

```text
head -> C -> B -> A -> null
n = 3
```

Estado B:

```text
head -> C -> B -> A -> null
n = 2
```

Estado C:

```text
head -> C -> B -> C -> B -> ...
n = 2
```

Estado D:

```text
head = null
n = 1
```

Responde:

1. cuáles estados son válidos,
2. qué parte del invariante viola cada estado inválido,
3. qué retornaría `isEmpty()` si se implementa únicamente como `n == 0`,
4. por qué `size()` podría producir una salida aparentemente razonable aunque la cadena de nodos esté dañada,
5. qué ocurriría al intentar recorrer el Estado C hasta `null`,
6. diseña un método conceptual `checkInvariant()` capaz de detectar los cuatro tipos de inconsistencia,
7. justifica la complejidad de esa comprobación,
8. explica por qué una operación puede retornar el valor esperado y aun así dejar la pila incorrecta.

No implementes nuevamente `push()` ni `pop()`.

#### Reto 3. El mismo resultado visible, distinto estado interno

Dos colas producen:

```text
logicalView() = [20, 30, 40, 50]
```

La primera tiene:

```text
a = [20, 30, 40, 50, _, _, _, _]
j = 0
n = 4
```

La segunda tiene:

```text
a = [40, 50, _, _, _, _, 20, 30]
j = 6
n = 4
```

Ambas tienen:

```text
a.length = 8
```

Responde:

1. demuestra mediante el invariante circular que ambas representan la misma secuencia,
2. determina la posición física del elemento lógico de índice 2 en ambas,
3. determina dónde se insertaría `60` en ambas,
4. determina qué posición se libera después de un `remove()` en ambas,
5. explica qué información no puede inferirse observando únicamente `logicalView()`,
6. indica qué condiciones mínimas debe cumplir `j`,
7. explica por qué el segundo estado no está "desordenado",
8. propone una operación que haga que la primera representación también quede físicamente envuelta sin cambiar la política FIFO.

#### Reto 4. Un resize que copia todos los datos y aun así es incorrecto

Un estudiante propone:

```java
private void resize() {
    Integer[] b = new Integer[Math.max(1, 2 * n)];

    for (int k = 0; k < n; k++) {
        b[k] = a[k];
    }

    a = b;
    j = 0;
}
```

El método:

```text
crea un arreglo nuevo
copia exactamente n posiciones
asigna a = b
fija j = 0
```

A primera vista parece correcto.

Sin ejecutarlo:

1. construye el estado válido más pequeño que puedas donde esta implementación cambie el orden FIFO,
2. dibuja el arreglo antes del `resize()`,
3. determina la vista lógica correcta antes de copiar,
4. ejecuta manualmente cada iteración del ciclo,
5. determina qué contenido queda en `b`,
6. identifica la suposición incorrecta realizada por el código,
7. escribe solamente la expresión que debe reemplazar `a[k]`,
8. explica por qué esa expresión preserva el elemento lógico `k`,
9. justifica que la versión correcta sigue siendo `O(n)`,
10. explica por qué dos algoritmos con la misma complejidad asintótica pueden diferir en correctitud.

#### Reto 5. Una cola correcta pero innecesariamente lenta

Se propone implementar una cola sobre un arreglo con esta idea:

```text
add(x)
    escribir al final

remove()
    guardar a[0]
    desplazar a[1..n-1] una posición a la izquierda
    reducir n
```

Otra implementación mantiene:

```text
a
j
n
```

y utiliza circularidad.

Compara ambas estrategias para una secuencia de `m` operaciones alternadas:

```text
add
remove
add
remove
...
```

cuando la cola mantiene aproximadamente `n` elementos durante buena parte de la ejecución.

Responde:

1. qué trabajo dominante aparece en cada `remove()` de la primera estrategia,
2. qué trabajo dominante aparece en un `remove()` ordinario de la segunda,
3. por qué mover `j` puede reemplazar muchos desplazamientos,
4. qué costo adicional introduce la necesidad ocasional de `resize()`
5. por qué ese costo no obliga a describir todas las eliminaciones como `O(n)`,
6. construye una tabla con el costo de 10 eliminaciones suponiendo que solo una ejecuta `resize()`,
7. explica la diferencia entre costo de una llamada individual y costo amortizado,
8. indica qué invariante adicional hace más delicada la implementación circular,
9. decide cuál implementación elegirías para una cola grande y justifica la decisión.

#### Reto 6. Crecimiento, reducción y ausencia de oscilación inmediata

La cola circular utiliza:

```text
crecer
    cuando n + 1 > a.length

nueva capacidad
    max(1, 2*n)

reducir después de eliminar
    cuando a.length >= 3*n

nueva capacidad
    max(1, 2*n)
```

Se parte de:

```text
a.length = 8
j = 0
n = 8
```

Diseña una secuencia que contenga:

```text
al menos un crecimiento
varias eliminaciones
al menos una reducción
nuevas inserciones
```

Registra únicamente cuando cambie la capacidad:

| Evento | `n` antes | capacidad antes | operación | `n` después | capacidad después |
|---|---:|---:|---|---:|---:|

Después responde:

1. cuántas operaciones ordinarias pueden ocurrir entre dos cambios de capacidad,
2. por qué no se reduce el arreglo apenas aparece una sola posición libre,
3. qué problema produciría crecer y reducir alrededor del mismo valor de `n`,
4. por qué separar los umbrales ayuda al análisis amortizado,
5. qué información debe conservar `resize()` además de los valores,
6. explica intuitivamente por qué una secuencia larga no puede estar formada únicamente por redimensionamientos costosos.

No se requiere una prueba formal mediante método potencial.

#### Reto 7. Elegir estructura para cuatro sistemas diferentes

Se deben diseñar cuatro componentes.

Sistema A:

```text
cada operación nueva debe poder deshacerse
solo interesa deshacer la más reciente
no se necesita acceder por índice
```

Sistema B:

```text
las solicitudes deben procesarse exactamente
en orden de llegada
el volumen es grande
se desea evitar desplazar elementos
```

Sistema C:

```text
se insertan y retiran elementos
por cualquiera de los dos extremos
no se necesita acceso arbitrario por índice
```

Sistema D:

```text
90 % de las operaciones consultan get(i)
10 % agregan elementos al final
```

Puedes elegir entre:

```text
arreglo dinámico
LinkedStack
SLList
DLList
ArrayQueue circular
Deque basado en DLList
```

Para cada sistema:

1. selecciona una representación,
2. identifica el ADT apropiado,
3. indica las operaciones dominantes,
4. explica qué información mantiene directamente la representación,
5. indica el invariante principal,
6. justifica los costos relevantes,
7. identifica una alternativa que también podría funcionar,
8. explica por qué tu primera elección es preferible para ese patrón concreto.

Finalmente responde:

> ¿Por qué no existe una estructura "más eficiente" sin especificar primero el patrón de operaciones?.

#### Reto 8. Un sistema que necesita simultáneamente LIFO y FIFO

Un programa recibe trabajos de usuarios.

Los trabajos nuevos deben ejecutarse en orden de llegada:

```text
FIFO
```

Sin embargo, mientras un trabajo todavía no ha sido enviado a ejecución, el usuario puede deshacer únicamente la modificación más reciente realizada sobre ese trabajo:

```text
LIFO
```

Diseña conceptualmente una solución que utilice dos ADT diferentes.

No necesitas implementar el sistema completo.

Responde:

1. qué información debería almacenarse en la cola,
2. qué información debería almacenarse en una pila asociada a cada trabajo,
3. qué operación representa "llega un nuevo trabajo",
4. qué operación representa "ejecutar el siguiente trabajo",
5. qué operación representa "deshacer el último cambio",
6. realiza una traza con tres trabajos y al menos dos modificaciones por trabajo,
7. identifica qué parte del sistema sigue FIFO y cuál sigue LIFO,
8. explica por qué una única pila no representa naturalmente todo el comportamiento,
9. explica por qué una única cola tampoco lo representa,
10. justifica las operaciones que pueden mantenerse `O(1)` o `O(1)` amortizado según la representación elegida

El objetivo es comprender que un algoritmo puede combinar varios ADT, cada uno encargado de una política distinta.

#### Reto 9. Diseñar un Deque sobre la representación de la Semana 2

Se parte de una `DLList` circular con nodo `dummy`:

```text
dummy <-> 10 <-> 20 <-> 30 <-> dummy
```

No debes implementar una clase `Deque` completa.

Diseña conceptualmente estas cuatro operaciones:

```java
void addFirst(int x)
void addLast(int x)
int removeFirst()
int removeLast()
```

Para cada operación:

1. identifica los nodos vecinos que ya están localizados mediante `dummy`,
2. dibuja el estado antes y después,
3. indica qué referencias `prev` y `next` cambian,
4. indica cómo cambia `n`,
5. analiza la lista vacía,
6. analiza una lista con un único nodo,
7. explica por qué el centinela reduce casos especiales,
8. justifica por qué la modificación puede ser `O(1)`.

Después compara esta representación con una `SLList`.

Explica exactamente cuál de las cuatro operaciones deja de ser naturalmente `O(1)` en una `SLList` y qué información estructural falta.

#### Reto 10. Diseñar una batería de pruebas para Stack y Queue

Recibes dos implementaciones desconocidas:

```text
LinkedStack
ArrayQueue
```

Ambas compilan y superan ejemplos sencillos.

Debes diseñar una batería mínima de pruebas capaz de descubrir errores internos.

Para `LinkedStack` intenta descubrir errores en:

```text
head
n
estado vacío
paso de vacío a no vacío
paso de un elemento a vacío
orden LIFO
cadena next
consistencia entre n y nodos alcanzables
```

Para `ArrayQueue` intenta descubrir errores en:

```text
j
n
capacidad
orden FIFO
envoltura circular
posición lógica frente a física
crecimiento
reducción
resize con j distinto de cero
normalización j = 0
```

Para cada prueba especifica:

```text
estado inicial
operaciones
resultado observable esperado
estado interno esperado
invariante que se intenta comprobar
tipo de error que podría detectar
```

Incluye obligatoriamente:

1. una prueba donde la salida visible sea correcta pero el estado interno sea inválido,
2. una prueba donde `j` esté cerca del final del arreglo,
3. una prueba que provoque envoltura circular,
4. una prueba que provoque `resize()` con `j != 0`,
5. una prueba que vacíe completamente la estructura,
6. una prueba donde una operación inválida deba ser rechazada.

Finalmente responde:

> ¿Por qué una colección de pruebas que solo compara valores retornados no es suficiente para estudiar una estructura de datos?.

### C. Ampliación opcional

Los siguientes retos utilizan ideas que no forman parte del núcleo obligatorio de la Semana 3. Pueden resolverse después de dominar los retos anteriores.

No deben interpretarse como contenido necesario para la evaluación de esta semana.

#### Reto opcional 1. Delimitadores balanceados con una pila

Se recibe una cadena que puede contener:

```text
()
[]
{}
```

y otros caracteres.

Ejemplos válidos:

```text
(a+[b*c])
{[()]}
x+(y*z)
```

Ejemplos inválidos:

```text
([)]
(()
{]
```

Diseña un algoritmo que utilice una pila para decidir si los delimitadores están correctamente balanceados.

No escribas código inmediatamente.

Primero responde:

1. qué elementos deben insertarse en la pila,
2. qué debe ocurrir cuando aparece un símbolo de cierre,
3. qué condición debe cumplirse entre el cierre actual y el elemento del tope,
4. qué significa encontrar un cierre cuando la pila está vacía,
5. qué condición final debe cumplirse al terminar la cadena,
6. realiza una traza completa para `([{}])`,
7. realiza una traza completa para `([)]`,
8. justifica el tiempo `O(n)`,
9. determina el peor espacio auxiliar requerido.

Después escribe una versión iterativa utilizando únicamente el ADT Stack.

#### Reto opcional 2. Evaluación de una expresión postfija

Considera:

```text
8 3 2 * + 5 -
```

Cada número se inserta en una pila.

Cuando aparece un operador binario:

```text
+
-
*
/
```

se retiran los dos operandos necesarios, se calcula el resultado y se vuelve a insertar.

Responde:

1. realiza la traza completa,
2. explica por qué el orden de los dos `pop()` importa para resta y división,
3. determina qué condición debe cumplir la pila al finalizar una expresión válida,
4. construye una expresión postfija inválida por falta de operandos,
5. construye otra que termine con demasiados valores en la pila,
6. diseña un algoritmo iterativo,
7. justifica su complejidad.

No se requiere conversión de notación infija a postfija.

#### Reto opcional 3. Una pila que también conoce el mínimo

Se desea un ADT con:

```java
void push(int x)
int pop()
int peek()
int min()
boolean isEmpty()
```

y se desea que:

```text
push
pop
peek
min
```

sean O(1).

No se permite buscar el mínimo recorriendo toda la pila cada vez.

Diseña una representación.

Puedes considerar, entre otras posibilidades:

```text
guardar información adicional en cada nodo

o

mantener una segunda pila auxiliar
```

Para tu propuesta:

1. especifica el estado interno,
2. define el invariante adicional,
3. realiza una traza con `5, 2, 7, 1, 3`,
4. ejecuta tres `pop()`,
5. indica el valor de `min()` después de cada operación,
6. explica qué información adicional hace posible `O(1)`,
7. analiza el costo espacial,
8. compara tu solución con calcular el mínimo mediante un recorrido.

#### Reto opcional 4. Diseñar un deque circular

Se desea implementar un deque basado en arreglo circular.

La estructura mantiene:

```text
a
j
n
```

donde `j` representa la posición física del primer elemento lógico.

Debes soportar conceptualmente:

```java
void addFirst(int x)
void addLast(int x)
int removeFirst()
int removeLast()
```

No utilices `java.util.ArrayDeque`.

Responde:

1. qué posición física corresponde al primer elemento,
2. qué posición corresponde al último,
3. cómo debe modificarse `j` al insertar por el frente,
4. cómo debe modificarse `j` al eliminar por el frente,
5. qué expresión modular permite localizar el final,
6. realiza una traza donde la estructura se envuelva por ambos lados,
7. explica qué debe preservar `resize()`,
8. explica por qué un `resize()` debería normalizar la secuencia lógica,
9. justifica qué operaciones pueden tener costo `O(1)` amortizado,
10. compara esta representación con un deque basado en `DLList`.

Este reto extiende la cola circular estudiada, pero su implementación completa no forma parte del contenido obligatorio.

#### Reto opcional 5. Dos pilas dentro de un solo arreglo

Se desea almacenar dos pilas dentro de un único arreglo:

```text
a[0..m-1]
```

La primera crece desde el extremo izquierdo.

La segunda crece desde el extremo derecho.

Conceptualmente:

```text
Stack A ->        <- Stack B

[ A A A _ _ _ B B ]
```

Diseña la representación.

Responde:

1. qué índices necesitas mantener,
2. cómo detectar que ambas pilas ya no tienen espacio,
3. cómo implementar conceptualmente `pushA`,
4. cómo implementar conceptualmente `pushB`,
5. cómo implementar `popA`,
6. cómo implementar `popB`,
7. qué invariante separa las regiones de ambas pilas,
8. realiza una traza que termine exactamente con el arreglo lleno,
9. explica qué ventaja espacial tiene compartir el arreglo,
10. identifica qué dificultad aparece si una pila crece mucho más que la otra.

No se requiere implementar redimensionamiento.

### D. Preguntas de cierre

#### Pregunta 1. La restricción puede simplificar la estructura

Explica por qué una pila puede necesitar menos información estructural que una lista general.

Tu respuesta debe relacionar:

```text
operaciones permitidas
head
localización
modificación
complejidad
```

#### Pregunta 2. Circularidad lógica no significa memoria circular

Explica con tus propias palabras qué significa afirmar que una `ArrayQueue` es circular.

Tu explicación debe distinguir:

```text
arreglo físico
orden lógico
j
módulo
posición lógica
posición física
```

#### Pregunta 3. Una operación O(n) no impide un costo amortizado O(1)

Explica cómo pueden ser simultáneamente verdaderas estas afirmaciones:

```text
resize() -> O(n)

add() de ArrayQueue -> O(1) amortizado

remove() de ArrayQueue -> O(1) amortizado
```

No utilices únicamente las fórmulas.

Explica qué ocurre a lo largo de una secuencia de operaciones.

#### Pregunta 4. La elección de representación es parte del algoritmo

Compara:

```text
LinkedStack
ArrayQueue circular
Deque basado en DLList
```

Para cada uno indica:

```text
ADT
política de acceso
estado interno principal
invariante central
operaciones favorecidas
costo relevante
```

Concluye explicando por qué:

```text
Stack
Queue
Deque
```

no son sinónimos de:

```text
lista enlazada
arreglo
lista doble
```

El ADT describe el comportamiento.

La representación es una decisión utilizada para implementar ese comportamiento.
