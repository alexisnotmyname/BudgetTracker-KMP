package ph.com.alexcr.tracker.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    viewModel: BudgetListViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TransactionModal(
        currentBudgetTransaction = state.selectedTransaction,
        expenseCategories = state.expenseCategories,
        incomeCategories = state.incomeCategories,
        onCancel = {
            viewModel.onAction(BudgetTransactionAction.OnSelectTransaction(null))
            onBack()
        },
        onSave = { newItem ->
            if (state.selectedTransaction != null) {
                viewModel.onAction(BudgetTransactionAction.OnEditTransaction(newItem))
            } else {
                viewModel.onAction(BudgetTransactionAction.OnAddTransaction(newItem))
            }
            viewModel.onAction(BudgetTransactionAction.OnSelectTransaction(null))
            onBack()
        }
    )
}

@Composable
fun TransactionModal(
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
    var editableBudgetTransaction by remember(currentBudgetTransaction) {
        mutableStateOf(currentBudgetTransaction)
    }

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
                        onClick = { editableBudgetTransaction?.let { onSave(it) } }
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
                        InputExpense(
                            modifier = Modifier.fillMaxSize(),
                            categories = expenseCategories,
                            initialTransaction = editableBudgetTransaction as? BudgetTransaction.Expense,
                            onBudgetItemChange = { editableBudgetTransaction = it },
                            onSave = { onSave(it) }
                        )
                    }

                    1 -> {
                        InputIncome(
                            modifier = Modifier.fillMaxSize(),
                            categories = incomeCategories,
                            initialTransaction = editableBudgetTransaction as? BudgetTransaction.Income,
                            onBudgetItemChange = { editableBudgetTransaction = it },
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
