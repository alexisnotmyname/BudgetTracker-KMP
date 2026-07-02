package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme

private val numPadKeys = listOf(
    listOf("7", "8", "9"),
    listOf("4", "5", "6"),
    listOf("1", "2", "3"),
    listOf(".", "0", "⌫"),
)

/**
 * A custom numeric keypad for money entry.
 *
 * @param onDigit  Called when a digit (0–9) is pressed.
 * @param onBackspace Called when the backspace key is pressed.
 */
@Composable
fun NumPad(
    modifier: Modifier = Modifier,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit = {},
    onBackspace: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        numPadKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    NumPadKey(
                        label = key,
                        modifier = Modifier.weight(1f),
                        onDigit = onDigit,
                        onDecimal = onDecimal,
                        onBackspace = onBackspace,
                    )
                }
            }
        }
    }
}

@Composable
private fun NumPadKey(
    label: String,
    modifier: Modifier = Modifier,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit = {},
    onBackspace: () -> Unit,
) {
    val isBackspace = label == "⌫"
    val isDecimal = label == "."
    val isEmpty = label.isEmpty()

    Button(
        onClick = {
            when {
                isDecimal -> onDecimal()
                isBackspace -> onBackspace()
                !isEmpty -> onDigit(label)
            }
        },
        modifier = modifier.height(60.dp),
        enabled = !isEmpty,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isBackspace -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = when {
                isBackspace -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onSurface
            },
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        if (isBackspace) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                contentDescription = "Backspace",
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NumPadPreview() {
    BudgetTrackerTheme {
        NumPad(
            onDigit = {},
            onDecimal = {},
            onBackspace = {},
        )
    }
}

