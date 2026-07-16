package ph.com.alexcr.tracker.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import budget.shared.generated.resources.Res
import budget.shared.generated.resources.save
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import ph.com.alexcr.core.presentation.components.CategoryPickerSheet
import ph.com.alexcr.core.presentation.components.GenericButton
import ph.com.alexcr.core.presentation.components.NumPadBottomSheet
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.util.iconForCategory
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultIncomeCategories
import kotlin.time.Instant

@Composable
fun InputIncome(
    categories: List<TransactionCategory>,
    modifier: Modifier = Modifier,
    initialTransaction: BudgetTransaction.Income? = null,
    onBudgetItemChange: (BudgetTransaction.Income) -> Unit = {},
    onSave: (BudgetTransaction.Income) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var isAmountFocused by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember(categories) {
        mutableStateOf(
            initialTransaction?.category
                ?: categories.firstOrNull()
                ?: TransactionCategory(name = "")
        )
    }
    var notes by remember { mutableStateOf(initialTransaction?.note ?: "") }

    val (initInteger, initDecimal, initDecimalMode) = remember(initialTransaction) {
        val amount = initialTransaction?.amount ?: 0.0
        if (amount == 0.0) {
            Triple("", null as String?, false)
        } else {
            val str = amount.toString()
            val parts = str.split(".")
            val intPart = if (parts[0] == "0") "" else parts[0]
            val decPart = parts.getOrNull(1)?.trimEnd('0')
            if (decPart.isNullOrEmpty()) {
                Triple(intPart, null as String?, false)
            } else {
                Triple(intPart, decPart, true)
            }
        }
    }

    var integerPart by remember { mutableStateOf(initInteger) }
    var decimalPart by remember { mutableStateOf<String?>(initDecimal) }
    var isDecimalMode by remember { mutableStateOf(initDecimalMode) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialTransaction?.dateTimeCreated
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        val local = Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val month = local.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }.take(3)
        "$month ${local.day.toString().padStart(2, '0')}, ${local.year}"
    } ?: ""

    val displayAmount = remember(integerPart, decimalPart, isDecimalMode) {
        val intFormatted = if (integerPart.isEmpty()) "0"
        else integerPart.toLongOrNull()?.toString()
            ?.reversed()?.chunked(3)?.joinToString(",")?.reversed()
            ?: "0"
        when {
            !isDecimalMode -> "₱ $intFormatted"
            decimalPart.isNullOrEmpty() -> "₱ $intFormatted."
            else -> "₱ $intFormatted.$decimalPart"
        }
    }

    val finalAmount = remember(integerPart, decimalPart, isDecimalMode) {
        buildString {
            append(integerPart.ifEmpty { "0" })
            if (isDecimalMode) {
                append(".")
                append((decimalPart ?: "").padEnd(2, '0'))
            }
        }.toDoubleOrNull() ?: 0.0
    }

    LaunchedEffect(
        finalAmount,
        selectedCategory,
        notes,
        datePickerState.selectedDateMillis
    ) {
        onBudgetItemChange(
            BudgetTransaction.Income(
                amount = finalAmount,
                category = selectedCategory,
                note = notes,
                dateTimeCreated = datePickerState.selectedDateMillis
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isAmountFocused = true
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                value = displayAmount,
                onValueChange = {},
                enabled = false,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (isAmountFocused)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary,
                )
            )
        }

        Row(
            modifier = Modifier
                .padding(4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showCategoryPicker = true
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge,
                value = selectedCategory.name,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = "Select category",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = selectedCategory.let { cat ->
                    {
                        Icon(
                            imageVector = iconForCategory(cat.name),
                            contentDescription = cat.name,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (isAmountFocused)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary,
                )
            )
        }

        Row(
            modifier = Modifier
                .padding(4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showDatePicker = true
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge,
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Pick date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (isAmountFocused)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary,
                )
            )
        }

        Row(
            modifier = Modifier
                .padding(4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isAmountFocused = false
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            isAmountFocused = false
                        }
                    },
                textStyle = MaterialTheme.typography.labelLarge,
                value = notes,
                onValueChange = { notes = it },
                enabled = true,
                placeholder = {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        GenericButton(
            text = stringResource(Res.string.save),
            onClick = {
                onSave(
                    BudgetTransaction.Income(
                        id = initialTransaction?.id ?: 0L,
                        amount = finalAmount,
                        category = selectedCategory,
                        note = notes,
                        dateTimeCreated = datePickerState.selectedDateMillis,
                    )
                )
            }
        )
    }

    if (isAmountFocused) {
        NumPadBottomSheet(
            onDismiss = { isAmountFocused = false },
            onDigit = { digit ->
                if (isDecimalMode) {
                    if ((decimalPart?.length ?: 0) < 2) {
                        decimalPart = (decimalPart ?: "") + digit
                    }
                } else {
                    if (integerPart.length < 10) integerPart += digit
                }
            },
            onDecimal = {
                if (!isDecimalMode) {
                    isDecimalMode = true
                    decimalPart = ""
                }
            },
            onBackspace = {
                when {
                    isDecimalMode && decimalPart?.isNotEmpty() == true -> {
                        decimalPart = decimalPart!!.dropLast(1)
                    }

                    isDecimalMode && decimalPart?.isEmpty() == true -> {
                        isDecimalMode = false
                        decimalPart = null
                    }

                    integerPart.isNotEmpty() -> {
                        integerPart = integerPart.dropLast(1)
                    }
                }
            }
        )
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            categories = categories,
            selected = selectedCategory,
            onSelect = { selectedCategory = it },
            onDismiss = { showCategoryPicker = false }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            shape = RoundedCornerShape(8.dp),
            confirmButton = {
                IconButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputIncomePreview() {
    BudgetTrackerTheme {
        InputIncome(categories = defaultIncomeCategories)
    }
}
