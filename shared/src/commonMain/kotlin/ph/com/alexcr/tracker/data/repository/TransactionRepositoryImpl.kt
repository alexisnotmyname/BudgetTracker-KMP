package ph.com.alexcr.tracker.data.repository

import kotlinx.coroutines.flow.Flow
import ph.com.alexcr.tracker.data.source.LocalDbSource
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val localDbSource: LocalDbSource
): TransactionRepository {
    override suspend fun getTransactions(): Flow<List<BudgetTransaction>> {
        return localDbSource.getTransactions()
    }

    override suspend fun addTransaction(transaction: BudgetTransaction): Result<Unit> {
        return try {
            localDbSource.upsertExpense(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: BudgetTransaction) {

    }

    override suspend fun deleteTransaction(transaction: BudgetTransaction) {

    }
}