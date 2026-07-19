package ph.com.alexcr.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.tracker.presentation.BudgetListScreenRoot
import ph.com.alexcr.tracker.presentation.TransactionModalScreenRoot

@Composable
@Preview
fun App() {
    BudgetTrackerTheme {
        val backStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(AppRoute.BudgetList::class)
                        subclass(AppRoute.TransactionModal::class)
                    }
                }
            },
            AppRoute.BudgetList
        )

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(BottomSheetSceneStrategy(), SinglePaneSceneStrategy()),
            entryProvider = entryProvider {
                entry<AppRoute.BudgetList> {
                    BudgetListScreenRoot(
                        onNavigateToModal = { backStack.add(AppRoute.TransactionModal) }
                    )
                }
                entry<AppRoute.TransactionModal> {
                    TransactionModalScreenRoot(
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
