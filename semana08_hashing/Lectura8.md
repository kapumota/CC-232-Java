### Lectura: hashing con encadenamiento y localización eficiente por clave

Esta lectura consolida y amplía las ideas trabajadas en la Semana 8 de CC232. Durante las siete semanas anteriores hemos cambiado de representación, de invariante o de ADT cada vez que cambió el tipo de operación que queríamos favorecer. En la Semana 1, un arreglo dinámico (arreglo + tamaño lógico + capacidad) permitió acceso directo por índice y obligó a distinguir estado lógico, almacenamiento físico, crecimiento y costo amortizado. En la Semana 2, los nodos enlazados hicieron barata la modificación local cuando ya conocíamos las referencias adecuadas, aunque localizar una posición dejó de ser directo. En la Semana 3, `Stack`, `Queue` y `Deque` plantearon la pregunta de qué operaciones permite el ADT sobre una secuencia.

En las Semanas 4 y 5, el invariante `subárbol izquierdo < nodo < subárbol derecho` permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones. En la Semana 6, AVL añadió altura y balance para impedir que el BST degenerara en una cadena. En la Semana 7, `Priority Queue` y `BinaryHeap` resolvieron otro problema, mantener disponible el mínimo, mediante dos decisiones, forma completa e invariante min-heap.

La Semana 8 cambia nuevamente la pregunta: ¿qué ocurre si no necesitamos mantener las claves ordenadas ni conservar el mínimo, sino localizar una clave exacta lo más directamente posible? Esta pregunta conduce al **hashing** y a una de sus implementaciones clásicas, una tabla hash con **encadenamiento separado**. Los términos centrales son los siguientes.

```text
clave
    dato que queremos localizar

hashCode
    entero asociado a la clave

función hash
    transforma el hashCode en un índice

bucket
    región candidata donde debe encontrarse la clave

colisión
    varias claves distintas comparten el mismo bucket

encadenamiento separado
    cada bucket mantiene una colección con esas claves
```

El objetivo no es memorizar una expresión de hashing ni afirmar que una tabla hash cuesta `O(1)`. El objetivo es continuar la misma cadena de siempre, qué **ADT** queremos ofrecer, qué **representación** almacena el estado, qué **invariantes** deben permanecer verdaderos, qué **algoritmos** localizan y modifican, y cuánto trabajo exigen, su **complejidad**. Al terminar deberías poder explicar por qué una tabla hash localiza una clave sin mantener orden global, por qué las colisiones son normales, cómo el encadenamiento las resuelve y por qué el costo depende de la longitud del bucket seleccionado.

Esta lectura explica los mecanismos y las trazas. Los métodos `hash`, `dimensionFor` y `remove` se completan en la Actividad 8. Resuelve primero los ejercicios y contrasta después tus resultados mediante Java.

### 1. El nuevo problema: localizar una clave exacta

Considera los identificadores `1042, 8517, 3301, 9240, 4175` almacenados en un arreglo sin ordenar, y la pregunta repetida "¿está almacenada la clave 9240?". El arreglo no ofrece información que permita descartar posiciones, así que en el peor caso hay que examinar todos los elementos y la búsqueda secuencial cuesta **O(n)**. La pregunta de esta semana es si podemos reducir la región que debemos examinar.

No necesitamos obtener las claves ordenadas, ni buscar sucesor o predecesor, ni mantener el mínimo. Solo queremos localizar una clave exacta.

### 2. Lo que BST, AVL y heap ya resolvían, y lo que no

En un BST decidimos por qué rama continuar comparando `x` con el nodo actual (menor a la izquierda, mayor a la derecha, igual encontrado), porque el árbol mantiene un orden global entre subárboles. El costo es **O(h)**, y en AVL la altura está controlada, así que `contains(x)` cuesta **O(log n)**. Es una solución excelente cuando necesitamos orden. Pero aparece una pregunta nueva: si solo necesito saber si una clave exacta está presente, ¿es obligatorio mantener un orden global? Hashing responde que no.

El `BinaryHeap` de la Semana 7 tampoco sirve para esto. Mantiene el mínimo en `a[0]`, pero no mantiene el orden de un BST, así que en el heap

```text
          2
        /   \
       5     4
      / \   / \
     9   7 8   6
```

observar `5` no permite descartar un subárbol al buscar la clave `7`, y la búsqueda de una clave arbitraria cuesta **O(n)**. La Semana 7 favorecía obtener el mínimo; la Semana 8 favorece localizar una clave exacta.

### 3. Direccionamiento directo y su límite

Supongamos que las únicas claves posibles son `0, 1, ..., 999`. Podríamos reservar `boolean[] present = new boolean[1000];`, insertar la clave 317 con `present[317] = true;` y consultarla con `present[317]`. La clave determina directamente la posición. Esta técnica se denomina **direccionamiento directo**.

Funciona bien si el **universo de claves**, es decir, el conjunto de todos los valores que podrían aparecer como claves, es pequeño y compacto. No debe confundirse con las claves actualmente almacenadas. Si el universo son todos los enteros de 32 bits, o todas las cadenas posibles, y almacenamos apenas `n = 100` claves, reservar una posición por cada clave posible desperdicia cantidades enormes de memoria. Tenemos entonces un universo muy grande, una cantidad real de elementos relativamente pequeña y muchas menos posiciones disponibles que claves posibles. Necesitamos comprimir el espacio de claves.

En esta semana la **clave** es el dato que usamos para identificar o localizar un elemento. Puede ser el propio valor (`"arreglo"`, `"heap"`, `1042`) o un campo de un objeto más complejo, como el `codigo` de un `Estudiante`. Trabajaremos con conjuntos de claves.

### 4. Tabla hash, bucket y arreglo de buckets

Una **tabla hash** utiliza una función hash para transformar una clave en un índice dentro de una tabla.

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

La tabla no mantiene las claves ordenadas. Su objetivo es reducir la región que examinamos. En lugar de preguntar "¿en cuál de los n elementos está x?", preguntamos primero "¿qué bucket corresponde a x?" y buscamos solo allí.

Un **bucket** es una posición lógica de la tabla asociada a un índice. La representación externa es un arreglo `buckets[0..m-1]`, donde `m` es la cantidad de buckets, y cada posición contiene una colección de claves. Con cinco buckets podríamos tener:

```text
buckets
  |
  +--> 0: [10]
  +--> 1: [ ]
  +--> 2: [12, 7, 17]
  +--> 3: [3]
  +--> 4: [14]
```

El bucket 2 contiene 12, 7 y 17. Eso no significa que sean iguales, significa que la función hash los dirigió al mismo índice. La representación reutiliza dos ideas anteriores. De la Semana 1, que un arreglo da acceso directo a una posición por índice. De la Semana 2, que una colección local puede mantenerse mediante elementos enlazados. Ahora combinamos ambas, un arreglo externo con una colección local por posición.

### 5. Función hash, hashCode e índice

Una **función hash** transforma información de la clave en un índice utilizable. Si existen `m` buckets, el índice debe satisfacer `0 <= índice < m`. En Java conviene distinguir tres niveles.

```text
clave
    objeto o valor lógico
        |
        v
hashCode()
    entero asociado a la clave
        |
        v
transformación
        |
        v
índice de bucket
    posición válida dentro de buckets
```

El `hashCode` puede ser negativo y estar muy lejos del rango `0..m-1`, por eso no debe confundirse con el índice final. Para una configuración fija de la tabla, la misma clave debe dirigirse siempre al mismo bucket (`hash(x) = i`), porque `add(x)` y un `contains(x)` posterior deben consultar la misma región. La función es determinista mientras la configuración no cambie.

#### El contrato entre equals y hashCode

Dos objetos iguales según `equals` deben producir el mismo `hashCode`. El recíproco no vale, dos objetos distintos pueden compartir `hashCode`. Esta regla no es un dato que haya que memorizar, es consecuencia del invariante de ubicación que veremos en la sección 8. Si dos objetos `a` y `b` cumplieran `a.equals(b)` pero tuvieran `hashCode` distintos, podrían caer en buckets distintos. Insertaríamos `a` en un bucket, y `contains(b)` buscaría en otro, retornaría `false` y la tabla contradiría a `equals`. Por eso en Java, cuando una clase sobrescribe `equals`, debe sobrescribir también `hashCode`.

#### Estabilidad de las claves

El mismo razonamiento tiene otra consecuencia. Si un objeto ya almacenado cambia un campo que interviene en `hashCode`, su hash puede cambiar y el objeto permanecer en el bucket calculado anteriormente. Si el nuevo índice es distinto, se rompe el invariante de ubicación y `contains` puede dejar de encontrarlo. Una modificación de los campos utilizados por `equals` también puede alterar la identidad lógica de la clave y vulnerar la unicidad del conjunto, incluso si el índice no cambia. Por eso las claves deben ser inmutables o, al menos, sus campos relevantes para `equals` y `hashCode` deben permanecer estables mientras estén almacenadas.

### 6. Una función didáctica, colisiones y por qué son normales

Para comprender la estructura usaremos enteros y `m = 5` con `hash(x) = floorMod(x, 5)`. Como `Integer.hashCode()` es el propio valor, esta función es exactamente la reducción de `hashCode` a un índice. Entonces:

```text
hash(12) = 2
hash(7)  = 2
hash(17) = 2
hash(3)  = 3
hash(14) = 4
```

Una **colisión** ocurre cuando dos claves distintas producen el mismo índice, `x != y` pero `hash(x) = hash(y)`. Aquí `12 != 7` pero ambas van al bucket 2. Una colisión no significa que la tabla esté dañada ni que la función sea incorrecta. Con muchas claves posibles y solo `m` buckets, claves distintas deben compartir índices. La pregunta real es cómo representar correctamente varias claves que comparten el mismo índice. Si cada bucket admitiera una sola clave, insertar 7 no podría sobrescribir 12 ni podríamos afirmar que 7 ya existe. Necesitamos una política explícita de resolución de colisiones.

### 7. Encadenamiento separado

En el **encadenamiento separado**, cada bucket mantiene una colección con todas las claves que producen ese índice.

```text
0: [10]
1: [ ]
2: [12, 7, 17]
3: [3]
4: [14]
```

La colisión no destruye datos, solo hace que la búsqueda local pueda requerir más trabajo. Se llama separado porque cada posición mantiene una colección independiente.

Conviene distinguir la referencia conceptual de la implementación didáctica. La referencia conceptual es la tabla hash con encadenamiento separado, donde cada bucket es una cadena de elementos, y en la presentación clásica esa cadena es una lista enlazada. La implementación didáctica de esta semana es `ChainedHashSet<T>`, que representa cada bucket con un `ArrayList`. El encadenamiento separado no obliga a que cada bucket sea una lista enlazada, lo que lo define es que todas las claves que colisionan se conservan juntas en una colección propia de ese bucket.

### 8. Estado e invariantes de la representación

La tabla necesita dos campos, `buckets` (arreglo de colecciones) y `size` (cantidad total de elementos lógicos). Son cantidades distintas. En

```text
0: [10]
1: [ ]
2: [12, 7, 17]
3: [3]
4: [14]
```

tenemos `buckets.length = 5` pero `size = 6`.

El **invariante de ubicación** dice que todo elemento `x` almacenado pertenece a `buckets[hash(x)]`. El **invariante de tamaño** dice que `size` es la cantidad total de elementos almacenados en todos los buckets, aquí `1 + 0 + 3 + 1 + 1 = 6`. Como implementamos un conjunto (`Set`), también exigimos el **invariante de unicidad**: no puede haber dos elementos almacenados que sean iguales según `equals`, aunque estén en el mismo bucket. Una operación correcta debe preservar los tres invariantes simultáneamente.

Un elemento puede existir físicamente y estar mal ubicado. Supongamos

```text
0: [10]
1: [ ]
2: [12, 7]
3: [3, 17]
4: [14]
```

con `hash(17) = 2`. La clave 17 aparece en la tabla, pero en el bucket equivocado. Un `contains(17)` correcto buscará solo en `buckets[2]` y retornará `false`. La representación está dañada aunque el valor siga visible en otro lugar.

Los invariantes pueden romperse de distintas maneras. Si `add(22)` inserta en el bucket correcto pero olvida `size++`, la ubicación es correcta y el tamaño no. Si incrementa `size` pero coloca 22 en otro bucket, el tamaño coincide y la ubicación no. Si inserta otra vez `12` y aumenta `size`, ubicación y tamaño podrían seguir siendo correctos, pero se viola la unicidad. Además, una salida visible puede ser correcta con la estructura dañada: tras olvidar `size++`, `contains(22)` sigue retornando `true`. 

Este principio ya apareció en listas, BST, AVL y heap: una salida correcta no implica una representación correcta.

### 9. El ADT y la operación contains

Durante esta semana nos interesa un conjunto sin duplicados, con las operaciones `int size()`, `boolean contains(Object value)`, `boolean add(T value)` y `boolean remove(T value)`. El ADT describe qué puede hacer el usuario, la tabla hash describe cómo se implementa.

`contains(x)` se deriva directamente del invariante de ubicación. Si `x` está almacenado, debe estar en `buckets[hash(x)]`, así que calculamos `hash(x)`, seleccionamos ese bucket y buscamos `x` solo allí, sin recorrer los demás.

Con `0: [10]`, `2: [12, 7, 17]`, `3: [3]`, `4: [14]`, la llamada `contains(17)` calcula `hash(17) = 2`, busca en `[12, 7, 17]` y retorna `true`. La llamada `contains(22)` también calcula `hash(22) = 2`, no encuentra 22 en esa colección y retorna `false`. Que `hash(22) = 2` no demuestra que 22 esté almacenado, solo dice que si estuviera, estaría en el bucket 2. El hash localiza una región candidata y la búsqueda local confirma la existencia. Por eso el hash reduce candidatos y `equals` confirma la clave.

### 10. La operación add

Para agregar sin duplicados, localizamos antes de modificar, igual que en BST, cambiando solo el mecanismo de localización. Calculamos `hash(x)`, comprobamos si `x` ya está en ese bucket, y si existe retornamos `false`. Si no existe, insertamos, incrementamos `size` y retornamos `true`.

Partimos de

```text
0: [10]
1: [ ]
2: [12, 7]
3: [3]
4: [14]

size = 5
```

y ejecutamos `add(17)`. Como `hash(17) = 2` y 17 no está en `[12, 7]`, insertamos y obtenemos `2: [12, 7, 17]` con `size = 6`. Si ahora ejecutamos `add(12)`, la búsqueda local encuentra 12, retorna `false` y no cambian ni `buckets` ni `size`.

### 11. La operación remove

La eliminación sigue la misma estrategia. Calculamos `hash(x)`, buscamos `x` en ese bucket, y si no existe retornamos `false`. Si existe, eliminamos exactamente esa clave, decrementamos `size` y retornamos `true`. No se busca por toda la tabla, porque el invariante garantiza que `x` solo puede estar en `buckets[hash(x)]`.

Con `2: [12, 7, 17]` y `size = 6`, la llamada `remove(12)` deja `2: [7, 17]` con `size = 5`. La llamada `remove(22)` calcula `hash(22) = 2`, no encuentra 22 en `[7, 17]`, retorna `false` y `size` no cambia.

Aquí aparece una decisión de implementación. Queremos recorrer la colección del bucket y eliminar el elemento encontrado. Si lo hacemos con un recorrido `for-each` y llamamos a la eliminación de la lista dentro del ciclo, modificamos la colección mientras la estamos recorriendo.

```java
// Patrón incorrecto.
for (T x : bucket) {
    if (x.equals(value)) {
        bucket.remove(x);   // modifica la lista durante el recorrido
    }
}
```

Modificar directamente una lista mientras se recorre con `for-each` puede provocar `ConcurrentModificationException`, pero no está garantizado que la excepción aparezca en todos los casos. En cualquier caso, ese patrón no es una forma segura de eliminar durante el recorrido. La técnica requerida en esta actividad consiste en utilizar un `Iterator` y eliminar mediante `it.remove()`, que borra el último elemento devuelto por `it.next()` y mantiene consistente la iteración. Además, `size--` debe ejecutarse exactamente una vez y solo cuando se eliminó algo. La implementación de `remove` es parte de la actividad.

Conviene razonar también sobre los casos frontera de un bucket. Un bucket vacío (`[ ]`) termina la búsqueda de inmediato. Un bucket con un elemento puede resolverse con una comparación. Un bucket con varias colisiones (`[12, 7, 17, 22]`) puede exigir examinar varios elementos. Buscar una clave ausente en un bucket no vacío exige comprobar que no aparece en toda esa colección. Agregar un duplicado no modifica el conjunto.

### 12. La clase ChainedHashSet

Reunimos la representación y las operaciones en una clase. El siguiente fragmento es una clase anidada, por eso lleva `static`, y debe escribirse dentro de una clase contenedora, como ocurre en el programa de la semana. Aislado en un archivo no compila, porque `static` no es válido en una clase de nivel superior.

```java
// Fragmento de clase anidada. Requiere imports de ArrayList y List.
static class ChainedHashSet<T> {
    private final List<T>[] buckets;
    private int size;

    @SuppressWarnings({"unchecked", "rawtypes"})
    ChainedHashSet(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "capacidad debe ser positiva");
        }

        buckets = (List<T>[]) new List[capacity];

        for (int i = 0; i < capacity; i++) {
            buckets[i] = new ArrayList<>();
        }
    }

    int size() {
        return size;
    }

    private int indexFor(Object value) {
        return Math.floorMod(value.hashCode(), buckets.length);
    }

    boolean contains(Object value) {
        return buckets[indexFor(value)].contains(value);
    }

    boolean add(T value) {
        List<T> bucket = buckets[indexFor(value)];

        if (bucket.contains(value)) {
            return false;
        }

        bucket.add(value);
        size++;
        return true;
    }

    // TODO (actividad): eliminar value recorriendo el bucket
    // con un Iterator. Postcondición: size baja exactamente
    // una vez si y solo si se eliminó algo.
    boolean remove(T value) {
        throw new UnsupportedOperationException("TODO");
    }
}
```

Algunas decisiones merecen atención. Java no permite crear directamente un arreglo de tipo genérico, por eso se crea `new List[capacity]` y se convierte, con las advertencias *unchecked* y *rawtypes* suprimidas de forma local. `Math.floorMod` reduce el `hashCode` al rango válido, a diferencia de `%`, que con un `hashCode` negativo puede producir un índice negativo (por ejemplo `-7 % 5` es `-2`), y un índice debe cumplir `0 <= índice < buckets.length`. La clave `null` no está soportada porque `null.hashCode()` lanza excepción. Cada método calcula una única vez el bucket y trabaja solo sobre él.

### 13. Hashing multiplicativo y la dimensión de la tabla

El primer archivo de la semana presenta otra forma de reducir un `hashCode` a un índice. La diferencia con `floorMod` es que no admite cualquier cantidad de buckets, sino exactamente `2^d`. Se definen las constantes y dos métodos que debes completar.

```java
static final int W = 32;
static final int Z = 0x9E3779B9;

static int hash(int hashCode, int d) {
    // TODO: multiplicar por Z (módulo 2^W) y conservar
    // los d bits más altos del resultado.
    throw new UnsupportedOperationException("TODO");
}

static int dimensionFor(int n) {
    // TODO: menor d >= 1 tal que 2^d > n.
    throw new UnsupportedOperationException("TODO");
}
```

La idea de `hash` es la siguiente. La multiplicación `Z * hashCode` se realiza módulo `2^32`, que es el desbordamiento natural de `int`. De la palabra resultante se conservan los `d` bits más altos, y para eso hace falta un desplazamiento lógico, no aritmético, porque el producto puede ser negativo.

```text
hashCode
    |
    v
multiplicación por Z (mod 2^32)
    |
    v
palabra de 32 bits
    |
    v
se conservan los d bits más altos
    |
    v
índice en 0 .. 2^d - 1
```

El resultado debe quedar en `0 .. 2^d - 1`, y para eso se requiere `1 <= d <= 31`. Puedes comprobar tu implementación con estos valores.

| Llamada | Resultado esperado |
|---|---|
| `hash(0, 3)` | 0 |
| `hash(1, 3)` | 4 |
| `hash(2, 3)` | 1 |
| `hash(3, 3)` | 6 |
| `hash(1, 4)` | 9 |
| `hash(2, 4)` | 3 |
| `hash(3, 4)` | 13 |

Las claves consecutivas 1, 2 y 3 quedan dispersas, porque los productos `Z`, `2Z` y `3Z` valen `0x9E3779B9`, `0x3C6EF372` y `0xDAA66D2B`.

`dimensionFor(n)` elige `d` como el menor `d >= 1` tal que `2^d > n`, y así la cantidad de buckets `2^d` es mayor que `n`. Por ejemplo, para `n = 8`, `2^3 = 8` no supera a 8 pero `2^4 = 16` sí, así que el resultado es 4.

| Llamada | Resultado esperado |
|---|---|
| `dimensionFor(0)` | 1 |
| `dimensionFor(1)` | 1 |
| `dimensionFor(2)` | 2 |
| `dimensionFor(7)` | 3 |
| `dimensionFor(8)` | 4 |
| `dimensionFor(16)` | 5 |

Las dos transformaciones, `floorMod` y la multiplicativa, resuelven el mismo problema, pasar de `hashCode` a índice válido. La clase `ChainedHashSet` de la sección 12 usa `floorMod` y acepta cualquier capacidad. La variante multiplicativa es una alternativa cuando el número de buckets es una potencia de dos. En ambos casos el análisis de las siguientes secciones es el mismo.

### 14. El costo depende de la longitud del bucket

Sea `k` la cantidad de elementos del bucket seleccionado. Calcular el índice y acceder al bucket cuesta tiempo constante, y recorrer el bucket cuesta tiempo proporcional a `k`. Por tanto `contains`, `add` y `remove` cuestan

```text
T(k) = O(1 + k)
```

El término constante importa cuando `k = 0`. Consultar un bucket vacío también realiza trabajo, calcular el índice y acceder al arreglo. Es un análisis más preciso que afirmar "tabla hash O(1)". `size()` cuesta **O(1)** porque solo retorna un campo.

Si el bucket está vacío la búsqueda termina de inmediato, y con `k = 1` el trabajo local es mínimo. El peor caso ocurre cuando todas las claves terminan en el mismo bucket (`k = n`), y entonces el costo es `O(1 + n) = O(n)`. Eso no requiere mala suerte, puede ocurrir con una función sencilla y claves poco afortunadas. Con `m = 5` y `hash(x) = floorMod(x, 5)`, las claves `5, 10, 15, 20, 25` van todas al bucket 0.

```text
0: [5, 10, 15, 20, 25]
1: [ ]
2: [ ]
3: [ ]
4: [ ]
```

Un `contains(30)` calcula `hash(30) = 0`, examina los cinco elementos y retorna `false`. Aquí `k = n`. La tabla sigue siendo correcta, el problema es de eficiencia.

#### Costo esperado y una hipótesis explícita

Para hablar de costo esperado necesitamos una hipótesis. Supongamos **hashing uniforme simple**, es decir, que cada clave tiene la misma probabilidad de caer en cualquiera de los `m` buckets, independientemente de las demás. Con `n` elementos y `m` buckets, la longitud esperada de un bucket es `n/m`, y entonces

```text
E[T] = O(1 + n/m)
```

Esta fórmula dice más que "buena distribución implica O(1)". Una buena distribución no basta por sí sola, hace falta que `n/m` permanezca acotado. Si `m` es fijo y `n` crece, el costo esperado crece aunque la distribución sea perfecta. Esperado no significa garantizado, y la hipótesis de uniformidad es una hipótesis, no una propiedad que la tabla garantice. Una tabla puede tener costo esperado constante bajo esa hipótesis y peor caso `O(n)` a la vez, sin contradicción. Comparemos con AVL, que ofrece **O(log n)** en peor caso gracias a su invariante de balance.

| Operación | Costo según el bucket | Esperado (hashing uniforme simple) | Peor caso |
|---|---|---|---|
| `size()` | O(1) | O(1) | O(1) |
| `contains(x)` | O(1 + k) | O(1 + n/m) | O(n) |
| `add(x)` | O(1 + k) | O(1 + n/m) | O(n) |
| `remove(x)` | O(1 + k) | O(1 + n/m) | O(n) |

### 15. Traza completa con enteros y con cadenas

Con `hash(x) = floorMod(x, 5)` partimos de cinco buckets vacíos con `size = 0` e insertamos sucesivamente 12, 7, 14 y 17. El estado final es:

```text
0: [ ]
1: [ ]
2: [12, 7, 17]
3: [ ]
4: [14]

size = 4
```

Para verificar la tabla comprobamos los tres invariantes. **Ubicación:** `12 -> 2`, `7 -> 2`, `17 -> 2`, `14 -> 4`, todos coinciden con su bucket. **Tamaño:** el total es 4 y `size = 4`. **Unicidad:** las cuatro claves son diferentes entre sí según `equals`.

Con cadenas, `String.hashCode()` calcula `s[0]*31^(n-1) + ... + s[n-1]`. Para cadenas de un carácter, el resultado es el código del carácter, y para `"ab"` y `"ba"` obtenemos:

```text
"a"  -> 97                  -> floorMod(97, 5)   = 2
"b"  -> 98                  -> floorMod(98, 5)   = 3
"ab" -> 97*31 + 98  = 3105  -> floorMod(3105, 5) = 0
"ba" -> 98*31 + 97  = 3135  -> floorMod(3135, 5) = 0
```

Insertando `"a"`, `"b"`, `"ab"` y `"ba"` en ese orden, las dos últimas colisionan en el bucket 0.

```text
0: ["ab", "ba"]
1: [ ]
2: ["a"]
3: ["b"]
4: [ ]

size = 4
```

Las cadenas `"ab"` y `"ba"` no son iguales, pero comparten bucket. El hash solo selecciona el bucket y `equals` decide dentro de la colección.

### 16. Hashing frente a las demás estructuras

Un arreglo, un BST, un heap y una tabla hash pueden representar exactamente el mismo conjunto lógico con estados internos muy distintos, y por eso cambian los invariantes, las operaciones favorecidas y los costos. Esta es una de las ideas centrales de todo el curso.

| Estructura | Búsqueda de clave exacta | Mantiene orden | Operación favorecida |
|---|---|---|---|
| Arreglo dinámico | O(n) sin orden | no | acceso por índice O(1) |
| Lista enlazada | O(n) | no | modificación local O(1) con referencia |
| BST | O(h) | sí | búsqueda y recorrido ordenado |
| AVL | O(log n) en peor caso | sí | lo anterior con altura garantizada |
| BinaryHeap | O(n) | no, solo prioridad | mínimo en O(1), add/remove en O(log n) |
| Tabla hash con encadenamiento | O(1 + k), esperado O(1 + n/m), peor caso O(n) | no | búsqueda exacta |

BST y AVL permiten recorrido ordenado, sucesor, predecesor, mínimo, máximo y consultas por rango. La tabla hash renuncia a todo eso y usa esa libertad para localizar claves exactas con poco trabajo esperado. La estructura apropiada depende del patrón de operaciones.

### 17. El orden de los buckets no es el orden de las claves

Considera

```text
0: [25]
1: [11]
2: [32]
3: [18]
4: [9]
```

Recorrer los buckets produce `25, 11, 32, 18, 9`, que no es orden creciente. El índice del bucket proviene de la función hash, no de una relación "menor que" o "mayor que" entre claves. Dos claves cercanas pueden ir a buckets distantes, y dos claves muy distintas pueden colisionar. Por eso la tabla no es un arreglo ordenado por hash en ningún sentido semántico.

Las colisiones cambian el costo local, no el significado del ADT. Dos tablas con la misma función y cinco elementos pueden diferir completamente en eficiencia según las claves.

```text
Tabla A   claves 10, 11, 12, 13, 14
0: [10]
1: [11]
2: [12]
3: [13]
4: [14]

Tabla B   claves 5, 10, 15, 20, 25
0: [5, 10, 15, 20, 25]
1: [ ]
2: [ ]
3: [ ]
4: [ ]
```

En la Tabla A cada bucket tiene longitud 1. En la Tabla B, `k = 5 = n`. Ambas son correctas. Una buena función hash debe ayudar a conseguir índices dentro del rango, el mismo resultado para la misma clave, una distribución razonable de claves y un cálculo suficientemente barato.

La palabra *hash* también aparece en criptografía, pero una tabla hash y una función hash criptográfica persiguen objetivos distintos. Aquí buscamos convertir una clave en un índice útil para localizarla eficientemente, y no estudiamos propiedades criptográficas.

### 18. Conexión con las semanas anteriores

De la Semana 1 reaparece la distinción entre estado lógico y almacenamiento físico. Antes distinguíamos `n` de `a.length`, ahora distinguimos `size` de `buckets.length`. Pero `buckets.length` mide regiones de dispersión, no cantidad máxima de elementos, y un bucket puede contener varias claves.

De la Semana 2 reaparece la lista enlazada como colección local (`7 -> 12 -> 17`) y la separación entre localizar y modificar. En `add(x)` localizar es calcular el bucket y buscar duplicado, y modificar es insertar y `size++`. En `remove(x)` localizar es calcular el bucket y buscar la clave, y modificar es eliminar y `size--`. La tabla no elimina el costo lineal, lo restringe a una colección local que esperamos mantener pequeña.

De la Semana 3 reaparece la separación entre ADT e implementación. El comportamiento expuesto es un conjunto (`add`, `contains`, `remove`, `size`), que podríamos implementar con BST, AVL o tabla hash.

De las Semanas 4 y 6 reaparece el problema de localizar una clave, resuelto de otro modo. BST usa comparaciones y sigue un camino, la tabla hash usa una transformación y selecciona un bucket. AVL mostró que una estructura puede mantener información adicional para conservar eficiencia, y hashing introduce otra preocupación, la distribución entre buckets.

De la Semana 7 reaparece el arreglo como recurso físico con otra interpretación. En el heap, el índice codificaba una posición dentro de un árbol completo. En la tabla hash, el índice es el bucket producido por el hash.

### 19. Lo que no estudiamos todavía y el problema abierto

En esta semana no desarrollamos como núcleo `open addressing`, `linear probing`, `quadratic probing`, `double hashing`, `tombstones`, `Robin Hood hashing`, `cuckoo hashing`, `perfect hashing` ni hashing criptográfico. Tampoco desarrollamos en profundidad factor de carga, rehashing ni políticas de crecimiento. El foco es hashing, colisiones, encadenamiento separado, operaciones básicas y costo local.

Pero queda un problema abierto, y la sección 14 ya lo anticipa. Considera una tabla con cinco buckets a la que insertamos cinco elementos, luego 50, luego 500. Si el número de buckets permanece fijo, aunque la distribución sea buena, las colecciones locales tienden a crecer, porque la longitud promedio de un bucket es `size / buckets.length` y el costo esperado es `O(1 + n/m)`. La pregunta es qué ocurre cuando `size` crece pero `buckets.length` permanece fijo.

La Semana 9 responderá cómo relacionar la cantidad de elementos con la de buckets, cuándo debe crecer la tabla, por qué cambiar la cantidad de buckets obliga a recalcular ubicaciones (la función depende de `buckets.length`, y por eso el invariante de ubicación exige recolocar los elementos) y cómo redistribuirlos sin romper el invariante. Eso conduce a **factor de carga** y **rehashing**. 

### 20. Síntesis

Una tabla hash con encadenamiento convierte una clave en un índice y trabaja solo sobre el bucket correspondiente.

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

Las colisiones, varias claves en el mismo bucket, son normales, y el encadenamiento separado las resuelve manteniendo una colección por bucket. La correctitud del `ChainedHashSet` exige tres invariantes: cada `x` está en `buckets[hash(x)]`, `size` coincide con el total almacenado y no hay duplicados lógicos según `equals`. De ahí se derivan las operaciones.

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
    recorrer con Iterator
    eliminar con it.remove()
    size-- solo si se eliminó
```

El costo es **O(1 + k)**, con `k` la longitud del bucket seleccionado. Bajo hashing uniforme simple el costo esperado es **O(1 + n/m)**, y en el peor caso, cuando todas las claves caen en un mismo bucket, es **O(n)**. El contrato `equals`/`hashCode` y la estabilidad de las claves no son reglas externas, son lo que hace posible el invariante de ubicación.

La cadena de diseño de todo el curso se mantiene.

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
    unicidad según equals

algoritmos
    contains, add, remove

complejidad
    O(1 + k), esperado O(1 + n/m)
```

La idea final es una transformación conceptual. Antes, buscar una clave era recorrer o seguir comparaciones de orden. Ahora es calcular una región candidata y buscar localmente. La eficiencia no surge solo de una fórmula hash, surge de la relación completa entre clave, hash, bucket, resolución de colisiones, invariantes, distribución y costo local. 

La tabla hash con encadenamiento es útil precisamente porque renuncia a mantener un orden global y usa esa libertad para localizar claves exactas con muy poco trabajo esperado.
