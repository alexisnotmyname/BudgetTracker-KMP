package ph.com.alexcr.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.number
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.repository.TransactionRepository
import kotlin.time.Clock.System

class BudgetListViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val currentMonth: YearMonth
        get() {
            val now = System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return YearMonth(now.year, now.month)
        }

    private val _state = MutableStateFlow(BudgetListState(selectedYearMonth = currentMonth))
    val state = _state
        .onStart { loadTransactions(currentMonth) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var currentJob: Job? = null

    private fun loadTransactions(yearMonth: YearMonth?) {
        currentJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        val flow = if (yearMonth == null) {
            transactionRepository.getTransactions()
        } else {
            transactionRepository.getTransactionsByMonth(yearMonth.year, yearMonth.month.number)
        }
        currentJob = flow
            .onEach { transactions ->
                println("Transactions: $transactions")
                val totalIncome = transactions
                    .filterIsInstance<BudgetTransaction.Income>()
                    .sumOf { it.amount }
                val totalExpense = transactions
                    .filterIsInstance<BudgetTransaction.Expense>()
                    .sumOf { it.amount }
                _state.update {
                    it.copy(
                        isLoading = false,
                        budgetList = transactions,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        remainingBalance = totalIncome - totalExpense
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: BudgetTransactionAction) {
        when (action) {
            BudgetTransactionAction.OnQueryTransaction -> {
                loadTransactions(_state.value.selectedYearMonth)
            }

            is BudgetTransactionAction.OnDeleteTransaction -> {
                viewModelScope.launch {
                    transactionRepository.deleteTransaction(action.budgetTransaction)
                }
            }

            is BudgetTransactionAction.OnFilterByMonth -> {
                _state.update { it.copy(selectedYearMonth = action.yearMonth) }
                loadTransactions(action.yearMonth)
            }
        }
    }
}