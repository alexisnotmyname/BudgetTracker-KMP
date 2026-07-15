package ph.com.alexcr.di

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module
import ph.com.alexcr.tracker.data.database.DriverFactory

actual val platformModule: Module
    get() = module {
        single<SqlDriver> {
            runBlocking { DriverFactory().createDriver() }
        }
    }