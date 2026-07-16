package ph.com.alexcr.tracker.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import kotlin.time.Clock

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
                    dateTimeUpdated = transaction.dateTimeUpdated
                )
                is BudgetTransaction.Income -> query.insertBudgetTransaction(
                    amount = transaction.amount,
                    paymentMethod = "none",
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "INCOME",
                    dateTimeCreated = transaction.dateTimeCreated,
                    dateTimeUpdated = transaction.dateTimeUpdated
                )
            }
        }
    }

    override suspend fun updateTransaction(transaction: BudgetTransaction) {
        val now = Clock.System.now().toEpochMilliseconds()
        query.transaction {
            when (transaction) {
                is BudgetTransaction.Expense -> query.updateBudgetTransaction(
                    id = transaction.id,
                    amount = transaction.amount,
                    paymentMethod = transaction.paymentMethod.name,
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "EXPENSE",
                    dateTimeUpdated = now
                )
                is BudgetTransaction.Income -> query.updateBudgetTransaction(
                    id = transaction.id,
                    amount = transaction.amount,
                    paymentMethod = "none",
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "INCOME",
                    dateTimeUpdated = now
                )
            }
        }
    }

    override fun getTransactions(): Flow<List<BudgetTransaction>> {
        return query.getAllBudgetTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    val category = if (entity.category == "none") null
                                   else TransactionCategory(name = entity.category)
                    if (entity.budgetItemType == "EXPENSE") {
                        BudgetTransaction.Expense(
                            id = entity.id,
                            amount = entity.amount,
                            paymentMethod = PaymentMethod.valueOf(entity.paymentMethod),
                            category = category,
                            note = entity.note,
                            dateTimeCreated = entity.dateTimeCreated,
                            dateTimeUpdated = entity.dateTimeUpdated
                        )
                    } else {
                        BudgetTransaction.Income(
                            id = entity.id,
                            amount = entity.amount,
                            category = category,
                            note = entity.note,
                            dateTimeCreated = entity.dateTimeCreated,
                            dateTimeUpdated = entity.dateTimeUpdated
                        )
                    }
                }
            }
    }

    override fun deleteTransaction(id: Long) {
        query.deleteBudgetTransaction(id)
    }
}