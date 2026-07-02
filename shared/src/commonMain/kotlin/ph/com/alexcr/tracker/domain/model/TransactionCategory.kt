package ph.com.alexcr.tracker.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionCategory(
    val name: String,
    val icon: ImageVector
)

val expenseCategories = listOf(
    TransactionCategory("Mortgage/Rent",          Icons.Filled.Home),
    TransactionCategory("Financial\nObligations",  Icons.Filled.AccountBalance),
    TransactionCategory("Food/\nGroceries",        Icons.Filled.ShoppingCart),
    TransactionCategory("Health/\nMedicine",             Icons.Filled.LocalHospital),
    TransactionCategory("Personal/\nEntertainment",Icons.Filled.SportsEsports),
    TransactionCategory("Utilities",               Icons.Filled.WbSunny),
    TransactionCategory("Church/\nBenevolence",    Icons.Filled.Church),
    TransactionCategory("Travel",        Icons.Filled.Flight),
    TransactionCategory("Insurance",               Icons.Filled.CardMembership),
    TransactionCategory("Maintenance/\nImprovements", Icons.Filled.MoreHoriz),
    TransactionCategory("Miscellaneous",           Icons.Filled.MoreHoriz),
)


