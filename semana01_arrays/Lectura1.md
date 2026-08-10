### Lectura: arreglos dinámicos y costo de las operaciones

Esta lectura consolida las ideas trabajadas en la primera sesión de CC232. Su objetivo es relacionar representación, operaciones, correctitud y eficiencia en una estructura basada en arreglos.

El propósito no es memorizar una implementación particular, sino comprender por qué una estructura de datos se diseña de cierta manera y cómo esa elección afecta el costo de sus operaciones.

### 1. Una estructura de datos es una decisión de representación

Resolver un problema computacional exige representar información. La elección de esa representación no es neutral, porque determina qué operaciones son sencillas y cuáles requieren más trabajo.

Una secuencia como:

```text
4, 7, 1, 9
```

puede representarse mediante un arreglo. Esa elección permite acceder directamente a una posición, pero introduce una limitación importante, un arreglo de Java tiene una longitud fija después de su creación.

Por ello, estudiar una estructura de datos no consiste solamente en observar dónde se guardan los valores. También implica relacionar cuatro elementos:

```text
representación
operaciones
propiedades de correctitud
costo
```

Una representación es adecuada cuando permite implementar las operaciones necesarias, mantener propiedades claras y obtener costos razonables.

La misma tarea puede admitir representaciones diferentes, y esas representaciones pueden producir costos diferentes para las mismas operaciones.

#### Estructura de datos y ADT

Conviene separar el comportamiento que se desea ofrecer de la forma concreta en que se implementa.

Supongamos que queremos una secuencia con operaciones como:

```java
int size()
Integer get(int i)
boolean add(Integer x)
```

Estas operaciones describen qué puede hacer la estructura. Ese nivel corresponde al tipo abstracto de dato, ADT.

El ADT se interesa por el comportamiento observable, no por los detalles internos de almacenamiento.

La implementación responde otra pregunta, cómo se consigue ese comportamiento.

Una posible representación en Java utiliza:

```java
private Integer[] a = new Integer[1];
private int n = 0;
```

El arreglo `a` proporciona almacenamiento, mientras que `n` indica cuántos elementos pertenecen actualmente a la estructura.

La distinción puede resumirse así:

```text
ADT
qué operaciones ofrece la estructura

implementación
cómo se representan y ejecutan esas operaciones
```

Un mismo ADT puede tener implementaciones diferentes. Cambiar la representación puede cambiar también el costo de sus operaciones.

### 2. Estado lógico y almacenamiento físico

Considera el siguiente estado:

```text
índice      0   1   2   3   4   5   6   7
          +---+---+---+---+---+---+---+---+
a         | 4 | 7 | 1 |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+

n = 3
```

El arreglo contiene ocho posiciones físicas, pero solamente tres posiciones forman parte del contenido lógico de la estructura.

Los elementos válidos están en:

```text
a[0]
a[1]
a[2]
```

Las posiciones restantes existen, pero todavía no representan elementos de la secuencia.

Esta diferencia conduce a dos conceptos fundamentales.

#### Tamaño

El tamaño indica cuántos elementos pertenecen actualmente a la estructura.

```text
tamaño = n
```

#### Capacidad

La capacidad indica cuántas posiciones posee el arreglo de respaldo.

```text
capacidad = a.length
```

Para el estado anterior:

```text
tamaño = 3
capacidad = 8
```

La capacidad puede ser mayor que el tamaño. Esa diferencia deja espacio disponible para inserciones futuras sin tener que construir un arreglo nuevo en cada operación.

### 3. Invariantes y estados válidos

Una implementación necesita reglas que permitan distinguir un estado válido de uno incorrecto.

Para esta representación debe cumplirse:

```text
0 <= n <= a.length
```

Además, los elementos que pertenecen a la estructura ocupan:

```text
a[0..n-1]
```

Estas propiedades forman un invariante de representación.

Un invariante expresa una condición que debe conservarse mientras la estructura permanezca en un estado válido.

Por ejemplo:

```text
n = 4
a.length = 8
```

es compatible con el invariante.

En cambio:

```text
n = 9
a.length = 8
```

no es compatible con la representación, porque afirma que existen nueve elementos válidos dentro de un arreglo que solo dispone de ocho posiciones.

Los invariantes permiten razonar sobre correctitud. Una operación no es correcta solamente porque produzca una salida esperada en un ejemplo, también debe dejar la estructura en un estado válido.

### 4. Por qué el acceso por índice es O(1)

Los arreglos permiten acceso directo por posición.

Una operación como:

```java
Integer get(int i) {
    return a[i];
}
```

no necesita recorrer las posiciones anteriores para obtener `a[i]`.

La operación esencial es la misma si la estructura contiene diez elementos o un millón.

Por esa razón se describe como:

```text
get(i) -> O(1)
```

La notación `O(1)` expresa aquí que el trabajo fundamental no crece con el número de elementos `n`.

Esto no significa que la operación tarde literalmente una unidad de tiempo. El tiempo real depende del computador y del entorno de ejecución. La notación asintótica se concentra en cómo crece el trabajo cuando crece la entrada.

#### Un contraste con O(n)

Considera una búsqueda secuencial:

```java
int indexOf(Integer x) {
    for (int i = 0; i < n; i++) {
        if (a[i].equals(x)) {
            return i;
        }
    }
    return -1;
}
```

Si `x` se encuentra al inicio, la búsqueda puede terminar pronto. Si está al final o no existe, puede ser necesario revisar todos los elementos.

En el caso más exigente, el número de posiciones examinadas crece con `n`.

Por ello:

```text
búsqueda secuencial -> O(n)
```

Para esta semana interesa reconocer principalmente la diferencia:

```text
O(1)
el trabajo no crece con n

O(n)
el trabajo crece aproximadamente con n
```

### 5. El problema de una capacidad fija

Un arreglo de Java tiene longitud fija.

Si se crea:

```java
Integer[] a = new Integer[4];
```

ese arreglo tendrá longitud 4 durante toda su existencia.

Supongamos ahora:

```text
a = [4, 7, 1, 9]
n = 4
a.length = 4
```

La estructura está llena.

Queremos agregar:

```text
6
```

No existe una quinta posición disponible dentro del mismo arreglo.

Una estructura dinámica resuelve este problema sin cambiar la longitud del arreglo existente. En su lugar realiza tres pasos:

```text
1. crea un arreglo mayor
2. copia los elementos válidos
3. reemplaza la referencia al arreglo de respaldo
```

Por ejemplo:

```text
arreglo anterior

[4][7][1][9]

arreglo nuevo

[4][7][1][9][ ][ ][ ][ ]
```

El arreglo anterior no se expandió. Se creó otro objeto con mayor capacidad.

El término arreglo dinámico describe a la estructura que administra arreglos de respaldo, no a un arreglo individual cuya longitud cambia.

### 6. resize() como operación de mantenimiento

El crecimiento puede concentrarse en una operación auxiliar:

```java
private void resize()
```

Una implementación típica en Java crea un arreglo mayor, copia los elementos existentes y reemplaza la referencia anterior.

Por ejemplo:

```java
private void resize() {
    Integer[] b = new Integer[Math.max(1, 2 * n)];

    for (int i = 0; i < n; i++) {
        b[i] = a[i];
    }

    a = b;
}
```

La operación crea un arreglo `b`, copia los `n` elementos válidos y finalmente hace que `a` se refiera al nuevo arreglo.

Observa que `resize()` modifica la capacidad, pero no modifica el tamaño lógico.

Antes de una expansión podríamos tener:

```text
n = 4
a.length = 4
```

Después de `resize()`:

```text
n = 4
a.length = 8
```

Siguen existiendo cuatro elementos lógicos. Lo que cambió fue la cantidad de espacio disponible.

#### Costo de resize()

El ciclo de copia ejecuta una asignación por cada elemento válido:

```java
for (int i = 0; i < n; i++) {
    b[i] = a[i];
}
```

Por tanto, el trabajo crece con `n`:

```text
resize() -> O(n)
```

Este costo lineal no convierte automáticamente al arreglo dinámico en una estructura ineficiente. Para entender por qué, es necesario estudiar con qué frecuencia ocurre el redimensionamiento.

### 7. Crecimiento geométrico

Una política posible sería aumentar la capacidad en una sola posición cada vez que el arreglo se llena:

```text
1, 2, 3, 4, 5, 6, ...
```

El problema es que una secuencia grande de inserciones provocaría copias con demasiada frecuencia.

Una estrategia más adecuada consiste en multiplicar la capacidad, por ejemplo por dos:

```text
1, 2, 4, 8, 16, 32, ...
```

Después de crecer de 8 a 16, la estructura dispone de varias posiciones libres antes de necesitar otro crecimiento.

La idea importante no es únicamente reservar más memoria. El crecimiento geométrico separa las operaciones costosas de copia mediante muchas inserciones ordinarias.

Esta política es la base del análisis amortizado del arreglo dinámico.

### 8. add al final

Para insertar al final necesitamos distinguir dos situaciones.

#### Hay capacidad disponible

Si:

```text
n < a.length
```

la primera posición libre es `a[n]`.

La inserción puede realizarse con:

```java
a[n] = x;
n++;
```

Por ejemplo:

```text
antes

a = [4, 7, 1, _, _, _, _, _]
n = 3

después de add(9)

a = [4, 7, 1, 9, _, _, _, _]
n = 4
```

La operación preserva el orden lógico y actualiza el tamaño.

#### No hay capacidad disponible

Si:

```text
n == a.length
```

se necesita crecer antes de escribir el nuevo elemento.

Una posible implementación es:

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

El orden de las acciones es importante.

Primero se garantiza que exista espacio, después se escribe el elemento, finalmente se actualiza el tamaño.

Al terminar debe seguir cumpliéndose:

```text
0 <= n <= a.length
```

### 9. Costo amortizado de add

Una llamada particular a `add` puede ser barata o costosa.

Si existe capacidad disponible, la operación realiza esencialmente:

```java
a[n] = x;
n++;
```

Este trabajo es constante.

Si el arreglo está lleno, `add` llama a `resize()`, y esa operación copia `n` elementos.

Entonces aparece una pregunta natural:

```text
si algunas inserciones cuestan O(n),
por qué se afirma que insertar al final es O(1) amortizado
```

La respuesta depende de analizar una secuencia de operaciones, no una llamada aislada.

Supongamos que las capacidades siguen:

```text
1, 2, 4, 8, 16, ...
```

Para llegar a una capacidad grande, las cantidades de elementos copiadas durante expansiones sucesivas siguen aproximadamente:

```text
1 + 2 + 4 + 8 + ...
```

Esta suma crece en el mismo orden que la capacidad final.

Si realizamos `m` inserciones, el trabajo total invertido en todas las expansiones es `O(m)`.

El resto de las inserciones aporta trabajo constante por operación.

Por tanto, el costo total de `m` inserciones es lineal:

```text
O(m)
```

Al distribuir ese costo entre las `m` operaciones obtenemos:

```text
O(m) / m = O(1)
```

Por eso se dice:

```text
add al final -> O(1) amortizado
```

#### Amortizado no significa promedio probabilístico

El análisis amortizado no necesita suponer que ciertas entradas ocurren con determinada probabilidad.

Se estudia una secuencia completa de operaciones y se reparte su costo total.

Esto permite afirmar que una operación puede ser ocasionalmente costosa sin que el costo por operación de una secuencia larga deje de ser constante.

### 10. Una misma representación favorece ciertas operaciones

La representación basada en arreglos tiene una ventaja clara:

```text
get(i) -> O(1)
```

También permite agregar al final con:

```text
add(x) -> O(1) amortizado
```

Sin embargo, otras operaciones pueden requerir recorrer o desplazar elementos.

Por eso, la eficiencia de una estructura debe evaluarse en relación con las operaciones que se desean realizar.

Una representación puede ser excelente para una operación y menos adecuada para otra.

Este principio será recurrente en el estudio de estructuras de datos.

### 11. Síntesis

- Un arreglo dinámico combina una interfaz de operaciones con una representación interna basada en un arreglo de longitud fija y un tamaño lógico separado.
- La capacidad disponible evita crear un arreglo nuevo en cada inserción.
- El invariante permite razonar sobre estados válidos.
- El acceso por índice aprovecha la representación basada en arreglos y tiene costo O(1).
- El crecimiento requiere copiar elementos y una llamada a `resize()` cuesta O(n).
- La política de duplicación hace que las expansiones ocurran con poca frecuencia.
- Al analizar una secuencia larga de inserciones, el costo total de las expansiones es lineal y `add` al final tiene costo O(1) amortizado.

Estas ideas pueden resumirse así:

```text
- la representación condiciona las operaciones,
- las operaciones deben preservar invariantes,
- el costo depende del trabajo que exige cada operación.
```
