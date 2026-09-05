### Validación

#### Alcance

Los archivos Java del repositorio se validan de forma independiente.

La validación actual se realizó con JDK 23.0.2 utilizando `javac --release 17`. De esta forma se comprueba la compilación con el JDK instalado y, al mismo tiempo, que el código sea compatible con Java 17.

| Indicador | Resultado |
|---|---:|
| Archivos Java totales | 46 |
| Archivos del núcleo obligatorio | 28 |
| Archivos obligatorios de grafos | 4 |
| Versión mínima comprobada | Java 17 |
| JDK utilizado para la validación | JDK 23.0.2 |
| Archivos con error de compilación | 0 |

#### Resultados de compilación

- Los 46 archivos Java compilan individualmente.
- Los 46 archivos compilan con compatibilidad objetivo Java 17.
- La validación se realizó con `javac 23.0.2`.
- No se requieren Maven, Gradle ni dependencias externas.
- Los ejemplos no requieren declaraciones `package`.
- El nombre de cada clase pública coincide con el nombre de su archivo.
- Cada archivo se compila de forma independiente.
- Los archivos `.class` se generan únicamente en un directorio temporal.
- La validación no deja archivos `.class` dentro del repositorio.

El resultado de la última ejecución fue:

```text
Resumen
Archivos Java: 46
Correctos: 46
Con errores: 0

Validación correcta.
```

#### Validación reproducible

La compilación completa puede comprobarse desde Git Bash mediante:

```bash
./scripts/validar_java.sh
```

El script utiliza para cada archivo:

```bash
javac --release 17 -encoding UTF-8
```

El uso de `--release 17` es deliberado. Aunque la máquina de desarrollo utilice un JDK posterior, esta opción evita introducir accidentalmente características del lenguaje o API que no estén disponibles en Java 17.

Cada archivo se compila en un directorio temporal. Antes de compilar el siguiente archivo se elimina el contenido generado por la compilación anterior.

Esto permite detectar dependencias accidentales entre ejemplos que deberían ser autocontenidos.

El script termina con un código distinto de cero si al menos un archivo no compila.

#### Compatibilidad de Java

La versión mínima objetivo del repositorio es Java 17.

La validación actual se realizó con:

```text
javac 23.0.2
java 23.0.2
```

pero la compilación utiliza:

```text
--release 17
```

Por tanto, disponer de JDK 23 para el desarrollo no implica que los ejemplos dependan de Java 23.

#### Alcance de la ejecución pública

Los archivos públicos contienen esqueletos didácticos con operaciones marcadas mediante `TODO(alumno)`.

Una operación pendiente puede contener deliberadamente:

```java
throw new UnsupportedOperationException(
        "TODO: implementar ..."
);
```

Los métodos `main()` correspondientes pueden capturar esta excepción para mostrar de forma controlada que una operación todavía debe ser implementada.

Por esta razón, que un archivo compile correctamente no significa que todos sus `TODO(alumno)` estén resueltos.

La validación de compilación comprueba principalmente:

- sintaxis Java,
- consistencia de tipos,
- disponibilidad de las API utilizadas en Java 17,
- independencia de compilación,
- nombres correctos de las clases públicas,
- ausencia de dependencias externas.

Esta comprobación no reemplaza la validación funcional de las soluciones privadas.

#### Diagnósticos del IDE

Los esqueletos públicos contienen `TODO(alumno)` deliberados.

Como consecuencia, el Java Language Server de VS Code puede informar temporalmente diagnósticos como:

```text
The method ... is never used
The import ... is never used
The value of the field ... is not used
```

Estos avisos pueden ser esperados cuando el elemento señalado forma parte de una operación que el estudiante todavía debe completar.

Por ejemplo, métodos auxiliares como:

```text
resize()
rotateLeft()
rotateRight()
bubbleUp()
trickleDown()
split()
```

pueden aparecer inicialmente como no utilizados porque la operación principal que debe invocarlos contiene todavía un `TODO(alumno)`.

De manera similar, un `import` puede aparecer como no utilizado si está preparado para la implementación solicitada al estudiante.

Estos diagnósticos no deben eliminarse automáticamente completando las soluciones o retirando elementos que forman parte del ejercicio.

En el repositorio no deben existir errores reales de:

- sintaxis,
- tipos,
- nombres de clases públicas,
- paquetes,
- referencias inexistentes,
- compilación con Java 17.

#### Validación funcional del bloque de grafos

Las soluciones completas del bloque obligatorio de grafos se probaron temporalmente sin incluirlas en los archivos públicos.

| Prueba | Resultado esperado |
|---|---|
| Representación por lista y matriz | Vecinos `[1, 3]`, grado 2 |
| BFS | Distancias `[0, 1, 1, 2, 2, 3, -1]` |
| DFS | Orden `[0, 1, 3, 2, 4, 5]` |
| Componentes conexas | 4 componentes |

Las cuatro soluciones produjeron los resultados esperados.

#### Validación de archivos generados

Después de ejecutar la validación se comprobó:

```bash
find . -type f -name "*.class"
```

La búsqueda no produjo resultados.

Por tanto, la compilación de validación no deja artefactos `.class` dentro del árbol de trabajo.

#### Validación del estilo

- Los comentarios están escritos en español.
- Las cadenas de texto están escritas en español.
- Las firmas de funciones y métodos están escritas en inglés.
- Los identificadores técnicos propios de Java se conservan en inglés.
- La documentación utiliza títulos `###` y subtítulos `####`.
- La prosa no utiliza rayas largas.
- La prosa no utiliza emoticones.
- La prosa no utiliza separadores decorativos.
- La prosa evita los puntos y coma.
- Se revisan las tildes y los signos de puntuación.

#### Estado actual

La validación completa más reciente produjo:

```text
Resumen
Archivos Java: 46
Correctos: 46
Con errores: 0

Validación correcta.
```

No se encontraron archivos `.class` en el repositorio después de la validación.

El estado esperado antes de publicar cambios es:

```text
Errores de javac                  0
Errores reales del IDE           0
Archivos .class en el repositorio 0
TODO(alumno)                      permitidos
Warnings derivados de TODO        permitidos
```
