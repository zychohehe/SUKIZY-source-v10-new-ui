#!/usr/bin/env bash
# Hapus sisa package lama com/auralis dari seluruh repo (aman dijalankan berulang).
set -euo pipefail
cd "$(dirname "$0")/.."
find . -type d -path "*/com/auralis" -not -path "./.git/*" -print -prune -exec rm -rf {} +
echo "Selesai. Jalankan: git add -A && git commit -m 'Remove stale com.auralis sources' && git push"
