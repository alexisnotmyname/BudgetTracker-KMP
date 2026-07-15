package ph.com.alexcr.tracker.data.database

import app.cash.sqldelight.db.SqlDriver
import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase

actual class DriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BudgetTrackerDatabase.Schema,
            context = context,
            name = "budget_tracker.db",
        )
    }
}