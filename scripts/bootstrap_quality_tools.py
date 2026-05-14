#!/usr/bin/env python3
"""
Descarga google-java-format (JAR) e instala Semgrep en un venv local del repo.

Diseñado para ser agnóstico del SO: solo usa la stdlib + venv (sin pip --user
ni flags que alteren el Python del sistema). Escribe tools/quality-tools.env
para que scripts/preflight-ejercicio-03.sh cargue QUALITY_SEMGREP y QUALITY_GJF_JAR.
"""

from __future__ import annotations

import os
import subprocess
import sys
import urllib.request
from pathlib import Path

DEFAULT_GJF_VERSION = os.environ.get("GOOGLE_JAVA_FORMAT_VERSION", "1.33.0")


def repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def _venv_pip(venv: Path) -> Path:
    if sys.platform == "win32":
        return venv / "Scripts" / "pip.exe"
    return venv / "bin" / "pip"


def _venv_semgrep(venv: Path) -> Path:
    if sys.platform == "win32":
        return venv / "Scripts" / "semgrep.exe"
    return venv / "bin" / "semgrep"


def _sh_single_quote(value: str) -> str:
    return "'" + value.replace("'", "'\"'\"'") + "'"


def main() -> int:
    root = repo_root()
    tools = root / "tools"
    tools.mkdir(parents=True, exist_ok=True)

    venv = tools / "quality-venv"
    jar = tools / "google-java-format.jar"
    gjf_ver = DEFAULT_GJF_VERSION
    url = (
        f"https://github.com/google/google-java-format/releases/download/v{gjf_ver}/"
        f"google-java-format-{gjf_ver}-all-deps.jar"
    )

    if not jar.is_file():
        print(f"Descargando google-java-format {gjf_ver} -> {jar.name} ...", file=sys.stderr)
        req = urllib.request.Request(
            url,
            headers={"User-Agent": "IS1-tpi-quality-bootstrap/1.0"},
        )
        with urllib.request.urlopen(req, timeout=120) as resp:
            jar.write_bytes(resp.read())

    if not venv.is_dir():
        print(f"Creando entorno virtual en {venv.relative_to(root)} ...", file=sys.stderr)
        subprocess.run([sys.executable, "-m", "venv", str(venv)], check=True)

    pip = _venv_pip(venv)
    if not pip.is_file():
        print(f"ERROR: no se encontró pip en el venv: {pip}", file=sys.stderr)
        return 1

    print("Instalando semgrep en el venv del repo ...", file=sys.stderr)
    subprocess.run([str(pip), "install", "-q", "semgrep"], check=True)

    semgrep = _venv_semgrep(venv)
    if not semgrep.is_file():
        print(f"ERROR: no se encontró el ejecutable de semgrep: {semgrep}", file=sys.stderr)
        return 1

    subprocess.run([str(semgrep), "--version"], check=True, capture_output=True)

    env_file = tools / "quality-tools.env"
    env_file.write_text(
        "# Generado por scripts/bootstrap_quality_tools.py (no editar)\n"
        f"export QUALITY_GJF_JAR={_sh_single_quote(str(jar.resolve()))}\n"
        f"export QUALITY_SEMGREP={_sh_single_quote(str(semgrep.resolve()))}\n",
        encoding="utf-8",
    )

    print(
        f"OK: {jar.relative_to(root)}, semgrep en {semgrep.relative_to(root)}",
        file=sys.stderr,
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
