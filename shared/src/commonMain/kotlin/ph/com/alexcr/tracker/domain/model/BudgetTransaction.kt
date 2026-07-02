package ph.com.alexcr.tracker.domain.model

data class BudgetTransaction(
    val id: Long = 0,
    val amount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val category: TransactionCategory = expenseCategories.first(),
    val note: String = "",
    val budgetItemType: BudgetItemType = BudgetItemType.EXPENSE,
    val dateTimeCreated: Long? = null,
    val dateTimeUpdated: Long? = null
)

enum class BudgetItemType(val label: String) {
    INCOME("Income"),
    EXPENSE("Expense")
}
