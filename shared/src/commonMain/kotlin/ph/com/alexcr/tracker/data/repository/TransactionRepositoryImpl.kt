package ph.com.alexcr.tracker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ph.com.alexcr.tracker.data.source.LocalDbSource
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val localDbSource: LocalDbSource
): TransactionRepository {
    override fun getTransactions(): Flow<List<BudgetTransaction>> {
        return localDbSource.getTransactions()
    }

    override fun getTransactionsByMonth(year: Int, month: Int): Flow<List<BudgetTransaction>> {
        val tz = TimeZone.currentSystemDefault()
        val startMillis = LocalDateTime(LocalDate(year, month, 1), LocalTime(0, 0))
            .toInstant(tz).toEpochMilliseconds()
        // compute start of next month
        val (nextYear, nextMonth) = if (month == 12) year + 1 to 1 else year to month + 1
        val endMillis = LocalDateTime(LocalDate(nextYear, nextMonth, 1), LocalTime(0, 0))
            .toInstant(tz).toEpochMilliseconds()
        return localDbSource.getTransactionsByMonth(startMillis, endMillis)
    }

    override suspend fun addTransaction(transaction: BudgetTransaction): Result<Unit> {
        return try {
            localDbSource.insertTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: BudgetTransaction) {
        localDbSource.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: BudgetTransaction) {
        localDbSource.deleteTransaction(transaction.id)
    }

    override suspend fun getTransactionById(id: Long): BudgetTransaction? {
        return localDbSource.getTransactionById(id)
    }
}