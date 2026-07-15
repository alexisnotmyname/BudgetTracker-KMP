package ph.com.alexcr.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import ph.com.alexcr.tracker.data.repository.TransactionRepositoryImpl
import ph.com.alexcr.tracker.data.source.LocalDbSource
import ph.com.alexcr.tracker.data.source.LocalDbSourceImpl
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase
import ph.com.alexcr.tracker.domain.repository.TransactionRepository
import ph.com.alexcr.tracker.presentation.BudgetListViewModel

expect val platformModule: Module

val sharedModule = module {

    single<BudgetTrackerDatabase> { BudgetTrackerDatabase(get()) }

    singleOf(::LocalDbSourceImpl).bind<LocalDbSource>()
    singleOf(::TransactionRepositoryImpl).bind<TransactionRepository>()

    viewModelOf(::BudgetListViewModel)
}


fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}