### Lectura: eliminación en árboles binarios de búsqueda y preservación de la estructura

Esta lectura consolida y amplía las ideas trabajadas en la Semana 5 de CC232.

En la Semana 4 construimos un árbol binario de búsqueda a partir de una representación enlazada con `root`, `left`, `right`, `parent` y `n`. Utilizamos el invariante de orden para realizar búsqueda e inserción, y expresamos el costo de esas operaciones en función de la altura `h`.

En la Semana 5 conservamos la misma representación y el mismo invariante. La nueva pregunta es cómo retirar una clave de un BST sin perder nodos que deben permanecer y sin romper el orden. Eliminar es más delicado que insertar. Cuando insertamos, agregamos una nueva hoja en una referencia `null`. Cuando eliminamos, el nodo que queremos retirar puede estar conectando uno o dos subárboles que deben seguir siendo alcanzables desde `root`.

La idea central de la semana es localizar, clasificar el nodo, reconectar, preservar invariantes y analizar el costo. El objetivo no es memorizar `splice(Node u)` ni `remove(int x)`, sino comprender por qué esas operaciones tienen esa forma y qué propiedades deben conservar.

### 1. La representación que heredamos de la Semana 4

El nodo mantiene `int x`, `Node left`, `Node right` y `Node parent`; el árbol mantiene `private Node root` y `private int n`. Podemos separar contenido y estructura: `x` es la clave almacenada; `left` y `right` son las raíces de los subárboles izquierdo y derecho; `parent` es el padre estructural; `root` es el acceso al árbol completo, y `n` el número de nodos del BST. La eliminación debe modificar esta representación sin destruir sus propiedades.

### 2. El invariante global de orden sigue siendo obligatorio

Para cada nodo `u`, toda clave del subárbol izquierdo de `u` es menor que `u.x`, y toda clave del subárbol derecho es mayor. Como no permitimos duplicados, izquierda < nodo < derecha debe entenderse sobre subárboles completos. En el árbol con raíz 40, hijos 20 y 60, y nietos 10, 30, 50, 70, respecto de 40 se cumple 10, 20, 30 < 40 < 50, 60, 70. Una eliminación correcta debe producir otro árbol que siga satisfaciendo la misma propiedad.

### 3. El orden no es el único invariante

Para un árbol no vacío debe cumplirse `root != null` y `root.parent == null`. Para todo hijo izquierdo existente, `u.left.parent == u`; para todo hijo derecho existente, `u.right.parent == u`. También `n` debe coincidir con el número de nodos alcanzables desde `root`, y las claves deben seguir siendo distintas. Por tanto, eliminar correctamente significa preservar simultáneamente el orden BST, `root`, `parent`, `n`, la alcanzabilidad y la unicidad de claves.

### 4. Repaso de findLast(int x)

La Semana 5 reutiliza directamente `findLast(int x)`: si `x` existe, retorna el nodo que lo contiene; si no existe, retorna el último nodo real visitado; si el árbol está vacío, retorna `null`.

```java
private Node findLast(int x) {
    Node w = root;
    Node prev = null;

    while (w != null) {
        prev = w;

        if (x < w.x) {
            w = w.left;
        } else if (x > w.x) {
            w = w.right;
        } else {
            return w;
        }
    }

    return prev;
}
```

La propiedad que debemos recordar es que `findLast(x)` puede retornar un nodo aunque `x` no exista.

### 5. Una clave ausente también puede producir un nodo

Considera el árbol `40 -> 60 (izquierda) -> 50 (derecha) -> 55`. Al buscar 52, el camino es `52 > 40`, `52 < 60`, `52 > 50`, `52 < 55`, y `55.left == null`, así que `findLast(52)` retorna el nodo 55, aunque 52 no esté almacenado. Por eso `remove` no puede asumir que un resultado no nulo significa que la clave existe.

### 6. Confirmar existencia antes de eliminar

El comienzo de `remove(int x)` será `Node u = findLast(x);`, seguido de la comprobación `if (u == null || u.x != x) { return false; }`. Si el árbol está vacío, `u == null`; si no está vacío pero la clave no existe, `u` puede ser un nodo real con una clave distinta. En ambos casos, `remove(x) -> false` y no deben cambiar `root`, `parent`, `n` ni ningún enlace.

### 7. Por qué eliminar es más difícil que insertar

Durante la inserción, una búsqueda fallida terminaba en una referencia `null` y el nuevo nodo se conectaba allí; el nodo nuevo siempre era una hoja. Durante la eliminación, el nodo que queremos retirar puede tener 0, 1 o 2 hijos, y cada caso impone una obligación diferente sobre los subárboles que deben sobrevivir.

### 8. El problema de la alcanzabilidad

Considera de nuevo `40 -> 60 (izquierda) -> 50 (derecha) -> 55`. Si queremos eliminar 50 y hacemos únicamente `60.left = null;`, el nodo 50 deja de ser alcanzable, pero también 55. Eso elimina accidentalmente más información de la solicitada. La lección es que eliminar no significa borrar el enlace hacia `u`; la operación correcta debe retirar `u` y reconectar cualquier subárbol que deba permanecer.

### 9. Clasificar por número de hijos

Después de localizar `u`, distinguimos tres casos: cero hijos (hoja), un hijo (un único subárbol superviviente) y dos hijos (dos subárboles que deben preservarse). Los casos de cero y un hijo pueden resolverse mediante una sola operación auxiliar. El caso de dos hijos necesita una transformación adicional.

### 10. Caso de cero hijos, hoja

Una hoja satisface `u.left == null` y `u.right == null`. En el árbol `40(20(izq 10), 60)`, al eliminar 10 no existe ningún subárbol debajo que deba preservarse; como 10 es hijo izquierdo de 20, la modificación correcta es `20.left = null`, y el resultado es `40(20, 60)`.

### 11. El concepto de hijo superviviente

Para unificar hoja y un hijo utilizaremos `s`, el hijo que sobrevive. Si `u` es hoja, `s = null`; si `u` tiene un único hijo, `s` es ese hijo. Ambos casos pueden expresarse entonces como reemplazar estructuralmente `u` por `s`.

### 12. Caso de un hijo

Considera `60(izq 50(der 55))`. Queremos eliminar 50, con `u = 50`, `s = 55`, `p = 60`. Antes, `60.left == 50` y `55.parent == 50`; después, `60.left == 55` y `55.parent == 60`. El resultado es `60(izq 55)`. La clave 55 permanece; solo cambia su posición estructural respecto del padre.

### 13. Reconexión padre-hijo

Si almacenamos `parent`, no basta modificar el enlace del padre hacia el hijo. Si después de eliminar `u` ocurre `p.left == s` o `p.right == s`, también debe cumplirse `s.parent == p`. Una estructura puede verse correcta cuando se recorre hacia abajo con `left`/`right` y estar corrupta al usar `parent`.

### 14. La operación splice(Node u)

Definimos `private void splice(Node u)`, cuya responsabilidad es retirar estructuralmente `u`, suponiendo que `u` tiene a lo más un hijo. `splice` no busca una clave; recibe directamente una referencia al nodo que debe retirarse.

### 15. Precondición de splice

La precondición es que `u` tenga como máximo un hijo, es decir, `u.left == null || u.right == null`. Esto incluye hoja, solo hijo izquierdo y solo hijo derecho, pero no incluye dos hijos.

### 16. Seleccionar el hijo superviviente

Una forma compacta es `Node s = (u.left != null) ? u.left : u.right;`. Si existe hijo izquierdo, `s` es ese hijo; si no existe, `s` es el hijo derecho; si ambos son `null`, entonces `s = null`, que representa el caso hoja.

### 17. Conservar el padre de u

También necesitamos `Node p = u.parent;`. Ahora tenemos tres referencias: `u`, el nodo que desaparece; `s`, el hijo superviviente o `null`; y `p`, el padre de `u`. La excepción estructural es cuando `u` es `root`, porque entonces no existe un padre.

### 18. Eliminación de root con cero o un hijo

Considera `root -> 40 (der 60)`. Queremos eliminar 40, con `u = 40`, `s = 60`, `u == root`. El hijo superviviente debe convertirse en nueva raíz, `root = s;`, y además `s.parent = null;`. El resultado es `root -> 60`.

### 19. Eliminar la única raíz

Si el árbol contiene solamente `root -> 40`, entonces `u = 40`, `s = null`. Después de `splice`, `root = null`, y después de actualizar el tamaño, `n = 0`.

### 20. Distinguir si u era hijo izquierdo o derecho

Si `u` no es `root`, tenemos `p = u.parent`, y debemos determinar qué enlace de `p` apuntaba a `u`:

```java
if (p.left == u) {
    p.left = s;
} else {
    p.right = s;
}
```

Después, si `s` existe, `s.parent = p;`. La posición izquierda o derecha se conserva.

### 21. Implementación completa de splice

```java
private void splice(Node u) {
    Node s = (u.left != null) ? u.left : u.right;
    Node p = u.parent;

    if (u == root) {
        root = s;

        if (s != null) {
            s.parent = null;
        }
    } else {
        if (p.left == u) {
            p.left = s;
        } else {
            p.right = s;
        }

        if (s != null) {
            s.parent = p;
        }
    }
}
```

La lógica puede resumirse así: determinar `s`, recordar `p`, actualizar `root` o el enlace de `p`, y corregir `s.parent` si `s` existe.

### 22. Por qué splice preserva el orden

Supongamos que `u` era hijo izquierdo de `p`. Todo el subárbol de `u` ya estaba en el lado izquierdo de `p`. Por tanto, si `s` es el único hijo de `u`, todo el subárbol de `s` también contiene claves válidas para esa posición. Conectar `p.left = s` no inventa una nueva relación de orden, solo elimina un nodo intermedio del camino. El razonamiento es análogo si `u` era hijo derecho.

### 23. Por qué splice no modifica n

Separaremos responsabilidades: `splice` reconecta estructura, y `remove` representa la eliminación lógica y actualiza `n`. Esto es importante porque `splice` también será utilizado sobre un nodo auxiliar en el caso de dos hijos. La regla será que `splice` no modifica `n`, y `remove` decrementa `n` una sola vez.

### 24. El caso de dos hijos

Considera `50(izq 30, der 80(izq 60, der 90))`. Queremos eliminar 50, cuyo `left = 30` y `right = 80`. No podemos elegir un único `s` sin perder uno de los dos subárboles; por tanto, `splice(50)` no cumple su precondición.

### 25. Por qué no basta conectar uno de los dos hijos

Si hacemos `root = 30`, el subárbol derecho quedaría desconectado; si hacemos `root = 80`, el subárbol izquierdo quedaría desconectado. Necesitamos transformar el problema de dos hijos en otro donde el nodo que finalmente se retire tenga a lo más un hijo.

### 26. Recordar inorder

Inorder visita subárbol izquierdo, nodo, subárbol derecho, y en un BST produce claves crecientes. Para el árbol `50(30, 80(60(izq 55, der 70), 90))`, inorder produce `30 50 55 60 70 80 90`. Esta secuencia permite definir qué clave aparece inmediatamente después de otra.

### 27. Sucesor inorder

El **sucesor inorder** de una clave almacenada es el nodo que aparece inmediatamente después en la secuencia inorder. En `30 50 55 60 70 80 90`, el sucesor de 50 es 55.

### 28. Sucesor cuando existe subárbol derecho

Si `u` tiene dos hijos, necesariamente `u.right != null`. En ese caso, el sucesor de `u` es el **mínimo del subárbol derecho** de `u`. No necesitamos estudiar todavía todos los casos generales de sucesor; para `remove` basta este caso.

### 29. Mínimo del subárbol derecho

Considera `50(der 80(izq 60(izq 55)))`. Empezamos en `u.right = 80` y seguimos `left`, `80 -> 60 -> 55`; como `55.left == null`, 55 es el mínimo del subárbol derecho y, por tanto, el sucesor inorder de 50.

### 30. Código para localizar el sucesor necesario

Dentro del caso de dos hijos:

```java
Node w = u.right;

while (w.left != null) {
    w = w.left;
}
```

Al terminar, `w` es el sucesor inorder de `u`. El algoritmo sigue un único camino descendente.

### 31. Propiedad, el sucesor no tiene hijo izquierdo

Cuando termina el ciclo sabemos que `w.left == null`. La razón conceptual es que si `w` tuviera un hijo izquierdo `z`, por el invariante `z.x < w.x`, y entonces `w` no sería el mínimo del subárbol derecho. Por tanto, el sucesor elegido de esta forma no puede tener hijo izquierdo.

### 32. El sucesor sí puede tener hijo derecho

No debemos concluir que el sucesor es siempre una hoja. En `50(der 80(izq 60(izq 55(der 57))))`, el sucesor de 50 sigue siendo 55, pero 55 tiene hijo derecho 57. La propiedad correcta es que el sucesor no tiene hijo izquierdo, y por tanto tiene a lo más un hijo. Eso permite utilizar `splice`.

### 33. Reducir dos hijos al caso simple

La estrategia completa cuando `u` tiene dos hijos es buscar el sucesor `w`, copiar `w.x` en `u.x`, y ejecutar `splice(w)`. Como `w.left == null`, `w` tiene a lo más un hijo, así que `splice(w)` cumple su precondición.

### 34. Copiar la clave del sucesor

Considera `50(izq 30, der 80(izq 60(izq 55)))`. Queremos eliminar 50; el sucesor es 55. Primero hacemos `u.x = w.x;`, así que el nodo que contenía 50 pasa a contener 55. Sus referencias `left`, `right` y `parent` no cambian en ese momento.

### 35. Eliminación lógica y eliminación física

Después de copiar la clave, 50 ya no está almacenado en `u`; desde el punto de vista lógico, la clave solicitada ha desaparecido. Pero temporalmente existen dos nodos con 55, por eso todavía debemos retirar físicamente el nodo sucesor original con `splice(w)`. La distinción es entre **eliminación lógica** (desaparece la clave solicitada) y **eliminación física** (se retira el nodo sucesor original).

### 36. Estado intermedio del caso de dos hijos

Antes, el árbol es `50(30, 80(60(55)))`. Después de copiar la clave, temporalmente aparecen dos nodos con 55, el que era la raíz del subárbol y el sucesor original. Después de `splice(w)`, el árbol queda `55(30, 80(60))`, y la unicidad vuelve a mantenerse.

### 37. Por qué copiar el sucesor preserva el orden

El sucesor es la menor clave mayor que la antigua clave de `u` dentro del subárbol derecho. Todas las claves del subárbol izquierdo de `u` eran menores que la antigua `u.x`, por tanto también son menores que `w.x`. Después de retirar el nodo sucesor original, todas las claves restantes del subárbol derecho son mayores que `w.x`. Así se preserva izquierda < nueva clave de `u` < derecha.

### 38. Sucesor con hijo derecho

Considera `50(der 80(izq 60(izq 55(der 57))))`. El sucesor de 50 es 55. Después de copiar 55 en `u`, debemos ejecutar `splice(55)`. Como 55 tiene hijo derecho 57, el resultado es `60.left = 57` y `57.parent = 60`. El árbol final es `55(30, 80(60(57)))`. Este ejemplo muestra que `splice` debe manejar algo más que hojas.

### 39. Construcción de remove(int x)

Podemos estructurar la operación en cuatro etapas: localizar, comprobar existencia, resolver la estructura local y actualizar `n`. En pseudocódigo, `u = findLast(x)`; si `u == null` o `u.x != x`, retornar `false`; si `u` tiene a lo más un hijo, `splice(u)`; en otro caso, buscar `w`, el mínimo del subárbol derecho, copiar `w.x` en `u.x`, y `splice(w)`; finalmente `n--` y retornar `true`.

### 40. Implementación completa de remove(int x)

```java
boolean remove(int x) {
    Node u = findLast(x);

    if (u == null || u.x != x) {
        return false;
    }

    if (u.left == null || u.right == null) {
        splice(u);
    } else {
        Node w = u.right;

        while (w.left != null) {
            w = w.left;
        }

        u.x = w.x;
        splice(w);
    }

    n--;
    return true;
}
```

### 41. Por qué la condición usa OR

La condición `u.left == null || u.right == null` significa que al menos uno de los dos enlaces es `null`, lo que incluye exactamente cero hijos, un hijo izquierdo o un hijo derecho. El único caso que llega al `else` es `u.left != null && u.right != null`, es decir, dos hijos.

### 42. Clave ausente

Considera `40(20, 60)`. Ejecutamos `remove(50)`. El camino es `40 -> 60 -> left null`, así que `findLast(50) = 60`, pero `60 != 50`. Por tanto, `remove(50) = false` y `n` no cambia.

### 43. n-- exactamente una vez

Si una eliminación exitosa parte de `n = 8`, el resultado debe ser `n = 7`, sin importar si el nodo tenía cero, uno o dos hijos. En el caso de dos hijos copiamos una clave y retiramos un nodo físico, pero el ADT pierde una sola clave. Por eso `n--;` se ejecuta una sola vez dentro de `remove`.

### 44. El ejemplo del archivo Java de Semana 5

El archivo construye el árbol insertando `40, 20, 60, 10, 30, 50, 70, 55`, lo que produce `40(20(10, 30), 60(50(der 55), 70))`, con `n = 8`. Después se ejecuta `remove(10)`, `remove(50)` y `remove(60)`, una secuencia que cubre hoja, un hijo y dos hijos.

### 45. remove(10): hoja

10 tiene cero hijos: `u = 10`, `s = null`, `p = 20`. `splice` modifica `20.left = null`. El resultado es `40(20(der 30), 60(50(der 55), 70))`, con `n = 7`.

### 46. remove(50): un hijo

Ahora 50 tiene un único hijo, 55: `u = 50`, `s = 55`, `p = 60`. `splice` modifica `60.left = 55` y `55.parent = 60`. El resultado es `40(20(der 30), 60(55, 70))`, con `n = 6`.

### 47. remove(60): dos hijos

Ahora 60 tiene `left = 55` y `right = 70`. El sucesor inorder es 70, porque es el mínimo de su subárbol derecho. Copiamos `60.x = 70` y retiramos físicamente el antiguo nodo 70 con `splice`. El resultado es `40(20(der 30), 70(izq 55))`, con `n = 5`.

### 48. Verificación mediante inorder

El inorder final es `20 30 40 55 70`. Esto permite observar que 10, 50 y 60 desaparecieron; 20, 30, 40, 55 y 70 permanecieron, y la salida sigue ordenada. Inorder es una evidencia muy útil de preservación del conjunto y del orden observado mediante `left` y `right`.

### 49. Lo que inorder no demuestra

Un inorder correcto no prueba por sí solo que `parent` sea correcto. Podría ocurrir que `40.right == 70` pero `70.parent == 20`, y el inorder podría seguir siendo el mismo. Tampoco prueba por sí solo que `n == 5`. Por tanto, la verificación debe considerar varios invariantes a la vez.

### 50. Preservación de root, parent, n y alcanzabilidad

Después de eliminar debe cumplirse que, si `n == 0`, entonces `root == null`; para un árbol no vacío, `root != null` y `root.parent == null`. Eliminar una raíz con cero o un hijo puede cambiar la referencia `root`; eliminar lógicamente una raíz con dos hijos puede mantener el mismo objeto raíz y cambiar únicamente su clave.

Para cada nodo alcanzable `u`, si `u.left != null` entonces `u.left.parent == u`, y si `u.right != null` entonces `u.right.parent == u`. Después de `splice`, el enlace `parent` del superviviente merece una revisión explícita.

`n` debe coincidir con el número de nodos alcanzables desde `root`: un `remove` exitoso disminuye `n` una vez, un `remove` fallido no lo cambia, y el caso de dos hijos no es una excepción.

Finalmente, después de eliminar debemos comprobar que todos los nodos que debían permanecer siguen siendo alcanzables desde `root`. Un error de reconexión puede conservar un árbol aparentemente ordenado y perder un subárbol entero; la alcanzabilidad es una propiedad estructural independiente del orden.

### 51. Preservación del orden global

No basta verificar relaciones inmediatas entre padre e hijo. Para cada nodo `u` debe mantenerse que todo el subárbol izquierdo es menor que `u.x` y todo el subárbol derecho es mayor. La elección del sucesor en el caso de dos hijos está diseñada precisamente para preservar este invariante global.

### 52. Complejidad de splice y de findLast

`splice` realiza una cantidad constante de comparaciones y asignaciones de referencias; no recorre un camino proporcional a `n`. Por tanto, si ya tenemos la referencia a `u`, `splice -> O(1)`. `findLast(x)` sigue un único camino desde `root`, cuya longitud está acotada por la altura `h`, así que `findLast -> O(h)`.

### 53. Complejidad de remove

Con cero o un hijo, tenemos `findLast` en O(h), `splice` en O(1) y `n--` en O(1), así que `remove -> O(h)`. Con dos hijos, además de `findLast` buscamos el sucesor siguiendo `left` dentro del subárbol derecho, un recorrido también acotado por `h`; sumando `findLast` (O(h)), la búsqueda del sucesor (O(h)), copiar la clave (O(1)) y `splice` (O(1)), el resultado sigue siendo `remove -> O(h)`.

### 54. O(h) no significa O(log n) automáticamente

La afirmación general para un BST simple es que `findLast`, `add` y `remove` cuestan **O(h)**. La relación entre `h` y `n` depende de la forma del árbol. En el árbol de siete nodos con `h = 3`, si la altura se mantiene aproximadamente logarítmica (`h = O(log n)`), búsqueda, inserción y eliminación pueden comportarse como **O(log n)**. Pero en un árbol degenerado como la cadena `10 -> 20 -> 30 -> 40 -> 50 -> 60 -> 70`, tenemos `n = 7` y `h = 7`; una operación puede recorrer casi todos los nodos, así que `h = O(n)` y `remove = O(n)`.

Dos BST pueden almacenar exactamente las mismas claves y tener costos diferentes; un inorder idéntico no implica una altura idéntica. La secuencia de operaciones construye una forma concreta, la forma determina la altura, y la altura determina la longitud máxima de los caminos.

### 55. Errores frecuentes en splice

Vale la pena tener presente algunos errores típicos. Desconectar `u` y olvidar su hijo, por ejemplo hacer `p.left = null;` cuando existe un hijo superviviente, es incorrecto. Reconectar hacia abajo (`p.left = s;`) sin actualizar `s.parent = p;` cuando `s != null` también lo es. No tratar el caso `u == root`, donde no existe `p` que pueda modificar `left` o `right` y debe actualizarse `root` directamente, es otro error común. Y aplicar `splice` a un nodo con dos hijos viola su precondición.

### 56. Errores frecuentes en remove

Igualmente frecuentes son confiar únicamente en `findLast` sin comprobar `u.x == x` (recuerda que puede retornar un nodo aunque `x` no exista); decrementar `n` cuando la clave está ausente, cuando `remove(x)` retorna `false` y `n` no debería cambiar; decrementar `n` dos veces en el caso de dos hijos, cuando en realidad se elimina una sola clave lógica; suponer que el sucesor es siempre `u.right`, cuando el mínimo del subárbol derecho puede encontrarse varios niveles hacia la izquierda; y suponer que el sucesor siempre es una hoja, cuando puede tener hijo derecho.

### 57. Sucesor no significa hijo derecho inmediato

Considera `40(der 80(izq 60(izq 50)))`. El hijo derecho inmediato de 40 es 80, pero el sucesor inorder de 40 es 50. Por eso el algoritmo debe entrar por `right` y seguir `left` hasta no poder continuar.

### 58. Copiar la clave no significa mover el nodo

Cuando hacemos `u.x = w.x;`, no cambiamos automáticamente `u.parent`, `u.left` ni `u.right`. El nodo `u` permanece en su posición estructural; solo cambia la clave almacenada. Después se retira físicamente `w`. Valor almacenado e identidad del objeto `Node` son conceptos diferentes.

### 59. Localización frente a modificación reaparece

La misma separación vista anteriormente vuelve a ser útil: localizar `u` cuesta O(h), modificar con `splice` cuesta O(1); en el caso de dos hijos, localizar el sucesor cuesta O(h), y copiar y reconectar cuesta O(1). Las modificaciones son locales una vez que tenemos las referencias necesarias.

### 60. La eliminación no garantiza una buena altura

Podemos implementar correctamente `findLast`, `add` y `remove`, preservar todos los invariantes, y aun así obtener un árbol muy desbalanceado. Un BST simple garantiza orden, pero no garantiza que las ramas tengan longitudes parecidas. Por eso un árbol puede ser completamente correcto y tener `h = O(n)`.

### 61. Correctitud y eficiencia son problemas diferentes

Un árbol degenerado puede satisfacer el invariante BST, tener `parent` correcto, `root` correcto, `n` correcto e inorder ordenado, pero tener operaciones lentas. Eso muestra dos dimensiones distintas, la correctitud estructural y la eficiencia de la forma. La Semana 5 resuelve la primera para la eliminación; la siguiente semana comenzará a tratar la segunda.

### 62. Qué queda resuelto y qué queda abierto

Después de esta semana podemos realizar búsqueda, inserción y eliminación sobre un BST simple, preservando orden, `root`, `parent`, `n` y alcanzabilidad, y podemos justificar que `findLast`, `add` y `remove` cuestan **O(h)**.

Todavía no existe, sin embargo, un mecanismo que obligue a mantener una altura pequeña después de una historia arbitraria de inserciones y eliminaciones. Podemos terminar con una cadena degenerada de seis nodos y seguir teniendo un BST válido. La pregunta pendiente es cómo modificar la forma del árbol, sin cambiar el conjunto ordenado de claves, para impedir que la altura crezca demasiado.

### 63. Puente hacia AVL

La siguiente semana conservará la idea de BST, pero añadirá mecanismos para controlar la altura: altura almacenada, factor de balance y rotaciones. La motivación debe quedar clara antes de estudiarlos. No se introduce AVL porque el BST simple sea incorrecto; se introduce porque un BST correcto puede tener una forma ineficiente. La Semana 5 termina entonces con esta distinción, un BST simple preserva el orden, pero no controla su altura.

### 64. Síntesis

La eliminación empieza reutilizando la búsqueda, `u = findLast(x)`, pero antes de modificar debemos confirmar `u != null` y `u.x == x`. Después clasificamos el nodo según tenga 0, 1 o 2 hijos. Los casos de cero y un hijo se unifican con `splice(Node u)`, cuya precondición es que `u` tenga a lo más un hijo. `splice` identifica `s`, el hijo superviviente o `null`, y `p`, el padre de `u`, y mantiene correctamente `root`, el enlace del padre y `parent` del superviviente.

El caso de dos hijos se transforma mediante el sucesor inorder, el mínimo del subárbol derecho. Ese nodo no tiene hijo izquierdo y, por tanto, tiene a lo más un hijo. La eliminación se reduce entonces a copiar la clave del sucesor en `u` y ejecutar `splice(sucesor)`. `remove` decrementa `n` exactamente una vez cuando la eliminación tiene éxito, y después deben preservarse `root`, `parent`, `n`, la alcanzabilidad, el orden BST y la unicidad de claves. `inorder` ayuda a verificar claves y orden, pero no demuestra por sí solo que `parent` o `n` sean correctos.

La complejidad es `splice -> O(1)`, `findLast -> O(h)` y `remove -> O(h)`. La limitación final del BST simple permanece: `h` puede crecer hasta O(n). Ese problema prepara el estudio de árboles AVL en la Semana 6.
