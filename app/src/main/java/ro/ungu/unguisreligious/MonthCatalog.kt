package ro.ungu.unguisreligious

import java.time.YearMonth
import java.time.temporal.ChronoUnit

/**
 * The bundled month images in chronological order, one pager page per month.
 * File names follow `YYYY_MM_monthname.png`; the months must be contiguous.
 */
class MonthCatalog(fileNames: List<String>) {

    private val months: List<YearMonth>
    private val files: List<String>

    init {
        val parsed = fileNames
            .mapNotNull { name -> parseMonth(name)?.let { it to name } }
            .sortedBy { it.first }
        require(parsed.isNotEmpty()) { "No month images found" }
        parsed.zipWithNext().forEach { (a, b) ->
            require(b.first == a.first.plusMonths(1)) {
                "Month images are not contiguous: ${a.second} -> ${b.second}"
            }
        }
        months = parsed.map { it.first }
        files = parsed.map { it.second }
    }

    val size: Int get() = months.size

    fun fileName(index: Int): String = files[index]

    fun month(index: Int): YearMonth = months[index]

    /** Page index for [month], clamped to the first/last available month. */
    fun indexOf(month: YearMonth): Int {
        val offset = ChronoUnit.MONTHS.between(months.first(), month)
        return offset.coerceIn(0L, size - 1L).toInt()
    }

    companion object {
        const val ASSET_DIR = "months"

        private val FILE_NAME = Regex("""^(\d{4})_(\d{2})_.+\.png$""")

        fun parseMonth(fileName: String): YearMonth? {
            val match = FILE_NAME.matchEntire(fileName) ?: return null
            val month = match.groupValues[2].toInt()
            if (month !in 1..12) return null
            return YearMonth.of(match.groupValues[1].toInt(), month)
        }
    }
}
