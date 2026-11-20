import datetime
import unittest

import changelog

DAY = datetime.date(2026, 11, 20)


class ChangelogTest(unittest.TestCase):
    def test_groups_by_type_and_skips_noise(self):
        entry = changelog.render_entry(
            "14.2.0",
            [
                "feat(home): kartu sapaan",
                "fix: crash saat buka Search",
                "chore: versi 14.1.9 [skip ci]",
                "perapian tanpa prefix",
                "Merge branch 'x'",
            ],
            DAY,
        )
        self.assertIn("## v14.2.0 (2026-11-20)", entry)
        self.assertLess(entry.index("### Fitur baru"), entry.index("### Perbaikan"))
        self.assertIn("- Kartu sapaan", entry)
        self.assertIn("- Crash saat buka Search", entry)
        self.assertIn("### Lainnya\n- Perapian tanpa prefix", entry)
        self.assertNotIn("14.1.9", entry)
        self.assertNotIn("Merge", entry)

    def test_empty_history_still_renders(self):
        self.assertIn("Perubahan kecil", changelog.render_entry("1.0.0", [], DAY))

    def test_prepend_is_idempotent(self):
        entry = changelog.render_entry("1.0.1", ["fix: a"], DAY)
        once = changelog.prepend_entry("# Changelog\n\n## v1.0.0 (2026-01-01)\n\n- x\n", entry)
        twice = changelog.prepend_entry(once, entry)
        self.assertEqual(once, twice)
        self.assertLess(once.index("v1.0.1"), once.index("v1.0.0"))

    def test_extract_section(self):
        text = "# Changelog\n\n## v1.0.1 (d)\n\n- a\n\n## v1.0.0 (d)\n\n- b\n"
        self.assertEqual(changelog.extract_section(text, "1.0.1"), "## v1.0.1 (d)\n\n- a\n")
        self.assertIsNone(changelog.extract_section(text, "9.9.9"))

    def test_readme_block_replaced_only_between_markers(self):
        readme = f"a\n{changelog.README_START}\nold\n{changelog.README_END}\nb\n"
        out = changelog.replace_readme_block(readme, "## v1.0.1 (d)\n\n### Perbaikan\n- x\n")
        self.assertIn("#### v1.0.1 (d)", out)
        self.assertIn("##### Perbaikan", out)
        self.assertNotIn("old", out)
        self.assertTrue(out.startswith("a\n") and out.endswith("b\n"))
        self.assertEqual(changelog.replace_readme_block("tanpa penanda", "x"), "tanpa penanda")


if __name__ == "__main__":
    unittest.main()
