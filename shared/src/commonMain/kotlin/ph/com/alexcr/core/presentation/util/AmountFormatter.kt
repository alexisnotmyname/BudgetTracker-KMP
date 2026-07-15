package ph.com.alexcr.core.presentation.util

import kotlin.math.round

fun formatAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        amount.toLong().toString()
    } else {
        val rounded = round(amount * 100) / 100.0
        val intPart = rounded.toLong()
        val decPart = round((rounded - intPart) * 100).toInt()
        "$intPart.${decPart.toString().padStart(2, '0')}"
    }
}
