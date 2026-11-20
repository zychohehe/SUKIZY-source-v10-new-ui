#!/usr/bin/env python3
"""Naikkan appVersion di gradle.properties dan sinkronkan README.

Dipanggil oleh workflow SUKIZY BUILD APK sebelum build:

    python3 scripts/bump_version.py patch

Mencetak versi baru ke stdout. Opsi `none` hanya membaca versi sekarang.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
GRADLE_PROPERTIES = ROOT / "gradle.properties"
README = ROOT / "README.md"

VERSION_LINE = re.compile(r"^(appVersion=)(\d+)\.(\d+)\.(\d+)[ \t]*$", re.MULTILINE)
README_VERSION = re.compile(r"^(Versi \*\*)[^*]+(\*\*)[ \t]*$", re.MULTILINE)


def bump(version: tuple[int, int, int], part: str) -> tuple[int, int, int]:
    major, minor, patch = version
    if part == "major":
        return major + 1, 0, 0
    if part == "minor":
        return major, minor + 1, 0
    if part == "patch":
        return major, minor, patch + 1
    if part == "none":
        return version
    raise ValueError(f"bagian versi tidak dikenal: {part}")


def update_properties(text: str, part: str) -> tuple[str, str]:
    match = VERSION_LINE.search(text)
    if match is None:
        raise SystemExit("appVersion=MAJOR.MINOR.PATCH tidak ditemukan di gradle.properties")
    current = (int(match.group(2)), int(match.group(3)), int(match.group(4)))
    new = bump(current, part)
    new_text = text[: match.start()] + f"{match.group(1)}{new[0]}.{new[1]}.{new[2]}" + text[match.end() :]
    return new_text, ".".join(str(n) for n in new)


def update_readme(text: str, version: str) -> str:
    return README_VERSION.sub(lambda m: f"{m.group(1)}{version}{m.group(2)}", text, count=1)


def main(argv: list[str]) -> int:
    part = argv[1] if len(argv) > 1 else "patch"
    properties, version = update_properties(GRADLE_PROPERTIES.read_text(encoding="utf-8"), part)
    if part != "none":
        GRADLE_PROPERTIES.write_text(properties, encoding="utf-8")
        if README.exists():
            README.write_text(update_readme(README.read_text(encoding="utf-8"), version), encoding="utf-8")
    print(version)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
