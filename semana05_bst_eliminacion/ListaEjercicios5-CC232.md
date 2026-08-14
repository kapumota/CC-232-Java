### Ejercicios de la Semana 5

Estos ejercicios son opcionales y no requieren entrega obligatoria.

La parte de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan búsqueda, inserción, eliminación, referencias, invariantes, recorridos, representación, complejidad y conceptos de las Semanas 1, 2, 3, 4 y 5.

Los temas centrales utilizados son:

```text
BST como conjunto ordenado
root, left, right, parent y n
invariante global de orden
findLast
contains
add

eliminación en BST
clave ausente
nodo con 0, 1 o 2 hijos
hoja
hijo superviviente
reconexión padre-hijo
splice
precondición de splice
eliminación de root
mantenimiento de parent

nodo con dos hijos
sucesor inorder
mínimo del subárbol derecho
propiedad del sucesor
copia de la clave
eliminación física del sucesor
remove
n-- exactamente una vez

inorder
alcanzabilidad
altura h
O(h)
forma del árbol
BST de poca altura
BST degenerado
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
arreglos dinámicos
nodos y referencias
invariantes
localización frente a modificación
Stack
Queue
estado lógico frente a representación física
trazas
complejidad
```

No se requiere utilizar `TreeSet`, `TreeMap`, `java.util.Stack`, `java.util.ArrayDeque` ni otras estructuras de Java Collections.

No se requiere implementar AVL, factor de balance ni rotaciones.

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir una operación adicional, pero deben resolverse únicamente con conocimientos disponibles hasta la Semana 5.

### A. Consolidación esencial

#### Ejercicio 1. Reconstruir una eliminación a partir del estado final

Un BST tenía inicialmente:

```text
                62
              /    \
            31      84
           /  \    /  \
         18   45  73   96
             /    / \
            39   68 79
```

Después de una única llamada exitosa a:

```java
remove(x)
```

el árbol queda:

```text
                62
              /    \
            31      84
           /  \    /  \
         18   45  73   96
             /      \
            39       79
```

Se sabe que antes de la operación:

```text
n = 10
```

Responde sin ejecutar código:

1. determina qué clave fue eliminada,
2. clasifica el nodo eliminado por número de hijos en el estado inicial,
3. identifica qué nodo actuó como hijo superviviente, si existió,
4. indica qué referencias `left`, `right` y `parent` cambiaron,
5. indica el nuevo valor de `n`,
6. explica si era necesario buscar un sucesor inorder,
7. escribe el inorder antes y después,
8. justifica por qué el invariante global de orden sigue siendo válido,
9. explica qué observación adicional necesitarías para comprobar que todos los `parent` son correctos.

#### Ejercicio 2. Encontrar un sucesor que no es una hoja

Considera:

```text
                70
              /    \
            35      92
           /  \    /  \
         20   50  81   98
                 /
                76
                  \
                   79
```

Se desea eliminar la clave 70.

Sin escribir todavía `remove` completo:

1. escribe el inorder inicial,
2. identifica el sucesor inorder de 70,
3. muestra el camino utilizado para encontrarlo empezando en `70.right`,
4. explica por qué el sucesor no tiene hijo izquierdo,
5. identifica si el sucesor tiene hijo derecho,
6. explica por qué `splice` sí puede aplicarse al sucesor,
7. dibuja el árbol después de copiar la clave del sucesor pero antes de ejecutar `splice`,
8. dibuja el árbol final,
9. indica qué `parent` debe modificarse,
10. explica por qué `n` disminuye una sola vez.

### B. Retos integradores

#### Reto 1. Un splice que compila pero pierde un subárbol

Un estudiante propone:

```java
private void splice(Node u) {
    Node p = u.parent;

    if (u == root) {
        root = null;
        return;
    }

    if (p.left == u) {
        p.left = null;
    } else {
        p.right = null;
    }
}
```

El método compila.

Analízalo sobre los siguientes estados independientes.

Estado A:

```text
      30
     /
   15
```

Se ejecuta:

```text
splice(15)
```

Estado B:

```text
      30
     /
   15
     \
      22
```

Se ejecuta:

```text
splice(15)
```

Estado C:

```text
root
 |
 v
30
  \
   45
```

Se ejecuta:

```text
splice(30)
```

Para cada estado:

1. realiza la traza exacta del código,
2. dibuja el resultado producido,
3. identifica qué nodo o subárbol se pierde incorrectamente,
4. determina si `root` queda correcto,
5. determina si `parent` queda correcto,
6. indica qué invariante se viola,
7. escribe un contraejemplo mínimo que revele cada defecto,
8. corrige el método,
9. explica por qué la versión corregida sigue siendo `O(1)`.

Concluye explicando por qué:

```text
compilar
```

y:

```text
modificar pocas referencias
```

no garantizan correctitud.

#### Reto 2. remove decrementa n en el lugar equivocado

Se propone:

```java
boolean remove(int x) {
    n--;

    Node u = findLast(x);

    if (u == null || u.x != x) {
        return false;
    }

    if (u.left == null || u.right == null) {
        splice(u);
        return true;
    }

    Node w = u.right;

    while (w.left != null) {
        w = w.left;
    }

    u.x = w.x;
    splice(w);
    n--;

    return true;
}
```

Analiza sin ejecutarlo.

1. explica qué ocurre al llamar `remove(x)` sobre un árbol vacío,
2. explica qué ocurre cuando `x` no existe pero `findLast(x)` retorna un nodo real,
3. explica qué ocurre al eliminar una hoja,
4. explica qué ocurre al eliminar un nodo con un hijo,
5. explica qué ocurre al eliminar un nodo con dos hijos,
6. determina en qué casos `n` queda correcto por casualidad,
7. determina en qué casos queda incorrecto,
8. corrige únicamente la ubicación de las actualizaciones de `n`,
9. explica por qué `splice` no necesita conocer el tamaño lógico,
10. formula una regla general sobre cuándo debe modificarse `n`.

#### Reto 3. El sucesor incorrecto preserva todas las claves menos el orden

Un estudiante sabe que para eliminar un nodo con dos hijos debe usar una clave del subárbol derecho, pero elige siempre:

```java
Node w = u.right;
```

sin continuar por `left`.

Considera:

```text
                50
              /    \
            25      90
                   /  \
                 70   100
                / \
              60  80
```

Se ejecuta:

```text
remove(50)
```

y el estudiante copia:

```text
90
```

en el nodo que contenía 50.

Responde:

1. escribe el inorder inicial,
2. identifica el verdadero sucesor de 50,
3. dibuja el estado después de copiar 90 en la raíz sin modificar los demás enlaces,
4. indica qué parte del invariante global queda violada,
5. identifica una clave concreta que demuestra la violación,
6. explica por qué elegir cualquier clave mayor no es suficiente,
7. reconstruye el algoritmo correcto para localizar `w`,
8. dibuja el árbol final correcto,
9. justifica por qué el mínimo del subárbol derecho es una elección segura,
10. determina la complejidad de localizar ese sucesor en términos de `h`.

#### Reto 4. Inorder correcto, parent incorrecto después de remove

Un BST termina visualmente como:

```text
                72
              /    \
            36      90
           /  \    /
         18   54  81
```

y:

```text
inorder() -> 18 36 54 72 81 90
n = 6
```

Sin embargo, el depurador muestra:

```text
36.parent = 72
90.parent = 72
18.parent = 36
54.parent = 36
81.parent = 72
```

Responde:

1. identifica la referencia `parent` incorrecta,
2. indica su valor correcto,
3. explica por qué `inorder()` no revela el error,
4. explica por qué `n = 6` tampoco revela el error,
5. formula una verificación recursiva de coherencia padre-hijo,
6. escribe pseudocódigo para `validParents(Node u)`,
7. determina el tiempo de esa verificación,
8. determina el espacio de recursión en función de `h`,
9. explica qué concepto de una `DLList` reaparece en esta comprobación,
10. distingue entre comprobar comportamiento del ADT y comprobar representación interna.

#### Reto 5. Una secuencia de eliminaciones cambia la forma y el costo

Se construyen dos BST con las mismas claves.

Árbol A:

```text
                50
              /    \
            25      75
           /  \    /  \
         10   35  60   90
             /      \
            30       65
```

Árbol B:

```text
10
  \
   25
     \
      30
        \
         35
           \
            50
              \
               60
                 \
                  65
                    \
                     75
                       \
                        90
```

En ambos se ejecuta:

```text
remove(30)
remove(60)
remove(90)
contains(65)
remove(25)
```

Para cada árbol:

1. dibuja el estado después de cada operación,
2. calcula `n` después de cada eliminación,
3. calcula `h` después de cada eliminación,
4. registra el camino de búsqueda usado por cada `remove`,
5. registra el camino de `contains(65)`,
6. identifica cuáles eliminaciones usan `splice` directamente,
7. identifica si alguna necesita sucesor,
8. cuenta comparaciones realizadas durante las localizaciones,
9. explica por qué ambos BST representan el mismo conjunto después de cada paso,
10. explica por qué los costos concretos pueden seguir siendo muy diferentes,
11. justifica el peor caso de `remove` como `O(h)`.

#### Reto 6. Auditar remove cuando el sucesor tiene hijo derecho

Se tiene:

```text
                64
              /    \
            32      96
                   /
                  80
                 /
                72
                  \
                   76
```

Se ejecuta:

```text
remove(64)
```

Un estudiante describe el procedimiento así:

```text
1. sucesor = 72
2. copiar 72 en el nodo raíz
3. desconectar 72 de 80 haciendo 80.left = null
4. n--
```

Analiza la propuesta.

1. explica qué parte es correcta,
2. identifica qué nodo se pierde con el paso 3,
3. dibuja el estado incorrecto resultante,
4. escribe la reconexión que realmente debe ocurrir,
5. indica qué debe valer `76.parent`,
6. explica por qué el sucesor puede tener hijo derecho,
7. explica por qué aun así satisface la precondición de `splice`,
8. dibuja el árbol final correcto,
9. escribe el inorder final,
10. identifica los invariantes que deben verificarse al terminar.

#### Reto 7. De BST a arreglo después de una historia de eliminaciones

Se construye un BST mediante:

```text
add(57)
add(28)
add(83)
add(14)
add(41)
add(69)
add(94)
add(35)
add(48)
add(63)
add(74)
```

Después se ejecuta:

```text
remove(14)
remove(69)
remove(57)
```

Sin ejecutar código:

1. dibuja el BST inicial,
2. traza las tres eliminaciones,
3. dibuja el BST final,
4. calcula `n`,
5. calcula `h`,
6. escribe el inorder final,
7. diseña `int[] toSortedArray()` usando exactamente un arreglo de longitud `n`,
8. no utilices una ordenación posterior,
9. explica qué recorrido llena el arreglo directamente en orden,
10. determina el tiempo de la conversión,
11. determina el espacio adicional de la recursión en función de `h`,
12. explica qué idea de Semana 1 reaparece al convertir una representación jerárquica en almacenamiento contiguo.

#### Reto 8. Un registro de operaciones con Stack no restaura la estructura

Un estudiante quiere implementar una función de deshacer y propone guardar únicamente la última clave eliminada en una pila:

```text
removed.push(x)
```

Cuando se solicita deshacer:

```text
x = removed.pop()
add(x)
```

Considera este árbol:

```text
                50
              /    \
            25      75
           /  \    /  \
         10   35  60   90
```

Se ejecuta:

```text
remove(50)
```

y luego:

```text
undo
```

usando la estrategia anterior.

Responde:

1. determina el árbol después de `remove(50)` usando el algoritmo de Semana 5,
2. determina qué ocurre al ejecutar posteriormente `add(50)`,
3. dibuja el árbol obtenido,
4. compara ese árbol con el original,
5. explica por qué recuperar el mismo conjunto de claves no implica recuperar la misma representación,
6. compara los inorder de ambos,
7. compara las alturas,
8. explica qué información estructural faltaría registrar para restaurar exactamente el estado original,
9. relaciona el problema con la diferencia entre estado lógico y representación física estudiada previamente,
10. explica por qué una pila sí modela correctamente el orden LIFO de los deshacer, aunque el contenido almacenado sea insuficiente.

No implementes un sistema completo de undo.

#### Reto 9. Diseñar pruebas que distingan errores de remove

Se sospecha que una implementación de `remove` contiene exactamente uno de estos defectos:

```text
A
no corrige parent del hijo superviviente

B
decrementa n cuando la clave no existe

C
elige u.right como sucesor sin buscar el mínimo

D
no actualiza root al eliminar una raíz con un hijo

E
aplica splice directamente a un nodo con dos hijos
```

Diseña una batería mínima de pruebas.

Para cada defecto:

1. construye el BST más pequeño o uno suficientemente pequeño que permita observarlo,
2. indica la llamada `remove`,
3. escribe el estado esperado,
4. escribe qué salida o invariante revelaría el error,
5. indica si `inorder()` por sí solo detectaría el defecto,
6. indica si `size()` lo detectaría,
7. indica si revisar `parent` lo detectaría,
8. explica por qué la prueba elegida distingue ese defecto de al menos uno de los otros.

Al final organiza tus pruebas en una tabla conceptual:

```text
prueba
estado inicial
operación
propiedad observada
defecto detectado
```

#### Reto 10. Comparar dos diseños de responsabilidad

Se proponen dos diseños.

Diseño A:

```text
splice
    reconecta referencias
    no modifica n

remove
    comprueba existencia
    decide el caso
    llama a splice
    decrementa n una vez
```

Diseño B:

```text
splice
    reconecta referencias
    decrementa n

remove
    comprueba existencia
    decide el caso
    llama a splice
```

No supongas que uno es incorrecto de forma automática.

Responde:

1. explica cómo podría funcionar correctamente el diseño A,
2. explica qué tendría que cumplirse para que el diseño B también fuera correcto,
3. analiza especialmente el caso de dos hijos,
4. identifica qué método posee la responsabilidad del tamaño en cada diseño,
5. explica qué riesgo aparece si ambos métodos modifican `n`,
6. explica qué riesgo aparece si ninguno modifica `n`,
7. indica cuál de los dos diseños coincide con el archivo de la Semana 5,
8. justifica cuál resulta más simple para razonar en este curso,
9. relaciona la decisión con separación de responsabilidades,
10. explica por qué dos diseños diferentes pueden tener la misma complejidad asintótica y distinta claridad.

#### Reto 11. Auditoría completa de una historia mixta

Un BST inicialmente vacío recibe:

```text
add(72)
add(36)
add(108)
add(18)
add(54)
add(90)
add(126)
add(45)
add(63)
add(81)
add(99)
add(117)
add(135)
```

Después:

```text
contains(63)
remove(18)
remove(90)
contains(90)
remove(108)
remove(72)
remove(500)
add(84)
contains(84)
```

Realiza una auditoría completa.

Para cada operación relevante registra:

```text
camino de búsqueda
resultado booleano
caso estructural
sucesor si corresponde
referencias modificadas
n
h
```

Además:

1. dibuja el árbol inicial después de los `add`,
2. dibuja el árbol después de cada `remove` exitoso,
3. determina qué retorna `findLast(500)`,
4. explica por qué `remove(500)` no debe modificar nada,
5. escribe el inorder después de toda la historia,
6. verifica que no existan duplicados,
7. identifica el `parent` de cada nodo cuyo padre cambió,
8. determina cuál operación realizó el camino de localización más largo,
9. justifica el costo de cada eliminación como `O(h)`,
10. explica qué evidencia de la auditoría verifica comportamiento y qué evidencia verifica representación.

Concluye:

> ¿Qué conjunto mínimo de invariantes y observaciones considerarías necesario para confiar en que `remove` funciona más allá de esta historia concreta?.

#### Reto 12. Eliminar no vuelve obsoletas las estructuras anteriores

Se deben resolver cinco aplicaciones.

Aplicación A:

```text
colección de claves enteras distintas
muchas búsquedas
inserciones y eliminaciones
se necesita producir las claves ordenadas
```

Aplicación B:

```text
procesar solicitudes en estricto orden de llegada
las claves no se comparan por magnitud
```

Aplicación C:

```text
deshacer acciones en orden inverso al que ocurrieron
```

Aplicación D:

```text
acceso frecuente por índice
pocas modificaciones internas
```

Aplicación E:

```text
se dispone directamente de una referencia a un nodo
se necesita desconectarlo de una secuencia doble
```

Elige entre:

```text
arreglo dinámico
SLList
DLList
LinkedStack
ArrayQueue circular
BST
```

Para cada aplicación:

1. identifica el comportamiento requerido,
2. elige una representación principal,
3. explica el invariante central,
4. justifica los costos de las operaciones dominantes,
5. explica una desventaja de la elección,
6. indica por qué un BST no sería apropiado en todos los casos,
7. cuando elijas BST, expresa búsqueda, inserción y eliminación primero como `O(h)`.

Concluye:

> ¿Qué ha cambiado desde la Semana 1 en la forma de elegir una estructura de datos y qué principio se ha mantenido igual?.

### C. Retos de ampliación opcional

#### Reto opcional 1. Eliminar todas las claves de un intervalo

Diseña conceptualmente:

```java
int removeRange(int low, int high)
```

que elimine todas las claves `x` tales que:

```text
low <= x && x <= high
```

Restricciones:

```text
puedes reutilizar remove
no utilices TreeSet
no utilices ArrayList
no modifiques remove
```

Responde:

1. explica por qué modificar un árbol mientras lo recorres exige cuidado,
2. propone una estrategia segura para decidir qué clave eliminar a continuación,
3. utiliza únicamente conceptos disponibles hasta Semana 5,
4. prueba tu estrategia sobre un árbol pequeño,
5. indica cuántas claves retorna el método,
6. explica qué ocurre con `n`,
7. analiza un límite superior sencillo del costo usando `n` y `h`,
8. explica por qué este ejercicio no requiere AVL.

No se exige encontrar la solución asintóticamente óptima.

#### Reto opcional 2. Recorrido por niveles después de eliminaciones

Se construye:

```text
                48
              /    \
            24      72
           /  \    /  \
         12   36  60   84
                  / \
                 54 66
```

Después:

```text
remove(12)
remove(60)
```

Sin utilizar `java.util.Queue`:

1. dibuja el árbol final,
2. escribe su inorder,
3. diseña una cola enlazada mínima que almacene referencias `Node`,
4. utiliza esa cola para producir un recorrido por niveles,
5. traza el contenido lógico de la cola,
6. explica por qué cada nodo se encola y desencola una vez,
7. justifica `O(n)`,
8. compara qué propiedad verifica inorder y qué información muestra el recorrido por niveles,
9. explica por qué ninguno de los dos recorridos demuestra por sí solo que `parent` sea correcto.

Este reto reutiliza Queue de Semana 3 sin convertir el recorrido por niveles en contenido obligatorio de Semana 5.

#### Reto opcional 3. Medir la altura después de cada eliminación

Diseña:

```java
int height()
```

sin almacenar un campo `height` dentro de `Node`.

Usa la convención:

```text
árbol vacío
    altura 0

hoja
    altura 1
```

Después analiza:

```text
add(10)
add(20)
add(30)
add(40)
add(50)

remove(50)
remove(40)
remove(30)
```

Responde:

1. dibuja el árbol después de cada operación,
2. calcula `h`,
3. implementa `height()` recursivo,
4. justifica su costo,
5. explica por qué llamar `height()` después de cada operación añade trabajo adicional,
6. distingue calcular altura cuando se necesita de almacenarla y mantenerla en cada nodo,
7. explica por qué esta pregunta conduce naturalmente al problema que se estudiará después, sin implementar AVL ni rotaciones.

### D. Preguntas adicionales

#### Pregunta 1. El caso de dos hijos no es un cuarto algoritmo independiente

Explica por qué el caso:

```text
u tiene dos hijos
```

puede transformarse en:

```text
buscar sucesor
copiar clave
aplicar splice a un nodo con a lo más un hijo
```

Tu explicación debe incluir:

```text
mínimo del subárbol derecho
sucesor inorder
ausencia de hijo izquierdo
preservación del orden
```

#### Pregunta 2. Una eliminación puede ser correcta y seguir siendo costosa

Explica cómo un BST puede preservar correctamente:

```text
root
parent
n
alcanzabilidad
orden
```

y aun así ofrecer:

```text
remove(x) -> O(n)
```

Relaciona:

```text
altura h
forma
historia de operaciones
árbol degenerado
```

y explica por qué la expresión general sigue siendo:

```text
remove(x) -> O(h)
```

#### Pregunta 3. Invariantes, trazas y pruebas observan cosas diferentes

Supón que después de varias eliminaciones se obtiene un inorder creciente y `size()` devuelve el valor esperado.

Explica por qué todavía podría existir un defecto en la representación.

Incluye:

```text
parent
root
alcanzabilidad
orden global
n
```

y concluye indicando qué evidencia adicional usarías antes de considerar correcta una implementación de `remove`.
