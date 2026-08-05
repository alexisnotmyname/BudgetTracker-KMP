package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.theme.primaryLight
import ph.com.alexcr.core.presentation.theme.secondaryLight
import ph.com.alexcr.core.presentation.theme.surfaceLight
import ph.com.alexcr.core.presentation.theme.surfaceVariantLight
import kotlin.time.Clock.System

@Composable
fun DateFilterPicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        DateFilterContent(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun DateFilterContent(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val today = System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    var pendingDate by remember { mutableStateOf(selectedDate) }

    var currentMonth by remember {
        mutableStateOf(
            selectedDate?.let { YearMonth(it.year, it.month) }
                ?: YearMonth(today.year, today.month)
        )
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariantLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

        // Month/Year Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minus(1, DateTimeUnit.MONTH) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = buildString {
                    append(currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() })
                    append(" ")
                    append(currentMonth.year)
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Day headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Calendar days grid
        val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
        val daysInMonth = getDaysInMonth(currentMonth.month, currentMonth.year)
        // Sunday = 0 offset (isoDayNumber: Mon=1..Sun=7, so Sunday % 7 = 0)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber % 7

        val calendarDays = mutableListOf<LocalDate?>()
        // Add empty slots for days before month starts
        repeat(firstDayOfWeek) {
            calendarDays.add(null)
        }
        // Add days of the month
        for (day in 1..daysInMonth) {
            calendarDays.add(LocalDate(currentMonth.year, currentMonth.month, day))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            userScrollEnabled = false
        ) {
            items(calendarDays.size) { index ->
                val date = calendarDays[index]
                if (date == null) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    val isSelected = date == pendingDate
                    val isToday = date == today

                    DayButton(
                        day = date.dayOfMonth,
                        isSelected = isSelected,
                        isToday = isToday,
                        onClick = { pendingDate = date }
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                text = "Cancel",
                onClick = onDismiss
            )
            Spacer(modifier = Modifier.width(8.dp))
            GenericButton(
                text = "Done",
                modifier = Modifier.weight(1f),
                onClick = {
                    onDateSelected(pendingDate)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun DayButton(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val shape = CircleShape
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape)
            .background(if (isSelected) primaryLight else surfaceLight)
            .then(
                if (isToday && !isSelected)
                    Modifier.border(1.5.dp, secondaryLight, shape)
                else
                    Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            ),
            textAlign = TextAlign.Center,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                isToday -> secondaryLight
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun DateFilterContentPreview() {
    BudgetTrackerTheme {
        DateFilterContent(
            selectedDate = LocalDate(2026, 8, 4),
            onDateSelected = {},
            onDismiss = {}
        )
    }
}

private fun getDaysInMonth(month: Month, year: Int): Int {
    return when (month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
