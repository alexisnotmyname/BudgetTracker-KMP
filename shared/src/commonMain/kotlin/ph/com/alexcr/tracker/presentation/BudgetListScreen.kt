package ph.com.alexcr.tracker.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import budget.shared.generated.resources.Res
import budget.shared.generated.resources.add
import budget.shared.generated.resources.budget_tracker
import budget.shared.generated.resources.save
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budget.shared.generated.resources.edit
import org.koin.compose.viewmodel.koinViewModel
import ph.com.alexcr.core.presentation.components.Titlebar
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories
import ph.com.alexcr.tracker.domain.model.defaultIncomeCategories
import ph.com.alexcr.tracker.presentation.components.BudgetItemCard
import ph.com.alexcr.tracker.presentation.components.InputExpense
import ph.com.alexcr.tracker.presentation.components.InputIncome

@Composable
fun BudgetListScreenRoot(
    viewModel: BudgetListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BudgetListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    modifier: Modifier = Modifier,
    state: BudgetListState,
    onAction: (BudgetTransactionAction) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var pendingSwipeReset by remember { mutableStateOf<(() -> Unit)?>(null) }
    var selectedTransaction by remember { mutableStateOf<BudgetTransaction?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Titlebar(text = stringResource(Res.string.budget_tracker))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedTransaction = null
                    showBottomSheet = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Item"
                )
            }
        }
    ) { paddingValues ->
        if (state.budgetList.isEmpty()) {
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
                items(state.budgetList, key = { it.id }) { item ->
                    val scope = rememberCoroutineScope()
                    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                        initialValue = SwipeToDismissBoxValue.Settled,
                        positionalThreshold = { totalDistance -> totalDistance * 0.3f }
                    )
                    LaunchedEffect(swipeToDismissBoxState.currentValue) {
                        if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            selectedTransaction = item
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
                                        shape = RoundedCornerShape(8.dp)
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
                            onClick = {
                                selectedTransaction = item
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
            onDismissRequest = { showBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.primary,
            dragHandle = null
        ) {
            AddTransactionModal(
                currentBudgetTransaction = selectedTransaction,
                expenseCategories = state.expenseCategories,
                incomeCategories = state.incomeCategories,
                onCancel = { showBottomSheet = false },
                onSave = { newItem ->
                    if (selectedTransaction != null) {
                        onAction(BudgetTransactionAction.OnEditTransaction(newItem))
                    } else {
                        onAction(BudgetTransactionAction.OnAddTransaction(newItem))
                    }
                    showBottomSheet = false
                }
            )
        }
    }

    if(showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                pendingSwipeReset?.invoke()
                pendingSwipeReset = null
                showDeleteConfirmDialog = false
            },
            title = {
                Text(text = "Confirm Delete")
            },
            text = {
                Text("Are you sure you want to delete transaction?")
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedTransaction?.let { onAction(BudgetTransactionAction.OnDeleteTransaction(it)) }
                    pendingSwipeReset = null  // no reset — item is being deleted
                    showDeleteConfirmDialog = false
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingSwipeReset?.invoke()  // ← snaps card back
                    pendingSwipeReset = null
                    showDeleteConfirmDialog = false
                }) { Text("No") }
            }
        )
    }
}

@Composable
fun AddTransactionModal(
    modifier: Modifier = Modifier,
    currentBudgetTransaction: BudgetTransaction? = null,
    expenseCategories: List<TransactionCategory>,
    incomeCategories: List<TransactionCategory>,
    onCancel: () -> Unit = {},
    onSave: (BudgetTransaction) -> Unit = {}
) {
    val tabs = remember { listOf("Expense", "Income", "Transfer") }
    val initialPage = remember(currentBudgetTransaction) {
        when (currentBudgetTransaction) {
            is BudgetTransaction.Income -> 1
            else -> 0
        }
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val isEditMode = currentBudgetTransaction != null
    var currentBudgetTransaction by remember { mutableStateOf(currentBudgetTransaction) }

    Scaffold(
        topBar = {
            Titlebar(
                text = if (isEditMode) stringResource(Res.string.edit) else stringResource(Res.string.add),
                navigationIcon = {
                    IconButton(
                        onClick = { onCancel() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentBudgetTransaction?.let { onSave(it) } }
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            style = MaterialTheme.typography.labelLarge
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
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
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
                            InputExpense(
                                modifier = Modifier.fillMaxSize(),
                                categories = expenseCategories,
                                initialTransaction = currentBudgetTransaction as? BudgetTransaction.Expense,
                                onBudgetItemChange = { currentBudgetTransaction = it },
                                onSave = { onSave(it) }
                            )
                        }
                    }

                    1 -> {
                        InputIncome(
                            modifier = Modifier
                                .fillMaxSize(),
                            categories = incomeCategories,
                            initialTransaction = currentBudgetTransaction as? BudgetTransaction.Income,
                            onBudgetItemChange = { currentBudgetTransaction = it },
                            onSave = { onSave(it) }
                        )
                    }

                    2 -> {
                        Text("Transfer")
                    }
                }
            }
        }

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
            onAction = {}
        )
    }
}

@Composable
@Preview
fun AddTransactionModalPreview() {
    BudgetTrackerTheme {
        AddTransactionModal(
            expenseCategories = defaultExpenseCategories,
            incomeCategories = defaultIncomeCategories,
            onCancel = {},
            onSave = {}
        )
    }
}