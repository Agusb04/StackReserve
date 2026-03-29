#!/usr/bin/env bash

set -euo pipefail

grep -Fq 'No ejecutar `/init` ni otro inicializador que cree, reemplace o regenere `AGENTS.md`.' AGENTS.md
grep -Fq 'No reemplazar ni regenerar este archivo (`AGENTS.md`).' AGENTS.md
grep -Fq 'No modificar archivos en `ejercicios/`, `.github/` ni archivos `*-template`.' AGENTS.md
grep -Fq 'Guardar manifiestos de entrega en `entregas/`.' AGENTS.md
grep -Fq 'Los archivos `*.feature` deben ser ejecutables con Cucumber JVM, no sólo documentales.' AGENTS.md
