#!/usr/bin/env bash

set -euo pipefail

test -z "$(git ls-files -- .course)"
test -z "$(git ls-files -- opencode.json)"
test -z "$(git ls-files -- .opencode)"
test -z "$(git ls-files -- .litellm-api-key.example)"
