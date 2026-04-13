#!/usr/bin/env python3

from __future__ import annotations

import re
import sys
from pathlib import Path


def require_file(path: str) -> Path:
    target = Path(path)
    if not target.is_file():
        raise SystemExit(f"missing file: {path}")
    return target


def require_markdown_sections(path: str, sections: list[str], *, min_length: int = 120) -> None:
    text = require_file(path).read_text(encoding="utf-8")
    if len(text.strip()) < min_length:
        raise SystemExit(f"file too short: {path}")
    for section in sections:
        if section not in text:
            raise SystemExit(f"missing section {section!r} in {path}")


def require_tokens(path: str, tokens: list[str]) -> None:
    text = require_file(path).read_text(encoding="utf-8")
    for token in tokens:
        if token not in text:
            raise SystemExit(f"missing token {token!r} in {path}")


def require_without_placeholder_markers(path: str) -> None:
    text = require_file(path).read_text(encoding="utf-8")
    patterns = [
        r"(?m)^\s*\.\.\.\s*$",
        r"(?m)^\s*[-*]\s+\.\.\.\s*$",
        r"(?m)^\s*\d+\.\s+\.\.\.\s*$",
        r"(?m)^\|(?:[^|\n]*\.\.\.[^|\n]*\|){2,}\s*$",
    ]
    if any(re.search(pattern, text) for pattern in patterns):
        raise SystemExit(f"placeholder content detected in {path}")


def require_companion_files(markdown_path: str, *, editable_suffixes: list[str], image_suffixes: list[str]) -> None:
    markdown = require_file(markdown_path)
    stem = markdown.with_suffix("")
    editable = [stem.with_suffix(suffix) for suffix in editable_suffixes]
    image = [stem.with_suffix(suffix) for suffix in image_suffixes]

    editable_path = next((path for path in editable if path.is_file()), None)
    if editable_path is None:
        raise SystemExit(f"missing editable companion for {markdown_path}")

    image_path = next((path for path in image if path.is_file()), None)
    if image_path is None:
        raise SystemExit(f"missing image companion for {markdown_path}")

    text = markdown.read_text(encoding="utf-8")
    if image_path.name not in text:
        raise SystemExit(f"{markdown_path} must reference {image_path.name}")


def require_live_feature() -> None:
    features_dir = Path("src/test/resources/features")
    features = [
        path
        for path in features_dir.glob("*.feature")
        if path.name != "feature-template.feature"
    ]
    if not features:
        raise SystemExit("missing live feature file in src/test/resources/features")


def require_freeze_export() -> None:
    freeze = require_file("docs/ux/freeze.md").read_text(encoding="utf-8")
    exports = [path for path in Path("docs/ux/exports").glob("*") if path.is_file() and path.name != ".gitkeep"]
    if not exports:
        raise SystemExit("missing exported UX artifact in docs/ux/exports")
    if not any(path.name in freeze for path in exports):
        raise SystemExit("freeze.md must reference at least one exported UX artifact")


def require_java_under(path: str) -> None:
    if not list(Path(path).rglob("*.java")):
        raise SystemExit(f"missing Java source under {path}")


def require_rest_layer_structure() -> None:
    for relative in [
        "src/main/java/ar/edu/fiuba/is1/tpi/controller",
        "src/main/java/ar/edu/fiuba/is1/tpi/service",
        "src/main/java/ar/edu/fiuba/is1/tpi/domain",
        "src/main/java/ar/edu/fiuba/is1/tpi/repository",
        "src/main/java/ar/edu/fiuba/is1/tpi/config",
    ]:
        require_java_under(relative)


def check_ejercicio_01() -> None:
    require_markdown_sections(
        "docs/descubrimiento/entrevista.md",
        [
            "## Contexto",
            "## Hipótesis de necesidad",
            "## Preguntas abiertas",
            "## Preguntas acotadas",
            "## Preguntas cerradas",
            "## Plan de entrevista",
            "## Entrevista",
            "## Dudas pendientes",
        ],
        min_length=320,
    )
    require_without_placeholder_markers("docs/descubrimiento/entrevista.md")
    require_markdown_sections(
        "docs/descubrimiento/minuta.md",
        [
            "## Fecha y hora",
            "## Lugar o medio",
            "## Participantes",
            "## Objetivo",
            "## Hallazgos",
            "## Decisiones",
            "## Confirmaciones",
            "## Compromisos asumidos",
            "## Pendientes",
        ],
        min_length=220,
    )
    require_without_placeholder_markers("docs/descubrimiento/minuta.md")
    require_markdown_sections(
        "docs/descubrimiento/interesados.md",
        [
            "## Sistema analizado",
            "## Criterio de capas",
            "## Resumen del modelo",
            "## Registro de interesados",
            "## Cobertura faltante",
            "## Próximas acciones",
        ],
        min_length=320,
    )
    require_without_placeholder_markers("docs/descubrimiento/interesados.md")
    require_tokens(
        "docs/descubrimiento/interesados.md",
        ["Our System", "Containing System", "Wider Environment", "ID", "Capa", "Estado", "explícito", "inferido", "dudoso"],
    )
    require_companion_files(
        "docs/descubrimiento/interesados.md",
        editable_suffixes=[".drawio", ".puml"],
        image_suffixes=[".svg", ".png"],
    )
    require_markdown_sections(
        "docs/descubrimiento/historia-dominio.md",
        [
            "## Escenario elegido",
            "## Recorte aplicado",
            "## Actores",
            "## Objetos de trabajo",
            "## Historia paso a paso",
            "## Dudas y supuestos",
        ],
        min_length=280,
    )
    require_without_placeholder_markers("docs/descubrimiento/historia-dominio.md")
    require_companion_files(
        "docs/descubrimiento/historia-dominio.md",
        editable_suffixes=[".egn", ".puml"],
        image_suffixes=[".svg", ".png"],
    )
    require_markdown_sections(
        "docs/requisitos/requisitos.md",
        [
            "## Objetivo y alcance",
            "### Sistema a construir",
            "### Alcance inicial",
            "### Fuera de alcance",
            "### Supuestos relevantes",
            "## Requisitos funcionales",
            "## Requisitos no funcionales",
            "## Reglas de negocio",
            "## Trazabilidad a entrevista",
            "## Dudas abiertas",
        ],
        min_length=420,
    )
    require_without_placeholder_markers("docs/requisitos/requisitos.md")
    require_tokens(
        "docs/requisitos/requisitos.md",
        ["RF-", "RNF-", "RN-", "D-", "Evidencia o fuente", "Criterio de verificación", "Impacto en el sistema"],
    )
    require_markdown_sections(
        "entregas/ejercicio-01.md",
        [
            "## Objetivo de la entrega",
            "## Artefactos trabajados",
            "## Cambios principales",
            "## Feedback incorporado",
            "## Pendientes",
        ],
    )
    require_without_placeholder_markers("entregas/ejercicio-01.md")


def check_ejercicio_02() -> None:
    require_markdown_sections("docs/requisitos/historias-usuario.md", ["## Backlog", "## Historias priorizadas", "HU-"], min_length=200)
    require_markdown_sections("docs/requisitos/modelo-dominio.md", ["## Alcance del modelo", "## Conceptos principales", "```mermaid"], min_length=200)
    require_markdown_sections("docs/ux/prototipo.md", ["## Objetivo del prototipo", "## Link read-only", "## Recorrido principal"], min_length=160)
    require_markdown_sections("docs/ux/freeze.md", ["## Link read-only", "## Export congelado", "## Consistencia verificada"], min_length=140)
    require_markdown_sections(
        "entregas/ejercicio-02.md",
        [
            "## Objetivo de la entrega",
            "## Artefactos trabajados",
            "## Cambios principales",
            "## Feedback incorporado",
            "## Pendientes",
        ],
    )
    require_live_feature()
    require_freeze_export()


def check_ejercicio_03() -> None:
    for relative in [
        "docs/arquitectura/c4-contexto.md",
        "docs/arquitectura/c4-contenedores.md",
        "docs/arquitectura/c4-componentes.md",
    ]:
        require_markdown_sections(relative, ["## Propósito", "```mermaid"], min_length=160)
    require_markdown_sections("docs/arquitectura/adr-001.md", ["# ADR 001", "## Contexto", "## Decisión", "## Consecuencias"], min_length=180)
    require_markdown_sections(
        "entregas/ejercicio-03.md",
        [
            "## Objetivo de la entrega",
            "## Artefactos trabajados",
            "## Cambios principales",
            "## Feedback incorporado",
            "## Pendientes",
        ],
    )
    require_rest_layer_structure()
    require_java_under("src/test/java")
    require_live_feature()


def check_ejercicio_04() -> None:
    require_markdown_sections("docs/ux/refinamiento-ux.md", ["## Cambios UX aplicados", "## Evidencia del refinamiento", "## Pendientes UX"], min_length=180)
    require_markdown_sections("db/modelo-er.md", ["## Alcance del modelo", "```mermaid"], min_length=160)
    schema_text = require_file("db/schema.sql").read_text(encoding="utf-8").lower()
    if "create table" not in schema_text:
        raise SystemExit("schema.sql must contain at least one CREATE TABLE")
    require_markdown_sections(
        "entregas/ejercicio-04.md",
        [
            "## Objetivo de la entrega",
            "## Artefactos trabajados",
            "## Cambios principales",
            "## Feedback incorporado",
            "## Pendientes",
        ],
    )


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("usage: check_exercise_outputs.py <ejercicio-01|ejercicio-02|ejercicio-03|ejercicio-04>")

    checks = {
        "ejercicio-01": check_ejercicio_01,
        "ejercicio-02": check_ejercicio_02,
        "ejercicio-03": check_ejercicio_03,
        "ejercicio-04": check_ejercicio_04,
    }
    try:
        checks[sys.argv[1]]()
    except KeyError as exc:
        raise SystemExit(f"unsupported exercise slug: {sys.argv[1]}") from exc


if __name__ == "__main__":
    main()
