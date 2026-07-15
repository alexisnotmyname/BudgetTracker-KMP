package ph.com.alexcr

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ph.com.alexcr.di.initKoin

class BudgetTrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BudgetTrackerApplication)
        }
    }
}