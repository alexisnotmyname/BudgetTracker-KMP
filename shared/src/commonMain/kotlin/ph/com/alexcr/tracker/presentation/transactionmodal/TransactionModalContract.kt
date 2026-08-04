package ph.com.alexcr.tracker.presentation.transactionmodal

import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories
import ph.com.alexcr.tracker.domain.model.defaultIncomeCategories

data class TransactionModalState(
    /** Non-null when opened for edit; null for add flow. */
    val editingTransactionId: Long? = null,
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    /** Current form values; owned by the ViewModel. */
    val draft: BudgetTransaction? = null,
    val expenseCategories: List<TransactionCategory> = defaultExpenseCategories,
    val incomeCategories: List<TransactionCategory> = defaultIncomeCategories,
) {
    val isEditMode: Boolean get() = editingTransactionId != null
}

sealed interface TransactionModalAction {
    data class OnDraftChanged(val budgetTransaction: BudgetTransaction) : TransactionModalAction
    data class OnAddTransaction(val budgetTransaction: BudgetTransaction) : TransactionModalAction
    data class OnEditTransaction(val budgetTransaction: BudgetTransaction) : TransactionModalAction
}