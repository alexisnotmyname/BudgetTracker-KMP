package ph.com.alexcr.tracker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ph.com.alexcr.tracker.data.source.LocalDbSource
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.MonthlyBalance
import ph.com.alexcr.tracker.domain.repository.TransactionRepository
import kotlin.time.Clock
import kotlin.time.Instant

class TransactionRepositoryImpl(
    private val localDbSource: LocalDbSource
): TransactionRepository {
    override fun getTransactions(): Flow<List<BudgetTransaction>> {
        return localDbSource.getTransactions()
    }

    override fun getTransactionsByMonth(year: Int, month: Int): Flow<List<BudgetTransaction>> {
        val (startMillis, endMillis) = monthRangeMillis(year, month)
        return localDbSource.getTransactionsByMonth(startMillis, endMillis)
    }

    override suspend fun addTransaction(transaction: BudgetTransaction): Result<Unit> {
        return try {
            localDbSource.insertTransaction(transaction)
            val (year, month) = epochMillisToYearMonth(transaction.dateTimeCreated)
            recalculateBalancesFrom(year, month)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: BudgetTransaction) {
        localDbSource.updateTransaction(transaction)
        val (year, month) = epochMillisToYearMonth(transaction.dateTimeCreated)
        recalculateBalancesFrom(year, month)
    }

    override suspend fun deleteTransaction(transaction: BudgetTransaction) {
        val (year, month) = epochMillisToYearMonth(transaction.dateTimeCreated)
        localDbSource.deleteTransaction(transaction.id)
        recalculateBalancesFrom(year, month)
    }

    override suspend fun getTransactionById(id: Long): BudgetTransaction? {
        return localDbSource.getTransactionById(id)
    }

    override suspend fun getMonthlyBalance(year: Int, month: Int): MonthlyBalance? {
        return localDbSource.getMonthlyBalance(year, month)
    }

    override suspend fun upsertMonthlyBalance(balance: MonthlyBalance) {
        localDbSource.upsertMonthlyBalance(balance)
    }

    override suspend fun recalculateBalancesFrom(year: Int, month: Int) {
        // Get the opening balance of the affected month from an existing snapshot, or 0 if none
        val existingSnapshot = localDbSource.getMonthlyBalance(year, month)
        val openingForAffectedMonth = existingSnapshot?.openingBalance ?: run {
            // Try to derive from previous month's closing
            val (prevYear, prevMonth) = prevMonth(year, month)
            localDbSource.getMonthlyBalance(prevYear, prevMonth)?.closingBalance ?: 0.0
        }

        // Fetch all snapshots from this month forward (they may have stale data; we recompute)
        val futureSnapshots = localDbSource.getMonthlyBalancesFrom(year, month)

        // Collect distinct (year, month) pairs that need recalculation — either from existing
        // snapshots or the single affected month
        val monthsToRecalculate = if (futureSnapshots.isEmpty()) {
            listOf(year to month)
        } else {
            futureSnapshots.map { it.year to it.month }.toMutableList().also { list ->
                if (list.first() != year to month) list.add(0, year to month)
            }
        }

        var runningOpening = openingForAffectedMonth
        for ((y, m) in monthsToRecalculate) {
            val (startMillis, endMillis) = monthRangeMillis(y, m)
            val transactions = localDbSource.getTransactionsByMonthSuspend(startMillis, endMillis)
            val income = transactions.filterIsInstance<BudgetTransaction.Income>().sumOf { it.amount }
            val expense = transactions.filterIsInstance<BudgetTransaction.Expense>().sumOf { it.amount }
            val closing = runningOpening + income - expense
            localDbSource.upsertMonthlyBalance(
                MonthlyBalance(
                    year = y,
                    month = m,
                    openingBalance = runningOpening,
                    totalIncome = income,
                    totalExpense = expense,
                    closingBalance = closing
                )
            )
            runningOpening = closing
        }
    }

    private fun monthRangeMillis(year: Int, month: Int): Pair<Long, Long> {
        val tz = TimeZone.currentSystemDefault()
        val startMillis = LocalDateTime(LocalDate(year, month, 1), LocalTime(0, 0))
            .toInstant(tz).toEpochMilliseconds()
        val (nextYear, nextMonth) = if (month == 12) year + 1 to 1 else year to month + 1
        val endMillis = LocalDateTime(LocalDate(nextYear, nextMonth, 1), LocalTime(0, 0))
            .toInstant(tz).toEpochMilliseconds()
        return startMillis to endMillis
    }

    private fun prevMonth(year: Int, month: Int): Pair<Int, Int> {
        return if (month == 1) year - 1 to 12 else year to month - 1
    }

    private fun epochMillisToYearMonth(epochMillis: Long?): Pair<Int, Int> {
        if (epochMillis == null) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return now.year to now.month.number
        }
        val dt = Instant.fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return dt.year to dt.month.number
    }
}