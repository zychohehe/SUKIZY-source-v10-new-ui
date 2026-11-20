import unittest

import check_kotlin_sanity as sanity

NAMES = {"search", "library"}


class KotlinSanityTest(unittest.TestCase):
    def test_known_resources_pass(self):
        source = "import com.metrolist.shared.generated.resources.search\nval x = Res.string.library\n"
        self.assertEqual(sanity.check_source(source, "A.kt", NAMES), [])

    def test_missing_string_is_reported(self):
        errors = sanity.check_source("val x = Res.string.hilang\n", "A.kt", NAMES)
        self.assertEqual(len(errors), 1)
        self.assertIn("hilang", errors[0])

    def test_missing_import_and_duplicates(self):
        source = (
            "import com.metrolist.shared.generated.resources.hilang\n"
            "import com.metrolist.shared.generated.resources.search\n"
            "import com.metrolist.shared.generated.resources.search\n"
        )
        errors = sanity.check_source(source, "A.kt", NAMES)
        self.assertEqual(len(errors), 2)

    def test_resource_names_reads_all_kinds(self):
        xml = '<resources><string name="a">x</string><plurals name="b"/><string-array name="c"/></resources>'
        self.assertEqual(sanity.resource_names(xml), {"a", "b", "c"})


if __name__ == "__main__":
    unittest.main()
