package ph.com.alexcr.core.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatDateTime(epochMillis: Long?): String {
    epochMillis ?: return ""
    val local = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val day = local.day
    val dayOfWeek = local.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

    return "$month $day $dayOfWeek"
}

/** Returns a stable "yyyy-MM-dd" string for grouping transactions by day. */
fun formatDateKey(epochMillis: Long?): String {
    epochMillis ?: return ""
    val local = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.month.toString().padStart(2, '0')
    val day = local.day.toString().padStart(2, '0')
    return "${local.year}-$month-$day"
}

/** Returns a human-readable date header label, e.g. "Jul 16, Wednesday". */
fun formatDateHeader(epochMillis: Long?): String {
    epochMillis ?: return "Unknown Date"
    val local = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val day = local.day
    val dayOfWeek = local.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month $day, $dayOfWeek"
}
