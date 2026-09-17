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
import ph.com.alexcr.tracker.domain.model.MonthlyBalance
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
        println("Loading transactions for: $yearMonth")
        currentJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        val flow = if (yearMonth == null) {
            transactionRepository.getTransactions()
        } else {
            transactionRepository.getTransactionsByMonth(yearMonth.year, yearMonth.month.number)
        }
        currentJob = flow
            .onEach { transactions ->
                val totalIncome = transactions
                    .filterIsInstance<BudgetTransaction.Income>()
                    .sumOf { it.amount }
                val totalExpense = transactions
                    .filterIsInstance<BudgetTransaction.Expense>()
                    .sumOf { it.amount }

                if (yearMonth != null) {
                    val snapshot = transactionRepository.getMonthlyBalance(yearMonth.year, yearMonth.month.number)
                    if (snapshot != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                budgetList = transactions,
                                totalIncome = totalIncome,
                                totalExpense = totalExpense,
                                openingBalance = snapshot.openingBalance,
                                remainingBalance = snapshot.closingBalance,
                                showInitialBalanceDialog = false
                            )
                        }
                    } else {
                        // No snapshot — prompt user only if this is the earliest/first-ever month
                        val hasTransactions = transactions.isNotEmpty()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                budgetList = transactions,
                                totalIncome = totalIncome,
                                totalExpense = totalExpense,
                                openingBalance = 0.0,
                                remainingBalance = totalIncome - totalExpense,
                                showInitialBalanceDialog = hasTransactions
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            budgetList = transactions,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            openingBalance = 0.0,
                            remainingBalance = totalIncome - totalExpense
                        )
                    }
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

            is BudgetTransactionAction.OnSetInitialOpeningBalance -> {
                val yearMonth = _state.value.selectedYearMonth ?: return
                viewModelScope.launch {
                    val income = _state.value.totalIncome
                    val expense = _state.value.totalExpense
                    val closing = action.amount + income - expense
                    transactionRepository.upsertMonthlyBalance(
                        MonthlyBalance(
                            year = yearMonth.year,
                            month = yearMonth.month.number,
                            openingBalance = action.amount,
                            totalIncome = income,
                            totalExpense = expense,
                            closingBalance = closing
                        )
                    )
                    // Cascade recalculate all subsequent months
                    val (nextYear, nextMonth) = if (yearMonth.month.number == 12)
                        yearMonth.year + 1 to 1
                    else
                        yearMonth.year to yearMonth.month.number + 1
                    transactionRepository.recalculateBalancesFrom(nextYear, nextMonth)
                    _state.update {
                        it.copy(
                            openingBalance = action.amount,
                            remainingBalance = closing,
                            showInitialBalanceDialog = false
                        )
                    }
                }
            }

            BudgetTransactionAction.OnDismissInitialBalanceDialog -> {
                _state.update { it.copy(showInitialBalanceDialog = false) }
            }
        }
    }
}