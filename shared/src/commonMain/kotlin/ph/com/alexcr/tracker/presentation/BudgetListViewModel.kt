package ph.com.alexcr.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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

    }
}