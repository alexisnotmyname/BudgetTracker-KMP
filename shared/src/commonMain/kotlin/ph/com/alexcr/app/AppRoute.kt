package ph.com.alexcr.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object BudgetList : AppRoute

    @Serializable
    data class TransactionModal(val transactionId: Long?) : AppRoute
}
