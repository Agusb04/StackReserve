#!/usr/bin/env bash
# Delega en bootstrap_quality_tools.py (venv local + JAR; agnóstico del SO).

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec python3 "${SCRIPT_DIR}/bootstrap_quality_tools.py" "$@"
