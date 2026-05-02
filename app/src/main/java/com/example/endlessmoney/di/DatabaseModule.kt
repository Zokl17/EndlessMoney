package com.example.endlessmoney.di

import androidx.room.Room
import com.example.endlessmoney.data.local.db.AppDatabase
import com.example.endlessmoney.data.local.entity.AccountEntity
import com.example.endlessmoney.data.local.entity.CategoryEntity
import com.example.endlessmoney.data.repository.FinanceRepositoryImpl
import com.example.endlessmoney.domain.repository.FinanceRepository
import com.example.endlessmoney.domain.usecase.AddTransactionUseCase
import com.example.endlessmoney.domain.usecase.GetAccountUseCase
import com.example.endlessmoney.domain.usecase.GetRecentTransactionsUseCase
import com.example.endlessmoney.ui.screens.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.example.endlessmoney.domain.usecase.GetAllCategoriesUseCase
import com.example.endlessmoney.domain.usecase.GetAllTransactionsUseCase
import com.example.endlessmoney.ui.screens.TransactionsViewModel
import com.example.endlessmoney.data.datastore.SettingsDataStore
import com.example.endlessmoney.ui.screens.SettingsViewModel
import com.example.endlessmoney.data.remote.api.CurrencyApi
import com.example.endlessmoney.data.remote.repository.CurrencyRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.endlessmoney.domain.usecase.ClearTransactionsUseCase
import com.example.endlessmoney.domain.usecase.ClearSettingsUseCase
import com.example.endlessmoney.domain.usecase.ResetAllDataUseCase
val databaseModule = module {
    single { GetAllTransactionsUseCase(get()) }
    single {
        val db = Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "endless_money_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

        CoroutineScope(Dispatchers.IO).launch { val categoryDao = db.categoryDao()

            categoryDao.insert(
                CategoryEntity(
                    id = 1,
                    name = "Прочее",
                    icon = "💸",
                    color = 0xFF9C27B0
                )
            )

            categoryDao.insert(
                CategoryEntity(
                    id = 2,
                    name = "СБП",
                    icon = "💳",
                    color = 0xFF2196F3
                )
            )

            categoryDao.insert(
                CategoryEntity(
                    id = 3,
                    name = "ЖКХ",
                    icon = "🏠",
                    color = 0xFF4CAF50
                )
            )

            categoryDao.insert(
                CategoryEntity(
                    id = 4,
                    name = "Еда",
                    icon = "🍔",
                    color = 0xFFFF9800
                )
            )

            categoryDao.insert(
                CategoryEntity(
                    id = 5,
                    name = "Транспорт",
                    icon = "🚌",
                    color = 0xFFF44336
                )
            )

            if (db.accountDao().getAccount().first() == null) {
                db.accountDao().insert(
                    AccountEntity(
                        id = 1,
                        name = "Основной",
                        balance = 10000.0
                    )
                )
            }
        }

        db
    }
    single { SettingsDataStore(androidContext()) }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().accountDao() }
    single { ClearTransactionsUseCase(get()) }
    single { ClearSettingsUseCase(get()) }
    single { ResetAllDataUseCase(get()) }
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    single { CurrencyApi(get()) }
    single { CurrencyRepository(get(), get()) }
    single<FinanceRepository> {
        FinanceRepositoryImpl(
            accountDao = get(),
            transactionDao = get(),
            categoryDao = get(),
            settingsDataStore = get()
        )
    }
    single { GetAllCategoriesUseCase(get()) }
    single { GetAccountUseCase(get()) }
    single { GetRecentTransactionsUseCase(get()) }
    single { AddTransactionUseCase(get()) }

    viewModel {
        HomeViewModel(
            getAccountUseCase = get(),
            getRecentTransactionsUseCase = get(),
            addTransactionUseCase = get()
        )
    }
    viewModel {
        TransactionsViewModel(
            getAllTransactionsUseCase = get(),
            getAllCategoriesUseCase = get()
        )
    }
    viewModel {
        SettingsViewModel(
            settingsDataStore = get(),
            currencyRepository = get(),
            clearTransactionsUseCase = get(),
            clearSettingsUseCase = get(),
            resetAllDataUseCase = get()
        )
    }
}