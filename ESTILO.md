### Guía de estilo

Esta guía define las convenciones de escritura del repositorio CC-232.

#### Código Java

- Los comentarios se escriben en español.
- Las cadenas de texto se escriben en español.
- Las firmas de funciones y métodos se escriben en inglés.
- Los nombres de parámetros y variables principales se escriben en inglés.
- Los términos propios de Java, como `ArrayList`, `HashMap`, `TreeSet` y `Iterator`, conservan su nombre oficial.
- Cada archivo contiene una clase pública autocontenida.
- Cada actividad incluye uno o dos `TODO(alumno)` centrales.

Ejemplo:

```java
// TODO(alumno): agregar la arista y conservar el invariante.
void addEdge(int source, int destination)
```

#### Términos recomendados

| Término en la documentación | Identificador en el código |
|---|---|
| arreglo dinámico | `ArrayStack` |
| lista enlazada | `SLList`, `DLList` |
| montículo | `BinaryHeap`, `MinHeap` |
| tabla hash | `HashTable`, `HashSet` |
| vértice de origen | `source` |
| vértice de destino | `destination` |
| visitado | `visited` |
| distancia | `distance` |
| componentes conexas | `connectedComponents` |

