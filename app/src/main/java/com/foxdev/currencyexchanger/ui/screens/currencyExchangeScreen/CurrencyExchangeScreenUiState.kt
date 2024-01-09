package com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen

import androidx.compose.runtime.Immutable
import com.foxdev.currencyexchanger.ui.screens.currencyPicker.CurrencyWithBalance
import com.foxdev.currencyexchanger.utils.StringValue
import java.math.BigDecimal

@Immutable
data class CurrencyExchangeScreenUiState(
    val balances: List<UserBalancesRowUiItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val currencyPickerFrom: CurrencyPickerUiState = CurrencyPickerUiState(),
    val currencyPickerTo: CurrencyPickerUiState = CurrencyPickerUiState(),
    val feeInfo: Pair<String, String> = "" to "0.0"
) {
    val isSubmitButtonEnabled =
        isLoading.not() && currencyPickerFrom.isValid && currencyPickerTo.isValid
}

@Immutable
data class UserBalancesRowUiItemModel(
    val balance: String,
    val currency: String
)

@Immutable
data class CurrencyPickerUiState(
    val amount: String = "",
    val currency: String = "",
    val error: StringValue? = null,
) {
    private fun isAmountNonZero(): Boolean {
        val amountDecimal = amount.toBigDecimalOrNull()
        return amountDecimal != null && amountDecimal > BigDecimal.ZERO
    }

    val isValid = isAmountNonZero() && error == null
}

sealed interface CurrencyExchangeScreenUiIntent {
    data class ShowCurrencyConvertedDialog(
        val fromCurrency: String,
        val fromAmount: String,
        val toCurrency: String,
        val toAmount: String,
        val fee: String
    ) : CurrencyExchangeScreenUiIntent

    data class ShowErrorDialog(
        val message: StringValue
    ) : CurrencyExchangeScreenUiIntent

    data class OpenCurrencyPicker(
        val pickerType: PickerType,
        val alreadySelectedCurrency: String,
        val availableOption: List<CurrencyWithBalance>
    ) : CurrencyExchangeScreenUiIntent
}

sealed interface CurrencyExchangeScreenUiEvent {
    data class FromAmountChanged(val amount: String) : CurrencyExchangeScreenUiEvent
    data class OpenCurrencyPicker(val pickerType: PickerType) : CurrencyExchangeScreenUiEvent
    data class SubmitCurrency(val pickerType: PickerType, val currency: String) :
        CurrencyExchangeScreenUiEvent

    data object Submit : CurrencyExchangeScreenUiEvent
}

enum class PickerType {
    From,
    To
}