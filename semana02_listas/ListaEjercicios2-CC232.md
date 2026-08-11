### Ejercicios de la Semana 2

Estos ejercicios son opcionales y no requieren entrega obligatoria. La mayoría de los problemas exige combinar varias ideas de la Semana 2, reconstruir estados, preservar invariantes, modificar referencias y justificar la complejidad sin depender de prueba y error.

Los temas utilizados son únicamente los trabajados durante la semana:

```text
representación enlazada
nodos y referencias
head, tail y n
SLList
invariantes
casos frontera
prev y next
DLList
nodo centinela dummy
recorrido desde el extremo más cercano
localización frente a modificación
inserción y eliminación local
comparación con arreglos dinámicos
complejidad
```

No se requiere utilizar `java.util.LinkedList`, recursión, iteradores ni otras estructuras de Java Collections.

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir una estructura a partir de observaciones parciales

Se sabe que una `SLList` contiene cinco elementos y que:

```text
n = 5
head.x = 8
tail.x = 21
```

Además, al comenzar en `head` y seguir `next`, se observan los valores:

```text
8, 3, 14, 5, 21
```

Responde sin escribir código:

1. dibuja todos los nodos y referencias,
2. indica el valor de `next` del último nodo,
3. indica qué debe ocurrir con `head`, `tail` y `n` si se eliminan sucesivamente los cinco nodos desde la cabecera.
4. explica en qué momento `head` y `tail` deben volver a ser `null`,
5. construye un estado que conserve los mismos cinco valores y el mismo `n`, pero viole el invariante de la estructura,
6. explica por qué observar únicamente los valores impresos no basta para demostrar que la representación interna es correcta.

Después considera una `DLList` que contiene la misma secuencia:

```text
8, 3, 14, 5, 21
```

Dibuja la estructura incluyendo `dummy`, `prev` y `next`.

Explica qué información adicional permite verificar una lista doble que no puede comprobarse observando únicamente un recorrido hacia adelante.

#### Ejercicio 2. Elegir representación a partir de un patrón de operaciones

Se deben implementar cuatro aplicaciones distintas.

Aplicación A:

```text
90 % de las operaciones son get(i)
10 % son inserciones al final
```

Aplicación B:

```text
se insertan elementos principalmente al inicio
se eliminan elementos principalmente al inicio
casi nunca se accede por índice
```

Aplicación C:

```text
se dispone frecuentemente de una referencia directa al nodo
se insertan y eliminan elementos alrededor de ese nodo
se necesita avanzar y retroceder
```

Aplicación D:

```text
se recibe siempre un índice i
se consulta o elimina el elemento de esa posición
```

Para cada aplicación elige entre:

```text
arreglo dinámico
SLList
DLList
```

No basta con indicar una estructura.

Para cada caso debes justificar:

1. qué información mantiene la representación,
2. cuál es la operación dominante,
3. dónde aparece el costo de localización,
4. dónde aparece el costo de modificación,
5. qué ventaja concreta se obtiene,
6. qué costo o desventaja se acepta.

Finalmente responde:

> ¿Por qué decir que una estructura es "más rápida" sin especificar las operaciones no constituye una comparación válida?

### B. Retos integradores

#### Reto 1. Diagnóstico de una SLList que parece funcionar

Un estudiante implementa una operación nueva:

```java
int moveFirstToLast() {
    if (n == 0) {
        return -1;
    }

    int x = head.x;
    tail.next = head;
    tail = head;
    head = head.next;

    return x;
}
```

La intención es transformar:

```text
10 -> 20 -> 30 -> null
```

en:

```text
20 -> 30 -> 10 -> null
```

No ejecutes el código inicialmente.

Analiza la implementación para los siguientes estados:

```text
lista vacía
lista con un nodo
lista con dos nodos
lista con cuatro nodos
```

Para cada caso:

1. dibuja el estado antes de la operación,
2. realiza la traza asignación por asignación,
3. indica qué referencias cambian,
4. determina si la operación termina con una lista válida,
5. identifica cualquier ciclo accidental,
6. verifica `head`, `tail` y `tail.next`,
7. explica si `n` debe cambiar,
8. propone una versión correcta,
9. justifica su complejidad.

La versión corregida debe ejecutar la rotación sin crear ni eliminar nodos.

#### Reto 2. Invertir una SLList sin estructuras auxiliares

Se desea agregar:

```java
void reverse()
```

La operación debe transformar:

```text
head
 |
 v
10 -> 20 -> 30 -> 40 -> null
                 ^
                 |
                tail
```

en:

```text
head
 |
 v
40 -> 30 -> 20 -> 10 -> null
                 ^
                 |
                tail
```

Restricciones:

```text
no crear nodos nuevos
no usar arreglos
no usar otra lista
no usar recursión
no cambiar los valores x de los nodos
```

Debes trabajar únicamente con referencias.

Resuelve:

1. explica qué referencia se perdería si se modifica `next` sin conservar primero el resto de la lista,
2. identifica el número mínimo de referencias auxiliares que necesitas,
3. escribe una versión iterativa de `reverse()`,
4. muestra una traza completa para cuatro nodos,
5. explica cómo deben actualizarse `head` y `tail`,
6. verifica el caso de lista vacía,
7. verifica el caso de un único nodo,
8. demuestra mediante un argumento informal por qué todos los nodos siguen siendo alcanzables,
9. justifica por qué el tiempo es `O(n)`,
10. justifica por qué el espacio auxiliar es `O(1)`.

No ejecutes la solución hasta terminar la traza manual.

#### Reto 3. Insertar y eliminar después de un nodo conocido

En una `SLList` se desea soportar las operaciones:

```java
void insertAfter(Node w, int x)
Integer removeAfter(Node w)
```

La referencia `w` corresponde a un nodo que ya pertenece a la lista.

No puedes recorrer desde `head` para volver a localizar `w`.

Para `insertAfter` debes considerar:

```text
w es un nodo interno
w es tail
la lista tiene un solo nodo
```

Para `removeAfter` debes considerar:

```text
w.next es un nodo interno
w.next es tail
w es tail
```

Desarrolla ambas operaciones.

Para cada una:

1. dibuja el estado antes y después,
2. identifica qué referencias deben modificarse,
3. indica cuándo debe cambiar `tail`,
4. indica cuándo debe cambiar `n`,
5. decide qué debe retornar `removeAfter` cuando `w` es `tail`,
6. establece una precondición clara para `w`,
7. explica qué ocurriría si `w` no perteneciera realmente a la lista,
8. justifica la complejidad.

Después compara:

```text
insertAfter(w, x)
```

con:

```text
insertar en la posición i
```

y explica por qué una referencia conocida cambia radicalmente el costo de localización.

#### Reto 4. Reparar una DLList parcialmente corrupta

Se supone que la secuencia lógica debe ser:

```text
10, 20, 30, 40
```

El recorrido usando `next` produce correctamente:

```text
dummy -> 10 -> 20 -> 30 -> 40 -> dummy
```

pero las referencias `prev` quedaron así:

```text
10.prev = dummy
20.prev = 10
30.prev = 10
40.prev = 30
dummy.prev = 40
```

No ejecutes ningún programa.

Responde:

1. qué referencias `prev` son incorrectas,
2. qué invariantes se violan,
3. qué recorrido se obtiene comenzando en `dummy.prev` y siguiendo `prev`,
4. por qué `toString()` podría no detectar el problema,
5. cuál es la modificación mínima necesaria para reparar la estructura,
6. qué comprobaciones realizarías después de la reparación,
7. diseña un método conceptual `checkLinks()` que recorra la lista y detecte inconsistencias entre `next` y `prev`,
8. justifica la complejidad de esa verificación.

Después construye otro ejemplo donde el recorrido hacia atrás sea correcto, pero el recorrido hacia adelante esté dañado.

El objetivo es demostrar que una lista doble necesita consistencia en ambas direcciones.

#### Reto 5. Mover un nodo conocido al inicio en O(1)

En una `DLList` se dispone de una referencia directa:

```java
Node w
```

donde `w` es un nodo real de la lista.

Se desea implementar:

```java
void moveToFront(Node w)
```

La operación debe mover `w` inmediatamente después de `dummy`.

Ejemplo:

```text
antes

dummy <-> 10 <-> 20 <-> 30 <-> 40 <-> dummy
                         ^
                         |
                         w
```

debe producir:

```text
después

dummy <-> 30 <-> 10 <-> 20 <-> 40 <-> dummy
```

Restricciones:

```text
no crear nodos
no eliminar nodos
no modificar x
no recorrer la lista
n no debe cambiar
```

Resuelve:

1. identifica primero cómo desconectar `w`,
2. identifica después cómo insertar `w` tras `dummy`,
3. escribe las asignaciones en un orden seguro,
4. verifica los enlaces de los cuatro vecinos afectados,
5. analiza el caso donde `w` ya es el primer nodo,
6. analiza el caso donde `w` es el último nodo,
7. analiza el caso donde la lista tiene un solo nodo,
8. explica qué invariantes deben conservarse,
9. justifica por qué la operación puede ser `O(1)`.

Después responde:

> ¿Por qué la misma operación podría dejar de ser `O(1)` si en lugar de recibir `Node w` recibiera solamente el índice `i`?.

#### Reto 6. Dos algoritmos para eliminar todas las apariciones

Una `DLList` contiene:

```text
4, 7, 4, 9, 4, 2, 4
```

Se desea implementar:

```java
int removeAll(int x)
```

que elimine todas las apariciones de `x` y retorne la cantidad eliminada.

Se proponen dos estrategias.

Estrategia A:

```text
recorrer índices i
usar get(i)
cuando el valor coincide, llamar remove(i)
```

Estrategia B:

```text
recorrer directamente los nodos
cuando el nodo coincide, conservar primero el siguiente
desconectar localmente el nodo
continuar desde la referencia conservada
```

Sin escribir todavía el código:

1. realiza la traza completa de ambas estrategias para `x = 4`,
2. explica qué ocurre con los índices después de cada eliminación en la estrategia A,
3. identifica un posible error si se incrementa `i` después de eliminar,
4. determina el costo de localizar cada posición en la estrategia A,
5. estima el peor costo total de la estrategia A,
6. determina el costo total de la estrategia B,
7. explica por qué conservar el siguiente nodo antes de desconectar el actual evita perder el recorrido,
8. escribe una implementación iterativa de la estrategia B,
9. verifica los casos de lista vacía, ninguna coincidencia y todos los nodos iguales,
10. verifica que `dummy` y `n` queden correctos.

Finalmente explica por qué dos algoritmos que producen el mismo resultado pueden tener costos asintóticos distintos debido únicamente a la forma de recorrer la representación.

#### Reto 7. Una misma historia ejecutada con tres representaciones

Se parte de la secuencia:

```text
10, 20, 30, 40, 50, 60
```

Se ejecuta conceptualmente la siguiente historia:

```text
consultar posición 4
insertar 5 al inicio
eliminar el elemento de posición 3
insertar 70 al final
consultar posición 5
eliminar el primer elemento
```

Analiza cómo se comportaría la historia utilizando:

```text
arreglo dinámico
SLList
DLList
```

No necesitas escribir las tres implementaciones completas.

Construye una tabla con:

| Operación | Arreglo dinámico | SLList | DLList |
|---|---|---|---|
| costo de localizar | | | |
| costo de modificar | | | |
| trabajo dominante | | | |

Después responde:

1. qué representación favorece las consultas por índice,
2. cuál favorece inserciones y eliminaciones locales cuando ya conocemos el nodo,
3. qué operaciones de la historia obligan a recorrer una `SLList`,
4. cuándo una `DLList` puede empezar desde el extremo más cercano,
5. en qué casos un arreglo debe desplazar elementos,
6. si alguna representación es claramente mejor para toda la historia,
7. qué información adicional necesitarías sobre la frecuencia de operaciones antes de elegir una estructura para un sistema real.

El objetivo es separar explícitamente:

```text
localizar
modificar
mantener invariantes
```

#### Reto 8. Diseñar una batería mínima de pruebas estructurales

Recibes dos implementaciones desconocidas:

```text
SLList
DLList
```

Ambas compilan y producen resultados correctos en algunos ejemplos simples.

Debes diseñar una batería pequeña de pruebas capaz de detectar errores en la representación.

Para `SLList` debes intentar descubrir fallas en:

```text
head
tail
n
tail.next
lista vacía
lista con un nodo
paso de vacía a no vacía
paso de no vacía a vacía
inserción al inicio
inserción al final
```

Para `DLList` debes intentar descubrir fallas en:

```text
dummy.next
dummy.prev
next
prev
n
inserción al inicio
inserción al final
eliminación al inicio
eliminación al final
eliminación interna
recorrido hacia adelante
recorrido hacia atrás
```

Para cada prueba indica:

```text
estado inicial
operación o secuencia
estado esperado
invariante que intenta verificar
tipo de error que podría descubrir
```

Incluye al menos una prueba donde:

```text
el contenido lógico impreso sea correcto
pero la estructura interna sea incorrecta
```

y explica por qué esa prueba es necesaria.

Finalmente responde:

> ¿Por qué probar únicamente la salida visible no es suficiente para validar una estructura enlazada?.

### C. Preguntas adicionales

#### Pregunta 1. Localización frente a modificación

Explica con tus propias palabras por qué estas dos afirmaciones pueden ser verdaderas al mismo tiempo:

```text
eliminar un nodo conocido de una DLList puede ser O(1)

remove(i) de una DLList puede requerir O(1 + min(i, n - i))
```

No utilices únicamente las fórmulas.

Describe qué trabajo realiza realmente cada operación.

#### Pregunta 2. Más referencias no significa automáticamente mejor estructura

Compara:

```text
SLList
DLList
```

desde cuatro perspectivas:

```text
cantidad de información estructural
facilidad de modificación local
número de invariantes que deben preservarse
patrón de operaciones para el que resulta adecuada
```

Concluye explicando por qué una lista doble no reemplaza universalmente a una lista simple.

#### Pregunta 3. Ideas conjuntas

Completa y justifica:

```text
Semana 1
representación contigua
    ...

Semana 2
representación enlazada
    ...
```

Tu respuesta debe incluir:

```text
acceso por índice
desplazamientos
recorrido
referencias
localización
modificación
invariantes
complejidad
```

El objetivo final es explicar por qué elegir una estructura de datos significa elegir qué información estará disponible directamente y qué costos se aceptarán para 
las operaciones.
