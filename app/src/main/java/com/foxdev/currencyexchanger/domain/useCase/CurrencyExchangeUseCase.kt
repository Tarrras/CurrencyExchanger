package com.foxdev.currencyexchanger.domain.useCase

import com.foxdev.currencyexchanger.domain.model.ExchangeRatesDTO
import com.foxdev.currencyexchanger.domain.repository.CurrencyExchangeRepository
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class CurrencyExchangeUseCase(
    private val repository: CurrencyExchangeRepository,
    private val feeCalculator: CommissionCalculator
) {
    private val userBalances = mutableMapOf("EUR" to BigDecimal.valueOf(1000.0))
    private var rates: ExchangeRatesDTO? = null
    private var conversionCount = 0

    fun fetchUserBalances(): Map<String, BigDecimal> = userBalances

    suspend fun fetchCurrencyExchangeRates(): Result<ExchangeRatesDTO> {
        return withContext(Dispatchers.IO) {
            repository.fetchCurrentExchangeRates().also {
                rates = it.getOrNull()
            }
        }
    }

    fun getUserBalance(
        currency: String
    ): BigDecimal {
        return userBalances.getOrDefault(currency, BigDecimal.ZERO)
    }

    fun getCurrenciesToChoose(
        oppositeCurrency: String
    ): Map<String, BigDecimal> {
        val rateCurrencies = rates?.currencyRates?.keys?.toList() ?: emptyList()
        val allCurrencies = rates?.base?.let {
            listOf(it) + rateCurrencies
        } ?: rateCurrencies

        val filteredCurrencies = allCurrencies.filter {
            it != oppositeCurrency
        }.associateWith {
            userBalances.getOrDefault(it, BigDecimal.ZERO)
        }

        return filteredCurrencies
    }

    suspend fun doExchange(
        fromCurrency: String,
        fromValue: BigDecimal,
        toCurrency: String,
        toValue: BigDecimal,
    ): Result<Boolean> {
        return runCatching {
            val currentFromBalance = userBalances.getOrDefault(fromCurrency, BigDecimal.ZERO)
            val currentToBalance = userBalances.getOrDefault(toCurrency, BigDecimal.ZERO)
            val newFromCurrencyBalance =
                (currentFromBalance - fromValue).coerceAtLeast(BigDecimal.ZERO)

            userBalances[toCurrency] = currentToBalance + toValue
            userBalances[fromCurrency] = newFromCurrencyBalance

            conversionCount = conversionCount++
            true
        }
    }

    suspend fun getAmountToReceive(
        fromCurrency: String,
        fromValue: BigDecimal,
        toCurrency: String,
    ): Pair<BigDecimal, BigDecimal> {

        val currentRates = rates ?: return BigDecimal.ZERO to BigDecimal.ZERO

        val exchangeRateForFrom = getExchangeRate(currentRates, fromCurrency)
        val exchangeRateForTo = getExchangeRate(currentRates, toCurrency)
        val exchangeRate = exchangeRateForTo / exchangeRateForFrom

        val fee = feeCalculator.calculateCommission(fromValue, conversionCount)
        val amountAfterFee = (fromValue - fee).coerceAtLeast(BigDecimal.ZERO)
        val amountToGet = amountAfterFee * exchangeRate.toBigDecimal()

        return amountToGet to fee
    }

    private fun getExchangeRate(rates: ExchangeRatesDTO, currency: String): Double {
        return if (currency == rates.base) 1.0 else rates.currencyRates[currency]
            ?: throw IllegalArgumentException("Rate not found for currency: $currency")
    }
}