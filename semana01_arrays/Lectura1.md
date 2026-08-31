### Lectura 1 - Arreglos dinámicos: representación, correctitud y costo

Cuando resolvemos un problema mediante software no trabajamos directamente con objetos del mundo real, sino con representaciones de ellos. Una colección de estudiantes, una ruta entre ciudades, un conjunto de documentos o una secuencia de números debe convertirse en algún estado que un programa pueda almacenar y transformar. En ese sentido, una **estructura de datos** no es simplemente una clase de Java ni una colección de métodos: es una decisión acerca de cómo representar información de manera que ciertas operaciones puedan realizarse correctamente y con un costo razonable.

Esta observación será recurrente durante todo el curso. No existe una representación universalmente mejor. Una elección puede hacer que una operación resulte muy barata y que otra requiera considerablemente más trabajo. El estudio de estructuras de datos consiste precisamente en comprender esa relación entre la información que queremos manipular, la forma en que decidimos representarla, las propiedades que deben conservarse y el costo algorítmico de las operaciones.

Supongamos que queremos trabajar con una secuencia de enteros. Desde el punto de vista del usuario de la estructura podríamos desear operaciones como `size()`, `get(i)` o `add(x)`. Estas operaciones describen lo que la estructura permite hacer, pero todavía no dicen cómo se almacenarán los elementos. Aquí aparece la distinción entre **ADT frente a implementación**. Un tipo abstracto de dato, o ADT, especifica el comportamiento observable de una estructura: qué operaciones ofrece y, eventualmente, qué condiciones deben satisfacer esas operaciones. La implementación decide cómo hacer posible ese comportamiento.

Podríamos, por ejemplo, implementar una secuencia mediante nodos enlazados o mediante un arreglo. Si ambas implementaciones ofrecen las mismas operaciones observables, pueden corresponder al mismo ADT aunque internamente sean muy diferentes. Esta separación es importante porque cambiar la implementación puede modificar radicalmente el costo de una operación sin cambiar necesariamente la interfaz que utiliza el cliente.

Para nuestra primera implementación escogeremos un arreglo y un entero:

```java
private Integer[] a = new Integer[1];
private int n = 0;
```

Estas dos variables constituyen la **representación** del estado. El arreglo `a` proporciona almacenamiento físico; `n` indica cuántos elementos de ese almacenamiento pertenecen actualmente a la secuencia. A partir de una decisión aparentemente tan pequeña aparece inmediatamente una distinción conceptual fundamental.

> **Nota de implementación.** Se utiliza `Integer[]` por conveniencia didáctica y para mantener una interfaz orientada a objetos. En Java, `Integer` es un tipo envoltorio de `int` y puede implicar boxing y unboxing. Este detalle no forma parte del análisis de complejidad de esta semana.

Imaginemos que el arreglo tiene ocho posiciones pero `n` vale tres:

```text
índice     0   1   2   3   4   5   6   7
         +---+---+---+---+---+---+---+---+
a        | 4 | 7 | 1 |   |   |   |   |   |
         +---+---+---+---+---+---+---+---+

n = 3
```

Hay ocho posiciones físicas disponibles, pero la secuencia contiene únicamente tres elementos. Esto nos obliga a distinguir **tamaño frente a capacidad**. El tamaño lógico es `n`; la capacidad física es `a.length`. La estructura puede tener capacidad disponible que todavía no corresponde a elementos del ADT.

Esta diferencia explica por qué `a[6]` puede ser una posición válida para la máquina virtual de Java y, sin embargo, no ser una posición válida de nuestra secuencia. Si el tamaño es tres, los elementos lógicos ocupan solamente `a[0]`, `a[1]` y `a[2]`. Una operación como `get(6)` no debería aceptarse simplemente porque el arreglo tenga una posición física con ese índice.

La representación, por tanto, no consiste solamente en declarar variables. También necesitamos especificar qué estados de esas variables consideraremos válidos. Para esta estructura esperamos que siempre se cumpla:

```text
0 <= n <= a.length
```

y que los elementos que pertenecen a la secuencia se encuentren en:

```text
a[0..n-1]
```

Estas propiedades constituyen un **invariante** de representación. El término es importante. No describe algo que suele ser cierto ni una condición conveniente para ciertos ejemplos. Describe una propiedad que debe conservarse siempre que la estructura se encuentre en un estado válido.

Si `n = 4` y `a.length = 8`, la representación puede ser válida. Si `n = 9` y `a.length = 8`, no puede serlo: estaríamos afirmando que existen nueve elementos lógicos almacenados en ocho posiciones físicas. El **invariante** nos proporciona así una primera herramienta para razonar sobre correctitud.

Esta perspectiva cambia la forma en que debemos leer una implementación. Una operación no es correcta sólo porque produzca el resultado esperado en uno o dos ejemplos. También debemos preguntar qué estado recibe, qué modifica y si después de ejecutarse continúan siendo ciertas las propiedades de representación.

#### Comprueba tu comprensión 1

Supón que:

```text
n = 5
a.length = 8
```

1. ¿Qué posiciones pertenecen lógicamente a la estructura?
2. ¿Sería válido ejecutar `get(6)` aunque `a[6]` exista físicamente?
3. ¿Qué parte del **invariante** utilizarías para justificar tu respuesta?.

No continúes hasta poder responder las tres preguntas sin ejecutar código.

Consideremos ahora `get(i)`. Una vez comprobado que `i` pertenece al rango lógico, la operación esencial es un acceso directo:

```java
return a[i];
```

No necesitamos recorrer `a[0]`, `a[1]`, ..., `a[i-1]` para llegar a `a[i]`. Bajo el modelo usual de acceso a arreglos, el trabajo fundamental no crece cuando aumenta el número de elementos. Esta observación introduce la diferencia entre **O(1) y O(n)**.

Decir que `get(i)` es `O(1)` no significa que tarde exactamente una unidad de tiempo, que consuma una sola instrucción de máquina o que todas las computadoras lo ejecuten a la misma velocidad. Significa que, respecto del parámetro `n`, la cantidad esencial de trabajo no crece con el tamaño de la estructura.

En cambio, supongamos que buscamos un valor sin disponer de información adicional:

```java
for (int i = 0; i < n; i++) {
    if (a[i].equals(x)) {
        return i;
    }
}
```

En el peor caso podemos necesitar examinar los `n` elementos. El trabajo crece aproximadamente en proporción al tamaño de la secuencia y describimos la operación como `O(n)`. La distinción entre **O(1) y O(n)** no debe memorizarse como una tabla: debe poder justificarse examinando qué trabajo obliga a realizar la representación.

La elección del arreglo nos proporciona acceso directo, pero también introduce una limitación: en Java, la longitud de un arreglo queda fijada cuando el arreglo es creado. Si tenemos:

```text
a = [4, 7, 1, 9]
n = 4
a.length = 4
```

no existe una quinta posición donde almacenar otro elemento. Sin embargo, desde el punto de vista del ADT queremos que la secuencia pueda continuar creciendo.

Aquí aparece el **arreglo dinámico**. El nombre puede resultar engañoso si se interpreta literalmente. El arreglo individual no cambia dinámicamente de longitud. Lo dinámico es la estructura que administra sucesivos arreglos de respaldo. Cuando la capacidad existente deja de ser suficiente, la implementación crea otro arreglo, conserva los elementos lógicos y cambia la referencia utilizada como almacenamiento.

Esta transición se puede encapsular en **`resize()`**. Conceptualmente, la operación debe obtener un arreglo con capacidad apropiada, copiar los elementos existentes y hacer que `a` pase a referenciar el nuevo almacenamiento. Si antes teníamos cuatro elementos, después de **`resize()`** debemos seguir teniendo cuatro elementos. El tamaño lógico no ha cambiado; ha cambiado la capacidad disponible.

Una posible implementación es:

```java
private void resize() {
    Integer[] b = new Integer[Math.max(1, 2 * n)];

    for (int i = 0; i < n; i++) {
        b[i] = a[i];
    }

    a = b;
}
```

Este método merece ser leído como una transformación del estado, no como una receta de tres instrucciones. El nuevo arreglo `b` todavía no forma parte de la representación mientras `a` siga apuntando al arreglo anterior. El ciclo preserva los `n` elementos lógicos. La asignación `a = b` hace efectivo el cambio de almacenamiento. Durante todo el proceso `n` permanece inalterado porque **`resize()`** modifica capacidad, no tamaño.

También podemos analizar su costo. Copiar un elemento requiere trabajo constante bajo nuestro modelo, pero hay que copiar `n` elementos. Por eso **`resize()`** tiene costo `O(n)`. Una vez más, esta conclusión no procede simplemente de observar que existe un `for`; procede de identificar cuántas veces se ejecuta el trabajo relevante en función de `n`.

#### Comprueba tu comprensión 2

Supón que `resize()` crea correctamente un nuevo arreglo `b`, copia los `n` elementos y termina sin ejecutar:

```java
a = b;
```

1. ¿Seguiría siendo posible que `0 <= n <= a.length` fuese verdadero?
2. ¿Sería correcto el método?
3. ¿Qué resultado prometido por `resize()` no se habría producido?.

La pregunta distingue dos ideas que conviene mantener separadas: conservar el **invariante** y cumplir el efecto específico de una operación.

Llegamos entonces a una situación interesante. Queremos que **`add` al final** sea una operación eficiente. Mientras exista capacidad, agregar un nuevo elemento parece trivial:

```java
a[n] = x;
n++;
```

El valor se escribe en la primera posición que todavía no pertenece a la secuencia y después el tamaño aumenta en uno. Pero el orden de estas acciones importa. Antes de la inserción, `a[n]` representa precisamente la primera posición libre. Si modificáramos `n` antes de utilizarlo como índice, cambiaríamos el significado de la expresión y podríamos dejar un hueco dentro del rango lógico.

Además, esta escritura sólo es válida si existe capacidad. Una implementación de **`add` al final** debe por ello garantizar primero que `n + 1` pueda ser representado:

```java
boolean add(Integer x) {
    if (n + 1 > a.length) {
        resize();
    }

    a[n] = x;
    n++;

    return true;
}
```

Aquí el **invariante** vuelve a convertirse en una herramienta de razonamiento. Si había capacidad antes de insertar, `n < a.length`; después de incrementar `n` una sola vez seguimos teniendo `n <= a.length`. Si no había capacidad, primero ejecutamos **`resize()`**, que conserva los elementos y crea espacio suficiente, y sólo entonces incorporamos el nuevo valor. No necesitamos confiar exclusivamente en la ejecución para argumentar que el estado resultante sigue siendo válido.

Pero aparece otra pregunta. Si **`resize()`** cuesta `O(n)` y **`add` al final** puede llamar a **`resize()`**, ¿debemos concluir que insertar al final cuesta `O(n)`?

Para una llamada individual, sí puede ocurrir. Una inserción que encuentra capacidad disponible realiza trabajo constante; una inserción que provoca una expansión puede copiar todos los elementos existentes. En el peor caso de una operación aislada, el costo puede ser lineal.

Sin embargo, esa observación no describe lo que ocurre en una secuencia larga de inserciones. Aquí aparece la **idea de costo amortizado**.

Supongamos que la capacidad crece de uno en uno:

```text
1, 2, 3, 4, 5, 6, ...
```

Después de llenar una capacidad, prácticamente la siguiente inserción vuelve a obligar a crear y copiar un arreglo. A medida que la estructura crece, las copias se hacen cada vez mayores y ocurren con demasiada frecuencia.

Comparemos esa política con crecimiento geométrico:

```text
1, 2, 4, 8, 16, 32, ...
```

Cuando la capacidad aumenta de ocho a dieciséis, no volvemos a copiar en la siguiente inserción. Disponemos de varias posiciones libres. Las operaciones costosas quedan separadas por un número creciente de inserciones ordinarias.

La duplicación no es la única política posible. Un factor de crecimiento menor, por ejemplo 1.5, puede reducir la cantidad de memoria no utilizada, pero obliga a redimensionar con mayor frecuencia. Un factor mayor reduce la frecuencia de las copias, a cambio de reservar más espacio que quizá permanezca temporalmente sin usar. En esta semana utilizaremos duplicación porque hace especialmente clara la relación entre crecimiento geométrico y **idea de costo amortizado**. El factor de crecimiento es, por tanto, una decisión de diseño con un compromiso entre tiempo y espacio, no una constante universal.

Las cantidades copiadas durante las expansiones siguen aproximadamente:

```text
1 + 2 + 4 + 8 + 16 + ...
```

Si realizamos `m` inserciones, el total de elementos copiados durante todas las expansiones permanece `O(m)`. A ese trabajo debemos añadir las propias `m` escrituras de los elementos, también lineales en conjunto. Así, una secuencia de `m` inserciones requiere `O(m)` trabajo total y, distribuido entre las `m` operaciones, obtenemos `O(1)` por operación.

Ésta es la **idea de costo amortizado**: una operación individual puede ser ocasionalmente costosa aunque el costo garantizado por operación, considerado sobre una secuencia completa, permanezca constante. Por eso afirmamos que **`add` al final** tiene costo `O(1)` amortizado cuando el **arreglo dinámico** utiliza crecimiento geométrico.

Amortizado no significa "promedio" en el sentido probabilístico. No estamos suponiendo que ciertas entradas sean más frecuentes que otras ni calculando una esperanza matemática sobre una distribución. Estamos obteniendo una garantía sobre el costo agregado de una secuencia de operaciones.

###" Comprueba tu comprensión 3

Una llamada concreta a `add(x)` puede ejecutar **`resize()`** y costar `O(n)`.

1. ¿Por qué esto no contradice que **`add` al final** tenga costo `O(1)` amortizado?
2. ¿Qué cambia si la capacidad crece `1, 2, 3, 4, ...` en lugar de `1, 2, 4, 8, ...`?
3. ¿Por qué "amortizado" no significa "promedio probabilístico"?.

Intenta responder sin repetir una definición. Relaciona frecuencia de redimensionamientos, trabajo acumulado y secuencia de operaciones.

Este ejemplo permite volver al punto de partida. Una **estructura de datos** es una elección de **representación**. Esa representación determina qué estados son admisibles mediante un **invariante**, qué operaciones pueden implementarse fácilmente y qué costos aparecen. La distinción entre **ADT frente a implementación** permite separar el comportamiento que queremos ofrecer de la estrategia concreta utilizada para conseguirlo. La separación entre **tamaño frente a capacidad** surge de nuestra representación mediante un arreglo de respaldo y un tamaño lógico. La comparación entre **O(1) y O(n)** expresa cómo crece el trabajo de operaciones distintas. El **arreglo dinámico** resuelve la limitación física de una capacidad fija mediante **`resize()`**, y el crecimiento geométrico hace posible que **`add` al final** tenga un buen comportamiento a largo plazo, explicado por la **idea de costo amortizado**.

La conexión más importante de esta semana puede expresarse así:

```text
problema
   -> ADT
   -> representación
   -> invariante
   -> operaciones
   -> correctitud
   -> costo
```

No se trata, por tanto, de aprender una implementación concreta de `ArrayStack`. El propósito es adquirir una forma de leer y diseñar estructuras de datos. Frente a cualquier estructura que aparezca durante el curso deberíamos poder preguntar qué comportamiento abstracto ofrece, cómo representa su estado, qué propiedades deben permanecer verdaderas, cómo una operación transforma ese estado y cuánto trabajo exige esa transformación.

El laboratorio puede explorar operaciones adicionales, como inserción indexada, eliminación y búsqueda secuencial. Estas operaciones sirven para observar cómo la misma **representación** produce costos diferentes, pero no amplían el núcleo conceptual definido para esta lectura.

#### Alcance de la Semana 1

El núcleo de esta semana está formado por **qué es una estructura de datos**, **ADT frente a implementación**, **representación**, **tamaño frente a capacidad**, **invariante**, **O(1) y O(n)**, **arreglo dinámico**, **`resize()`**, **`add` al final** e **idea de costo amortizado**.

Quedan deliberadamente como ampliación opcional la recursión, las ecuaciones de recurrencia, la prueba formal de correctitud, el análisis agregado formal, el método contable y el método potencial para amortización, así como aspectos de Java que no son necesarios para comprender el mecanismo central: genéricos Java complejos, `ArrayList` como solución, iteradores e interfaces completas de Java Collections. Tampoco forman parte del núcleo de esta semana sorting, búsqueda binaria ni memoria JVM en profundidad.

Estos temas no se excluyen porque carezcan de importancia. Se posponen porque introducirlos ahora diluiría el objetivo principal: entender por primera vez la relación entre abstracción, representación, correctitud y complejidad a partir de una estructura suficientemente sencilla como para poder razonar sobre ella completamente.

#### Preguntas tipo control

Estas preguntas no introducen contenidos nuevos. Su objetivo es entrenar el formato de razonamiento que se utilizará en las evaluaciones escritas.

#### 1. Invariante y estado válido

Supón que una estructura tiene:

```text
a.length = 8
n = 5
```

Después de ejecutar un método, el estado queda:

```text
a.length = 8
n = 9
```

Responde:

a. ¿Qué propiedad de la **representación** se ha violado?  
b. Escribe el **invariante** correspondiente.  
c. Explica por qué el programa podría compilar y, sin embargo, la estructura ser incorrecta.

#### 2. Análisis de costo

Considera:

```java
for (int i = 0; i < n; i++) {
    b[i] = a[i];
}
```

Justifica por qué el costo es `O(n)`.

No se acepta como justificación únicamente "porque tiene un `for`". Tu explicación debe identificar:

- cuál es el parámetro que representa el tamaño del problema;
- cuántas iteraciones se realizan en función de ese parámetro;
- qué trabajo constante se realiza en cada iteración.

#### 3. Traza de crecimiento

Partiendo de:

```text
n = 0
capacidad = 1
```

y duplicando la capacidad cuando sea necesario, traza:

```text
add(4)
add(7)
add(1)
add(9)
add(6)
```

Completa una tabla con:

```text
operación | contenido lógico | n | capacidad | ¿hubo resize?
```

Finalmente responde:

a. ¿Qué inserciones ejecutaron **`resize()`**?  
b. ¿Cuál puede ser el peor costo de una llamada individual a **`add` al final**?  
c. ¿Por qué, a pesar de ello, hablamos de **idea de costo amortizado** `O(1)`?.
