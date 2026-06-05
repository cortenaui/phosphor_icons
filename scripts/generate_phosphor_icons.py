"""Generates the Phosphor glyph constants for every supported font weight.

Sources are read from this script's own folder, one subdirectory per
weight (regular, bold, light, fill, thin), each containing the
upstream IcoMoon `selection.json`.

Output is a single Kotlin file:

    <output>/framework/cortena/icons/PhosphorIcons.kt

Public shape:

    object PhosphorIcons {
        // Regular constants are also promoted to the top level so that
        // existing call sites like `PhosphorIcons.Alarm` keep working.
        val Alarm: PhosphorGlyph = Regular.Alarm

        object Regular { val Alarm: PhosphorGlyph = PhosphorGlyph("\\u...", PhosphorIconWeight.Regular) }
        object Bold    { val Alarm: PhosphorGlyph = PhosphorGlyph("\\u...", PhosphorIconWeight.Bold) }
        object Light   { ... }
        object Fill    { ... }
        object Thin    { ... }
    }
"""

import argparse
import json
import keyword
import shutil
from pathlib import Path

# Ordered so the generated file is deterministic.
WEIGHTS: tuple[str, ...] = ("regular", "bold", "light", "fill", "thin")

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True)
    return parser.parse_args()

def strip_weight_suffix(raw_name: str, weight: str) -> str:
    """Remove the trailing `-<weight>` segment that non-regular variants carry."""
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

def load_variant(scripts_dir: Path, weight: str) -> dict[str, str]:
    """Returns an ordered mapping of `IconName -> "\\uXXXX"` for a single weight."""
    selection_path = scripts_dir / weight / "selection.json"
    if not selection_path.is_file():
        raise FileNotFoundError(f"Missing selection.json for weight {weight!r}: {selection_path}")

    with selection_path.open(encoding="utf-8") as handle:
        data = json.load(handle)

    mapping: dict[str, str] = {}
    seen: set[str] = set()
    for icon in data["icons"]:
        props = icon["properties"]
        base_name = strip_weight_suffix(props["name"], weight)
        icon_name = sanitize_icon_name(base_name)
        if icon_name in seen:
            raise ValueError(f"[{weight}] duplicate normalized icon name {icon_name!r}")
        seen.add(icon_name)

        code_point = int(props["code"])
        mapping[icon_name] = f"\\u{code_point:04X}"
    return mapping

def render_variant_object(weight: str, icons: dict[str, str]) -> list[str]:
    object_name = weight.capitalize()
    weight_token = f"PhosphorIconWeight.{object_name}"
    lines = [f"    object {object_name} {{"]
    for name, code in icons.items():
        lines.append(
            f'        val {name}: PhosphorGlyph = PhosphorGlyph("{code}", {weight_token})'
        )
    lines.append("    }")
    return lines

def render_top_level_aliases(icons: dict[str, str]) -> list[str]:
    """Top-level Regular aliases for backward compatibility."""
    return [f"    val {name}: PhosphorGlyph = Regular.{name}" for name in icons]

def write_icons_file(output_dir: Path, variants: dict[str, dict[str, str]]) -> None:
    file_path = output_dir / "framework" / "cortena" / "icons" / "PhosphorIcons.kt"
    file_path.parent.mkdir(parents=True, exist_ok=True)

    lines: list[str] = [
        "/*",
        " * SPDX-License-Identifier: GPL-3.0-or-later",
        " * Copyright (C) 2026-present The CortenaOS Project",
        " */",
        "package framework.cortena.icons",
        "",
        "object PhosphorIcons {",
    ]
    for weight in WEIGHTS:
        lines.extend(render_variant_object(weight, variants[weight]))
        lines.append("")

    # Aliases must come after Regular is declared, but Kotlin object members
    # resolve regardless of textual order, so we emit them at the end.
    lines.extend(render_top_level_aliases(variants["regular"]))

    lines.append("}")
    lines.append("")

    file_path.write_text("\n".join(lines), encoding="utf-8")

def generate_icons(output_dir: Path, scripts_dir: Path) -> None:
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    variants: dict[str, dict[str, str]] = {weight: load_variant(scripts_dir, weight) for weight in WEIGHTS}
    write_icons_file(output_dir, variants)

def main() -> None:
    args = parse_args()
    scripts_dir = Path(__file__).resolve().parent
    output_dir = Path(args.output).resolve()
    generate_icons(output_dir, scripts_dir)

if __name__ == "__main__":
    main()
