package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.theme.primaryLight

@Composable
fun GenericButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = primaryLight,
        contentColor = Color.White,
        disabledContentColor = Color.White,
        disabledContainerColor = primaryLight.copy(alpha = 0.5f)
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            textAlign = TextAlign.Center,
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = primaryLight,
        disabledContentColor = Color.White,
        disabledContainerColor = primaryLight.copy(alpha = 0.5f)
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
@Preview(showBackground = true)
fun GenericButtonPreview() {
    BudgetTrackerTheme {
        GenericButton(text = "Preview Button")
    }
}