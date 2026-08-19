### Lectura: eliminación en árboles binarios de búsqueda y preservación de la estructura

Esta lectura consolida y amplía las ideas trabajadas en la Semana 5 de CC232.

En la Semana 4 construimos un árbol binario de búsqueda a partir de una representación enlazada con `root`, `left`, `right`, `parent` y `n`. Utilizamos el invariante de orden para realizar búsqueda e inserción y expresamos el costo de esas operaciones en función de la altura `h`.

En la Semana 5 conservamos la misma representación y el mismo invariante. La nueva pregunta es:

```text
¿cómo retirar una clave de un BST sin perder nodos que deben permanecer y sin romper el orden?
```

Eliminar es más delicado que insertar. Cuando insertamos, agregamos una nueva hoja en una referencia `null`. Cuando eliminamos, el nodo que queremos retirar puede estar conectando uno o dos subárboles que deben seguir siendo alcanzables desde `root`.

La idea central de la semana es:

```text
localizar
clasificar el nodo
reconectar
preservar invariantes
analizar el costo
```

El objetivo no es memorizar `splice(Node u)` ni `remove(int x)`. El objetivo es comprender por qué esas operaciones tienen esa forma y qué propiedades deben conservar.

### 1. La representación que heredamos de la Semana 4

El nodo mantiene:

```java
static class Node {
    int x;
    Node left;
    Node right;
    Node parent;

    Node(int x) {
        this.x = x;
    }
}
```

El árbol mantiene:

```java
private Node root;
private int n;
```

Podemos separar contenido y estructura:

```text
x
    clave almacenada

left
    raíz del subárbol izquierdo

right
    raíz del subárbol derecho

parent
    padre estructural

root
    acceso al árbol completo

n
    número de nodos del BST
```

La eliminación debe modificar esta representación sin destruir sus propiedades.

### 2. El invariante global de orden sigue siendo obligatorio

Para cada nodo `u`:

```text
toda clave del subárbol izquierdo de u es menor que u.x

toda clave del subárbol derecho de u es mayor que u.x
```

Como no permitimos duplicados:

```text
izquierda < nodo < derecha
```

debe entenderse sobre subárboles completos.

Por ejemplo:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

respecto de 40:

```text
10, 20, 30 < 40
50, 60, 70 > 40
```

Una eliminación correcta debe producir otro árbol que siga satisfaciendo la misma propiedad.

### 3. El orden no es el único invariante

Para un árbol no vacío debe cumplirse:

```text
root != null
root.parent == null
```

Para todo hijo izquierdo existente:

```text
u.left.parent == u
```

Para todo hijo derecho existente:

```text
u.right.parent == u
```

También:

```text
n = número de nodos alcanzables desde root
```

y las claves deben seguir siendo distintas.

Por tanto, eliminar correctamente significa preservar simultáneamente:

```text
orden BST
root
parent
n
alcanzabilidad
unicidad de claves
```

### 4. Repaso de findLast(int x)

La Semana 5 reutiliza directamente `findLast(int x)`.

Su especificación es:

```text
si x existe
    retornar el nodo que contiene x

si x no existe
    retornar el último nodo real visitado

si el árbol está vacío
    retornar null
```

Una implementación es:

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

Considera:

```text
          40
            \
             60
            /
           50
             \
              55
```

Busquemos 52:

```text
52 > 40
52 < 60
52 > 50
52 < 55
55.left == null
```

Camino:

```text
40 -> 60 -> 50 -> 55
```

Entonces:

```text
findLast(52) = nodo 55
```

pero 52 no está almacenado.

Por eso `remove` no puede asumir que un resultado no nulo significa que la clave existe.

### 6. Confirmar existencia antes de eliminar

El comienzo de `remove(int x)` será:

```java
Node u = findLast(x);
```

Después debemos comprobar:

```java
if (u == null || u.x != x) {
    return false;
}
```

Si el árbol está vacío, `u == null`.

Si el árbol no está vacío pero la clave no existe, `u` puede ser un nodo real con una clave distinta.

En ambos casos:

```text
remove(x) -> false
```

y no deben cambiar `root`, `parent`, `n` ni ningún enlace.

### 7. Por qué eliminar es más difícil que insertar

Durante la inserción, una búsqueda fallida terminaba en una referencia `null` y el nuevo nodo se conectaba allí.

El nodo nuevo siempre era una hoja.

Durante la eliminación, el nodo que queremos retirar puede tener:

```text
0 hijos
1 hijo
2 hijos
```

Cada caso impone una obligación diferente sobre los subárboles que deben sobrevivir.

### 8. El problema de la alcanzabilidad

Considera:

```text
        40
          \
           60
          /
         50
           \
            55
```

Supongamos que queremos eliminar 50 y hacemos únicamente:

```java
60.left = null;
```

El nodo 50 deja de ser alcanzable, pero también 55.

Eso elimina accidentalmente más información de la solicitada.

La lección es:

```text
eliminar no significa borrar el enlace hacia u
```

La operación correcta debe retirar `u` y reconectar cualquier subárbol que deba permanecer.

### 9. Clasificar por número de hijos

Después de localizar `u`, distinguimos:

```text
0 hijos
    hoja

1 hijo
    un único subárbol superviviente

2 hijos
    dos subárboles deben preservarse
```

Los casos de cero y un hijo pueden resolverse mediante una sola operación auxiliar.

El caso de dos hijos necesita una transformación adicional.

### 10. Caso de cero hijos: hoja

Una hoja satisface:

```text
u.left == null
u.right == null
```

Ejemplo:

```text
          40
        /    \
      20      60
     /
   10
```

Queremos eliminar 10.

No existe ningún subárbol debajo de 10 que deba preservarse.

Como 10 es hijo izquierdo de 20, la modificación correcta es:

```text
20.left = null
```

Resultado:

```text
          40
        /    \
      20      60
```

### 11. El concepto de hijo superviviente

Para unificar hoja y un hijo utilizaremos:

```text
s = hijo que sobrevive
```

Si `u` es hoja:

```text
s = null
```

Si `u` tiene un único hijo:

```text
s = ese hijo
```

Entonces ambos casos pueden expresarse como:

```text
reemplazar estructuralmente u por s
```

### 12. Caso de un hijo

Considera:

```text
        60
       /
     50
       \
        55
```

Queremos eliminar 50.

Tenemos:

```text
u = 50
s = 55
p = 60
```

Antes:

```text
60.left == 50
55.parent == 50
```

Después:

```text
60.left == 55
55.parent == 60
```

Resultado:

```text
        60
       /
     55
```

La clave 55 permanece. Solo cambia su posición estructural respecto del padre.

### 13. Reconexión padre-hijo

Si almacenamos `parent`, no basta modificar el enlace del padre hacia el hijo.

Si después de eliminar `u` ocurre:

```text
p.left == s
```

o:

```text
p.right == s
```

también debe cumplirse:

```text
s.parent == p
```

Una estructura puede verse correcta cuando se recorre hacia abajo y estar corrupta al usar `parent`.

### 14. La operación splice(Node u)

Definimos:

```java
private void splice(Node u)
```

Su responsabilidad es:

```text
retirar estructuralmente u
suponiendo que u tiene a lo más un hijo
```

`splice` no busca una clave.

Recibe directamente una referencia al nodo que debe retirarse.

### 15. Precondición de splice

La precondición es:

```text
u tiene como máximo un hijo
```

En Java:

```java
u.left == null || u.right == null
```

Esto incluye:

```text
hoja
solo hijo izquierdo
solo hijo derecho
```

No incluye:

```text
dos hijos
```

### 16. Seleccionar el hijo superviviente

Una forma compacta es:

```java
Node s = (u.left != null) ? u.left : u.right;
```

Si existe hijo izquierdo, `s` es ese hijo.

Si no existe, `s` es el hijo derecho.

Si ambos son `null`, entonces:

```text
s = null
```

que representa el caso hoja.

### 17. Conservar el padre de u

También necesitamos:

```java
Node p = u.parent;
```

Ahora podemos pensar en tres referencias:

```text
u
    nodo que desaparece

s
    hijo superviviente o null

p
    padre de u
```

La excepción estructural es cuando `u` es `root`, porque entonces no existe un padre.

### 18. Eliminación de root con cero o un hijo

Considera:

```text
root
 |
 v
40
  \
   60
```

Queremos eliminar 40.

Tenemos:

```text
u = 40
s = 60
u == root
```

El hijo superviviente debe convertirse en nueva raíz:

```java
root = s;
```

Además:

```java
s.parent = null;
```

Resultado:

```text
root
 |
 v
60
```

### 19. Eliminar la única raíz

Si el árbol contiene solamente:

```text
root
 |
 v
40
```

entonces:

```text
u = 40
s = null
```

Después de `splice`:

```text
root = null
```

Después de actualizar el tamaño:

```text
n = 0
```

### 20. Distinguir si u era hijo izquierdo o derecho

Si `u` no es `root`, tenemos:

```text
p = u.parent
```

Debemos determinar qué enlace de `p` apuntaba a `u`.

```java
if (p.left == u) {
    p.left = s;
} else {
    p.right = s;
}
```

Después, si `s` existe:

```java
s.parent = p;
```

La posición izquierda o derecha se conserva.

### 21. Implementación completa de splice

Una implementación coherente con la representación de la semana es:

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

La lógica puede resumirse así:

```text
determinar s
recordar p
actualizar root o el enlace de p
corregir s.parent si s existe
```

### 22. Por qué splice preserva el orden

Supongamos que `u` era hijo izquierdo de `p`.

Todo el subárbol de `u` ya estaba en el lado izquierdo de `p`.

Por tanto, si `s` es el único hijo de `u`, todo el subárbol de `s` también contiene claves válidas para esa posición.

Conectar:

```text
p.left = s
```

no inventa una nueva relación de orden.

Solo elimina un nodo intermedio del camino.

El razonamiento es análogo si `u` era hijo derecho.

### 23. Por qué splice no modifica n

En esta implementación separaremos responsabilidades:

```text
splice
    reconecta estructura

remove
    representa la eliminación lógica
    y actualiza n
```

Esto es importante porque `splice` también será utilizado sobre un nodo auxiliar en el caso de dos hijos.

La regla será:

```text
splice no modifica n
remove decrementa n una sola vez
```

### 24. El caso de dos hijos

Considera:

```text
          50
        /    \
      30      80
             /  \
           60    90
```

Queremos eliminar 50.

El nodo tiene:

```text
left = 30
right = 80
```

No podemos elegir un único `s` sin perder uno de los dos subárboles.

Por tanto:

```text
splice(50)
```

no cumple su precondición.

### 25. Por qué no basta conectar uno de los dos hijos

Si hacemos:

```text
root = 30
```

el subárbol derecho quedaría desconectado.

Si hacemos:

```text
root = 80
```

el subárbol izquierdo quedaría desconectado.

Necesitamos transformar el problema de dos hijos en otro donde el nodo que finalmente se retire tenga a lo más un hijo.

### 26. Recordar inorder

Inorder visita:

```text
subárbol izquierdo
nodo
subárbol derecho
```

En un BST produce claves crecientes.

Para:

```text
          50
        /    \
      30      80
             /  \
           60    90
          /  \
        55   70
```

inorder produce:

```text
30 50 55 60 70 80 90
```

Esta secuencia permite definir qué clave aparece inmediatamente después de otra.

### 27. Sucesor inorder

El sucesor inorder de una clave almacenada es el nodo que aparece inmediatamente después en la secuencia inorder.

En:

```text
30 50 55 60 70 80 90
```

el sucesor de 50 es 55.

Durante la eliminación con dos hijos utilizaremos una propiedad específica del sucesor.

### 28. Sucesor cuando existe subárbol derecho

Si `u` tiene dos hijos, necesariamente:

```text
u.right != null
```

En ese caso:

```text
sucesor de u
=
mínimo del subárbol derecho de u
```

No necesitamos estudiar todavía todos los casos generales de sucesor.

Para `remove` basta este caso.

### 29. Mínimo del subárbol derecho

Considera:

```text
          50
            \
             80
            /
           60
          /
         55
```

Empezamos en:

```text
u.right = 80
```

y seguimos `left`:

```text
80 -> 60 -> 55
```

Como:

```text
55.left == null
```

55 es el mínimo del subárbol derecho.

Por tanto es el sucesor inorder de 50.

### 30. Código para localizar el sucesor necesario

Dentro del caso de dos hijos:

```java
Node w = u.right;

while (w.left != null) {
    w = w.left;
}
```

Al terminar:

```text
w = sucesor inorder de u
```

El algoritmo sigue un único camino descendente.

### 31. Propiedad: el sucesor no tiene hijo izquierdo

Cuando termina el ciclo sabemos:

```text
w.left == null
```

La razón conceptual es importante.

Si `w` tuviera un hijo izquierdo `z`, por el invariante:

```text
z.x < w.x
```

Entonces `w` no sería el mínimo del subárbol derecho.

Por tanto el sucesor elegido de esta forma no puede tener hijo izquierdo.

### 32. El sucesor sí puede tener hijo derecho

No debemos concluir que el sucesor es siempre una hoja.

Puede ocurrir:

```text
          50
            \
             80
            /
           60
          /
         55
           \
            57
```

El sucesor de 50 sigue siendo 55.

Pero 55 tiene hijo derecho 57.

La propiedad correcta es:

```text
el sucesor no tiene hijo izquierdo
```

y por tanto:

```text
tiene a lo más un hijo
```

Eso permite utilizar `splice`.

### 33. Reducir dos hijos al caso simple

La estrategia completa es:

```text
u tiene dos hijos

buscar sucesor w

copiar w.x en u.x

splice(w)
```

Como `w.left == null`, `w` tiene a lo más un hijo.

Por tanto `splice(w)` cumple su precondición.

### 34. Copiar la clave del sucesor

Supongamos:

```text
          50
        /    \
      30      80
             /
           60
          /
        55
```

Queremos eliminar 50.

El sucesor es 55.

Primero hacemos:

```java
u.x = w.x;
```

El nodo que contenía 50 pasa a contener 55.

Sus referencias `left`, `right` y `parent` no cambian en ese momento.

### 35. Eliminación lógica y eliminación física

Después de copiar la clave, 50 ya no está almacenado en `u`. Desde el punto de vista lógico, la clave solicitada ha desaparecido.
Pero temporalmente existen dos nodos con 55.

Por eso todavía debemos retirar físicamente el nodo sucesor original:

```java
splice(w);
```

La distinción es:

```text
eliminación lógica
    desaparece la clave solicitada

eliminación física
    se retira el nodo sucesor original
```

### 36. Estado intermedio del caso de dos hijos

Antes:

```text
          50
        /    \
      30      80
             /
           60
          /
        55
```

Después de copiar:

```text
          55
        /    \
      30      80
             /
           60
          /
        55
```

Este estado es temporal.

Después de `splice(w)`:

```text
          55
        /    \
      30      80
             /
           60
```

La unicidad vuelve a mantenerse.

### 37. Por qué copiar el sucesor preserva el orden

El sucesor es la menor clave mayor que la antigua clave de `u` dentro del subárbol derecho.

Todas las claves del subárbol izquierdo de `u` eran menores que la antigua `u.x`.

Por tanto también son menores que `w.x`.

Después de retirar el nodo sucesor original, todas las claves restantes del subárbol derecho son mayores que `w.x`.

Así se preserva:

```text
izquierda < nueva clave de u < derecha
```

### 38. Sucesor con hijo derecho

Considera:

```text
          50
        /    \
      30      80
             /
           60
          /
        55
          \
           57
```

El sucesor de 50 es 55.

Después de copiar 55 en `u`, debemos ejecutar `splice(55)`.

Como 55 tiene hijo derecho 57:

```text
60.left = 57
57.parent = 60
```

Resultado:

```text
          55
        /    \
      30      80
             /
           60
          /
        57
```

Este ejemplo muestra que `splice` debe manejar algo más que hojas.

### 39. Construcción de remove(int x)

Podemos estructurar la operación en cuatro etapas:

```text
1. localizar
2. comprobar existencia
3. resolver la estructura local
4. actualizar n
```

Pseudocódigo:

```text
u = findLast(x)

si u == null o u.x != x
    retornar false

si u tiene a lo más un hijo
    splice(u)
en otro caso
    w = mínimo del subárbol derecho
    copiar w.x en u.x
    splice(w)

n--
retornar true
```

### 40. Implementación completa de remove(int x)

Una implementación coherente con el archivo de la semana es:

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

La condición:

```java
u.left == null || u.right == null
```

significa:

```text
al menos uno de los dos enlaces es null
```

Eso incluye exactamente:

```text
0 hijos
1 hijo izquierdo
1 hijo derecho
```

El único caso que llega al `else` es:

```text
u.left != null
u.right != null
```

es decir, dos hijos.

### 42. Clave ausente

Considera:

```text
      40
     /  \
   20    60
```

Ejecutamos:

```text
remove(50)
```

Camino:

```text
40 -> 60 -> left null
```

Entonces:

```text
findLast(50) = 60
```

pero:

```text
60 != 50
```

Por tanto:

```text
remove(50) = false
```

y `n` no cambia.

### 43. n-- exactamente una vez

Si una eliminación exitosa parte de:

```text
n = 8
```

el resultado debe ser:

```text
n = 7
```

sin importar si el nodo tenía cero, uno o dos hijos.

En el caso de dos hijos copiamos una clave y retiramos un nodo físico, pero el ADT pierde una sola clave.

Por eso:

```java
n--;
```

se ejecuta una sola vez dentro de `remove`.

### 44. El ejemplo del archivo Java de Semana 5

El archivo construye:

```text
40, 20, 60, 10, 30, 50, 70, 55
```

La forma es:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
              \
               55
```

Estado inicial:

```text
n = 8
```

Después se ejecuta:

```text
remove(10)
remove(50)
remove(60)
```

La secuencia cubre hoja, un hijo y dos hijos.

### 45. remove(10): hoja

10 tiene cero hijos.

```text
u = 10
s = null
p = 20
```

`splice` modifica:

```text
20.left = null
```

Resultado:

```text
          40
        /    \
      20      60
       \      /  \
       30    50   70
               \
                55
```

Tamaño:

```text
n = 7
```

### 46. remove(50): un hijo

Ahora 50 tiene un único hijo:

```text
55
```

Tenemos:

```text
u = 50
s = 55
p = 60
```

`splice` modifica:

```text
60.left = 55
55.parent = 60
```

Resultado:

```text
          40
        /    \
      20      60
       \      /  \
       30    55   70
```

Tamaño:

```text
n = 6
```

### 47. remove(60): dos hijos

Ahora 60 tiene:

```text
left = 55
right = 70
```

Sucesor inorder:

```text
70
```

porque es el mínimo de su subárbol derecho.

Copiamos:

```text
60.x = 70
```

y retiramos físicamente el antiguo nodo 70 con `splice`.

Resultado:

```text
          40
        /    \
      20      70
       \      /
       30    55
```

Tamaño:

```text
n = 5
```

### 48. Verificación mediante inorder

El inorder final es:

```text
20 30 40 55 70
```

Esto permite observar que:

```text
10, 50 y 60 desaparecieron
20, 30, 40, 55 y 70 permanecieron
la salida sigue ordenada
```

Inorder es una evidencia muy útil de preservación del conjunto y del orden observado mediante `left` y `right`.

### 49. Lo que inorder no demuestra

Un inorder correcto no prueba por sí solo que `parent` sea correcto.

Podría ocurrir:

```text
40.right == 70
```

pero:

```text
70.parent == 20
```

y el inorder podría seguir siendo el mismo.

Tampoco prueba por sí solo que:

```text
n == 5
```

Por tanto, la verificación debe considerar varios invariantes.

### 50. Preservación de root

Después de eliminar debe cumplirse:

```text
si n == 0
    root == null
```

Para un árbol no vacío:

```text
root != null
root.parent == null
```

Eliminar una raíz con cero o un hijo puede cambiar la referencia `root`.

Eliminar lógicamente una raíz con dos hijos puede mantener el mismo objeto raíz y cambiar únicamente su clave.

### 51. Preservación de parent

Para cada nodo alcanzable `u`:

```text
si u.left != null
    u.left.parent == u

si u.right != null
    u.right.parent == u
```

Después de `splice`, el enlace `parent` del superviviente merece una revisión explícita.

### 52. Preservación de n

`n` debe coincidir con el número de nodos alcanzables desde `root`.

```text
remove exitoso
    n disminuye una vez

remove fallido
    n no cambia
```

El caso de dos hijos no es una excepción.

### 53. Preservación de alcanzabilidad

Después de eliminar debemos comprobar:

```text
todos los nodos que debían permanecer siguen siendo alcanzables desde root
```

Un error de reconexión puede conservar un árbol aparentemente ordenado y perder un subárbol entero.

La alcanzabilidad es una propiedad estructural independiente del orden.

### 54. Preservación del orden global

No basta verificar relaciones inmediatas entre padre e hijo.

Para cada nodo `u` debe mantenerse:

```text
todo el subárbol izquierdo < u.x
todo el subárbol derecho > u.x
```

La elección del sucesor en el caso de dos hijos está diseñada precisamente para preservar este invariante global.

### 55. Complejidad de splice

`splice` realiza una cantidad constante de comparaciones y asignaciones de referencias.

No recorre un camino proporcional a `n`.

Por tanto, si ya tenemos la referencia a `u`:

```text
splice -> O(1)
```

### 56. Complejidad de findLast

`findLast(x)` sigue un único camino desde `root`.

La longitud de ese camino está acotada por la altura `h`.

Por tanto:

```text
findLast -> O(h)
```

### 57. Complejidad de remove con cero o un hijo

Tenemos:

```text
findLast    O(h)

splice    O(1)

n--        O(1)
```

Entonces:

```text
remove -> O(h)
```

### 58. Complejidad de remove con dos hijos

Además de `findLast`, buscamos el sucesor siguiendo `left` dentro del subárbol derecho.

Ese recorrido también está acotado por `h`.

Tenemos:

```text
findLast    O(h)

buscar sucesor    O(h)

copiar clave    O(1)

splice    O(1)
```

Por tanto:

```text
remove -> O(h)
```

### 59. O(h) no significa O(log n) automáticamente

La afirmación general para un BST simple es:

```text
findLast -> O(h)
add      -> O(h)
remove   -> O(h)
```

La relación entre `h` y `n` depende de la forma del árbol.

### 60. Árbol de poca altura

Considera:

```text
          40
        /    \
      20      60
     /  \    /  \
   10   30  50   70
```

Usando la convención hoja = 1:

```text
h = 3
```

Si la altura se mantiene aproximadamente logarítmica:

```text
h = O(log n)
```

entonces búsqueda, inserción y eliminación pueden comportarse como:

```text
O(log n)
```

### 61. Árbol degenerado

Considera:

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

Aquí:

```text
n = 7
h = 7
```

Una operación puede recorrer casi todos los nodos.

Entonces:

```text
h = O(n)
```

y:

```text
remove = O(n)
```

### 62. Forma del árbol y costo

Dos BST pueden almacenar exactamente las mismas claves y tener costos diferentes. Un inorder idéntico no implica una altura idéntica.

La secuencia de operaciones construye una forma concreta. La forma determina la altura.

La altura determina la longitud máxima de los caminos.

Por tanto:

```text
forma del árbol
-> altura
-> longitud de caminos
-> costo
```

### 63. Errores frecuentes en splice

#### Desconectar u y olvidar su hijo

Incorrecto cuando existe un hijo superviviente:

```java
p.left = null;
```

#### Reconectar hacia abajo y olvidar parent

Incorrecto:

```java
p.left = s;
```

sin actualizar:

```java
s.parent = p;
```

cuando `s != null`.

#### No tratar root

Si `u == root`, no existe `p` que pueda modificar `left` o `right`.

Debe actualizarse `root`.

#### Aplicar splice a dos hijos

La precondición no se cumple.

### 64. Errores frecuentes en remove

#### Confiar únicamente en findLast

`findLast(x)` puede retornar un nodo aunque `x` no exista.

#### Decrementar n cuando la clave está ausente

Si `remove(x)` retorna `false`, `n` no cambia.

#### Decrementar n dos veces

El caso de dos hijos elimina una sola clave lógica.

#### Suponer que el sucesor es u.right

El sucesor es el mínimo del subárbol derecho y puede encontrarse varios niveles hacia la izquierda.

#### Suponer que el sucesor siempre es hoja

Puede tener hijo derecho.

### 65. Sucesor no significa hijo derecho inmediato

Considera:

```text
          40
            \
             80
            /
           60
          /
         50
```

El hijo derecho inmediato de 40 es 80.

El sucesor inorder de 40 es 50.

Por eso el algoritmo debe:

```text
entrar por right
seguir left hasta no poder continuar
```

### 66. Copiar la clave no significa mover el nodo

Cuando hacemos:

```java
u.x = w.x;
```

no cambiamos automáticamente:

```text
u.parent
u.left
u.right
```

El nodo `u` permanece en su posición estructural.

Solo cambia la clave almacenada.

Después se retira físicamente `w`.

Valor almacenado e identidad del objeto `Node` son conceptos diferentes.

### 67. Localización frente a modificación reaparece

La misma separación vista anteriormente vuelve a ser útil:

```text
localizar u
    O(h)

modificar con splice
    O(1)
```

En dos hijos:

```text
localizar sucesor
    O(h)

copiar y reconectar
    O(1)
```

Las modificaciones son locales una vez que tenemos las referencias necesarias.

### 68. La eliminación no garantiza una buena altura

Podemos implementar correctamente `findLast`, `add` y `remove`, preservar todos los invariantes y aun así obtener un árbol muy desbalanceado.

Un BST simple garantiza orden.

No garantiza que las ramas tengan longitudes parecidas.

Por eso un árbol puede ser completamente correcto y tener:

```text
h = O(n)
```

### 69. Correctitud y eficiencia son problemas diferentes

Un árbol degenerado puede satisfacer:

```text
invariante BST
parent correcto
root correcto
n correcto
inorder ordenado
```

pero tener operaciones lentas.

Eso muestra dos dimensiones distintas:

```text
correctitud estructural

eficiencia de la forma
```

La Semana 5 resuelve la primera para la eliminación.

La siguiente semana comenzará a tratar la segunda.

### 70. Qué queda resuelto al terminar la Semana 5

Después de esta semana podemos realizar:

```text
búsqueda
inserción
eliminación
```

sobre un BST simple.

Podemos preservar:

```text
orden
root
parent
n
alcanzabilidad
```

Y podemos justificar:

```text
findLast -> O(h)
add      -> O(h)
remove   -> O(h)
```

### 71. Qué problema queda abierto

Todavía no existe un mecanismo que obligue a mantener una altura pequeña después de una historia arbitraria de inserciones y eliminaciones.

Podemos terminar con:

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
```

y seguir teniendo un BST válido.

La pregunta pendiente es:

```text
¿cómo modificar la forma del árbol
sin cambiar el conjunto ordenado de claves
para impedir que la altura crezca demasiado?
```

### 72. Puente hacia AVL

La siguiente semana conservará la idea de BST, pero añadirá mecanismos para controlar la altura.

Aparecerán conceptos como:

```text
altura almacenada
factor de balance
rotaciones
```

La motivación debe quedar clara antes de estudiarlos.

No se introduce AVL porque el BST simple sea incorrecto.

Se introduce porque un BST correcto puede tener una forma ineficiente.

La Semana 5 termina entonces con esta distinción:

```text
BST simple
    preserva orden

pero

BST simple
    no controla su altura
```

### 73. Síntesis

La eliminación empieza reutilizando la búsqueda:

```text
u = findLast(x)
```

pero antes de modificar debemos confirmar:

```text
u != null
u.x == x
```

Después clasificamos el nodo:

```text
0 hijos
1 hijo
2 hijos
```

Los casos de cero y un hijo se unifican con:

```java
splice(Node u)
```

cuya precondición es:

```text
u tiene a lo más un hijo
```

`splice` identifica:

```text
s
    hijo superviviente o null

p
    padre de u
```

y mantiene correctamente `root`, el enlace del padre y `parent` del superviviente.

El caso de dos hijos se transforma mediante el sucesor inorder:

```text
sucesor
=
mínimo del subárbol derecho
```

Ese nodo no tiene hijo izquierdo y por tanto tiene a lo más un hijo.

La eliminación se reduce a:

```text
copiar la clave del sucesor en u
splice(sucesor)
```

`remove` decrementa `n` exactamente una vez cuando la eliminación tiene éxito.

Después deben preservarse:

```text
root
parent
n
alcanzabilidad
orden BST
unicidad
```

`inorder` ayuda a verificar claves y orden, pero no demuestra por sí solo que `parent` o `n` sean correctos.

La complejidad es:

```text
splice   -> O(1)
findLast -> O(h)
remove   -> O(h)
```

La limitación final del BST simple permanece:

```text
h puede crecer hasta O(n)
```

Ese problema prepara el estudio de árboles AVL en la Semana 6.
