package ph.com.alexcr.tracker.data.source

import kotlinx.coroutines.flow.Flow
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.MonthlyBalance

interface LocalDbSource {
    suspend fun insertTransaction(transaction: BudgetTransaction)
    suspend fun updateTransaction(transaction: BudgetTransaction)
    fun getTransactions(): Flow<List<BudgetTransaction>>
    fun getTransactionsByMonth(startMillis: Long, endMillis: Long): Flow<List<BudgetTransaction>>
    fun deleteTransaction(id: Long)
    suspend fun getTransactionById(id: Long): BudgetTransaction?

    suspend fun getMonthlyBalance(year: Int, month: Int): MonthlyBalance?
    suspend fun getAllMonthlyBalances(): List<MonthlyBalance>
    suspend fun getMonthlyBalancesFrom(year: Int, month: Int): List<MonthlyBalance>
    suspend fun upsertMonthlyBalance(balance: MonthlyBalance)
    suspend fun getTransactionsByMonthSuspend(startMillis: Long, endMillis: Long): List<BudgetTransaction>
}