package ph.com.alexcr.tracker.presentation.transactionmodal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ph.com.alexcr.tracker.domain.repository.TransactionRepository

class TransactionModalViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionModalState())
    val state = _state.asStateFlow()

    fun loadTransaction(transactionId: Long?) {
        viewModelScope.launch {
            if (transactionId == null) {
                _state.update {
                    TransactionModalState(
                        expenseCategories = it.expenseCategories,
                        incomeCategories = it.incomeCategories,
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    editingTransactionId = transactionId,
                    isLoading = true,
                    loadFailed = false,
                    draft = null,
                )
            }

            val transaction = transactionRepository.getTransactionById(transactionId)
            if (transaction == null) {
                _state.update {
                    it.copy(isLoading = false, loadFailed = true, draft = null)
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, loadFailed = false, draft = transaction)
                }
            }
        }
    }

    fun reset() {
        _state.update {
            TransactionModalState(
                expenseCategories = it.expenseCategories,
                incomeCategories = it.incomeCategories,
            )
        }
    }

    fun onAction(action: TransactionModalAction) {
        when (action) {
            is TransactionModalAction.OnDraftChanged -> {
                _state.update { it.copy(draft = action.budgetTransaction) }
            }

            is TransactionModalAction.OnAddTransaction -> {
                viewModelScope.launch {
                    transactionRepository.addTransaction(action.budgetTransaction)
                }
            }

            is TransactionModalAction.OnEditTransaction -> {
                viewModelScope.launch {
                    transactionRepository.updateTransaction(action.budgetTransaction)
                }
            }
        }
    }
}
