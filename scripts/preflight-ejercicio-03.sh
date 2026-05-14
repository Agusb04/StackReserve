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

bash .github/classroom/check_required_files.sh
bash .github/classroom/check_forbidden_files.sh
bash .github/classroom/check_agents_contract.sh
bash .github/classroom/check_feedback_guidance.sh
python3 .github/classroom/check_exercise_outputs.py ejercicio-03
./gradlew check

bash "${ROOT}/scripts/_quality_checks_after_gradle.sh" "config/semgrep/ejercicio-03.yml"

printf 'Preflight de ejercicio-03: OK\n'
