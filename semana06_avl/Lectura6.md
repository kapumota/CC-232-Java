### Lectura: árboles AVL, altura, balance y rotaciones

Esta lectura consolida y amplía las ideas trabajadas en la Semana 6 de CC232. Durante las dos semanas anteriores estudiamos árboles binarios de búsqueda.

En la Semana 4 utilizamos un invariante de orden para buscar e insertar.

```text
subárbol izquierdo
    claves menores

nodo

subárbol derecho
    claves mayores
```

En la Semana 5 aprendimos a eliminar una clave sin perder subárboles que debían permanecer y sin romper el mismo invariante.

Al terminar esa semana podíamos justificar:

```text
findLast -> O(h)
add      -> O(h)
remove   -> O(h)
```

donde `h` representa la altura del árbol.

Sin embargo, quedó una pregunta abierta.

```text
¿qué ocurre si el BST es correcto
pero su altura crece demasiado?
```

La Semana 6 responde esa pregunta.

Un árbol AVL sigue siendo un árbol binario de búsqueda. No cambia el significado básico del conjunto ordenado. Lo que cambia es la representación y el conjunto de invariantes que debemos mantener.

Ahora cada nodo almacenará información sobre su altura y exigiremos una relación entre la altura de sus dos subárboles. Cuando una inserción rompa esa relación, utilizaremos modificaciones locales llamadas rotaciones.

La idea central de la semana puede anticiparse así:

```text
BST
    mantiene orden

AVL
    mantiene orden
    mantiene alturas
    mantiene balance
```

El objetivo no es memorizar cuatro casos llamados LL, RR, LR y RL.

El objetivo es comprender la misma cadena conceptual utilizada desde la primera semana:

```text
ADT
    qué comportamiento queremos ofrecer

representación
    qué estado almacenamos

invariante
    qué propiedades deben permanecer verdaderas

algoritmo
    cómo modificamos la representación

complejidad
    cuánto trabajo exige cada operación
```

En esta lectura utilizaremos la misma convención que los archivos Java de la Semana 6:

```text
height(null) = 0
altura de una hoja = 1
```

También utilizaremos:

```text
balanceFactor(u)
=
height(u.left) - height(u.right)
```

Esta convención debe conservarse durante toda la semana.

### 1. El problema que quedó abierto en la Semana 5

Un BST puede ser completamente correcto y tener una forma poco conveniente.

Considera las inserciones:

```text
40, 20, 60, 10, 30, 50, 70
```

El árbol resultante puede ser:

```text
            40
          /    \
        20      60
       /  \    /  \
     10   30  50   70
```

Con nuestra convención:

```text
h = 3
```

Ahora considera:

```text
10, 20, 30, 40, 50, 60, 70
```

Si insertamos esas claves en un BST simple, podemos obtener:

```text
10
  \
   20
     \
      30
        \
         40
           \
            50
              \
               60
                 \
                  70
```

Este árbol también satisface:

```text
izquierda < nodo < derecha
```

No existe ninguna violación del invariante BST.

Sin embargo:

```text
h = 7
```

La estructura se parece a una lista enlazada.

### 2. Mismas claves, distinta forma

Los dos árboles anteriores contienen exactamente las mismas claves.

Su recorrido inorder es:

```text
10 20 30 40 50 60 70
```

El conjunto lógico es el mismo.

La diferencia es la forma de la representación.

En el primer árbol, buscar 70 sigue:

```text
40 -> 60 -> 70
```

En el segundo:

```text
10 -> 20 -> 30 -> 40 -> 50 -> 60 -> 70
```

Esto muestra que dos BST con el mismo contenido pueden tener costos muy diferentes.

La historia de inserciones influye en la forma.

La forma influye en la altura.

La altura influye en el costo.

```text
historia de actualizaciones
        ->
forma
        ->
altura h
        ->
costo O(h)
```

### 3. Repaso de altura h

La altura mide qué tan largo puede ser un camino descendente dentro del árbol.

En esta semana utilizaremos:

```text
height(null) = 0
```

y una hoja tendrá:

```text
height(hoja) = 1
```

Por ejemplo:

```text
        30
       /
     20
    /
  10
```

tenemos:

```text
height(10) = 1
height(20) = 2
height(30) = 3
```

La altura del árbol es la altura de su raíz.

### 4. La altura controla el costo del BST

En un BST, una búsqueda sigue un camino.

Si el árbol tiene altura `h`, la longitud de ese camino está acotada por `h`.

Por eso:

```text
búsqueda BST  -> O(h)
inserción BST -> O(h)
eliminación   -> O(h)
```

Si:

```text
h = O(log n)
```

entonces esas operaciones pueden ser logarítmicas.

Pero un BST simple no garantiza esa relación.

Puede ocurrir:

```text
h = O(n)
```

y las operaciones pueden degradarse a tiempo lineal.

### 5. Correctitud y eficiencia estructural son problemas diferentes

Conviene separar dos preguntas.

Primera:

```text
¿el árbol representa correctamente
un conjunto ordenado?
```

Segunda:

```text
¿la forma del árbol permite
mantener caminos suficientemente cortos?
```

Un BST degenerado puede responder correctamente la primera pregunta y mal la segunda.

Puede tener:

```text
orden correcto
claves distintas
todos los nodos alcanzables
```

y aun así tener:

```text
h = O(n)
```

AVL agrega una condición para tratar la segunda dimensión.

### 6. AVL no reemplaza el BST

Un árbol AVL sigue satisfaciendo el invariante de búsqueda binaria.

Para cada nodo `u`:

```text
toda clave de u.left
es menor que u.x

toda clave de u.right
es mayor que u.x
```

La búsqueda sigue utilizando comparaciones.

```text
x < u.x
    avanzar a la izquierda

x > u.x
    avanzar a la derecha

x == u.x
    encontrado
```

AVL no cambia esa lógica.

Agrega información y restricciones sobre la forma.

Podemos resumirlo así:

```text
AVL
=
BST
+
altura almacenada
+
invariante de balance
+
operaciones de reparación
```

### 7. Cambiar la representación vuelve a cambiar los invariantes

En las Semanas 4 y 5 utilizamos nodos con campos como:

```text
x
left
right
parent
```

En los archivos de esta semana utilizaremos una representación didáctica distinta:

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

Aparece un nuevo campo:

```text
height
```

Ese campo forma parte del estado de la representación.

Por tanto, ya no basta con que `left` y `right` sean correctos.

También debemos mantener `height`.

### 8. Altura almacenada

El campo:

```java
int height = 1;
```

indica la altura del subárbol cuya raíz es ese nodo.

Cuando un nodo acaba de crearse como hoja:

```text
left  = null
right = null
```

por nuestra convención:

```text
height = 1
```

Esta inicialización es correcta.

Pero después de conectar hijos, el valor puede necesitar cambiar.

### 9. height es información derivada

`height` no es un dato independiente.

No podemos asignarle cualquier valor.

Su valor está determinado por:

```text
u.left
u.right
```

y por las alturas de esos subárboles.

Por eso decimos que es información derivada.

La relación correcta es:

```text
u.height
=
1 + max(
    height(u.left),
    height(u.right)
)
```

Esto introduce otro invariante de representación.

### 10. Una estructura puede tener enlaces correctos y altura incorrecta

Considera:

```text
      20
     /  \
   10    30
```

La forma es correcta.

El invariante BST también puede ser correcto.

Las alturas correctas son:

```text
height(10) = 1
height(30) = 1
height(20) = 2
```

Si almacenamos:

```text
20.height = 7
```

el árbol todavía puede producir un inorder correcto.

Pero el estado AVL es incorrecto.

Esta observación es importante:

```text
salida visible correcta
no implica
representación interna correcta
```

### 11. Función height(Node u)

El archivo de la semana utiliza una función auxiliar:

```java
static int height(Node u) {
    return u == null ? 0 : u.height;
}
```

Su objetivo es centralizar la convención:

```text
null -> 0

nodo real -> altura almacenada
```

Esto permite escribir las demás operaciones sin repetir comprobaciones especiales para hijos nulos.

### 12. Cálculo local de la altura

Si conocemos las alturas correctas de los hijos, la altura del nodo se calcula con:

```text
1 + máximo de las dos alturas
```

Ejemplo:

```text
height(u.left)  = 3
height(u.right) = 1
```

entonces:

```text
u.height = 1 + max(3, 1)
         = 4
```

Solo necesitamos consultar una cantidad constante de información.

### 13. updateHeight(Node u)

La operación correspondiente es:

```java
static void updateHeight(Node u) {
    u.height =
        1 + Math.max(
            height(u.left),
            height(u.right)
        );
}
```

`updateHeight` no recorre todo el subárbol.

Lee las alturas de dos hijos y escribe un nuevo valor.

Por tanto:

```text
updateHeight -> O(1)
```

si las alturas de los hijos ya son correctas.

### 14. El orden de actualización importa

Supongamos que modificamos primero un hijo profundo.

Su altura puede cambiar. Entonces su padre puede necesitar una nueva altura.

Después el abuelo puede necesitar otra actualización.

La dependencia es:

```text
altura del hijo
        ->
altura del padre
        ->
altura del abuelo
```

Por eso el mantenimiento natural ocurre desde abajo hacia arriba.

No debemos actualizar un ancestro usando alturas antiguas de sus hijos.

### 15. Factor de balance

Una vez que almacenamos alturas, podemos comparar los dos lados de un nodo.

Definimos:

```text
balanceFactor(u)
=
height(u.left)
-
height(u.right)
```

En Java:

```java
static int balanceFactor(Node u) {
    return u == null
        ? 0
        : height(u.left) - height(u.right);
}
```

El signo tiene significado.

### 16. Interpretar el signo del factor

Si:

```text
balanceFactor(u) = 0
```

los dos subárboles tienen la misma altura.

Si:

```text
balanceFactor(u) = 1
```

el lado izquierdo es un nivel más alto.

Si:

```text
balanceFactor(u) = -1
```

el lado derecho es un nivel más alto.

Si:

```text
balanceFactor(u) = 2
```

la izquierda es dos niveles más alta.

Si:

```text
balanceFactor(u) = -2
```

la derecha es dos niveles más alta.

La convención de esta semana es siempre:

```text
izquierda - derecha
```

### 17. El invariante AVL

Un nodo está balanceado según AVL cuando:

```text
|balanceFactor(u)| <= 1
```

Esto equivale a exigir:

```text
balanceFactor(u)
pertenece a
{-1, 0, 1}
```

para cada nodo del árbol.

No basta con verificar la raíz.

El invariante debe cumplirse en todos los nodos.

### 18. Balanceado no significa perfectamente simétrico

Considera:

```text
      30
     /  \
   20    40
  /
10
```

Las alturas son:

```text
height(10) = 1
height(20) = 2
height(40) = 1
height(30) = 3
```

Para la raíz:

```text
balanceFactor(30)
=
2 - 1
=
1
```

El árbol no es perfectamente simétrico.

Sin embargo, la raíz satisface AVL.

AVL permite una diferencia de un nivel.

### 19. Un BST puede ser válido y no ser AVL

Considera:

```text
      30
     /
   20
  /
10
```

El orden es correcto:

```text
10 < 20 < 30
```

Por tanto, es un BST.

Pero:

```text
height(30.left)  = 2
height(30.right) = 0
```

Entonces:

```text
balanceFactor(30) = 2
```

y:

```text
|2| > 1
```

No es AVL.

Esta distinción debe quedar clara:

```text
BST válido
no implica
AVL válido
```

### 20. Los invariantes de Semana 6

En esta implementación debemos preservar simultáneamente:

```text
orden BST

altura almacenada correcta

balance AVL

unicidad de claves

alcanzabilidad de todos los nodos
```

Si una operación rompe cualquiera de estas propiedades, la estructura queda incorrecta.

### 21. Por qué una inserción puede romper el balance

Una inserción AVL comienza igual que una inserción BST.

La nueva clave se coloca como hoja en una referencia `null`.

Por ejemplo:

```text
      30
     /
   20
```

es AVL.

Insertamos 10:

```text
      30
     /
   20
  /
10
```

La clave 10 se encuentra en una posición correcta respecto del invariante BST.

No hay error de orden.

Sin embargo, la altura izquierda de 30 aumentó.

Ahora:

```text
balanceFactor(30) = 2
```

La inserción preservó BST pero rompió AVL.

### 22. Solo ciertos nodos pueden cambiar de altura

Cuando insertamos una nueva hoja, no cambia todo el árbol. Solo pueden cambiar las alturas de los nodos que están en el camino desde la nueva hoja hacia la raíz.

Los nodos de otros subárboles conservan exactamente la misma forma.

Por eso no necesitamos recorrer todos los nodos después de cada inserción.

Necesitamos revisar los ancestros afectados.

### 23. La reparación debe preservar el conjunto ordenado

No queremos solucionar el problema eliminando claves o cambiando sus valores arbitrariamente.

Queremos conservar:

```text
mismas claves
mismo orden lógico
```

pero permitir una forma diferente.

Eso conduce a las rotaciones.

### 24. Rotación como transformación local

Una rotación modifica una región pequeña del árbol.

No reconstruye toda la estructura.

Su objetivo es cambiar relaciones padre-hijo localmente.

Conceptualmente:

```text
antes
    una raíz local

después
    otra raíz local
```

pero el conjunto de claves del subárbol debe ser el mismo.

### 25. Equivalencia por inorder

En un BST con claves distintas, el recorrido inorder produce las claves en orden creciente.

Si una transformación conserva:

```text
A, x, B, y, C
```

en el mismo orden inorder, puede cambiar la forma sin cambiar el orden lógico del conjunto.

Esta idea permite justificar las rotaciones.

### 26. Anatomía de una rotación derecha

Considera:

```text
           y
          / \
         x   C
        / \
       A   B
```

Las relaciones de orden son:

```text
toda clave de A < x

x < toda clave de B

toda clave de B < y

y < toda clave de C
```

Podemos resumir:

```text
A < x < B < y < C
```

Aquí `A`, `B` y `C` representan subárboles completos.

### 27. Qué queremos conseguir con rotateRight

La rotación derecha alrededor de `y` produce:

```text
           x
          / \
         A   y
            / \
           B   C
```

El hijo izquierdo `x` sube.

La antigua raíz local `y` baja hacia la derecha. Pero el subárbol `B` debe permanecer.

### 28. El subárbol intermedio es el punto crítico

Antes:

```text
B = x.right
```

Después, `x.right` debe apuntar a `y`.

Si sobrescribimos esa referencia sin conservar `B`, podemos perder todo el subárbol.

Por eso primero necesitamos recordar:

```text
middle = x.right
```

Después:

```text
x.right = y
y.left = middle
```

### 29. Por qué middle pasa a y.left

Las claves de `middle` estaban entre `x` e `y`.

Por tanto:

```text
x < middle < y
```

Después de que `y` se convierta en hijo derecho de `x`, esas claves deben quedar:

```text
a la derecha de x
y
a la izquierda de y
```

La posición:

```text
y.left
```

es exactamente la posición compatible con el invariante BST.

### 30. Preservación del inorder durante rotateRight

Antes:

```text
           y
          / \
         x   C
        / \
       A   B
```

el inorder es:

```text
A
x
B
y
C
```

Después:

```text
           x
          / \
         A   y
            / \
           B   C
```

el inorder sigue siendo:

```text
A
x
B
y
C
```

La forma cambia.

El orden no cambia.

### 31. Implementación de rotateRight

Una implementación coherente con los archivos de la semana es:

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

La estructura del método refleja directamente el razonamiento anterior.

### 32. Precondición de una rotación derecha

Para ejecutar:

```text
rotateRight(y)
```

debe existir:

```text
y.left
```

porque ese nodo será promovido.

Si:

```text
y.left == null
```

no existe el nodo `x` necesario para esa transformación.

En esta semana, `rebalance` llamará a la rotación solo en configuraciones donde la precondición sea válida.

### 33. Actualizar primero y y después x

Después de la rotación:

```text
y
```

queda debajo de:

```text
x
```

La nueva altura de `x` depende de la nueva altura de `y`.

Por eso:

```java
updateHeight(y);
updateHeight(x);
```

y no al revés.

La regla general es:

```text
actualizar primero
el nodo que quedó más abajo
```

### 34. Retornar la nueva raíz local

Antes de la rotación:

```text
raíz local = y
```

Después:

```text
raíz local = x
```

Por eso:

```java
return x;
```

no es un detalle secundario.

El código que llamó a la rotación debe saber qué nodo representa ahora el subárbol completo.

### 35. Rotación izquierda

La rotación izquierda es simétrica.

Antes:

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

Ahora:

```text
middle = y.left
```

y ese subárbol debe convertirse en:

```text
x.right
```

### 36. Preservación del inorder durante rotateLeft

Antes:

```text
A
x
B
y
C
```

Después:

```text
A
x
B
y
C
```

Otra vez:

```text
forma distinta
mismo orden BST
```

### 37. Implementación de rotateLeft

Una implementación es:

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

Primero se actualiza `x` porque queda más abajo.

Después se actualiza `y` porque se convierte en la nueva raíz local.

### 38. Costo de una rotación

Una rotación realiza una cantidad constante de trabajo.

Modifica unas pocas referencias.

Actualiza dos alturas.

No recorre todos los nodos del subárbol.

Por tanto:

```text
rotateRight -> O(1)
rotateLeft  -> O(1)
```

Esto no significa que una inserción AVL completa sea `O(1)`.

La rotación es solamente la reparación local.

### 39. Rotar no significa buscar

La misma separación vista en semanas anteriores reaparece.

```text
localizar el punto relevante
    depende de h

modificar localmente
    puede ser O(1)
```

En una lista doblemente enlazada, una vez localizado el nodo, una eliminación local podía ser `O(1)`.

En AVL, una vez identificado el nodo desbalanceado, la rotación local también es `O(1)`.

### 40. Los cuatro patrones aparecen después de insertar

Para comprender el rebalanceo debemos observar dos decisiones.

Primera:

```text
¿el nodo desbalanceado
está cargado a izquierda o a derecha?
```

Segunda:

```text
¿dentro del hijo relevante
la rama alta continúa en la misma dirección
o cambia de dirección?
```

De esas dos preguntas surgen:

```text
LL
RR
LR
RL
```

### 41. Caso LL

Insertemos:

```text
30, 20, 10
```

Antes de reparar:

```text
      30
     /
   20
  /
10
```

Las alturas son:

```text
height(10) = 1
height(20) = 2
height(30) = 3
```

Los factores relevantes:

```text
balanceFactor(20) = 1
balanceFactor(30) = 2
```

30 está demasiado cargado hacia la izquierda.

Su hijo izquierdo también está cargado hacia la izquierda.

```text
izquierda
    ->
izquierda
```

Por eso llamamos al patrón:

```text
LL
```

### 42. Reparación del caso LL

Aplicamos:

```text
rotateRight(30)
```

y obtenemos:

```text
      20
     /  \
   10    30
```

Ahora:

```text
height(10) = 1
height(30) = 1
height(20) = 2
```

y todos los factores son válidos.

### 43. Caso RR

Insertemos:

```text
10, 20, 30
```

Antes de reparar:

```text
10
  \
   20
     \
      30
```

Tenemos:

```text
balanceFactor(20) = -1
balanceFactor(10) = -2
```

La rama alta sigue:

```text
derecha
    ->
derecha
```

Es el caso:

```text
RR
```

### 44. Reparación del caso RR

Aplicamos:

```text
rotateLeft(10)
```

Resultado:

```text
      20
     /  \
   10    30
```

RR es el caso simétrico de LL.

### 45. Caso LR

Insertemos:

```text
30, 10, 20
```

Antes de reparar:

```text
      30
     /
   10
     \
      20
```

La raíz 30 está cargada hacia izquierda:

```text
balanceFactor(30) = 2
```

pero su hijo izquierdo 10 está cargado hacia derecha:

```text
balanceFactor(10) = -1
```

La dirección cambia:

```text
izquierda
    ->
derecha
```

Es el caso:

```text
LR
```

### 46. Por qué LR necesita dos rotaciones

La rama problemática no está alineada.

El nodo intermedio 20 debe convertirse en la raíz local final.

Primero aplicamos:

```text
rotateLeft(10)
```

y obtenemos:

```text
      30
     /
   20
  /
10
```

Ahora la forma se convirtió en un caso LL.

Después aplicamos:

```text
rotateRight(30)
```

Resultado:

```text
      20
     /  \
   10    30
```

Por tanto:

```text
LR
=
rotación izquierda sobre el hijo
+
rotación derecha sobre el nodo
```

### 47. Caso RL

Insertemos:

```text
10, 30, 20
```

Antes de reparar:

```text
10
  \
   30
   /
 20
```

La raíz 10 está cargada hacia derecha:

```text
balanceFactor(10) = -2
```

pero su hijo derecho 30 está cargado hacia izquierda:

```text
balanceFactor(30) = 1
```

La dirección es:

```text
derecha
    ->
izquierda
```

Es el caso:

```text
RL
```

### 48. Reparación completa de RL

Primero:

```text
rotateRight(30)
```

Después:

```text
rotateLeft(10)
```

Resultado:

```text
      20
     /  \
   10    30
```

Por tanto:

```text
RL
=
rotación derecha sobre el hijo
+
rotación izquierda sobre el nodo
```

### 49. Rotaciones dobles son composiciones

No necesitamos inventar dos operaciones primitivas nuevas.

Las configuraciones dobles pueden expresarse mediante:

```text
rotateLeft
rotateRight
```

ya comprendidas.

Esto reduce el número de operaciones primitivas que debemos razonar y probar.

### 50. No memorizar una tabla sin entender los factores

Una tabla final puede ser útil:

```text
LL
    rotateRight(u)

RR
    rotateLeft(u)

LR
    u.left = rotateLeft(u.left)
    rotateRight(u)

RL
    u.right = rotateRight(u.right)
    rotateLeft(u)
```

Pero esta tabla debe ser una consecuencia del razonamiento.

No debe ser el punto de partida.

La pregunta esencial sigue siendo:

```text
¿qué lado está demasiado alto?

¿la rama interna continúa
en la misma dirección
o cambia?
```

### 51. Decidir con balanceFactor

Supongamos:

```text
bf = balanceFactor(u)
```

Si:

```text
bf > 1
```

la izquierda está demasiado alta.

Entonces miramos:

```text
balanceFactor(u.left)
```

Si es negativo, el hijo izquierdo está cargado hacia derecha.

Eso identifica LR.

Si no es negativo, la reparación principal puede tratarse como LL.

### 52. Decisión simétrica para el lado derecho

Si:

```text
bf < -1
```

la derecha está demasiado alta.

Miramos:

```text
balanceFactor(u.right)
```

Si es positivo, el hijo derecho está cargado hacia izquierda.

Eso identifica RL.

Si no es positivo, la reparación principal puede tratarse como RR.

### 53. Inserción AVL recursiva

El archivo de la semana utiliza una estrategia recursiva.

La idea general es:

```text
insertar como BST

al regresar:
    actualizar altura
    rebalancear
```

Una versión compacta de la entrada pública es:

```java
void add(int x) {
    root = add(root, x);
}
```

La llamada privada trabaja con la raíz de un subárbol.

### 54. Qué significa add(Node u, int x)

La firma conceptual es:

```java
private Node add(Node u, int x)
```

El método recibe:

```text
u
    raíz actual de un subárbol
```

y debe retornar:

```text
la raíz correcta de ese subárbol
después de intentar insertar x
```

La referencia retornada puede ser `u` o puede ser otra referencia si ocurrió una rotación.

### 55. Caso base de la inserción

Si:

```text
u == null
```

hemos encontrado la posición donde debe aparecer la nueva hoja.

Entonces:

```java
return new Node(x);
```

La nueva hoja nace con:

```text
height = 1
```

que es coherente con nuestra convención.

### 56. Descenso por la propiedad BST

Si:

```text
x < u.x
```

insertamos en el subárbol izquierdo.

Si:

```text
x > u.x
```

insertamos en el subárbol derecho.

Conceptualmente:

```text
menor -> izquierda

mayor -> derecha
```

La lógica de búsqueda no ha cambiado respecto del BST simple.

### 57. Rechazo de duplicados

Si:

```text
x == u.x
```

no creamos otro nodo.

La implementación de esta semana representa un conjunto de claves distintas.

Por tanto:

```java
else {
    return u;
}
```

La estructura queda sin cambios.

No aumenta la altura.

No se introduce una segunda copia de la clave.

### 58. Por qué debemos reasignar el hijo retornado

No escribimos solamente:

```text
insertar recursivamente en u.left
```

Necesitamos conceptualmente:

```text
u.left
=
nueva raíz del subárbol izquierdo
```

La razón es que una rotación interna puede cambiar la raíz local.

Por eso la forma es:

```java
u.left = add(u.left, x);
```

o:

```java
u.right = add(u.right, x);
```

### 59. Una rotación interna puede cambiar una referencia del padre

Considera:

```text
padre
  |
  v
 30
 /
20
/
10
```

Después de una rotación derecha dentro de ese subárbol:

```text
padre
  |
  v
 20
/  \
10  30
```

El padre ya no debe apuntar a 30.

Debe apuntar a 20.

El valor retornado por la recursión permite hacer exactamente esa reconexión.

### 60. Por qué esta implementación no necesita parent

En Semanas 4 y 5 utilizamos:

```text
parent
```

para navegar y reconectar.

En la implementación AVL de esta semana no lo almacenamos.

Eso no significa que un AVL no pueda tener `parent`.

Es una decisión de representación.

Aquí:

```text
la pila de llamadas
+
el valor retornado por cada llamada
```

proporcionan el camino para reconstruir los enlaces al regresar.

### 61. Actualizar altura al regresar

Después de insertar en un hijo, el nodo actual puede tener una altura distinta.

Por eso, antes de decidir si está balanceado, debemos ejecutar:

```java
updateHeight(u);
```

La secuencia lógica es:

```text
el hijo queda actualizado

después
el padre recalcula su altura
```

No debemos calcular el factor con una altura antigua.

### 62. Rebalancear después de actualizar

Después de que `u.height` es correcta podemos calcular:

```text
balanceFactor(u)
```

y decidir si el nodo necesita reparación.

La operación conceptual es:

```java
return rebalance(u);
```

Otra vez, el valor retornado puede ser una nueva raíz local.

### 63. Implementación completa de add recursivo

Una versión coherente con el archivo de la semana es:

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

La secuencia tiene una razón estructural.

```text
descender
insertar
regresar
actualizar
rebalancear
retornar
```

### 64. rebalance(Node u)

`rebalance` recibe un nodo cuya altura ya fue actualizada.

Debe decidir:

```text
¿sigue balanceado?

si no:
    ¿qué reparación local corresponde?
```

Una implementación es:

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

### 65. Leer rebalance como decisiones, no como sintaxis

La primera rama:

```text
bf > 1
```

significa:

```text
lado izquierdo demasiado alto
```

La condición interior:

```text
balanceFactor(u.left) < 0
```

significa:

```text
el hijo izquierdo está cargado a derecha
```

Por tanto:

```text
LR
```

Primero corregimos el hijo y después hacemos la rotación principal.

### 66. La rama derecha de rebalance

La condición:

```text
bf < -1
```

significa:

```text
lado derecho demasiado alto
```

Si:

```text
balanceFactor(u.right) > 0
```

el hijo derecho está cargado a izquierda.

Por tanto:

```text
RL
```

Primero corregimos el hijo.

Después aplicamos la rotación izquierda principal.

### 67. Si el nodo ya está balanceado

Si no se cumple:

```text
bf > 1
```

ni:

```text
bf < -1
```

entonces:

```text
-1 <= bf <= 1
```

y no necesitamos rotar.

Retornamos `u`.

AVL no intenta hacer que todos los factores sean exactamente cero.

### 68. Rebalanceo de abajo hacia arriba

La nueva hoja aparece en la parte inferior del camino.

La recursión regresa por sus ancestros.

En cada uno:

```text
1. recibe un hijo ya actualizado

2. recalcula height

3. calcula balanceFactor

4. repara si es necesario

5. retorna la raíz local correcta
```

Por eso decimos que el mantenimiento ocurre:

```text
de abajo hacia arriba
```

### 69. Una traza completa

Insertemos:

```text
50, 30, 70, 20, 40, 10
```

Después de las primeras cinco claves tenemos:

```text
        50
       /  \
     30    70
    /  \
  20    40
```

Las alturas son:

```text
height(20) = 1
height(40) = 1
height(70) = 1
height(30) = 2
height(50) = 3
```

Todavía es AVL.

### 70. Insertar 10

La inserción BST produce:

```text
          50
         /  \
       30    70
      /  \
    20    40
   /
 10
```

Las nuevas alturas:

```text
height(10) = 1
height(20) = 2
height(40) = 1
height(30) = 3
height(70) = 1
height(50) = 4
```

### 71. Revisar los factores durante el regreso

En 20:

```text
balanceFactor(20)
=
1 - 0
=
1
```

Sigue balanceado.

En 30:

```text
balanceFactor(30)
=
2 - 1
=
1
```

Sigue balanceado.

En 50:

```text
balanceFactor(50)
=
3 - 1
=
2
```

50 queda desbalanceado.

### 72. Identificar el caso de la traza

El lado pesado de 50 es:

```text
izquierda
```

El hijo izquierdo es 30.

Su factor es positivo.

Tenemos:

```text
LL
```

respecto de 50.

Aplicamos:

```text
rotateRight(50)
```

### 73. Estado después de reparar

El árbol queda:

```text
          30
         /  \
       20    50
      /     /  \
    10     40   70
```

Las alturas correctas son:

```text
height(10) = 1
height(20) = 2
height(40) = 1
height(70) = 1
height(50) = 2
height(30) = 3
```

Los factores:

```text
balanceFactor(20) = 1
balanceFactor(50) = 0
balanceFactor(30) = 0
```

El árbol vuelve a satisfacer AVL.

### 74. Qué debe preservarse después de cada inserción

Una inserción AVL correcta debe dejar simultáneamente:

```text
orden BST correcto

claves distintas

todos los nodos alcanzables

height correcta en cada nodo

|balanceFactor| <= 1
```

No debemos verificar una sola propiedad y asumir las demás.

### 75. Inorder verifica una parte, no todo

Si el inorder final es creciente, tenemos evidencia de que el orden BST se preservó.

Pero inorder no demuestra por sí solo que:

```text
height sea correcta

balanceFactor sea correcto
```

Podemos tener un árbol con claves ordenadas y metadatos de altura corruptos.

La verificación debe considerar la representación completa.

### 76. Auditar height antes de auditar balance

Para verificar alturas podemos comparar:

```text
altura almacenada
```

con:

```text
1 + max(
    altura real del hijo izquierdo,
    altura real del hijo derecho
)
```

Después de comprobar alturas podemos verificar:

```text
-1 <= balanceFactor(u) <= 1
```

El orden conceptual de auditoría es:

```text
estructura

orden BST

height

balance
```

### 77. Búsqueda en AVL

La búsqueda no necesita rotaciones.

No modifica la estructura.

Sigue exactamente la propiedad BST.

La ventaja de AVL es que el camino tiene una altura controlada.

La búsqueda sigue costando:

```text
O(h)
```

pero la propiedad AVL garantiza:

```text
h = O(log n)
```

Por tanto:

```text
búsqueda AVL
=
O(log n)
```

### 78. Por qué la altura AVL es logarítmica

AVL no exige que el árbol sea completo.

Tampoco exige que todos los nodos tengan dos hijos.

La restricción local es solamente:

```text
las alturas de dos subárboles hermanos
no pueden diferir en más de uno
```

Esa restricción evita que un lado pueda crecer arbitrariamente mientras el otro permanece muy pequeño.

El resultado importante para esta semana es:

```text
h = O(log n)
```

No necesitamos desarrollar aquí una demostración formal completa.

### 79. Una intuición estructural sobre la cota

Para obtener mucha altura en un AVL necesitamos suficientes nodos que sostengan esa altura.

No podemos construir una cadena larga porque los nodos superiores tendrían diferencias de altura mayores que uno.

Cada nivel alto obliga a que existan subárboles adicionales.

Por eso el número de nodos necesario crece suficientemente rápido con la altura.

Equivalentemente, la altura crece lentamente respecto de `n`.

### 80. Costo de inserción AVL

La inserción desciende hasta una referencia `null`.

Después regresa por los ancestros mediante la recursión.

En cada nodo del camino realiza trabajo constante:

```text
comparación

reasignación de referencia

updateHeight

balanceFactor

reparación local si corresponde
```

Por tanto:

```text
add -> O(h)
```

y como:

```text
h = O(log n)
```

tenemos:

```text
inserción AVL
=
O(log n)
```

### 81. Una o dos rotaciones siguen siendo trabajo constante

En un caso simple realizamos una rotación.

En un caso doble realizamos dos.

Pero una y dos son cantidades constantes.

Por tanto:

```text
reparación local
=
O(1)
```

Eso no cambia la cota dominada por el recorrido de altura `h`.

### 82. BST simple frente a AVL

Un BST simple mantiene:

```text
orden
```

pero no controla la forma.

Un AVL mantiene:

```text
orden
+
altura almacenada
+
balance
```

Comparación conceptual:

```text
BST simple

representación más sencilla
menos mantenimiento
puede degenerar
costo general O(h)
h puede ser O(n)


AVL

más estado
más invariantes
más mantenimiento
rotaciones
h = O(log n)
búsqueda O(log n)
inserción O(log n)
```

### 83. El balance tiene un costo

AVL no obtiene mejores garantías gratuitamente.

Cada nodo almacena:

```text
height
```

Cada modificación debe preocuparse por:

```text
actualizar alturas
calcular factores
preservar balance
```

La implementación es más compleja.

Ese es el costo de mantener una cota estructural más fuerte.

### 84. Más información puede permitir mejores garantías

Esta idea conecta con la Semana 1.

Una representación puede almacenar información adicional para favorecer ciertas operaciones.

Ejemplos conceptuales:

```text
arreglo dinámico
    capacidad adicional

AVL
    altura adicional
```

En ambos casos, el estado adicional debe mantenerse correctamente.

No es información gratuita.

### 85. Cambiar la representación cambia las obligaciones

En un BST simple sin `height`, una rotación solo tendría que preocuparse por referencias y orden.

En nuestro AVL debe preocuparse además por:

```text
height
```

Por eso una rotación que produce una forma visual correcta pero no actualiza alturas sigue siendo incorrecta.

### 86. La ausencia de parent no elimina el problema de reconexión

Semanas 4 y 5 usaban:

```text
parent
```

para conocer el contexto de un nodo.

Semana 6 utiliza recursión y retornos.

La necesidad conceptual sigue siendo la misma:

```text
después de modificar un subárbol
alguien debe reconectarlo con su contexto
```

Solo cambia el mecanismo.

### 87. La ausencia de n también es una decisión didáctica

Los archivos de esta semana no mantienen:

```text
n
```

Eso no significa que un AVL real no pueda tener tamaño.

Podríamos agregar:

```text
private int n
```

pero entonces cada inserción tendría que mantener también ese dato.

Esta semana evita ese estado adicional para concentrarse en:

```text
height
balance
rotaciones
```

### 88. Integración con las Semanas 1 a 5

La Semana 6 no debe estudiarse como un bloque aislado.

La Semana 1 introdujo:

```text
representación
invariante
costo
```

La Semana 2 mostró que una modificación local de referencias puede ser barata cuando ya tenemos las referencias correctas.

La Semana 3 reforzó la separación entre ADT e implementación.

La Semana 4 introdujo el invariante BST y la dependencia:

```text
O(h)
```

La Semana 5 mostró que modificar un árbol exige preservar subárboles y reconectar correctamente.

La Semana 6 reúne esas ideas:

```text
representación
    Node con height

invariantes
    BST + altura + balance

modificación local
    rotaciones

costo local
    O(1)

costo de búsqueda e inserción
    O(log n)
```

El campo `height` también recuerda una idea de la Semana 1.

Podemos almacenar información adicional para favorecer ciertas operaciones, pero esa información introduce nuevas obligaciones de mantenimiento.

Una rotación recuerda una idea de la Semana 2 y de la Semana 5.

Una modificación local puede ser `O(1)`, pero no debemos perder referencias que representan subestructuras que deben sobrevivir.

En AVL esa subestructura es especialmente visible en:

```text
middle
```

### 89. El subárbol intermedio debe funcionar también cuando no está vacío

Los ejemplos de tres nodos son útiles para aprender LL, RR, LR y RL.

Pero una rotación correcta debe funcionar también cuando el subárbol intermedio contiene nodos.

Considera:

```text
          50
         /  \
       30    70
      /  \
    20    40
          /
        35
```

En una rotación derecha alrededor de 50:

```text
x = 30
middle = 40
```

El subárbol con raíz 40 no puede desaparecer.

Después debe quedar:

```text
          30
         /  \
       20    50
             / \
           40   70
          /
        35
```

El inorder antes es:

```text
20 30 35 40 50 70
```

El inorder después sigue siendo:

```text
20 30 35 40 50 70
```

Esta prueba es más fuerte que usar únicamente tres nodos, porque obliga a manejar correctamente la referencia intermedia.

### 90. Errores frecuentes que revelan una comprensión incompleta

Primer error:

```text
mezclar convenciones de altura
```

En esta semana:

```text
height(null) = 0
hoja = 1
```

Segundo error:

```text
invertir el signo de balanceFactor
```

Nuestra convención es:

```text
izquierda - derecha
```

Por tanto:

```text
positivo
    izquierda más alta

negativo
    derecha más alta
```

Tercer error:

```text
considerar desbalanceado un nodo con bf = 1 o bf = -1
```

AVL permite:

```text
-1
0
1
```

Cuarto error:

```text
olvidar middle
```

Eso puede hacer inaccesible un subárbol completo.

Quinto error:

```text
actualizar alturas en orden incorrecto
```

Dentro de una rotación debemos actualizar primero el nodo que quedó más abajo.

Sexto error:

```text
no retornar la nueva raíz local
```

La modificación interna puede ser correcta y aun así quedar mal conectada con el resto del árbol.

Séptimo error:

```text
no reasignar el resultado de la llamada recursiva
```

Si una llamada cambia la raíz de su subárbol, el padre debe recibir esa nueva referencia.

Octavo error:

```text
rotar sin actualizar height
```

El inorder puede seguir correcto mientras el estado AVL queda corrupto.

Noveno error:

```text
creer que add AVL es O(1)
```

Las rotaciones son `O(1)`.

La inserción completa sigue un camino y cuesta `O(h)`.

Décimo error:

```text
creer que todo BST tiene altura O(log n)
```

Eso solo es cierto cuando existe una garantía adicional sobre la forma.

### 91. Qué conviene probar

Las pruebas deben evolucionar cuando cambia la representación.

Para `rotateRight` y `rotateLeft` conviene verificar al menos:

```text
nueva raíz local

inorder antes y después

subárbol intermedio conservado

height de los nodos modificados
```

También conviene probar:

```text
middle == null
```

y:

```text
middle != null
```

Para `rebalance` debemos cubrir:

```text
LL
RR
LR
RL

nodo ya balanceado
```

El último caso es importante porque una operación correcta también debe saber cuándo no modificar la estructura.

Después de una inserción completa debemos comprobar simultáneamente:

```text
inorder creciente

claves distintas

height consistente

|balanceFactor| <= 1
```

`preorder` puede ayudar a observar la raíz y la forma general.

Sin embargo, una salida preorder razonable no demuestra por sí sola todos los invariantes.

### 92. Limitaciones y costo de mantener balance

AVL ofrece una garantía fuerte sobre la altura.

Pero esa garantía tiene un precio.

Comparado con un BST simple, necesitamos:

```text
un campo height

updateHeight

balanceFactor

rotaciones

rebalance

más cuidado al modificar enlaces
```

La implementación es más compleja.

Ese intercambio es deliberado:

```text
más mantenimiento
        ->
formas más restringidas
        ->
altura controlada
        ->
mejores garantías de costo
```

Esta semana tampoco estudia todas las operaciones posibles de un AVL completo.

En particular, no desarrollaremos eliminación AVL.

Ya conocemos la eliminación de un BST simple, pero combinar ahora:

```text
sucesor
splice
remove
height
balance
rotaciones
```

aumentaría significativamente la carga conceptual.

Para un curso inicial es suficiente dominar con profundidad:

```text
búsqueda
altura
balance
rotaciones
inserción AVL
```

Tampoco necesitamos una demostración formal completa de la cota de altura.

Utilizaremos el resultado:

```text
h = O(log n)
```

y la intuición de que el invariante local impide formar una cadena arbitrariamente larga.

### 93. Preguntas que deberían poder responderse sin memorizar código

Ante una rotación derecha:

```text
¿quién será la nueva raíz local?

¿quién baja?

¿qué subárbol cambia de padre?

¿por qué ese subárbol puede ocupar su nueva posición?

¿por qué el inorder no cambia?

¿qué altura se actualiza primero?
```

> Ante una rotación izquierda debemos poder responder las preguntas simétricas.

Ante una inserción AVL debemos poder explicar:

```text
por qué se inserta primero como BST

por qué add retorna Node

por qué se reasigna u.left o u.right

por qué los duplicados se rechazan

por qué updateHeight ocurre al regresar

por qué rebalance también retorna Node
```

Ante `rebalance` debemos poder decidir el caso sin recordar una tabla de memoria.

Primero:

```text
¿qué lado está demasiado alto?
```

Después:

```text
¿el hijo relevante continúa hacia el mismo lado
o cambia de dirección?
```

Si podemos contestar estas preguntas, el código puede reconstruirse desde los invariantes y los diagramas.

### 94. Puente hacia estructuras jerárquicas con otros invariantes

AVL no es la única estructura donde una propiedad local produce una garantía global.

Otras estructuras jerárquicas pueden mantener información o restricciones relacionadas con:

```text
prioridad

color

tamaño

rango

otras propiedades
```

para favorecer operaciones diferentes.

No estudiaremos esas alternativas en esta lectura.

La idea que sí debemos conservar es:

```text
representación
+
invariante adicional
=
nuevas garantías
```

La siguiente semana cambiará nuevamente la representación y el invariante.

La pregunta que conviene mantener es:

```text
¿qué propiedad debe permanecer verdadera

y
qué operación vuelve eficiente esa propiedad?
```

### 95. Síntesis

La Semana 6 comienza con una limitación del BST simple.

Aunque el invariante:

```text
izquierda < nodo < derecha
```

sea correcto, la altura puede crecer hasta:

```text
O(n)
```

y entonces las operaciones basadas en caminos pueden degradarse.

AVL conserva el BST y añade información.

Cada nodo almacena:

```text
height
```

con la convención:

```text
height(null) = 0
hoja = 1
```

La altura debe satisfacer:

```text
u.height
=
1 + max(
    height(u.left),
    height(u.right)
)
```

y se mantiene localmente mediante `updateHeight(Node u)`.

El factor de balance se define como:

```text
balanceFactor(u)
=
height(u.left)
-
height(u.right)
```

El invariante AVL exige:

```text
|balanceFactor(u)| <= 1
```

Una inserción comienza igual que en un BST.

La nueva hoja puede aumentar las alturas de sus ancestros.

Por eso la inserción recursiva trabaja:

```text
de arriba hacia abajo
para localizar e insertar
```

y luego:

```text
de abajo hacia arriba
para actualizar y rebalancear
```

Las rotaciones modifican localmente la forma.

Una rotación derecha transforma:

```text
           y                 x
          / \               / \
         x   C      ->      A   y
        / \                   / \
       A   B                 B   C
```

y una rotación izquierda es simétrica.

En ambos casos el subárbol intermedio se conserva.

El inorder no cambia.

Por tanto, el invariante BST se preserva.

Las rotaciones cuestan:

```text
O(1)
```

Los patrones:

```text
LL
RR
LR
RL
```

no son recetas independientes.

Se derivan del lado pesado del nodo y del lado pesado del hijo relevante.

LL y RR usan una rotación simple.

LR y RL componen dos rotaciones simples.

La inserción recursiva debe retornar la nueva raíz local porque una rotación puede cambiar la raíz de un subárbol.

Después de modificar un hijo:

```text
updateHeight(u)
rebalance(u)
```

restauran los invariantes de altura y balance.

Las claves duplicadas se rechazan y no modifican la estructura.

La garantía fundamental de AVL es:

```text
h = O(log n)
```

Por eso:

```text
búsqueda AVL
    O(log n)

inserción AVL
    O(log n)
```

El precio de esa garantía es:

```text
más estado
más invariantes
más mantenimiento
rotaciones
```

La lección general no es solamente cómo implementar AVL.

La idea más importante es:

```text
una representación puede añadir
información derivada e invariantes
para restringir las formas válidas
y obtener mejores garantías de costo
```

Esa relación entre representación, invariantes, operaciones y complejidad continuará apareciendo en las siguientes estructuras del curso.
