### Lectura: factor de carga, crecimiento y rehashing en tablas hash con encadenamiento

Esta lectura consolida y amplía las ideas trabajadas en la Semana 9 de CC232.

Durante las ocho semanas anteriores hemos cambiado de representación, de invariante o de ADT cuando cambió la operación que queríamos favorecer.

En la Semana 1 utilizamos un arreglo dinámico y distinguimos tamaño lógico, capacidad física y crecimiento.

En la Semana 2 utilizamos listas enlazadas y vimos que una modificación local puede ser barata cuando conocemos las referencias apropiadas.

En la Semana 3 estudiamos `Stack`, `Queue` y `Deque`, donde el ADT restringe qué posiciones pueden utilizarse.

En las Semanas 4 y 5 estudiamos árboles binarios de búsqueda. El invariante de orden permitió buscar, insertar y eliminar siguiendo caminos determinados por comparaciones.

En la Semana 6 estudiamos AVL. El BST seguía siendo correcto, pero añadimos altura y balance para impedir que la forma destruyera la eficiencia.

En la Semana 7 estudiamos una cola de prioridad y un heap binario. La pregunta pasó a ser cómo mantener disponible el elemento mínimo.

En la Semana 8 estudiamos hashing con encadenamiento separado. La pregunta fue:

```text
¿cómo localizar una clave exacta sin mantener orden global
y sin recorrer toda la colección?
```

La respuesta fue:

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

La Semana 9 no introduce una estructura nueva. Continúa con la misma tabla hash y formula una pregunta distinta:

```text
¿qué ocurre si n crece
pero la cantidad de buckets permanece fija?
```

La respuesta conduce al **factor de carga**, a una **política de crecimiento**, al cambio de capacidad y al **rehashing**.

La idea central de la semana es:

```text
n creciente + m fijo = mayor carga promedio

mayor carga + operaciones locales dentro de una bucket = más trabajo potencial

política de crecimiento + nueva capacidad + rehashing = restaurar una representación adecuada
```

El objetivo no es memorizar `loadFactor`, `shouldGrow`, `capacityFor` o `resize`. El objetivo es continuar la misma cadena conceptual usada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    qué estado almacenamos

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo modificamos o reconstruimos la representación

complejidad
    cuánto trabajo exige cada operación
```

### 1. El problema heredado de la Semana 8

En la Semana 8 vimos que una tabla hash con encadenamiento separado reduce una búsqueda global a una búsqueda local.

Si una clave `x` pertenece a:

```text
t[hash(x)]
```

entonces `find(x)` no necesita recorrer toda la tabla.

Solo recorre la bucket correspondiente.

Si esa bucket contiene `k` elementos:

```text
find(x) -> O(k)
```

Lo mismo ocurre conceptualmente con `add(x)` y `remove(x)`, porque primero localizan una bucket y después trabajan dentro de ella.

La ventaja de hashing depende de que `k` no crezca demasiado.

### 2. Una tabla correcta puede volverse menos eficiente

Considera:

```text
0: [12]
1: [5]
2: [18]
3: [7]
```

Tenemos:

```text
n = 4
m = 4
```

Si seguimos insertando sin aumentar `m`, podríamos llegar a:

```text
0: [12, 20, 28]
1: [5, 9, 17]
2: [18, 26, 34]
3: [7, 15, 23]
```

La tabla puede seguir siendo correcta, pero las búsquedas locales recorren colecciones más largas.

Por tanto:

```text
correctitud no implica eficiencia constante
```

### 3. Estado de la representación: n, m, t y d

Utilizaremos:

```text
n
    número de elementos almacenados

m
    número de buckets

t
    arreglo de buckets

d
    exponente tal que t.length = 2^d
```

También:

```text
m = t.length
```

`n` describe contenido lógico.

`t.length` describe capacidad física.

Dos tablas pueden contener exactamente las mismas claves y utilizar cantidades diferentes de buckets.

### 4. La relación t.length = 2^d

La implementación mantiene:

```text
t.length = 2^d
```

Ejemplos:

```text
d = 1 -> 2 buckets
d = 2 -> 4 buckets
d = 3 -> 8 buckets
d = 4 -> 16 buckets
d = 5 -> 32 buckets
```

Incrementar `d` en uno duplica la capacidad:

```text
2^(d + 1) = 2 * 2^d
```

Esta propiedad será importante al estudiar crecimiento geométrico.

### 5. Conexión con la Semana 1

En un arreglo dinámico distinguimos:

```text
n
    elementos lógicos

a.length
    capacidad física
```

Ahora distinguimos:

```text
n
    elementos lógicos

t.length
    cantidad de buckets físicas
```

La misma idea reaparece:

```text
estado lógico != capacidad física
```

Pero crecer una tabla hash será más delicado que crecer un arreglo.

### 6. Factor de carga

Definimos:

```text
alpha = n/m
```

En Java:

```java
static double loadFactor(int size, int capacity) {
    if (capacity <= 0) {
        throw new IllegalArgumentException(
            "La capacidad debe ser positiva"
        );
    }

    return (double) size / capacity;
}
```

El cast a `double` evita división entera.

### 7. Interpretación de alpha

Si:

```text
n = 6
m = 8
```

entonces:

```text
alpha = 6/8 = 0.75
```

No significa que cada bucket contenga `0.75` elementos.

Significa que el promedio global es:

```text
0.75 elementos por bucket
```

### 8. Alpha es la longitud promedio de bucket

Si las longitudes de las `m` buckets son:

```text
k0, k1, ..., k(m-1)
```

entonces:

```text
k0 + k1 + ... + k(m-1) = n
```

Por tanto:

```text
promedio
=
(k0 + k1 + ... + k(m-1)) / m
=
n/m
=
alpha
```

Esta relación es natural en encadenamiento separado.

### 9. Factor de carga no es distribución

Dos tablas pueden tener:

```text
n = 8
m = 8
alpha = 1
```

Primera:

```text
0: [8]
1: [17]
2: [26]
3: [35]
4: [44]
5: [53]
6: [62]
7: [71]
```

Segunda:

```text
0: [8, 16, 24, 32, 40, 48, 56, 64]
1: []
2: []
3: []
4: []
5: []
6: []
7: []
```

El factor de carga es igual, pero la distribución es muy diferente.

Por tanto:

```text
mismo alpha
no implica
misma distribución
```

### 10. Función hash y factor de carga

La función hash responde:

```text
¿cómo se reparten las claves entre las buckets?
```

El factor de carga responde:

```text
¿cuántos elementos existen respecto de cuántas buckets?
```

Entonces:

```text
función hash
    influye en distribución

factor de carga
    mide ocupación global
```

Una tabla eficiente necesita ambas condiciones razonables.

### 11. Colisiones siguen siendo normales

Una colisión ocurre cuando:

```text
x != y
hash(x) = hash(y)
```

En encadenamiento separado, ambas claves pueden almacenarse en la misma bucket. La meta no es eliminar todas las colisiones.

La meta es evitar concentraciones que hagan crecer demasiado las colecciones locales.

### 12. Alpha puede ser mayor que 1

Como una bucket puede contener varias claves, es posible:

```text
n > m
```

y por tanto:

```text
alpha > 1
```

Ejemplo:

```text
n = 10
m = 8
alpha = 1.25
```

La tabla todavía puede ser correcta.

Pero ya no satisface la política de carga que usaremos.

### 13. Correctitud y política de rendimiento

Debemos separar:

```text
correctitud
    cada clave está en la bucket que corresponde
    n coincide con el contenido
    no hay duplicados

política de rendimiento
    n/m se mantiene dentro
    de un rango deseado
```

Superar un umbral no destruye necesariamente el ADT.

Indica que la implementación decidió reconstruir para conservar eficiencia esperada.

### 14. Política de crecimiento

La semana utiliza:

```java
static boolean shouldGrow(int size, int capacity) {
    return loadFactor(size + 1, capacity) > 1.0;
}
```

La expresión importante es:

```text
size + 1
```

El método no pregunta solamente cómo está la tabla ahora.

Pregunta:

```text
¿qué carga tendría la tabla después de insertar un elemento más sin cambiar la capacidad?
```

### 15. Mirar el estado futuro

Si:

```text
size = 8
capacity = 8
```

actualmente:

```text
alpha = 1
```

pero una inserción adicional produciría:

```text
(8 + 1)/8 = 1.125
```

Entonces:

```text
shouldGrow(8, 8) -> true
```

La decisión se toma antes de modificar el contenido.

### 16. El operador > forma parte de la política

La condición usa:

```java
> 1.0
```

No usa:

```java
>= 1.0
```

Por tanto, una inserción que deja:

```text
alpha = 1
```

todavía está permitida.

La tabla crece cuando la siguiente inserción produciría:

```text
alpha > 1
```

Cambiar `>` por `>=` cambia la política.

### 17. El umbral no es universal

El valor `1.0` es una decisión didáctica apropiada para esta tabla con encadenamiento. No debe interpretarse como una ley universal de todas las tablas hash.

La idea general es:

```text
umbral + estado futuro -> decisión de crecimiento
```

### 18. Elegir la siguiente capacidad

`capacityFor(size)` debe retornar:

```text
una potencia de dos estrictamente mayor que size
```

con:

```text
capacidad mínima = 2
```

Por ejemplo:

```text
capacityFor(0) -> 2
capacityFor(1) -> 2
capacityFor(2) -> 4
capacityFor(3) -> 4
capacityFor(7) -> 8
capacityFor(8) -> 16
```

### 19. Por qué "estrictamente mayor" importa

Para:

```text
size = 8
```

el valor:

```text
8
```

es una potencia de dos, pero no cumple:

```text
capacity > size
```

Por eso:

```text
capacityFor(8) = 16
```

### 20. Construcción iterativa de capacityFor

Podemos comenzar con:

```text
capacity = 2
```

y duplicar:

```text
2, 4, 8, 16, 32, ...
```

Una implementación es:

```java
static int capacityFor(int size) {
    int capacity = 2;

    while (capacity <= size) {
        capacity *= 2;
    }

    return capacity;
}
```

Durante todo el ciclo:

```text
capacity sigue siendo potencia de dos
```

### 21. El problema adicional de una tabla hash

En un arreglo dinámico, un elemento almacenado en:

```text
a[3]
```

puede copiarse a:

```text
b[3]
```

En una tabla hash, una clave pertenece a una bucket porque:

```text
hash(x)
```

produce ese índice.

Si cambia la configuración de hashing, la bucket correcta puede cambiar.

### 22. La función hash depende de d

La implementación usa:

```java
private int hash(Object x) {
    return (z * x.hashCode()) >>> (W - d);
}
```

Por tanto:

```text
hash(x)
```

depende de:

```text
x.hashCode()
```

y también de:

```text
d
```

Si `d` cambia, el índice puede cambiar.

### 23. hashCode e índice no son lo mismo

Una clave puede conservar exactamente el mismo:

```text
x.hashCode()
```

antes y después del crecimiento.

Pero la transformación posterior usa un `d` distinto.

Por eso:

```text
mismo hashCode no implica misma bucket después de resize
```

### 24. Por qué copiar las buckets no funciona

Supongamos que pasamos de:

```text
d = 3
t.length = 8
```

a:

```text
d = 4
t.length = 16
```

Una estrategia incorrecta sería copiar:

```text
t[0] antigua -> t[0] nueva
t[1] antigua -> t[1] nueva
...
```

Puede conservar todas las claves físicamente, pero no garantiza:

```text
x pertenece a t[hash(x)]
```

con el nuevo `d`.

### 25. Una clave puede existir y find puede no encontrarla

Si una clave `x` queda físicamente en:

```text
t[2]
```

pero con el nuevo `d`:

```text
hash(x) = 9
```

entonces `find(x)` consultará:

```text
t[9]
```

La clave existe físicamente, pero la estructura es incorrecta.

Esta observación muestra el poder de los invariantes.

### 26. Rehashing

Llamamos **rehashing** a reconstruir la tabla bajo una nueva configuración.

Conceptualmente:

```text
recordar tabla antigua
->
elegir nuevo d
->
crear nuevas buckets
->
recorrer elementos antiguos
->
recalcular hash(x)
->
reinsertar en la bucket nueva
```

El conjunto lógico no cambia.

La representación física sí.

### 27. Conservar la tabla antigua

Antes de reemplazar `t` necesitamos:

```java
List<T>[] oldTable = t;
```

Así podemos construir una nueva tabla y todavía recorrer los elementos existentes.

Durante la reconstrucción tenemos:

```text
oldTable
    representación anterior

t
    representación nueva
```

### 28. Elegir el nuevo d

El contrato del crecimiento es elegir el menor `d` tal que:

```text
2^d > n
```

Conceptualmente:

```text
d = 1

mientras 2^d <= n
    d++
```

Al terminar:

```text
2^d > n
```

### 29. Crear nuevas buckets

Después:

```java
t = allocateTable(1 << d);
```

Ahora:

```text
t.length = 2^d
```

pero la tabla nueva está vacía.

Todavía debemos restaurar su contenido lógico.

### 30. Reinsertar con el d nuevo

Debemos visitar cada elemento antiguo y recalcular su índice.

Una forma es:

```java
for (List<T> chain : oldTable) {
    for (T x : chain) {
        t[hash(x)].add(x);
    }
}
```

En ese momento `hash(x)` utiliza el `d` nuevo.

Eso es rehashing.

### 31. n no debe cambiar durante resize

Si antes de `resize`:

```text
n = 8
```

después debe seguir:

```text
n = 8
```

Los ocho elementos ya existían.

Solo cambiaron de ubicación.

Por tanto:

```text
resize cambia representación pero no cambia contenido lógico
```

### 32. Inserción lógica y reinserción física

Debemos distinguir:

```text
add(x) público
    incorpora una clave nueva
    n puede aumentar

reinserción durante resize
    mueve una clave existente
    n no aumenta
```

Esta diferencia es central.

### 33. Por qué usar add(x) dentro de resize puede ser incorrecto

Si hacemos:

```java
for (List<T> chain : oldTable) {
    for (T x : chain) {
        add(x);
    }
}
```

cada llamada exitosa puede ejecutar:

```text
n++
```

Entonces el contador dejará de coincidir con el número real de elementos.

Además, la operación pública puede volver a activar la política de crecimiento.

La reconstrucción debe separar responsabilidades.

### 34. Invariantes de la Semana 9

Después de cada operación válida debe cumplirse:

```text
1. t.length = 2^d

2. para todo x almacenado:
   x pertenece a t[hash(x)]
   usando el d actual

3. n =
   suma de tamaños
   de todas las buckets

4. no existen claves duplicadas
```

Además, la política de inserción busca conservar:

```text
n <= t.length
```

después de cada inserción pública.

### 35. find confía en el invariante de ubicación

Una implementación es:

```java
T find(Object x) {
    for (T y : t[hash(x)]) {
        if (y.equals(x)) {
            return y;
        }
    }

    return null;
}
```

`find` no revisa todas las buckets.

Confía en:

```text
si x existe
entonces está en t[hash(x)]
```

Ese invariante permite eficiencia.

### 36. add y el orden de sus pasos

Una versión coherente es:

```java
boolean add(T x) {
    if (find(x) != null) {
        return false;
    }

    if (n + 1 > t.length) {
        resize();
    }

    t[hash(x)].add(x);
    n++;
    return true;
}
```

La secuencia es:

```text
comprobar duplicado
->
comprobar crecimiento
->
resize si corresponde
->
calcular hash con el d vigente
->
insertar
->
n++
```

El orden es parte de la correctitud.

### 37. Insertar después del posible resize

La nueva clave se inserta después de reconstruir.

Esto importa porque:

```text
resize puede cambiar d
```

y por tanto:

```text
resize puede cambiar hash(x)
```

La nueva clave debe usar la configuración vigente.

### 38. remove localiza y modifica una sola bucket

Una implementación es:

```java
T remove(T x) {
    Iterator<T> it = t[hash(x)].iterator();

    while (it.hasNext()) {
        T y = it.next();

        if (y.equals(x)) {
            it.remove();
            n--;
            return y;
        }
    }

    return null;
}
```

La lógica es:

```text
localizar bucket
->
confirmar igualdad
->
eliminar
->
n--
```

Si la clave no existe, `n` no cambia.

### 39. La versión de la semana no reduce capacidad al eliminar

En esta implementación:

```text
remove
```

puede disminuir `n`, pero:

```text
t.length
```

permanece igual.

La Semana 9 se concentra en crecimiento y rehashing.

Una política de contracción puede estudiarse posteriormente.

### 40. Costo local O(k)

Sea:

```text
k
```

la longitud de la bucket seleccionada.

Entonces:

```text
find   -> O(k)
remove -> O(k)
add    -> O(k)
```

ignorando por un momento el costo eventual de `resize`.

### 41. Peor caso O(n)

Si todas las claves caen en la misma bucket:

```text
k = n
```

Entonces:

```text
O(k) = O(n)
```

Por eso no debemos afirmar sin condiciones:

```text
hashing = O(1)
```

### 42. Costo esperado cercano a O(1)

Si:

```text
la función hash distribuye razonablemente
```

y:

```text
la carga permanece controlada
```

esperamos que `k` permanezca pequeño.

Entonces:

```text
find
add
remove
->
O(1) esperado
```

La palabra **esperado** es fundamental.

### 43. resize cuesta O(n)

Durante `resize` debemos visitar todos los elementos existentes.

Por tanto:

```text
resize -> O(n)
```

Una inserción que provoca crecimiento puede ser mucho más cara que una inserción ordinaria.

### 44. Crecimiento geométrico

Las capacidades evolucionan como:

```text
2
4
8
16
32
64
...
```

Las reconstrucciones no ocurren después de cada inserción.

Sus costos forman una suma semejante a:

```text
2 + 4 + 8 + 16 + ...
```

Esta suma geométrica es del mismo orden que su término mayor.

### 45. Análisis amortizado intuitivo

Aunque una reconstrucción individual cuesta:

```text
O(n)
```

el costo total de muchas reconstrucciones sobre una secuencia larga de inserciones sigue siendo lineal.

Al repartirlo entre muchas inserciones obtenemos:

```text
O(1) amortizado
```

como costo adicional de crecimiento por inserción.

Esto no significa que cada llamada individual cueste `O(1)`.

### 46. Esperado y amortizado no son lo mismo

```text
esperado
    depende de la distribución
    de las claves entre buckets

amortizado
    depende de repartir
    reconstrucciones ocasionales
    entre muchas operaciones
```

Una misma tabla hash puede necesitar ambas ideas simultáneamente.

### 47. Conexión con la Semana 1

En la Semana 1:

```text
resize de arreglo
    O(n)

muchas inserciones
    O(1) amortizado
```

En la Semana 9:

```text
rehashing
    O(n)

muchas inserciones
    O(1) amortizado adicional
```

La diferencia es que una tabla hash debe recalcular ubicaciones.

### 48. Copia física y reconstrucción semántica

Podemos resumir:

```text
arreglo dinámico
    crear almacenamiento mayor
    copiar posiciones

tabla hash
    crear almacenamiento mayor
    recalcular ubicaciones
    reconstruir la representación
```

Rehashing no es solamente copiar memoria.

### 49. Postcondición fuerte de resize

Sea `S` el conjunto lógico antes de `resize`.

Después debe cumplirse:

```text
S_antes = S_después

n_antes = n_después

t.length = 2^d

para todo x en S:
    x pertenece a t[hash(x)]
    usando el d nuevo
```

Esta especificación es más importante que memorizar líneas de Java.

### 50. Implementación conceptual de resize

Una implementación coherente es:

```java
private void resize() {
    List<T>[] oldTable = t;

    d = 1;
    while ((1L << d) <= n) {
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

Observa:

```text
d cambia antes de recalcular ubicaciones

t se reemplaza por una tabla nueva

n no se modifica
```

### 51. El significado de 1L << d

La expresión:

```java
1L << d
```

representa:

```text
2^d
```

El literal `1L` realiza el desplazamiento usando un valor `long`.

Para la semana, el concepto importante sigue siendo la relación:

```text
capacidad = 2^d
```

### 52. allocateTable crea estructura, no contenido

`allocateTable(size)` crea las buckets vacías.

No modifica:

```text
n
```

La reconstrucción del contenido ocurre después.

Así se separan dos responsabilidades:

```text
crear almacenamiento
```

y:

```text
restaurar elementos
```

### 53. Errores frecuentes en resize

#### Usar el d antiguo

Si los índices se calculan antes de actualizar `d`, las ubicaciones corresponden a la tabla vieja.

#### Incrementar n

Reinsertar elementos existentes no debe modificar el tamaño lógico.

#### Omitir una bucket

Se perderían elementos durante la reconstrucción.

#### Copiar buckets completas

Las claves pueden quedar en posiciones incompatibles con el nuevo `hash(x)`.

### 54. Cómo validar un rehashing

Después de `resize`, pregunta:

```text
¿t.length = 2^d?

¿cada x está en t[hash(x)]?

¿n coincide con la suma de tamaños?

¿se conservaron todas las claves?

¿apareció algún duplicado?
```

Estas comprobaciones expresan invariantes, no detalles accidentales de implementación.

### 55. Tabla hash frente a AVL

Un AVL ofrece:

```text
find
add
remove
->
O(log n) peor caso
```

y mantiene orden.

Una tabla hash con encadenamiento busca:

```text
find
add
remove
->
O(1) esperado
```

pero no mantiene orden global.

La elección depende de las operaciones que necesitamos.

### 56. Tabla hash frente a heap

Un heap binario favorece:

```text
consultar o retirar el mínimo
```

Una tabla hash favorece:

```text
localizar una clave exacta
```

Buscar una clave arbitraria en un heap puede requerir `O(n)`.

La tabla hash sacrifica orden global para favorecer localización exacta esperada.

### 57. La secuencia conceptual de las primeras nueve semanas

```text
Semana 1
arreglo dinámico
->
capacidad y crecimiento

Semana 2
listas enlazadas
->
modificación mediante referencias

Semana 3
Stack, Queue, Deque
->
restricciones del ADT

Semana 4
BST
->
búsqueda mediante orden

Semana 5
eliminación BST
->
reconexión preservando estructura

Semana 6
AVL
->
altura, balance y rotaciones

Semana 7
heap binario
->
forma completa + prioridad

Semana 8
hashing
->
clave -> bucket

Semana 9
factor de carga
->
crecimiento -> rehashing
```

### 58. Cadena ADT, representación, invariante, algoritmo y complejidad

#### ADT

```text
find
add
remove
size
```

#### Representación

```text
t
d
n
colecciones locales
```

#### Invariantes

```text
t.length = 2^d

x pertenece a t[hash(x)]

n coincide con el contenido total

no hay duplicados
```

#### Algoritmos

```text
find
add
remove
resize
rehashing
```

#### Complejidad

```text
operación local O(k)

peor caso O(n)

operaciones O(1) esperadas
bajo condiciones razonables

resize O(n)

crecimiento O(1) amortizado
```

### 59. Distinciones que deben quedar claras

```text
n != t.length
```

```text
hashCode != índice de bucket
```

```text
factor de carga != calidad de distribución
```

```text
correctitud != política de rendimiento
```

```text
inserción lógica != reinserción física
```

```text
copiar !=rehashing
```

```text
esperado !=amortizado
```
### 60. Síntesis

La Semana 8 mostró cómo una función hash permite reducir una búsqueda global a una búsqueda dentro de una bucket.

La Semana 9 estudia qué debemos hacer cuando esa representación comienza a cargarse.

El problema no es solamente disponer de más memoria. La ubicación de cada clave depende de la configuración de la tabla.

Por eso el crecimiento exige reconstrucción.

La secuencia final es:

```text
clave
->
hashCode
->
hash con d actual
->
bucket

n/m
->
factor de carga
->
política de crecimiento

crecimiento
->
nuevo d
->
nueva capacidad
->
rehashing

rehashing correcto
->
mismo conjunto lógico
+
n preservado
+
invariantes restaurados
```

La idea final es:

```text
una estructura de datos eficiente
no solo necesita una representación correcta

también necesita una política
para mantener esa representación
dentro de condiciones razonables
a medida que el estado crece
```

Esta idea conecta directamente con la Semana 1 y prepara la Semana 10, donde utilizaremos las abstracciones `Map` y `Set` apoyándonos en las propiedades de las tablas hash que ahora ya podemos razonar desde su representación interna.
