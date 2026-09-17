package ph.com.alexcr.tracker.domain.repository

import kotlinx.coroutines.flow.Flow
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.MonthlyBalance

interface TransactionRepository {
    fun getTransactions(): Flow<List<BudgetTransaction>>
    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<BudgetTransaction>>
    suspend fun addTransaction(transaction: BudgetTransaction): Result<Unit>
    suspend fun updateTransaction(transaction: BudgetTransaction)
    suspend fun deleteTransaction(transaction: BudgetTransaction)
    suspend fun getTransactionById(id: Long): BudgetTransaction?

    suspend fun getMonthlyBalance(year: Int, month: Int): MonthlyBalance?
    suspend fun upsertMonthlyBalance(balance: MonthlyBalance)
    suspend fun recalculateBalancesFrom(year: Int, month: Int)
}