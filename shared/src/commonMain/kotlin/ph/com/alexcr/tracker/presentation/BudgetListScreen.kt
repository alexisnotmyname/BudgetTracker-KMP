package ph.com.alexcr.tracker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budget.shared.generated.resources.Res
import budget.shared.generated.resources.add_item
import budget.shared.generated.resources.budget_tracker
import budget.shared.generated.resources.cancel
import budget.shared.generated.resources.confirm_delete
import budget.shared.generated.resources.no_transactions_yet_add_one
import budget.shared.generated.resources.yes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ph.com.alexcr.core.presentation.components.BaseAlertDialog
import ph.com.alexcr.core.presentation.components.GenericButton
import ph.com.alexcr.core.presentation.components.MonthYearPickerDialog
import ph.com.alexcr.core.presentation.components.TextButton
import ph.com.alexcr.core.presentation.components.Titlebar
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.util.formatDateHeader
import ph.com.alexcr.core.presentation.util.formatDateKey
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories
import ph.com.alexcr.tracker.domain.model.defaultIncomeCategories
import ph.com.alexcr.tracker.presentation.components.BalanceSummaryCard
import ph.com.alexcr.tracker.presentation.components.BalanceSummaryCardSkeleton
import ph.com.alexcr.tracker.presentation.components.BudgetItemCard

@Composable
fun BudgetListScreenRoot(
    onNavigateToModal: (BudgetTransaction?) -> Unit,
    viewModel: BudgetListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BudgetListScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToModal = { transaction ->
            onNavigateToModal(transaction)
        }
    )
}

@Composable
fun rememberKeyboardVisible(): Boolean {
    val imeInsets = WindowInsets.ime
    return imeInsets.getBottom(LocalDensity.current) > 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    modifier: Modifier = Modifier,
    state: BudgetListState,
    onAction: (BudgetTransactionAction) -> Unit,
    onNavigateToModal: (BudgetTransaction?) -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMonthFilterDialog by remember { mutableStateOf(false) }
    var pendingSwipeReset by remember { mutableStateOf<(() -> Unit)?>(null) }
    var transactionPendingDeletion by remember { mutableStateOf<BudgetTransaction?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            Titlebar(
                text = stringResource(Res.string.budget_tracker),
                actions = {
                    BadgedBox(
                        badge = {
                            if (state.selectedYearMonth != null) {
                                Badge()
                            }
                        }
                    ) {
                        IconButton(onClick = { showMonthFilterDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Filter by month",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToModal(null) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.add_item)
                )
            }
        }
    ) { paddingValues ->
        if (state.budgetList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
            ) {
                if (state.isLoading) {
                    BalanceSummaryCardSkeleton()
                } else {
                    BalanceSummaryCard(
                        totalIncome = state.totalIncome,
                        totalExpense = state.totalExpense,
                        remainingBalance = state.remainingBalance
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_transactions_yet_add_one),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val groupedTransactions = remember(state.budgetList) {
                state.budgetList
                    .groupBy { formatDateKey(it.dateTimeCreated) }
                    .entries
                    .sortedByDescending { it.key }
                    .map { (_, transactions) -> transactions.sortedByDescending { it.dateTimeCreated } }
            }
            println("Grouped Transactions: $groupedTransactions")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    if (state.isLoading) {
                        BalanceSummaryCardSkeleton(modifier = Modifier.padding(bottom = 8.dp))
                    } else {
                        BalanceSummaryCard(
                            totalIncome = state.totalIncome,
                            totalExpense = state.totalExpense,
                            remainingBalance = state.remainingBalance,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                groupedTransactions.forEach { transactionsForDate ->
                    val firstItem = transactionsForDate.first()
                    stickyHeader(key = "header_${formatDateKey(firstItem.dateTimeCreated)}") {
                        Text(
                            text = formatDateHeader(firstItem.dateTimeCreated),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 4.dp)
                        )
                    }
                    items(transactionsForDate, key = { it.id }) { item ->
                        val scope = rememberCoroutineScope()
                        val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                            initialValue = SwipeToDismissBoxValue.Settled,
                            positionalThreshold = { totalDistance -> totalDistance * 0.5f }
                        )
                        LaunchedEffect(swipeToDismissBoxState.currentValue) {
                            if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                transactionPendingDeletion = item
                                pendingSwipeReset = { scope.launch { swipeToDismissBoxState.reset() } }
                                showDeleteConfirmDialog = true
                            }
                        }
                        SwipeToDismissBox(
                            state = swipeToDismissBoxState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .background(
                                            color = Color.Red,
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 20.dp)
                                    )
                                }
                            }
                        ) {
                            BudgetItemCard(
                                budgetTransaction = item,
                                onClick = { onNavigateToModal(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMonthFilterDialog) {
        MonthYearPickerDialog(
            selectedYearMonth = state.selectedYearMonth,
            onYearMonthSelected = { yearMonth ->
                onAction(BudgetTransactionAction.OnFilterByMonth(yearMonth))
            },
            onDismiss = { showMonthFilterDialog = false }
        )
    }

    if (showDeleteConfirmDialog) {
        BaseAlertDialog(
            title = {
                Text(
                    text = "Confirm Delete",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.1.em,
                    maxLines = 5,
                )
            },
            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.confirm_delete),
                        lineHeight = 1.2.em,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF727F93)
                    )
                }
            },
            positiveButton = {
                GenericButton(
                    text = stringResource(Res.string.yes),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        transactionPendingDeletion?.let {
                            onAction(BudgetTransactionAction.OnDeleteTransaction(it))
                        }
                        pendingSwipeReset = null
                        transactionPendingDeletion = null
                        showDeleteConfirmDialog = false
                    }
                )
            },
            negativeButton = {
                TextButton(
                    text = stringResource(Res.string.cancel),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pendingSwipeReset?.invoke()
                        pendingSwipeReset = null
                        transactionPendingDeletion = null
                        showDeleteConfirmDialog = false
                    }
                )
            },
            onDismissRequest = {
                pendingSwipeReset?.invoke()
                pendingSwipeReset = null
                transactionPendingDeletion = null
                showDeleteConfirmDialog = false
            }
        )
    }
}

@Composable
@Preview
fun BudgetListScreenPreview() {
    BudgetTrackerTheme {
        BudgetListScreen(
            state = BudgetListState(
                isLoading = false,
                budgetList = listOf(
                    BudgetTransaction.Expense(
                        amount = 100.0,
                        category = defaultExpenseCategories.first(),
                        note = "Groceries",
                        dateTimeCreated = 0L,
                        dateTimeUpdated = 0L
                    ),
                    BudgetTransaction.Expense(
                        amount = 200.0,
                        category = defaultIncomeCategories.first(),
                        note = "Utilities",
                        dateTimeCreated = 0L,
                        dateTimeUpdated = 0L
                    )
                ),
                error = ""
            ),
            onAction = {},
            onNavigateToModal = {}
        )
    }
}
