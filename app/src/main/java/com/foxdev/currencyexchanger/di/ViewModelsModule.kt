package com.foxdev.currencyexchanger.di

import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.CurrencyExchangeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { CurrencyExchangeViewModel(get()) }
}