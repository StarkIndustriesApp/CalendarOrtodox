package ro.ungu.unguisreligious

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File
import java.time.YearMonth

class MonthCatalogTest {

    private val romanianMonths = listOf(
        "ianuarie", "februarie", "martie", "aprilie", "mai", "iunie",
        "iulie", "august", "septembrie", "octombrie", "noiembrie", "decembrie",
    )

    private val fileNames = (2026..2028).flatMap { year ->
        romanianMonths.mapIndexed { i, name -> "%d_%02d_%s.png".format(year, i + 1, name) }
    }

    private val catalog = MonthCatalog(fileNames.shuffled())

    @Test
    fun bundledAssetsMatchExpectedNames() {
        // Unit tests run with the module directory as working directory.
        val bundled = File("src/main/assets/${MonthCatalog.ASSET_DIR}").list().orEmpty().sorted()
        assertEquals(fileNames.sorted(), bundled)
    }

    @Test
    fun ordersMonthsChronologically() {
        assertEquals(36, catalog.size)
        assertEquals("2026_01_ianuarie.png", catalog.fileName(0))
        assertEquals("2028_12_decembrie.png", catalog.fileName(35))
        assertEquals(YearMonth.of(2027, 6), catalog.month(17))
    }

    @Test
    fun beforeRangeOpensJanuary2026() {
        assertEquals(0, catalog.indexOf(YearMonth.of(2025, 12)))
        assertEquals(0, catalog.indexOf(YearMonth.of(1999, 1)))
    }

    @Test
    fun firstMonth() {
        assertEquals(0, catalog.indexOf(YearMonth.of(2026, 1)))
    }

    @Test
    fun midRange() {
        assertEquals(9, catalog.indexOf(YearMonth.of(2026, 10)))
        assertEquals(12, catalog.indexOf(YearMonth.of(2027, 1)))
    }

    @Test
    fun lastMonth() {
        assertEquals(35, catalog.indexOf(YearMonth.of(2028, 12)))
    }

    @Test
    fun afterRangeOpensDecember2028() {
        assertEquals(35, catalog.indexOf(YearMonth.of(2029, 1)))
        assertEquals(35, catalog.indexOf(YearMonth.of(2100, 6)))
    }

    @Test
    fun parsesFileNames() {
        assertEquals(YearMonth.of(2026, 2), MonthCatalog.parseMonth("2026_02_februarie.png"))
        assertNull(MonthCatalog.parseMonth("2026_13_x.png"))
        assertNull(MonthCatalog.parseMonth("icon.png"))
        assertNull(MonthCatalog.parseMonth("2026_02_februarie.jpg"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsGapInMonths() {
        MonthCatalog(fileNames.filterNot { it.startsWith("2027_05") })
    }
}
