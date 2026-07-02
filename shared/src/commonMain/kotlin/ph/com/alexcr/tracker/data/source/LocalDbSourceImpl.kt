package ph.com.alexcr.tracker.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase
import ph.com.alexcr.tracker.domain.model.BudgetItemType
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.expenseCategories

class LocalDbSourceImpl(
    database: BudgetTrackerDatabase
): LocalDbSource {

    private val query = database.budgetTrackerDatabaseQueries

    override fun upsertExpense(transaction: BudgetTransaction) {

        query.transaction{
            query.insertBudgetTransaction(
                amount = transaction.amount,
                paymentMethod = transaction.paymentMethod.name,
                category = transaction.category.name,
                note = transaction.note,
                budgetItemType = transaction.budgetItemType.label,
                dateTimeCreated = transaction.dateTimeCreated,
                dateTimeUpdated = transaction.dateTimeUpdated
            )
        }}

    override fun getTransactions(): Flow<List<BudgetTransaction>> {
        return query.getAllBudgetTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map {
                it.map { transactionEntity ->
                    BudgetTransaction(
                        id = transactionEntity.id,
                        amount = transactionEntity.amount,
                        paymentMethod = PaymentMethod.valueOf(transactionEntity.paymentMethod),
                        category = expenseCategories.firstOrNull { category -> category.name == transactionEntity.category } ?: expenseCategories.first(),
                        note = transactionEntity.note,
                        budgetItemType = BudgetItemType.valueOf(transactionEntity.budgetItemType),
                        dateTimeCreated = transactionEntity.dateTimeCreated,
                        dateTimeUpdated = transactionEntity.dateTimeUpdated
                    )
                }

            }
    }


}