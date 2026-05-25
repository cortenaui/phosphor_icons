import argparse
import json
import keyword
import shutil
from pathlib import Path

FONT_RESOURCE_NAME = "phosphor_regular"

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

def write_icons_file(output_dir: Path, icons: list[dict]) -> None:
    file_path = output_dir / "framework" / "cortena" / "icons" / "PhosphorIcons.kt"
    file_path.parent.mkdir(parents=True, exist_ok=True)

    seen_names: set[str] = set()
    icon_lines: list[str] = []
    for icon in icons:
        props = icon["properties"]
        icon_name = sanitize_icon_name(props["name"])
        if icon_name in seen_names:
            raise ValueError(f"Duplicate normalized icon name {icon_name!r}")
        seen_names.add(icon_name)

        code_point = int(props["code"])
        icon_lines.append(f'    const val {icon_name}: String = "\\u{code_point:04X}"')

    file_path.write_text(
        "\n".join(
            [
                "package framework.cortena.icons",
                "",
                "import androidx.compose.ui.text.font.Font",
                "import androidx.compose.ui.text.font.FontFamily",
                "",
                "object PhosphorIcons {",
                f'    val DefaultFontFamily: FontFamily = FontFamily(Font(R.font.{FONT_RESOURCE_NAME}))',
                "",
                *icon_lines,
                "}",
                "",
            ]
        ),
        encoding="utf-8",
    )

def generate_icons(output_dir: Path, repo_root: Path) -> None:
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    selection_path = repo_root / "ref" / "Fonts" / "regular" / "selection.json"
    with selection_path.open(encoding="utf-8") as handle:
        data = json.load(handle)

    write_icons_file(output_dir, data["icons"])

def main() -> None:
    args = parse_args()
    repo_root = Path(__file__).resolve().parent.parent
    output_dir = Path(args.output).resolve()
    generate_icons(output_dir, repo_root)

if __name__ == "__main__":
    main()
