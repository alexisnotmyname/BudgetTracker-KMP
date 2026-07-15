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

class LocalDbSourceImpl(
    database: BudgetTrackerDatabase
): LocalDbSource {

    private val query = database.budgetTrackerDatabaseQueries

    override suspend fun upsertTransaction(transaction: BudgetTransaction) {
        query.transaction {
            when (transaction) {
                is BudgetTransaction.Expense -> query.insertBudgetTransaction(
                    amount = transaction.amount,
                    paymentMethod = transaction.paymentMethod.name,
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "EXPENSE",
                    dateTimeCreated = transaction.date,
                    dateTimeUpdated = null
                )
                is BudgetTransaction.Income -> query.insertBudgetTransaction(
                    amount = transaction.amount,
                    paymentMethod = "none",
                    category = transaction.category?.name ?: "none",
                    note = transaction.note,
                    budgetItemType = "INCOME",
                    dateTimeCreated = transaction.date,
                    dateTimeUpdated = null
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
                            date = entity.dateTimeCreated
                        )
                    } else {
                        BudgetTransaction.Income(
                            id = entity.id,
                            amount = entity.amount,
                            category = category,
                            note = entity.note,
                            date = entity.dateTimeCreated
                        )
                    }
                }
            }
    }

    override fun deleteTransaction(id: Long) {
        query.deleteBudgetTransaction(id)
    }
}