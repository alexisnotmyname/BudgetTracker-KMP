package ph.com.alexcr.tracker.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ph.com.alexcr.tracker.domain.model.BudgetTransaction
import ph.com.alexcr.tracker.domain.model.BudgetItemType
import ph.com.alexcr.tracker.domain.model.PaymentMethod
import ph.com.alexcr.tracker.domain.model.expenseCategories

@Composable
fun BudgetItemCard(
    budgetTransaction: BudgetTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = budgetTransaction.budgetItemType == BudgetItemType.INCOME
    val amountText = if (isIncome) "+₱${budgetTransaction.amount}" else "-₱${budgetTransaction.amount}"
    val amountColor = if (isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(size = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Column 1: Category + Notes
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = budgetTransaction.category.icon,
                        contentDescription = budgetTransaction.category.name,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = budgetTransaction.category.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (budgetTransaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = budgetTransaction.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Column 2: Amount + Payment Method
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleMedium,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = budgetTransaction.paymentMethod.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
@Preview
fun BudgetItemCardPreview() {
    val sampleBudgetTransaction = BudgetTransaction(
        id = 1,
        amount = 100.0,
        category = expenseCategories.first(),
        paymentMethod = PaymentMethod.CASH,
        note = "Weekly grocery run",
        budgetItemType = BudgetItemType.EXPENSE
    )
    BudgetItemCard(
        budgetTransaction = sampleBudgetTransaction,
        onClick = {}
    )
}