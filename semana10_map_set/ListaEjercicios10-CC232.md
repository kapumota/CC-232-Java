### Ejercicios de la Semana 10

Estos ejercicios son opcionales y no requieren entrega obligatoria.

No se presupone que todos deban resolverse. La sección de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1 a 10.

El objetivo no es repetir la Actividad 10 ni volver a resolver los mismos ejemplos utilizados para introducir `frequencies(...)`, `mostFrequent(...)`, `firstDuplicate(...)`, `uniqueSorted(...)` o `chooseImplementation(...)`.

En particular, esta lista evita dedicar muchos ejercicios a:

```text
contar palabras de un arreglo
retornar directamente el primer duplicado
convertir directamente un arreglo a TreeSet
repetir la misma comparación HashSet frente a TreeSet
```

Esos problemas ya cumplen su función introductoria en la actividad y en los archivos de la semana.

La mayoría de los problemas de esta lista exige:

```text
razonar antes de ejecutar
reconstruir estados lógicos y físicos
separar ADT de implementación
formular invariantes
detectar código que compila pero es incorrecto
diseñar contraejemplos
analizar actualizaciones y no solo inserciones
combinar Map y Set
combinar estructuras de semanas anteriores
distinguir igualdad de identidad
distinguir pertenencia de asociación
distinguir orden de propiedad lógica
analizar costos concretos y asintóticos
distinguir costo esperado de costo amortizado
analizar compromisos entre tiempo y memoria
elegir estructuras a partir del patrón de operaciones
diseñar algoritmos que preserven varios invariantes
diseñar pruebas que revelen errores estructurales
```

Los temas centrales de la Semana 10 utilizados son:

```text
problema de asociar información a una clave
par clave-valor

ADT Map
clave
valor
unicidad de claves
get
put
containsKey
remove
size
actualización de una clave existente

Map implementado mediante hashing
hash calculado sobre la clave
bucket de pares clave-valor
Map.Entry
entrySet
hashCode
equals

Map no implica orden
LinkedHashMap como implementación con propiedad adicional de orden

complejidad esperada de operaciones por clave
O(1) esperado
peor caso O(n)
esperado frente a amortizado
rehashing

ADT Set
pertenencia
elementos únicos
add
contains
remove
size
unicidad como invariante
resultado booleano de add

HashSet
Set implementado mediante hashing

TreeSet
Set ordenado
árbol balanceado
O(log n)

propiedad del ADT frente a propiedad de la implementación
HashSet frente a TreeSet
hashing frente a árbol balanceado
hashing frente a BinaryHeap
elección de representación
tiempo frente a memoria
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
Semana 1
arreglos dinámicos
tamaño y capacidad
resize
crecimiento geométrico
costo amortizado
acceso por índice

Semana 2
listas enlazadas
nodos
referencias
modificación local
recorridos

Semana 3
Stack
Queue
Deque
FIFO
LIFO
arreglo circular

Semanas 4 y 5
BST
orden
find
add
remove
sucesor
predecesor
altura h
preservación de invariantes

Semana 6
AVL
altura
balance
rotaciones
O(log n)

Semana 7
Priority Queue
BinaryHeap
mínimo
peek
bubbleUp
trickleDown
búsqueda arbitraria O(n)

Semana 8
hashing
hashCode
función hash
buckets
colisiones
encadenamiento separado
O(k)
costo esperado

Semana 9
factor de carga
capacidad
resize
rehashing
crecimiento geométrico
costo amortizado
esperado frente a amortizado

trazas
invariantes
correctitud
contraejemplos
complejidad
```

Para los ejercicios de esta semana se pueden utilizar, cuando el enunciado lo permita:

```java
Map<K,V>
HashMap<K,V>
LinkedHashMap<K,V>
Map.Entry<K,V>
Set<T>
HashSet<T>
TreeSet<T>
```

pero solamente con las operaciones esenciales estudiadas.

No se requiere utilizar:

```text
Streams
Collectors
compute
merge
putIfAbsent
ConcurrentHashMap
WeakHashMap
EnumMap
TreeMap
LinkedHashSet
Collections.sort
```

Tampoco se requiere estudiar la implementación interna de `HashMap`, `LinkedHashMap` o `TreeSet`, árboles rojo-negro ni variantes nuevas de hashing.

Cuando un reto utilice una tabla hash conceptual, puede emplearse el mismo modelo de las Semanas 8 y 9:

```text
arreglo de buckets
+
colección local por bucket
+
hashCode
+
función hash
+
equals
```

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir una situación nueva, pero deben poder analizarse utilizando los conceptos disponibles hasta la Semana 10.

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir un Map a partir de su representación física

Se modela conceptualmente un `Map<Integer,String>` mediante una tabla hash con encadenamiento.

La función utilizada es:

```text
hash(key) = floorMod(key, 7)
```

Se observa el siguiente estado físico:

```text
bucket 0: [(14,"AVL")]
bucket 1: [(8,"Heap"), (22,"Hash")]
bucket 2: []
bucket 3: [(10,"Queue")]
bucket 4: []
bucket 5: [(12,"Map")]
bucket 6: [(20,"Set")]

size = 6
```

Sin ejecutar código:

1. identifica las seis claves lógicas del Map,
2. identifica los seis valores asociados,
3. verifica el bucket correcto de cada entrada,
4. determina todas las colisiones visibles,
5. verifica si `size` coincide con la cantidad de asociaciones,
6. explica por qué `hash` debe calcularse con la clave y no con el valor,
7. indica qué bucket debe examinar `get(22)`,
8. indica qué debe retornar conceptualmente `get(22)`,
9. indica qué debe responder `containsKey(22)`,
10. explica qué diferencia conceptual existe entre esas dos últimas preguntas,
11. describe el estado lógico después de `put(22,"Grafos")`,
12. determina si `size` debe cambiar,
13. describe el estado después de `put(15,"Deque")`,
14. determina si existe una colisión nueva,
15. describe el estado después de `remove(10)`,
16. formula el invariante de unicidad de claves,
17. formula el invariante de ubicación,
18. construye un estado con las mismas asociaciones y el mismo `size` que viole únicamente el invariante de ubicación,
19. construye otro estado que respete la ubicación pero viole la unicidad de claves,
20. explica por qué imprimir todas las entradas no basta para demostrar que el Map está correctamente representado.

Concluye distinguiendo:

```text
contenido lógico del Map
representación física de buckets
propiedades del ADT
propiedades de la implementación
```

#### Ejercicio 2. Elegir primero el ADT y después la representación

Se deben diseñar ocho componentes independientes.

Sistema A:

```text
código de estudiante -> nota
muchas consultas por código
el orden de los códigos no importa
```

Sistema B:

```text
códigos de estudiantes ya matriculados
no se permiten repetidos
el orden no importa
```

Sistema C:

```text
códigos distintos
se necesita recorrerlos de menor a mayor
y consultar sucesor y predecesor
```

Sistema D:

```text
trabajos con prioridad
se consulta y elimina repetidamente el mínimo
```

Sistema E:

```text
solicitudes que deben procesarse
en el mismo orden en que llegaron
```

Sistema F:

```text
90 % de las operaciones son get(i)
10 % son inserciones al final
```

Sistema G:

```text
claves ordenadas
búsquedas frecuentes
se exige altura O(log n)
```

Sistema H:

```text
se conoce una referencia directa a un nodo
y se realizan modificaciones locales alrededor de él
```

Elige entre:

```text
arreglo dinámico
DLList
Queue
AVL
BinaryHeap
Map basado en hashing
HashSet
TreeSet
```

Para cada sistema:

1. identifica primero el ADT o comportamiento requerido,
2. elige después una representación,
3. formula el invariante principal,
4. identifica la operación dominante,
5. justifica el costo relevante,
6. indica una propiedad que tu estructura mantiene,
7. indica una propiedad que deliberadamente no mantiene,
8. propone una alternativa razonable,
9. explica qué costo o propiedad empeoraría con la alternativa,
10. decide si tu argumento depende de costo esperado, peor caso o costo amortizado.

Concluye:

> ¿Por qué la pregunta "qué estructura es mejor" está incompleta si no se especifica primero qué operaciones deben favorecerse?

### B. Retos integradores

#### Reto 1. Un Map que inserta una nueva entrada cuando debería actualizar

Un estudiante implementa conceptualmente un Map con encadenamiento.

Cada bucket almacena objetos:

```java
static class Entry {
    int key;
    String value;
}
```

Su `put` contiene la siguiente lógica:

```java
void put(int key, String value) {
    int i = hash(key);
    buckets[i].add(new Entry(key, value));
    size++;
}
```

El método compila y permite recuperar algunas claves correctamente si `get` retorna la primera coincidencia.

Se ejecuta:

```text
put(12, "AVL")
put(19, "Heap")
put(12, "Hash")
```

1. construye el estado físico suponiendo `hash(x) = floorMod(x, 7)`,
2. determina cuántas entradas físicas existen,
3. determina cuántas claves lógicas distintas deberían existir,
4. identifica qué invariante de Map se rompe,
5. determina cuál debería ser el valor final asociado a 12,
6. determina cuál debería ser el `size` final,
7. explica por qué un `get(12)` que retorna `"AVL"` puede parecer razonable y aun ser incorrecto,
8. explica por qué un `get(12)` que retorna `"Hash"` tampoco repara la representación,
9. reconstruye conceptualmente el algoritmo correcto de `put`,
10. identifica qué operación debe ocurrir antes de modificar el bucket,
11. explica cuándo `size++` es correcto,
12. explica cuándo no debe ejecutarse,
13. justifica la complejidad en función de la longitud `k` del bucket,
14. relaciona este error con `add` de la tabla hash de la Semana 8,
15. relaciona la regla "localizar antes de modificar" con una operación estudiada en BST.

No utilices `HashMap` para responder. El objetivo es auditar la semántica de Map sobre la representación conocida.

#### Reto 2. El hash se calcula sobre el valor

Se propone una implementación de Map que almacena:

```text
key -> value
```

pero calcula:

```java
private int hash(int key, int value) {
    return Math.floorMod(value, buckets.length);
}
```

Supón:

```text
buckets.length = 8
```

y se ejecuta:

```text
put(101, 13)
get(101)
put(101, 21)
get(101)
```

1. determina el bucket elegido durante la primera inserción,
2. explica qué información posee `get(101)` para localizar la entrada,
3. determina por qué el valor no puede ser la base estable de localización,
4. muestra qué ocurre si la asociación cambia de `101 -> 13` a `101 -> 21`,
5. explica por qué actualizar un valor no debería cambiar la identidad de la asociación,
6. formula la propiedad estable que sí posee la clave,
7. corrige conceptualmente el cálculo,
8. explica qué papel cumple `equals` después de seleccionar un bucket,
9. construye dos claves distintas que colisionen bajo tu función corregida,
10. explica por qué esa colisión no rompe la correctitud,
11. identifica la diferencia entre:
   - actualizar un valor,
   - cambiar una clave,
12. relaciona el defecto con el invariante:

```text
cada Entry(key,value) pertenece al bucket determinado por hash(key)
```

#### Reto 3. Rehashing correcto para Set, incorrecto para Map

Una implementación anterior de conjunto almacenaba únicamente claves.

Al convertirla a Map, cada bucket almacena ahora:

```text
Entry(key, value)
```

Durante `resize()` un estudiante escribe:

```java
for (List<Entry> bucket : oldTable) {
    for (Entry entry : bucket) {
        newTable[hash(entry.value)].add(entry);
    }
}
```

No modifica `size`.

1. identifica el defecto,
2. explica por qué el código podría haber tenido sentido en una estructura que almacenara únicamente un valor-clave,
3. formula qué dato identifica ahora cada asociación,
4. construye un ejemplo con dos entradas donde el error sea observable,
5. muestra la tabla antes y después del crecimiento,
6. determina si `size` puede conservar un valor correcto mientras la ubicación es incorrecta,
7. explica qué puede ocurrir con `get(key)` después del resize,
8. corrige únicamente la expresión necesaria,
9. explica por qué rehashing debe recalcular la ubicación con la nueva capacidad,
10. distingue:
   - reinserción física,
   - nueva inserción lógica,
11. explica por qué una reinserción física no debe incrementar `size`,
12. relaciona el problema con la Semana 9,
13. determina la complejidad del `resize`,
14. indica qué parte del análisis es amortizada.

#### Reto 4. Eliminar repetidos preservando el orden original

Se recibe:

```text
[8, 3, 8, 5, 3, 9, 5, 1]
```

Se desea producir:

```text
[8, 3, 5, 9, 1]
```

Condiciones:

```text
cada valor debe aparecer una sola vez
el orden debe ser el de la primera aparición
no se permite ordenar la entrada
no se permite usar LinkedHashSet
```

Puedes utilizar:

```text
un arreglo dinámico para la salida
un HashSet auxiliar
```

1. identifica qué ADT representa la salida,
2. identifica qué ADT responde si un valor ya apareció,
3. formula el invariante del `HashSet` auxiliar antes de procesar la posición `i`,
4. formula el invariante del arreglo de salida,
5. diseña el algoritmo,
6. traza las ocho posiciones,
7. determina cuántas veces cambia el Set,
8. determina cuántas inserciones recibe la salida,
9. justifica el orden de la salida,
10. justifica la unicidad,
11. analiza el tiempo esperado,
12. analiza el espacio adicional,
13. compara con una solución de dos bucles sin Set,
14. explica qué idea se reutiliza de la Semana 1,
15. explica qué idea se reutiliza de la Semana 10.

Después responde:

> ¿Por qué `TreeSet` no es una sustitución correcta si el requisito es conservar el orden de primera aparición?

#### Reto 5. Intersección con multiplicidad

Se tienen:

```text
A = [4, 7, 4, 2, 7, 7, 9]
B = [7, 4, 7, 4, 4, 8]
```

Se desea producir una intersección que respete multiplicidades.

Para estos datos, una salida válida es:

```text
[7, 4, 7, 4]
```

porque:

```text
4 aparece min(2,3) = 2 veces
7 aparece min(3,2) = 2 veces
```

No basta con usar un `Set`, porque un Set elimina multiplicidades.

Diseña una solución usando:

```text
Map<Integer,Integer>
+
arreglo dinámico de salida
```

1. explica qué información debe almacenar el Map,
2. decide cuál de los dos arreglos conviene usar para construir el Map si quieres reducir memoria auxiliar,
3. formula el invariante del Map después de procesar un prefijo del primer arreglo elegido,
4. explica qué debe ocurrir al encontrar un valor del segundo arreglo cuya cuenta es positiva,
5. explica qué debe ocurrir cuando la cuenta llega a cero,
6. decide si es necesario eliminar la clave del Map cuando la cuenta llega a cero,
7. justifica que ningún valor aparezca demasiadas veces en la salida,
8. traza el algoritmo con los datos dados,
9. determina el resultado,
10. analiza el tiempo esperado,
11. analiza el espacio adicional en función de la cantidad de valores distintos,
12. explica por qué `HashSet` no contiene suficiente información,
13. explica por qué `TreeSet` tampoco resuelve por sí solo la multiplicidad,
14. relaciona el problema con la diferencia:

```text
Set
    pertenencia

Map
    clave -> información asociada
```

#### Reto 6. Two-sum con índices y una condición de correctitud

Se recibe:

```text
values = [11, 4, 7, 2, 15, 6]
target = 13
```

Se desea retornar dos índices distintos `i` y `j` tales que:

```text
values[i] + values[j] = target
```

No se permite usar dos bucles anidados como solución principal.

Diseña una solución de una sola pasada utilizando:

```text
Map<Integer,Integer>
```

donde el Map asocie:

```text
valor visto -> índice
```

1. antes de procesar `values[i]`, ¿qué información debe contener el Map?,
2. para el valor actual `x`, ¿qué complemento se necesita?,
3. ¿qué debe consultarse antes de insertar `x`?,
4. explica por qué el orden:
   - consultar complemento,
   - insertar valor actual,
   ayuda a impedir utilizar dos veces la misma posición,
5. traza la entrada dada,
6. identifica la primera pareja encontrada,
7. construye una entrada con valores repetidos donde actualizar siempre el índice de una clave produzca una pareja distinta,
8. decide si importa conservar el primer índice o el último índice,
9. especifica tu política,
10. justifica su correctitud,
11. analiza el tiempo esperado,
12. analiza el espacio,
13. compara con el doble bucle `O(n^2)`,
14. explica por qué este algoritmo necesita `Map` y no solamente `Set` si deben retornarse índices.

#### Reto 7. Índice invertido de documentos con Map de Sets

Se tienen cuatro documentos ya tokenizados:

```text
doc 0: [avl, heap, hash]
doc 1: [map, hash, map]
doc 2: [heap, queue]
doc 3: [hash, queue, avl]
```

Se desea construir:

```text
palabra -> conjunto de documentos que contienen la palabra
```

Por ejemplo:

```text
avl   -> {0, 3}
heap  -> {0, 2}
hash  -> {0, 1, 3}
```

La palabra `map` aparece dos veces en el documento 1, pero el documento 1 debe aparecer una sola vez en su conjunto.

Diseña conceptualmente:

```text
Map<String, Set<Integer>>
```

1. identifica el ADT exterior,
2. identifica el ADT utilizado como valor,
3. explica qué representa cada clave,
4. explica qué representa cada Set,
5. formula el invariante después de procesar los primeros `d` documentos,
6. construye manualmente el índice completo,
7. explica por qué la repetición de `map` dentro del documento 1 no duplica el identificador 1,
8. explica qué operación de Set conserva esa propiedad,
9. determina qué retorna una consulta conceptual de `"queue"`,
10. determina qué debería ocurrir con una palabra inexistente,
11. analiza el tiempo esperado en función del número total de tokens,
12. analiza el espacio en función de las asociaciones palabra-documento distintas,
13. explica qué información se perdería si el valor fuera solo un entero,
14. explica qué información se perdería si se utilizara solo un Set global de palabras,
15. relaciona la estructura con la idea:

```text
clave -> colección de información relacionada
```

#### Reto 8. Registro de matrícula: Queue + Map + Set

Las solicitudes llegan en una `Queue` y deben procesarse en FIFO.

Cada solicitud tiene una forma:

```text
ADD estudiante curso
DROP estudiante curso
```

Se recibe:

```text
ADD A CC232
ADD A MA101
ADD B CC232
ADD A CC232
DROP A MA101
DROP B MA101
ADD C CC232
DROP B CC232
```

Se desea mantener:

```text
Map<String, Set<String>>
```

con:

```text
estudiante -> cursos en los que está matriculado
```

Reglas:

```text
un estudiante no puede estar dos veces en el mismo curso
DROP de un curso inexistente no cambia el estado
si un estudiante queda sin cursos, se elimina su clave del Map
```

1. explica qué comportamiento aporta la Queue,
2. explica qué comportamiento aporta el Map,
3. explica qué comportamiento aporta cada Set,
4. formula el invariante global de la representación,
5. procesa todas las solicitudes en orden,
6. registra el estado después de cada una,
7. explica qué ocurre con el segundo `ADD A CC232`,
8. explica qué ocurre con `DROP B MA101`,
9. determina cuándo debe desaparecer una clave del Map,
10. explica por qué `size()` del Map no es la cantidad total de matrículas,
11. diseña una operación para calcular el número total de matrículas recorriendo `entrySet`,
12. analiza su costo,
13. analiza el costo esperado de cada actualización individual,
14. indica qué estructura de la Semana 3 reaparece,
15. indica qué propiedad de Semana 10 evita duplicados.

#### Reto 9. Un algoritmo de ventana reciente necesita más que un Set

Se procesan identificadores de solicitudes:

```text
[5, 8, 5, 5, 9, 8, 4]
```

En cada instante solo interesan las últimas tres solicitudes ya procesadas.

Queremos poder responder antes de insertar el nuevo identificador:

```text
¿este identificador apareció en las últimas tres posiciones?
```

Un estudiante propone mantener:

```text
Queue<Integer> recent
HashSet<Integer> present
```

Cuando la ventana supera tres elementos, elimina el frente de `recent` y también lo elimina de `present`.

Analiza el caso:

```text
5, 8, 5, 5
```

1. traza la Queue y el Set,
2. identifica cuándo una misma clave puede aparecer más de una vez dentro de la Queue,
3. explica por qué eliminar el elemento más antiguo del Set puede borrar una pertenencia que todavía debería existir,
4. construye el instante exacto donde ocurre el defecto,
5. explica qué información adicional falta,
6. reemplaza el Set por:

```text
Map<Integer,Integer>
```

donde el valor represente cantidad de apariciones en la ventana,
7. formula el invariante de la Queue,
8. formula el invariante del Map,
9. diseña las operaciones al:
   - insertar un identificador,
   - retirar el identificador más antiguo,
10. determina cuándo debe eliminarse una clave del Map,
11. traza toda la entrada,
12. justifica la correctitud,
13. analiza el tiempo esperado total,
14. relaciona:
   - Queue de Semana 3,
   - Map de Semana 10,
15. explica por qué este problema muestra que "pertenencia" y "cantidad" son necesidades diferentes.

#### Reto 10. HashSet, TreeSet o dos estructuras

Se necesita implementar un conjunto de identificadores con estas operaciones:

```text
add(x)
contains(x)
remove(x)
printSorted()
```

Se consideran tres diseños.

Diseño A:

```text
HashSet
```

Diseño B:

```text
TreeSet
```

Diseño C:

```text
HashSet
+
construir una estructura ordenada solamente cuando se llama printSorted()
```

Supón una carga de trabajo con:

```text
100000 operaciones add/contains/remove
10 llamadas a printSorted
```

y otra con:

```text
10000 operaciones add/contains/remove
5000 llamadas a printSorted
```

No necesitas calcular tiempos exactos.

Para cada carga:

1. identifica qué operaciones favorece HashSet,
2. identifica qué operaciones favorece TreeSet,
3. explica qué costo paga TreeSet aun cuando nunca se solicita orden,
4. explica qué costo adicional aparece en el Diseño C cuando se necesita ordenar,
5. determina qué información debe reconstruirse en cada `printSorted`,
6. argumenta qué diseño parece más razonable en la primera carga,
7. argumenta qué diseño parece más razonable en la segunda,
8. distingue costo esperado de HashSet frente a costo `O(log n)` de TreeSet,
9. explica por qué "HashSet es más rápido" es una afirmación incompleta,
10. explica por qué "TreeSet es mejor porque hace más cosas" también es incompleta,
11. relaciona el análisis con la regla:

```text
no mantener una propiedad que el problema no necesita
```

#### Reto 11. Una estructura única para pertenencia y mínimo

Se necesita un ADT con:

```text
addUnique(x)
contains(x)
removeMin()
size()
```

Requisitos:

```text
no se permiten duplicados
contains debe ser frecuente
removeMin también es frecuente
```

Se consideran tres opciones:

```text
A. HashSet
B. TreeSet
C. HashSet + BinaryHeap
```

1. explica qué requisito resuelve bien A,
2. explica qué requisito resuelve mal A,
3. explica por qué B puede resolver las cuatro operaciones,
4. indica los costos relevantes de B,
5. explica qué ventaja puede buscar C,
6. formula el invariante que debe relacionar el HashSet y el BinaryHeap en C,
7. explica qué debe ocurrir en ambos cuando `addUnique(x)` tiene éxito,
8. explica qué debe ocurrir cuando el valor ya existe,
9. explica qué debe ocurrir durante `removeMin()`,
10. construye una secuencia donde actualizar una estructura y olvidar la otra rompa el diseño,
11. determina qué errores podrían permanecer ocultos si solo se prueba `contains`,
12. compara la complejidad de las tres alternativas,
13. compara el consumo de memoria,
14. explica por qué combinar dos estructuras puede mejorar ciertas operaciones pero aumentar el número de invariantes,
15. elige un diseño y justifica la decisión según una carga de trabajo concreta inventada por ti.

#### Reto 12. Map basado en hashing frente a Map basado en AVL

Se desea implementar el mismo ADT:

```text
Map<Integer,String>
```

con dos representaciones distintas.

Implementación H:

```text
tabla hash con encadenamiento
```

Implementación A:

```text
AVL cuyos nodos almacenan (key,value)
```

La aplicación requiere:

```text
get(key)
put(key,value)
remove(key)
imprimir claves ordenadas
buscar la menor clave >= x
```

1. explica por qué ambas representaciones pueden implementar `get`, `put` y `remove`,
2. formula el invariante principal de H,
3. formula los invariantes principales de A,
4. compara `get` por clave exacta,
5. compara `put`,
6. compara `remove`,
7. determina cuál conserva información de orden,
8. explica cómo esa información permite recorrer claves ordenadas,
9. explica cuál puede responder naturalmente la consulta "menor clave >= x",
10. explica por qué H no puede descartar claves usando relaciones `<` y `>`,
11. distingue:
   - `O(1)` esperado,
   - `O(log n)` garantizado por altura balanceada,
12. construye un escenario donde H sea una elección razonable,
13. construye otro donde A sea preferible,
14. explica por qué ambas implementan el mismo ADT sin tener la misma complejidad ni las mismas propiedades adicionales.

#### Reto 13. La misma colección en arreglo, AVL, heap, HashSet y TreeSet

Se insertan las claves:

```text
42, 17, 68, 9, 31, 55, 74, 26, 60
```

en:

```text
arreglo dinámico sin ordenar
AVL
BinaryHeap mínimo
HashSet conceptual con 7 buckets
TreeSet
```

Para el HashSet conceptual utiliza:

```text
hash(x) = floorMod(x, 7)
```

1. representa el contenido del arreglo,
2. dibuja el AVL resultante,
3. dibuja el heap después de todas las inserciones,
4. construye los siete buckets del HashSet,
5. escribe el recorrido ordenado conceptual del TreeSet,
6. traza la búsqueda de 60 en cada estructura,
7. traza la búsqueda fallida de 61,
8. explica qué información permite descartar candidatos en AVL,
9. explica qué información reduce la región candidata en HashSet,
10. explica por qué el heap no favorece la búsqueda arbitraria,
11. compara la obtención del mínimo,
12. compara la obtención de todos los elementos en orden,
13. compara la detección de pertenencia,
14. distingue peor caso, costo esperado y costo `O(log n)`,
15. explica por qué las cinco representaciones pueden contener el mismo conjunto lógico y, sin embargo, favorecer operaciones diferentes.

#### Reto 14. `equals` se omite dentro del bucket

Una tabla hash conceptual para Map utiliza:

```text
hash(key) = floorMod(key, 5)
```

y almacena:

```text
6  -> "A"
11 -> "B"
16 -> "C"
```

Las tres claves colisionan.

Un estudiante implementa `get` así:

```java
String get(int key) {
    List<Entry> bucket = buckets[hash(key)];

    if (bucket.isEmpty()) {
        return null;
    }

    return bucket.get(0).value;
}
```

1. determina qué retorna `get(6)`,
2. determina qué retorna `get(11)`,
3. determina qué retorna `get(16)`,
4. explica qué parte del hashing sí está utilizando,
5. identifica la comparación que falta,
6. explica por qué mismo bucket no significa misma clave,
7. relaciona el error con colisiones,
8. escribe conceptualmente el recorrido correcto del bucket,
9. explica qué propiedad debe cumplir `equals`,
10. determina el costo en función de la longitud `k` del bucket,
11. construye un caso donde el método incorrecto parezca funcionar,
12. explica por qué una prueba con una sola entrada por bucket sería insuficiente,
13. diseña una batería mínima que fuerce colisiones y detecte el defecto.

#### Reto 15. `containsKey` implementado buscando entre valores

Se tiene un Map:

```text
101 -> "Ana"
205 -> "Luis"
310 -> "Ana"
```

Un estudiante implementa:

```java
boolean containsKey(int key) {
    for (Entry entry : entries) {
        if (entry.value.equals(String.valueOf(key))) {
            return true;
        }
    }
    return false;
}
```

1. identifica qué pregunta responde realmente el código,
2. explica por qué no representa `containsKey`,
3. determina qué debería responder `containsKey(205)`,
4. determina si dos claves distintas pueden compartir el mismo valor,
5. explica por qué eso no viola el invariante de Map,
6. formula correctamente la pregunta de `containsKey`,
7. indica cómo debería localizarse la clave en una implementación hash,
8. distingue:
   - `containsKey`,
   - "¿existe este valor?",
9. explica por qué un recorrido entre valores puede costar `O(n)`,
10. explica qué ventaja de hashing se pierde,
11. diseña un contraejemplo mínimo,
12. relaciona el problema con la diferencia entre clave y valor.

#### Reto 16. Esperado y amortizado en la misma historia

Una tabla hash que implementa Map comienza con:

```text
capacity = 2
size = 0
```

La política de crecimiento duplica la capacidad cuando la siguiente inserción nueva produciría:

```text
size + 1 > capacity
```

Se insertan 20 claves distintas.

Supón además que, entre redimensionamientos, la función hash mantiene una longitud esperada constante de bucket.

1. determina las capacidades que aparecen,
2. identifica qué inserciones producen crecimiento,
3. calcula cuántas entradas deben reinsertarse en cada crecimiento,
4. calcula el número total de reinserciones físicas,
5. explica por qué cada resize individual puede costar `O(n)`,
6. explica por qué el costo total de crecimiento puede analizarse amortizadamente,
7. explica qué propiedad diferente se usa para afirmar `get -> O(1)` esperado,
8. explica por qué buena amortización no garantiza buena distribución,
9. explica por qué buena distribución no elimina el costo de resize,
10. construye conceptualmente una mala función hash donde el análisis amortizado del crecimiento siga siendo válido pero `get` sea `O(n)`,
11. distingue con tus propias palabras:
   - esperado,
   - amortizado,
   - peor caso,
12. relaciona el crecimiento con el arreglo dinámico de la Semana 1,
13. explica qué diferencia específica obliga a rehashing en lugar de copiar entradas al mismo índice.

#### Reto 17. Un recorrido de entrySet debe preservar una propiedad global

Se tiene:

```text
curso -> cantidad de estudiantes
```

en un `Map<String,Integer>`.

Se desea encontrar simultáneamente:

```text
curso con menor cantidad
curso con mayor cantidad
suma total de estudiantes
```

No se permite ordenar las entradas.

Se proporciona la estructura general:

```java
for (Map.Entry<String,Integer> entry : data.entrySet()) {
    // actualizar estado acumulado
}
```

No escribas inmediatamente el código.

1. identifica qué variables de estado necesitas,
2. formula un invariante después de procesar `r` entradas,
3. explica cómo debe inicializarse el mínimo,
4. explica cómo debe inicializarse el máximo,
5. analiza el caso de Map vacío,
6. analiza el caso de una sola entrada,
7. diseña el algoritmo,
8. justifica la correctitud a partir del invariante,
9. determina el costo si existen `k` claves,
10. explica por qué el orden de iteración no afecta los tres resultados,
11. explica qué resultado sí podría depender del orden si existieran empates y se retornara "el primero",
12. distingue propiedad lógica del Map frente a orden de una implementación como LinkedHashMap.

Este reto utiliza `entrySet`, pero no repite el problema de `mostFrequent(...)`: debes mantener varias propiedades acumuladas simultáneamente.

#### Reto 18. Diseñar una batería de pruebas para una implementación desconocida de Map y Set

Se reciben dos implementaciones desconocidas.

La primera ofrece:

```text
Map:
put
get
containsKey
remove
size
```

La segunda ofrece:

```text
Set:
add
contains
remove
size
```

No puedes observar directamente su representación.

Diseña una batería pequeña pero potente de pruebas que permita detectar errores en:

```text
Map
inserción de clave nueva
actualización de clave existente
size después de actualizar
búsqueda de clave ausente
eliminación de clave presente
eliminación de clave ausente
dos claves con el mismo valor
colisiones entre claves
rehashing que pierde asociaciones

Set
inserción nueva
inserción repetida
valor booleano de add
size después de repetición
remove presente
remove ausente
colisiones
rehashing que pierde elementos
```

Para cada prueba indica:

```text
estado inicial
operaciones
resultado observable esperado
propiedad que intenta verificar
tipo de defecto que detectaría
```

Después:

1. identifica qué errores pueden detectarse sin conocer los buckets,
2. identifica cuáles requieren forzar claves con colisiones conocidas,
3. explica por qué probar únicamente casos sin colisiones es insuficiente,
4. explica por qué comprobar solo `size` es insuficiente,
5. explica por qué comprobar solo `get` o `contains` también es insuficiente,
6. diseña una secuencia que fuerce al menos un crecimiento y después verifique todas las claves anteriores,
7. relaciona esta batería con la idea de invariante de representación.

### C. Ampliaciones opcionales

#### Ampliación 1. Clave mutable y pérdida de localización

Considera:

```java
static class Student {
    int code;
    String name;

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public boolean equals(Object other) {
        // igualdad basada en code
    }
}
```

Se ejecuta conceptualmente:

```text
Student s = (code=101, name="Ana")
set.add(s)

s.code = 205

set.contains(s)
```

No necesitas conocer la implementación interna de `HashSet`.

1. explica qué información utilizó el Set al insertar,
2. explica qué información ha cambiado,
3. determina por qué el bucket esperado puede cambiar,
4. explica cómo un objeto puede seguir físicamente almacenado y dejar de ser localizable,
5. identifica qué invariante conceptual se rompe,
6. explica por qué modificar un campo que participa en `hashCode` mientras la clave está almacenada es peligroso,
7. propone dos estrategias de diseño que eviten el problema,
8. relaciona el caso con el principio de que la clave debe permanecer estable para la localización.

#### Ampliación 2. `equals` y `hashCode` no son consistentes

Se define un tipo `Student` donde:

```text
equals
    compara solamente code

hashCode
    usa code y name
```

Dos objetos:

```text
a = (101, "Ana")
b = (101, "ANITA")
```

son iguales según `equals`, pero pueden producir hash codes distintos.

1. explica qué exige la consistencia entre igualdad y `hashCode`,
2. muestra cómo dos objetos lógicamente iguales podrían dirigirse a buckets distintos,
3. explica por qué un Set podría terminar aceptando dos presencias lógicas equivalentes,
4. explica por qué un Map podría fallar al recuperar un valor usando una clave equivalente,
5. distingue esta situación de una colisión normal,
6. propone una definición consistente,
7. explica por qué:

```text
mismo hashCode
```

no exige:

```text
equals == true
```

pero:

```text
equals == true
```

sí exige hash codes compatibles.

#### Ampliación 3. Representación genérica de un grafo con Map y Set

Todavía no se requiere dominar BFS.

Se desea representar un grafo no dirigido cuyos vértices son nombres:

```text
"A"
"B"
"C"
"D"
```

Una representación posible es:

```text
Map<String, Set<String>>
```

con:

```text
vértice -> vecinos
```

Aristas:

```text
A-B
A-C
B-D
C-D
```

1. construye la representación,
2. explica qué invariante debe cumplir cada Set de vecinos,
3. formula el invariante adicional de un grafo no dirigido:

```text
si v pertenece a vecinos(u)
entonces u pertenece a vecinos(v)
```

4. diseña conceptualmente `addEdge(u,v)`,
5. explica qué debe ocurrir si la arista ya existe,
6. explica qué debe ocurrir si aparece un vértice nuevo,
7. determina la complejidad esperada de consultar si existe la arista `u-v`,
8. compara esta representación con una matriz de adyacencia a nivel conceptual,
9. explica qué idea de Map se reutiliza,
10. explica qué idea de Set se reutiliza.

#### Ampliación 4. Puente hacia BFS: Queue + visitados

Se proporciona el siguiente algoritmo conceptual:

```text
marcar source como visto
encolar source

mientras la Queue no esté vacía:
    retirar u
    para cada vecino v de u:
        si v no ha sido visto:
            marcar v
            encolar v
```

No necesitas estudiar todavía todos los detalles de grafos.

1. identifica qué propiedad de Queue utiliza el algoritmo,
2. identifica qué pregunta responde la estructura de visitados,
3. explica por qué "visitado" tiene semántica de Set,
4. determina qué ocurriría si nunca se registraran visitados,
5. en un grafo con ciclo `A-B-C-A`, traza las primeras operaciones sin visitados,
6. explica por qué una representación `boolean[] seen` puede reemplazar a un HashSet cuando los vértices son `0..n-1`,
7. relaciona esa decisión con direccionamiento directo,
8. explica qué parte corresponde al ADT y qué parte a la representación.

#### Ampliación 5. Mantener varias vistas del mismo estado

Un sistema mantiene simultáneamente:

```text
Map<Integer,String> idToName
TreeSet<Integer> orderedIds
```

El objetivo es soportar:

```text
nombre por id
pertenencia de id
recorrido de ids ordenados
```

1. formula el invariante que debe relacionar ambas estructuras,
2. diseña conceptualmente una inserción correcta,
3. diseña una eliminación correcta,
4. construye un error donde se actualiza solo una estructura,
5. explica qué consultas seguirían funcionando,
6. explica cuáles revelarían la inconsistencia,
7. compara esta estrategia con usar únicamente un árbol balanceado que almacene `(id,name)`,
8. discute el compromiso entre:
   - duplicación de estado,
   - velocidad de distintas operaciones,
   - cantidad de invariantes que deben preservarse,
9. relaciona el problema con el Reto 11 de esta lista.

