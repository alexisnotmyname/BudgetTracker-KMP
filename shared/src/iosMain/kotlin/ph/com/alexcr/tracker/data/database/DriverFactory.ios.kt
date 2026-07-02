package ph.com.alexcr.tracker.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = BudgetTrackerDatabase.Schema,
            name = "budget_tracker.db"
        )
    }
}