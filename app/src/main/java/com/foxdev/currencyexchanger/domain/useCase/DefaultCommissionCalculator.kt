package com.foxdev.currencyexchanger.domain.useCase

import java.math.BigDecimal

class DefaultCommissionCalculator : CommissionCalculator {
    companion object {
        private const val FREE_CONVERSIONS = 5
        private val COMMISSION_RATE = BigDecimal.valueOf(0.007)
    }

    override fun calculateCommission(amount: BigDecimal, conversionCount: Int): BigDecimal {
        return if (conversionCount > FREE_CONVERSIONS) amount.multiply(COMMISSION_RATE) else BigDecimal.ZERO
    }
}


//create more implementations to expand the calculation of the fee
interface CommissionCalculator {
    fun calculateCommission(amount: BigDecimal, conversionCount: Int): BigDecimal
}