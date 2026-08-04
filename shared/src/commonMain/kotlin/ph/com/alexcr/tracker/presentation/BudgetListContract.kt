package ph.com.alexcr.tracker.presentation

import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories
import ph.com.alexcr.tracker.domain.model.defaultIncomeCategories

data class BudgetListState(
    val isLoading: Boolean = false,
    val budgetList: List<BudgetTransaction> = emptyList(),
    val expenseCategories: List<TransactionCategory> = defaultExpenseCategories,
    val incomeCategories: List<TransactionCategory> = defaultIncomeCategories,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val error: String = ""
)

sealed interface BudgetTransactionAction {
    data object OnQueryTransaction: BudgetTransactionAction
    data class OnDeleteTransaction(val budgetTransaction: BudgetTransaction): BudgetTransactionAction
}