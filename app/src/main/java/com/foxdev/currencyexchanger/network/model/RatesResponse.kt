package com.foxdev.currencyexchanger.network.model


data class ExchangeRatesResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)