#!/usr/bin/env bash

set -euo pipefail

grep -Fq 'pull request especial titulado `Feedback`' README.md
grep -Fq 'Ese pull request no debe cerrarse.' README.md
