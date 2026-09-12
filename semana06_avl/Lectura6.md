### Lectura: árboles AVL, altura, balance y rotaciones

Esta lectura consolida y amplía las ideas trabajadas en la Semana 6 de CC232. Durante las dos semanas anteriores estudiamos árboles binarios de búsqueda. En la Semana 4 utilizamos un invariante de orden (subárbol izquierdo con claves menores, subárbol derecho con claves mayores) para buscar e insertar. En la Semana 5 aprendimos a eliminar una clave sin perder subárboles que debían permanecer y sin romper ese mismo invariante. Al terminar esa semana podíamos justificar que `findLast`, `add` y `remove` cuestan **O(h)**, donde `h` representa la altura del árbol.

Quedó, sin embargo, una pregunta abierta: ¿qué ocurre si el BST es correcto pero su altura crece demasiado? La Semana 6 responde esa pregunta. Un árbol **AVL** sigue siendo un árbol binario de búsqueda; no cambia el significado básico del conjunto ordenado. Lo que cambia es la representación y el conjunto de invariantes que debemos mantener. Ahora cada nodo almacenará información sobre su altura, y exigiremos una relación entre la altura de sus dos subárboles. Cuando una inserción rompa esa relación, utilizaremos modificaciones locales llamadas **rotaciones**.

La idea central de la semana puede anticiparse así: un BST mantiene orden; un AVL mantiene orden, mantiene alturas y mantiene balance. El objetivo no es memorizar cuatro casos llamados LL, RR, LR y RL, sino comprender la misma cadena conceptual utilizada desde la primera semana, qué **ADT** queremos ofrecer, qué **representación** almacena el estado, qué **invariante** debe permanecer verdadero, qué **algoritmo** modifica esa representación, y cuánto trabajo exige, su **complejidad**.

En esta lectura utilizaremos la misma convención que los archivos Java de la Semana 6: `height(null) = 0`, la altura de una hoja es 1, y `balanceFactor(u) = height(u.left) - height(u.right)`. Esta convención debe conservarse durante toda la semana.

### 1. El problema que quedó abierto en la Semana 5

Un BST puede ser completamente correcto y tener una forma poco conveniente. Insertando `40, 20, 60, 10, 30, 50, 70` podemos obtener el árbol balanceado de siete nodos con `h = 3`. Insertando las mismas claves ya ordenadas, `10, 20, 30, 40, 50, 60, 70`, podemos obtener una cadena degenerada con `h = 7`, que se parece estructuralmente a una lista enlazada. Ambos árboles satisfacen `izquierda < nodo < derecha`; no existe ninguna violación del invariante BST, solo una diferencia de forma.

### 2. Mismas claves, distinta forma

Los dos árboles anteriores contienen exactamente las mismas claves y el mismo recorrido inorder, `10 20 30 40 50 60 70`; el conjunto lógico es el mismo. En el árbol balanceado, buscar 70 sigue `40 -> 60 -> 70`; en la cadena degenerada, sigue los siete nodos en orden. Esto muestra que dos BST con el mismo contenido pueden tener costos muy diferentes: la historia de inserciones influye en la forma, la forma influye en la altura, y la altura influye en el costo.

### 3. Repaso de altura h

La altura mide qué tan largo puede ser un camino descendente dentro del árbol. Usaremos `height(null) = 0` y `height(hoja) = 1`. Para la cadena `10 -> 20 -> 30` (de abajo hacia arriba), `height(10) = 1`, `height(20) = 2`, `height(30) = 3`. La altura del árbol es la altura de su raíz.

### 4. La altura controla el costo del BST

En un BST, una búsqueda sigue un camino; si el árbol tiene altura `h`, la longitud de ese camino está acotada por `h`. Por eso búsqueda, inserción y eliminación en un BST cuestan **O(h)**. Si `h = O(log n)`, esas operaciones pueden ser logarítmicas, pero un BST simple no garantiza esa relación: puede ocurrir `h = O(n)` y las operaciones pueden degradarse a tiempo lineal.

### 5. Correctitud y eficiencia estructural son problemas diferentes

Conviene separar dos preguntas: ¿el árbol representa correctamente un conjunto ordenado?, y ¿la forma del árbol permite mantener caminos suficientemente cortos? Un BST degenerado puede responder correctamente la primera pregunta y mal la segunda: puede tener orden correcto, claves distintas y todos los nodos alcanzables, y aun así tener `h = O(n)`. AVL agrega una condición para tratar la segunda dimensión.

### 6. AVL no reemplaza el BST

Un árbol AVL sigue satisfaciendo el invariante de búsqueda binaria: para cada nodo `u`, toda clave de `u.left` es menor que `u.x`, y toda clave de `u.right` es mayor. La búsqueda sigue utilizando las mismas comparaciones de siempre (menor va a la izquierda, mayor va a la derecha, igual se encuentra); AVL no cambia esa lógica, agrega información y restricciones sobre la forma. Podemos resumirlo así: **AVL = BST + altura almacenada + invariante de balance + operaciones de reparación**.

### 7. Una nueva representación: el campo height

En las Semanas 4 y 5 utilizamos nodos con `x`, `left`, `right` y `parent`. En los archivos de esta semana utilizaremos una representación didáctica distinta:

```java
static class Node {
    int x;
    int height = 1;
    Node left;
    Node right;

    Node(int x) {
        this.x = x;
    }
}
```

Aparece un nuevo campo, `height`, que forma parte del estado de la representación. Por tanto, ya no basta con que `left` y `right` sean correctos; también debemos mantener `height`. Cuando un nodo acaba de crearse como hoja (`left = null`, `right = null`), por nuestra convención `height = 1`, y esa inicialización es correcta. Pero después de conectar hijos, el valor puede necesitar cambiar.

### 8. height es información derivada

`height` no es un dato independiente al que podamos asignarle cualquier valor; su valor está determinado por `u.left`, `u.right` y las alturas de esos subárboles. Por eso decimos que es información derivada. La relación correcta es `u.height = 1 + max(height(u.left), height(u.right))`. Esto introduce otro invariante de representación.

Considera `20(izq 10, der 30)`, donde el invariante BST y la forma son correctos, y las alturas correctas serían `height(10) = 1`, `height(30) = 1`, `height(20) = 2`. Si en cambio almacenamos `20.height = 7`, el árbol todavía puede producir un inorder correcto, pero el estado AVL es incorrecto. Esta observación es importante: una salida visible correcta no implica una representación interna correcta.

### 9. Las funciones height, updateHeight y balanceFactor

El archivo de la semana centraliza la convención `null -> 0`, `nodo real -> altura almacenada` mediante una función auxiliar:

```java
static int height(Node u) {
    return u == null ? 0 : u.height;
}
```

Esto permite escribir las demás operaciones sin repetir comprobaciones especiales para hijos nulos. Si conocemos las alturas correctas de los hijos, la altura del nodo se calcula con una cantidad constante de información: por ejemplo, si `height(u.left) = 3` y `height(u.right) = 1`, entonces `u.height = 1 + max(3, 1) = 4`. La operación correspondiente es:

```java
static void updateHeight(Node u) {
    u.height =
        1 + Math.max(
            height(u.left),
            height(u.right)
        );
}
```

`updateHeight` no recorre todo el subárbol, solo lee las alturas de dos hijos y escribe un nuevo valor, así que `updateHeight -> O(1)` si las alturas de los hijos ya son correctas.

Supongamos que modificamos primero un hijo profundo: su altura puede cambiar, entonces su padre puede necesitar una nueva altura, y después el abuelo puede necesitar otra actualización. La dependencia va del hijo al padre y de ahí al abuelo, por eso el mantenimiento natural ocurre desde abajo hacia arriba; no debemos actualizar un ancestro usando alturas antiguas de sus hijos.

Una vez que almacenamos alturas, podemos comparar los dos lados de un nodo con el **factor de balance**, `balanceFactor(u) = height(u.left) - height(u.right)`:

```java
static int balanceFactor(Node u) {
    return u == null
        ? 0
        : height(u.left) - height(u.right);
}
```

### 10. Interpretar el signo del factor de balance

Si `balanceFactor(u) = 0`, los dos subárboles tienen la misma altura. Si es 1, el lado izquierdo es un nivel más alto; si es -1, el lado derecho es un nivel más alto; si es 2, la izquierda es dos niveles más alta; si es -2, la derecha es dos niveles más alta. La convención de esta semana es siempre izquierda menos derecha.

### 11. El invariante AVL

Un nodo está balanceado según AVL cuando `|balanceFactor(u)| <= 1`, es decir, cuando el factor pertenece a `{-1, 0, 1}`. No basta con verificar la raíz, el invariante debe cumplirse en todos los nodos.

Balanceado no significa perfectamente simétrico. En `30(izq 20(izq 10), der 40)`, las alturas son `height(10) = 1`, `height(20) = 2`, `height(40) = 1`, `height(30) = 3`, y `balanceFactor(30) = 2 - 1 = 1`. El árbol no es perfectamente simétrico, pero la raíz satisface AVL, porque AVL permite una diferencia de un nivel.

En cambio, un BST puede ser válido y no ser AVL. En la cadena `30(izq 20(izq 10))`, el orden es correcto (`10 < 20 < 30`), pero `height(30.left) = 2` y `height(30.right) = 0`, así que `balanceFactor(30) = 2`, y `|2| > 1` no es AVL. Un BST válido no implica un AVL válido.

En esta implementación debemos preservar simultáneamente el orden BST, la altura almacenada correcta, el balance AVL, la unicidad de claves y la alcanzabilidad de todos los nodos. Si una operación rompe cualquiera de estas propiedades, la estructura queda incorrecta.

### 12. Por qué una inserción puede romper el balance

Una inserción AVL comienza igual que una inserción BST: la nueva clave se coloca como hoja en una referencia `null`. Por ejemplo, `30(izq 20)` es AVL; al insertar 10 obtenemos `30(izq 20(izq 10))`. La clave 10 se encuentra en una posición correcta respecto del invariante BST, no hay error de orden, pero la altura izquierda de 30 aumentó, y ahora `balanceFactor(30) = 2`. La inserción preservó BST pero rompió AVL.

Cuando insertamos una nueva hoja, no cambia todo el árbol; solo pueden cambiar las alturas de los nodos que están en el camino desde la nueva hoja hacia la raíz. Los nodos de otros subárboles conservan exactamente la misma forma, así que no necesitamos recorrer todos los nodos después de cada inserción, solo revisar los ancestros afectados.

No queremos solucionar el problema eliminando claves o cambiando sus valores arbitrariamente; queremos conservar las mismas claves y el mismo orden lógico, pero permitir una forma diferente. Eso conduce a las rotaciones.

### 13. Rotación como transformación local

Una rotación modifica una región pequeña del árbol, no reconstruye toda la estructura; su objetivo es cambiar relaciones padre-hijo localmente, de modo que una raíz local se convierta en otra, pero el conjunto de claves del subárbol debe ser el mismo. En un BST con claves distintas, el recorrido inorder produce las claves en orden creciente; si una transformación conserva el mismo orden inorder, puede cambiar la forma sin cambiar el orden lógico del conjunto. Esta idea permite justificar las rotaciones.

### 14. Anatomía de una rotación derecha

Considera:

```text
    y
   / \
  x   C
 / \
A   B
```

Las relaciones de orden son A < x < B < y < C, donde `A`, `B` y `C` representan subárboles completos. La rotación derecha alrededor de `y` produce:

```text
  x
 / \
A   y
   / \
  B   C
```

El hijo izquierdo `x` sube, y la antigua raíz local `y` baja hacia la derecha, pero el subárbol `B` debe permanecer. Antes, `B = x.right`; después, `x.right` debe apuntar a `y`. Si sobrescribimos esa referencia sin conservar `B`, podemos perder todo el subárbol; por eso primero necesitamos recordar `middle = x.right`, y después hacer `x.right = y; y.left = middle;`.

¿Por qué `middle` pasa a `y.left`? Las claves de `middle` estaban entre `x` e `y` (`x < middle < y`). Después de que `y` se convierta en hijo derecho de `x`, esas claves deben quedar a la derecha de `x` y a la izquierda de `y`, y la posición `y.left` es exactamente la posición compatible con el invariante BST.

El inorder antes de la rotación es `A, x, B, y, C`, y después de la rotación sigue siendo `A, x, B, y, C`. La forma cambia, el orden no cambia.

```java
static Node rotateRight(Node y) {
    Node x = y.left;
    Node middle = x.right;

    x.right = y;
    y.left = middle;

    updateHeight(y);
    updateHeight(x);

    return x;
}
```

Para ejecutar `rotateRight(y)` debe existir `y.left`, porque ese nodo será promovido; si `y.left == null`, no existe el nodo `x` necesario para la transformación. En esta semana, `rebalance` llamará a la rotación solo en configuraciones donde la precondición sea válida.

Después de la rotación, `y` queda debajo de `x`, así que la nueva altura de `x` depende de la nueva altura de `y`; por eso `updateHeight(y); updateHeight(x);` y no al revés. La regla general es actualizar primero el nodo que quedó más abajo. Y como la raíz local pasó de ser `y` a ser `x`, el método retorna `x`; esto no es un detalle secundario, el código que llamó a la rotación debe saber qué nodo representa ahora el subárbol completo.

### 15. Rotación izquierda

La rotación izquierda es simétrica. Antes:

```text
  x
 / \
A   y
   / \
  B   C
```

Después:

```text
    y
   / \
  x   C
 / \
A   B
```

Ahora `middle = y.left`, y ese subárbol debe convertirse en `x.right`. El inorder antes y después sigue siendo `A, x, B, y, C`; otra vez, forma distinta, mismo orden BST.

```java
static Node rotateLeft(Node x) {
    Node y = x.right;
    Node middle = y.left;

    y.left = x;
    x.right = middle;

    updateHeight(x);
    updateHeight(y);

    return y;
}
```

Primero se actualiza `x` porque queda más abajo; después se actualiza `y` porque se convierte en la nueva raíz local.

### 16. Costo de una rotación

Una rotación realiza una cantidad constante de trabajo: modifica unas pocas referencias, actualiza dos alturas, y no recorre todos los nodos del subárbol. Por tanto, `rotateRight -> O(1)` y `rotateLeft -> O(1)`. Esto no significa que una inserción AVL completa sea O(1); la rotación es solamente la reparación local. La misma separación vista en semanas anteriores reaparece: localizar el punto relevante depende de `h`, modificar localmente puede ser **O(1)** una vez que tenemos las referencias correctas, igual que en la lista doblemente enlazada de la Semana 2.

### 17. Los cuatro patrones de desbalance

Para comprender el rebalanceo debemos observar dos decisiones: ¿el nodo desbalanceado está cargado a izquierda o a derecha?, y ¿dentro del hijo relevante la rama alta continúa en la misma dirección o cambia de dirección? De esas dos preguntas surgen los patrones **LL**, **RR**, **LR** y **RL**.

**Caso LL.** Insertando `30, 20, 10` obtenemos `30(izq 20(izq 10))`, con `balanceFactor(20) = 1` y `balanceFactor(30) = 2`. 30 está demasiado cargado hacia la izquierda, y su hijo izquierdo también está cargado hacia la izquierda (izquierda → izquierda), de ahí el nombre LL. Aplicamos `rotateRight(30)` y obtenemos `20(izq 10, der 30)`, con todas las alturas y factores válidos.

**Caso RR.** Insertando `10, 20, 30` obtenemos la cadena `10 -> 20 -> 30` hacia la derecha, con `balanceFactor(20) = -1` y `balanceFactor(10) = -2`. La rama alta sigue derecha → derecha, el caso simétrico de LL. Aplicamos `rotateLeft(10)` y obtenemos `20(izq 10, der 30)`.

**Caso LR.** Insertando `30, 10, 20` obtenemos `30(izq 10(der 20))`. La raíz 30 está cargada hacia izquierda (`balanceFactor(30) = 2`), pero su hijo izquierdo 10 está cargado hacia derecha (`balanceFactor(10) = -1`); la dirección cambia, izquierda → derecha. La rama problemática no está alineada, y el nodo intermedio 20 debe convertirse en la raíz local final. Primero aplicamos `rotateLeft(10)`, obteniendo `30(izq 20(izq 10))`, que ya es un caso LL; después aplicamos `rotateRight(30)`, y obtenemos `20(izq 10, der 30)`. Por tanto, LR es una rotación izquierda sobre el hijo seguida de una rotación derecha sobre el nodo.

**Caso RL.** Insertando `10, 30, 20` obtenemos `10(der 30(izq 20))`. La raíz 10 está cargada hacia derecha (`balanceFactor(10) = -2`), pero su hijo derecho 30 está cargado hacia izquierda (`balanceFactor(30) = 1`); la dirección es derecha → izquierda. Primero `rotateRight(30)`, después `rotateLeft(10)`, y obtenemos `20(izq 10, der 30)`. RL es una rotación derecha sobre el hijo seguida de una rotación izquierda sobre el nodo.

No necesitamos inventar dos operaciones primitivas nuevas para los casos dobles; se expresan mediante `rotateLeft` y `rotateRight` ya comprendidas, lo que reduce el número de operaciones primitivas que debemos razonar y probar.

### 18. Decidir el caso con balanceFactor

Una tabla final puede ser útil como resumen, pero debe ser una consecuencia del razonamiento, no el punto de partida: si `bf > 1`, LL usa `rotateRight(u)`; RR usa `rotateLeft(u)`; LR hace `u.left = rotateLeft(u.left)` y después `rotateRight(u)`; RL hace `u.right = rotateRight(u.right)` y después `rotateLeft(u)`. La pregunta esencial sigue siendo qué lado está demasiado alto y si la rama interna continúa en la misma dirección o cambia.

Supongamos `bf = balanceFactor(u)`. Si `bf > 1`, la izquierda está demasiado alta; miramos `balanceFactor(u.left)`. Si es negativo, el hijo izquierdo está cargado hacia derecha, y eso identifica LR; si no es negativo, la reparación principal puede tratarse como LL. Simétricamente, si `bf < -1`, la derecha está demasiado alta; miramos `balanceFactor(u.right)`. Si es positivo, el hijo derecho está cargado hacia izquierda, identificando RL; si no es positivo, se trata como RR.

### 19. Inserción AVL recursiva

El archivo de la semana utiliza una estrategia recursiva: insertar como BST, y al regresar, actualizar altura y rebalancear. Una versión compacta de la entrada pública es `void add(int x) { root = add(root, x); }`. La llamada privada, `private Node add(Node u, int x)`, recibe la raíz actual de un subárbol y debe retornar la raíz correcta de ese subárbol después de intentar insertar `x`; la referencia retornada puede ser `u` o puede ser otra referencia si ocurrió una rotación.

Si `u == null`, hemos encontrado la posición donde debe aparecer la nueva hoja, y `return new Node(x);` crea una hoja con `height = 1`, coherente con nuestra convención. Si `x < u.x`, insertamos en el subárbol izquierdo; si `x > u.x`, en el derecho; la lógica de búsqueda no ha cambiado respecto del BST simple. Si `x == u.x`, no creamos otro nodo, la implementación de esta semana representa un conjunto de claves distintas, así que simplemente `return u;` sin modificar la estructura ni aumentar la altura.

No escribimos solamente "insertar recursivamente en `u.left`"; necesitamos conceptualmente `u.left = add(u.left, x);` o `u.right = add(u.right, x);`, porque una rotación interna puede cambiar la raíz local. Por ejemplo, si dentro de `u.left` ocurre una rotación derecha que convierte a 20 en la nueva raíz de ese subárbol, el padre ya no debe apuntar al antiguo nodo, sino a 20; el valor retornado por la recursión permite hacer exactamente esa reconexión.

En las Semanas 4 y 5 utilizamos `parent` para navegar y reconectar. En la implementación AVL de esta semana no lo almacenamos, una decisión de representación, no una limitación de AVL en general: aquí la pila de llamadas y el valor retornado por cada llamada proporcionan el camino para reconstruir los enlaces al regresar.

Después de insertar en un hijo, el nodo actual puede tener una altura distinta, así que antes de decidir si está balanceado debemos ejecutar `updateHeight(u);`; el hijo queda actualizado primero, y después el padre recalcula su altura, nunca con una altura antigua. Una vez que `u.height` es correcta, podemos calcular `balanceFactor(u)` y decidir si el nodo necesita reparación, con `return rebalance(u);`, cuyo valor retornado también puede ser una nueva raíz local.

```java
private Node add(Node u, int x) {
    if (u == null) {
        return new Node(x);
    }

    if (x < u.x) {
        u.left = add(u.left, x);
    } else if (x > u.x) {
        u.right = add(u.right, x);
    } else {
        return u;
    }

    updateHeight(u);
    return rebalance(u);
}
```

La secuencia tiene una razón estructural: descender, insertar, regresar, actualizar, rebalancear, retornar.

### 20. rebalance(Node u)

`rebalance` recibe un nodo cuya altura ya fue actualizada y debe decidir si sigue balanceado y, si no, qué reparación local corresponde.

```java
private Node rebalance(Node u) {
    int bf = balanceFactor(u);

    if (bf > 1) {
        if (balanceFactor(u.left) < 0) {
            u.left = rotateLeft(u.left);
        }

        return rotateRight(u);
    }

    if (bf < -1) {
        if (balanceFactor(u.right) > 0) {
            u.right = rotateRight(u.right);
        }

        return rotateLeft(u);
    }

    return u;
}
```

La primera rama, `bf > 1`, significa lado izquierdo demasiado alto; la condición interior, `balanceFactor(u.left) < 0`, significa que el hijo izquierdo está cargado a derecha, y eso identifica LR, así que primero corregimos el hijo y después hacemos la rotación principal. La rama derecha es simétrica: `bf < -1` significa lado derecho demasiado alto, y si `balanceFactor(u.right) > 0`, el hijo derecho está cargado a izquierda, identificando RL. Si no se cumple ni `bf > 1` ni `bf < -1`, entonces `-1 <= bf <= 1` y no necesitamos rotar, así que retornamos `u`; AVL no intenta hacer que todos los factores sean exactamente cero.

La nueva hoja aparece en la parte inferior del camino, y la recursión regresa por sus ancestros. En cada uno: recibe un hijo ya actualizado, recalcula `height`, calcula `balanceFactor`, repara si es necesario, y retorna la raíz local correcta. Por eso decimos que el mantenimiento ocurre de abajo hacia arriba.

### 21. Una traza completa

Insertemos `50, 30, 70, 20, 40, 10`. Después de las primeras cinco claves tenemos `50(30(20, 40), 70)`, con `height(20) = height(40) = height(70) = 1`, `height(30) = 2`, `height(50) = 3`; todavía es AVL.

Al insertar 10, la inserción BST produce `50(30(20(izq 10), 40), 70)`, con nuevas alturas `height(10) = 1`, `height(20) = 2`, `height(40) = 1`, `height(30) = 3`, `height(70) = 1`, `height(50) = 4`. Revisando los factores durante el regreso: en 20, `balanceFactor(20) = 1 - 0 = 1`, sigue balanceado; en 30, `balanceFactor(30) = 2 - 1 = 1`, sigue balanceado; en 50, `balanceFactor(50) = 3 - 1 = 2`, queda desbalanceado.

El lado pesado de 50 es la izquierda, y su hijo izquierdo, 30, tiene factor positivo, así que es el caso LL respecto de 50. Aplicamos `rotateRight(50)`, y el árbol queda `30(20(izq 10), 50(40, 70))`, con alturas `height(10) = 1`, `height(20) = 2`, `height(40) = 1`, `height(70) = 1`, `height(50) = 2`, `height(30) = 3`, y factores `balanceFactor(20) = 1`, `balanceFactor(50) = 0`, `balanceFactor(30) = 0`. El árbol vuelve a satisfacer AVL.

### 22. Qué debe preservarse y qué no demuestra el inorder

Una inserción AVL correcta debe dejar simultáneamente el orden BST correcto, las claves distintas, todos los nodos alcanzables, la `height` correcta en cada nodo, y `|balanceFactor| <= 1`. No debemos verificar una sola propiedad y asumir las demás.

Si el inorder final es creciente, tenemos evidencia de que el orden BST se preservó, pero inorder no demuestra por sí solo que `height` o `balanceFactor` sean correctos; podemos tener un árbol con claves ordenadas y metadatos de altura corruptos. Para auditar alturas podemos comparar la altura almacenada con `1 + max(altura real del hijo izquierdo, altura real del hijo derecho)`, y después verificar `-1 <= balanceFactor(u) <= 1`. El orden conceptual de auditoría es estructura, orden BST, `height`, balance.

### 23. Búsqueda en AVL y por qué la altura es logarítmica

La búsqueda no necesita rotaciones, no modifica la estructura, y sigue exactamente la propiedad BST. La ventaja de AVL es que el camino tiene una altura controlada: la búsqueda sigue costando O(h), pero la propiedad AVL garantiza `h = O(log n)`, así que la búsqueda en AVL es **O(log n)**.

AVL no exige que el árbol sea completo ni que todos los nodos tengan dos hijos. La restricción local es solamente que las alturas de dos subárboles hermanos no pueden diferir en más de uno, y esa restricción evita que un lado pueda crecer arbitrariamente mientras el otro permanece muy pequeño. Para obtener mucha altura en un AVL necesitamos suficientes nodos que sostengan esa altura; no podemos construir una cadena larga porque los nodos superiores tendrían diferencias de altura mayores que uno. Por eso el número de nodos necesario crece suficientemente rápido con la altura, o equivalentemente, la altura crece lentamente respecto de `n`. No necesitamos aquí una demostración formal completa; basta el resultado `h = O(log n)`.

### 24. Costo de inserción AVL

La inserción desciende hasta una referencia `null` y después regresa por los ancestros mediante la recursión. En cada nodo del camino realiza trabajo constante (comparación, reasignación de referencia, `updateHeight`, `balanceFactor`, reparación local si corresponde), así que `add -> O(h)`, y como `h = O(log n)`, la inserción AVL es **O(log n)**. En un caso simple realizamos una rotación, en un caso doble realizamos dos, pero una y dos son cantidades constantes; la reparación local sigue siendo **O(1)** y no cambia la cota dominada por el recorrido de altura `h`.

### 25. BST simple frente a AVL

Un BST simple mantiene orden, pero no controla la forma: representación más sencilla, menos mantenimiento, puede degenerar, costo general O(h) con `h` potencialmente O(n). Un AVL mantiene orden, altura almacenada y balance: más estado, más invariantes, más mantenimiento, rotaciones, pero `h = O(log n)`, con búsqueda e inserción en O(log n).

AVL no obtiene mejores garantías gratuitamente. Cada nodo almacena `height`, y cada modificación debe preocuparse por actualizar alturas, calcular factores y preservar balance; la implementación es más compleja. Ese es el costo de mantener una cota estructural más fuerte, la misma idea que ya vimos en la Semana 1: una representación puede almacenar información adicional para favorecer ciertas operaciones (capacidad adicional en un arreglo dinámico, altura adicional en AVL), pero ese estado adicional debe mantenerse correctamente, no es información gratuita. Por eso una rotación que produce una forma visual correcta pero no actualiza alturas sigue siendo incorrecta.

### 26. Decisiones de representación de esta semana

Las Semanas 4 y 5 usaban `parent` para conocer el contexto de un nodo; la Semana 6 utiliza recursión y retornos en su lugar. La necesidad conceptual sigue siendo la misma, después de modificar un subárbol alguien debe reconectarlo con su contexto, solo cambia el mecanismo. De manera similar, los archivos de esta semana no mantienen `n`; eso no significa que un AVL real no pueda tener tamaño, sino que esta semana evita ese estado adicional para concentrarse en `height`, balance y rotaciones.

### 27. Integración con las Semanas 1 a 5

La Semana 6 no debe estudiarse como un bloque aislado. La Semana 1 introdujo representación, invariante y costo. La Semana 2 mostró que una modificación local de referencias puede ser barata cuando ya tenemos las referencias correctas. La Semana 3 reforzó la separación entre ADT e implementación. La Semana 4 introdujo el invariante BST y la dependencia O(h). La Semana 5 mostró que modificar un árbol exige preservar subárboles y reconectar correctamente. La Semana 6 reúne esas ideas: representación (`Node` con `height`), invariantes (BST + altura + balance), modificación local (rotaciones, costo O(1)), y costo de búsqueda e inserción (O(log n)).

### 28. El subárbol intermedio también debe funcionar cuando no está vacío

Los ejemplos de tres nodos son útiles para aprender LL, RR, LR y RL, pero una rotación correcta debe funcionar también cuando el subárbol intermedio contiene nodos. Considera `50(30(20, 40(izq 35)), 70)`. En una rotación derecha alrededor de 50, `x = 30`, `middle = 40` (con su hijo izquierdo 35); el subárbol con raíz 40 no puede desaparecer. Después debe quedar `30(20, 50(40(izq 35), 70))`. El inorder antes es `20 30 35 40 50 70`, y después sigue siendo el mismo. Esta prueba es más fuerte que usar únicamente tres nodos, porque obliga a manejar correctamente la referencia intermedia.

### 29. Errores frecuentes que revelan una comprensión incompleta

Vale la pena tener presente un catálogo de errores típicos. Mezclar convenciones de altura (esta semana usa `height(null) = 0`, hoja = 1) es el primero. Invertir el signo de `balanceFactor` (nuestra convención es izquierda menos derecha, positivo significa izquierda más alta) es el segundo. Considerar desbalanceado un nodo con `bf = 1` o `bf = -1`, cuando AVL permite -1, 0 y 1, es el tercero. Olvidar `middle`, lo que puede hacer inaccesible un subárbol completo, es el cuarto. Actualizar alturas en orden incorrecto, cuando dentro de una rotación debemos actualizar primero el nodo que quedó más abajo, es el quinto. No retornar la nueva raíz local, dejando la modificación interna correcta pero mal conectada con el resto del árbol, es el sexto. No reasignar el resultado de la llamada recursiva, cuando una llamada cambia la raíz de su subárbol y el padre debe recibir esa nueva referencia, es el séptimo. Rotar sin actualizar `height`, dejando el inorder correcto pero el estado AVL corrupto, es el octavo. Creer que `add` en AVL es O(1), cuando las rotaciones son O(1) pero la inserción completa sigue un camino y cuesta O(h), es el noveno. Y creer que todo BST tiene altura O(log n), cuando eso solo es cierto con una garantía adicional sobre la forma, es el décimo.

### 30. Qué conviene probar

Las pruebas deben evolucionar cuando cambia la representación. Para `rotateRight` y `rotateLeft` conviene verificar la nueva raíz local, el inorder antes y después, que el subárbol intermedio se conserve, y la `height` de los nodos modificados, tanto con `middle == null` como con `middle != null`. Para `rebalance` debemos cubrir LL, RR, LR, RL y el caso donde el nodo ya está balanceado, importante porque una operación correcta también debe saber cuándo no modificar la estructura. Después de una inserción completa debemos comprobar simultáneamente inorder creciente, claves distintas, `height` consistente y `|balanceFactor| <= 1`. Un recorrido `preorder` puede ayudar a observar la raíz y la forma general, pero una salida preorder razonable no demuestra por sí sola todos los invariantes.

### 31. Limitaciones y costo de mantener balance

AVL ofrece una garantía fuerte sobre la altura, pero esa garantía tiene un precio: un campo `height`, `updateHeight`, `balanceFactor`, rotaciones, `rebalance`, y más cuidado al modificar enlaces. Ese intercambio es deliberado, más mantenimiento produce formas más restringidas, que producen altura controlada, que produce mejores garantías de costo.

Esta semana tampoco estudia todas las operaciones posibles de un AVL completo; en particular, no desarrollaremos eliminación AVL. Ya conocemos la eliminación de un BST simple, pero combinar ahora sucesor, `splice`, `remove`, `height`, balance y rotaciones aumentaría significativamente la carga conceptual. Para un curso inicial es suficiente dominar con profundidad búsqueda, altura, balance, rotaciones e inserción AVL. Tampoco necesitamos una demostración formal completa de la cota de altura; utilizaremos el resultado `h = O(log n)` y la intuición de que el invariante local impide formar una cadena arbitrariamente larga.

### 32. Preguntas que deberían poder responderse sin memorizar código

Ante una rotación derecha deberíamos poder responder quién será la nueva raíz local, quién baja, qué subárbol cambia de padre, por qué ese subárbol puede ocupar su nueva posición, por qué el inorder no cambia, y qué altura se actualiza primero; ante una rotación izquierda, las preguntas simétricas. Ante una inserción AVL, deberíamos poder explicar por qué se inserta primero como BST, por qué `add` retorna `Node`, por qué se reasigna `u.left` o `u.right`, por qué los duplicados se rechazan, por qué `updateHeight` ocurre al regresar, y por qué `rebalance` también retorna `Node`. Ante `rebalance`, deberíamos poder decidir el caso sin recordar una tabla de memoria, preguntando primero qué lado está demasiado alto y después si el hijo relevante continúa hacia el mismo lado o cambia de dirección. Si podemos contestar estas preguntas, el código puede reconstruirse desde los invariantes y los diagramas.

### 33. Puente hacia estructuras jerárquicas con otros invariantes

AVL no es la única estructura donde una propiedad local produce una garantía global. Otras estructuras jerárquicas pueden mantener información o restricciones relacionadas con prioridad, color, tamaño, rango u otras propiedades, para favorecer operaciones diferentes. No estudiaremos esas alternativas en esta lectura; la idea que sí debemos conservar es que representación más invariante adicional producen nuevas garantías. La siguiente semana cambiará nuevamente la representación y el invariante, y la pregunta que conviene mantener es qué propiedad debe permanecer verdadera y qué operación vuelve eficiente esa propiedad.

### 34. Síntesis

La Semana 6 comienza con una limitación del BST simple: aunque el invariante izquierda < nodo < derecha sea correcto, la altura puede crecer hasta O(n), y entonces las operaciones basadas en caminos pueden degradarse. AVL conserva el BST y añade información. Cada nodo almacena `height`, con la convención `height(null) = 0`, hoja = 1, y la altura debe satisfacer `u.height = 1 + max(height(u.left), height(u.right))`, mantenida localmente mediante `updateHeight(Node u)`. El factor de balance se define como `balanceFactor(u) = height(u.left) - height(u.right)`, y el invariante AVL exige `|balanceFactor(u)| <= 1`.

Una inserción comienza igual que en un BST; la nueva hoja puede aumentar las alturas de sus ancestros. Por eso la inserción recursiva trabaja de arriba hacia abajo para localizar e insertar, y luego de abajo hacia arriba para actualizar y rebalancear. Las rotaciones modifican localmente la forma:

```text
    y                 x
   / \               / \
  x   C      ->      A   y
 / \                   / \
A   B                 B   C
```

y una rotación izquierda es simétrica. En ambos casos el subárbol intermedio se conserva, el inorder no cambia, y por tanto el invariante BST se preserva. Las rotaciones cuestan O(1). Los patrones LL, RR, LR y RL no son recetas independientes, se derivan del lado pesado del nodo y del lado pesado del hijo relevante; LL y RR usan una rotación simple, LR y RL componen dos rotaciones simples.

La inserción recursiva debe retornar la nueva raíz local porque una rotación puede cambiar la raíz de un subárbol. Después de modificar un hijo, `updateHeight(u)` y `rebalance(u)` restauran los invariantes de altura y balance. Las claves duplicadas se rechazan y no modifican la estructura.

La garantía fundamental de AVL es `h = O(log n)`, por eso búsqueda e inserción son **O(log n)**. El precio de esa garantía es más estado, más invariantes, más mantenimiento y rotaciones. La lección general no es solamente cómo implementar AVL; la idea más importante es que una representación puede añadir información derivada e invariantes para restringir las formas válidas y obtener mejores garantías de costo. Esa relación entre representación, invariantes, operaciones y complejidad continuará apareciendo en las siguientes estructuras del curso.
