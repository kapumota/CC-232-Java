### Lectura: Map, Set y elección de representación sobre búsqueda por clave

Esta lectura consolida y amplía las ideas trabajadas en la Semana 10 de CC232.

Durante las nueve semanas anteriores hemos cambiado de representación, de invariante o de ADT cuando cambió el tipo de operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico.

```text
arreglo + tamaño lógico + capacidad
```

La representación permitió acceso directo por índice y obligó a distinguir entre estado lógico, almacenamiento físico, crecimiento y costo amortizado.

En la Semana 2 utilizamos nodos enlazados.

```text
nodo -> nodo -> nodo
```

La modificación local se volvió barata cuando ya conocíamos las referencias apropiadas, aunque localizar una posición dejó de ser directo.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`.

La pregunta pasó a ser:

```text
¿qué operaciones permite el ADT sobre una secuencia?
```

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda.

```text
subárbol izquierdo < nodo < subárbol derecho
```

El invariante de orden permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones.

En la Semana 6 estudiamos AVL.

El BST seguía siendo correcto, pero añadimos altura y balance para impedir que una mala forma del árbol destruyera la eficiencia.

En la Semana 7 estudiamos `Priority Queue` y `BinaryHeap`.

La pregunta cambió a:

```text
¿cómo mantener disponible el elemento mínimo?
```

El heap respondió mediante:

```text
forma completa + invariante min-heap
```

En la Semana 8 estudiamos hashing con encadenamiento separado.

La pregunta fue:

```text
¿cómo localizar una clave exacta sin mantener orden global?
```

La respuesta siguió el camino:

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
```

En la Semana 9 mantuvimos la misma tabla hash y preguntamos:

```text
¿qué ocurre si n crece pero la cantidad de buckets permanece fija?
```

La respuesta introdujo factor de carga, crecimiento y rehashing.

La Semana 10 no necesita volver a construir desde cero una tabla hash. Utiliza lo aprendido durante las Semanas 8 y 9 para formular dos preguntas nuevas:

```text
¿cómo asociamos información a una clave?

¿cómo representamos solamente la pertenencia de elementos sin duplicados?
```

Estas preguntas conducen a dos ADT fundamentales:

```text
Map<K,V>
    clave -> valor

Set<T>
    elemento -> pertenece / no pertenece
```

La idea central de la semana puede anticiparse así:

```text
tabla hash
    proporciona una representación eficiente para localizar claves exactas

Map
    utiliza claves para localizar valores asociados

Set
    utiliza claves para representar pertenencia y unicidad
```

El objetivo no es memorizar la jerarquía completa de colecciones de Java ni aprender una lista extensa de métodos.

El objetivo es continuar la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    cómo almacenamos el estado

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo consultamos y modificamos el estado

complejidad
    cuánto trabajo exige cada operación
```

Al finalizar la lectura deberías poder explicar cómo `Map` y `Set` reutilizan la búsqueda por clave, cómo una tabla hash puede implementarlos, por qué `HashSet` y `TreeSet` representan el mismo ADT con propiedades distintas y cómo elegir una estructura según la operación que se desea favorecer.

### 1. El problema heredado de la Semana 9

Al terminar la Semana 9 podíamos mantener una colección de claves dentro de una tabla hash con encadenamiento.

Conceptualmente:

```text
t[0] -> [clave]
t[1] -> [clave, clave]
t[2] -> []
t[3] -> [clave]
```

Para localizar una clave `x` realizábamos:

```text
x
|
v
hash(x)
|
v
bucket candidato
|
v
comparar dentro del bucket
```

La tabla resolvía muy bien preguntas como:

```text
¿está x?
```

o:

```text
agregar x
eliminar x
```

Pero muchas aplicaciones necesitan algo más.

Supongamos que tenemos códigos de estudiantes:

```text
20260101
20260102
20260103
```

y queremos guardar una nota para cada código:

```text
20260101 -> 15
20260102 -> 18
20260103 -> 12
```

La clave sigue sirviendo para localizar.

Lo nuevo es que cada clave tiene información asociada.

La transición conceptual es:

```text
Semana 8-9

clave
    |
    v
pertenencia


Semana 10

clave
    |
    v
valor asociado
```

Esa asociación conduce al ADT `Map`.

### 2. De una clave a un par clave-valor

Una entrada de un mapa puede entenderse como un par:

```text
(key, value)
```

Por ejemplo:

```text
("grafo", 3)
("heap", 2)
("tabla hash", 1)
```

La clave identifica la asociación.

El valor contiene la información que queremos recuperar o actualizar.

En:

```text
"grafo" -> 3
```

tenemos:

```text
clave
    "grafo"

valor
    3
```

La separación es importante porque el hashing se aplica a la clave, no al valor.

Queremos localizar rápidamente:

```text
"grafo"
```

para después recuperar:

```text
3
```

El valor podría cambiar sin que la identidad de la clave cambie.

Por ejemplo:

```text
"grafo" -> 2
```

puede actualizarse a:

```text
"grafo" -> 3
```

La clave sigue siendo la misma.

### 3. El ADT Map<K,V>

`Map<K,V>` describe una colección de asociaciones entre claves de tipo `K` y valores de tipo `V`.

Una interfaz conceptual mínima puede incluir:

```java
V get(K key)
V put(K key, V value)
boolean containsKey(K key)
V remove(K key)
int size()
```

En Java las firmas concretas de la interfaz tienen algunos detalles adicionales, pero para esta semana interesa su comportamiento.

#### get(key)

`get(key)` intenta recuperar el valor asociado a una clave.

Conceptualmente:

```text
Map

"heap"       -> 2
"grafo"      -> 3
"tabla hash" -> 1
```

Entonces:

```text
get("grafo") -> 3
```

La operación empieza localizando la clave.

No busca el valor `3` directamente.

#### containsKey(key)

`containsKey(key)` responde si existe una asociación cuya clave sea `key`.

```text
containsKey("heap")  -> true
containsKey("avl")   -> false
```

La pregunta se refiere a la clave.

#### put(key, value)

`put(key, value)` crea o actualiza una asociación.

Existen dos casos conceptualmente distintos.

Primer caso:

```text
la clave no existe
```

Entonces:

```text
put("avl", 1)
```

agrega una nueva asociación.

Segundo caso:

```text
la clave ya existe
```

Entonces:

```text
put("avl", 2)
```

actualiza el valor asociado a `"avl"`.

No deben aparecer dos asociaciones independientes con la misma clave.

#### remove(key)

`remove(key)` elimina la asociación identificada por la clave.

Por ejemplo:

```text
antes

"heap"  -> 2
"grafo" -> 3
"avl"   -> 1
```

después de:

```text
remove("avl")
```

queda:

```text
"heap"  -> 2
"grafo" -> 3
```

#### size()

`size()` cuenta asociaciones, es decir, claves distintas almacenadas.

Si actualizamos:

```text
"grafo" -> 2
```

a:

```text
"grafo" -> 3
```

el tamaño no aumenta.

La estructura sigue teniendo una sola clave `"grafo"`.

### 4. Unicidad de claves como invariante de Map

Una propiedad central de `Map` es:

```text
cada clave aparece como máximo una vez
```

Podemos expresarlo como invariante lógico:

```text
para toda clave k
existe a lo más una asociación (k, value)
```

Esto distingue:

```text
actualizar una asociación
```

de:

```text
insertar otra asociación con la misma clave
```

Por ejemplo, el siguiente estado no representa un `Map` válido bajo esta semántica:

```text
"grafo" -> 2
"grafo" -> 3
```

El estado correcto después de una actualización es:

```text
"grafo" -> 3
```

Por tanto, `put` debe preservar la unicidad.

Esta idea reutiliza una propiedad que ya apareció en la tabla hash de las Semanas 8 y 9.

Cuando `add(x)` rechazaba duplicados, la estructura mantenía una sola presencia lógica de cada clave.

### 5. ADT frente a implementación en Map

`Map` describe comportamiento.

No obliga a una única representación.

Conceptualmente podríamos tener:

```text
Map
|
+-- representación mediante hashing
|
+-- representación mediante árbol de búsqueda balanceado
```

Las dos representaciones pueden ofrecer asociaciones clave-valor, pero mantienen propiedades distintas.

Una representación mediante hashing favorece principalmente:

```text
búsqueda exacta por clave
```

Una representación mediante árbol balanceado puede además mantener:

```text
orden de las claves
mínimo
máximo
predecesor
sucesor
recorridos ordenados
```

La Semana 10 utiliza principalmente la primera idea porque continúa directamente las Semanas 8 y 9.

En Java podemos declarar:

```java
Map<String, Integer> frequencies;
```

La variable se expresa mediante el ADT `Map`.

Después podemos seleccionar una implementación concreta.

En el archivo `Semana10_MapSet0.java` se utiliza:

```java
Map<String, Integer> result = new LinkedHashMap<>();
```

La parte izquierda expresa el comportamiento requerido.

```text
Map<String, Integer>
```

La parte derecha selecciona una implementación.

```text
LinkedHashMap
```

Para esta semana no necesitamos estudiar la representación interna completa de `LinkedHashMap`.

Se utiliza porque conserva un orden de iteración determinista para que la salida del ejemplo sea estable.

La idea algorítmica continúa siendo:

```text
clave -> valor
```

### 6. Cómo una tabla hash puede implementar Map

En las Semanas 8 y 9 un bucket podía almacenar claves:

```text
bucket 0 -> [12]
bucket 1 -> [5, 17]
bucket 2 -> [8]
```

Para implementar un `Map`, el bucket puede almacenar entradas:

```text
Entry<K,V>
    key
    value
```

Entonces podemos imaginar:

```text
bucket 0 -> [(k1,v1)]
bucket 1 -> [(k2,v2), (k3,v3)]
bucket 2 -> []
```

El hash se calcula sobre la clave:

```text
key
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
bucket de Entry
```

No calculamos el bucket a partir del valor.

Esto permite que el valor cambie sin cambiar la región en la que se busca la clave.

#### Ejemplo

Supongamos:

```text
("heap", 2)
("grafo", 3)
("tabla hash", 1)
```

Para ejecutar:

```text
get("grafo")
```

la estructura realiza conceptualmente:

```text
1. calcular hash("grafo")
2. localizar el bucket correspondiente
3. examinar las entradas del bucket
4. comparar sus claves
5. devolver el valor de la entrada cuya clave sea "grafo"
```

El resultado es:

```text
3
```

### 7. hashCode localiza una región, equals confirma la clave

Este punto conserva exactamente la distinción estudiada en hashing.

Debemos separar:

```text
clave
hashCode
índice de bucket
```

Dos claves distintas pueden terminar en el mismo bucket.

Por tanto, llegar al bucket correcto no significa haber encontrado automáticamente la clave correcta.

Supongamos:

```text
bucket 2

("heap", 2)
("grafo", 3)
```

Si ambas claves producen el mismo índice, la tabla debe distinguirlas dentro del bucket.

Conceptualmente:

```text
hashCode/hash
    reduce la región de búsqueda

equals
    confirma cuál clave es la buscada
```

Para una búsqueda correcta necesitamos una relación coherente entre igualdad y hash:

```text
si x.equals(y) es true
entonces x.hashCode() y y.hashCode()
deben ser compatibles con la misma localización
```

No es necesario que claves distintas tengan hash codes distintos.

Las colisiones siguen siendo normales.

### 8. put sobre una clave nueva y sobre una clave existente

La operación `put` tiene dos comportamientos importantes.

#### Clave ausente

Antes:

```text
"heap"  -> 2
"grafo" -> 3
```

Ejecutamos:

```text
put("avl", 1)
```

Después:

```text
"heap"  -> 2
"grafo" -> 3
"avl"   -> 1
```

La cantidad de claves distintas aumenta.

#### Clave existente

Antes:

```text
"heap"  -> 2
"grafo" -> 2
```

Ejecutamos:

```text
put("grafo", 3)
```

Después:

```text
"heap"  -> 2
"grafo" -> 3
```

La cantidad de claves distintas no aumenta.

Esta distinción será central en el conteo de frecuencias.

### 9. Aplicación: conteo de frecuencias

Considera:

```text
heap, grafo, tabla hash, grafo, heap, grafo
```

Queremos obtener:

```text
heap       -> 2
grafo      -> 3
tabla hash -> 1
```

La pregunta que hacemos para cada palabra es:

```text
¿es la primera vez que aparece o ya existe una asociación para ella?
```

Si es la primera aparición:

```text
word -> 1
```

Si ya apareció:

```text
word -> contador anterior + 1
```

El patrón general es:

```text
para cada elemento x

    localizar x en el Map

    si x no estaba
        crear x -> 1

    si x ya estaba
        actualizar x -> frecuencia(x) + 1
```

La tabla evoluciona así:

```text
procesar "heap"

heap -> 1
```

después:

```text
procesar "grafo"

heap  -> 1
grafo -> 1
```

después:

```text
procesar "tabla hash"

heap       -> 1
grafo      -> 1
tabla hash -> 1
```

después:

```text
procesar "grafo"

heap       -> 1
grafo      -> 2
tabla hash -> 1
```

después:

```text
procesar "heap"

heap       -> 2
grafo      -> 2
tabla hash -> 1
```

finalmente:

```text
procesar "grafo"

heap       -> 2
grafo      -> 3
tabla hash -> 1
```

Este algoritmo no necesita ordenar las palabras.

Tampoco necesita comparar cada palabra con todas las anteriores.

Utiliza búsqueda exacta por clave.

### 10. Relación con Semana10_MapSet0.java

El primer archivo de la semana contiene:

```java
static Map<String, Integer> frequencies(String[] words)
```

Su objetivo es construir:

```text
palabra -> cantidad de apariciones
```

La entrada es:

```java
String[] words
```

La salida es:

```java
Map<String, Integer>
```

La decisión algorítmica importante no es qué método abreviado ofrece Java.

La decisión importante es distinguir:

```text
primera aparición
```

de:

```text
aparición repetida
```

y preservar el invariante:

```text
después de procesar un prefijo del arreglo, el Map contiene para cada palabra de ese prefijo
exactamente su número de apariciones
```

Podemos formular el invariante del recorrido así:

```text
antes de procesar words[i],
frequencies contiene las frecuencias correctas
de words[0..i-1]
```

Después de procesar `words[i]`, el mismo enunciado debe quedar verdadero para:

```text
words[0..i]
```

Este razonamiento permite justificar la correctitud sin depender de un caso particular.

### 11. get y containsKey no expresan exactamente la misma pregunta

Para una clave `key`, podemos preguntar:

```text
containsKey(key)
```

o intentar:

```text
get(key)
```

Las operaciones están relacionadas, pero expresan ideas diferentes.

`containsKey` pregunta explícitamente:

```text
¿existe esta clave?
```

`get` pregunta:

```text
¿qué valor está asociado a esta clave?
```

En el ejercicio de frecuencias los valores almacenados son enteros positivos.

Por ello, un resultado `null` obtenido al consultar una palabra puede utilizarse como señal de ausencia, porque el algoritmo nunca almacena `null` como contador.

En una aplicación general conviene distinguir cuidadosamente:

```text
clave ausente
```

de:

```text
clave presente con un valor null
```

si la implementación concreta permite valores `null`.

Para CC232 no necesitamos profundizar en todas las políticas de `null` de las implementaciones de Java.

Lo importante es comprender qué pregunta expresa cada operación.

### 12. Recorrer asociaciones: Map.Entry y entrySet()

Hasta ahora hemos buscado por una clave concreta.

Sin embargo, algunas operaciones necesitan examinar todas las asociaciones.

Por ejemplo:

```text
¿qué palabra tiene la frecuencia más alta?
```

Si ya tenemos:

```text
heap       -> 2
grafo      -> 3
tabla hash -> 1
```

debemos inspeccionar los valores almacenados.

Java representa una asociación individual mediante:

```java
Map.Entry<K, V>
```

Conceptualmente:

```text
Entry
    key
    value
```

Una entrada permite consultar:

```java
entry.getKey()
entry.getValue()
```

El conjunto de entradas de un mapa puede obtenerse mediante:

```java
entrySet()
```

El método `mostFrequent(...)` proporcionado utiliza:

```java
for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
    if (entry.getValue() > bestCount) {
        best = entry.getKey();
        bestCount = entry.getValue();
    }
}
```

Este recorrido mantiene dos datos:

```text
best
    clave de la mejor entrada encontrada hasta ahora

bestCount
    mayor frecuencia encontrada hasta ahora
```

Podemos formular otro invariante de recorrido:

```text
después de examinar cierto número de entradas, bestCount es la mayor frecuencia entre las entradas ya examinadas
y best es una clave que alcanza esa frecuencia
```

Al finalizar el recorrido, todas las entradas han sido examinadas.

Por tanto, `best` corresponde a un elemento de frecuencia máxima.

### 13. Map no implica orden

Un `Map` define asociaciones clave-valor.

No define necesariamente un orden de recorrido.

Esto significa que:

```text
{heap=2, grafo=3, tabla hash=1}
```

y otro orden de presentación de las mismas asociaciones pueden representar el mismo contenido lógico.

Por ejemplo:

```text
{grafo=3, tabla hash=1, heap=2}
```

contiene exactamente las mismas asociaciones.

El orden puede ser una propiedad adicional de una implementación.

En el archivo de la semana se utiliza `LinkedHashMap` para obtener una salida determinista basada en el orden de primera inserción.

Pero el algoritmo de frecuencias no depende conceptualmente de esa propiedad.

Esta separación vuelve a mostrar:

```text
ADT
    asociaciones clave-valor

implementación
    decide propiedades adicionales de representación
```

### 14. Complejidad esperada de las operaciones por clave

En una tabla hash con encadenamiento, una operación por clave realiza:

```text
1. calcular hash
2. localizar bucket
3. trabajar dentro de ese bucket
```

Si el bucket seleccionado contiene `k` entradas:

```text
buscar dentro del bucket -> O(k)
```

En el peor caso:

```text
k = n
```

y una operación puede degradarse a:

```text
O(n)
```

Por eso no debemos afirmar:

```text
Map basado en hashing siempre cuesta O(1)
```

La afirmación apropiada para esta semana es:

```text
get        -> O(1) esperado
put        -> O(1) esperado
contains   -> O(1) esperado
remove     -> O(1) esperado
```

bajo condiciones razonables de dispersión y carga.

Esta conclusión depende de las ideas de las Semanas 8 y 9:

```text
buena distribución
+
factor de carga controlado
```

### 15. Complejidad del conteo de frecuencias

Supongamos que el arreglo contiene `n` palabras.

El algoritmo procesa cada palabra una vez.

Para cada palabra realiza una cantidad constante de operaciones de Map en el caso esperado.

Por tanto:

```text
frequencies(words) -> O(n) esperado
```

Si existen `k` palabras distintas:

```text
k <= n
```

El recorrido de `entrySet()` para encontrar la frecuencia máxima examina las `k` asociaciones.

Entonces:

```text
mostFrequent -> O(k)
```

Como:

```text
k <= n
```

el trabajo combinado sigue siendo:

```text
O(n) esperado
```

No se requiere ordenar las palabras.

### 16. Esperado y amortizado siguen siendo conceptos diferentes

La Semana 10 reutiliza dos tipos de análisis que no deben confundirse.

#### Costo esperado

El costo esperado de una operación hash depende de propiedades como:

```text
distribución de las claves
longitud esperada de los buckets
factor de carga
```

Por ejemplo:

```text
get -> O(1) esperado
```

significa que bajo las condiciones del modelo de hashing, el trabajo esperado permanece constante.

#### Costo amortizado

La Semana 9 mostró que una operación ocasional de `resize` o `rehash` puede costar:

```text
O(n)
```

porque debe reconstruir la tabla.

Sin embargo, con crecimiento geométrico esa reconstrucción no ocurre en cada inserción.

Al analizar una secuencia larga, el costo total de las reconstrucciones se reparte entre muchas operaciones.

Esta es una afirmación amortizada.

Podemos resumir:

```text
esperado
    depende del comportamiento esperado de hashing

amortizado
    reparte el costo total de operaciones costosas
    sobre una secuencia de operaciones
```

Una operación de Map basada en una tabla hash puede involucrar ambas ideas.

### 17. Del Map al ADT Set<T>

Ahora consideremos otra necesidad.

Supongamos que únicamente queremos responder:

```text
¿ya apareció este valor?
```

No necesitamos asociar un contador, una nota ni otro dato.

Solo queremos representar:

```text
pertenece
```

o:

```text
no pertenece
```

Esta necesidad conduce al ADT `Set<T>`.

Un `Set` representa una colección de elementos únicos.

Conceptualmente:

```text
Set<Integer>

{7, 2, 9, 4}
```

No representa:

```text
7 -> algo
2 -> algo
```

Solo representa la presencia de cada elemento.

La diferencia esencial es:

```text
Map
    clave -> valor

Set
    elemento -> pertenencia
```

### 18. Operaciones esenciales de Set

Para esta semana interesan:

```java
boolean add(T x)
boolean contains(T x)
boolean remove(T x)
int size()
```

#### add(x)

Intenta agregar `x`.

Si `x` no estaba:

```text
el conjunto cambia
```

Si `x` ya estaba:

```text
el conjunto no cambia
```

#### contains(x)

Pregunta:

```text
¿x pertenece al conjunto?
```

#### remove(x)

Retira `x` si está presente.

#### size()

Cuenta elementos distintos.

No cuenta cuántas veces se intentó insertar cada uno.

### 19. Unicidad como invariante de Set

La propiedad fundamental de `Set` es:

```text
cada elemento aparece como máximo una vez
```

Por ejemplo:

```text
add(7)
add(7)
```

no debe producir:

```text
{7, 7}
```

El estado lógico sigue siendo:

```text
{7}
```

Podemos expresar el invariante así:

```text
para todo valor x
x tiene como máximo una presencia lógica en el Set
```

Esta propiedad se relaciona directamente con la `ChainedHashTable<T>` estudiada en las Semanas 8 y 9, donde la inserción comprobaba primero si una clave ya estaba presente.

Por ello, la tabla hash que ya estudiamos estaba muy cerca del comportamiento de un conjunto.

### 20. HashSet: Set implementado mediante hashing

`HashSet` es una implementación del ADT `Set` basada conceptualmente en hashing.

La relación es:

```text
Set
    comportamiento de pertenencia y unicidad

HashSet
    representación basada en tabla hash
```

Para localizar un elemento:

```text
x
 |
 v
hashCode
 |
 v
función hash
 |
 v
bucket
 |
 v
equals
```

Por ello reutilizamos las mismas ideas:

```text
colisiones
buckets
factor de carga
rehashing
costo esperado
```

La diferencia es el comportamiento que exponemos al usuario.

En una tabla hash de conjunto no necesitamos asociar un valor independiente a cada elemento.

### 21. El resultado booleano de add(x)

En Java, `Set.add(x)` resulta especialmente útil porque devuelve un booleano.

Conceptualmente:

```text
add(x) -> true
    x no pertenecía al Set
    el conjunto cambió

add(x) -> false
    x ya pertenecía al Set
    el conjunto no cambió
```

Esto permite reconocer una repetición sin realizar necesariamente dos preguntas separadas.

Una forma menos compacta de pensar sería:

```text
¿contains(x)?

si no:
    add(x)
```

Pero el resultado de `add(x)` ya informa si la inserción cambió el conjunto.

Esta propiedad será la base de `firstDuplicate(...)`.

### 22. Aplicación: detectar el primer duplicado

Considera:

```text
7, 2, 9, 4, 2, 8
```

Queremos retornar:

```text
2
```

porque `2` es el primer valor que aparece nuevamente durante el recorrido de izquierda a derecha.

No estamos preguntando:

```text
¿cuál es el menor valor repetido?
```

ni:

```text
¿qué valores aparecen más de una vez?
```

La pregunta depende del orden del recorrido:

```text
¿cuál es el primer valor cuya nueva aparición encuentra una aparición anterior?
```

Para responder mantenemos un conjunto:

```text
seen
```

que representa los valores ya observados.

La traza es:

```text
valor 7

seen antes = {}
add(7) -> true
seen después = {7}
```

después:

```text
valor 2

seen antes = {7}
add(2) -> true
seen después = {7, 2}
```

después:

```text
valor 9

seen antes = {7, 2}
add(9) -> true
seen después = {7, 2, 9}
```

después:

```text
valor 4

seen antes = {7, 2, 9}
add(4) -> true
seen después = {7, 2, 9, 4}
```

después:

```text
valor 2

seen antes = {7, 2, 9, 4}
add(2) -> false
```

Ese `false` significa que el conjunto no cambió porque `2` ya pertenecía a `seen`.

Por tanto:

```text
primer duplicado = 2
```

### 23. El invariante de seen

La variable `seen` no es solamente un contenedor auxiliar.

Tiene un significado preciso durante el algoritmo.

Antes de procesar `values[i]`:

```text
seen contiene exactamente los valores que aparecen en values[0..i-1]
```

Este enunciado es un invariante del recorrido.

Al inicio:

```text
i = 0
seen = {}
```

No existe ningún elemento anterior a `values[0]`.

Por tanto, el invariante es correcto.

Si `values[i]` no está en `seen`, lo agregamos.

Entonces, antes de avanzar a `i+1`, `seen` contiene exactamente los valores vistos hasta `values[i]`.

Si `values[i]` ya está en `seen`, sabemos que apareció antes en el prefijo.

Por tanto, es un duplicado.

Además, como recorremos de izquierda a derecha y terminamos inmediatamente al encontrarlo, es el primer duplicado que reaparece.

Esta formulación permite justificar correctitud sin depender solamente de pruebas.

### 24. Relación con Semana10_MapSet1.java

El segundo archivo de la semana contiene:

```java
static Integer firstDuplicate(int[] values)
```

El objetivo es:

```text
retornar el primer valor que vuelve a aparecer
```

o:

```text
null
```

si no existe repetición.

La estructura auxiliar apropiada es un:

```java
Set<Integer>
```

implementado en el archivo mediante:

```java
HashSet<Integer>
```

La idea que debe reconstruir el estudiante es:

```text
recorrer de izquierda a derecha
mantener seen
usar el resultado de add
terminar en la primera repetición
```

El punto importante no es memorizar una línea de Java.

Es reconocer qué información necesitamos conservar y qué ADT expresa mejor esa información.

### 25. Detección de duplicados mediante búsqueda directa

Podríamos resolver el problema sin un Set.

Para cada posición `i`, buscaríamos el valor `values[i]` entre todas las posiciones anteriores:

```text
values[0..i-1]
```

En el peor caso:

```text
i = 0      examina 0 anteriores
i = 1      examina 1 anterior
i = 2      examina 2 anteriores
...
i = n - 1  examina n - 1 anteriores
```

El trabajo total sigue:

```text
0 + 1 + 2 + ... + (n - 1)
```

por tanto:

```text
O(n^2)
```

Este algoritmo puede ser correcto.

Pero no utiliza una representación que favorezca la pregunta de pertenencia.

### 26. Detección de duplicados con HashSet

Con `HashSet` procesamos cada valor una vez.

Para cada elemento realizamos esencialmente:

```text
add(value)
```

con costo esperado cercano a:

```text
O(1)
```

Entonces para `n` valores:

```text
firstDuplicate -> O(n) esperado
```

El espacio adicional puede crecer hasta:

```text
O(n)
```

si todos los valores son distintos.

Aquí aparece una decisión clásica de algoritmos:

```text
más memoria auxiliar a cambio de evitar búsquedas repetidas
```

No existe una representación universalmente mejor.

La elección depende de las operaciones que queremos favorecer y de los recursos que estamos dispuestos a utilizar.

### 27. Set no implica orden

El ADT `Set` exige unicidad.

No exige orden de recorrido.

Por tanto, de:

```text
{7, 2, 9, 4}
```

no podemos inferir que una implementación general de `Set` deba producir:

```text
2, 4, 7, 9
```

El orden es una propiedad adicional.

Esto prepara la comparación entre:

```text
HashSet
```

y:

```text
TreeSet
```

### 28. TreeSet: el mismo ADT con una representación ordenada

`TreeSet` también implementa el ADT `Set`.

Por tanto, mantiene:

```text
elementos únicos
```

Pero además conserva orden.

Conceptualmente:

```text
Set
|
+-- HashSet
|      hashing
|      no requiere orden
|
+-- TreeSet
       árbol de búsqueda balanceado
       mantiene orden
```

Esta comparación conecta directamente con la Semana 6.

Un árbol de búsqueda balanceado mantiene una altura:

```text
O(log n)
```

Por ello, operaciones básicas de un conjunto ordenado pueden mantenerse en:

```text
O(log n)
```

No necesitamos estudiar durante esta semana la implementación interna concreta del árbol utilizado por la biblioteca de Java.

Ya conocemos la idea que importa:

```text
orden global
+
balance
=
búsqueda e inserción O(log n)
```

### 29. uniqueSorted y separación entre dos propiedades

El archivo `Semana10_MapSet1.java` contiene:

```java
static Set<Integer> uniqueSorted(int[] values) {
    Set<Integer> result = new TreeSet<>();
    for (int value : values) result.add(value);
    return result;
}
```

Para:

```text
7, 2, 9, 4, 2, 8
```

el resultado es:

```text
[2, 4, 7, 8, 9]
```

Debemos separar dos propiedades.

#### Unicidad

El `2` aparece una sola vez en el resultado.

Esa propiedad pertenece al ADT:

```text
Set
```

#### Orden

Los elementos aparecen ordenados.

Esa propiedad corresponde a la implementación:

```text
TreeSet
```

Esta distinción es central.

No debemos concluir:

```text
todo Set está ordenado
```

La afirmación correcta es:

```text
todo Set mantiene unicidad

TreeSet además mantiene orden
```

### 30. HashSet frente a TreeSet

Podemos resumir:

```text
HashSet

ADT:
    Set

representación conceptual:
    tabla hash

propiedad:
    unicidad

orden:
    no garantizado por el ADT

costo típico:
    add      O(1) esperado
    contains O(1) esperado
    remove   O(1) esperado
```

En cambio:

```text
TreeSet

ADT:
    Set

representación conceptual:
    árbol de búsqueda balanceado

propiedades:
    unicidad
    orden

costo:
    add      O(log n)
    contains O(log n)
    remove   O(log n)
```

La diferencia no significa que una estructura sea siempre mejor.

Depende del problema.

### 31. Qué estructura elegir según la operación

Supongamos que solo necesitamos responder:

```text
¿x pertenece?
```

y no necesitamos orden.

Entonces una tabla hash evita mantener una propiedad adicional que no utilizaremos.

Una elección razonable es:

```text
HashSet
```

Supongamos ahora que necesitamos:

```text
elementos únicos
+
recorrido ordenado
```

Entonces:

```text
TreeSet
```

resulta más natural.

Supongamos que necesitamos:

```text
clave -> valor
```

Entonces necesitamos el ADT:

```text
Map
```

Supongamos que necesitamos:

```text
buscar sucesor y predecesor
```

Un árbol de búsqueda balanceado conserva la información de orden necesaria.

Supongamos que necesitamos repetidamente:

```text
obtener y retirar el mínimo
```

La Semana 7 mostró un ADT más específico:

```text
Priority Queue
```

con una implementación:

```text
BinaryHeap
```

La pregunta correcta antes de escoger una clase es:

```text
¿qué operación domina el problema?
```

### 32. Hashing frente a árbol balanceado

Hashing y árbol balanceado resuelven problemas relacionados, pero no mantienen la misma información.

#### Tabla hash

Mantiene una correspondencia:

```text
clave -> región candidata
```

No necesita conservar:

```text
x < y
```

entre todas las claves.

Favorece:

```text
exact-match
```

#### Árbol balanceado

Mantiene:

```text
orden global
```

y una forma que controla la altura.

Favorece:

```text
búsqueda
inserción
eliminación
recorrido ordenado
mínimo
máximo
predecesor
sucesor
```

con costos logarítmicos para las operaciones fundamentales.

Podemos resumir:

```text
HashSet
    no mantiene orden que no necesita
    O(1) esperado para pertenencia

TreeSet
    mantiene orden
    O(log n) para operaciones fundamentales
```

### 33. Hashing frente a BinaryHeap

Un `BinaryHeap` tampoco mantiene orden global como un BST.

Sin embargo, su invariante es distinto al de hashing.

El heap mantiene:

```text
padre <= hijos
```

y coloca el mínimo en una posición conocida:

```text
a[0]
```

Por ello:

```text
peek mínimo -> O(1)
```

Pero buscar una clave arbitraria en un heap puede requerir:

```text
O(n)
```

En una tabla hash:

```text
buscar una clave exacta -> O(1) esperado
```

pero no obtenemos necesariamente el mínimo eficientemente.

Esto muestra otra vez que las estructuras favorecen operaciones distintas.

### 34. Relación entre Map, Set y las estructuras anteriores

Podemos organizar las ideas estudiadas así:

```text
DynamicArray

objetivo:
    acceso por índice

representación:
    arreglo + n + capacidad

costo destacado:
    get O(1)
```

```text
Stack/Queue/Deque

objetivo:
    acceso restringido a extremos

representación:
    lista o arreglo circular

invariante:
    política LIFO/FIFO/extremos
```

```text
AVL

objetivo:
    búsqueda manteniendo orden

representación:
    árbol enlazado

invariantes:
    BST + balance

costo:
    O(log n)
```

```text
BinaryHeap

objetivo:
    mantener disponible el mínimo

representación:
    arreglo como árbol completo

invariante:
    padre <= hijos

costo:
    peek O(1)
    add/remove O(log n)
```

```text
HashSet

objetivo:
    pertenencia exacta y unicidad

representación:
    tabla hash

invariante:
    una presencia lógica por elemento

costo:
    O(1) esperado por operación
```

```text
Map basado en hashing

objetivo:
    clave -> valor

representación:
    tabla hash de entradas

invariante:
    una asociación por clave

costo:
    O(1) esperado por operación de clave
```

La estructura adecuada depende del comportamiento que queremos obtener.

### 35. Propiedad del ADT frente a propiedad de la implementación

Esta semana ofrece dos ejemplos especialmente claros.

#### Ejemplo 1: Map

El ADT exige:

```text
asociaciones clave -> valor
unicidad de claves
```

No exige necesariamente:

```text
orden de iteración
```

Si una implementación como `LinkedHashMap` preserva un orden concreto, esa es una propiedad adicional de la implementación.

#### Ejemplo 2: Set

El ADT exige:

```text
unicidad
```

No exige:

```text
orden
```

`HashSet` no mantiene orden como requisito.

`TreeSet` añade orden porque utiliza una representación ordenada.

Esta separación es la misma que vimos desde la Semana 1:

```text
ADT
    qué comportamiento prometemos

implementación
    cómo conseguimos ese comportamiento
```

### 36. Elegir primero el ADT y después la representación

Una forma disciplinada de resolver problemas es seguir dos decisiones.

Primera pregunta:

```text
¿qué comportamiento necesito?
```

Segunda pregunta:

```text
¿qué representación ofrece ese comportamiento con el costo y las propiedades apropiadas?
```

Ejemplos:

```text
código de estudiante -> nota

ADT:
    Map

representación posible:
    hashing
```

```text
códigos ya procesados

ADT:
    Set

representación posible:
    HashSet
```

```text
códigos únicos que deben recorrerse ordenados

ADT:
    Set

representación:
    TreeSet
```

```text
trabajos de los que siempre se retira el mínimo

ADT:
    Priority Queue

representación:
    BinaryHeap
```

```text
claves con consultas de sucesor y predecesor

ADT:
    conjunto ordenado

representación:
    árbol balanceado
```

La clase concreta debe aparecer después de comprender el problema.

### 37. Un patrón general: memoria auxiliar para acelerar una búsqueda

El algoritmo `firstDuplicate` ilustra un patrón que aparecerá muchas veces en algoritmos.

Sin memoria auxiliar:

```text
cada elemento
    busca entre muchos anteriores
```

El costo puede convertirse en:

```text
O(n^2)
```

Con una estructura auxiliar de pertenencia:

```text
seen
```

podemos transformar cada consulta en una búsqueda esperada constante.

Entonces:

```text
tiempo
    O(n) esperado

espacio adicional
    O(n)
```

Este intercambio entre tiempo y espacio es una decisión algorítmica importante.

No pertenece solamente a `HashSet`.

Volverá a aparecer en problemas de grafos, procesamiento de texto, cachés e indexación.

### 38. Casos borde que deben formar parte del razonamiento

Una implementación correcta no debe funcionar únicamente para el ejemplo principal.

#### Conteo de frecuencias

Arreglo vacío:

```text
[]
```

resultado:

```text
{}
```

Una sola palabra:

```text
["heap"]
```

resultado:

```text
{heap=1}
```

Todas iguales:

```text
["avl", "avl", "avl"]
```

resultado:

```text
{avl=3}
```

Todas distintas:

```text
["array", "lista", "heap"]
```

resultado:

```text
cada clave tiene frecuencia 1
```

#### Primer duplicado

Arreglo vacío:

```text
[]
```

resultado:

```text
null
```

Un elemento:

```text
[5]
```

resultado:

```text
null
```

Duplicado inmediato:

```text
[5, 5]
```

resultado:

```text
5
```

Duplicado posterior:

```text
[1, 2, 3, 1]
```

resultado:

```text
1
```

Sin duplicados:

```text
[1, 2, 3]
```

resultado:

```text
null
```

Estos casos ayudan a comprobar que los invariantes formulados describen realmente el algoritmo.

### 39. Errores conceptuales frecuentes

#### Error 1

```text
una tabla hash siempre cuesta O(1)
```

Corrección:

```text
O(1) esperado bajo condiciones razonables
de dispersión y carga
```

#### Error 2

```text
hashCode es el índice final
```

Corrección:

```text
clave -> hashCode -> transformación -> índice
```

#### Error 3

```text
si dos claves tienen el mismo hash son iguales
```

Corrección:

```text
pueden existir colisiones
equals confirma la igualdad lógica
```

#### Error 4

```text
put sobre una clave existente agrega otra clave
```

Corrección:

```text
actualiza la asociación
```

#### Error 5

```text
Set conserva cuántas veces aparece un valor
```

Corrección:

```text
Set conserva pertenencia y unicidad
```

Para contar apariciones necesitamos información adicional, por ejemplo:

```text
Map<valor, contador>
```

#### Error 6

```text
todo Set está ordenado
```

Corrección:

```text
Set exige unicidad
TreeSet añade orden
```

#### Error 7

```text
TreeSet siempre es mejor porque mantiene más información
```

Corrección:

```text
mantener propiedades que el problema no necesita
puede implicar trabajo adicional
```

#### Error 8

```text
HashSet y BinaryHeap resuelven el mismo problema
```

Corrección:

```text
HashSet favorece pertenencia exacta

BinaryHeap favorece acceso al mínimo
```

### 40. Puente hacia grafos

La Semana 11 introduce grafos.

Un grafo cambia nuevamente la pregunta.

En lugar de representar principalmente:

```text
una secuencia
un conjunto ordenado
una prioridad
una asociación
```

queremos representar:

```text
vértices
+
relaciones entre vértices
```

Por ejemplo:

```text
0 -> 1, 3
1 -> 0, 2
2 -> 1
3 -> 0
```

Aparecerá una nueva representación:

```text
lista de adyacencia
```

o:

```text
matriz de adyacencia
```

Pero los recorridos de grafos reutilizarán ideas anteriores.

#### Queue vuelve a aparecer en BFS

En BFS necesitaremos una frontera de vértices pendientes.

La política adecuada será:

```text
FIFO
```

Por tanto, reaparece el ADT `Queue` de la Semana 3.

#### La idea de seen vuelve a aparecer

Durante un recorrido debemos evitar procesar repetidamente un vértice.

Conceptualmente necesitamos mantener:

```text
visitados
```

Esta información tiene la misma semántica que:

```text
Set de elementos ya vistos
```

Si los vértices se identifican como:

```text
0, 1, 2, ..., n - 1
```

la implementación puede utilizar una representación más directa, por ejemplo:

```java
boolean[] seen;
```

Pero conceptualmente la pregunta sigue siendo:

```text
¿este vértice ya fue visitado?
```

Por tanto, la Semana 10 prepara una idea que será central en grafos:

```text
pertenencia de elementos ya procesados
```

#### Map también permite pensar en adyacencias genéricas

En aplicaciones donde los vértices no son enteros compactos, una representación conceptual posible sería:

```text
vértice -> colección de vecinos
```

es decir:

```text
Map<V, Collection<V>>
```

La Semana 11 del curso utilizará representaciones más directas para vértices numerados, pero la asociación:

```text
objeto -> información relacionada
```

será una idea reutilizable.

### 41. La progresión conceptual hasta Semana 10

Podemos resumir las primeras diez semanas así:

```text
Semana 1

posición lógica
    ->
posición física
```

```text
Semana 2

referencia
    ->
siguiente nodo
```

```text
Semana 3

reglas del ADT
    ->
posiciones permitidas de acceso
```

```text
Semana 4

comparación
    ->
rama del BST
```

```text
Semana 5

clasificación del nodo
    ->
reconexión correcta
```

```text
Semana 6

altura y balance
    ->
rotaciones
```

```text
Semana 7

índice del arreglo
    ->
posición implícita en un heap
```

```text
Semana 8

clave
    ->
hash
    ->
bucket
```

```text
Semana 9

carga creciente
    ->
resize
    ->
rehashing
```

```text
Semana 10

clave
    ->
valor
```

o:

```text
elemento
    ->
pertenencia
```

La representación cambia.

La forma de razonar permanece:

```text
ADT
    ->
representación
    ->
invariante
    ->
algoritmo
    ->
complejidad
```

### 42. Síntesis

- `Map<K,V>` representa asociaciones `clave -> valor`.
- Las claves de un `Map` son únicas. Un `put` sobre una clave existente actualiza la asociación.
- Una tabla hash puede implementar `Map` almacenando entradas `(key,value)` dentro de buckets.
- El hash se calcula sobre la clave.
- `hashCode` reduce la región de búsqueda y `equals` confirma la clave dentro del bucket.
- `get`, `put`, `containsKey` y `remove` pueden tener costo `O(1)` esperado con una tabla hash bien mantenida.
- El conteo de frecuencias utiliza el patrón `elemento -> contador`.
- Construir frecuencias para `n` elementos cuesta `O(n)` esperado.
- `Map.Entry` representa una asociación individual y `entrySet()` permite recorrer todas las asociaciones.
- `Set<T>` representa pertenencia y mantiene elementos únicos.
- `HashSet` implementa `Set` mediante hashing.
- El resultado de `add(x)` permite saber si una inserción cambió el conjunto.
- En `firstDuplicate`, `seen` contiene exactamente los valores observados antes de la posición actual.
- Buscar duplicados mediante dos recorridos anidados puede costar `O(n^2)`.
- Mantener los valores ya vistos en un `HashSet` permite obtener `O(n)` esperado.
- `TreeSet` representa el mismo ADT `Set`, pero añade orden mediante una estructura de búsqueda balanceada.
- `HashSet` favorece pertenencia exacta sin orden con costo esperado constante.
- `TreeSet` mantiene orden con operaciones fundamentales `O(log n)`.
- `Set` no implica orden. El orden pertenece a determinadas implementaciones.
- `Map` tampoco implica orden. `LinkedHashMap` preserva un orden adicional que no forma parte de la abstracción básica.
- El costo esperado de hashing y el costo amortizado de rehashing son conceptos diferentes.
- La elección de una estructura debe comenzar por la operación que se desea favorecer.
- `HashSet`, `TreeSet`, AVL y `BinaryHeap` mantienen propiedades diferentes porque resuelven necesidades diferentes.
- En grafos volverán a aparecer `Queue` para BFS y la idea de mantener elementos ya visitados.

Estas ideas pueden resumirse así:

```text
Map
    clave -> valor

Set
    elemento -> pertenencia

HashSet
    Set + hashing

TreeSet
    Set + orden

BinaryHeap
    Priority Queue + mínimo eficiente
```

y, de manera más general:

```text
no existe una estructura universalmente mejor

la representación adecuada depende
de las operaciones que queremos favorecer
```

### 43. Alcance de la semana

Para esta semana no se requiere estudiar internamente:

```text
HashMap
LinkedHashMap
TreeMap
TreeSet
```

Tampoco se requiere estudiar:

```text
árboles rojo-negro
ConcurrentHashMap
WeakHashMap
EnumMap
jerarquías completas de Collections
Streams
Collectors
compute
merge
putIfAbsent
open addressing adicional
cuckoo hashing
Robin Hood hashing
```

Estos temas pueden ser valiosos en otros contextos, pero no son necesarios para comprender el objetivo de la Semana 10.
