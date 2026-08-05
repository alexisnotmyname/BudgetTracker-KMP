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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toLocalDateTime
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.theme.primaryLight
import ph.com.alexcr.core.presentation.theme.secondaryLight
import ph.com.alexcr.core.presentation.theme.surfaceLight
import ph.com.alexcr.core.presentation.theme.surfaceVariantLight
import kotlin.time.Clock.System

@Composable
fun MonthYearPickerDialog(
    selectedYearMonth: YearMonth?,
    onYearMonthSelected: (YearMonth?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        MonthYearPickerContent(
            selectedYearMonth = selectedYearMonth,
            onYearMonthSelected = onYearMonthSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun MonthYearPickerContent(
    selectedYearMonth: YearMonth?,
    onYearMonthSelected: (YearMonth?) -> Unit,
    onDismiss: () -> Unit
) {
    val today = System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val currentYearMonth = YearMonth(today.year, today.month)

    var pendingYearMonth by remember { mutableStateOf(selectedYearMonth ?: currentYearMonth) }
    var showYearDropdown by remember { mutableStateOf(false) }

    val yearRange = (today.year - 5)..(today.year + 1)

    val months = Month.entries

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariantLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Select Month",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

        // Year row: dropdown + < >
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (pendingYearMonth.year > yearRange.first) {
                        pendingYearMonth = YearMonth(pendingYearMonth.year - 1, pendingYearMonth.month)
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous year",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showYearDropdown = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pendingYearMonth.year.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = " ▾",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showYearDropdown,
                    onDismissRequest = { showYearDropdown = false }
                ) {
                    yearRange.forEach { year ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = year.toString(),
                                    fontWeight = if (year == pendingYearMonth.year) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                pendingYearMonth = YearMonth(year, pendingYearMonth.month)
                                showYearDropdown = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    if (pendingYearMonth.year < yearRange.last) {
                        pendingYearMonth = YearMonth(pendingYearMonth.year + 1, pendingYearMonth.month)
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next year",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4x3 month grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            userScrollEnabled = false
        ) {
            items(months) { month ->
                val isSelected = pendingYearMonth.month == month
                val isCurrentMonth = currentYearMonth.month == month && currentYearMonth.year == pendingYearMonth.year
                val label = month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

                MonthChip(
                    label = label,
                    isSelected = isSelected,
                    isCurrentMonth = isCurrentMonth,
                    onClick = { pendingYearMonth = YearMonth(pendingYearMonth.year, month) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                text = "All",
                onClick = {
                    onYearMonthSelected(null)
                    onDismiss()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                text = "Cancel",
                onClick = onDismiss
            )
            Spacer(modifier = Modifier.width(8.dp))
            GenericButton(
                text = "Done",
                modifier = Modifier.weight(1f),
                onClick = {
                    onYearMonthSelected(pendingYearMonth)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun MonthChip(
    label: String,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(shape)
            .background(if (isSelected) primaryLight else surfaceLight)
            .then(
                if (isCurrentMonth && !isSelected)
                    Modifier.border(1.5.dp, secondaryLight, shape)
                else
                    Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
            ),
            textAlign = TextAlign.Center,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                isCurrentMonth -> secondaryLight
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MonthYearPickerContentPreview() {
    BudgetTrackerTheme {
        MonthYearPickerContent(
            selectedYearMonth = YearMonth(2026, Month.AUGUST),
            onYearMonthSelected = {},
            onDismiss = {}
        )
    }
}
