package ph.com.alexcr.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

class BottomSheetSceneStrategy : SceneStrategy<NavKey> {
    override fun SceneStrategyScope<NavKey>.calculateScene(
        entries: List<NavEntry<NavKey>>
    ): OverlayScene<NavKey>? {
        val topEntry = entries.lastOrNull() ?: return null
        if (topEntry.contentKey !is AppRoute.TransactionModal) return null

        val underlaidEntries = entries.dropLast(1)
        if (underlaidEntries.isEmpty()) return null

        return TransactionModalOverlayScene(
            topEntry = topEntry,
            underlaidEntries = underlaidEntries
        )
    }
}

private class TransactionModalOverlayScene(
    private val topEntry: NavEntry<NavKey>,
    private val underlaidEntries: List<NavEntry<NavKey>>
) : OverlayScene<NavKey> {
    override val key = topEntry.contentKey
    override val entries = listOf(topEntry)
    override val previousEntries = underlaidEntries
    override val overlaidEntries = underlaidEntries
    override val metadata: Map<String, Any> = emptyMap()
    override val content: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize()) {
            underlaidEntries.forEach { it.Content() }
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(300)) { it },
                exit = slideOutVertically(animationSpec = tween(300)) { it }
            ) {
                topEntry.Content()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransactionModalOverlayScene) return false
        return key == other.key
    }

    override fun hashCode(): Int = key.hashCode()
}
