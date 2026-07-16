package ph.com.alexcr.app

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.tracker.presentation.BudgetListScreen
import ph.com.alexcr.tracker.presentation.BudgetListScreenRoot

@Composable
@Preview
fun App() {
    BudgetTrackerTheme {
        BudgetListScreenRoot()
    }
}