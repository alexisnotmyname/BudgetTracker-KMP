package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun OutlinedTextField(
    value: String,
    placeholder: @Composable () -> Unit = {},
    enabled: Boolean = true,
    singleLine: Boolean = false,
    textDecoration: TextDecoration?,
    textStyle: TextStyle,
    textColor: Color,
    colors: TextFieldColors,
    onValueChanged: (String) -> Unit = {},
    onStoppedEditing: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = {
            onValueChanged(it)
        },
        textStyle = textStyle.copy(
            color = textColor,
            textDecoration = textDecoration
        ),
        enabled = enabled,
        singleLine = singleLine,
        keyboardActions = KeyboardActions(
            onDone = {
                onStoppedEditing()
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            TextField(
                value = value,
                onValueChange = {},
                placeholder = placeholder,
                enabled = enabled,
                singleLine = singleLine,
                colors = colors,
                modifier = modifier
            )
            innerTextField()
        }
    )
}