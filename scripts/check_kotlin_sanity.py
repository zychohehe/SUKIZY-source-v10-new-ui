#!/usr/bin/env python3
"""Pengecekan cepat tanpa Gradle, supaya error umum ketahuan sebelum build 20 menit.

1. Setiap `Res.string.nama` di Kotlin harus ada di values/strings.xml.
2. Setiap import `...generated.resources.nama` harus punya string, plural, atau array yang sesuai.
3. Tidak ada import ganda dalam satu file.

Pengecekan kurung sengaja tidak ada: literal karakter dan raw string membuatnya salah lapor.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parent.parent
RESOURCES = ROOT / "shared/src/commonMain/composeResources/values/strings.xml"
KOTLIN_ROOT = ROOT / "shared/src/commonMain/kotlin"

STRING_REF = re.compile(r"\bRes\.string\.([A-Za-z0-9_]+)")
RES_IMPORT = re.compile(r"^import com\.metrolist\.shared\.generated\.resources\.([A-Za-z0-9_]+)\s*$", re.MULTILINE)


def resource_names(xml_text: str) -> set[str]:
    root = ElementTree.fromstring(xml_text)
    return {el.get("name") for el in root if el.get("name")}


def duplicate_imports(source: str, label: str) -> list[str]:
    imports = [line for line in source.splitlines() if line.startswith("import ")]
    return [f"{label}: import ganda `{line}`" for line in sorted({i for i in imports if imports.count(i) > 1})]


def check_source(source: str, label: str, names: set[str]) -> list[str]:
    errors = duplicate_imports(source, label)
    for name in sorted(set(STRING_REF.findall(source))):
        if name not in names:
            errors.append(f"{label}: Res.string.{name} tidak ada di values/strings.xml")
    for name in sorted(set(RES_IMPORT.findall(source))):
        # Import `Res` sendiri dan resource non-string (drawable, font, dll.) dilewati.
        if name in {"Res"} or name[0].isupper():
            continue
        if name not in names and not re.search(rf"\bRes\.(drawable|font|files)\.{name}\b", source):
            errors.append(f"{label}: import resource `{name}` tidak ada di values/strings.xml")
    return errors


def main() -> int:
    names = resource_names(RESOURCES.read_text(encoding="utf-8"))
    errors: list[str] = []
    for path in sorted(KOTLIN_ROOT.rglob("*.kt")):
        errors += check_source(path.read_text(encoding="utf-8"), str(path.relative_to(ROOT)), names)
    for error in errors:
        print(f"::error::{error}")
    print(f"{len(errors)} masalah ditemukan." if errors else "Kotlin sanity check: OK")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
