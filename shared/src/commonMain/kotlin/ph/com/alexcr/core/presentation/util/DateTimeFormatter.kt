package ph.com.alexcr.core.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatDateTime(epochMillis: Long?): String {
    epochMillis ?: return ""
    val local = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val day = local.dayOfMonth
    val dayOfWeek = local.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

    return "$month $day $dayOfWeek"
}
