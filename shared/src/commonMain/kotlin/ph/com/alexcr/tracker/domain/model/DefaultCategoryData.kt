package ph.com.alexcr.tracker.domain.model

import kotlin.collections.List

val defaultExpenseCategories: List<TransactionCategory> = listOf(
    TransactionCategory(name = "Mortgage/Rent"),
    TransactionCategory(name = "Financial Obligations"),
    TransactionCategory(name = "Food/Groceries"),
    TransactionCategory(name = "Health/Medicine"),
    TransactionCategory(name = "Personal/Entertainment"),
    TransactionCategory(name = "Bills/Utilities"),
    TransactionCategory(name = "Church/Benevolence"),
    TransactionCategory(name = "Travel"),
    TransactionCategory(name = "Insurance"),
    TransactionCategory(name = "Home Improvements"),
    TransactionCategory(name = "Miscellaneous"),
)

val defaultIncomeCategories: List<TransactionCategory> = listOf(
    TransactionCategory(name = "Salary"),
    TransactionCategory(name = "Investment"),
    TransactionCategory(name = "Gift"),
    TransactionCategory(name = "Other Income"),
)