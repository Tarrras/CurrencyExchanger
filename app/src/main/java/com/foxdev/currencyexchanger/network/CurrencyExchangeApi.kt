package com.foxdev.currencyexchanger.network

import com.foxdev.currencyexchanger.network.model.ExchangeRatesResponse
import retrofit2.http.GET

interface CurrencyExchangeApi {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getCurrencyExchangeRates(): ExchangeRatesResponse
}