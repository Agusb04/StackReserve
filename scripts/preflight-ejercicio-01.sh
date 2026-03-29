#!/usr/bin/env bash

set -euo pipefail

current_branch="${GITHUB_REF_NAME:-$(git branch --show-current)}"

if [[ "$current_branch" != "ejercicio-01" ]]; then
  echo "La rama actual debe ser 'ejercicio-01'." >&2
  exit 1
fi

bash .github/classroom/check_required_files.sh
bash .github/classroom/check_forbidden_files.sh
bash .github/classroom/check_agents_contract.sh
bash .github/classroom/check_feedback_guidance.sh
python3 .github/classroom/check_exercise_outputs.py ejercicio-01

printf 'Preflight de ejercicio-01: OK\n'
