package ph.com.alexcr.tracker.domain.repository

import kotlinx.coroutines.flow.Flow
import ph.com.alexcr.tracker.domain.model.BudgetTransaction

interface TransactionRepository {
    suspend fun getTransactions(): Flow<List<BudgetTransaction>>
    suspend fun addTransaction(transaction: BudgetTransaction): Result<Unit>
    suspend fun updateTransaction(transaction: BudgetTransaction)
    suspend fun deleteTransaction(transaction: BudgetTransaction)
}