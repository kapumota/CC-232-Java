### Ejercicios de la Semana 8

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La parte de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 8.

El objetivo no es repetir la Actividad 8 ni volver a ejecutar los mismos ejemplos utilizados para introducir hashing, colisiones, encadenamiento separado, `contains`, `add` y `remove`.

La mayoría de los problemas exige:

```text
razonar antes de ejecutar
reconstruir estados
detectar código que compila pero es incorrecto
formular y verificar invariantes
diseñar contraejemplos
separar ADT de implementación
separar estado lógico de representación física
distinguir localización de modificación
comparar estructuras por patrón de operaciones
combinar estructuras de semanas anteriores
analizar mejor caso, peor caso y costo esperado
diseñar pruebas que revelen errores estructurales
```

Los temas centrales de la Semana 8 utilizados son:

```text
problema de localizar una clave exacta
búsqueda secuencial
direccionamiento directo
universo de claves
clave
hashCode
función hash
índice de bucket
rango 0..m-1
hash determinista
colisiones
encadenamiento separado
arreglo de buckets
colección por bucket
buckets
size
invariante de ubicación
invariante de tamaño
contains
add
remove
duplicados
clave ausente
longitud k del bucket
O(k)
peor caso O(n)
costo esperado cercano a O(1)
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
arreglos dinámicos
tamaño y capacidad
resize
costo amortizado
listas enlazadas
nodos y referencias
localización frente a modificación
Stack
Queue
Deque
BST
findLast
contains
add
remove
inorder
altura h
AVL
height
balanceFactor
rotaciones
BinaryHeap
min-heap
bubbleUp
trickleDown
heapify
trazas
invariantes
complejidad
```

No se requiere utilizar `HashMap`, `HashSet`, `Hashtable`, `TreeMap`, `TreeSet` ni `PriorityQueue` como solución directa.

Cuando un ejercicio trabaje con la representación de la Semana 8, se puede utilizar el mismo enfoque de los archivos de clase con `List<T>[]`, `ArrayList` e `Iterator`.

No se requiere estudiar todavía `open addressing`, `linear probing`, `quadratic probing`, `double hashing`, tombstones, Robin Hood hashing, cuckoo hashing ni perfect hashing.

Los ejercicios de la sección C son ampliaciones opcionales.

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir una tabla a partir de su representación física

Se utiliza:

```text
hash(x) = Math.floorMod(x, 11)
```

y se observa:

```text
bucket 0:  [22]
bucket 1:  [45, 89]
bucket 2:  [13]
bucket 3:  [ ]
bucket 4:  [37, 70]
bucket 5:  [ ]
bucket 6:  [17]
bucket 7:  [ ]
bucket 8:  [30]
bucket 9:  [ ]
bucket 10: [21]

size = 9
```

Sin ejecutar código:

1. verifica el bucket de cada clave,
2. determina si el invariante de ubicación se cumple,
3. cuenta el número total de elementos y verifica `size`,
4. identifica todas las colisiones visibles,
5. determina el bucket que debe examinar `contains(81)`,
6. determina el bucket que debe examinar `contains(70)`,
7. explica por qué encontrar el mínimo no es una operación favorecida por esta representación,
8. explica por qué recorrer los buckets desde 0 hasta 10 no produce necesariamente las claves ordenadas,
9. distingue estado lógico de representación,
10. construye un estado con las mismas claves y el mismo `size` que viole solamente el invariante de ubicación.

#### Ejercicio 2. Elegir estructura a partir del patrón de operaciones

Se deben implementar seis componentes.

Sistema A:

```text
muchas consultas contains(x)
muchas inserciones y eliminaciones por clave
no se necesita orden
```

Sistema B:

```text
muchas consultas contains(x)
también se necesita imprimir las claves ordenadas
```

Sistema C:

```text
solo interesa consultar y retirar repetidamente
el mínimo actual
```

Sistema D:

```text
los elementos salen estrictamente
en el orden en que llegaron
```

Sistema E:

```text
90 % get(i)
10 % inserciones al final
```

Sistema F:

```text
se dispone de una referencia directa a un nodo
y se realizan modificaciones locales alrededor de él
```

Elige entre:

```text
arreglo dinámico
DLList
Queue
AVL
BinaryHeap
tabla hash con encadenamiento
```

Para cada elección:

1. identifica el ADT,
2. indica la representación relevante,
3. formula el invariante principal,
4. identifica la operación dominante,
5. justifica los costos,
6. indica una operación que la estructura no favorece,
7. propone una alternativa,
8. explica por qué tu primera elección se ajusta mejor.

### B. Retos integradores

#### Reto 1. `contains` recorre todos los buckets y parece funcionar

Un estudiante implementa:

```java
boolean contains(Object value) {
    for (List<T> bucket : buckets) {
        if (bucket.contains(value)) {
            return true;
        }
    }
    return false;
}
```

No lo ejecutes inicialmente.

1. explica por qué puede devolver resultados correctos,
2. identifica qué invariante está ignorando,
3. compara su trabajo con `buckets[hash(value)]`,
4. construye una tabla donde examine casi toda la estructura,
5. expresa su peor caso en función de `n`,
6. explica qué ventaja del hashing se pierde,
7. determina si podría encontrar una clave almacenada en el bucket equivocado,
8. explica por qué eso puede ocultar corrupción,
9. escribe una versión correcta,
10. relaciona el problema con una búsqueda BST que ignorara el orden.

#### Reto 2. `add` inserta primero y verifica duplicados después

Se propone:

```java
boolean add(T value) {
    buckets[hash(value)].add(value);
    size++;

    if (contains(value)) {
        return false;
    }

    return true;
}
```

1. determina qué retorna al agregar una clave nueva,
2. determina qué ocurre con una clave existente,
3. explica por qué `contains` después de insertar siempre encuentra una aparición,
4. muestra cómo se rompe la política sin duplicados,
5. muestra cómo queda incorrecto `size`,
6. construye el ejemplo mínimo,
7. corrige el orden de las acciones,
8. explica por qué localizar debe preceder a modificar,
9. compara con `add` de BST,
10. justifica `O(k)`.

#### Reto 3. El hash produce índices negativos

Un estudiante usa:

```java
value.hashCode() % buckets.length
```

en lugar de:

```java
Math.floorMod(value.hashCode(), buckets.length)
```

Considera:

```text
hashCode = -31
buckets.length = 7
```

1. calcula `-31 % 7` en Java,
2. determina si es un índice válido,
3. calcula `Math.floorMod(-31, 7)`,
4. formula el invariante de rango,
5. explica por qué un `hashCode` válido puede ser negativo,
6. distingue clave, `hashCode` e índice,
7. escribe una función correcta,
8. diseña pruebas con hash positivo, cero y negativo.

#### Reto 4. Una función hash cambia entre llamadas

Se propone:

```java
private int hash(Object value) {
    return Math.floorMod(
        value.hashCode() + counter++,
        buckets.length
    );
}
```

Se ejecuta:

```text
add(x)
contains(x)
remove(x)
```

1. explica por qué la misma clave puede dirigirse a buckets distintos,
2. traza con `counter = 0`,
3. identifica qué propiedad deja de ser utilizable,
4. determina si `add(x)` puede funcionar y `contains(x)` fallar,
5. explica qué significa hash determinista,
6. corrige el diseño,
7. diseña una prueba para detectar no determinismo,
8. explica por qué esto no es una colisión normal.

#### Reto 5. `remove` siempre decrementa `size`

Se propone:

```java
boolean remove(T value) {
    boolean removed =
        buckets[hash(value)].remove(value);

    size--;
    return removed;
}
```

Analiza:

```text
clave presente
clave ausente
bucket vacío
bucket con varias colisiones
```

Para cada caso:

1. indica el retorno,
2. indica si cambia el contenido,
3. indica si debería cambiar `size`,
4. muestra cuándo se rompe el invariante,
5. construye una secuencia que produzca `size < 0`,
6. corrige el código,
7. relaciona la regla con `n--` en BST,
8. justifica `O(k)`.

#### Reto 6. Todas las claves están visibles, pero una está mal ubicada

Se usa:

```text
hash(x) = floorMod(x, 8)
```

Estado:

```text
0: [16]
1: [9, 33]
2: [18]
3: [27]
4: [12]
5: [ ]
6: [22]
7: [31]

size = 8
```

1. verifica cada clave,
2. identifica cualquier clave mal ubicada,
3. determina qué retorna `contains` para ella,
4. explica por qué imprimir todo no prueba correctitud,
5. explica cómo `size` puede ser correcto y ubicación incorrecta,
6. propone la reparación mínima,
7. diseña `validLocation()`,
8. justifica su complejidad,
9. relaciona el caso con un BST cuyo inorder es correcto pero `parent` está mal.

#### Reto 7. Dos hashes tienen rango válido, pero distribución muy distinta

Se almacenan:

```text
8, 16, 24, 32, 40, 48, 56, 64
```

en 8 buckets.

Se proponen:

```text
h1(x) = floorMod(x, 8)
h2(x) = floorMod(x / 8, 8)
```

1. calcula todos los índices,
2. dibuja ambas tablas,
3. cuenta colisiones,
4. determina longitud máxima de bucket,
5. estima una búsqueda fallida en el bucket más largo,
6. verifica que ambas produzcan índices válidos,
7. explica por qué rango válido no implica buena distribución,
8. determina cuál funciona mejor para estas claves,
9. construye otro conjunto donde `h1` se comporte mejor,
10. explica por qué un solo conjunto no demuestra calidad general.

#### Reto 8. Arreglo, AVL, heap y tabla hash reciben las mismas claves

Se insertan:

```text
42, 17, 68, 9, 31, 55, 74, 26, 60
```

en:

```text
arreglo dinámico sin ordenar
AVL
BinaryHeap mínimo
tabla hash con 7 buckets
```

Para la tabla:

```text
hash(x) = floorMod(x, 7)
```

1. construye el arreglo,
2. dibuja el AVL,
3. dibuja el heap después de `add` y `bubbleUp`,
4. construye la tabla hash,
5. traza `contains(60)` en las cuatro,
6. traza una búsqueda fallida de 61,
7. identifica qué información permite descartar candidatos,
8. compara búsqueda arbitraria,
9. compara obtención del mínimo,
10. compara recorrido ordenado,
11. explica por qué el mismo conjunto admite invariantes distintos,
12. elige la mejor estructura para búsqueda exacta, orden, mínimo y acceso por índice.

#### Reto 9. Queue + tabla hash + Stack

Se dispone de:

```text
ArrayQueue de solicitudes
ChainedHashSet de claves activas
LinkedStack de claves eliminadas
```

Solicitudes:

```text
ADD 14
ADD 25
ADD 36
REMOVE 25
ADD 47
REMOVE 99
ADD 14
REMOVE 36
```

La tabla usa:

```text
m = 5
hash(x) = floorMod(x, 5)
```

Cuando una eliminación tiene éxito, la clave se agrega al Stack.

1. procesa todo en FIFO,
2. dibuja la tabla después de cada operación,
3. registra `size`,
4. registra el Stack,
5. identifica colisiones,
6. explica `ADD 14` repetido,
7. explica `REMOVE 99`,
8. determina el primer `pop()` final,
9. identifica qué comportamiento proviene de FIFO, LIFO y hashing,
10. explica por qué combinar ADT no mezcla sus invariantes.

#### Reto 10. Se usa `size` como cantidad de buckets

Se propone:

```java
private int hash(Object value) {
    return Math.floorMod(value.hashCode(), size);
}
```

Analiza:

```text
size = 0
size = 1
size = 3 con buckets.length = 11
```

1. explica el caso `size = 0`,
2. determina el rango cuando `size = 1`,
3. determina el rango cuando `size = 3`,
4. explica qué buckets quedan inaccesibles,
5. muestra cómo cambiar `size` puede volver ilocalizable una clave anterior,
6. distingue cantidad de elementos de cantidad de buckets,
7. relaciona con `n` y `a.length`,
8. corrige el método,
9. explica por qué el divisor forma parte de la configuración estructural.

#### Reto 11. Convertir un AVL a una tabla hash

Se tiene un AVL válido con:

```text
12, 20, 27, 35, 41, 53, 68, 74, 89
```

Se construye una tabla con:

```text
m = 7
hash(x) = floorMod(x, 7)
```

1. indica qué recorrido usarías para visitar todas las claves,
2. construye la tabla,
3. registra colisiones,
4. compara `size`,
5. explica qué invariante AVL deja de tener sentido,
6. explica qué información de orden se pierde,
7. compara `contains(53)`, mínimo, máximo y recorrido creciente,
8. compara garantías de complejidad,
9. explica por qué cambia representación pero no conjunto lógico,
10. explica por qué la tabla no permite reconstruir de forma única el AVL original.

#### Reto 12. Dos versiones correctas de `remove`, costos distintos

Versión A:

```java
boolean remove(T value) {
    Iterator<T> it =
        buckets[hash(value)].iterator();

    while (it.hasNext()) {
        if (it.next().equals(value)) {
            it.remove();
            size--;
            return true;
        }
    }
    return false;
}
```

Versión B:

```java
boolean remove(T value) {
    for (int i = 0; i < buckets.length; i++) {
        Iterator<T> it = buckets[i].iterator();

        while (it.hasNext()) {
            if (it.next().equals(value)) {
                it.remove();
                size--;
                return true;
            }
        }
    }
    return false;
}
```

1. demuestra que ambas pueden dar el mismo resultado,
2. identifica cuál usa el invariante de ubicación,
3. construye un caso con gran diferencia de trabajo,
4. expresa el costo de A en función de `k`,
5. expresa el peor caso de B en función de `n`,
6. explica cuál podría encontrar una clave mal ubicada,
7. explica por qué eso no es una ventaja estructural,
8. relaciona con recorrer todo un BST frente a seguir un camino.

#### Reto 13. `hashCode()` correcto para igualdad, pero distribución pésima

Se define:

```java
static class Key {
    final int id;

    Key(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Key k)) {
            return false;
        }
        return id == k.id;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
```

1. determina si objetos iguales tienen el mismo `hashCode`,
2. explica si varias claves distintas pueden almacenarse correctamente,
3. dibuja la tabla para ids 10, 20, 30, 40 y 50,
4. determina longitud del bucket,
5. analiza `contains(new Key(50))`,
6. analiza una búsqueda fallida,
7. explica por qué puede ser correcto pero ineficiente,
8. compara con un BST correcto pero degenerado,
9. propone un `hashCode()` sencillo más informativo,
10. distingue correctitud de `hashCode` de calidad de distribución.

#### Reto 14. El hashing multiplicativo desplaza la cantidad equivocada

Se propone:

```java
static int hash(int hashCode, int d) {
    return (Z * hashCode) >>> d;
}
```

en lugar de utilizar `W - d`.

Con:

```text
W = 32
d = 3
```

1. explica cuántos bits conserva aproximadamente la versión propuesta,
2. compara el rango con `0..7`,
3. explica por qué usar `>>>` no basta,
4. identifica el papel de `W - d`,
5. escribe la versión correcta,
6. diseña una prueba de rango,
7. explica por qué rango correcto no demuestra buena distribución,
8. relaciona el defecto con correctitud frente a eficiencia.

#### Reto 15. Diseñar una batería mínima de pruebas

Recibes una implementación desconocida de `ChainedHashSet<T>`.

Solo puedes usar:

```text
size
contains
add
remove
printBuckets
```

Diseña pruebas para:

```text
tabla vacía
inserción en bucket vacío
colisión
duplicado
contains existente
contains ausente
remove existente
remove ausente
remove dentro de bucket con varias claves
size tras inserciones
size tras duplicados
size tras eliminaciones fallidas
preservación de otras claves
hashCode negativo
```

Para cada prueba indica:

```text
estado inicial
operación
resultado esperado
estado final esperado
invariante verificado
defecto que podría revelar
```

Incluye al menos una secuencia donde el error no aparezca en la primera operación y solo sea visible después.

#### Reto 16. Diseñar `validState()`

Se desea agregar para depuración:

```java
boolean validState()
```

Debe verificar:

```text
buckets != null
cada bucket existe
cada elemento está en buckets[hash(x)]
size coincide con el total
no existen duplicados
```

No uses `HashSet`.

1. escribe una especificación,
2. diseña el algoritmo,
3. explica cómo detectar duplicados,
4. analiza el costo con longitudes `k0, k1, ..., km-1`,
5. da una cota simple en función de `n`,
6. explica por qué verificar puede costar más que una operación ordinaria,
7. compara con `validBST`, verificación de `parent` y verificación min-heap,
8. explica por qué `validState()` no necesita pertenecer al ADT público.

### C. Ampliación opcional

#### Ampliación 1. `bucketSizeFor(T value)`

Diseña:

```java
int bucketSizeFor(T value)
```

que retorne cuántos elementos contiene el bucket al que se dirige `value`.

1. escribe la implementación,
2. explica por qué no necesita buscar `value`,
3. determina su costo,
4. explica qué informa sobre una futura búsqueda,
5. explica por qué es una operación de diagnóstico.

#### Ampliación 2. `maxBucketSize()`

Diseña:

```java
int maxBucketSize()
```

Restricciones:

```text
no modificar la tabla
no usar otra tabla hash
no ordenar
```

1. escribe el algoritmo,
2. justifica su costo en función de `m = buckets.length`,
3. construye dos tablas con igual `size` y distinto máximo,
4. relaciona el máximo con el peor trabajo local de `contains`.

#### Ampliación 3. Contar colisiones de inserción

Define una colisión de inserción como:

```text
insertar una nueva clave
en un bucket que ya contenía
al menos una clave
```

Diseña un contador:

```text
numberOfCollisions
```

1. indica cuándo se incrementa,
2. explica qué ocurre con duplicados,
3. construye una secuencia con `size = 8` y muchas colisiones,
4. otra con `size = 8` y ninguna colisión si hay suficientes buckets,
5. explica por qué contar colisiones no sustituye medir la longitud de los buckets.

#### Ampliación 4. Copiar contenido lógico sin copiar disposición física

Se tiene una tabla válida A y se construye otra B con las mismas claves, pero B puede usar una función hash diferente.

1. explica por qué no debe copiarse cada bucket al mismo índice,
2. indica qué debe hacerse con cada clave,
3. explica qué estado lógico sí debe preservarse,
4. explica qué representación puede cambiar,
5. compara con reconstruir un BST o un heap a partir del mismo conjunto,
6. explica por qué un mismo ADT puede tener varias representaciones internas válidas.

No desarrolles todavía rehashing como política de crecimiento.

### Síntesis final

La cadena de análisis sigue siendo:

```text
ADT
    define comportamiento

representación
    define estado interno

invariante
    define estados válidos

algoritmo
    localiza y modifica

complejidad
    depende del trabajo exigido
```

Para hashing con encadenamiento:

```text
clave
    |
    v
hashCode
    |
    v
función hash
    |
    v
índice
    |
    v
bucket
    |
    v
colección local
```

La correctitud exige:

```text
cada x está en buckets[hash(x)]

size coincide con el total almacenado
```

Si el bucket seleccionado tiene longitud `k`:

```text
contains -> O(k)
add      -> O(k)
remove   -> O(k)
```

Con buena distribución:

```text
k pequeño en promedio
->
costo esperado cercano a O(1)
```

En el peor caso:

```text
k = n
->
O(n)
```

La integración con las semanas anteriores puede resumirse así:

```text
arreglo dinámico
    acceso por índice

lista enlazada
    modificación local con referencia

Stack / Queue / Deque
    política de acceso

BST
    orden para decidir un camino

AVL
    balance para controlar altura

BinaryHeap
    prioridad extrema mediante forma completa

tabla hash
    búsqueda exacta mediante bucket candidato
```

La pregunta que debe quedar abierta es:

```text
¿qué ocurre cuando size crece
pero buckets.length permanece fijo?
```

Esa pregunta conduce después a:

```text
factor de carga
rehashing
```
