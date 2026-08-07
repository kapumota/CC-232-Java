### Validación

#### Alcance

Todos los archivos Java se validaron de forma independiente con JDK 21.

| Indicador | Resultado |
|---|---:|
| Archivos Java totales | 44 |
| Archivos del núcleo obligatorio | 26 |
| Líneas Java del núcleo | 1720 |
| Marcas `TODO(alumno)` del núcleo | 40 |
| Archivos obligatorios de grafos | 4 |
| Líneas del bloque de grafos | 328 |
| Marcas `TODO(alumno)` de grafos | 4 |

#### Resultados de compilación

- Los 44 archivos Java compilan individualmente.
- Los 26 métodos `main()` del núcleo terminan con código de salida 0.
- No se requieren Maven, Gradle, paquetes Java ni dependencias externas.
- El nombre de cada clase pública coincide con el nombre de su archivo.

#### Alcance de la ejecución pública

Los esqueletos públicos capturan `UnsupportedOperationException` para indicar qué operación está pendiente.

Esta comprobación valida la compilación y la ejecución controlada. No reemplaza la validación funcional de las soluciones privadas.

#### Validación funcional del bloque de grafos

Las soluciones completas se probaron temporalmente sin incluirlas en el archivo ZIP público.

| Prueba | Resultado esperado |
|---|---|
| Representación por lista y matriz | Vecinos `[1, 3]`, grado 2 |
| BFS | Distancias `[0, 1, 1, 2, 2, 3, -1]` |
| DFS | Orden `[0, 1, 3, 2, 4, 5]` |
| Componentes conexas | 4 componentes |

Las cuatro soluciones produjeron los resultados esperados.

#### Validación del estilo

- Los comentarios están escritos en español.
- Las cadenas de texto están escritas en español.
- Las firmas de funciones y métodos están escritas en inglés.
- La documentación utiliza títulos `###` y subtítulos `####`.
- La prosa no utiliza rayas largas, emoticones ni separadores decorativos.
- La prosa evita los puntos y coma.
- Se revisaron las tildes y los signos de puntuación.
