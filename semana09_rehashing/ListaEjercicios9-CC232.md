### Ejercicios de la Semana 9

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La sección de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 9.

La mayoría de los problemas exige:

```text
razonar antes de ejecutar
reconstruir estados a partir de información parcial
detectar código que compila pero es incorrecto
formular y verificar invariantes
diseñar contraejemplos
separar contenido lógico de representación física
distinguir inserción lógica de reinserción física
combinar estructuras de semanas anteriores
analizar costos concretos y asintóticos
distinguir costo esperado de costo amortizado
diseñar pruebas que revelen errores estructurales
comparar políticas de crecimiento
```

Los temas centrales de la Semana 9 utilizados son:

```text
problema heredado de Semana 8
n creciente con cantidad de buckets fija
capacidad
t.length = 2^d

factor de carga
alpha = n/m
longitud promedio de bucket
distribución frente a carga

política de crecimiento
estado actual frente a estado futuro
shouldGrow
umbral alpha > 1
capacityFor
potencias de dos

resize
cambio de capacidad
cambio de d
por qué copiar buckets no basta
rehashing
tabla antigua y tabla nueva
reinserción física
preservación de n

invariantes
t.length = 2^d
x pertenece a t[hash(x)]
n coincide con el total de elementos
unicidad

find
add
remove

O(k)
peor caso O(n)
resize O(n)
costo esperado cercano a O(1)
crecimiento geométrico
análisis amortizado
esperado frente a amortizado
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
arreglos dinámicos
tamaño lógico y capacidad
resize
crecimiento geométrico
costo amortizado

listas enlazadas
referencias
Iterator
modificación local

Stack
Queue
Deque

BST
invariante de orden
find
add
remove
altura h

AVL
balance
O(log n)

Priority Queue
BinaryHeap
mínimo
búsqueda arbitraria O(n)

hashing con encadenamiento
hashCode
función hash
buckets
colisiones
O(k)

trazas
invariantes
contraejemplos
complejidad
```

No se requiere utilizar `HashMap`, `HashSet`, `Hashtable`, `TreeMap`, `TreeSet` ni `PriorityQueue` como solución directa.

Cuando un ejercicio trabaje con la representación de la Semana 9, puede utilizarse el mismo enfoque de los archivos de clase con `List<T>[]`, `ArrayList` e `Iterator`.

No se requiere estudiar `open addressing`, `linear probing`, `quadratic probing`, `double hashing`, tombstones, Robin Hood hashing, cuckoo hashing, perfect hashing ni otras variantes de hashing.

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir una política adicional o una operación auxiliar, pero deben resolverse a partir de las ideas disponibles hasta la Semana 9.

### A. Consolidación esencial

#### Ejercicio 1. Dos tablas con el mismo contenido lógico y distinta historia física

Una tabla hash con encadenamiento contiene exactamente las claves:

```text
11, 18, 25, 32, 39, 46
```

Dos ejecuciones distintas terminan con:

```text
Tabla A
d = 3
t.length = 8
n = 6
```

y:

```text
Tabla B
d = 4
t.length = 16
n = 6
```

No se conoce la historia de inserciones.

Responde:

1. calcula el factor de carga de cada tabla,
2. indica cuál tiene mayor longitud promedio de bucket,
3. explica por qué ambas pueden representar exactamente el mismo conjunto,
4. explica qué parte del estado es lógica y qué parte es física,
5. indica si la Tabla B necesariamente tiene mejor distribución,
6. construye conceptualmente una función hash que produzca una mala distribución incluso en la Tabla B,
7. explica qué información adicional necesitas para estimar el costo concreto de `find(39)`,
8. indica qué invariantes pueden verificarse sin conocer la historia de inserciones,
9. explica por qué una capacidad mayor no garantiza por sí sola una búsqueda más rápida,
10. relaciona el problema con dos arreglos dinámicos que contienen los mismos elementos pero tienen capacidades distintas.

#### Ejercicio 2. Reconstruir la política a partir de una secuencia de capacidades

Una estructura empieza con:

```text
n = 0
capacity = 2
```

Después de cada inserción exitosa se registra únicamente:

```text
n          1  2  3  4  5  6  7  8  9
capacity   2  2  4  4  8  8  8  8 16
```

Sin observar el código:

1. identifica en qué inserciones ocurrió crecimiento,
2. determina qué estado existía inmediatamente antes de cada crecimiento,
3. verifica si los datos son compatibles con la política `n + 1 > capacity`,
4. determina el valor de `alpha` después de cada inserción,
5. explica por qué la capacidad no crece en cada operación,
6. determina cuántos elementos tuvieron que reinsertarse en total,
7. separa el costo de las inserciones ordinarias del costo de reconstrucción,
8. explica por qué esta historia sugiere crecimiento geométrico,
9. indica qué no puedes concluir acerca de la distribución de las claves,
10. explica por qué esta evidencia sirve para hablar de costo amortizado pero no basta para demostrar costo esperado cercano a `O(1)`.

### B. Retos integradores

#### Reto 1. Se calcula el bucket correcto demasiado pronto

Un estudiante implementa:

```java
boolean add(T x) {
    if (find(x) != null) {
        return false;
    }

    int i = hash(x);

    if (n + 1 > t.length) {
        resize();
    }

    t[i].add(x);
    n++;
    return true;
}
```

El código compila.

Sin ejecutarlo:

1. explica qué representa `i`,
2. identifica qué parte del estado puede cambiar durante `resize`,
3. explica por qué `i` puede dejar de ser válido aunque siga dentro del rango del arreglo nuevo,
4. construye un ejemplo donde `hash(x)` sea distinto antes y después de crecer,
5. muestra cómo la clave queda físicamente almacenada pero no puede ser encontrada posteriormente,
6. indica qué invariante se rompe,
7. corrige únicamente el orden de las acciones necesario,
8. explica por qué calcular `hash(x)` después de `resize` forma parte de la correctitud,
9. determina si la versión incorrecta y la correcta tienen el mismo orden asintótico,
10. explica por qué misma complejidad no implica misma corrección.

#### Reto 2. Un resize preserva n pero pierde elementos

Se propone:

```java
private void resize() {
    List<T>[] oldTable = t;

    d++;
    t = allocateTable(1 << d);

    for (int i = 0; i < oldTable.length - 1; i++) {
        for (T x : oldTable[i]) {
            t[hash(x)].add(x);
        }
    }
}
```

El método no modifica `n`.

Responde:

1. identifica el defecto,
2. construye el estado más pequeño donde el error sea observable,
3. muestra que `n` puede conservar su valor y aun así quedar incorrecto,
4. identifica qué invariante falla,
5. explica qué puede retornar `size()` después del error,
6. explica qué puede retornar `find(x)` para una clave perdida,
7. corrige el recorrido,
8. diseña una comprobación posterior que detecte la inconsistencia,
9. determina la complejidad de esa comprobación,
10. explica por qué preservar el contador es necesario pero no suficiente.

#### Reto 3. Un resize duplica físicamente claves sin cambiar n

Se propone:

```java
private void resize() {
    List<T>[] oldTable = t;

    d++;
    List<T>[] newTable = allocateTable(1 << d);

    for (int i = 0; i < oldTable.length; i++) {
        newTable[i].addAll(oldTable[i]);

        for (T x : oldTable[i]) {
            newTable[hash(x)].add(x);
        }
    }

    t = newTable;
}
```

No se modifica `n`.

Analiza:

1. por qué algunas claves pueden aparecer dos veces,
2. construye una clave que conserve el mismo índice y otra que cambie de índice,
3. dibuja el estado físico final,
4. determina qué ocurre con la unicidad,
5. determina qué ocurre con `n = suma de tamaños de buckets`,
6. explica por qué `find` puede seguir pareciendo correcto,
7. explica por qué una prueba que solo use `find` podría no detectar el defecto,
8. propone una estrategia de validación más fuerte,
9. corrige conceptualmente el algoritmo,
10. justifica por qué rehashing debe reconstruir y no copiar más reinsertar.

#### Reto 4. Una política de crecimiento más agresiva cambia el trabajo total

Se comparan dos políticas.

Política A:

```text
crecer si
(n + 1)/capacity > 1.0
```

Política B:

```text
crecer si
(n + 1)/capacity >= 0.75
```

Ambas usan capacidades:

```text
2, 4, 8, 16, 32, ...
```

y rehashing completo.

Se insertan 20 claves distintas.

Sin ejecutar código:

1. determina en qué inserciones crece la Política A,
2. determina en qué inserciones crece la Política B,
3. registra la capacidad después de cada crecimiento,
4. calcula cuántos elementos se reinsertan en total con cada política,
5. compara la capacidad final,
6. compara el espacio libre final,
7. explica qué política intenta mantener menor carga,
8. explica por qué menor carga no garantiza mejor distribución,
9. compara el costo total de reconstrucción,
10. explica el compromiso entre memoria y longitud esperada de bucket.

No necesitas demostrar cuál política es universalmente mejor.

#### Reto 5. Una mala función hash derrota una buena política de crecimiento

Una tabla utiliza una política que garantiza:

```text
alpha <= 1
```

después de cada inserción.

Sin embargo, para todas las claves utilizadas ocurre:

```text
hash(x) = 0
```

Se insertan 64 claves distintas.

Responde:

1. dibuja conceptualmente la distribución final,
2. determina la longitud del bucket 0,
3. determina el factor de carga si la capacidad final es 64,
4. determina el costo de una búsqueda exitosa cerca del final del bucket,
5. determina el costo de una búsqueda fallida que cae en ese bucket,
6. explica por qué controlar `alpha` no evita el peor caso,
7. explica qué parte del análisis esperado dejó de cumplirse,
8. determina si el análisis amortizado del crecimiento puede seguir siendo válido,
9. explica por qué esperado y amortizado deben analizarse por separado,
10. relaciona el caso con un BST degenerado: identifica qué propiedad de rendimiento falla en cada estructura.

#### Reto 6. Rehashing correcto, pero capacidad incorrecta

Se implementa:

```java
private void resize() {
    List<T>[] oldTable = t;

    while ((1L << d) < n) {
        d++;
    }

    t = allocateTable(1 << d);

    for (List<T> chain : oldTable) {
        for (T x : chain) {
            t[hash(x)].add(x);
        }
    }
}
```

Supón que `resize()` se llama con:

```text
d = 3
t.length = 8
n = 8
```

Responde:

1. determina si el ciclo modifica `d`,
2. determina la capacidad final,
3. explica por qué la tabla reconstruida puede seguir satisfaciendo el invariante de ubicación,
4. explica por qué no satisface la política deseada para la siguiente inserción,
5. identifica la diferencia entre `< n` y `<= n`,
6. construye otro valor de `n` donde la diferencia quede oculta,
7. corrige la condición,
8. formula la postcondición exacta que debe satisfacer el `d` elegido,
9. explica por qué una reconstrucción puede ser estructuralmente correcta y aun así elegir una capacidad inadecuada,
10. relaciona el defecto con la diferencia entre correctitud y política de rendimiento.

#### Reto 7. Auditar una secuencia completa con dos crecimientos

Para evitar depender de `hashCode()`, se proporciona la siguiente tabla de índices:

| Clave | `d = 2` | `d = 3` | `d = 4` |
|---|---:|---:|---:|
| A | 0 | 4 | 12 |
| B | 1 | 2 | 10 |
| C | 3 | 7 | 7 |
| D | 1 | 5 | 13 |
| E | 2 | 6 | 14 |
| F | 0 | 1 | 9 |
| G | 2 | 3 | 11 |
| H | 3 | 0 | 8 |
| I | 1 | 4 | 4 |

La tabla comienza vacía con:

```text
d = 2
t.length = 4
n = 0
```

Se ejecuta:

```text
add(A)
add(B)
add(C)
add(D)
remove(B)
add(E)
add(F)
add(G)
add(H)
add(I)
```

La política es:

```text
si n + 1 > t.length
    ejecutar resize
```

Resuelve toda la historia:

1. registra `n`, `d` y `t.length` después de cada operación,
2. identifica cada crecimiento,
3. dibuja la tabla después de cada crecimiento,
4. identifica las claves que cambian de bucket,
5. identifica las que conservan índice,
6. registra el factor de carga,
7. verifica la unicidad,
8. verifica el invariante de ubicación,
9. cuenta cuántas reinserciones físicas se realizaron,
10. separa inserciones lógicas, eliminaciones lógicas y reinserciones físicas,
11. explica por qué `remove(B)` influye en cuándo ocurre el siguiente crecimiento,
12. identifica la operación individual con mayor trabajo.

#### Reto 8. Convertir un AVL a tabla hash y después crecer

Se tiene un AVL válido con las claves:

```text
14, 22, 31, 38, 47, 53, 61, 72, 85
```

Se desea migrar el conjunto a una tabla hash con encadenamiento.

La tabla empieza con:

```text
d = 2
capacity = 4
n = 0
```

La política de crecimiento es la de la Semana 9.

No utilices `HashSet`.

Responde:

1. elige un recorrido del AVL que visite todas las claves,
2. explica por qué cualquier recorrido completo preserva el conjunto lógico,
3. inserta conceptualmente las nueve claves en la tabla,
4. identifica cuándo deben producirse los crecimientos,
5. explica qué información de orden se pierde,
6. explica qué información de balance deja de tener sentido,
7. compara `find(53)` en AVL y tabla hash,
8. compara una búsqueda fallida,
9. compara obtener el mínimo,
10. compara imprimir todas las claves en orden,
11. analiza el costo de recorrer el AVL,
12. analiza el costo esperado de insertar las claves en la tabla,
13. separa el costo de rehashing,
14. explica por qué migrar representación no significa conservar la forma del AVL.

#### Reto 9. Queue de operaciones, tabla hash de estado y Stack de deshacer

Un sistema recibe operaciones en una `ArrayQueue`:

```text
ADD 21
ADD 37
ADD 53
REMOVE 37
ADD 69
ADD 85
REMOVE 21
ADD 101
UNDO
ADD 117
```

La tabla hash mantiene las claves activas.

Una `LinkedStack` guarda únicamente eliminaciones exitosas para permitir:

```text
UNDO
    reinsertar la clave eliminada más reciente
```

La tabla empieza con:

```text
d = 2
capacity = 4
n = 0
```

Se sabe que las claves fueron elegidas para producir varias colisiones y al menos un crecimiento.

Diseña una función hash sencilla sobre enteros que produzca ese comportamiento y luego:

1. procesa las solicitudes en orden FIFO,
2. registra la tabla después de cada operación,
3. registra `n`, `d`, `capacity` y `alpha`,
4. registra el contenido lógico del Stack,
5. identifica las operaciones que no modifican la tabla,
6. identifica cada crecimiento,
7. muestra qué claves cambian de bucket durante rehashing,
8. explica qué ADT determina el orden de procesamiento,
9. explica qué ADT determina el orden de `UNDO`,
10. explica qué invariante pertenece a la tabla hash y cuál a las estructuras auxiliares,
11. determina el costo esperado de las operaciones de tabla,
12. identifica los costos amortizados que aparecen en la Queue o en la tabla.

#### Reto 10. Deduplicar preservando el primer orden de aparición

Se recibe:

```text
18, 7, 18, 25, 7, 31, 42, 25, 53, 31, 64
```

Se desea producir:

```text
18, 7, 25, 31, 42, 53, 64
```

Restricciones:

```text
preservar la primera aparición
no ordenar
no usar HashSet
```

Puedes utilizar:

```text
tabla hash con encadenamiento
arreglo dinámico
```

Diseña un algoritmo donde:

```text
tabla hash
    decide si una clave ya apareció

arreglo dinámico
    conserva el orden de primera aparición
```

Responde:

1. describe el estado de ambas estructuras después de cada entrada,
2. indica qué estructura controla pertenencia,
3. indica qué estructura controla orden,
4. identifica cuándo puede ocurrir rehashing,
5. explica por qué rehashing no cambia el orden del arreglo de salida,
6. analiza el costo esperado total,
7. incluye el costo amortizado del arreglo dinámico,
8. incluye el costo amortizado del crecimiento de la tabla,
9. determina el peor caso si la función hash concentra todas las claves,
10. explica por qué combinar dos estructuras permite obtener una propiedad que ninguna ofrece por sí sola.

#### Reto 11. Un verificador de invariantes debe detectar más de un tipo de corrupción

Diseña conceptualmente:

```java
boolean checkInvariant()
```

para una tabla con:

```java
List<T>[] t;
int d;
int n;
```

Debe detectar al menos:

```text
t.length != 2^d
clave en bucket incorrecto
n distinto del total almacenado
clave duplicada
```

No puedes utilizar `HashSet` para detectar duplicados.

Responde:

1. diseña una estrategia para verificar `t.length = 2^d`,
2. diseña una estrategia para comprobar ubicación,
3. diseña una estrategia para contar elementos,
4. diseña una estrategia para detectar duplicados usando solamente las operaciones y estructuras disponibles hasta Semana 9,
5. explica qué orden de comprobaciones usarías,
6. determina el costo total de tu verificador,
7. indica cómo cambia el costo si todas las claves están en un mismo bucket,
8. construye cuatro estados, cada uno violando solamente una condición,
9. explica por qué `find` correcto para algunas claves no demuestra que la tabla completa sea válida,
10. compara este verificador con `validParents` de BST o con un verificador de AVL.

#### Reto 12. Pruebas de caja negra para descubrir un rehashing defectuoso

Recibes una implementación cerrada con estas únicas operaciones:

```java
boolean add(int x)
Integer find(int x)
Integer remove(int x)
int size()
```

No puedes observar `t`, `d` ni la capacidad.

Sospechas que:

```text
la tabla funciona antes del primer crecimiento
pero algunas claves dejan de ser localizables después
```

Diseña una batería mínima de pruebas que permita distinguir entre:

```text
A. implementación correcta

B. copia buckets sin recalcular hash

C. rehashing pierde algunos elementos

D. rehashing duplica físicamente elementos
pero size no cambia

E. remove decrementa n cuando la clave no existe
```

Para cada prueba indica:

```text
secuencia de operaciones
resultado esperado
síntoma observado si existe el defecto
qué hipótesis permite descartar
```

No dependas de inspeccionar campos privados.

Explica qué defectos podrían permanecer ocultos incluso con estas operaciones públicas y qué observabilidad adicional sería útil.

#### Reto 13. Esperado y amortizado en la misma ejecución

Se insertan `n` claves en una tabla que duplica capacidad cuando la siguiente inserción produciría `alpha > 1`.

Compara dos escenarios.

Escenario A:

```text
la función hash distribuye razonablemente
```

Escenario B:

```text
todas las claves terminan en el mismo bucket
```

En ambos casos la política de crecimiento y las capacidades son exactamente las mismas.

Responde:

1. compara el número de llamadas a `resize`,
2. compara el número total de elementos reinsertados,
3. compara el costo amortizado del crecimiento,
4. compara la longitud del bucket relevante,
5. compara el costo de `find`,
6. compara el costo de comprobar duplicados en `add`,
7. determina qué parte del análisis es igual en ambos escenarios,
8. determina qué parte cambia radicalmente,
9. explica por qué el crecimiento geométrico no convierte una mala función hash en una buena,
10. explica por qué una implementación puede tener buen costo amortizado de resize y mal costo esperado de búsqueda.

#### Reto 14. Arreglo dinámico, AVL, BinaryHeap y tabla hash reciben el mismo flujo

Se insertan las claves:

```text
44, 19, 71, 8, 32, 57, 83, 26, 63, 91
```

en cuatro estructuras independientes:

```text
arreglo dinámico sin ordenar
AVL
BinaryHeap mínimo
tabla hash con encadenamiento
```

Después se ejecutan conceptualmente estas consultas:

```text
buscar 63
buscar 64
obtener mínimo
retirar mínimo
imprimir claves en orden creciente
eliminar 32
insertar 17
```

Para cada estructura:

1. identifica qué operaciones son naturales para su ADT,
2. identifica cuáles requieren trabajo adicional,
3. formula el invariante principal,
4. determina la complejidad relevante,
5. explica qué estructura favorece búsqueda exacta,
6. explica cuál favorece mínimo,
7. explica cuál preserva orden,
8. explica cuál favorece acceso por índice,
9. indica qué estructura puede necesitar crecimiento físico,
10. distingue el crecimiento de un arreglo dinámico del rehashing de una tabla,
11. explica cómo la misma secuencia lógica produce representaciones radicalmente distintas,
12. decide qué estructura elegirías para cuatro aplicaciones con patrones diferentes.

#### Reto 15. Diseñar una implementación interna que evite reutilizar add durante rehashing

Se desea separar:

```java
boolean add(T x)
```

de una operación interna:

```java
private void placeExisting(T x)
```

La segunda debe utilizarse únicamente durante `resize`.

Diseña ambos métodos bajo estas reglas:

```text
add
    comprueba duplicado
    comprueba crecimiento
    inserta
    incrementa n

placeExisting
    asume que x ya pertenece al conjunto lógico
    calcula bucket con el d actual
    inserta físicamente
    no modifica n
    no vuelve a ejecutar resize
```

Responde:

1. escribe contratos claros para ambos métodos,
2. identifica sus precondiciones,
3. identifica sus postcondiciones,
4. implementa ambos,
5. modifica `resize` para usar `placeExisting`,
6. explica por qué esta separación reduce riesgo de errores,
7. determina qué ocurre si `placeExisting` se llama desde código externo,
8. explica por qué debe ser `private`,
9. analiza la complejidad,
10. relaciona la separación con `splice` frente a `remove` en BST: una operación interna modifica representación mientras otra representa una operación lógica del ADT.

#### Reto 16. Auditoría completa de una implementación

Se presenta:

```java
boolean add(T x) {
    if (find(x) != null) {
        return false;
    }

    if (n + 1 >= t.length) {
        resize();
    }

    n++;
    t[hash(x)].add(x);

    return true;
}

private void resize() {
    List<T>[] oldTable = t;

    int oldN = n;
    n = 0;

    d++;

    t = allocateTable(1 << d);

    for (List<T> chain : oldTable) {
        for (T x : chain) {
            add(x);
        }
    }

    n = oldN;
}
```

No lo ejecutes inicialmente.

Realiza una revisión sistemática:

1. identifica qué política implementa realmente `add`,
2. compara `>=` con la política de la semana,
3. analiza qué ocurre al entrar en `resize`,
4. determina qué significa temporalmente `n = 0`,
5. analiza cada llamada a `add(x)` durante la reconstrucción,
6. determina si puede dispararse otro `resize`,
7. determina si la comprobación de duplicados durante reconstrucción tiene sentido,
8. analiza si `n = oldN` al final corrige todos los posibles problemas,
9. busca un caso donde el resultado físico sea correcto por casualidad,
10. busca un caso donde la política de capacidad sea diferente a la esperada,
11. propone una versión más simple con reinserción física directa,
12. formula los invariantes que deben verificarse al terminar,
13. compara la claridad de ambas versiones,
14. determina la complejidad de ambas,
15. explica por qué una implementación puede terminar con `n` correcto y aun contener decisiones estructurales innecesarias o equivocadas.

### C. Ampliaciones opcionales

#### Ampliación 1. Política de reducción con histéresis

La implementación de la semana solo crece.

Diseña conceptualmente una extensión que también pueda reducir capacidad.

No copies la misma condición de crecimiento.

Debes evitar que la tabla oscile continuamente entre dos capacidades cuando `n` cambia alrededor de una frontera.

Responde:

1. propone un umbral para crecer,
2. propone otro umbral más bajo para reducir,
3. explica por qué deben estar separados,
4. mantiene capacidades como potencias de dos,
5. explica cómo rehashing sirve tanto para crecer como para reducir,
6. preserva `n`,
7. analiza qué ocurre con una secuencia alternada de `add` y `remove`,
8. relaciona la idea con las políticas de reducción del arreglo dinámico estudiadas anteriormente.

No se requiere una política óptima.

#### Ampliación 2. Contabilizar explícitamente el costo de rehashing

Agrega conceptualmente un contador:

```java
private long movedByRehash;
```

Cada vez que una clave existente cambia de tabla durante `resize`, incrementa el contador.

Diseña un experimento con:

```text
10
100
1000
10000
```

inserciones.

Para cada tamaño registra:

```text
cantidad de resizes
elementos reinsertados
capacidad final
movedByRehash / n
```

Antes de ejecutar, predice la tendencia.

Después explica qué evidencia experimental esperarías observar si el crecimiento geométrico produce costo amortizado constante.

#### Ampliación 3. Elegir un umbral mediante un requisito de aplicación

Una aplicación exige:

```text
memoria limitada
muchas búsquedas exactas
muy pocas inserciones después de construir la tabla
```

Otra aplicación exige:

```text
muchas inserciones continuas
memoria abundante
latencia de búsqueda importante
```

Propón para cada una una política de carga razonable.

No necesitas usar valores de bibliotecas reales.

Justifica:

1. uso de memoria,
2. longitud esperada de bucket,
3. frecuencia de rehashing,
4. costo de construcción,
5. costo de búsqueda,
6. por qué no existe un único umbral óptimo para todos los sistemas.

#### Ampliación 4. Construir una tabla desde un arreglo sin utilizar add público

Se recibe un arreglo con claves distintas:

```text
a[0..n-1]
```

Se desea construir directamente una tabla hash cuya capacidad sea la menor potencia de dos estrictamente mayor que `n`.

Diseña:

```java
static <T> ChainedHashTable<T> build(T[] a)
```

La construcción debe:

```text
elegir d una sola vez
crear la tabla una sola vez
insertar físicamente cada elemento
establecer n correctamente
no ejecutar crecimiento durante la construcción
```

Responde:

1. explica por qué ya conoces de antemano la capacidad necesaria,
2. separa reserva de almacenamiento e inserción física,
3. analiza cómo verificar duplicados si la precondición de claves distintas se elimina,
4. compara este procedimiento con construir mediante llamadas repetidas a `add`,
5. compara el número de resizes,
6. analiza la complejidad esperada,
7. relaciona el diseño con `heapify`: ambos aprovechan conocer todo el conjunto inicial para construir una representación sin repetir exactamente el mismo proceso usado por inserciones individuales.
