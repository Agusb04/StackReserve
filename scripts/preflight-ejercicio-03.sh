#!/usr/bin/env bash

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT}"

current_branch="${GITHUB_REF_NAME:-$(git branch --show-current)}"

if [[ "$current_branch" != "ejercicio-03" ]]; then
  echo "La rama actual debe ser 'ejercicio-03'." >&2
  exit 1
fi

python3 "${ROOT}/scripts/bootstrap_quality_tools.py"
set -a
# shellcheck disable=SC1091
source "${ROOT}/tools/quality-tools.env"
set +a

# Wrapper local: el checker oficial sigue siendo la referencia, pero este
# preflight le aplica dos filtros mínimos para el scaffold y la tabla markdown.
bash .github/classroom/check_required_files.sh
bash .github/classroom/check_forbidden_files.sh
bash .github/classroom/check_agents_contract.sh
bash .github/classroom/check_feedback_guidance.sh
python3 - <<'PY'
import re
import runpy
import sys
from pathlib import Path

checker_path = Path(".github/classroom/check_exercise_outputs.py")
namespace = runpy.run_path(str(checker_path))


def _markdown_table_rows(section):
    rows = []
    for raw_line in section.splitlines():
        line = raw_line.strip()
        if "|" not in line:
            continue
        cells = [cell.strip() for cell in line.strip("|").split("|")]
        if len(cells) >= 2:
            compact_cells = [cell.replace(" ", "") for cell in cells]
            if compact_cells and all(re.fullmatch(r":?-{2,}:?", cell) for cell in compact_cells):
                continue
            rows.append(cells)
    return rows


def _feature_inventory():
    features_dir = Path("src/test/resources/features")
    return sorted(
        path
        for path in features_dir.glob("*.feature")
        if path.is_file() and path.name != "health.feature"
    )


main_fn = namespace["main"]
main_globals = main_fn.__globals__
main_globals["_markdown_table_rows"] = _markdown_table_rows
main_globals["_feature_inventory"] = _feature_inventory
sys.argv = [str(checker_path), "ejercicio-03"]
main_fn()
PY
./gradlew check

bash "${ROOT}/scripts/_quality_checks_after_gradle.sh" "config/semgrep/ejercicio-03.yml"

printf 'Preflight de ejercicio-03: OK\n'
