package com.foxdev.currencyexchanger

import android.app.Application
import com.foxdev.currencyexchanger.di.domainModule
import com.foxdev.currencyexchanger.di.networkModule
import com.foxdev.currencyexchanger.di.viewModelsModule
import org.koin.android.ext.koin.androidContext

class CurrencyExchangeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    private fun startKoin() {
        val modules = networkModule + domainModule + viewModelsModule
        org.koin.core.context.startKoin {
            androidContext(this@CurrencyExchangeApp)
            modules(
                modules = modules
            )
        }
    }
}