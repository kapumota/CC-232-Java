### Lectura: factor de carga, crecimiento y rehashing en tablas hash con encadenamiento

Esta lectura consolida y amplía las ideas trabajadas en la Semana 9 de CC232. Durante las ocho semanas anteriores hemos cambiado de representación, de invariante o de ADT cuando cambió la operación que queríamos favorecer. En la Semana 1 utilizamos un arreglo dinámico y distinguimos tamaño lógico, capacidad física y crecimiento. En la Semana 2 utilizamos listas enlazadas y vimos que una modificación local puede ser barata cuando conocemos las referencias apropiadas. En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`, donde el ADT restringe qué posiciones pueden utilizarse.

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda, donde el invariante de orden permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones. En la Semana 6, AVL añadió altura y balance para impedir que la forma destruyera la eficiencia. En la Semana 7, el heap binario resolvió cómo mantener disponible el mínimo. En la Semana 8 estudiamos hashing con encadenamiento separado, que respondía a una pregunta distinta, cómo localizar una clave exacta sin mantener orden global y sin recorrer toda la colección.

La Semana 9 no introduce una estructura nueva. Continúa con la misma tabla hash y formula otra pregunta: ¿qué ocurre si `n` crece pero la cantidad de buckets permanece fija? La respuesta conduce al **factor de carga**, a una **política de crecimiento**, al cambio de capacidad y al **rehashing**. La idea central de la semana es la siguiente.

```text
n creciente + m fijo = mayor carga promedio

mayor carga + trabajo local dentro del bucket = más trabajo potencial por operación

política de crecimiento + nueva capacidad + rehashing = restaurar una representación adecuada
```

El objetivo no es memorizar `loadFactor`, `shouldGrow`, `capacityFor` o `resize`. El objetivo es continuar la misma cadena conceptual usada desde la primera semana, qué **ADT** queremos ofrecer, qué **representación** almacena el estado, qué **invariantes** deben permanecer verdaderos, qué **algoritmo** modifica o reconstruye la representación, y cuánto trabajo exige, su **complejidad**.

Esta lectura explica los mecanismos, las trazas y las especificaciones necesarias para resolver la Actividad 9. Los métodos pendientes deben implementarse primero a partir de sus contratos y de los invariantes, antes de consultar cualquier solucionario.

### 1. El problema heredado de la Semana 8

En la Semana 8 vimos que una tabla hash con encadenamiento separado reduce una búsqueda global a una búsqueda local. Si una clave `x` pertenece a `t[hash(x)]`, entonces `find(x)` no necesita recorrer toda la tabla, solo el bucket correspondiente. Si ese bucket contiene `k` elementos, el trabajo es calcular el índice, acceder al bucket y recorrerlo, es decir, `O(1 + k)`. El término constante importa cuando `k = 0`, porque consultar un bucket vacío también realiza trabajo. Lo mismo ocurre con `add(x)` y `remove(x)`, que primero localizan un bucket y después trabajan dentro de él.

La ventaja de hashing depende entonces de que `k` no crezca demasiado.

### 2. Una tabla correcta puede volverse menos eficiente

Los ejemplos de esta lectura usan la misma función hash de la Semana 8, hashing multiplicativo con `W = 32` y `Z = 0x9E3779B9`, calculada con el `d` vigente. Considera una tabla con `d = 2`, es decir, cuatro buckets, y las claves 1, 2, 3 y 4.

```text
0: [2]
1: [4]
2: [1]
3: [3]

n = 4
m = 4
```

Si seguimos insertando las claves 5 a 12 sin aumentar `m`, obtenemos:

```text
0: [2, 5, 10]
1: [4, 7, 12]
2: [1, 6, 9]
3: [3, 8, 11]

n = 12
m = 4
```

La tabla sigue siendo correcta, y además el reparto es perfectamente uniforme, tres claves por bucket. Aun así, las búsquedas locales recorren colecciones tres veces más largas que antes. Por tanto, **correctitud no implica eficiencia constante**, y una buena distribución tampoco basta si la cantidad de buckets permanece fija.

### 3. Estado de la representación, n, m, t y d

Utilizaremos cuatro cantidades. `n` es el número de elementos almacenados. `m` es el número de buckets, y se cumple `m = t.length`. `t` es el arreglo de buckets. `d` es el exponente tal que `t.length = 2^d`. Mantener una potencia de dos tiene una consecuencia práctica, incrementar `d` en uno duplica la capacidad, porque `2^(d + 1) = 2 * 2^d`.

| `d` | Buckets |
|---|---|
| 1 | 2 |
| 2 | 4 |
| 3 | 8 |
| 4 | 16 |
| 5 | 32 |

`n` describe contenido lógico y `t.length` describe capacidad física. Dos tablas pueden contener exactamente las mismas claves y utilizar cantidades diferentes de buckets. La misma idea apareció en la Semana 1, donde distinguíamos `n` de `a.length`, y ahora distinguimos `n` de `t.length`. Pero crecer una tabla hash será más delicado que crecer un arreglo, como veremos en la sección 7.

### 4. Factor de carga

Definimos el **factor de carga** como `alpha = n/m`. Si `n = 6` y `m = 8`, entonces `alpha = 0.75`. Eso no significa que cada bucket contenga 0.75 elementos, significa que el promedio global es 0.75 elementos por bucket. La función `loadFactor(size, capacity)` debe calcular ese cociente sin división entera, y rechazar una capacidad no positiva.

El factor de carga es exactamente la longitud promedio de un bucket. Si las longitudes de los `m` buckets son `k0, k1, ..., k(m-1)`, entonces `k0 + k1 + ... + k(m-1) = n`, y por tanto el promedio es `(k0 + ... + k(m-1))/m = n/m = alpha`. Esta relación es natural en encadenamiento separado. En la tabla de la sección 2, con `n = 12` y `m = 4`, el promedio es 3.

Como un bucket puede contener varias claves, es posible `n > m` y por tanto `alpha > 1`. Con `n = 10` y `m = 8`, `alpha = 1.25`. La tabla todavía puede ser correcta, pero no satisface la política de carga que usaremos en la sección 5.

#### Factor de carga no es distribución

Dos tablas pueden tener `n = 8`, `m = 8` y `alpha = 1`, y aun así comportarse de manera muy distinta. Con las claves 1 a 8 y `d = 3`, la función hash las reparte así:

```text
Tabla A
0: [5]
1: [2]
2: [7]
3: [4]
4: [1]
5: [6]
6: [3]
7: [8]
```

Con otras ocho claves, elegidas para que la función las envíe al mismo bucket, obtenemos:

```text
Tabla B
0: [5, 13, 18, 26, 34, 39, 47, 60]
1: [ ]
2: [ ]
3: [ ]
4: [ ]
5: [ ]
6: [ ]
7: [ ]
```

El factor de carga es igual, pero la distribución es muy diferente. La **función hash** responde cómo se reparten las claves entre los buckets, y el **factor de carga** responde cuántos elementos existen respecto de cuántos buckets. Una tabla eficiente necesita ambas condiciones razonables. Las colisiones, por su parte, siguen siendo normales. Dos claves distintas pueden compartir bucket y el encadenamiento las conserva juntas. La meta no es eliminar todas las colisiones, es evitar concentraciones que hagan crecer demasiado las colecciones locales.

### 5. Política de crecimiento

Debemos separar dos cosas. La **correctitud** exige que cada clave esté en el bucket que le corresponde, que `n` coincida con el contenido y que no haya duplicados. La **política de rendimiento** exige que `n/m` se mantenga dentro de un rango deseado. Superar un umbral no destruye el ADT, indica que la implementación decide reconstruir para conservar la eficiencia esperada.

La política de esta semana es la siguiente. Antes de insertar, `shouldGrow(size, capacity)` pregunta qué factor de carga tendría la tabla después de insertar un elemento más sin cambiar la capacidad, es decir, `(size + 1)/capacity`, y responde que sí debe crecer si ese valor supera 1.0. La clave está en el `size + 1`. El método no pregunta solamente cómo está la tabla ahora, mira el estado futuro. Por ejemplo, con `size = 8` y `capacity = 8` el factor actual es 1, pero una inserción adicional produciría `9/8 = 1.125`, así que la decisión se toma antes de modificar el contenido.

La condición es estrictamente mayor que 1.0, no mayor o igual. Una inserción que deja `alpha = 1` todavía está permitida, y la tabla crece cuando la siguiente inserción produciría `alpha > 1`. Cambiar `>` por `>=` cambia la política.

El umbral 1.0 es una decisión didáctica apropiada para esta tabla con encadenamiento, no una ley universal. Las implementaciones reales eligen otros valores, por ejemplo `java.util.HashMap` usa por defecto un factor de carga de 0.75, algo que retomaremos en la Semana 10. La idea general es que un umbral más el estado futuro producen una decisión de crecimiento.

### 6. Elegir la nueva capacidad

`capacityFor(size)` debe retornar una potencia de dos estrictamente mayor que `size`, con capacidad mínima 2. Por ejemplo, `capacityFor(0) = 2`, `capacityFor(3) = 4`, `capacityFor(7) = 8` y `capacityFor(8) = 16`. La palabra "estrictamente" importa. Para `size = 8`, el valor 8 es una potencia de dos, pero no cumple `capacity > size`, así que la respuesta correcta es 16.

Podemos construir el resultado partiendo de 2 y duplicando mientras la capacidad no supere a `size`. Durante todo el ciclo la capacidad sigue siendo potencia de dos.

Esta función ya la conocemos con otro nombre. En la Semana 8, `dimensionFor(n)` devolvía el menor `d >= 1` tal que `2^d > n`, y por tanto `capacityFor(n) = 2^dimensionFor(n)`, siempre que la potencia pueda representarse como una capacidad positiva de tipo `int`. La equivalencia se deduce directamente de las dos especificaciones: una devuelve el exponente mínimo y la otra la potencia correspondiente.

Hay dos consecuencias. La primera es que, partiendo de una tabla válida y aplicando la política de la sección 5, el crecimiento se dispara cuando `n = m`, ya que después de cada inserción pública se mantiene `n <= m`. La nueva capacidad es entonces `2m`: el crecimiento es geométrico. La segunda es un cuidado de implementación. La mayor potencia de dos positiva representable en un `int` es `2^30`. Si se duplica otra vez, se produce desbordamiento y el resultado deja de ser una capacidad válida. `capacityFor` y `resize` deben validar el límite, en lugar de permitir que un ciclo de duplicación desborde.

### 7. La función hash depende de d

En un arreglo dinámico, un elemento almacenado en `a[3]` puede copiarse a `b[3]` y la estructura sigue siendo correcta. En una tabla hash, una clave pertenece a un bucket porque la función hash produce ese índice, y esa función depende de `d`. Escribiremos `hash_d(x)` para los `d` bits más altos de `Z * x.hashCode()`, módulo `2^32`, como en la Semana 8. Si cambia `d`, el índice puede cambiar.

Por eso conviene distinguir `hashCode` de índice. Una clave conserva exactamente el mismo `x.hashCode()` antes y después del crecimiento, pero la transformación posterior usa un `d` distinto. Dicho de otro modo, **el mismo hashCode no implica el mismo bucket después de un resize**.

#### Cada bucket se parte en dos

Esta función tiene una propiedad que hace el crecimiento más fácil de visualizar. Al pasar de `d` a `d + 1` se conserva un bit más del resultado, así que los `d` bits anteriores no cambian y se agrega uno nuevo. Por tanto `hash_(d+1)(x) = 2 * hash_d(x) + b`, donde `b` es 0 o 1. Todas las claves de un bucket `i` terminan en el bucket `2i` o en el bucket `2i + 1`, y en ningún otro.

```text
d = 3                      d = 4

bucket i        --->       bucket 2i
                           bucket 2i + 1
```

Con `d = 3`, la clave 7 pertenece al bucket 2, y con `d = 4` pertenece al bucket 5, que es `2 * 2 + 1`. La clave 15 también pertenece al bucket 2 con `d = 3`, y con `d = 4` pertenece al bucket 4, que es `2 * 2`. Una clave que estaba en el bucket 2 solo puede terminar en el 4 o en el 5.

#### Por qué copiar los buckets no funciona

Supongamos que pasamos de `d = 3` a `d = 4` y copiamos el bucket antiguo `t[i]` al bucket nuevo `t[i]`. Todas las claves se conservan físicamente, pero la clave 7 queda en `t[2]` mientras que `hash_4(7) = 5`. Entonces `find(7)` consulta `t[5]` y no la encuentra. La clave existe, pero la estructura es incorrecta, porque rompe el invariante de ubicación. Esta observación muestra otra vez el poder de los invariantes.

### 8. Rehashing

Llamamos **rehashing** a reconstruir la tabla bajo una nueva configuración. El conjunto lógico no cambia, la representación física sí.

```text
recordar la tabla antigua
        |
        v
elegir el nuevo d
        |
        v
crear los nuevos buckets, vacíos
        |
        v
recorrer los elementos antiguos
        |
        v
recalcular hash con el d nuevo
        |
        v
reinsertar en el bucket nuevo
```

Antes de reemplazar `t` necesitamos conservar la tabla antigua, para construir la nueva y todavía poder recorrer los elementos existentes. Durante la reconstrucción hay dos tablas, `oldTable`, la representación anterior, y `t`, la nueva. El nuevo `d` es el menor tal que `2^d > n`, que es exactamente `capacityFor(n)` expresado como exponente. Crear las nuevas colecciones no modifica `n`, y la reconstrucción del contenido ocurre después. Así se separan dos responsabilidades, crear almacenamiento y restaurar elementos.

#### n no cambia durante resize

Si antes de `resize` hay `n = 8`, después debe seguir habiendo `n = 8`. Los ocho elementos ya existían, solo cambiaron de ubicación. Por tanto, **`resize` cambia la representación pero no el contenido lógico**. Esto obliga a distinguir dos operaciones.

```text
add(x) público
    incorpora una clave nueva
    n puede aumentar

reinserción durante resize
    mueve una clave existente
    n no aumenta
```

Por eso reinsertar llamando al `add` público de la tabla es incorrecto. Cada llamada exitosa ejecutaría `n++`, y el contador dejaría de coincidir con el número real de elementos. Además, la operación pública podría volver a activar la política de crecimiento en medio de la reconstrucción. Dentro de `resize`, la reinserción debe agregar el elemento directamente a la colección del bucket nuevo, con el `add` de la lista, que no toca `n`. Es fácil confundir ambos, porque se llaman igual.

### 9. Métodos del repositorio y comprobaciones

El trabajo práctico utiliza dos archivos independientes. **No todos los métodos que aparecen en los ejemplos están pendientes**: es necesario distinguir el código ya proporcionado del que debe implementar el estudiante.

| Archivo | Métodos ya implementados | Métodos pendientes |
|---|---|---|
| `Semana09_Rehashing0.java` | `loadFactor(int size, int capacity)` y `shouldGrow(int size, int capacity)` | `capacityFor(int size)` |
| `Semana09_Rehashing1.java` | Constructor, `allocateTable`, `hash`, `size`, `find` y `add` | `remove(T x)` y `resize()` |

En el primer archivo ya se encuentra la política de crecimiento. El estudiante debe explicar por qué se utiliza `size + 1`, interpretar el umbral estricto y completar solamente `capacityFor`:

```java
// Semana09_Rehashing0.java: método pendiente.
static int capacityFor(int size) {
    // TODO: devolver la menor potencia de dos estrictamente
    // mayor que size, con capacidad mínima 2.
    // Validar el rango para impedir desbordamientos.
    throw new UnsupportedOperationException("TODO");
}
```

En el segundo archivo, `find` y `add` ya están escritos. Los métodos pendientes corresponden a una eliminación localizada y a una reconstrucción completa. La clase del repositorio ya contiene los campos, el constructor y `allocateTable`:

```java
// Semana09_Rehashing1.java: fragmento dentro de ChainedHashTable<T>.
T remove(T x) {
    // TODO: recorrer solo t[hash(x)] con Iterator<T>,
    // eliminar la coincidencia, decrementar n y retornar
    // el objeto almacenado, o null si no existe.
    throw new UnsupportedOperationException("TODO");
}

private void resize() {
    // TODO: elegir un nuevo d con 2^d > n, crear la tabla,
    // redistribuir todas las claves y preservar n.
    // Rechazar una capacidad que no quepa en un int positivo.
    throw new UnsupportedOperationException("TODO");
}
```

Puedes comprobar las decisiones de capacidad y crecimiento con los siguientes casos:

| Llamada | Resultado esperado |
|---|---|
| `loadFactor(6, 8)` | `0.75` |
| `loadFactor(8, 8)` | `1.0` |
| `loadFactor(10, 8)` | `1.25` |
| `loadFactor(3, 0)` | `IllegalArgumentException` |
| `shouldGrow(7, 8)` | `false` |
| `shouldGrow(8, 8)` | `true` |
| `shouldGrow(1, 2)` | `false` |
| `shouldGrow(2, 2)` | `true` |
| `capacityFor(0)` | `2` |
| `capacityFor(1)` | `2` |
| `capacityFor(2)` | `4` |
| `capacityFor(3)` | `4` |
| `capacityFor(7)` | `8` |
| `capacityFor(8)` | `16` |
| `capacityFor(2^30 - 1)` | `2^30` |
| `capacityFor(2^30)` | `IllegalArgumentException` |

Los dos últimos casos comprueban una decisión de robustez añadida a la referencia docente: el archivo inicial del repositorio no define explícitamente el comportamiento para tamaños extremos. Para `resize`, la comprobación central es preservar el conjunto y los cuatro invariantes al reproducir la siguiente traza.

### 10. Traza completa de resize

Para observar varios crecimientos con pocas claves, **la traza usa un constructor didáctico con `d = 1` y dos buckets iniciales**. En cambio, `Semana09_Rehashing1.java` comienza con `d = 3` y ocho buckets para que su programa principal pueda ejecutarse sin llamar inmediatamente a `resize`. Ambas inicializaciones son válidas, pero producen historias de crecimiento distintas. En esta traza partimos de `d = 1` y ejecutamos `add` con las claves 18, 23, 10, 9, 22, 6, 24 y 16, en ese orden. Durante esas inserciones la tabla crece dos veces, y después de la octava inserción queda así.

```text
d = 3, m = 8, n = 8, alpha = 1

0: [18]
1: [23, 10]
2: [ ]
3: [ ]
4: [9, 22]
5: [6]
6: [24]
7: [16]
```

Ahora ejecutamos `add(31)`. La clave no existe, y `shouldGrow(8, 8)` es verdadero porque `(8 + 1)/8 = 1.125 > 1`. La tabla debe crecer antes de insertar. El nuevo `d` es el menor tal que `2^d > 8`, es decir, `d = 4`, y la capacidad pasa de 8 a 16.

`resize` recorre los buckets antiguos en orden y reinserta cada elemento con `hash_4`. Cada bucket `i` alimenta al `2i` o al `2i + 1`.

```text
bucket antiguo    elementos      bucket nuevo

0                 [18]           1
1                 [23, 10]       3 y 2
4                 [9, 22]        8 y 9
5                 [6]            11
6                 [24]           13
7                 [16]           14
```

Observa que los buckets antiguos 1 y 4, que tenían dos colisiones, se separaron en dos buckets nuevos. Terminada la reinserción, y todavía sin la clave 31, la tabla es la siguiente.

```text
d = 4, m = 16, n = 8, alpha = 0.5

0: [ ]
1: [18]
2: [10]
3: [23]
4: [ ]
5: [ ]
6: [ ]
7: [ ]
8: [9]
9: [22]
10: [ ]
11: [6]
12: [ ]
13: [24]
14: [16]
15: [ ]
```

El valor de `n` sigue siendo 8, y `t.length = 16 = 2^4`. Solo ahora, con el `d` vigente, se calcula `hash_4(31) = 2` y se inserta la clave nueva en el bucket 2, junto a la clave 10. Luego se ejecuta `n++`.

```text
d = 4, m = 16, n = 9, alpha = 0.5625

0: [ ]
1: [18]
2: [10, 31]
3: [23]
4: [ ]
5: [ ]
6: [ ]
7: [ ]
8: [9]
9: [22]
10: [ ]
11: [6]
12: [ ]
13: [24]
14: [16]
15: [ ]
```

#### Cómo validar el resultado

Después del `resize` comprobamos las preguntas de siempre. ¿Es `t.length = 2^d`? Sí, 16 es 2^4. ¿Está cada `x` en `t[hash_4(x)]`? Sí, por construcción. ¿Coincide `n` con la suma de los tamaños de los buckets? Sí, ocho buckets con un elemento. ¿Se conservaron todas las claves y no apareció ningún duplicado? Sí. Estas comprobaciones expresan invariantes, no detalles accidentales de la implementación.

### 11. Invariantes y postcondición de resize

Después de cada operación válida debe cumplirse lo siguiente.

1. `t.length = 2^d`.
2. Para todo `x` almacenado, `x` pertenece a `t[hash_d(x)]`, usando el `d` actual.
3. `n` es la suma de los tamaños de todos los buckets.
4. No existen claves duplicadas.

Además, la política de inserción busca conservar `n <= t.length` después de cada inserción pública. La postcondición de `resize` es más fuerte. Sea `S` el conjunto lógico antes de `resize`. Después debe cumplirse que el conjunto es el mismo, que `n` es el mismo, que `t.length = 2^d`, y que todo `x` de `S` pertenece a `t[hash_d(x)]` con el `d` nuevo. Esta especificación es más importante que memorizar líneas de Java.

### 12. find, add y remove

`find` no revisa todos los buckets. Calcula el bucket con el `d` vigente, recorre su colección comparando con `equals`, y retorna el elemento encontrado o `null`. Confía en el invariante de ubicación, si `x` existe entonces está en `t[hash_d(x)]`, y ese invariante es lo que permite la eficiencia.

El orden de los pasos de `add` es parte de su correctitud.

```text
add(x)
    1. comprobar duplicado       find(x) != null  ->  retornar false
    2. comprobar crecimiento     shouldGrow(n, t.length)
    3. resize si corresponde
    4. calcular hash con el d vigente
    5. insertar en ese bucket
    6. n++
```

La clave nueva se inserta después del posible `resize`, porque `resize` puede cambiar `d` y por tanto puede cambiar `hash_d(x)`. La nueva clave debe usar la configuración vigente. `remove` localiza el bucket, recorre su colección con un `Iterator`, y si encuentra la clave la elimina con `it.remove()`, ejecuta `n--` y retorna el elemento. Si la clave no existe, `n` no cambia.

La versión de esta semana no reduce la capacidad al eliminar. `remove` puede disminuir `n`, pero `t.length` permanece igual, así que `alpha` puede bajar mucho con el tiempo. La implementación no admite claves `null`: utiliza `hashCode()` sobre la clave y `null` representa ausencia en `find` y `remove`. Asimismo, las claves deben respetar el contrato `equals`/`hashCode` y no cambiar los campos que determinan su hash mientras estén almacenadas. La Semana 9 se concentra en crecimiento y rehashing, y una política de contracción puede estudiarse después.

### 13. Costo local y costo esperado

Sea `k` la longitud del bucket seleccionado. Ignorando por ahora el costo eventual de `resize`, `find`, `add` y `remove` cuestan `O(1 + k)`. Si todas las claves caen en el mismo bucket, como en la Tabla B de la sección 4, entonces `k = n` y el costo es `O(n)`. Por eso no debemos afirmar sin condiciones que hashing es `O(1)`.

Para hablar de costo esperado necesitamos una hipótesis explícita. Supongamos **hashing uniforme simple**, es decir, que cada clave tiene la misma probabilidad de caer en cualquiera de los `m` buckets, independientemente de las demás. Es un modelo, no una propiedad que la función concreta garantice. Bajo esa hipótesis, la longitud esperada de un bucket es `n/m = alpha`, y por tanto

```text
E[T] = O(1 + alpha)
```

Aquí está el motivo de toda la semana. La sección 4 mostró que `alpha` es la longitud promedio de bucket, y esta fórmula dice que el costo esperado depende de `alpha` y no solo de la función hash. Con `m` fijo y `n` creciente, `alpha` crece y el costo esperado crece aunque la distribución sea perfecta, como en la tabla de la sección 2. La política de crecimiento mantiene `alpha <= 1` después de cada inserción pública, y entonces `E[T] = O(1 + 1) = O(1)`. Esa conclusión depende de la hipótesis de uniformidad y de la política.

| Operación | Costo local | Esperado (hashing uniforme simple) | Peor caso |
|---|---|---|---|
| `find`, `add`, `remove` | O(1 + k) | O(1 + alpha), y con `alpha <= 1` es O(1) | O(n) |
| `resize` | O(n + m) | no aplica | O(n) |

### 14. Costo de resize y análisis amortizado

Durante `resize` visitamos los `m` buckets antiguos, recorremos los `n` elementos y creamos los buckets nuevos. El trabajo es `O(n + m)`. Como la política dispara el crecimiento solo cuando `n = m`, esto es `O(n)`. Una inserción que provoca crecimiento puede ser mucho más cara que una inserción ordinaria.

Aun así, las reconstrucciones no ocurren después de cada inserción. Con una tabla inicial de `d = 1`, el crecimiento se dispara cuando `n` vale 2, 4, 8, 16, y así sucesivamente, y cada reconstrucción cuesta del orden de `n`. Para `N` inserciones, el costo total de todas las reconstrucciones es del orden de `2 + 4 + 8 + ... + N`, una suma geométrica menor que `2N`. Repartido entre las `N` inserciones, queda un costo adicional de crecimiento de `O(1)` **amortizado** por inserción. Esto no significa que cada llamada individual cueste `O(1)`.

Las dos ideas de costo son distintas y una misma tabla puede necesitar ambas.

| Idea | De qué depende |
|---|---|
| esperado | de la distribución de las claves entre los buckets |
| amortizado | de repartir reconstrucciones ocasionales entre muchas operaciones |

En la Semana 1, un `resize` de arreglo costaba `O(n)` y muchas inserciones daban `O(1)` amortizado. El patrón de crecimiento geométrico es similar, pero la reconstrucción de la representación es diferente.

| Estructura | Qué hace al crecer |
|---|---|
| arreglo dinámico | crea almacenamiento mayor y copia posiciones |
| tabla hash | crea almacenamiento mayor y recalcula ubicaciones |

Rehashing no es solamente copiar memoria.

### 15. Errores frecuentes en resize

- **Usar el `d` antiguo.** Si los índices se calculan antes de actualizar `d`, las ubicaciones corresponden a la tabla vieja.
- **Incrementar `n`.** Reinsertar elementos existentes no debe modificar el tamaño lógico.
- **Llamar al `add` público.** Incrementa `n` y puede reactivar el crecimiento dentro de la reconstrucción.
- **Omitir un bucket.** Se perderían elementos durante la reconstrucción.
- **Copiar buckets completos.** Las claves quedan en posiciones incompatibles con el nuevo `hash_d(x)`.
- **Cambiar `>` por `>=` en la política.** Cambia el momento en que la tabla crece.
- **Duplicar la capacidad con `int` sin cuidado.** Al intentar superar `2^30`, la capacidad deja de ser positiva. Debe validarse el límite tanto al calcular la capacidad como dentro de `resize`.

### 16. Tabla hash frente a AVL y heap

Un AVL mantiene orden y ofrece `O(log n)` en peor caso para `find`, `add` y `remove`. Un heap binario favorece consultar o retirar el mínimo, y buscar una clave arbitraria puede requerir `O(n)`. La tabla hash con encadenamiento busca `O(1)` esperado para localizar una clave exacta, pero no mantiene orden global.

| Estructura | Búsqueda de clave exacta | Mantiene orden | Qué favorece |
|---|---|---|---|
| AVL | O(log n), peor caso | sí | búsqueda ordenada con altura garantizada |
| BinaryHeap | O(n) | no, solo prioridad | el mínimo |
| Tabla hash con encadenamiento | O(1) esperado con `alpha <= 1`, O(n) peor caso | no | localizar una clave exacta |

La elección depende de las operaciones que necesitamos.

### 17. Distinciones que deben quedar claras

| Distinción | Significado |
|---|---|
| `n` y `t.length` | contenido lógico frente a capacidad física |
| `hashCode` e índice de bucket | el primero es un entero asociado a la clave, el segundo depende además de `d` |
| factor de carga y distribución | `alpha` mide ocupación global, la función hash determina el reparto |
| correctitud y política de rendimiento | los invariantes frente al umbral de crecimiento |
| inserción lógica y reinserción física | `add` puede aumentar `n`, la reinserción en `resize` no |
| copiar y rehashing | copiar posiciones frente a recalcular ubicaciones |
| esperado y amortizado | distribución de claves frente a reparto de reconstrucciones |

### 18. Síntesis

La Semana 8 mostró cómo una función hash permite reducir una búsqueda global a una búsqueda dentro de un bucket. La Semana 9 estudia qué hacer cuando esa representación comienza a cargarse. El problema no es solamente disponer de más memoria, porque la ubicación de cada clave depende de la configuración de la tabla, y por eso el crecimiento exige reconstrucción.

```text
clave
    |
    v
hashCode
    |
    v
hash con d actual
    |
    v
bucket

n/m
    |
    v
factor de carga
    |
    v
política de crecimiento

crecimiento
    |
    v
nuevo d
    |
    v
nueva capacidad
    |
    v
rehashing

rehashing correcto
    =
mismo conjunto lógico
+ n preservado
+ invariantes restaurados
```

La cadena de diseño del curso se mantiene. El **ADT** ofrece `find`, `add`, `remove` y `size`. La **representación** es `t`, `d`, `n` y una colección local por bucket. Los **invariantes** son `t.length = 2^d`, la ubicación correcta de cada clave, `n` igual al contenido total y la ausencia de duplicados. Los **algoritmos** son `find`, `add`, `remove` y `resize`. La **complejidad** es `O(1 + k)` por operación local, `O(1)` esperado bajo hashing uniforme con `alpha <= 1`, `O(n)` en el peor caso, `O(n)` por `resize`, y `O(1)` amortizado para el crecimiento.

La idea final es que una estructura de datos eficiente no solo necesita una representación correcta, también necesita una política para mantener esa representación dentro de condiciones razonables a medida que el estado crece. Esa idea conecta directamente con la Semana 1, y prepara la Semana 10, donde utilizaremos las abstracciones `Map` y `Set` apoyándonos en las propiedades de las tablas hash que ahora ya podemos razonar desde su representación interna.
