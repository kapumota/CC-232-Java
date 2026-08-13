### Ejercicios de la Semana 4

Estos ejercicios son opcionales y no requieren entrega obligatoria. 

La mayor parte de los problemas exige combinar ideas de las Semanas 1, 2, 3 y 4, reconstruir estados, revisar código que compila pero puede ser incorrecto, preservar invariantes, justificar decisiones estructurales y analizar costos.

Los temas centrales utilizados son:

```text
estructuras lineales frente a estructuras jerárquicas
árbol, nodo, raíz, padre, hijo, hoja y subárbol
árbol binario
BST como conjunto ordenado
root, left, right, parent y n
clave
invariante global de orden
camino de búsqueda
findLast
contains
add
duplicados
inorder
altura h
O(h)
árbol de poca altura
árbol degenerado
representación, forma y costo
```

También se reutilizan conceptos anteriores cuando ayudan a resolver un problema:

```text
arreglos dinámicos
nodos y referencias
invariantes
localización frente a modificación
Stack
Queue
trazas
complejidad
```

No se requiere utilizar `TreeSet`, `TreeMap`, `java.util.Stack`, `java.util.ArrayDeque` ni otras estructuras de Java Collections.

No se desarrolla todavía eliminación en BST, `splice`, sucesor para eliminar, rotaciones ni AVL.

Los ejercicios de la sección C son ampliaciones opcionales. Pueden pedir una operación nueva, pero debe resolverse usando únicamente las ideas disponibles hasta la Semana 4.

### A. Consolidación esencial

#### Ejercicio 1. Una salida correcta puede ocultar una representación incorrecta

Un depurador muestra:

```text
                52
              /    \
            27      81
           /  \    /  \
         14   33  68   95
```

Además:

```text
root.x = 52
n = 7

27.parent = 52
81.parent = 52
14.parent = 27
33.parent = 52
68.parent = 81
95.parent = 81
```

El método `inorder()` imprime:

```text
14 27 33 52 68 81 95
```

Responde:

1. determina si el invariante de orden del BST se cumple,
2. determina si la representación completa es válida,
3. identifica la referencia incorrecta,
4. explica por qué `inorder()` no detecta el problema,
5. escribe un invariante de orden y otro de coherencia padre-hijo,
6. indica qué debería valer `33.parent`,
7. explica por qué `n = 7` puede ser correcto aunque otra parte de la representación sea incorrecta,
8. diseña una comprobación conceptual que permita verificar todos los enlaces `parent`.

El objetivo es distinguir entre:

```text
salida observable correcta

y

representación interna correcta
```

#### Ejercicio 2. Mismas claves, historias distintas y costos distintos

Se insertan las mismas nueve claves en dos BST inicialmente vacíos.

Historia A:

```text
45, 20, 70, 10, 30, 60, 80, 25, 65
```

Historia B:

```text
10, 20, 25, 30, 45, 60, 65, 70, 80
```

Sin ejecutar código:

1. dibuja los dos árboles,
2. calcula `n` y `h`,
3. traza `contains(65)` en ambos,
4. traza `contains(55)` en ambos,
5. cuenta las comparaciones,
6. determina qué retorna `findLast(55)` en cada árbol,
7. explica por qué ambos representan el mismo conjunto ordenado,
8. explica por qué pueden tener costos distintos,
9. expresa los costos usando primero `O(h)`,
10. identifica qué parte depende de la historia de inserciones.

### B. Retos integradores

#### Reto 1. Una búsqueda que compila pero toma decisiones equivocadas

Un estudiante propone:

```java
private Node findLast(int x) {
    Node w = root;
    Node prev = null;

    while (w != null) {
        prev = w;

        if (x < w.x) {
            w = w.right;
        } else if (x > w.x) {
            w = w.left;
        } else {
            return w;
        }
    }

    return prev;
}
```

Considera:

```text
                50
              /    \
            25      75
           /  \    /  \
         10   35  60   90
```

Sin ejecutarlo:

1. traza `findLast(35)`,
2. traza `findLast(65)`,
3. indica qué retorna en ambos casos,
4. identifica las decisiones incorrectas,
5. corrige únicamente las líneas necesarias,
6. relaciona la corrección con el invariante global,
7. explica por qué el método puede seguir siendo `O(h)` y ser incorrecto,
8. construye un árbol mínimo donde el error no sea observable,
9. construye el árbol más pequeño donde el error sí pueda demostrarse.

Distingue:

```text
compila
termina
tiene O(h)
es correcto
```

#### Reto 2. Un add con varios errores de representación

Se propone:

```java
boolean add(int x) {
    Node p = findLast(x);
    Node u = new Node(x);

    if (p == null) {
        root = u;
        n++;
        return true;
    }

    if (x <= p.x) {
        p.left = u;
    } else {
        p.right = u;
    }

    n++;
    return true;
}
```

El árbol inicial es:

```text
                40
              /    \
            20      65
```

Responde:

1. traza `add(30)`,
2. traza `add(20)`,
3. traza `add(80)`,
4. identifica todos los problemas,
5. explica qué ocurre con `parent`,
6. explica qué ocurre con duplicados,
7. determina si `n` sigue correcto después de `add(20)`,
8. determina si inorder puede ocultar alguno de los errores,
9. escribe una versión corregida,
10. explica por qué conviene crear el nodo después de verificar duplicado,
11. separa localización de modificación,
12. justifica `O(h)`.

#### Reto 3. Un verificador local acepta un árbol que no es BST

Se propone:

```java
boolean validLocal(Node u) {
    if (u == null) {
        return true;
    }

    if (u.left != null && u.left.x >= u.x) {
        return false;
    }

    if (u.right != null && u.right.x <= u.x) {
        return false;
    }

    return validLocal(u.left) && validLocal(u.right);
}
```

Responde:

1. explica qué propiedad sí verifica,
2. construye un árbol mínimo donde retorne `true` pero el árbol no sea BST,
3. identifica la restricción impuesta por un ancestro que se viola,
4. explica por qué padre-hijo no basta,
5. propone una especificación recursiva con límite inferior y superior,
6. escribe pseudocódigo para `validBST(Node u, Integer low, Integer high)`,
7. explica qué significa cada límite,
8. determina el costo de revisar un árbol con `n` nodos,
9. explica por qué esta verificación puede ser `O(n)` aunque `contains(x)` siga un solo camino,
10. indica qué falta verificar para comprobar también `parent`.

No utilices inorder como única solución.

#### Reto 4. Reparar parent sin cambiar la forma ni las claves

Se sabe que `left` y `right` representan correctamente:

```text
                64
              /    \
            32      88
           /  \    /  \
         16   48  72   96
              /
             40
```

Todas las referencias `parent` pueden estar mal.

Diseña:

```java
void repairParents()
```

Restricciones:

```text
no cambiar claves
no cambiar left
no cambiar right
no crear nodos
no eliminar nodos
no modificar n
```

Responde:

1. qué debe valer `root.parent`,
2. cuál debe ser el `parent` de cada nodo,
3. diseña una operación auxiliar recursiva que reciba nodo y padre esperado,
4. escribe el código,
5. traza el subárbol enraizado en 48,
6. justifica por qué cada nodo se visita una vez,
7. determina el tiempo,
8. determina el espacio auxiliar en función de `h`,
9. explica por qué la forma afecta la profundidad de recursión,
10. compara este problema con la coherencia `prev` y `next` de una `DLList`,

#### Reto 5. Convertir el BST en un arreglo ordenado

Se desea:

```java
int[] toSortedArray()
```

Para:

```text
                58
              /    \
            31      82
           /  \    /  \
         12   47  69   91
```

debe producir:

```text
[12, 31, 47, 58, 69, 82, 91]
```

Restricciones:

```text
no usar TreeSet
no usar ArrayList
no ordenar después
no modificar el árbol
```

Puedes crear:

```java
new int[n]
```

Responde:

1. por qué `n` permite reservar el tamaño exacto,
2. qué recorrido debe utilizarse,
3. por qué preorder no garantiza orden creciente,
4. diseña una versión recursiva con índice de escritura,
5. escribe una implementación,
6. traza el índice al procesar el subárbol de 31,
7. verifica que se escriban exactamente `n` elementos,
8. justifica tiempo `O(n)`,
9. analiza espacio del arreglo de salida,
10. analiza espacio de recursión en función de `h`,
11. explica qué idea de Semana 1 reaparece.

#### Reto 6. Inorder iterativo reutilizando una pila enlazada

Diseña:

```java
void inorderIterative()
```

Restricciones:

```text
no usar recursión
no usar java.util.Stack
no usar java.util.ArrayDeque
no modificar el BST
```

Puedes reutilizar una pila enlazada que almacene referencias `Node`.

Árbol:

```text
                50
              /    \
            25      75
           /  \      \
         10   35      90
```

Responde:

1. por qué necesitas recordar nodos mientras bajas por la izquierda,
2. qué representa el tope,
3. diseña la estructura mínima de `NodeStack`,
4. escribe el algoritmo,
5. traza nodo actual y contenido lógico de la pila,
6. explica por qué cada nodo se apila y desapila una vez,
7. justifica `O(n)`,
8. determina el máximo tamaño de la pila en función de `h`,
9. compara la pila explícita con la pila de llamadas de la versión recursiva.

Este reto conecta Semana 3 con Semana 4.

#### Reto 7. Contar claves de un intervalo usando poda

Se desea:

```java
int countInRange(int low, int high)
```

Considera:

```text
                50
              /    \
            25      75
           /  \    /  \
         10   35  60   90
             /      \
            30       65
```

Para:

```text
low = 28
high = 70
```

las claves válidas son:

```text
30, 35, 50, 60, 65
```

Responde:

1. diseña primero una solución que recorra todos los nodos,
2. usa después el invariante para descartar ramas,
3. si `u.x < low`, explica por qué no necesitas `u.left`,
4. si `u.x > high`, explica por qué no necesitas `u.right`,
5. escribe pseudocódigo o Java con poda,
6. traza la ejecución,
7. identifica nodos no visitados,
8. determina el peor caso,
9. construye un caso con mucha poda,
10. construye otro donde se visite casi todo,
11. explica por qué mejorar el trabajo concreto no cambia necesariamente el peor caso `O(n)`.

No se requiere almacenar tamaños de subárboles.

#### Reto 8. Diseñar una historia de inserciones para controlar la forma

Se tiene:

```text
[5, 12, 19, 27, 34, 41, 50, 63, 71, 84, 96]
```

Diseña dos historias de inserción.

Historia A:

```text
intenta producir poca altura
```

Historia B:

```text
intenta producir la mayor altura posible
```

Responde:

1. escribe ambos órdenes,
2. dibuja ambos árboles,
3. calcula `h`,
4. traza la búsqueda de 71,
5. traza una búsqueda fallida de 72,
6. cuenta comparaciones,
7. explica por qué elegir primero claves cercanas al centro puede ayudar,
8. explica por qué orden creciente o decreciente es adversarial,
9. indica qué no garantiza esta estrategia frente a inserciones futuras,
10. explica por qué esto no es todavía un algoritmo de balanceo.

No utilices rotaciones.

#### Reto 9. Elegir representación según la aplicación

Se diseñan cinco componentes.

Sistema A:

```text
identificadores únicos
muchas consultas de pertenencia
también se imprimen ordenados
las inserciones continúan
```

Sistema B:

```text
90 % de operaciones son get(i)
casi no hay inserciones internas
```

Sistema C:

```text
trabajos en estricto orden de llegada
sin consulta por clave
```

Sistema D:

```text
solo interesa deshacer la operación más reciente
```

Sistema E:

```text
se recibe una referencia directa a un nodo
se modifican enlaces alrededor de ese nodo
sin búsqueda por clave
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

Para cada sistema:

1. elige una representación,
2. identifica el ADT o comportamiento,
3. explica la operación dominante,
4. identifica el invariante principal,
5. justifica costos relevantes,
6. indica una desventaja,
7. propone una alternativa con otro costo.

Concluye:

> ¿Por qué introducir un BST no vuelve obsoletas las estructuras de las Semanas 1, 2 y 3?.

#### Reto 10. Auditoría completa de un índice de identificadores

Un BST se construye mediante:

```text
add(54)
add(26)
add(83)
add(17)
add(39)
add(68)
add(91)
add(32)
add(44)
add(61)
add(73)
```

Después:

```text
contains(44)
contains(70)
add(70)
add(39)
contains(70)
```

Realiza una auditoría completa.

Primero:

1. dibuja el árbol tras las once primeras inserciones,
2. escribe `root.x`,
3. calcula `n`,
4. calcula `h`,
5. escribe inorder,
6. verifica el invariante respecto de 54, 26, 83 y 39.

Después:

7. traza `contains(44)`,
8. traza `contains(70)`,
9. determina `findLast(70)` antes de insertar,
10. indica dónde se conecta 70,
11. indica su `parent`,
12. actualiza `n`,
13. explica qué ocurre con `add(39)`,
14. verifica que `n` no cambie por el duplicado,
15. traza el segundo `contains(70)`.

Al finalizar:

16. escribe el nuevo inorder,
17. calcula la nueva altura,
18. identifica la operación con más comparaciones,
19. justifica los costos mediante `h`,
20. distingue evidencia sobre comportamiento de evidencia sobre representación,
21. diseña tres pruebas para detectar errores en `parent`, duplicados y `n`.

Concluye:

> ¿Qué evidencia necesitarías para afirmar que el BST está correctamente implementado y no solamente que produjo las salidas esperadas en esta historia?.

### C. Retos de ampliación opcional

#### Reto opcional 1. Codificar el camino desde root

Se desea:

```java
String pathTo(int x)
```

La cadena usa:

```text
L
    seguir left

R
    seguir right
```

Para:

```text
                50
              /    \
            25      75
           /  \    /  \
         10   35  60   90
```

se espera:

```text
pathTo(50) -> ""
pathTo(35) -> "LR"
pathTo(90) -> "RR"
```

Define primero qué debe ocurrir si `x` no existe.

Después:

1. escribe una especificación,
2. implementa sin recorrer ramas innecesarias,
3. traza 35, 90 y una clave ausente,
4. justifica `O(h)`,
5. explica qué información del camino ya estaba implícita en `findLast`.

#### Reto opcional 2. Recorrido por niveles reutilizando Queue

Para:

```text
                50
              /    \
            25      75
           /  \      \
         10   35      90
```

se desea:

```text
50 25 75 10 35 90
```

No uses `java.util.Queue`.

Reutiliza conceptualmente una cola de referencias `Node`.

Responde:

1. qué se encola al inicio,
2. qué ocurre al desencolar,
3. en qué orden se encolan los hijos,
4. realiza una traza completa,
5. escribe el algoritmo,
6. justifica `O(n)`.
7. identifica el máximo número de nodos simultáneos en la cola,
8. compara con inorder,
9. explica por qué no produce necesariamente claves ordenadas.

#### Reto opcional 3. Encontrar la clave mínima sin inorder completo

Diseña:

```java
Integer min()
```

Responde:

1. qué propiedad BST permite decidir el camino,
2. qué nodo contiene la clave mínima,
3. escribe el algoritmo,
4. traza un árbol de poca altura,
5. traza un árbol degenerado hacia la izquierda,
6. explica qué ocurre si el árbol está vacío,
7. justifica `O(h)`,
8. compara con recorrer todos los nodos.

No desarrolles sucesor, predecesor ni eliminación.

### D. Preguntas de cierre

#### Pregunta 1. Un BST no es un arreglo ordenado con referencias

Explica por qué:

```text
[10, 20, 30, 40, 50]
```

y:

```text
        30
       /  \
     20    40
    /        \
  10          50
```

pueden representar el mismo conjunto pero ofrecer costos diferentes.

Relaciona:

```text
representación
localización
modificación
altura
contigüidad
```

#### Pregunta 2. O(h) expresa una dependencia estructural

Explica por qué:

```text
contains(x) -> O(h)
```

es más preciso para un BST simple que afirmar directamente:

```text
contains(x) -> O(log n)
```

Incluye:

```text
forma
historia de inserciones
árbol de poca altura
árbol degenerado
```

#### Pregunta 3. Correctitud significa conservar varias propiedades

Una implementación de `add(x)` produce inorder ordenado en algunas pruebas.

Explica por qué eso no basta para afirmar que el BST es correcto.

Considera:

```text
root
parent
n
duplicados
alcanzabilidad
invariante global de orden
```

Concluye explicando por qué pruebas, trazas e invariantes aportan evidencias diferentes.
