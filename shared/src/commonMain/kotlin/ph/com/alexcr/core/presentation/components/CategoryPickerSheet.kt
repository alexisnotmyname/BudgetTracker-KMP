package ph.com.alexcr.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.com.alexcr.core.presentation.theme.BudgetTrackerTheme
import ph.com.alexcr.core.presentation.theme.primaryLight
import ph.com.alexcr.core.presentation.theme.surfaceLight
import ph.com.alexcr.core.presentation.theme.surfaceVariantLight
import ph.com.alexcr.core.presentation.util.iconForCategory
import ph.com.alexcr.tracker.domain.model.TransactionCategory
import ph.com.alexcr.tracker.domain.model.defaultExpenseCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<TransactionCategory>,
    selected: TransactionCategory?,
    onSelect: (TransactionCategory) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        CategoryPickerContent(
            categories = categories,
            selected = selected,
            onSelect = onSelect,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun CategoryPickerContent(
    categories: List<TransactionCategory>,
    selected: TransactionCategory?,
    onSelect: (TransactionCategory) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(surfaceVariantLight)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Select Category",
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(categories) { category ->
                val isSelected = category.name == selected?.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected)
                                primaryLight
                            else
                                surfaceLight
                        )
                        .clickable { onSelect(category); onDismiss() }
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = iconForCategory(category.name),
                        contentDescription = category.name,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = category.name,
                        style = typography.labelSmall,
                        textAlign = TextAlign.Center,
                        minLines = 2,
                        maxLines = 2,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CategoryPickerSheetPreview() {
    BudgetTrackerTheme {
        CategoryPickerContent(
            categories = defaultExpenseCategories,
            selected = defaultExpenseCategories.firstOrNull(),
            onSelect = {},
            onDismiss = {}
        )
    }
}