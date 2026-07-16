package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme

@Composable
fun BaseAlertDialog(
    onDismissRequest: () -> Unit,
    backgroundColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(6.dp),
    positiveButton: @Composable () -> Unit,
    title: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth(8f / 9f)
                .height(IntrinsicSize.Min)
                .background(backgroundColor, shape = shape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (title != null) {
                    title()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                content()
                Spacer(modifier = Modifier.height(8.dp))
                positiveButton()
                if (negativeButton != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    negativeButton()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBaseAlertDialog() {
    BudgetTrackerTheme {
        BaseAlertDialog(
            title = {
                Text(
                    text = "This is a customized title, color red wow",
                    textAlign = TextAlign.Center,
                    lineHeight = 1.1.em,
                    maxLines = 5,
                )
            },
            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "This content contains a small image for demo purposes",
                        lineHeight = 1.2.em,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF727F93)
                    )
                }
            },
            positiveButton = {
                GenericButton(
                    text = "Proceed",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }
                )
            },
            negativeButton = {
                GenericButton(
                    text = "Cancel",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                )
            },
            onDismissRequest = {  }
        )
    }
}