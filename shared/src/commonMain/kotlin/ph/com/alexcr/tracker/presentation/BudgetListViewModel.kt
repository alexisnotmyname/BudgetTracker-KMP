package ph.com.alexcr.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.repository.TransactionRepository

class BudgetListViewModel(
    private val transactionRepository: TransactionRepository
): ViewModel() {

    private val _state = MutableStateFlow(BudgetListState())
    val state = _state
        .onStart { getTransactionList() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun getTransactionList() {
        transactionRepository.getTransactions()
            .onEach { transactions ->
                println("Transactions: $transactions")
                _state.update {
                    it.copy(
                        budgetList = transactions
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: BudgetTransactionAction) {
        when(action) {
            is BudgetTransactionAction.OnAddTransaction -> {
                viewModelScope.launch {
                    transactionRepository.addTransaction(action.budgetTransaction)
                }
            }
            BudgetTransactionAction.OnQueryTransaction -> {
                getTransactionList()
            }

            is BudgetTransactionAction.OnDeleteTransaction -> {
                viewModelScope.launch {
                    transactionRepository.deleteTransaction(action.budgetTransaction)
                }
            }

            is BudgetTransactionAction.OnEditTransaction -> {
                viewModelScope.launch {
                    transactionRepository.updateTransaction(action.budgetTransaction)
                }
            }
        }
    }
}