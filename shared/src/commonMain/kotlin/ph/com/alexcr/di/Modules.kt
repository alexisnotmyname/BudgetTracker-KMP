package ph.com.alexcr.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ph.com.alexcr.tracker.data.repository.TransactionRepositoryImpl
import ph.com.alexcr.tracker.data.source.LocalDbSource
import ph.com.alexcr.tracker.data.source.LocalDbSourceImpl
import ph.com.alexcr.tracker.database.BudgetTrackerDatabase
import ph.com.alexcr.tracker.domain.repository.TransactionRepository
import ph.com.alexcr.tracker.presentation.BudgetListViewModel

val sharedModule = module {

    single<BudgetTrackerDatabase> { BudgetTrackerDatabase(get()) }

    singleOf(::LocalDbSourceImpl).bind<LocalDbSource>()
    singleOf(::TransactionRepositoryImpl).bind<TransactionRepository>()

    viewModelOf(::BudgetListViewModel)
}