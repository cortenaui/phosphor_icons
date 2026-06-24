"""Generates the demo-only icon registry consumed by the :catalog module.

This registry is intentionally local to the catalog app so that the
`:phosphor-icons` library does not have to expose a global runtime registry
(see CLAUDE.md). Naming rules mirror `generate_phosphor_icons.py` so that
the listed names match the constants in `PhosphorIcons`.

The registry contains every icon for every supported weight. To keep each
JVM `<clinit>` method below the 64KB bytecode ceiling, entries are emitted
in private chunked builder functions and concatenated at runtime.
"""

import argparse
import json
import keyword
from pathlib import Path

LICENSE_HEADER = (
    "/*\n"
    " * SPDX-License-Identifier: GPL-3.0-or-later\n"
    " * Copyright (C) 2026-present The CortenaOS Project\n"
    " */"
)

WEIGHTS: tuple[str, ...] = ("regular", "bold", "light", "fill", "thin", "duotone")

# Conservative chunk size: each entry pushes ~40 bytes of bytecode in the
# enclosing method, so 512 entries land near 20KB which keeps every <clinit>
# comfortably under the 64KB JVM limit.
CHUNK_SIZE = 512

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True)
    return parser.parse_args()

def strip_weight_suffix(raw_name: str, weight: str) -> str:
    raw_name = raw_name.split(",", 1)[0]
    if weight == "regular":
        return raw_name
    suffix = f"-{weight}"
    if raw_name.endswith(suffix):
        return raw_name[: -len(suffix)]
    return raw_name

def sanitize_icon_name(raw_name: str) -> str:
    parts: list[str] = []
    token: list[str] = []

    for char in raw_name:
        if char.isalnum():
            token.append(char)
        elif token:
            parts.append("".join(token))
            token = []

    if token:
        parts.append("".join(token))

    if not parts:
        raise ValueError(f"Cannot normalize icon name: {raw_name!r}")

    normalized = "".join(part[:1].upper() + part[1:] for part in parts)
    if normalized[0].isdigit():
        normalized = f"Icon{normalized}"
    if keyword.iskeyword(normalized):
        normalized = f"{normalized}Icon"
    return normalized

def load_variant_names(scripts_dir: Path, weight: str) -> list[str]:
    selection_path = scripts_dir / weight / "selection.json"
    with selection_path.open(encoding="utf-8") as handle:
        data = json.load(handle)

    names: list[str] = []
    seen: set[str] = set()
    for icon in data["icons"]:
        props = icon["properties"]
        base_name = strip_weight_suffix(props["name"], weight)
        icon_name = sanitize_icon_name(base_name)
        if icon_name in seen:
            raise ValueError(f"[{weight}] duplicate normalized icon name {icon_name!r}")
        seen.add(icon_name)
        names.append(icon_name)
    return names

def chunk_lines(weight: str, names: list[str], chunk_index: int, chunk_names: list[str]) -> list[str]:
    object_name = weight.capitalize()
    weight_token = f"PhosphorIconWeight.{object_name}"
    fn_name = f"chunk_{weight}_{chunk_index}"
    lines = [f"private fun {fn_name}(): List<PhosphorIconEntry> = listOf("]
    for name in chunk_names:
        lines.append(
            f'    PhosphorIconEntry("{name}", {weight_token}, PhosphorIcons.{object_name}.{name}),'
        )
    lines.append(")")
    lines.append("")
    return lines, fn_name

def write_catalog_file(output_dir: Path, scripts_dir: Path) -> None:
    file_path = output_dir / "app" / "cortena" / "icons" / "catalog" / "PhosphorIconCatalog.kt"
    file_path.parent.mkdir(parents=True, exist_ok=True)

    chunk_fns_by_weight: dict[str, list[str]] = {}
    chunk_blocks: list[str] = []

    for weight in WEIGHTS:
        chunk_fns: list[str] = []
        names = load_variant_names(scripts_dir, weight)
        for chunk_index, start in enumerate(range(0, len(names), CHUNK_SIZE)):
            chunk = names[start : start + CHUNK_SIZE]
            block, fn_name = chunk_lines(weight, names, chunk_index, chunk)
            chunk_blocks.extend(block)
            chunk_fns.append(fn_name)
        chunk_fns_by_weight[weight] = chunk_fns

    builder_lines = [
        "/** Demo-only generated icon lists, built lazily per selected weight. */",
    ]
    for weight in WEIGHTS:
        property_name = f"{weight}Icons"
        builder_lines.append(f"private val {property_name}: List<PhosphorIconEntry> by lazy {{")
        builder_lines.append("    buildList {")
        for fn in chunk_fns_by_weight[weight]:
            builder_lines.append(f"        addAll({fn}())")
        builder_lines.append("    }")
        builder_lines.append("}")
        builder_lines.append("")

    builder_lines.extend(
        [
            "fun phosphorIconCatalog(weight: PhosphorIconWeight?): List<PhosphorIconEntry> =",
            "    when (weight) {",
            "        PhosphorIconWeight.Regular -> regularIcons",
            "        PhosphorIconWeight.Bold -> boldIcons",
            "        PhosphorIconWeight.Light -> lightIcons",
            "        PhosphorIconWeight.Fill -> fillIcons",
            "        PhosphorIconWeight.Thin -> thinIcons",
            "        PhosphorIconWeight.Duotone -> duotoneIcons",
            "        null -> PhosphorIconCatalog",
            "    }",
            "",
            "val PhosphorIconCatalog: List<PhosphorIconEntry> by lazy {",
            "    regularIcons + boldIcons + lightIcons + fillIcons + thinIcons + duotoneIcons",
            "}",
        ]
    )

    body = "\n".join(
        [
            LICENSE_HEADER,
            "package app.cortena.icons.catalog",
            "",
            "import framework.cortena.icons.PhosphorGlyph",
            "import framework.cortena.icons.PhosphorIconWeight",
            "import framework.cortena.icons.PhosphorIcons",
            "",
            "/** Demo-only descriptor pairing a constant name with its glyph. */",
            "data class PhosphorIconEntry(",
            "    val name: String,",
            "    val weight: PhosphorIconWeight,",
            "    val glyph: PhosphorGlyph,",
            ")",
            "",
            *chunk_blocks,
            *builder_lines,
            "",
        ]
    )

    file_path.write_text(body, encoding="utf-8")

def generate_catalog(output_dir: Path, scripts_dir: Path) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)
    write_catalog_file(output_dir, scripts_dir)

def main() -> None:
    args = parse_args()
    scripts_dir = Path(__file__).resolve().parent
    output_dir = Path(args.output).resolve()
    generate_catalog(output_dir, scripts_dir)

if __name__ == "__main__":
    main()
