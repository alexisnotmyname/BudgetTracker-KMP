package ph.com.alexcr.tracker.domain.model

data class Expense(
    val id: Int = 0,
    val amount: Double = 0.0,
    val notes: String = "",
    val category: String = "",
    val dateTimeCreated: Long? = null,
    val dateTimeUpdated: Long? = null
)