#!/usr/bin/env bash
# google-java-format + Semgrep tras ./gradlew check (ejercicio-03 y 04).
# Requiere: cwd = raíz del repo, variables QUALITY_GJF_JAR y QUALITY_SEMGREP (tools/quality-tools.env).
# Uso: bash scripts/_quality_checks_after_gradle.sh <ruta-config-semgrep>

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT}"

semgrep_config="${1:?falta ruta al YAML de semgrep (ej. config/semgrep/ejercicio-04.yml)}"

gjf_jar="${GOOGLE_JAVA_FORMAT_JAR:-${QUALITY_GJF_JAR:-}}"
if [[ -z "${gjf_jar}" ]] && [[ -f "tools/google-java-format.jar" ]]; then
  gjf_jar="tools/google-java-format.jar"
fi
if [[ -z "${gjf_jar}" ]] || [[ ! -f "${gjf_jar}" ]]; then
  echo "ERROR: no se encontró el JAR de google-java-format (ejecutá python3 scripts/bootstrap_quality_tools.py)." >&2
  exit 1
fi

gjf_list="$(mktemp)"
find src/main/java src/test/java -name '*.java' 2>/dev/null >"${gjf_list}" || true
gjf_args=()
while IFS= read -r line; do
  [[ -z "${line}" ]] && continue
  gjf_args+=("${line}")
done <"${gjf_list}"
rm -f "${gjf_list}"

if ((${#gjf_args[@]})); then
  if ! java -jar "${gjf_jar}" --dry-run --set-exit-if-changed "${gjf_args[@]}"; then
    echo "ERROR: google-java-format detectó archivos fuera de formato." >&2
    exit 1
  fi
fi

if [[ -z "${QUALITY_SEMGREP:-}" ]] || [[ ! -e "${QUALITY_SEMGREP}" ]]; then
  echo "ERROR: QUALITY_SEMGREP no definido o inválido (ejecutá python3 scripts/bootstrap_quality_tools.py)." >&2
  exit 1
fi

if [[ ! -f "${semgrep_config}" ]]; then
  echo "ERROR: no existe la config de Semgrep: ${semgrep_config}" >&2
  exit 1
fi

if ! "${QUALITY_SEMGREP}" scan --config "${semgrep_config}" --error --metrics=off src/main/java src/test/java; then
  echo "ERROR: Semgrep reportó hallazgos (config: ${semgrep_config}). Arriba deberían listarse archivo y regla." >&2
  exit 1
fi
