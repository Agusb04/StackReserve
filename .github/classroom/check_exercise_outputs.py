#!/usr/bin/env python3

from __future__ import annotations

import re
import sys
from pathlib import Path


MERMAID_BLOCK_RE = re.compile(r"(?ms)```mermaid\s*(.*?)\s*```")
_C4_RELATION_RE = re.compile(r"\b(?:BiRel|Rel(?:_[A-Za-z]+)?|RelIndex)\s*\(")
_C4_PERSON_RE = re.compile(r"\bPerson(?:_Ext)?\s*\(")
_C4_SYSTEM_RE = re.compile(r"\bSystem(?:_Ext|Db|Queue)?\s*\(")
_C4_CONTAINER_RE = re.compile(r"\bContainer(?:_Ext|Db|Queue)?\s*\(")
_C4_COMPONENT_RE = re.compile(r"\bComponent(?:_Ext|Db|Queue)?\s*\(")


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
        raise SystemExit(f"missing editable source file for {markdown_path}")

    image_path = next((path for path in image if path.is_file()), None)
    if image_path is None:
        raise SystemExit(f"missing visual export for {markdown_path}")

    text = markdown.read_text(encoding="utf-8")
    if image_path.name not in text:
        raise SystemExit(f"{markdown_path} must reference {image_path.name}")


def require_hu_features(*, min_count: int) -> list[Path]:
    features_dir = Path("src/test/resources/features")
    features = [
        path
        for path in features_dir.glob("HU-*.feature")
        if path.is_file()
    ]
    if len(features) < min_count:
        raise SystemExit(
            f"missing HU feature files in src/test/resources/features (expected at least {min_count}, found {len(features)})"
        )
    return features


def require_storybook_sources(*, min_count: int = 1) -> None:
    storybook_root = Path("docs/ux/storybook")
    index_path = require_file("docs/ux/storybook/index.html")
    template_path = require_file("docs/ux/storybook/src/story-template.html")
    story_files = [
        path
        for path in (storybook_root / "src").glob("*.html")
        if path.is_file() and path.name != "story-template.html"
    ]
    if len(story_files) < min_count:
        raise SystemExit(
            f"missing live Storybook stories in {storybook_root / 'src'} (expected at least {min_count}, found {len(story_files)})"
        )
    if "story-template.html" not in index_path.read_text(encoding="utf-8"):
        raise SystemExit("docs/ux/storybook/index.html must reference src/story-template.html")
    if "HU-" not in template_path.read_text(encoding="utf-8"):
        raise SystemExit("docs/ux/storybook/src/story-template.html must be aligned with HU traceability")


def require_freeze_export() -> None:
    freeze = require_file("docs/ux/freeze.md").read_text(encoding="utf-8")
    exports = [path for path in Path("docs/ux/exports").glob("*") if path.is_file() and path.name != ".gitkeep"]
    if not exports:
        raise SystemExit("missing exported UX artifact in docs/ux/exports")
    if not any(path.name in freeze for path in exports):
        raise SystemExit("freeze.md must reference at least one exported UX artifact")


def require_freeze_screenshots(*, min_count: int = 3) -> None:
    freeze = require_file("docs/ux/freeze.md").read_text(encoding="utf-8")
    screenshot_refs: list[str] = []
    for match in re.finditer(r"!\[[^\]]*\]\((docs/ux/exports/[^)]+\.(?:png|jpe?g|webp|svg))\)", freeze):
        ref = match.group(1)
        if ref not in screenshot_refs:
            screenshot_refs.append(ref)
    if len(screenshot_refs) < min_count:
        raise SystemExit(f"freeze.md must reference at least {min_count} screenshot images under docs/ux/exports")
    for ref in screenshot_refs:
        if not Path(ref).is_file():
            raise SystemExit(f"freeze.md references a missing screenshot: {ref}")


def require_java_under(path: str) -> None:
    if not list(Path(path).rglob("*.java")):
        raise SystemExit(f"missing Java source under {path}")


def _normalize_text(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip().casefold()


def _strip_markdown_code(value: str) -> str:
    cleaned = value.strip()
    cleaned = re.sub(r"^[`*_~\s]+", "", cleaned)
    cleaned = re.sub(r"[`*_~\s]+$", "", cleaned)
    return cleaned.strip()


def _markdown_section(text: str, heading: str) -> str | None:
    pattern = re.compile(rf"(?ms)^##\s+{re.escape(heading)}\s*\n(.*?)(?=^##\s+|\Z)")
    match = pattern.search(text)
    if match is None:
        return None
    return match.group(1)


def _markdown_table_rows(section: str) -> list[list[str]]:
    rows: list[list[str]] = []
    for raw_line in section.splitlines():
        line = raw_line.strip()
        if "|" not in line:
            continue
        cells = [cell.strip() for cell in line.strip("|").split("|")]
        if len(cells) >= 2:
            rows.append(cells)
    return rows


def _find_table_column_index(header: list[str], names: tuple[str, ...]) -> int | None:
    normalized = [_normalize_text(cell) for cell in header]
    for index, cell in enumerate(normalized):
        if any(name in cell for name in names):
            return index
    return None


def _validate_historias_usuario_03() -> list[str]:
    text = require_file("docs/requisitos/historias-usuario.md").read_text(encoding="utf-8")
    require_markdown_sections(
        "docs/requisitos/historias-usuario.md",
        ["## Backlog", "## Historias priorizadas", "## Criterios de calidad"],
        min_length=260,
    )

    backlog_section = _markdown_section(text, "Backlog")
    prioritized_section = _markdown_section(text, "Historias priorizadas")
    quality_section = _markdown_section(text, "Criterios de calidad") or ""
    if backlog_section is None:
        raise SystemExit("missing section `## Backlog` in `docs/requisitos/historias-usuario.md`")
    if prioritized_section is None:
        raise SystemExit("missing section `## Historias priorizadas` in `docs/requisitos/historias-usuario.md`")

    backlog_rows = _markdown_table_rows(backlog_section)
    prioritized_rows = _markdown_table_rows(prioritized_section)
    if len(backlog_rows) < 7:
        raise SystemExit("docs/requisitos/historias-usuario.md must contain at least 6 backlog stories in a table")
    if len(prioritized_rows) < 4:
        raise SystemExit("docs/requisitos/historias-usuario.md must prioritize at least 3 stories in a table")

    backlog_header = backlog_rows[0]
    prioritized_header = prioritized_rows[0]
    backlog_id_index = _find_table_column_index(backlog_header, ("id",))
    backlog_story_index = _find_table_column_index(backlog_header, ("historia",))
    prioritized_id_index = _find_table_column_index(prioritized_header, ("id",))
    feature_index = _find_table_column_index(prioritized_header, ("feature esperado", "feature"))
    if backlog_id_index is None or backlog_story_index is None:
        raise SystemExit("docs/requisitos/historias-usuario.md backlog table must expose `ID` and `Historia` columns")
    if prioritized_id_index is None or feature_index is None:
        raise SystemExit(
            "docs/requisitos/historias-usuario.md prioritized table must expose `ID` and `Feature esperado` columns"
        )

    backlog_ids: list[str] = []
    for row in backlog_rows[1:]:
        if backlog_id_index >= len(row):
            continue
        story_id = _strip_markdown_code(row[backlog_id_index])
        if re.fullmatch(r"HU-\d+", story_id):
            backlog_ids.append(story_id)
    if len(backlog_ids) < 6:
        raise SystemExit("docs/requisitos/historias-usuario.md must list at least 6 backlog stories")

    prioritized_ids: list[str] = []
    expected_feature_paths: list[str] = []
    for row in prioritized_rows[1:]:
        if prioritized_id_index >= len(row) or feature_index >= len(row):
            continue
        story_id = _strip_markdown_code(row[prioritized_id_index])
        expected_feature = _strip_markdown_code(row[feature_index])
        if not re.fullmatch(r"HU-\d+", story_id):
            raise SystemExit(
                f"docs/requisitos/historias-usuario.md prioritizes an invalid story ID `{story_id or row[prioritized_id_index]}`"
            )
        expected_path = f"src/test/resources/features/{story_id}.feature"
        prioritized_ids.append(story_id)
        expected_feature_paths.append(expected_path)
        if expected_feature != expected_path:
            raise SystemExit(
                f"docs/requisitos/historias-usuario.md must map `{story_id}` to `{expected_path}`, not `{expected_feature or row[feature_index]}`"
            )

    if len(prioritized_ids) < 3:
        raise SystemExit("docs/requisitos/historias-usuario.md must prioritize at least 3 stories")
    if "1:1" not in quality_section and "uno a uno" not in _normalize_text(quality_section):
        raise SystemExit(
            "docs/requisitos/historias-usuario.md must make the 1:1 story-to-feature rule explicit in `## Criterios de calidad`"
        )

    return expected_feature_paths


def _feature_inventory() -> list[Path]:
    return sorted(path for path in Path("src/test/resources/features").glob("*.feature") if path.is_file())


def _feature_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _extract_mermaid_blocks(text: str) -> list[str]:
    return [block.strip() for block in MERMAID_BLOCK_RE.findall(text)]


def _mermaid_block_header(block: str) -> str | None:
    for raw_line in block.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("%%"):
            continue
        return line.split(maxsplit=1)[0]
    return None


def _find_mermaid_block(text: str, diagram_type: str) -> str | None:
    for block in _extract_mermaid_blocks(text):
        if _mermaid_block_header(block) == diagram_type:
            return block
    return None


def _mermaid_block_has_pattern(block: str, pattern: re.Pattern[str]) -> bool:
    return pattern.search(block) is not None


def _require_c4_mermaid(
    path: str,
    *,
    diagram_type: str,
    missing_message: str,
    required_patterns: list[tuple[re.Pattern[str], str]],
) -> None:
    text = require_file(path).read_text(encoding="utf-8")
    block = _find_mermaid_block(text, diagram_type)
    if block is None:
        raise SystemExit(missing_message)
    for pattern, message in required_patterns:
        if not _mermaid_block_has_pattern(block, pattern):
            raise SystemExit(message)


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
    require_markdown_sections(
        "docs/requisitos/historias-usuario.md",
        ["## Backlog", "## Historias priorizadas", "## Criterios de calidad", "HU-"],
        min_length=260,
    )
    require_markdown_sections(
        "docs/requisitos/modelo-dominio.md",
        [
            "## Archivo fuente y export visual",
            "## Alcance del modelo",
            "## Inputs usados",
            "## Historias priorizadas de referencia",
            "## Entidades conceptuales",
            "## Relaciones persistidas",
            "## Atributos",
            "## Diagrama",
            "## Trazabilidad",
            "## Fuera de alcance y exclusiones",
            "## Dudas abiertas",
            "## Subset permitido de PlantUML",
        ],
        min_length=300,
    )
    require_tokens("docs/requisitos/modelo-dominio.md", ["PlantUML", "HU-", "Trazabilidad"])
    require_companion_files(
        "docs/requisitos/modelo-dominio.md",
        editable_suffixes=[".puml"],
        image_suffixes=[".svg", ".png"],
    )
    require_markdown_sections(
        "docs/ux/prototipo.md",
        [
            "## Objetivo del prototipo",
            "## Historias y escenarios cubiertos",
            "## Pantallas o recorridos cubiertos",
            "## Storybook",
            "## Link read-only",
            "## Export congelado",
            "## Decisiones de UX visibles",
        ],
        min_length=220,
    )
    require_tokens("docs/ux/prototipo.md", ["Storybook", "HU-"])
    require_markdown_sections(
        "docs/ux/freeze.md",
        ["## Link read-only", "## Storybook congelado", "## Export congelado", "## Consistencia verificada"],
        min_length=160,
    )
    require_tokens("docs/ux/freeze.md", ["Storybook", "HU-", "docs/ux/exports/"])
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
    require_hu_features(min_count=3)
    require_storybook_sources(min_count=1)
    require_freeze_export()


def check_ejercicio_03() -> None:
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
    require_tokens("docs/requisitos/requisitos.md", ["RF-", "RNF-", "RN-", "D-"])
    expected_feature_paths = _validate_historias_usuario_03()
    require_markdown_sections(
        "docs/requisitos/modelo-dominio.md",
        [
            "## Archivo fuente y export visual",
            "## Alcance del modelo",
            "## Inputs usados",
            "## Historias priorizadas de referencia",
            "## Entidades conceptuales",
            "## Relaciones persistidas",
            "## Atributos",
            "## Diagrama",
            "## Trazabilidad",
            "## Fuera de alcance y exclusiones",
            "## Dudas abiertas",
            "## Subset permitido de PlantUML",
        ],
        min_length=300,
    )
    require_tokens("docs/requisitos/modelo-dominio.md", ["PlantUML", "HU-", "Trazabilidad"])
    require_companion_files(
        "docs/requisitos/modelo-dominio.md",
        editable_suffixes=[".puml"],
        image_suffixes=[".svg", ".png"],
    )
    feature_inventory = _feature_inventory()
    hu_feature_inventory = [path for path in feature_inventory if re.fullmatch(r"HU-\d+\.feature", path.name)]
    if len(hu_feature_inventory) < 3:
        raise SystemExit("missing HU feature files in src/test/resources/features (expected at least 3)")

    actual_feature_names = {path.name for path in hu_feature_inventory}
    expected_feature_names = {Path(path).name for path in expected_feature_paths}
    missing_features = sorted(expected_feature_names - actual_feature_names)
    extra_features = sorted(actual_feature_names - expected_feature_names)
    if missing_features:
        raise SystemExit(
            "missing prioritized feature files: "
            + ", ".join(f"`src/test/resources/features/{name}`" for name in missing_features)
        )
    if extra_features:
        raise SystemExit(
            "unexpected HU feature files without prioritized stories: "
            + ", ".join(f"`src/test/resources/features/{name}`" for name in extra_features)
        )

    non_hu_features = [
        path
        for path in feature_inventory
        if path.name != "feature-template.feature" and not re.fullmatch(r"HU-\d+\.feature", path.name)
    ]
    if non_hu_features:
        raise SystemExit(
            "unexpected non-HU feature files: "
            + ", ".join(f"`src/test/resources/features/{path.name}`" for path in non_hu_features)
        )

    feature_texts = [_feature_text(path) for path in hu_feature_inventory]
    if not any("@disponibilidad-salas" in text for text in feature_texts):
        raise SystemExit("missing required feature tag `@disponibilidad-salas`")
    if not any("@cancelacion-reserva" in text for text in feature_texts):
        raise SystemExit("missing required feature tag `@cancelacion-reserva`")
    for path, text in zip(hu_feature_inventory, feature_texts, strict=True):
        if "# language: es" not in text:
            raise SystemExit(f"`{path.as_posix()}` must declare `# language: es`")
        if "Característica:" not in text:
            raise SystemExit(f"`{path.as_posix()}` must contain `Característica:`")
        scenario_count = len(re.findall(r"(?mi)^\s*(?:Escenario|Esquema del escenario)\s*:", text))
        if scenario_count < 3:
            raise SystemExit(f"`{path.as_posix()}` must contain at least 3 scenarios or scenario outlines")

    for relative in [
        "docs/arquitectura/c4-contexto.md",
        "docs/arquitectura/c4-contenedores.md",
        "docs/arquitectura/c4-componentes.md",
    ]:
        require_markdown_sections(relative, ["## Propósito", "```mermaid"], min_length=160)
    _require_c4_mermaid(
        "docs/arquitectura/c4-contexto.md",
        diagram_type="C4Context",
        missing_message=(
            "`docs/arquitectura/c4-contexto.md` should use Mermaid C4 syntax with `C4Context` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_PERSON_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `Person(...)` or `Person_Ext(...)`.",
            ),
            (
                _C4_SYSTEM_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `System(...)` or `System_Ext(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
    _require_c4_mermaid(
        "docs/arquitectura/c4-contenedores.md",
        diagram_type="C4Container",
        missing_message=(
            "`docs/arquitectura/c4-contenedores.md` should use Mermaid C4 syntax with `C4Container` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_CONTAINER_RE,
                "`docs/arquitectura/c4-contenedores.md` should declare at least one `Container(...)` or `ContainerDb(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-contenedores.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
    _require_c4_mermaid(
        "docs/arquitectura/c4-componentes.md",
        diagram_type="C4Component",
        missing_message=(
            "`docs/arquitectura/c4-componentes.md` should use Mermaid C4 syntax with `C4Component` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_COMPONENT_RE,
                "`docs/arquitectura/c4-componentes.md` should declare at least one `Component(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-componentes.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
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


def check_ejercicio_04() -> None:
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
    require_tokens("docs/requisitos/requisitos.md", ["RF-", "RNF-", "RN-", "D-", "Evidencia o fuente", "Criterio de verificación", "Impacto en el sistema"])
    require_markdown_sections(
        "docs/requisitos/historias-usuario.md",
        ["## Backlog", "## Historias priorizadas", "## Criterios de calidad", "HU-"],
        min_length=260,
    )
    require_without_placeholder_markers("docs/requisitos/historias-usuario.md")
    expected_feature_paths = _validate_historias_usuario_03()
    require_markdown_sections(
        "docs/requisitos/modelo-dominio.md",
        [
            "## Archivo fuente y export visual",
            "## Alcance del modelo",
            "## Inputs usados",
            "## Historias priorizadas de referencia",
            "## Entidades conceptuales",
            "## Relaciones persistidas",
            "## Atributos",
            "## Diagrama",
            "## Trazabilidad",
            "## Fuera de alcance y exclusiones",
            "## Dudas abiertas",
            "## Subset permitido de PlantUML",
        ],
        min_length=300,
    )
    require_without_placeholder_markers("docs/requisitos/modelo-dominio.md")
    require_tokens("docs/requisitos/modelo-dominio.md", ["PlantUML", "HU-", "Trazabilidad"])
    require_companion_files(
        "docs/requisitos/modelo-dominio.md",
        editable_suffixes=[".puml"],
        image_suffixes=[".svg", ".png"],
    )
    require_markdown_sections(
        "docs/ux/prototipo.md",
        [
            "## Objetivo del prototipo",
            "## Historias y escenarios cubiertos",
            "## Pantallas o recorridos cubiertos",
            "## Storybook",
            "## Link read-only",
            "## Export congelado",
            "## Decisiones de UX visibles",
        ],
        min_length=220,
    )
    require_without_placeholder_markers("docs/ux/prototipo.md")
    require_tokens("docs/ux/prototipo.md", ["Storybook", "HU-"])
    require_markdown_sections(
        "docs/ux/freeze.md",
        [
            "## Link read-only",
            "## Storybook congelado",
            "## Capturas congeladas",
            "## Export congelado",
            "## Consistencia verificada",
        ],
        min_length=160,
    )
    require_without_placeholder_markers("docs/ux/freeze.md")
    require_tokens("docs/ux/freeze.md", ["Storybook", "HU-", "docs/ux/exports/"])
    require_storybook_sources(min_count=1)
    require_freeze_export()
    require_freeze_screenshots()
    require_markdown_sections(
        "docs/arquitectura/c4-contexto.md",
        ["## Propósito", "```mermaid"],
        min_length=160,
    )
    require_markdown_sections(
        "docs/arquitectura/c4-contenedores.md",
        ["## Propósito", "```mermaid"],
        min_length=160,
    )
    require_markdown_sections(
        "docs/arquitectura/c4-componentes.md",
        ["## Propósito", "```mermaid"],
        min_length=160,
    )
    _require_c4_mermaid(
        "docs/arquitectura/c4-contexto.md",
        diagram_type="C4Context",
        missing_message=(
            "`docs/arquitectura/c4-contexto.md` should use Mermaid C4 syntax with `C4Context` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_PERSON_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `Person(...)` or `Person_Ext(...)`.",
            ),
            (
                _C4_SYSTEM_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `System(...)` or `System_Ext(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-contexto.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
    _require_c4_mermaid(
        "docs/arquitectura/c4-contenedores.md",
        diagram_type="C4Container",
        missing_message=(
            "`docs/arquitectura/c4-contenedores.md` should use Mermaid C4 syntax with `C4Container` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_CONTAINER_RE,
                "`docs/arquitectura/c4-contenedores.md` should declare at least one `Container(...)` or `ContainerDb(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-contenedores.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
    _require_c4_mermaid(
        "docs/arquitectura/c4-componentes.md",
        diagram_type="C4Component",
        missing_message=(
            "`docs/arquitectura/c4-componentes.md` should use Mermaid C4 syntax with `C4Component` "
            "instead of a generic `flowchart`."
        ),
        required_patterns=[
            (
                _C4_COMPONENT_RE,
                "`docs/arquitectura/c4-componentes.md` should declare at least one `Component(...)`.",
            ),
            (
                _C4_RELATION_RE,
                "`docs/arquitectura/c4-componentes.md` should declare at least one `Rel(...)` relationship.",
            ),
        ],
    )
    require_markdown_sections(
        "docs/arquitectura/adr-001.md",
        ["# ADR 001", "## Contexto", "## Decisión", "## Consecuencias"],
        min_length=180,
    )
    require_markdown_sections(
        "docs/arquitectura/api-rest.md",
        [
            "## Objetivo y alcance",
            "## Recursos y endpoints",
            "## Contratos de request y respuesta",
            "## Errores y estados HTTP",
            "## Flujo de prueba de API",
            "## Trazabilidad",
        ],
        min_length=220,
    )
    require_without_placeholder_markers("docs/arquitectura/api-rest.md")
    require_tokens("docs/arquitectura/api-rest.md", ["HTTP", "HU-", "API REST"])
    require_markdown_sections(
        "db/modelo-er.md",
        ["## Alcance del modelo", "```mermaid"],
        min_length=160,
    )
    require_without_placeholder_markers("db/modelo-er.md")
    schema_path = Path("db/schema.sql")
    if schema_path.is_file():
        schema_text = schema_path.read_text(encoding="utf-8").lower()
        if "create table" not in schema_text:
            raise SystemExit("db/schema.sql must contain at least one CREATE TABLE")
    require_markdown_sections(
        "entregas/ejercicio-04.md",
        [
            "## Objetivo de la entrega",
            "## Artefactos trabajados",
            "## Cambios principales",
            "## Flujo de prueba de API",
            "## Feedback incorporado",
            "## Pendientes",
        ],
        min_length=220,
    )
    require_without_placeholder_markers("entregas/ejercicio-04.md")
    feature_inventory = _feature_inventory()
    hu_feature_inventory = [path for path in feature_inventory if re.fullmatch(r"HU-\d+\.feature", path.name)]
    if len(hu_feature_inventory) < 3:
        raise SystemExit("missing HU feature files in src/test/resources/features (expected at least 3)")

    actual_feature_names = {path.name for path in hu_feature_inventory}
    expected_feature_names = {Path(path).name for path in expected_feature_paths}
    missing_features = sorted(expected_feature_names - actual_feature_names)
    extra_features = sorted(actual_feature_names - expected_feature_names)
    if missing_features:
        raise SystemExit(
            "missing prioritized feature files: "
            + ", ".join(f"`src/test/resources/features/{name}`" for name in missing_features)
        )
    if extra_features:
        raise SystemExit(
            "unexpected HU feature files without prioritized stories: "
            + ", ".join(f"`src/test/resources/features/{name}`" for name in extra_features)
        )

    non_hu_features = [
        path
        for path in feature_inventory
        if path.name != "feature-template.feature" and not re.fullmatch(r"HU-\d+\.feature", path.name)
    ]
    if non_hu_features:
        raise SystemExit(
            "unexpected non-HU feature files: "
            + ", ".join(f"`src/test/resources/features/{path.name}`" for path in non_hu_features)
        )

    feature_texts = [_feature_text(path) for path in hu_feature_inventory]
    if not any("@disponibilidad-salas" in text for text in feature_texts):
        raise SystemExit("missing required feature tag `@disponibilidad-salas`")
    if not any("@creacion-reserva" in text for text in feature_texts):
        raise SystemExit("missing required feature tag `@creacion-reserva`")
    if not any("@cancelacion-reserva" in text for text in feature_texts):
        raise SystemExit("missing required feature tag `@cancelacion-reserva`")
    for path, text in zip(hu_feature_inventory, feature_texts, strict=True):
        if "# language: es" not in text:
            raise SystemExit(f"`{path.as_posix()}` must declare `# language: es`")
        if "Característica:" not in text:
            raise SystemExit(f"`{path.as_posix()}` must contain `Característica:`")
        scenario_count = len(re.findall(r"(?mi)^\s*(?:Escenario|Esquema del escenario)\s*:", text))
        if scenario_count < 3:
            raise SystemExit(f"`{path.as_posix()}` must contain at least 3 scenarios or scenario outlines")

    for layer in [
        "src/main/java/ar/edu/fiuba/is1/tpi/controller",
        "src/main/java/ar/edu/fiuba/is1/tpi/service",
        "src/main/java/ar/edu/fiuba/is1/tpi/domain",
        "src/main/java/ar/edu/fiuba/is1/tpi/repository",
        "src/main/java/ar/edu/fiuba/is1/tpi/config",
    ]:
        require_java_under(layer)
    require_java_under("src/test/java")


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
