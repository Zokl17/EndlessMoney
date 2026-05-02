package com.example.endlessmoney

import android.app.Application
import com.example.endlessmoney.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EndlessMoneyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EndlessMoneyApp)
            modules(databaseModule)
        }
    }
}