"""Generates the demo-only icon registry consumed by the :catalog module.

This registry is intentionally local to the catalog app so that the
`:phosphor-icons` library does not have to expose a global runtime registry
(see CLAUDE.md). Naming rules mirror `generate_phosphor_icons.py` so that
the listed names match the constants in `PhosphorIcons`.
"""

import argparse
import json
import keyword
from pathlib import Path

LICENSE_HEADER = (
    "/*\n"
    " * SPDX-License-Identifier: GPL-3.0-or-later\n"
    " * Copyright (C) 2026-present The CortenaOS Project\n"
    " */\n"
)

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True)
    return parser.parse_args()

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

def write_catalog_file(output_dir: Path, icons: list[dict]) -> None:
    file_path = output_dir / "app" / "cortena" / "icons" / "catalog" / "PhosphorIconCatalog.kt"
    file_path.parent.mkdir(parents=True, exist_ok=True)

    seen_names: set[str] = set()
    entry_lines: list[str] = []
    for icon in icons:
        props = icon["properties"]
        icon_name = sanitize_icon_name(props["name"])
        if icon_name in seen_names:
            raise ValueError(f"Duplicate normalized icon name {icon_name!r}")
        seen_names.add(icon_name)
        entry_lines.append(f"    PhosphorIconEntry(\"{icon_name}\", PhosphorIcons.{icon_name}),")

    body = "\n".join(
        [
            LICENSE_HEADER,
            "package app.cortena.icons.catalog",
            "",
            "import framework.cortena.icons.PhosphorIcons",
            "",
            "/** Demo-only descriptor pairing a constant name with its glyph code point. */",
            "data class PhosphorIconEntry(val name: String, val code: String)",
            "",
            "/** Full list of Phosphor icons exposed by the library, generated from selection.json. */",
            "val PhosphorIconCatalog: List<PhosphorIconEntry> = listOf(",
            *entry_lines,
            ")",
            "",
        ]
    )

    file_path.write_text(body, encoding="utf-8")

def generate_catalog(output_dir: Path, repo_root: Path) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)

    selection_path = repo_root / "ref" / "Fonts" / "regular" / "selection.json"
    with selection_path.open(encoding="utf-8") as handle:
        data = json.load(handle)

    write_catalog_file(output_dir, data["icons"])

def main() -> None:
    args = parse_args()
    repo_root = Path(__file__).resolve().parent.parent
    output_dir = Path(args.output).resolve()
    generate_catalog(output_dir, repo_root)

if __name__ == "__main__":
    main()
