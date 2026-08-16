### Ejercicios de la Semana 6

Estos ejercicios son opcionales y no requieren entrega obligatoria.

La parte de consolidación es deliberadamente breve. La mayor parte de la lista contiene problemas retadores que combinan conceptos de las Semanas 1, 2, 3, 4, 5 y 6. 
El objetivo no es repetir la Actividad 6 ni volver a resolver los mismos árboles utilizados para introducir `rotateRight`, `rotateLeft`, LL, RR, LR y RL.

La mayoría de los problemas exige:

```text
reconstruir estados
razonar antes de ejecutar
detectar código que compila pero es incorrecto
preservar varios invariantes simultáneamente
separar estado lógico de representación
combinar estructuras de semanas anteriores
justificar complejidad
diseñar contraejemplos
diseñar pruebas
```

Los temas centrales de la Semana 6 utilizados son:

```text
BST degenerado
altura h
height almacenada
height(null) = 0
hoja de altura 1
updateHeight
información derivada
balanceFactor
height(left) - height(right)
invariante AVL
nodo balanceado y desbalanceado

rotaciones
preservación del inorder
rotateRight
rotateLeft
subárbol intermedio
nueva raíz local
actualización de alturas
LL
RR
LR
RL
rotaciones dobles

inserción recursiva
retorno de la nueva raíz local
actualización al regresar
rebalance
rechazo de duplicados
búsqueda O(log n)
inserción O(log n)
costo O(1) de una rotación
BST simple frente a AVL
```

También se reutilizan ideas anteriores cuando son necesarias:

```text
arreglos dinámicos
tamaño y capacidad
costo amortizado
listas enlazadas
referencias
localización frente a modificación
Stack
Queue
ArrayQueue circular
estado lógico frente a estado físico
BST
findLast
contains
add
remove
splice
sucesor inorder
invariantes
alcanzabilidad
trazas
complejidad
```

No se requiere utilizar `TreeSet`, `TreeMap`, `java.util.Stack`, `java.util.ArrayDeque` ni otras estructuras de Java Collections.

No se requiere implementar eliminación AVL.

No se requiere estudiar Red-Black Trees, Splay Trees, Treaps ni otras familias de árboles balanceados.

Los ejercicios de la sección C son ampliaciones opcionales. Pueden introducir verificadores o extensiones de representación, pero deben resolverse con las ideas disponibles hasta la Semana 6.

### A. Consolidación esencial

#### Ejercicio 1. Un árbol visualmente correcto tiene metadatos incorrectos

Se observa el siguiente árbol:

```text
                 58(h=5)
               /         \
          31(h=3)       84(h=3)
          /    \         /    \
      17(h=1) 45(h=2) 72(h=1) 96(h=1)
               /
           39(h=1)
```

Las claves y los enlaces `left` y `right` son exactamente los mostrados.

Usa la convención:

```text
height(null) = 0
hoja = 1
balanceFactor(u)
    = height(u.left) - height(u.right)
```

Sin ejecutar código:

1. determina la altura real de cada nodo,
2. identifica todos los campos `height` incorrectos,
3. indica en qué orden deberían corregirse mediante `updateHeight`,
4. calcula el factor de balance real de cada nodo,
5. determina si el árbol es un BST válido,
6. determina si es un AVL válido,
7. explica por qué un inorder correcto no detectaría necesariamente los errores de `height`,
8. explica por qué calcular `balanceFactor` usando alturas almacenadas incorrectas puede producir una conclusión falsa,
9. indica qué costo tendría corregir únicamente la altura de todos los nodos si no sabemos cuáles campos están dañados.

Después responde:

> ¿Qué diferencia existe entre que la forma sea correcta, que los metadatos sean correctos y que el invariante AVL sea correcto?.

#### Ejercicio 2. Una rotación con subárbol intermedio no vacío

Considera:

```text
                    80
                  /    \
                50      95
               /  \
             30    65
                  /  \
                58    72
               /
             55
```

Se desea realizar una rotación derecha alrededor de 80.

Sin escribir primero el código:

1. identifica la raíz local antigua,
2. identifica la nueva raíz local,
3. identifica el subárbol intermedio,
4. explica qué intervalo de claves representa ese subárbol,
5. dibuja el árbol después de la rotación,
6. escribe el inorder antes y después,
7. enumera exactamente qué referencias cambian,
8. calcula las alturas antes y después suponiendo que inicialmente eran correctas,
9. indica el orden correcto de las llamadas a `updateHeight`,
10. explica qué ocurriría si la referencia al subárbol intermedio se sobrescribe antes de guardarla,
11. justifica por qué la rotación sigue siendo `O(1)` aunque el subárbol intermedio contenga varios nodos.

No uses un ejemplo de tres nodos. El objetivo es verificar que la rotación funcione en el caso general.

### B. Retos integradores

#### Reto 1. Dos rotateRight compilan, pero solo uno conserva toda la estructura

Se proponen dos versiones.

Versión A:

```java
static Node rotateRight(Node y) {
    Node x = y.left;

    x.right = y;
    y.left = x.right;

    updateHeight(y);
    updateHeight(x);

    return x;
}
```

Versión B:

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

No las ejecutes inicialmente.

1. determina cuál conserva correctamente el subárbol intermedio,
2. construye el árbol más pequeño donde ambas versiones no produzcan el mismo resultado,
3. realiza la traza asignación por asignación de la versión incorrecta,
4. identifica el instante exacto en que una referencia útil se pierde o cambia de significado,
5. determina si el inorder final sigue siendo correcto,
6. determina si puede aparecer un ciclo,
7. explica por qué ambas versiones tienen el mismo orden asintótico y solo una es correcta,
8. construye un caso donde el error quede oculto porque el subárbol intermedio sea `null`,
9. formula una regla general sobre cuándo debe guardarse una referencia antes de modificar enlaces.

#### Reto 2. Un rebalance con signos invertidos

Se propone:

```java
private Node rebalance(Node u) {
    int bf = balanceFactor(u);

    if (bf > 1) {
        if (balanceFactor(u.left) > 0) {
            u.left = rotateLeft(u.left);
        }
        return rotateRight(u);
    }

    if (bf < -1) {
        if (balanceFactor(u.right) < 0) {
            u.right = rotateRight(u.right);
        }
        return rotateLeft(u);
    }

    return u;
}
```

Analiza sin ejecutar.

1. identifica qué significan `bf > 1` y `bf < -1`,
2. determina qué condición debería distinguir LL de LR,
3. determina qué condición debería distinguir RR de RL,
4. construye una secuencia mínima de inserciones que revele el primer error,
5. construye otra que revele el segundo,
6. dibuja el estado justo antes de llamar a `rebalance`,
7. indica qué rotación equivocada ejecutaría el código,
8. explica si puede ocurrir una excepción o simplemente obtenerse una forma incorrecta,
9. corrige únicamente las condiciones necesarias,
10. explica por qué memorizar LL, RR, LR y RL sin comprender el signo de `balanceFactor` facilita este tipo de defecto.

#### Reto 3. Mismas claves, tres representaciones y tres historias de costo

Se desea almacenar exactamente estas claves:

```text
8, 14, 19, 27, 33, 41, 56, 63, 75
```

Construye tres estructuras independientes:

```text
A. arreglo dinámico ordenado
B. BST simple
C. AVL
```

Restricciones:

- en el arreglo, las claves deben mantenerse siempre ordenadas,
- en el BST simple se insertan en el orden mostrado,
- en el AVL se insertan también en el orden mostrado.

Responde:

1. reconstruye la evolución del BST simple,
2. reconstruye la evolución del AVL e identifica todas las rotaciones,
3. dibuja ambos árboles finales,
4. calcula sus alturas,
5. compara `contains(75)` en ambos árboles,
6. compara la búsqueda de una clave inexistente mayor que 75,
7. para el arreglo ordenado explica el costo de localizar una clave con búsqueda binaria si se permitiera usarla,
8. explica el costo de insertar una nueva clave en la posición correcta del arreglo,
9. compara la cantidad de estado adicional mantenido por cada representación,
10. identifica qué invariante principal mantiene cada estructura,
11. explica por qué no tiene sentido afirmar que una de las tres estructuras es siempre mejor sin especificar el patrón de operaciones.

#### Reto 4. La nueva raíz local se pierde dentro de un árbol mayor

Se tiene:

```text
                    90
                  /    \
                40      120
               /  \
             20    60
            /
          10
```

Supón que el subárbol cuya raíz es 40 debe ser rebalanceado.

Un estudiante llama conceptualmente:

```text
rebalance(40)
```

pero ignora el valor retornado.

Otro estudiante realiza conceptualmente:

```text
90.left = rebalance(40)
```

Responde:

1. identifica el nodo desbalanceado,
2. determina el caso correspondiente,
3. dibuja el subárbol antes y después de repararlo,
4. determina qué referencia retorna `rebalance`,
5. dibuja el árbol global si se ignora el valor retornado,
6. dibuja el árbol global si se reasigna `90.left`,
7. explica por qué una rotación local correcta puede producir un árbol global incorrecto si no se reconecta la nueva raíz,
8. relaciona este problema con las reconexiones estudiadas en listas enlazadas y en `splice`,
9. explica por qué `root = add(root, x)` resuelve el caso análogo cuando el subárbol modificado es todo el árbol.

#### Reto 5. Una ArrayQueue determina la historia de inserción del AVL

Una `ArrayQueue` circular tiene:

```text
a.length = 8
j = 5
n = 7
```

y el arreglo físico:

```text
índice     0    1    2    3    4    5    6    7
a         48   62   57   59    _   36   24   72
```

Por el invariante circular, la secuencia lógica es:

```text
[36, 24, 72, 48, 62, 57, 59]
```

Se ejecuta repetidamente:

```text
x = q.remove()
tree.add(x)
```

hasta vaciar la cola.

Sin ejecutar código:

1. verifica la vista lógica a partir de `j` y `n`,
2. indica la posición física retirada en cada `remove`,
3. registra la evolución de `j`,
4. inserta las claves en el AVL en el orden en que salen de la cola,
5. dibuja el árbol después de cada inserción,
6. identifica toda rotación simple o doble,
7. registra qué nodos cambian de altura después de cada inserción,
8. dibuja el AVL final,
9. escribe su inorder,
10. calcula su altura,
11. determina el costo asintótico de vaciar la cola e insertar todos sus elementos en el AVL en términos de `m`, donde `m` es el número de elementos procesados,
12. separa el costo amortizado de `ArrayQueue.remove()` del costo de `AVL.add()`.

#### Reto 6. Usar una pila para registrar un camino de búsqueda

Se desea agregar a un AVL una operación de diagnóstico:

```java
boolean traceContains(int x, IntStack path)
```

La pila enlazada `IntStack` ofrece:

```java
void push(int x)
int pop()
boolean isEmpty()
```

La operación debe buscar `x` como en un BST.

Cada nodo visitado debe agregarse a `path`.

No debe modificar el AVL.

Para el árbol:

```text
                    50
                  /    \
                25      80
               /  \    /  \
             10   35  65   90
                       \
                        70
```

responde:

1. escribe el contenido que queda en la pila después de buscar 70,
2. indica el orden de la pila desde el tope hacia abajo,
3. indica el contenido después de buscar 68 en una pila inicialmente vacía,
4. implementa `traceContains`,
5. explica qué invariante del AVL utiliza para decidir izquierda o derecha,
6. explica por qué `height` no es necesaria para la búsqueda,
7. determina el tiempo de la búsqueda,
8. determina el espacio adicional utilizado por la pila,
9. compara este espacio con una búsqueda iterativa que no registra el camino,
10. explica por qué una estructura auxiliar puede ser útil para diagnóstico aunque no sea necesaria para la operación abstracta `contains`.

#### Reto 7. Actualizar height antes de tiempo

Se propone esta variante de inserción:

```java
private Node add(Node u, int x) {
    if (u == null) {
        return new Node(x);
    }

    updateHeight(u);

    if (x < u.x) {
        u.left = add(u.left, x);
    } else if (x > u.x) {
        u.right = add(u.right, x);
    } else {
        return u;
    }

    return rebalance(u);
}
```

Responde sin ejecutar:

1. explica por qué la llamada a `updateHeight(u)` ocurre demasiado pronto,
2. construye una inserción donde la altura de un hijo cambie después de esa llamada,
3. muestra qué valor antiguo queda almacenado en `u.height`,
4. muestra cómo ese valor puede afectar a un ancestro superior,
5. explica si `balanceFactor(u)` usa directamente `u.height` o las alturas de sus hijos,
6. explica por qué aun así una altura almacenada obsoleta puede afectar decisiones posteriores,
7. corrige el orden de las operaciones,
8. formula la regla general de dependencia entre modificación de hijos y actualización de metadatos,
9. relaciona este defecto con otros estados derivados estudiados en semanas anteriores.

#### Reto 8. Eliminar en BST y reconstruir mediante inserciones AVL

Considera el BST simple:

```text
                    50
                  /    \
                25      80
               /  \    /  \
             10   35  65   95
                 /    / \
               30    60 70
```

Primero trabaja únicamente como BST de Semana 5.

Se ejecuta:

```text
remove(50)
```

Después se toman las claves restantes en inorder y se insertan, en ese orden, en un AVL inicialmente vacío.

Responde:

1. identifica el sucesor inorder de 50,
2. muestra el estado después de copiar la clave del sucesor pero antes de retirar físicamente el sucesor,
3. ejecuta conceptualmente `splice` sobre el nodo correspondiente,
4. dibuja el BST final,
5. escribe su inorder,
6. verifica que el tamaño lógico disminuyó exactamente una vez,
7. usa ese inorder como historia de inserción del AVL nuevo,
8. reconstruye las rotaciones que aparecen durante la construcción del AVL,
9. dibuja el AVL final,
10. compara la altura del BST después de `remove` con la altura del AVL reconstruido,
11. explica por qué este ejercicio no implementa eliminación AVL,
12. identifica qué conceptos pertenecen a Semana 5 y cuáles a Semana 6.

#### Reto 9. Diseñar un verificador que no confíe en height

Se desea auditar un AVL posiblemente corrupto.

No puedes asumir que los campos `height` almacenados sean correctos.

Diseña conceptualmente:

```java
int validateAVL(Node u, Integer low, Integer high)
```

La convención será:

```text
si el subárbol es válido
    retornar su altura real

si existe alguna violación
    retornar -1
```

El método debe detectar:

```text
violación del orden BST
height almacenada incorrecta
violación del balance AVL
```

Responde:

1. explica qué significan `low` y `high`,
2. determina el caso base,
3. explica cómo obtener la altura real del subárbol izquierdo,
4. explica cómo obtener la altura real del subárbol derecho,
5. indica cuándo debe fallar por orden,
6. indica cuándo debe fallar por `height`,
7. indica cuándo debe fallar por balance,
8. escribe una implementación recursiva,
9. justifica su complejidad temporal,
10. justifica la profundidad máxima de la recursión,
11. explica por qué usar directamente `balanceFactor(u)` no basta si no confiamos en las alturas almacenadas,
12. construye un árbol que sea BST válido pero falle únicamente por `height`,
13. construye otro que tenga `height` correcta pero falle por balance.

#### Reto 10. Una misma secuencia, arreglo dinámico y AVL

Se reciben estas operaciones sobre un conjunto de claves distintas:

```text
insertar 42
insertar 18
insertar 63
insertar 9
insertar 27
insertar 54
insertar 72
buscar 54
buscar 26
insertar 24
buscar 24
```

Compara dos implementaciones.

Implementación A:

```text
arreglo dinámico siempre ordenado
```

Implementación B:

```text
AVL
```

Para cada una:

1. describe el estado después de cada inserción,
2. indica qué información debe mantenerse,
3. cuenta desplazamientos del arreglo cuando correspondan,
4. identifica las rotaciones del AVL,
5. cuenta comparaciones en las tres búsquedas,
6. expresa los costos asintóticos relevantes,
7. explica dónde aparece el costo amortizado en el arreglo dinámico,
8. explica por qué ese análisis amortizado no es la razón por la que AVL obtiene `O(log n)`,
9. compara el costo de insertar cerca del inicio del arreglo con insertar una clave profunda en el AVL,
10. explica qué estructura elegirías si las búsquedas dominan ampliamente y las inserciones son escasas,
11. explica qué información adicional necesitarías sobre el patrón de uso para tomar una decisión fundamentada.

#### Reto 11. Añadir n al AVL sin romper la semántica de conjunto

La implementación didáctica de Semana 6 no almacena tamaño.

Se desea extenderla con:

```java
private int n;
```

La operación pública seguirá siendo:

```java
void add(int x)
```

y los duplicados seguirán rechazándose.

Diseña una solución que mantenga:

```text
n = número de nodos alcanzables desde root
```

Responde:

1. identifica por qué incrementar `n` al entrar en `add(int x)` sería incorrecto,
2. explica qué debe ocurrir cuando la clave ya existe,
3. propone una estrategia para saber si realmente se creó una hoja,
4. indica si una rotación modifica `n`,
5. justifica por qué LL, RR, LR y RL deben conservar exactamente el mismo tamaño,
6. implementa una versión coherente de la inserción que mantenga `n`,
7. construye una secuencia con duplicados y verifica el valor de `n` después de cada llamada,
8. explica qué nuevo invariante se agregó a la representación,
9. compara esta obligación con el mantenimiento de `n` en Semanas 1, 2, 4 y 5.

#### Reto 12. Diseñar una batería mínima de pruebas para AVL

Recibes una implementación desconocida con:

```text
add
contains
preorder
```

y acceso de prueba a:

```text
root
left
right
height
```

Diseña una batería pequeña pero exigente que permita detectar errores en:

```text
height de una hoja
updateHeight
rotateRight
rotateLeft
subárbol intermedio
LL
RR
LR
RL
duplicados
nueva raíz global
nueva raíz local interna
árbol ya balanceado
inorder
balanceFactor
```

Para cada prueba indica:

```text
estado inicial
operación
propiedad que intenta verificar
resultado estructural esperado
altura esperada
factor esperado
defecto que permitiría detectar
```

Incluye obligatoriamente:

1. una rotación con `middle == null`,
2. una rotación con `middle != null`,
3. una rotación en la raíz global,
4. una rotación dentro de un subárbol,
5. un duplicado,
6. una secuencia que no requiera ninguna rotación,
7. los cuatro casos LL, RR, LR y RL,
8. una comprobación donde preorder parezca razonable pero una altura esté corrupta.

Concluye:

> ¿Por qué probar solamente las cuatro secuencias mínimas de tres nodos no es suficiente para confiar en una implementación AVL?.

### C. Ampliaciones opcionales

#### Ampliación 1. Reparar alturas no convierte automáticamente un BST en AVL

Se tiene un BST con enlaces correctos y claves ordenadas, pero todos los campos `height` contienen valores arbitrarios.

Diseña:

```java
int rebuildHeights(Node u)
```

que recalcule todas las alturas desde la forma real.

Después responde:

1. qué debe retornar para `null`,
2. en qué orden debe visitar los hijos,
3. qué debe almacenar en `u.height`,
4. cuál es su complejidad,
5. qué ocurre si el árbol es degenerado,
6. por qué ejecutar `rebuildHeights(root)` puede dejar todas las alturas correctas y aun así el árbol no ser AVL,
7. construye un ejemplo concreto,
8. explica qué trabajo adicional sería necesario para cambiar la forma.

No implementes una operación global de balanceo.

#### Ampliación 2. Comparar representación con parent frente a retorno recursivo

Imagina dos implementaciones AVL.

Versión A:

```text
cada Node almacena parent
la inserción localiza una hoja
después sube explícitamente por parent
```

Versión B:

```text
no almacena parent
la inserción es recursiva
cada llamada retorna la nueva raíz local
```

Compara ambas representaciones.

1. qué estado adicional mantiene A,
2. qué información implícita proporciona la pila de llamadas en B,
3. cómo se reconecta una nueva raíz local en cada diseño,
4. qué referencias adicionales debe actualizar una rotación en A,
5. qué riesgo aparece si `parent` queda obsoleto,
6. qué riesgo aparece en B si se ignora el valor retornado,
7. qué complejidad asintótica tiene la inserción en ambas,
8. explica por qué dos implementaciones del mismo ADT pueden tener invariantes internos distintos.

#### Ampliación 3. Experimento manual sobre altura

Para cada valor:

```text
n = 7
n = 15
n = 31
```

construye conceptualmente:

```text
A. un BST completamente degenerado
B. un árbol de altura pequeña compatible con AVL
```

No necesitas enumerar todas las historias posibles.

Después:

1. compara las alturas,
2. estima el máximo número de nodos examinados por una búsqueda,
3. explica la diferencia entre `O(h)` y `O(log n)`,
4. explica qué propiedad local impide que el AVL adopte la forma degenerada,
5. indica por qué este experimento es evidencia intuitiva pero no constituye una demostración formal de la cota de altura AVL.

#### Ampliación 4. Elegir estructura a partir de una carga de trabajo completa

Para cada escenario elige entre:

```text
arreglo dinámico
SLList
DLList
LinkedStack
ArrayQueue
BST simple
AVL
```

No todas las estructuras son razonables en todos los casos.

Escenario A:

```text
se necesita acceso intenso por índice
casi nunca se inserta en el medio
```

Escenario B:

```text
se procesan elementos estrictamente FIFO
```

Escenario C:

```text
se dispone de referencias directas a nodos
y se modifica localmente alrededor de ellas
```

Escenario D:

```text
se necesita pertenencia por clave
las claves pueden llegar casi ordenadas
se quiere evitar el peor caso lineal
```

Escenario E:

```text
se necesita pertenencia por clave
el conjunto es muy pequeño
la simplicidad de implementación domina
```

Escenario F:

```text
se necesita deshacer la operación más reciente
```

Para cada escenario:

1. elige una representación,
2. identifica el ADT o comportamiento dominante,
3. describe su invariante principal,
4. indica el costo de la operación más frecuente,
5. identifica el costo que estás dispuesto a aceptar,
6. explica por qué al menos una alternativa es menos adecuada,
7. indica qué información adicional podría cambiar tu decisión.

### Síntesis de la lista

Esta lista está diseñada para reforzar una idea que atraviesa las seis primeras semanas:

```text
una estructura de datos
no se comprende solamente
por el código de sus operaciones
```

Es necesario relacionar:

```text
representación
invariantes
algoritmos
estado lógico
estado interno
complejidad
```

En Semana 6 esa relación aparece con especial claridad.

Un AVL conserva el invariante BST, pero añade:

```text
height
balanceFactor
rotaciones
```

para restringir la forma y mantener:

```text
h = O(log n)
```

Los problemas más importantes de esta lista no consisten en repetir:

```text
30, 20, 10
```

o memorizar:

```text
LL -> rotateRight
```

Consisten en poder explicar por qué una modificación es correcta, detectar cuándo una representación aparentemente válida está dañada, integrar estructuras anteriores, diseñar contraejemplos y justificar el costo real de cada decisión.
