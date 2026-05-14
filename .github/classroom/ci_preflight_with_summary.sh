#!/usr/bin/env bash
# Ejecuta el preflight de la rama actual y, si falla, escribe pistas concretas en el resumen del job (GitHub Actions).

set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${ROOT}"

write_failure_summary() {
  local exit_code=$1
  local branch="${GITHUB_REF_NAME:-$(git branch --show-current 2>/dev/null || echo desconocida)}"
  if [[ -z "${GITHUB_STEP_SUMMARY:-}" ]]; then
    return 0
  fi
  {
    echo "## Preflight de entrega: falló"
    echo ""
    echo "**Rama:** \`${branch}\` · **código de salida:** \`${exit_code}\`"
    echo ""
    echo "### Dónde está el error concreto"
    echo "En el log de este paso, **subí** hasta el bloque anterior a este resumen: ahí aparece el comando que falló (Gradle, Checkstyle, PMD, SpotBugs, \`google-java-format\`, Semgrep, scripts de classroom, etc.)."
    echo ""
    echo "### Qué reproducir en tu máquina (misma rama que en GitHub)"
    case "${branch}" in
      main)
        echo "- Los chequeos de \`main\` solo validan archivos de classroom y contratos; revisá los mensajes en stderr arriba."
        ;;
      ejercicio-01)
        echo "- Corré: \`bash scripts/preflight-ejercicio-01.sh\`"
        ;;
      ejercicio-02)
        echo "- Corré: \`bash scripts/preflight-ejercicio-02.sh\`"
        ;;
      ejercicio-03|ejercicio-04)
        echo "1. \`python3 scripts/bootstrap_quality_tools.py\` (JAR de \`google-java-format\` + venv con Semgrep bajo \`tools/\`)"
        echo "2. \`./gradlew check\` (tests, Checkstyle, PMD, SpotBugs)"
        echo "3. \`bash scripts/preflight-${branch}.sh\` (incluye salidas del ejercicio, formato y Semgrep para esa entrega)"
        echo ""
        echo "- Si falló **Solo Gradle**: abrí \`build/reports/\` (checkstyle, pmd, spotbugs) o el stacktrace de tests en la consola local."
        echo "- Si falló **google-java-format**: formateá con el mismo JAR o con el plugin/IDE alineado a la versión del repo."
        echo "- Si falló **Semgrep**: el mensaje indica regla y archivo; ajustá el código según \`config/semgrep/ejercicio-0X.yml\`."
        ;;
      *)
        echo "- Corré el preflight indicado para la rama \`${branch}\` (ver \`.github/classroom/run_preflight.sh\`)."
        ;;
    esac
    echo ""
    echo "### Ayuda docente"
    echo "Si el error no se entiende con el log, compartí el enlace al **job fallido** y la rama con los docentes."
  } >>"${GITHUB_STEP_SUMMARY}"
}

trap 'code=$?; write_failure_summary "${code}"; exit "${code}"' ERR

bash "${ROOT}/.github/classroom/run_preflight.sh"
