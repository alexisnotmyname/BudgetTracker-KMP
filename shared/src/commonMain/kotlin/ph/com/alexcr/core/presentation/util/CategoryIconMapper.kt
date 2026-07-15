package ph.com.alexcr.core.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

fun iconForCategory(categoryName: String?): ImageVector = when (categoryName) {
    "Mortgage/Rent"            -> Icons.Filled.Home
    "Financial Obligations"    -> Icons.Filled.AccountBalance
    "Food/Groceries"           -> Icons.Filled.ShoppingCart
    "Health/Medicine"          -> Icons.Filled.LocalHospital
    "Personal/Entertainment"   -> Icons.Filled.SportsEsports
    "Bills/Utilities"                -> Icons.Filled.Bolt
    "Church/Benevolence"       -> Icons.Filled.Church
    "Travel"                   -> Icons.Filled.Flight
    "Insurance"                -> Icons.Filled.CardMembership
    "Home Improvements" -> Icons.Filled.House
    "Salary"                   -> Icons.Filled.Work
    "Freelance"                -> Icons.Filled.Work
    "Investment"               -> Icons.AutoMirrored.Filled.TrendingUp
    "Gift"                     -> Icons.Filled.CardGiftcard
    "Miscellaneous"            -> Icons.Filled.MoreHoriz
    "Other Income"             -> Icons.Filled.MoreHoriz
    else                       -> Icons.Filled.MoreHoriz
}