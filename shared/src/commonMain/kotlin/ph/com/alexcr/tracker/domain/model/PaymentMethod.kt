package ph.com.alexcr.tracker.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector


enum class PaymentMethod(val label: String, val icon: ImageVector) {
    CASH("Cash", Icons.Filled.Wallet),
    BANK("Bank", Icons.Filled.AccountBalance)

}