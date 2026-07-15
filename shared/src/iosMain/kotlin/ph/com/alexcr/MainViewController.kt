package ph.com.alexcr

import androidx.compose.ui.window.ComposeUIViewController
import ph.com.alexcr.app.App
import ph.com.alexcr.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }