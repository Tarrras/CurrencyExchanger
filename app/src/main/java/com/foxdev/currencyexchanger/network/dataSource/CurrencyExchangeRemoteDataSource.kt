package com.foxdev.currencyexchanger.network.dataSource

import com.foxdev.currencyexchanger.network.CurrencyExchangeApi
import com.foxdev.currencyexchanger.network.model.ExchangeRatesResponse

class CurrencyExchangeRemoteDataSource(
    private val api: CurrencyExchangeApi
) {
    suspend fun getCurrentRates(): Result<ExchangeRatesResponse> {
        return runCatching { api.getCurrencyExchangeRates() }
    }
}