package ph.com.alexcr.tracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import budget.shared.generated.resources.Res
import budget.shared.generated.resources.add
import budget.shared.generated.resources.budget_tracker
import budget.shared.generated.resources.save
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ph.com.alexcr.core.presentation.components.CategoryPickerSheet
import ph.com.alexcr.core.presentation.components.CenterAlignedTopAppBar
import ph.com.alexcr.core.presentation.components.NumPad
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.BudgetItemType
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.expenseCategories
import androidx.compose.runtime.LaunchedEffect


@Composable
fun BudgetListScreenRoot(
    modifier: Modifier = Modifier
) {
    BudgetListScreen(
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    modifier: Modifier = Modifier
) {
    var items by remember { mutableStateOf(listOf<BudgetTransaction>()) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.budget_tracker),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Item",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items yet. Tap + to add one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(items) { item ->
                    BudgetItemCard(
                        budgetTransaction = item,
                        onClick = {
                            // Handle item click if needed
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            AddTransaction(
                onCancel = { showBottomSheet = false },
                onSave = { newItem ->
                    println("Saved item: $newItem")
                    items = items + newItem
                }
            )
        }
    }
}

@Composable
fun AddTransaction(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onSave: (BudgetTransaction) -> Unit = {}
) {
    val tabs = remember { listOf("Expense", "Income", "Transfer") }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var currentBudgetTransaction by remember { mutableStateOf(BudgetTransaction()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = stringResource(Res.string.add),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigation = {
                    IconButton(
                        onClick = { onCancel() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color.White
                        )
                    }
                },
                action = {
                    IconButton(
                        onClick = { onSave(currentBudgetTransaction) }
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
        ) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            AddTransactionContent(
                                modifier = Modifier.fillMaxSize(),
                                budgetItemType = BudgetItemType.EXPENSE,
                                onBudgetItemChange = { currentBudgetTransaction = it }
                            )
                        }
                    }

                    1 -> {
                        Text("Income")
                    }

                    2 -> {
                        Text("Transfer")
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    modifier: Modifier = Modifier,
    budgetItemType: BudgetItemType = BudgetItemType.EXPENSE,
    onBudgetItemChange: (BudgetTransaction) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var isAmountFocused by remember { mutableStateOf(true) }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var notes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(expenseCategories.first()) }

    var integerPart by rememberSaveable { mutableStateOf("") }
    var decimalPart by rememberSaveable { mutableStateOf<String?>(null) }
    var isDecimalMode by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
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

    LaunchedEffect(finalAmount, selectedPaymentMethod, selectedCategory, notes, datePickerState.selectedDateMillis) {
        onBudgetItemChange(
            BudgetTransaction(
                amount = finalAmount,
                paymentMethod = selectedPaymentMethod,
                category = selectedCategory,
                note = notes,
                budgetItemType = budgetItemType,
                dateTimeCreated = datePickerState.selectedDateMillis
            )
        )
    }

    // Outer box: tapping the background dismisses the numpad
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isAmountFocused = false
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Amount display — tap to open numpad
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
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
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
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
                            count = PaymentMethod.entries.size
                        ),
                        icon = {
                    SegmentedButtonDefaults.Icon(active = selectedPaymentMethod == method) {
                                Icon(
                                    imageVector = method.icon,
                                    contentDescription = method.label,
                                    modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                )
                            }
                        }
                    ) {
                        Text(text = method.label)
                    }
                }
            }

            // Category row
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        showCategoryPicker = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
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
                    leadingIcon = selectedCategory?.let { cat ->
                        {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.name,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Category,
                            contentDescription = "Pick category"
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Date picker row — tapping dismisses numpad and opens picker
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
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.labelLarge,
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = {
                        Text(
                            text = "Select date",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Pick date"
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Notes
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
                )
            }
        }

        // NumPad — slides up from the bottom like a real keyboard
        AnimatedVisibility(
            visible = isAmountFocused,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NumPad(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface),
                onDigit = { digit ->
                    if (isDecimalMode) {
                        if ((decimalPart?.length ?: 0) < 2) {  // max 2 decimal places
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
                            // backspace past the dot — go back to integer mode
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
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            categories = expenseCategories,
            selected = selectedCategory,
            onSelect = { selectedCategory = it },
            onDismiss = { showCategoryPicker = false }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                IconButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "OK",
                    )
                }
            },
            dismissButton = {
                IconButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Cancel",
                    )
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
@Preview
fun BudgetListScreenPreview() {
    BudgetTrackerTheme {
        BudgetListScreen()
    }
}

@Composable
@Preview
fun AddTransactionPreview() {
    BudgetTrackerTheme {
        AddTransaction()
    }
}