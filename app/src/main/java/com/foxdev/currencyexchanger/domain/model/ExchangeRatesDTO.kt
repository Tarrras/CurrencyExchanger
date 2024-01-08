package com.foxdev.currencyexchanger.domain.model

import com.foxdev.currencyexchanger.network.model.ExchangeRatesResponse

data class ExchangeRatesDTO(
    val base: String,
    val currencyRates: Map<String, Double>
)

fun ExchangeRatesResponse.toDomainModel() = ExchangeRatesDTO(
    base, rates
)