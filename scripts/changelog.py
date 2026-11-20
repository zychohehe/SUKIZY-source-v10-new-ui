#!/usr/bin/env python3
"""Changelog otomatis dari pesan commit (gaya conventional commits).

    changelog.py notes   --version X.Y.Z [--from TAG] [--to REV]   cetak catatan rilis
    changelog.py update  --version X.Y.Z --notes-file FILE         tambahkan ke CHANGELOG.md dan README
    changelog.py section --version X.Y.Z                           cetak bagian versi itu dari CHANGELOG.md

Commit `chore: versi ...` dan yang memuat `[skip ci]` diabaikan.
"""

from __future__ import annotations

import argparse
import datetime
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CHANGELOG = ROOT / "CHANGELOG.md"
README = ROOT / "README.md"

README_START = "<!-- changelog:start -->"
README_END = "<!-- changelog:end -->"

COMMIT = re.compile(r"^(?P<type>[a-z]+)(\([^)]*\))?!?:\s*(?P<text>.+)$")

# (judul, tipe commit); urutan = urutan tampil.
SECTIONS: list[tuple[str, tuple[str, ...]]] = [
    ("Fitur baru", ("feat",)),
    ("Perbaikan", ("fix",)),
    ("Performa", ("perf",)),
    ("Perapian kode", ("refactor",)),
    ("Build dan CI", ("build", "ci")),
    ("Dokumentasi", ("docs",)),
]
OTHER_TITLE = "Lainnya"


def skip_subject(subject: str) -> bool:
    lowered = subject.lower()
    return "[skip ci]" in lowered or lowered.startswith("chore: versi") or lowered.startswith("merge ")


def group_subjects(subjects: list[str]) -> dict[str, list[str]]:
    known = {kind: title for title, kinds in SECTIONS for kind in kinds}
    grouped: dict[str, list[str]] = {}
    for subject in subjects:
        subject = subject.strip()
        if not subject or skip_subject(subject):
            continue
        match = COMMIT.match(subject)
        if match and match.group("type") in known:
            title, text = known[match.group("type")], match.group("text")
        elif match:
            title, text = OTHER_TITLE, match.group("text")
        else:
            title, text = OTHER_TITLE, subject
        text = text[0].upper() + text[1:] if text else text
        grouped.setdefault(title, []).append(text)
    return grouped


def render_entry(version: str, subjects: list[str], today: datetime.date | None = None) -> str:
    today = today or datetime.date.today()
    grouped = group_subjects(subjects)
    lines = [f"## v{version} ({today.isoformat()})", ""]
    order = [title for title, _ in SECTIONS] + [OTHER_TITLE]
    wrote = False
    for title in order:
        items = grouped.get(title)
        if not items:
            continue
        wrote = True
        lines.append(f"### {title}")
        lines.extend(f"- {item}" for item in items)
        lines.append("")
    if not wrote:
        lines += ["- Perubahan kecil dan perbaikan internal.", ""]
    return "\n".join(lines).rstrip() + "\n"


def prepend_entry(changelog: str, entry: str) -> str:
    header = "# Changelog\n\n"
    body = changelog[len(header):] if changelog.startswith(header) else changelog
    version_heading = entry.splitlines()[0]
    if version_heading in body:
        return header + body  # sudah ada, jangan duplikat
    return header + entry + "\n" + body.lstrip("\n")


def extract_section(changelog: str, version: str) -> str | None:
    pattern = re.compile(rf"^## v{re.escape(version)} .*?(?=^## v|\Z)", re.MULTILINE | re.DOTALL)
    match = pattern.search(changelog)
    return match.group(0).strip() + "\n" if match else None


def replace_readme_block(readme: str, entry: str) -> str:
    if README_START not in readme or README_END not in readme:
        return readme
    start = readme.index(README_START) + len(README_START)
    end = readme.index(README_END)
    # Di README judul diturunkan dua tingkat supaya tidak bentrok dengan struktur halaman.
    demoted = re.sub(r"^(#{2,3}) ", lambda m: "##" + m.group(1) + " ", entry, flags=re.MULTILINE)
    block = "\n" + demoted + "\n"
    return readme[:start] + block + readme[end:]


def git(*args: str) -> str:
    return subprocess.run(["git", *args], cwd=ROOT, check=True, capture_output=True, text=True).stdout


def previous_tag(rev: str) -> str | None:
    try:
        return git("describe", "--tags", "--abbrev=0", "--match", "v*", f"{rev}^").strip() or None
    except subprocess.CalledProcessError:
        return None


def commit_subjects(since: str | None, rev: str) -> list[str]:
    span = f"{since}..{rev}" if since else rev
    args = ["log", "--no-merges", "--pretty=%s"]
    if not since:
        args += ["-n", "50"]
    return git(*args, span).splitlines()


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)
    notes = sub.add_parser("notes")
    notes.add_argument("--version", required=True)
    notes.add_argument("--from", dest="since")
    notes.add_argument("--to", default="HEAD")
    update = sub.add_parser("update")
    update.add_argument("--version", required=True)
    update.add_argument("--notes-file", required=True)
    section = sub.add_parser("section")
    section.add_argument("--version", required=True)
    args = parser.parse_args(argv[1:])

    if args.command == "notes":
        since = args.since or previous_tag(args.to)
        sys.stdout.write(render_entry(args.version, commit_subjects(since, args.to)))
        return 0
    if args.command == "update":
        entry = Path(args.notes_file).read_text(encoding="utf-8")
        current = CHANGELOG.read_text(encoding="utf-8") if CHANGELOG.exists() else "# Changelog\n\n"
        CHANGELOG.write_text(prepend_entry(current, entry), encoding="utf-8")
        if README.exists():
            README.write_text(replace_readme_block(README.read_text(encoding="utf-8"), entry), encoding="utf-8")
        return 0
    found = extract_section(CHANGELOG.read_text(encoding="utf-8"), args.version) if CHANGELOG.exists() else None
    if found is None:
        return 1
    sys.stdout.write(found)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
