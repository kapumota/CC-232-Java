### Lectura: hashing con encadenamiento y localización eficiente por clave

Esta lectura consolida y amplía las ideas trabajadas en la Semana 8 de CC232.

Durante las siete semanas anteriores hemos cambiado de representación, de invariante o de ADT cada vez que cambió el tipo de operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico.

```text
arreglo + tamaño lógico + capacidad
```

La representación permitió acceso directo por índice y obligó a distinguir entre estado lógico, almacenamiento físico, crecimiento y costo amortizado.

En la Semana 2 utilizamos nodos enlazados.

```text
nodo -> nodo -> nodo
```

La modificación local se volvió barata cuando ya conocíamos las referencias adecuadas, aunque localizar una posición dejó de ser directo.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`.

La pregunta central pasó a ser:

```text
¿qué operaciones permite el ADT sobre una secuencia?
```

Las restricciones de acceso permitieron elegir representaciones especialmente apropiadas para LIFO, FIFO y operaciones por ambos extremos.

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda.

```text
subárbol izquierdo < nodo < subárbol derecho
```

Ese invariante global de orden permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones.

En la Semana 6 estudiamos AVL.

El BST seguía siendo correcto, pero añadimos información de altura y un invariante de balance para impedir que la altura creciera linealmente.

En la Semana 7 estudiamos `Priority Queue` y `BinaryHeap`.

La pregunta dejó de ser buscar una clave arbitraria y pasó a ser:

```text
¿cómo mantener disponible el elemento mínimo?
```

El `BinaryHeap` resolvió ese problema mediante dos decisiones:

```text
forma completa + invariante min-heap
```

La Semana 8 cambia nuevamente la pregunta.

```text
¿qué ocurre si no necesitamos mantener las claves ordenadas ni conservar el mínimo, sino localizar una clave exacta
lo más directamente posible?
```

Esta pregunta conduce al **hashing** y a una de sus implementaciones clásicas, una tabla hash con **encadenamiento separado**.

La idea central puede anticiparse así:

```text
clave
    dato que queremos localizar

hashCode
    representación entera asociada a la clave

función hash
    transforma el hashCode en un índice

bucket
    región candidata donde debe encontrarse la clave

colisión
    varias claves comparten el mismo bucket

encadenamiento separado
    cada bucket mantiene una colección de esas claves
```

El objetivo no es memorizar una expresión de hashing ni afirmar simplemente que una tabla hash cuesta `O(1)`.

El objetivo es continuar la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    cómo almacenamos el estado

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo localizamos y modificamos el estado

complejidad
    cuánto trabajo exige cada operación
```

Al finalizar la lectura deberías poder explicar por qué una tabla hash puede localizar una clave sin mantener orden global, 
por qué las colisiones son normales, cómo el encadenamiento separado permite resolverlas y por qué el costo depende de la longitud del bucket seleccionado.

### 1. El nuevo problema: localizar una clave exacta

Considera una colección de identificadores:

```text
1042
8517
3301
9240
4175
```

Supongamos que queremos responder repetidamente:

```text
¿está almacenada la clave 9240?
```

Si los elementos se encuentran en un arreglo sin ordenar:

```text
[1042, 8517, 3301, 9240, 4175]
```

una estrategia directa consiste en recorrer posiciones.

En el peor caso debemos examinar todos los elementos.

Por tanto:

```text
búsqueda secuencial -> O(n)
```

La pregunta de esta semana es si podemos reducir la región que debemos examinar.

No queremos necesariamente:

```text
obtener las claves ordenadas
buscar el sucesor
buscar el predecesor
mantener el mínimo
```

Solo queremos localizar una clave exacta.

### 2. Lo que ya sabíamos hacer con BST y AVL

En un BST utilizamos orden.

Para buscar `x` en un nodo `u`:

```text
x < u.x
    continuar por la izquierda

x > u.x
    continuar por la derecha

x == u.x
    encontrado
```

La decisión funciona porque el árbol mantiene:

```text
todas las claves del subárbol izquierdo < u.x

todas las claves del subárbol derecho > u.x
```

El costo depende de la altura:

```text
contains(x) -> O(h)
```

En AVL garantizamos:

```text
h = O(log n)
```

y por tanto:

```text
contains(x) -> O(log n)
```

Esto es una solución excelente cuando necesitamos orden y búsqueda.

Pero introduce una pregunta nueva:

```text
si solamente necesito saber si una clave exacta está presente,

¿es obligatorio mantener un orden global entre todas las claves?
```

Hashing responde que no.

### 3. Lo que aprendimos con BinaryHeap tampoco resuelve este problema

El `BinaryHeap` de la Semana 7 mantiene el mínimo en una posición conocida.

En un min-heap:

```text
a[0]
```

contiene el mínimo.

Por ello:

```text
peek() -> O(1)
```

Pero el heap no mantiene el orden global de un BST.

Considera:

```text
          2
        /   \
       5     4
      / \   / \
     9   7 8   6
```

Si buscamos la clave `7`, observar `5` no permite descartar todo un subárbol como en un BST.

Por tanto, para una clave arbitraria:

```text
búsqueda en BinaryHeap -> O(n)
```

La Semana 7 favorecía:

```text
obtener el mínimo
```

La Semana 8 favorece:

```text
localizar una clave exacta
```

### 4. La secuencia conceptual de las primeras ocho semanas

Podemos resumir la progresión así:

```text
Semana 1
posición lógica
->
posición física del arreglo

Semana 2
referencia
->
siguiente nodo

Semana 3
reglas del ADT
->
posiciones permitidas de acceso

Semana 4
comparación
->
rama del BST

Semana 5
clasificación del nodo
->
reconexión correcta

Semana 6
altura y balance
->
reparación mediante rotaciones

Semana 7
índice del arreglo
->
posición implícita dentro de un heap

Semana 8
clave
->
hash
->
bucket candidato
```

La representación cambia, pero la forma de razonar permanece.

### 5. Una motivación ideal: direccionamiento directo

Antes de estudiar hashing conviene imaginar un caso sencillo.

Supongamos que las únicas claves posibles son:

```text
0, 1, 2, ..., 999
```

Podríamos reservar:

```java
boolean[] present = new boolean[1000];
```

Entonces para insertar la clave `317` podríamos hacer conceptualmente:

```java
present[317] = true;
```

y para consultar:

```java
present[317]
```

La clave determina directamente la posición.

La idea es:

```text
clave
->
posición
```

Esta técnica se denomina **direccionamiento directo**.

### 6. Por qué el direccionamiento directo no siempre es práctico

El direccionamiento directo funciona bien si el universo de claves es pequeño y compacto.

Pero considera un universo como:

```text
todos los enteros de 32 bits
```

o:

```text
todas las cadenas posibles
```

Podríamos tener solamente:

```text
n = 100
```

claves almacenadas, pero un universo con millones o miles de millones de valores posibles.

Reservar una posición para cada clave posible puede desperdiciar enormes cantidades de memoria.

Aparece entonces una separación:

```text
universo de claves
    muy grande

cantidad real de elementos
    relativamente pequeña

cantidad de posiciones disponibles
    mucho menor que el universo
```

Necesitamos comprimir el espacio de claves.

### 7. Qué es una clave

Una **clave** es el dato que utilizamos para identificar o localizar un elemento.

En esta semana podemos almacenar directamente valores:

```text
"arreglo"
"lista"
"heap"
"grafo"
```

y utilizarlos como claves.

También podríamos tener objetos más complejos.

Por ejemplo:

```text
Estudiante
    codigo
    nombre
    promedio
```

y decidir que la clave sea:

```text
codigo
```

Durante esta semana trabajaremos principalmente con un conjunto de claves.

### 8. Qué significa universo de claves

El **universo de claves** es el conjunto de todos los valores que podrían aparecer como claves.

No debe confundirse con las claves actualmente almacenadas.

Por ejemplo:

```text
universo:
todos los enteros de 32 bits

elementos almacenados:
17, 25, 81, 104
```

El conjunto real puede ser pequeño aunque el universo sea enorme. Esta diferencia es precisamente la que hace interesante al hashing.

### 9. Qué es una tabla hash

Una **tabla hash** es una estructura que utiliza una función hash para transformar una clave en un índice dentro de una tabla.

Su idea básica es:

```text
clave
    |
    v
función hash
    |
    v
índice
    |
    v
bucket candidato
```

La tabla no necesita mantener las claves ordenadas.

Su objetivo es reducir la región que debemos examinar.

En lugar de preguntar:

```text
¿en cuál de todos los n elementos se encuentra x?
```

preguntamos primero:

```text
¿qué bucket corresponde a x?
```

y después buscamos solamente allí.

### 10. Qué es un bucket

Un **bucket** es una posición lógica de la tabla hash asociada a un índice.

En una tabla con cinco buckets podríamos tener:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]
```

El bucket 2 contiene:

```text
7, 12, 17
```

No significa que esas claves sean iguales. Significa que la función hash las ha dirigido al mismo índice.

En encadenamiento separado, cada bucket mantiene una colección.

### 11. Arreglo de buckets

La representación externa puede pensarse como:

```text
buckets[0]
buckets[1]
buckets[2]
...
buckets[m-1]
```

donde:

```text
m = cantidad de buckets
```

Cada posición contiene una colección.

Conceptualmente:

```text
buckets
  |
  +--> 0: [10]
  +--> 1: [ ]
  +--> 2: [7, 12, 17]
  +--> 3: [3]
  +--> 4: [14]
```

Esta representación recupera una idea de la Semana 1:

```text
el arreglo permite acceder directamente a una posición por índice
```

Pero ahora la posición contiene una colección de claves.

### 12. Una conexión directa entre Semana 1, Semana 2 y Semana 8

En Semana 1 aprendimos:

```text
índice
->
posición del arreglo
```

En Semana 2 aprendimos que una colección puede representarse mediante elementos enlazados.

Ahora combinamos ambas ideas:

```text
arreglo externo
+
colección local por posición
```

La tabla hash con encadenamiento reutiliza ideas anteriores dentro de una nueva representación.

### 13. Función hash

Una **función hash** transforma información de la clave en un índice utilizable por la tabla.

Si existen `m` buckets, el índice final debe satisfacer:

```text
0 <= hash(x) < m
```

Por ejemplo, con:

```text
m = 5
```

los únicos índices válidos son:

```text
0, 1, 2, 3, 4
```

La transformación debe respetar el rango de la representación.

### 14. Clave, hashCode e índice no son lo mismo

En Java conviene distinguir tres niveles.

```text
clave
    objeto o valor lógico

hashCode
    entero asociado a la clave

índice de bucket
    posición válida dentro de buckets
```

El flujo conceptual es:

```text
clave
    |
    v
hashCode()
    |
    v
transformación
    |
    v
índice de bucket
```

El `hashCode` puede ser negativo y puede encontrarse muy lejos del rango `0..m-1`.

Por ello no debe confundirse con el índice final.

### 15. Qué papel cumple `hashCode()` en Java

Los objetos Java pueden proporcionar:

```java
int hashCode()
```

Para esta semana interesa una propiedad fundamental:

```text
si dos objetos son iguales según el criterio de igualdad,

deben producir el mismo hashCode
```

Sin embargo:

```text
mismo hashCode
```

no implica necesariamente:

```text
objetos iguales
```

Dos objetos diferentes pueden compartir el mismo código.

### 16. Hash determinista

Para una configuración fija de la tabla, la misma clave debe dirigirse repetidamente al mismo bucket.

Conceptualmente:

```text
hash(x) = i
```

debe permitir que:

```text
add(x)
```

y después:

```text
contains(x)
```

consulten la misma región.

Por eso necesitamos una correspondencia determinista mientras la configuración relevante no cambie.

### 17. Una función didáctica sencilla

Para comprender la estructura podemos utilizar enteros y una función simple.

Supongamos:

```text
m = 5
```

y:

```text
hash(x) = floorMod(x, 5)
```

Entonces:

```text
hash(12) = 2
hash(7)  = 2
hash(17) = 2
hash(3)  = 3
hash(14) = 4
```

Esto produce:

```text
0: [ ]
1: [ ]
2: [12, 7, 17]
3: [3]
4: [14]
```

### 18. Colisiones

Una **colisión** ocurre cuando dos claves distintas producen el mismo índice.

Formalmente:

```text
x != y

pero

hash(x) = hash(y)
```

En el ejemplo:

```text
12 != 7
```

pero:

```text
hash(12) = 2
hash(7)  = 2
```

Por tanto, existe una colisión.

### 19. Las colisiones son normales

Una colisión no significa que la tabla esté dañada. Tampoco significa que la función hash sea necesariamente incorrecta.

Si tenemos muchas claves posibles y solamente `m` buckets, diferentes claves pueden compartir índices.

La pregunta real es:

```text
¿cómo representar correctamente varias claves que comparten el mismo índice?
```

### 20. El problema si cada bucket admitiera una sola clave

Supongamos:

```text
hash(12) = 2
hash(7)  = 2
```

y el bucket 2 pudiera contener solamente una clave.

Insertar 7 no puede sobrescribir 12. Tampoco podemos afirmar que 7 ya existe.

Necesitamos una política explícita de resolución de colisiones.

### 21. Encadenamiento separado

La estrategia que estudiaremos es **encadenamiento separado**.

La idea es:

```text
cada bucket mantiene una colección de todas las claves que producen ese índice
```

Por ejemplo:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]
```

La colisión no destruye datos. Simplemente hace que la búsqueda local pueda requerir más trabajo.

### 22. Por qué se llama encadenamiento separado

Cada posición de la tabla mantiene una colección independiente.

En una implementación clásica esa colección puede ser una lista enlazada.

Conceptualmente:

```text
buckets[0]
    10

buckets[1]
    vacío

buckets[2]
    7 -> 12 -> 17

buckets[3]
    3

buckets[4]
    14
```

Las colecciones están separadas por bucket.

En los archivos didácticos de esta semana utilizamos una colección Java por bucket para concentrarnos en el mecanismo de hashing.

### 23. Estado de la representación

Una tabla hash con encadenamiento necesita al menos dos tipos de información.

```text
buckets
    arreglo de colecciones

size
    cantidad total de elementos lógicos
```

En Java:

```java
private final List<T>[] buckets;
private int size;
```

Cada campo responde una pregunta distinta.

### 24. `size` no es la cantidad de buckets

Considera:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]
```

Tenemos:

```text
buckets.length = 5
```

pero:

```text
size = 6
```

Son cantidades distintas.

```text
buckets.length
    número de buckets

size
    número total de elementos
```

### 25. Invariante de ubicación

El primer invariante central de la semana es:

```text
cada elemento x almacenado se encuentra en:

buckets[hash(x)]
```

Podemos expresarlo como:

```text
x almacenado
->
x pertenece a buckets[hash(x)]
```

### 26. Un elemento puede existir físicamente y estar estructuralmente mal ubicado

Supongamos:

```text
0: [10]
1: [ ]
2: [7, 12]
3: [3, 17]
4: [14]
```

pero seguimos usando:

```text
hash(17) = 2
```

La clave 17 aparece en la tabla, pero está mal ubicada.

Una implementación correcta de:

```text
contains(17)
```

buscará solamente en:

```text
buckets[2]
```

y podría retornar `false`.

La representación está dañada aunque el valor siga visible en otro lugar.

### 27. Invariante de tamaño

El segundo invariante central es:

```text
size =
cantidad total de elementos almacenados en todos los buckets
```

Para:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]
```

el total es:

```text
1 + 0 + 3 + 1 + 1 = 6
```

Por tanto:

```text
size = 6
```

sería coherente.

### 28. Correctitud exige preservar ambos invariantes

Una operación correcta debe preservar simultáneamente:

```text
invariante de ubicación + invariante de tamaño
```

Si insertamos 22 en el bucket correcto pero olvidamos `size++`, la ubicación es correcta y el tamaño es incorrecto.

Si incrementamos `size` pero colocamos 22 en otro bucket, el tamaño puede ser correcto y la ubicación es incorrecta.

### 29. El ADT que queremos ofrecer

Durante esta semana nos interesa un comportamiento de conjunto sin duplicados.

Podemos pensar en operaciones:

```java
int size()
boolean contains(Object value)
boolean add(T value)
boolean remove(T value)
```

El ADT describe qué puede hacer el usuario.

La tabla hash describe cómo se implementa ese comportamiento.

### 30. `contains`: calcular primero, buscar después

La operación:

```text
contains(x)
```

se deriva del invariante de ubicación.

Si `x` está almacenado, debe encontrarse en:

```text
buckets[hash(x)]
```

Por tanto:

```text
1. calcular hash(x)
2. seleccionar ese bucket
3. buscar x solamente dentro de esa colección
```

No necesitamos recorrer todos los buckets.

### 31. Traza de `contains`

Considera:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]
```

Para:

```text
contains(17)
```

calculamos:

```text
hash(17) = 2
```

y buscamos en:

```text
[7, 12, 17]
```

Resultado:

```text
true
```

Para:

```text
contains(22)
```

obtenemos también:

```text
hash(22) = 2
```

pero 22 no aparece en esa colección.

Resultado:

```text
false
```

### 32. La función hash no demuestra existencia

Si:

```text
hash(22) = 2
```

eso no significa:

```text
22 está almacenado
```

Solo significa:

```text
si 22 está almacenado, debe encontrarse en buckets[2]
```

Por tanto:

```text
hash
    localiza una región candidata

búsqueda local
    confirma si la clave existe
```

### 33. `add`: localizar antes de modificar

Para agregar una clave sin permitir duplicados:

```text
1. calcular hash(x)
2. comprobar si x ya está en ese bucket
3. si existe, retornar false
4. si no existe, insertar
5. incrementar size
6. retornar true
```

La lógica recuerda una idea de BST:

```text
primero localizar
después modificar
```

Lo que cambia es el mecanismo de localización.

### 34. Insertar un elemento nuevo

Supongamos:

```text
0: [10]
1: [ ]
2: [7, 12]
3: [3]
4: [14]

size = 5
```

Ejecutamos:

```text
add(17)
```

Calculamos:

```text
hash(17) = 2
```

No existe 17 en `[7, 12]`.

Insertamos:

```text
2: [7, 12, 17]
```

y actualizamos:

```text
size = 6
```

### 35. Duplicados

Supongamos:

```text
2: [7, 12, 17]
size = 6
```

Ejecutamos:

```text
add(12)
```

La búsqueda local encuentra 12.

Si el ADT representa un conjunto:

```text
add(12) -> false
```

y no debe modificarse:

```text
buckets
size
```

### 36. `remove`: localizar, eliminar y actualizar

La eliminación sigue la misma estrategia inicial.

```text
1. calcular hash(x)
2. buscar x dentro del bucket correspondiente
3. si no existe, retornar false
4. eliminar exactamente x
5. decrementar size
6. retornar true
```

No se necesita buscar por toda la tabla.

### 37. Traza de `remove`

Considera:

```text
0: [10]
1: [ ]
2: [7, 12, 17]
3: [3]
4: [14]

size = 6
```

Ejecutamos:

```text
remove(12)
```

Tenemos:

```text
hash(12) = 2
```

Eliminamos solamente 12.

Resultado:

```text
2: [7, 17]
size = 5
```

### 38. Eliminar una clave ausente

Ejecutamos:

```text
remove(22)
```

Calculamos:

```text
hash(22) = 2
```

Buscamos en:

```text
[7, 17]
```

22 no aparece.

Entonces:

```text
remove(22) -> false
```

y:

```text
size
```

no cambia.

### 39. Casos frontera de los buckets

Conviene razonar explícitamente sobre varios estados.

#### Bucket vacío

```text
2: [ ]
```

Una búsqueda termina inmediatamente.

#### Bucket con un elemento

```text
2: [17]
```

Una comparación puede bastar.

#### Bucket con varias colisiones

```text
2: [7, 12, 17, 22]
```

Puede ser necesario examinar varios elementos.

#### Clave ausente en bucket no vacío

Buscar una clave ausente exige comprobar que no aparece en esa colección.

#### Duplicado

Agregar una clave que ya existe no modifica el conjunto.

### 40. Una implementación conceptual en Java

Podemos representar una tabla hash con encadenamiento mediante:

```java
static class ChainedHashSet<T> {
    private final List<T>[] buckets;
    private int size;
}
```

La estructura externa es:

```text
arreglo de colecciones
```

### 41. Construcción de los buckets

Si la tabla recibe una capacidad:

```java
ChainedHashSet(int capacity)
```

podemos preparar cada colección:

```java
buckets = new ArrayList[capacity];

for (int i = 0; i < capacity; i++) {
    buckets[i] = new ArrayList<>();
}
```

Al terminar:

```text
0: [ ]
1: [ ]
2: [ ]
...
```

y:

```text
size = 0
```

### 42. Una función de índice sencilla en Java

Una implementación didáctica puede utilizar:

```java
private int hash(Object value) {
    return Math.floorMod(
        value.hashCode(),
        buckets.length
    );
}
```

La expresión:

```java
value.hashCode()
```

obtiene un entero.

`Math.floorMod` lo reduce al rango válido.

La idea que debe recordarse es:

```text
hashCode
->
índice válido
```

### 43. Por qué no usar simplemente `%` sin pensar en el signo

En Java un `hashCode` puede ser negativo.

Una expresión como:

```java
value.hashCode() % buckets.length
```

puede producir un resultado negativo.

Pero un índice debe satisfacer:

```text
0 <= index < buckets.length
```

La reducción debe preservar esa propiedad.

### 44. Hashing multiplicativo

El primer archivo de la semana presenta otra transformación.

Se define:

```java
static final int W = 32;
static final int Z = 0x9E3779B9;
```

y se busca implementar conceptualmente:

```text
((Z * hashCode) mod 2^W)
dividido entre
2^(W-d)
```

La idea puede expresarse como:

```text
hashCode
    |
    v
multiplicación y mezcla
    |
    v
palabra de W bits
    |
    v
selección de d bits
    |
    v
índice
```

### 45. Interpretar `d`

Si:

```text
d = 3
```

entonces:

```text
2^d = 8
```

y los índices posibles son:

```text
0..7
```

Una llamada:

```java
hash(key.hashCode(), 3)
```

debe retornar un valor dentro de ese rango.

### 46. El desplazamiento lógico

En Java podemos expresar la selección de bits mediante:

```java
return (Z * hashCode) >>> (W - d);
```

El operador:

```text
>>>
```

es un desplazamiento lógico hacia la derecha.

Para esta lectura interesa comprender su papel estructural:

```text
conservar d bits para producir un índice
```

El foco sigue siendo:

```text
clave
->
índice
->
bucket
```

### 47. `dimensionFor(int n)`

El archivo de la semana también pide determinar el menor `d >= 1` tal que:

```text
2^d > n
```

Por ejemplo:

```text
n = 8
2^3 = 8
2^4 = 16
```

Entonces:

```text
dimensionFor(8) = 4
```

Una implementación conceptual es:

```java
static int dimensionFor(int n) {
    int d = 1;

    while ((1L << d) <= n) {
        d++;
    }

    return d;
}
```

### 48. `contains` en la representación Java

La implementación puede ser muy pequeña:

```java
boolean contains(Object value) {
    return buckets[hash(value)].contains(value);
}
```

Pero esa línea contiene varias decisiones conceptuales.

```text
hash(value)
    calcula el índice

buckets[...]
    selecciona una única colección

contains(value)
    confirma igualdad dentro del bucket
```

### 49. `add` en la representación Java

Una implementación puede ser:

```java
boolean add(T value) {
    if (contains(value)) {
        return false;
    }

    buckets[hash(value)].add(value);
    size++;

    return true;
}
```

La secuencia expresa:

```text
comprobar duplicado
->
calcular ubicación
->
insertar
->
actualizar tamaño
```

### 50. `remove` en la representación Java

Para eliminar mientras recorremos una colección Java podemos utilizar un `Iterator`.

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

Lo importante para la estructura es:

```text
recorrer únicamente el bucket correcto eliminar solamente la clave encontrada
decrementar size exactamente una vez
no modificar nada si la clave no existe
```

### 51. Por qué `remove` no recorre todos los buckets

Supongamos:

```text
hash(x) = 3
```

Si el invariante es correcto, `x` solo puede estar en:

```text
buckets[3]
```

La implementación debe aprovechar el invariante que define la estructura.

### 52. El costo depende de la longitud del bucket

Sea:

```text
k
```

la cantidad de elementos dentro del bucket seleccionado.

Entonces:

```text
contains -> O(k)
add      -> O(k)
remove   -> O(k)
```

Este análisis es más preciso que afirmar simplemente:

```text
tabla hash -> O(1)
```

### 53. Mejor comportamiento concreto

Si el bucket está vacío:

```text
k = 0
```

la búsqueda puede terminar inmediatamente.

Si contiene un elemento:

```text
k = 1
```

el trabajo local es muy pequeño.

La tabla funciona bien cuando las claves quedan distribuidas de manera que los buckets relevantes permanezcan cortos.

### 54. Peor caso

Imaginemos que todas las claves terminan en el mismo bucket.

```text
0: [ ]
1: [ ]
2: [x1, x2, x3, ..., xn]
3: [ ]
4: [ ]
```

Entonces:

```text
k = n
```

y las operaciones pueden costar:

```text
contains -> O(n)
add      -> O(n)
remove   -> O(n)
```

La tabla sigue siendo correcta. El problema es de eficiencia.

### 55. Buena distribución y costo esperado

Si las claves quedan repartidas entre muchos buckets, la longitud típica de una colección puede permanecer pequeña.

En esas condiciones podemos obtener:

```text
contains
add
remove
```

con costo esperado cercano a:

```text
O(1)
```

La palabra importante es:

```text
esperado
```

### 56. Esperado no significa garantizado

Una tabla hash puede tener:

```text
costo esperado O(1)
```

y al mismo tiempo:

```text
peor caso O(n)
```

No existe contradicción.

Podemos comparar:

```text
AVL
    O(log n) en peor caso
    bajo su invariante de balance

tabla hash con encadenamiento
    O(1) esperado
    O(n) en peor caso
```

### 57. Hashing frente a arreglo sin ordenar

Un arreglo sin ordenar almacena claves compactamente, pero para `contains(x)` no ofrece información que permita descartar posiciones.

En el peor caso:

```text
O(n)
```

Una tabla hash agrega:

```text
función hash
+
buckets
```

para reducir la región de búsqueda.

### 58. Hashing frente a BST y AVL

BST y AVL mantienen orden global.

Esto permite operaciones como:

```text
recorrido ordenado
sucesor
predecesor
mínimo
máximo
consultas por rango
```

Una tabla hash no mantiene ese orden.

Puede favorecer búsqueda, inserción y eliminación de clave exacta, pero no produce un recorrido ordenado por recorrer sus buckets.

### 59. Hashing frente a BinaryHeap

El heap mantiene prioridad.

En un min-heap:

```text
peek() -> O(1)
```

La tabla hash no mantiene el mínimo.

En cambio, intenta localizar una clave exacta mediante:

```text
hash(x)
```

La comparación puede resumirse así:

```text
BinaryHeap
    favorece prioridad extrema

tabla hash
    favorece búsqueda exacta
```

### 60. Misma colección, diferentes representaciones

Supongamos que queremos almacenar:

```text
3, 7, 10, 12, 14, 17
```

Un arreglo, un BST, un heap y una tabla hash pueden representar exactamente ese conjunto lógico mediante estados internos muy diferentes.

Por tanto, cambian:

```text
los invariantes
las operaciones favorecidas
los costos
```

Esta es una de las ideas centrales de todo el curso.

### 61. El orden de los buckets no es el orden de las claves

Considera:

```text
0: [25]
1: [11]
2: [32]
3: [18]
4: [9]
```

Recorrer los buckets produce:

```text
25, 11, 32, 18, 9
```

Esto no representa orden creciente.

El índice del bucket proviene de la función hash, no de una relación menor que o mayor que entre claves.

### 62. Las colisiones cambian el costo local, no el significado del ADT

Dos tablas pueden almacenar el mismo conjunto y tener distribuciones muy diferentes.

```text
Tabla A
0: [10]
1: [11]
2: [12]
3: [13]
4: [14]
```

```text
Tabla B
0: [10, 11, 12, 13, 14]
1: [ ]
2: [ ]
3: [ ]
4: [ ]
```

El contenido lógico puede coincidir mientras el costo cambia.

### 63. El significado de una buena función hash

Para esta semana una buena función hash debe ayudar a conseguir:

```text
índices dentro del rango

mismo resultado para la misma clave
bajo la misma configuración

distribución razonable de claves

cálculo suficientemente barato
```

No necesitamos estudiar todavía todas las familias posibles de funciones hash.

### 64. Hashing no es criptografía

La palabra `hash` también aparece en criptografía.

Pero una tabla hash y una función hash criptográfica persiguen objetivos diferentes.

En esta semana buscamos:

```text
convertir una clave en un índice útil para localizarla eficientemente
```

No estudiamos propiedades criptográficas.

### 65. Por qué una colisión no implica igualdad

Supongamos:

```text
hash("lista") = 1
hash("otro")  = 1
```

No podemos concluir que las cadenas sean iguales.

El hash solamente selecciona el bucket.

Después debemos comprobar igualdad dentro de la colección.

```text
hash
    reduce candidatos

equals
    confirma la clave
```

### 66. La tabla hash como estructura de dos niveles

Podemos pensar en la representación como dos niveles.

```text
nivel 1
arreglo de buckets

nivel 2
colección dentro del bucket
```

La localización también tiene dos niveles.

```text
nivel 1
calcular índice

nivel 2
buscar dentro de una colección de tamaño k
```

Por eso aparece el costo:

```text
O(k)
```

### 67. Traza completa de inserciones

Usaremos:

```text
hash(x) = floorMod(x, 5)
```

Partimos de:

```text
0: [ ]
1: [ ]
2: [ ]
3: [ ]
4: [ ]

size = 0
```

Insertamos sucesivamente:

```text
12
7
14
17
```

El estado final es:

```text
0: [ ]
1: [ ]
2: [12, 7, 17]
3: [ ]
4: [14]

size = 4
```

Las colisiones en el bucket 2 son válidas.

### 68. Verificación sistemática del estado

Para verificar una tabla debemos comprobar:

```text
1. cada elemento está en el bucket correcto

2. size coincide con el total
```

Para el estado anterior:

```text
12 -> 2
7  -> 2
17 -> 2
14 -> 4
```

y:

```text
total = 4
size  = 4
```

La estructura es coherente.

### 69. Una operación puede retornar correctamente y dejar la tabla dañada

Supongamos que `add(22)` inserta correctamente 22 pero olvida:

```text
size++
```

Después:

```text
contains(22) -> true
```

La salida parece correcta, pero el invariante de tamaño quedó roto.

Este principio ya apareció en listas, BST, AVL y heap:

```text
salida correcta no implica representación correcta
```

### 70. Una tabla puede tener `size` correcto y ubicación incorrecta

También puede ocurrir lo opuesto.

El número total puede coincidir con `size`, pero una clave puede encontrarse en un bucket incorrecto.

Por tanto, los invariantes deben considerarse conjuntamente.

### 71. Relación con localización frente a modificación

En Semana 2 distinguimos:

```text
localizar
```

de:

```text
modificar
```

La misma separación aparece aquí.

```text
add(x)
    localizar: calcular bucket y buscar duplicado
    modificar: insertar y size++

remove(x)
    localizar: calcular bucket y buscar clave
    modificar: eliminar y size--
```

### 72. Relación con estado lógico y físico de Semana 1

En Semana 1 distinguimos:

```text
n
```

de:

```text
a.length
```

Ahora distinguimos:

```text
size
```

de:

```text
buckets.length
```

Pero `buckets.length` representa cantidad de regiones de dispersión, no cantidad máxima de elementos lógicos.

Un bucket puede contener varias claves.

### 73. Relación con listas enlazadas de Semana 2

El encadenamiento separado puede imaginarse mediante listas.

```text
bucket 2
7 -> 12 -> 17
```

La búsqueda dentro del bucket es secuencial.

La tabla hash no elimina completamente el costo lineal. Lo restringe a una colección local que esperamos mantener pequeña.

### 74. Relación con los ADT de Semana 3

Una tabla hash es una implementación.

El comportamiento que exponemos puede ser un conjunto:

```text
add
contains
remove
size
```

Podríamos implementar un ADT similar mediante BST, AVL o tabla hash.

La interfaz describe comportamiento. La representación explica cómo se consigue.

### 75. Relación con el orden de las Semanas 4 y 5

BST utiliza:

```text
orden
```

para localizar.

Hashing utiliza:

```text
transformación
```

para localizar.

```text
BST
clave -> comparaciones -> camino

tabla hash
clave -> hash -> bucket
```

### 76. Relación con AVL de Semana 6

AVL mostró que una estructura puede necesitar mantener información adicional para conservar eficiencia.

Hashing introduce otra preocupación:

```text
distribución entre buckets
```

Durante la Semana 8 no resolveremos todavía cómo adaptar el número de buckets cuando crece la tabla.

### 77. Relación con el heap de Semana 7

El heap utilizaba un arreglo para codificar una estructura jerárquica.

```text
índice
->
posición dentro de un árbol completo
```

La tabla hash utiliza un arreglo con otro significado.

```text
índice
->
bucket producido por el hash
```

Mismo recurso físico, distinta interpretación estructural.

### 78. Una tabla hash no es un arreglo ordenado por hash

Dos claves cercanas pueden ir a buckets distantes.

Dos claves muy distintas pueden colisionar.

Por tanto, el orden numérico del índice no representa orden semántico de las claves.

### 79. Comparación de estructuras estudiadas

Podemos resumir:

```text
Arreglo dinámico
    acceso por índice: O(1)
    búsqueda sin orden: O(n)

Lista enlazada
    modificación local: O(1) con referencia
    búsqueda: O(n)

BST
    búsqueda: O(h)
    mantiene orden

AVL
    búsqueda: O(log n) peor caso
    mantiene orden y balance

BinaryHeap
    peek mínimo: O(1)
    add/remove mínimo: O(log n)
    búsqueda arbitraria: O(n)

Tabla hash con encadenamiento
    búsqueda exacta: O(k)
    esperado cercano a O(1)
    peor caso O(n)
    no mantiene orden
```

La estructura apropiada depende del patrón de operaciones.

### 80. Qué significa `O(k)` en una llamada concreta

Supongamos:

```text
bucket 2 = [7, 12, 17, 22]
```

Entonces:

```text
k = 4
```

Buscar 22 puede examinar cuatro elementos.

Buscar 7 puede terminar antes.

`O(k)` describe el crecimiento respecto de la longitud de la colección local.

### 81. Por qué el peor caso es `O(n)`

Si todos los elementos terminan en un mismo bucket:

```text
k = n
```

Entonces una búsqueda fallida puede examinar todos los elementos.

Por eso `O(k)` se convierte en `O(n)` en el peor caso.

### 82. Por qué el costo esperado puede ser cercano a `O(1)`

Si la función distribuye razonablemente y hay suficientes buckets, esperamos que una clave no comparta su bucket con demasiados elementos.

Entonces:

```text
buena distribución
->
buckets pequeños
->
pocas comparaciones locales
->
costo esperado cercano a O(1)
```

### 83. Lo que no estudiaremos todavía

Durante esta semana no necesitamos desarrollar como núcleo:

```text
open addressing
linear probing
quadratic probing
double hashing
tombstones
Robin Hood hashing
cuckoo hashing
perfect hashing
hashing criptográfico
```

Tampoco necesitamos desarrollar todavía en profundidad:

```text
factor de carga
rehashing
políticas de crecimiento
```

El foco es:

```text
hashing
+
colisiones
+
encadenamiento separado
+
operaciones básicas
+
costo local
```

### 84. El problema que queda abierto

Considera una tabla con cinco buckets.

Al comienzo insertamos cinco elementos.

Después 50.

Después 500.

Si mantenemos siempre la misma cantidad de buckets, incluso una buena distribución hará que las colecciones locales tiendan a crecer.

Aparece una nueva pregunta:

```text
¿qué ocurre cuando size crece pero buckets.length permanece fijo?
```

Si cada bucket acumula más elementos, entonces:

```text
k aumenta
```

y las operaciones pueden requerir más trabajo.

### 85. Puente hacia la Semana 9

La Semana 9 responderá preguntas como:

```text
¿cómo relacionar cantidad de elementos con cantidad de buckets?

¿cuándo debe crecer la tabla?

¿por qué cambiar la cantidad de buckets obliga a recalcular ubicaciones?

¿cómo redistribuir los elementos sin romper el invariante?
```

Esto conducirá a:

```text
factor de carga + rehashing
```

Pero todavía no necesitamos resolverlo.

El cierre correcto de Semana 8 es reconocer el problema.

### 86. Síntesis conceptual de la Semana 8

Podemos resumir la estructura así:

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

Las colisiones producen:

```text
varias claves
->
mismo bucket
```

El encadenamiento separado responde:

```text
mantener todas esas claves en una colección local
```

La correctitud exige:

```text
cada x está en buckets[hash(x)]

size coincide con el total almacenado
```

Las operaciones siguen:

```text
contains
    calcular bucket
    buscar localmente

add
    calcular bucket
    evitar duplicado
    insertar
    size++

remove
    calcular bucket
    localizar
    eliminar
    size--
```

Y el costo depende de:

```text
k = longitud del bucket seleccionado
```

### 87. La cadena de diseño continúa

Desde la Semana 1 hemos utilizado la misma forma de analizar estructuras.

```text
ADT
    qué comportamiento se necesita

representación
    qué información se almacena

invariante
    qué debe permanecer verdadero

algoritmo
    cómo se aprovecha y modifica esa información

complejidad
    cuánto trabajo exige
```

Para Semana 8:

```text
ADT
    conjunto con búsqueda exacta

representación
    arreglo de buckets
    colección por bucket
    size

invariantes
    ubicación correcta
    tamaño correcto

algoritmos
    contains
    add
    remove

complejidad
    depende de la longitud del bucket
```

### 88. Idea final

La Semana 8 puede condensarse en una transformación conceptual:

```text
antes

buscar una clave =
recorrer
o
seguir comparaciones de orden

ahora

buscar una clave =
calcular una región candidata
+
buscar localmente
```

La eficiencia no surge solamente de una fórmula hash.

Surge de la relación completa:

```text
clave
+
hash
+
bucket
+
resolución de colisiones
+
invariantes
+
distribución
+
costo local
```

La tabla hash con encadenamiento es útil precisamente porque renuncia a mantener un orden global y utiliza esa libertad para intentar localizar claves exactas con muy poco trabajo esperado.
