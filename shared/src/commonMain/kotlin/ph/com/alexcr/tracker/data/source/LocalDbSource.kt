package ph.com.alexcr.tracker.data.source

import kotlinx.coroutines.flow.Flow
import ph.com.alexcr.tracker.domain.model.BudgetTransaction

interface LocalDbSource {

    fun upsertExpense(transaction: BudgetTransaction)

    fun getTransactions(): Flow<List<BudgetTransaction>>
}