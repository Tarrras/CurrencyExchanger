package com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxdev.currencyexchanger.domain.useCase.CurrencyExchangeUseCase
import com.foxdev.currencyexchanger.utils.StringValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

class CurrencyExchangeViewModel(
    private val useCase: CurrencyExchangeUseCase
) : ViewModel() {
    private var debounceQuoteJob: Job? = null

    private val mutableUiState = MutableStateFlow(CurrencyExchangeScreenUiState())
    val uiState = mutableUiState.asStateFlow()

    private val mutableUiIntent = MutableSharedFlow<CurrencyExchangeScreenUiIntent>()
    val uiIntent = mutableUiIntent.asSharedFlow()

    private var userBalanceOfSelectedCurrency: Pair<String, BigDecimal>? = null
    private var firstLoad: Boolean = true

    init {
        fetchUserBalances()
    }

    val fromCurrencyFlow = mutableUiState.map {
        it.currencyPickerFrom.currency
    }.distinctUntilChanged().onEach {
        userBalanceOfSelectedCurrency =
            it to useCase.getUserBalance(it)
    }.launchIn(viewModelScope)

    fun handleUiEvent(event: CurrencyExchangeScreenUiEvent) {
        when (event) {
            is CurrencyExchangeScreenUiEvent.FromAmountChanged -> {
                handleFromAmountInput(event.amount)
            }

            is CurrencyExchangeScreenUiEvent.OpenCurrencyPicker -> {
                prepareInfoForCurrencySelect(event.pickerType)
            }

            is CurrencyExchangeScreenUiEvent.SubmitCurrency -> {
                submitCurrency(event.pickerType, event.currency)
            }

            CurrencyExchangeScreenUiEvent.Submit -> {
                viewModelScope.launch {
                    val uiState = mutableUiState.value
                    val fromUiState = uiState.currencyPickerFrom
                    val toUiState = uiState.currencyPickerTo

                    useCase.doExchange(
                        fromValue = fromUiState.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                        fromCurrency = fromUiState.currency,
                        toCurrency = toUiState.currency,
                        toValue = toUiState.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    ).let { result ->
                        result.onSuccess {
                            fetchUserBalances()
                            mutableUiIntent.emit(
                                CurrencyExchangeScreenUiIntent.ShowCurrencyConvertedDialog(
                                    fromAmount = fromUiState.amount,
                                    fromCurrency = fromUiState.currency,
                                    toCurrency = toUiState.currency,
                                    toAmount = toUiState.amount,
                                    fee = uiState.feeInfo.second
                                )
                            )
                        }.onFailure { error ->
                            showErrorMessage(error)
                        }
                    }
                }
            }
        }
    }

    private fun submitCurrency(
        pickerType: PickerType,
        newCurrency: String
    ) {
        val uiState = mutableUiState.value

        val newState = when (pickerType) {
            PickerType.From -> {
                uiState.copy(
                    currencyPickerFrom = uiState.currencyPickerFrom.copy(currency = newCurrency)
                )
            }

            PickerType.To -> {
                uiState.copy(
                    currencyPickerTo = uiState.currencyPickerTo.copy(currency = newCurrency)
                )
            }
        }

        mutableUiState.value = newState
        handleFromAmountInput(newState.currencyPickerFrom.amount)
    }

    private fun prepareInfoForCurrencySelect(
        pickerType: PickerType
    ) {
        viewModelScope.launch {
            val uiState = mutableUiState.value
            val oppositeCurrency = when (pickerType) {
                PickerType.From -> uiState.currencyPickerTo.currency
                PickerType.To -> uiState.currencyPickerFrom.currency
            }
            val selectedCurrency = when (pickerType) {
                PickerType.From -> uiState.currencyPickerFrom.currency
                PickerType.To -> uiState.currencyPickerTo.currency
            }

            val currenciesToChoose = useCase.getCurrenciesToChoose(oppositeCurrency)
            mutableUiIntent.emit(
                CurrencyExchangeScreenUiIntent.OpenCurrencyPicker(
                    pickerType = pickerType,
                    alreadySelectedCurrency = selectedCurrency,
                    availableOption = currenciesToChoose
                )
            )
        }
    }

    private fun handleFromAmountInput(
        amount: String
    ) {
        debounceQuoteJob?.cancel()

        if (!amount.isBigDecimalAmountValid()) return
        val convertedAmount = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val uiState = mutableUiState.value
        val userBalance = userBalanceOfSelectedCurrency?.takeIf {
            it.first == uiState.currencyPickerFrom.currency
        }?.second ?: BigDecimal.ZERO

        val fromError = when {
            convertedAmount > userBalance -> StringValue.DynamicString("You don't have enough balance")
            else -> null
        }

        mutableUiState.update {
            it.copy(
                currencyPickerFrom = it.currencyPickerFrom.copy(
                    amount = amount,
                    error = fromError
                ),
            )
        }

        debounceQuoteJob = viewModelScope.launch {
            delay(1.seconds)
            fetchAmountToReceive()
        }
    }

    private suspend fun fetchAmountToReceive() {
        val uiState = mutableUiState.value
        val fromAmount = uiState.currencyPickerFrom.amount

        if (fromAmount.isEmpty()) {
            mutableUiState.update {
                it.copy(
                    currencyPickerFrom = it.currencyPickerFrom.copy(
                        amount = "",
                        error = null
                    ),
                    currencyPickerTo = it.currencyPickerTo.copy(
                        amount = "",
                        error = null
                    )
                )
            }
            return
        }

        val (amountToGet, fee) = useCase.getAmountToReceive(
            fromCurrency = uiState.currencyPickerFrom.currency,
            fromValue = fromAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            toCurrency = uiState.currencyPickerTo.currency
        )

        mutableUiState.update {
            it.copy(
                currencyPickerTo = it.currencyPickerTo.copy(
                    amount = amountToGet.toPlainString(),
                ),
                feeInfo = it.currencyPickerTo.currency to fee.toPlainString()
            )
        }
    }

    private fun fetchUserBalances() {
        val userBalances = useCase.fetchUserBalances()
        val sortedBalances =
            userBalances.mapValues { (_, value) ->
                value
            }.toList().sortedByDescending { (_, value) -> value }.map {
                UserBalancesRowUiItemModel(
                    balance = it.second.stripTrailingZeros().toPlainString(),
                    currency = it.first
                )
            }

        val newCurrencyPickerFromUiState = mutableUiState.updateAndGet {
            it.copy(balances = sortedBalances)
        }.currencyPickerFrom
        val selectedCurrency = newCurrencyPickerFromUiState.currency

        val newBalanceInfo = selectedCurrency to useCase.getUserBalance(selectedCurrency)
        userBalanceOfSelectedCurrency = newBalanceInfo
        handleFromAmountInput(newCurrencyPickerFromUiState.amount)
    }

    private val ratesFlow = flow<Unit> {
        while (true) {
            useCase.fetchCurrencyExchangeRates().also {
                it.onSuccess {
                    if (firstLoad) {
                        mutableUiState.update { uiState ->
                            uiState.copy(
                                currencyPickerFrom = CurrencyPickerUiState(
                                    currency = it.base
                                ),
                                currencyPickerTo = CurrencyPickerUiState(
                                    currency = it.currencyRates.keys.first()
                                ),
                                feeInfo = it.base to "0.0"
                            )
                        }
                        firstLoad = false
                    } else {
                        fetchAmountToReceive()
                    }
                }.onFailure { error ->
                    showErrorMessage(error = error)
                }
            }
            delay(5.seconds)
        }
    }.launchIn(viewModelScope)

    private suspend fun showErrorMessage(
        error: Throwable
    ) {
        mutableUiIntent.emit(
            CurrencyExchangeScreenUiIntent.ShowErrorDialog(
                message = StringValue.DynamicString(
                    error.message
                        ?: "Something went wrong! Try again later"
                )
            )
        )
    }


    fun String.isBigDecimalAmountValid(): Boolean {
        if (isEmpty()) return true

        return try {
            contains(',').not() && toBigDecimal() >= BigDecimal.ZERO
        } catch (e: NumberFormatException) {
            false
        }
    }
}