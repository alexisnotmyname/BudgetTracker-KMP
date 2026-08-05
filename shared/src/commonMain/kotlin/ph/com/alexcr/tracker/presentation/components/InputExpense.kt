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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import ph.com.alexcr.core.presentation.components.CategoryPickerSheet
import ph.com.alexcr.core.presentation.components.DateFilterPicker
import ph.com.alexcr.core.presentation.components.GenericButton
import ph.com.alexcr.core.presentation.components.NumPadBottomSheet
import ph.com.alexcr.core.presentation.util.toEpochMillis
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.theme.surfaceLight
import ph.com.alexcr.core.presentation.util.iconForCategory
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories
import kotlin.text.ifEmpty
import kotlin.time.Instant

@Composable
fun InputExpense(
    categories: List<TransactionCategory>,
    modifier: Modifier = Modifier,
    initialTransaction: BudgetTransaction.Expense? = null,
    onBudgetItemChange: (BudgetTransaction.Expense) -> Unit = {},
    onSave: (BudgetTransaction.Expense) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var isAmountFocused by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember(initialTransaction?.id) {
        mutableStateOf(initialTransaction?.paymentMethod ?: PaymentMethod.CASH)
    }
    var notes by remember(initialTransaction?.id) { mutableStateOf(initialTransaction?.note ?: "") }
    var selectedCategory by remember(initialTransaction?.id, categories) {
        mutableStateOf(
            initialTransaction?.category
                ?: categories.firstOrNull()
                ?: TransactionCategory(name = "")
        )
    }

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

    var integerPart by remember(initialTransaction?.id) { mutableStateOf(initInteger) }
    var decimalPart by remember(initialTransaction?.id) { mutableStateOf(initDecimal) }
    var isDecimalMode by remember(initialTransaction?.id) { mutableStateOf(initDecimalMode) }

    var selectedDate by remember(initialTransaction?.id) {
        mutableStateOf(
            initialTransaction?.dateTimeCreated?.let { millis ->
                Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .let { LocalDate(it.year, it.monthNumber, it.dayOfMonth) }
            }
        )
    }

    val displayDateText = selectedDate?.let {
        val month = it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }.take(3)
        "$month ${it.dayOfMonth.toString().padStart(2, '0')}, ${it.year}"
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
        selectedPaymentMethod,
        selectedCategory,
        notes,
        selectedDate
    ) {
        onBudgetItemChange(
            BudgetTransaction.Expense(
                id = initialTransaction?.id ?: 0L,
                amount = finalAmount,
                paymentMethod = selectedPaymentMethod,
                category = selectedCategory,
                note = notes,
                dateTimeCreated = selectedDate?.toEpochMillis()
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

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            PaymentMethod.entries.forEachIndexed { index, method ->
                SegmentedButton(
                    selected = selectedPaymentMethod == method,
                    onClick = { selectedPaymentMethod = method },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = PaymentMethod.entries.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = selectedPaymentMethod == method) {
                            Icon(
                                imageVector = method.icon,
                                contentDescription = method.label,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        inactiveContainerColor = surfaceLight,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        activeBorderColor = Color.Transparent,
                        inactiveBorderColor = Color.Transparent
                    )
                ) {
                    Text(text = method.label)
                }
            }
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
                value = displayDateText,
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
                    BudgetTransaction.Expense(
                        id = initialTransaction?.id ?: 0L,
                        amount = finalAmount,
                        paymentMethod = selectedPaymentMethod,
                        category = selectedCategory,
                        note = notes,
                        dateTimeCreated = selectedDate?.toEpochMillis(),
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
        DateFilterPicker(
            selectedDate = selectedDate,
            onDateSelected = { newDate -> selectedDate = newDate },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputExpensePreview() {
    BudgetTrackerTheme {
        InputExpense(
            categories = defaultExpenseCategories
        )
    }
}