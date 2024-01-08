package com.foxdev.currencyexchanger.di

import com.foxdev.currencyexchanger.domain.repository.CurrencyExchangeRepository
import com.foxdev.currencyexchanger.domain.repository.CurrencyExchangeRepositoryImpl
import com.foxdev.currencyexchanger.domain.useCase.CommissionCalculator
import com.foxdev.currencyexchanger.domain.useCase.CurrencyExchangeUseCase
import com.foxdev.currencyexchanger.domain.useCase.DefaultCommissionCalculator
import com.foxdev.currencyexchanger.network.dataSource.CurrencyExchangeRemoteDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {

    factory {
        DefaultCommissionCalculator()
    } bind CommissionCalculator::class

    factory {
        CurrencyExchangeRepositoryImpl(get())
    } bind CurrencyExchangeRepository::class

    single {
        CurrencyExchangeUseCase(get(), get())
    }
}