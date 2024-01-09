package com.foxdev.currencyexchanger.ui.dialogs.exchangeSuccess

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExchangeSuccessInfo(
    val fromCurrency: String,
    val fromAmount: String,
    val toCurrency: String,
    val toAmount: String,
    val fee: String
): Parcelable