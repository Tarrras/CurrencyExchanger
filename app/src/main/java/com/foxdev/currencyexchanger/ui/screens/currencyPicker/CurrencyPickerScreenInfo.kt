package com.foxdev.currencyexchanger.ui.screens.currencyPicker

import android.os.Parcelable
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.PickerType
import kotlinx.parcelize.Parcelize


@Parcelize
data class CurrencyPickerScreenInfo(
    val pickerType: PickerType,
    val alreadySelectedCurrency: String,
    val availableOption: List<CurrencyWithBalance>
) : Parcelable

@Parcelize
data class CurrencyWithBalance(
    val balance: String,
    val currency: String
) : Parcelable

@Parcelize
data class CurrencyPickerResult(
    val pickerType: PickerType,
    val selectedCurrency: String
) : Parcelable
