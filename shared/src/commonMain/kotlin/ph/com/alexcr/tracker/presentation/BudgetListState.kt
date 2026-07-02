package ph.com.alexcr.tracker.presentation

import ph.com.alexcr.tracker.domain.model.BudgetTransaction

data class BudgetListState(
    val isLoading: Boolean = false,
    val budgetList: List<BudgetTransaction> = emptyList(),
    val error: String = ""
)
