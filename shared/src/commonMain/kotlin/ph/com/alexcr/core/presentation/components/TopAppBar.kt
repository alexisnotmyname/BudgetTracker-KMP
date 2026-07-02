package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    navigation: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = { navigation() },
        actions = { action() },
        colors = colors,
        modifier = modifier
    )
}

@Composable
fun GenericTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navigation: @Composable () -> Unit = {},
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigation()
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        action()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CenterAlignedTopAppBarPreview() {
    BudgetTrackerTheme {
        CenterAlignedTopAppBar(
            title = "Untitled",
            navigation = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Navigation icon",
                    )
                }
            },
            action = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Action icon",
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenericTopAppBarPreview() {
    BudgetTrackerTheme {
        GenericTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = "Preview",
            navigation = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            action = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            },
        )
    }
}

