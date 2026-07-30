package ph.com.alexcr.tracker.domain.model

sealed class BudgetTransaction {
    abstract val id: Long
    abstract val amount: Double
    abstract val category: TransactionCategory?
    abstract val note: String
    abstract val dateTimeCreated: Long?
    abstract val dateTimeUpdated: Long?

    data class Expense(
        override val id: Long = 0,
        override val amount: Double = 0.0,
        override val category: TransactionCategory? = null,
        override val note: String = "",
        override val dateTimeCreated: Long? = null,
        override val dateTimeUpdated: Long? = null,
        val paymentMethod: PaymentMethod = PaymentMethod.CASH,
        val remainingBalance: Double? = null
    ) : BudgetTransaction()

    data class Income(
        override val id: Long = 0,
        override val amount: Double = 0.0,
        override val category: TransactionCategory? = null,
        override val note: String = "",
        override val dateTimeCreated: Long? = null,
        override val dateTimeUpdated: Long? = null,
        val remainingBalance: Double? = null
    ) : BudgetTransaction()
}
