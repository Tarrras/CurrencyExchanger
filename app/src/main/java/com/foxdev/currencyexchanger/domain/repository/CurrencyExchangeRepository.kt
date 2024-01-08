package com.foxdev.currencyexchanger.domain.repository

import com.foxdev.currencyexchanger.domain.model.ExchangeRatesDTO
import com.foxdev.currencyexchanger.domain.model.toDomainModel
import com.foxdev.currencyexchanger.network.dataSource.CurrencyExchangeRemoteDataSource

class CurrencyExchangeRepositoryImpl(
    private val exchangeRatesRemoteDataSource: CurrencyExchangeRemoteDataSource
) : CurrencyExchangeRepository {
    override suspend fun fetchCurrentExchangeRates(): Result<ExchangeRatesDTO> {
        return exchangeRatesRemoteDataSource.getCurrentRates().map { it.toDomainModel() }
    }
}

interface CurrencyExchangeRepository {
    suspend fun fetchCurrentExchangeRates(): Result<ExchangeRatesDTO>
}