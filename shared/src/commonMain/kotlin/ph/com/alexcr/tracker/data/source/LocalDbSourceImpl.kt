package ph.com.alexcr.tracker.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase
import ph.com.alexcr.tracker.database.BudgetTransactionEntity
import ph.com.alexcr.tracker.database.MonthlyBalanceEntity
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.MonthlyBalance
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import kotlin.time.Clock.System

class LocalDbSourceImpl(
    database: BudgetTrackerDatabase
): LocalDbSource {

    private val query = database.budgetTrackerDatabaseQueries

    override suspend fun insertTransaction(transaction: BudgetTransaction) {
        query.transaction {
            when (transaction) {
                is BudgetTransaction.Expense -> query.insertBudgetTransaction(
                    amount = transaction.amount,
                    paymentMethod = transaction.paymentMethod.name,
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "EXPENSE",
                    dateTimeCreated = transaction.dateTimeCreated,
                    dateTimeUpdated = transaction.dateTimeUpdated,
                    remainingBalance = transaction.remainingBalance
                )
                is BudgetTransaction.Income -> query.insertBudgetTransaction(
                    amount = transaction.amount,
                    paymentMethod = "none",
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "INCOME",
                    dateTimeCreated = transaction.dateTimeCreated,
                    dateTimeUpdated = transaction.dateTimeUpdated,
                    remainingBalance = transaction.remainingBalance
                )
            }
        }
    }

    override suspend fun updateTransaction(transaction: BudgetTransaction) {
        val now = System.now().toEpochMilliseconds()
        query.transaction {
            when (transaction) {
                is BudgetTransaction.Expense -> query.updateBudgetTransaction(
                    id = transaction.id,
                    amount = transaction.amount,
                    paymentMethod = transaction.paymentMethod.name,
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "EXPENSE",
                    dateTimeCreated = transaction.dateTimeCreated,
                    dateTimeUpdated = now,
                    remainingBalance = transaction.remainingBalance
                )
                is BudgetTransaction.Income -> query.updateBudgetTransaction(
                    id = transaction.id,
                    amount = transaction.amount,
                    paymentMethod = "none",
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "INCOME",
                    dateTimeCreated = transaction.dateTimeCreated,
                    dateTimeUpdated = now,
                    remainingBalance = transaction.remainingBalance
                )
            }
        }
    }

    override fun getTransactions(): Flow<List<BudgetTransaction>> {
        return query.getAllBudgetTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity -> entity.toDomain() }
            }
    }

    override fun getTransactionsByMonth(startMillis: Long, endMillis: Long): Flow<List<BudgetTransaction>> {
        return query.getTransactionsByMonth(startMillis, endMillis)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity -> entity.toDomain() }
            }
    }

    override fun deleteTransaction(id: Long) {
        query.deleteBudgetTransaction(id)
    }

    override suspend fun getTransactionById(id: Long): BudgetTransaction? {
        return query.selectBudgetTransactionById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override suspend fun getMonthlyBalance(year: Int, month: Int): MonthlyBalance? {
        return withContext(Dispatchers.Default) {
            query.getMonthlyBalance(year.toLong(), month.toLong())
                .executeAsOneOrNull()
                ?.toDomain()
        }
    }

    override suspend fun getAllMonthlyBalances(): List<MonthlyBalance> {
        return withContext(Dispatchers.Default) {
            query.getAllMonthlyBalances()
                .executeAsList()
                .map { it.toDomain() }
        }
    }

    override suspend fun getMonthlyBalancesFrom(year: Int, month: Int): List<MonthlyBalance> {
        return withContext(Dispatchers.Default) {
            query.getMonthlyBalancesFrom(year.toLong(), month.toLong())
                .executeAsList()
                .map { it.toDomain() }
        }
    }

    override suspend fun upsertMonthlyBalance(balance: MonthlyBalance) {
        withContext(Dispatchers.Default) {
            query.insertOrReplaceMonthlyBalance(
                year = balance.year.toLong(),
                month = balance.month.toLong(),
                openingBalance = balance.openingBalance,
                totalIncome = balance.totalIncome,
                totalExpense = balance.totalExpense,
                closingBalance = balance.closingBalance
            )
        }
    }

    override suspend fun getTransactionsByMonthSuspend(startMillis: Long, endMillis: Long): List<BudgetTransaction> {
        return withContext(Dispatchers.Default) {
            query.getTransactionsByMonth(startMillis, endMillis)
                .executeAsList()
                .map { it.toDomain() }
        }
    }
}

private fun BudgetTransactionEntity.toDomain(): BudgetTransaction {
    val category = if (category == "none") null else TransactionCategory(name = category)
    return if (budgetItemType == "EXPENSE") {
        BudgetTransaction.Expense(
            id = id,
            amount = amount,
            paymentMethod = PaymentMethod.valueOf(paymentMethod),
            category = category,
            note = note,
            dateTimeCreated = dateTimeCreated,
            dateTimeUpdated = dateTimeUpdated,
            remainingBalance = remainingBalance
        )
    } else {
        BudgetTransaction.Income(
            id = id,
            amount = amount,
            category = category,
            note = note,
            dateTimeCreated = dateTimeCreated,
            dateTimeUpdated = dateTimeUpdated,
            remainingBalance = remainingBalance
        )
    }
}

private fun MonthlyBalanceEntity.toDomain(): MonthlyBalance {
    return MonthlyBalance(
        year = year.toInt(),
        month = month.toInt(),
        openingBalance = openingBalance,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        closingBalance = closingBalance
    )
}