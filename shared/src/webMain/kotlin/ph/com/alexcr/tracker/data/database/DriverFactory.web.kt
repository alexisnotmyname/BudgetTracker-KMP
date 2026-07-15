package ph.com.alexcr.tracker.data.database

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return createDefaultWebWorkerDriver().also { driver ->
            BudgetTrackerDatabase.Schema.create(driver)
        }
    }
}
