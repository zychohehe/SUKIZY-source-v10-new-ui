import unittest

import bump_version


class BumpVersionTest(unittest.TestCase):
    def test_bump_parts(self):
        self.assertEqual(bump_version.bump((14, 0, 7), "patch"), (14, 0, 8))
        self.assertEqual(bump_version.bump((14, 0, 7), "minor"), (14, 1, 0))
        self.assertEqual(bump_version.bump((14, 0, 7), "major"), (15, 0, 0))
        self.assertEqual(bump_version.bump((14, 0, 7), "none"), (14, 0, 7))

    def test_update_properties_keeps_other_lines(self):
        text = "a=1\nappVersion=14.0.7\ninnerTubeXVersion=v0.11.9\n"
        new_text, version = bump_version.update_properties(text, "patch")
        self.assertEqual(version, "14.0.8")
        self.assertEqual(new_text, "a=1\nappVersion=14.0.8\ninnerTubeXVersion=v0.11.9\n")

    def test_update_readme_only_touches_version_line(self):
        text = "# SUKIZY\n\nVersi **14.0.7**\n\n## Yang baru di 14.0.7\n"
        self.assertEqual(
            bump_version.update_readme(text, "14.0.8"),
            "# SUKIZY\n\nVersi **14.0.8**\n\n## Yang baru di 14.0.7\n",
        )


if __name__ == "__main__":
    unittest.main()
