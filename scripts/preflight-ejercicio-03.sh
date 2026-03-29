#!/usr/bin/env bash

set -euo pipefail

current_branch="${GITHUB_REF_NAME:-$(git branch --show-current)}"

if [[ "$current_branch" != "ejercicio-03" ]]; then
  echo "La rama actual debe ser 'ejercicio-03'." >&2
  exit 1
fi

bash .github/classroom/check_required_files.sh
bash .github/classroom/check_forbidden_files.sh
bash .github/classroom/check_agents_contract.sh
bash .github/classroom/check_feedback_guidance.sh
python3 .github/classroom/check_exercise_outputs.py ejercicio-03
./gradlew check

printf 'Preflight de ejercicio-03: OK\n'
