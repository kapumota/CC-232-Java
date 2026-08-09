### CC-232: Estructuras de Datos en Java

Repositorio  para el curso **CC-232: Algoritmos y Estructuras de Datos**.

El material utiliza Java sin Maven, Gradle ni dependencias externas. Cada archivo es autocontenido, compila con `javac` y concentra la sesión en la representación, los invariantes, las operaciones esenciales, las trazas y la complejidad.

#### Propósito

El repositorio proporciona ejemplos y esqueletos de implementación para las sesiones presenciales. El estudiante utiliza la computadora durante el laboratorio para leer, completar, compilar y probar el código.

Las evaluaciones se realizan en hoja y papel. El objetivo es comprobar que el estudiante comprende la lógica, puede seguir una traza, identifica invariantes y completa fragmentos esenciales sin depender del compilador ni de herramientas de inteligencia artificial.

#### Convenciones del código

- Se recomienda Java 21, también se admite Java 17 o posterior.
- Cada archivo contiene una clase pública autocontenida.
- Los comentarios están escritos en español.
- Las cadenas de texto están escritas en español.
- Las firmas de las funciones y los métodos están escritas en inglés.
- Los identificadores técnicos de Java se conservan en inglés cuando forman parte del código.
- Cada sesión contiene uno o dos `TODO(alumno)` centrales.
- Los ejemplos de `main()` son públicos.
- Las soluciones completas del docente son privadas.

#### Compilación y ejecución

Ejemplo para compilar y ejecutar la práctica de BFS:

```bash
javac semana11_grafos_bfs/Semana11_GrafoBFS1.java
java -cp semana11_grafos_bfs Semana11_GrafoBFS1
```

#### Contenido obligatorio y calendario 2026-2

| Semana | Fechas | Tema | Evaluación |
|---:|---|---|---|
| 1 | 31 de agosto y 4 de septiembre | ADT, complejidad y arreglos dinámicos | Sin evaluación |
| 2 | 7 y 11 de septiembre | Listas enlazadas | Control 1 |
| 3 | 14 y 18 de septiembre | Pilas, colas y deques | Práctica 1 |
| 4 | 21 y 25 de septiembre | BST, búsqueda, inserción y recorridos | Control 2 |
| 5 | 28 de septiembre y 2 de octubre | BST, eliminación y casos borde | Práctica 2 |
| 6 | 5 y 9 de octubre | AVL, balance y rotaciones | Control 3 |
| 7 | 12 y 16 de octubre | Heap y cola de prioridad | Práctica 3 |
| Parcial | 19 al 24 de octubre | Contenidos de las semanas 1 a 7 | Examen parcial |
| 8 | 26 y 30 de octubre | Hashing con encadenamiento | Sin evaluación |
| 9 | 2 y 6 de noviembre | Factor de carga y rehashing | Control 4 |
| 10 | 9 y 13 de noviembre | Map y Set | Práctica 4 |
| 11 | 16 y 20 de noviembre | Grafos, representación y BFS | Sin evaluación |
| 12 | 23 y 27 de noviembre | Grafos, DFS y componentes conexas | Control 5 |
| 13 | 30 de noviembre y 4 de diciembre | Selección e integración | Práctica 5 |
| 14 | 7 y 11 de diciembre | Repaso integrador | Sin evaluación |

#### Evaluaciones en papel

Todas las evaluaciones son individuales, presenciales y se resuelven en hoja y papel.

| Componente | Puntaje | Evidencia principal |
|---|---:|---|
| Control | 8 | Trazas, diagramas, fragmentos de Java, invariantes y complejidad |
| Práctica Calificada | 12 | Representación, operaciones centrales, casos borde y análisis |
| Nota de cada PC | 20 | Suma del control y la práctica correspondiente |

El laboratorio se desarrolla en computadora y sirve para implementar, compilar y practicar. Durante los controles, las prácticas calificadas, el examen parcial y el examen final no se utilizan computadoras, teléfonos, Internet ni asistentes de inteligencia artificial.

Las preguntas de código se formulan en Java. Los errores menores de sintaxis se valoran de manera proporcional cuando la lógica y el invariante son correctos.

#### Bloque de grafos basado en Morin

Las semanas 11 y 12 adaptan las ideas de `Graph`, `AdjacencyLists`, `AdjacencyMatrix` y los recorridos de `Algorithms.java`, de Pat Morin.

El bloque incluye:

- vértices identificados con valores de `0` a `n - 1`,
- listas de adyacencia,
- matrices de adyacencia,
- consulta de aristas y grado de salida,
- BFS con una cola y marcado al encolar,
- DFS con estados blanco, gris y negro,
- componentes conexas mediante recorridos repetidos.

Las adaptaciones eliminan `package ods`, las fábricas, los iteradores no esenciales y las dependencias entre archivos. El resultado son prácticas independientes y apropiadas para una sesión de laboratorio.

#### Material adicional

La carpeta `adicionales/` contiene demostraciones breves que no forman parte de las evaluaciones obligatorias.

La carpeta `adicionales_avanzados/` conserva Fenwick Tree, Segment Tree, Lazy Propagation, Sparse Table, Suffix Array, B-Tree y algoritmos de texto. Estos temas se reservan para cursos posteriores o para ampliación voluntaria.

#### Bibliografía

**Texto principal**:

- Pat Morin, *Open Data Structures*.

Referencias complementarias:

- Mark Allen Weiss, *Data Structures and Algorithm Analysis in Java*.
- Robert Sedgewick y Kevin Wayne, *Algorithms*.
- Michael T. Goodrich, Roberto Tamassia y Michael H. Goldwasser, *Data Structures and Algorithms in Java*.
