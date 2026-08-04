package ph.com.alexcr.tracker.presentation.transactionmodal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import budget.shared.generated.resources.Res
import budget.shared.generated.resources.add
import budget.shared.generated.resources.edit
import budget.shared.generated.resources.save
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ph.com.alexcr.core.presentation.components.Titlebar
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.presentation.components.InputExpense
import ph.com.alexcr.tracker.presentation.components.InputIncome

@Composable
fun TransactionModalScreenRoot(
    transactionId: Long?,
    viewModel: TransactionModalViewModel = koinViewModel(key = "transaction_modal_${transactionId ?: "new"}"),
    onBack: () -> Unit,
) {
    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }
    val state by viewModel.state.collectAsState()

    val isEditFlow = transactionId != null
    val showLoading = isEditFlow && state.draft == null && !state.loadFailed

    TransactionModal(
        isEditMode = isEditFlow,
        isLoading = showLoading,
        loadFailed = state.loadFailed,
        draft = state.draft,
        expenseCategories = state.expenseCategories,
        incomeCategories = state.incomeCategories,
        onDraftChanged = { viewModel.onAction(TransactionModalAction.OnDraftChanged(it)) },
        onClose = {
            viewModel.reset()
            onBack()
        },
        onSave = { item ->
            if (transactionId != null) {
                viewModel.onAction(TransactionModalAction.OnEditTransaction(item))
            } else {
                viewModel.onAction(TransactionModalAction.OnAddTransaction(item))
            }
            viewModel.reset()
            onBack()
        },
    )
}

@Composable
fun TransactionModal(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isLoading: Boolean = false,
    loadFailed: Boolean = false,
    draft: BudgetTransaction? = null,
    expenseCategories: List<TransactionCategory>,
    incomeCategories: List<TransactionCategory>,
    onDraftChanged: (BudgetTransaction) -> Unit = {},
    onClose: () -> Unit = {},
    onSave: (BudgetTransaction) -> Unit = {},
) {
    Scaffold(
        topBar = {
            Titlebar(
                text = if (isEditMode) stringResource(Res.string.edit) else stringResource(Res.string.add),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { draft?.let { onSave(it) } },
                        enabled = draft != null && !isLoading && !loadFailed,
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                loadFailed -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Transaction not found",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                isEditMode -> {
                    when (val current = draft) {
                        is BudgetTransaction.Expense -> {
                            key(current.id) {
                                InputExpense(
                                    modifier = Modifier.fillMaxSize(),
                                    categories = expenseCategories,
                                    initialTransaction = current,
                                    onBudgetItemChange = onDraftChanged,
                                    onSave = { onSave(it) },
                                )
                            }
                        }

                        is BudgetTransaction.Income -> {
                            key(current.id) {
                                InputIncome(
                                    modifier = Modifier.fillMaxSize(),
                                    categories = incomeCategories,
                                    initialTransaction = current,
                                    onBudgetItemChange = onDraftChanged,
                                    onSave = { onSave(it) },
                                )
                            }
                        }

                        null -> Unit
                    }
                }

                else -> {
                    val initialPage = remember(draft) {
                        when (draft) {
                            is BudgetTransaction.Income -> 1
                            else -> 0
                        }
                    }
                    val coroutineScope = rememberCoroutineScope()
                    val tabs = remember { listOf("Expense", "Income", "Transfer") }
                    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { tabs.size })

                    PrimaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(text = title) },
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        when (page) {
                            0 -> {
                                InputExpense(
                                    modifier = Modifier.fillMaxSize(),
                                    categories = expenseCategories,
                                    initialTransaction = draft as? BudgetTransaction.Expense,
                                    onBudgetItemChange = onDraftChanged,
                                    onSave = { onSave(it) },
                                )
                            }

                            1 -> {
                                InputIncome(
                                    modifier = Modifier.fillMaxSize(),
                                    categories = incomeCategories,
                                    initialTransaction = draft as? BudgetTransaction.Income,
                                    onBudgetItemChange = onDraftChanged,
                                    onSave = { onSave(it) },
                                )
                            }

                            2 -> Text("Transfer")
                        }
                    }
                }
            }
        }
    }
}
