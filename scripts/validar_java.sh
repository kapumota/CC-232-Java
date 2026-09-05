#!/usr/bin/env bash

set -u

cd "$(git rev-parse --show-toplevel)" || exit 1

if ! command -v javac >/dev/null 2>&1; then
    echo "ERROR: javac no está disponible en PATH."
    exit 1
fi

echo "Validación de CC-232-Java"
javac -version
echo "Compatibilidad objetivo: Java 17"
echo

TEMP_DIR="$(mktemp -d)"

if [[ ! -d "$TEMP_DIR" ]]; then
    echo "ERROR: no se pudo crear el directorio temporal."
    exit 1
fi

trap 'rm -rf "$TEMP_DIR"' EXIT

total=0
correctos=0
errores=0

while IFS= read -r -d '' file; do
    total=$((total + 1))

    rm -rf "$TEMP_DIR"/*

    printf "[%02d] %s ... " "$total" "$file"

    if javac \
        --release 17 \
        -encoding UTF-8 \
        -d "$TEMP_DIR" \
        "$file" >/dev/null 2>&1; then

        echo "OK"
        correctos=$((correctos + 1))
    else
        echo "ERROR"
        errores=$((errores + 1))

        echo
        echo "Diagnóstico de javac:"

        javac \
            --release 17 \
            -encoding UTF-8 \
            -d "$TEMP_DIR" \
            "$file"

        echo
    fi

done < <(
    find . \
        -type f \
        -name "*.java" \
        -not -path "./.git/*" \
        -print0 |
    sort -z
)

echo
echo "Resumen"
echo "Archivos Java: $total"
echo "Correctos: $correctos"
echo "Con errores: $errores"

if [[ $errores -ne 0 ]]; then
    exit 1
fi

echo
echo "Validación correcta."