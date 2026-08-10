### Ejercicios de la Semana 1

Estos ejercicios son opcionales y no requieren entrega obligatoria. El objetivo no es repetir los ejemplos desarrollados en clase. La mayoría de los problemas exige combinar varias ideas de la Semana 1 y justificar las decisiones realizadas.

Los temas utilizados son únicamente los trabajados con los arreglos dinámicos de la semana:

```text
representación
tamaño y capacidad
invariantes
acceso por índice
resize
crecimiento geométrico
inserción al final
inserción indexada
desplazamientos
eliminación
búsqueda secuencial
complejidad
costo amortizado
```

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir un estado desconocido

Se observa únicamente la siguiente salida de depuración:

```text
[12, 5, 9, 20] tamaño=4 capacidad=8
```

Sin observar el código que produjo ese estado, responde:

1. ¿qué representa el valor 4?
2. ¿qué representa el valor 8?
3. ¿qué posiciones del arreglo contienen elementos lógicos?
4. ¿cuál es la primera posición disponible para una inserción al final?
5. ¿qué desigualdad debe cumplirse entre tamaño y capacidad?
6. ¿qué información no puede determinarse únicamente a partir de esta salida?.

Explica qué parte de tus respuestas pertenece a la representación y qué parte pertenece al comportamiento observable de la estructura.

#### Ejercicio 2. Una misma estructura, costos diferentes

Se tiene:

```text
a = [10, 20, 30, 40, 50, _, _, _]
n = 5
```

Compara las siguientes operaciones:

```java
get(3)
set(3, 99)
add(5, 60)
add(0, 60)
remove(4)
remove(0)
indexOf(50)
```

Para cada operación indica:

1. si necesita desplazamientos
2. cuántos elementos como máximo se desplazan o examinan en este estado
3. si puede necesitar `resize()`
4. cuál es su complejidad asintótica relevante.

No ejecutes el programa para responder.

#### Ejercicio 3. El orden de las instrucciones importa

Considera estas cuatro acciones:

```text
A. incrementar n
B. escribir x
C. comprobar capacidad
D. ejecutar resize si es necesario
```

Construye el orden correcto para implementar una inserción al final.

Después propone dos órdenes incorrectos distintos y, para cada uno, construye un estado concreto que muestre el error producido.

No basta con indicar que el código "fallaría". Debes mostrar cómo evoluciona el estado.

#### Ejercicio 4. Elemento válido frente a posición válida

Para una estructura con:

```text
n = 4
a.length = 8
```

clasifica los siguientes índices:

```text
-1
0
3
4
7
8
```

Para cada índice indica si es válido para:

```java
get(i)
set(i, x)
add(i, x)
remove(i)
```

Explica por qué una posición puede ser válida para insertar y no ser válida para consultar un elemento existente.

### B. Retos integradores

#### Reto 1. Historia completa de una estructura

Una estructura comienza con:

```text
a.length = 1
n = 0
```

Se ejecuta la secuencia:

```text
add(8)
add(3)
add(12)
add(1, 5)
set(2, 7)
add(15)
remove(1)
add(0, 4)
remove(3)
indexOf(15)
```

Reconstruye toda la ejecución.

Para cada operación registra:

| Operación | Contenido lógico | `n` | `a.length` | Copias por `resize` | Desplazamientos | Comparaciones |
|---|---|---:|---:|---:|---:|---:|

Después responde:

1. ¿en qué operaciones cambió la capacidad?
2. ¿en cuáles cambió solamente el contenido?
3. ¿cuál realizó más desplazamientos?
4. ¿cuál realizó más copias?
5. ¿cuál realizó más comparaciones?
6. ¿qué operaciones conservaron el tamaño?
7. ¿cuál fue la operación individual con mayor trabajo observable?
8. ¿se preservó el invariante después de cada operación?.

No ejecutes el programa hasta terminar toda la traza manual.

#### Reto 2. Dos programas que compilan, uno está mal

Se proponen dos implementaciones de inserción indexada.

Versión A:

```java
void add(int i, Integer x) {
    checkPositionIndex(i);

    if (n + 1 > a.length) {
        resize();
    }

    for (int j = n; j > i; j--) {
        a[j] = a[j - 1];
    }

    a[i] = x;
    n++;
}
```

Versión B:

```java
void add(int i, Integer x) {
    checkPositionIndex(i);

    if (n + 1 > a.length) {
        resize();
    }

    for (int j = i; j < n; j++) {
        a[j + 1] = a[j];
    }

    a[i] = x;
    n++;
}
```

Ambas versiones pueden compilar.

Sin ejecutarlas:

1. determina cuál es correcta
2. construye el ejemplo más pequeño posible que permita demostrar el error de la otra
3. realiza la traza posición por posición
4. identifica el instante exacto en que se pierde información
5. explica por qué la dirección del recorrido forma parte de la correctitud del algoritmo
6. indica si ambas versiones tienen la misma complejidad asintótica
7. explica por qué tener la misma complejidad no implica ser igualmente correctas.

#### Reto 3. Eliminar correctamente no es escribir `null`

Un estudiante propone:

```java
Integer remove(int i) {
    checkElementIndex(i);

    Integer old = a[i];
    a[i] = null;
    n--;

    return old;
}
```

El código compila.

Analiza la propuesta.

1. construye un estado donde el resultado lógico sea incorrecto
2. identifica qué propiedad de la representación deja de cumplirse
3. explica por qué reducir `n` no es suficiente
4. diseña el desplazamiento necesario
5. explica qué posición debe limpiarse con `null`
6. determina el peor caso de la operación corregida
7. determina un caso donde no sea necesario desplazar ningún elemento.

Finalmente escribe una versión correcta de `remove(int i)`.

#### Reto 4. Comparar dos historias con el mismo resultado final

Dos estudiantes terminan con el mismo contenido:

```text
[10, 20, 30, 40]
```

El estudiante A construyó la secuencia usando solamente:

```java
add(x)
```

El estudiante B debe construir exactamente el mismo resultado usando solamente:

```java
add(int i, Integer x)
```

pero intentando maximizar el número total de desplazamientos.

Diseña ambas secuencias desde una estructura vacía.

Después calcula:

1. número total de inserciones
2. número total de desplazamientos
3. número de llamadas a `resize()`
4. número total de elementos copiados por crecimiento
5. capacidad final
6. complejidad dominante de cada estrategia.

Explica por qué dos ejecuciones con el mismo estado final pueden haber requerido cantidades de trabajo muy diferentes.

#### Reto 5. Contabilidad del crecimiento

La capacidad inicial es 1 y la política de crecimiento duplica la capacidad.

Se realizan 33 inserciones al final.

Sin ejecutar Java, determina:

1. todas las capacidades que aparecen
2. en qué inserciones ocurre cada `resize()`
3. cuántos elementos se copian en cada redimensionamiento
4. el número total de elementos copiados
5. cuántas inserciones no ejecutan `resize()`
6. la capacidad final
7. cuántas posiciones quedan libres al terminar.

Después compara este resultado con una política que aumentara la capacidad en una unidad cada vez.

No necesitas obtener una fórmula general, pero debes explicar cuál de las dos políticas realiza más trabajo de copia y por qué.

Finalmente responde:

> ¿Qué evidencia proporciona este experimento para justificar intuitivamente que agregar al final tiene costo `O(1)` amortizado con crecimiento geométrico?.

#### Reto 6. Diseñar una secuencia adversarial

La estructura tiene inicialmente:

```text
a.length = 32
n = 16
```

Por tanto, hay capacidad suficiente y ninguna de las siguientes cinco inserciones necesita crecer.

Diseña una secuencia de cinco llamadas a:

```java
add(int i, Integer x)
```

que maximice el número total de desplazamientos.

Luego diseña otra secuencia de cinco llamadas que minimice el número total de desplazamientos.

Para cada secuencia:

1. indica los índices elegidos
2. calcula los desplazamientos de cada operación
3. calcula el total
4. identifica el mejor y el peor patrón
5. explica por qué el parámetro `i` modifica el costo real aunque el método sea el mismo

Después responde:

> ¿Por qué decir únicamente "`add(i, x)` es O(n)" no describe completamente el comportamiento de cada llamada concreta?.

#### Reto 7. El invariante mínimo puede no ser suficiente

Se propone como único invariante:

```text
0 <= n <= a.length
```

Considera el estado:

```text
a = [10, 20, 30, 40, _, _, _, _]
n = 4
```

Después considera:

```text
a = [10, _, 30, 40, _, _, _, _]
n = 4
```

y finalmente:

```text
a = [10, null, 30, 40, _, _, _, _]
n = 4
```

Analiza los tres casos.

1. ¿cumplen la desigualdad?
2. ¿representan necesariamente una secuencia válida?
3. ¿qué significa exactamente afirmar que los elementos ocupan `a[0..n-1]`?
4. ¿qué ocurre si `null` está permitido como valor lógico?
5. ¿qué información debe proporcionar la especificación del ADT para decidirlo?
6. propone un conjunto de invariantes suficientemente claro para la implementación estudiada.

El objetivo es distinguir entre una condición necesaria y una descripción completa del estado válido.

#### Reto 8. Diseñar una nueva operación sin romper la estructura

Se desea agregar:

```java
Integer removeLast()
```

No puedes utilizar:

```java
remove(n - 1)
```

Debes implementarla directamente usando la representación interna.

Tu solución debe:

1. validar correctamente el caso de estructura vacía
2. guardar el elemento que será retornado
3. actualizar `n`
4. limpiar la posición que deja de ser lógica
5. decidir si debe comprobar reducción de capacidad
6. preservar el invariante
7. justificar su complejidad.

Después compara:

```java
removeLast()
```

con:

```java
remove(0)
```

Explica por qué ambas eliminan un elemento pero pueden tener costos muy diferentes.

#### Reto 9. Encontrar una secuencia que fuerce crecimiento y reducción

La estructura utiliza estas reglas:

```text
crecer cuando n + 1 > a.length
nueva capacidad = max(1, 2 * n)

reducir después de eliminar cuando
a.length >= 3 * n

nueva capacidad = max(1, 2 * n)
```

Parte de una estructura vacía.

Diseña una secuencia de operaciones que produzca:

1. al menos tres crecimientos,
2. después al menos una reducción,
3. después un nuevo crecimiento.

Registra en cada cambio:

```text
contenido lógico
n
capacidad anterior
capacidad nueva
motivo del resize
cantidad de elementos copiados
```

Después explica por qué no sería conveniente reducir el arreglo después de cada eliminación.

#### Reto 10. Diagnóstico completo de una implementación

Se presenta el siguiente código:

```java
Integer remove(int i) {
    checkElementIndex(i);

    Integer old = a[i];

    for (int j = i; j < n; j++) {
        a[j] = a[j + 1];
    }

    if (a.length >= 3 * n) {
        resize();
    }

    n--;

    return old;
}
```

No lo ejecutes inicialmente.

Realiza una revisión sistemática:

1. identifica todos los posibles problemas
2. determina si puede acceder a una posición incorrecta
3. analiza si el orden de `resize()` y `n--` es correcto
4. verifica qué valor de `n` utiliza `resize()`
5. determina si queda una referencia fuera del rango lógico
6. construye un contraejemplo mínimo para cada error encontrado
7. escribe una versión corregida
8. justifica la complejidad de la versión corregida.

Este ejercicio debe resolverse como una revisión de código, no como prueba y error.

#### Reto 11. Mismo orden asintótico, distinto trabajo

Considera una estructura con:

```text
n = 100
a.length = 128
```

Compara conceptualmente:

```java
add(0, x)
add(99, x)
remove(0)
remove(99)
indexOf(a[0])
indexOf(valorQueNoExiste)
```

Todas las operaciones lineales pueden describirse usando `O(n)` en el peor caso.

Sin embargo, el trabajo concreto no es igual.

Para cada llamada estima:

1. número de desplazamientos o comparaciones,
2. trabajo dominante,
3. si el caso concreto está cerca del mejor o del peor comportamiento,
4. por qué la notación asintótica no sustituye una traza concreta.

#### Reto 12. Diseñar una batería mínima de pruebas

Supón que recibes una implementación desconocida de `ArrayStack`.

Solo puedes observar su comportamiento mediante:

```java
size()
capacity()
get(i)
set(i, x)
add(x)
add(i, x)
remove(i)
indexOf(x)
```

Diseña una batería pequeña de operaciones que permita detectar errores en:

1. crecimiento de capacidad
2. preservación de elementos durante `resize()`
3. inserción al inicio
4. inserción en el medio
5. inserción al final
6. eliminación al inicio
7. eliminación en el medio
8. eliminación al final
9. reducción de capacidad
10. búsqueda de un elemento existente
11. búsqueda de un elemento inexistente
12. validación de índices.

Para cada prueba indica:

```text
estado inicial
operación
resultado esperado
estado final esperado
propiedad que se está verificando
```

El objetivo es utilizar pocas operaciones, pero obtener la mayor cantidad posible de evidencia sobre la correctitud de la estructura.

### C. Problema integrador final

#### Reto 13. Auditoría completa de una ejecución

Una estructura comienza vacía con capacidad 1.

Se ejecuta:

```text
add(7)
add(2)
add(9)
add(1, 5)
add(4, 11)
remove(2)
set(1, 8)
add(0, 3)
indexOf(11)
remove(0)
remove(0)
add(6)
```

Realiza una auditoría completa de la ejecución.

Para cada operación registra:

| Paso | Operación | Contenido lógico | `n` | Capacidad | Copias | Desplazamientos | Comparaciones |
|---:|---|---|---:|---:|---:|---:|---:|

Después responde:

1. ¿cuántas veces se ejecutó `resize()`?
2. ¿cuántos elementos se copiaron en total?
3. ¿cuántos desplazamientos se realizaron en total?
4. ¿cuántas comparaciones realizó `indexOf`?
5. ¿cuál fue la capacidad máxima?
6. ¿en qué pasos se modificó solamente un valor y no el tamaño?
7. ¿en qué pasos se modificó el tamaño pero no la capacidad?
8. ¿en qué pasos se modificaron tamaño y capacidad?
9. ¿se mantuvo siempre `0 <= n <= a.length`?
10. ¿se mantuvieron los elementos válidos en `a[0..n-1]`?
11. ¿qué operación concreta tuvo el mayor costo?
12. ¿qué operación tuvo costo constante?
13. ¿qué parte de la ejecución ilustra mejor el análisis amortizado?
14. ¿qué parte ilustra mejor el costo de preservar la contigüidad?.

Finalmente escribe un párrafo que explique qué enseña esta ejecución sobre la relación entre:

```text
representación
correctitud
invariantes
operaciones
complejidad
```

### Criterio de uso

Estos ejercicios no constituyen una práctica calificada y no requieren entrega obligatoria.

La intención es que el estudiante pueda elegir problemas que realmente exijan razonamiento después de haber observado la implementación en clase.

No es necesario completar toda la lista.

Los problemas más importantes son aquellos que obligan a responder, sin depender de ejecutar inmediatamente el programa:

```text
qué representa el estado
qué cambia una operación
qué puede salir mal
qué invariante debe mantenerse
cuánto trabajo realiza
por qué tiene esa complejidad.
```
