package ph.com.alexcr.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.tracker.presentation.BudgetListScreenRoot
import ph.com.alexcr.tracker.presentation.transactionmodal.TransactionModalScreenRoot

@Composable
@Preview
fun App() {
    BudgetTrackerTheme {
//        val config = SavedStateConfiguration {
//            serializersModule = SerializersModule {
//                polymorphic(NavKey::class) {
//                    subclass(AppRoute.BudgetList::class)
//                    subclass(AppRoute.TransactionModal::class)
//                }
//            }
//        }
//        val backStack = rememberNavBackStack(config, AppRoute.BudgetList)
        val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.BudgetList) }


        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(BottomSheetSceneStrategy(), SinglePaneSceneStrategy()),
            entryProvider = entryProvider {
                entry<AppRoute.BudgetList> {
                    BudgetListScreenRoot(
                        onNavigateToModal = { transaction ->
                            backStack.add(AppRoute.TransactionModal(transaction?.id))
                        }
                    )
                }
                entry<AppRoute.TransactionModal> { route ->
                    println("Navigating to TransactionModal with transactionId: ${route.transactionId}")
                    TransactionModalScreenRoot(
                        transactionId = route.transactionId,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
