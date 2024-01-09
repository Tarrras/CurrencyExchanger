package com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.destinations.CurrencyPickerScreenDestination
import com.foxdev.currencyexchanger.ui.destinations.ErrorDialogScreenDestination
import com.foxdev.currencyexchanger.ui.destinations.ExchangeSuccessDialogScreenDestination
import com.foxdev.currencyexchanger.ui.dialogs.exchangeSuccess.ExchangeSuccessInfo
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components.CurrencyExchangeCard
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components.UserBalancesRow
import com.foxdev.currencyexchanger.ui.screens.currencyPicker.CurrencyPickerResult
import com.foxdev.currencyexchanger.ui.screens.currencyPicker.CurrencyPickerScreenInfo
import com.foxdev.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.foxdev.currencyexchanger.utils.collectAsEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.getViewModel

@Composable
@Destination(start = true)
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = getViewModel(),
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<CurrencyPickerScreenDestination, CurrencyPickerResult>
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}

            is NavResult.Value -> {
                val value = result.value
                viewModel.handleUiEvent(
                    event = CurrencyExchangeScreenUiEvent.SubmitCurrency(
                        pickerType = value.pickerType,
                        currency = value.selectedCurrency
                    )
                )
            }
        }
    }

    viewModel.uiIntent.collectAsEffect {
        when (it) {
            is CurrencyExchangeScreenUiIntent.OpenCurrencyPicker -> {
                navigator.navigate(
                    CurrencyPickerScreenDestination(
                        CurrencyPickerScreenInfo(
                            pickerType = it.pickerType,
                            alreadySelectedCurrency = it.alreadySelectedCurrency,
                            availableOption = it.availableOption
                        )
                    )
                )
            }

            is CurrencyExchangeScreenUiIntent.ShowCurrencyConvertedDialog -> {
                navigator.navigate(
                    ExchangeSuccessDialogScreenDestination(
                        ExchangeSuccessInfo(
                            fromCurrency = it.fromCurrency,
                            fromAmount = it.fromAmount,
                            toCurrency = it.toCurrency,
                            toAmount = it.toAmount,
                            fee = it.fee,
                        )
                    )
                )
            }

            is CurrencyExchangeScreenUiIntent.ShowErrorDialog -> {
                navigator.navigate(
                    direction = ErrorDialogScreenDestination(
                        message = it.message.asString(context)
                    ),
                    onlyIfResumed = true
                )
            }
        }
    }

    CurrencyExchangeScreenContent(
        uiState = uiState,
        handleEvent = viewModel::handleUiEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeScreenContent(
    uiState: CurrencyExchangeScreenUiState,
    handleEvent: (CurrencyExchangeScreenUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.currency_converter),
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(vertical = 16.dp)
                    )
                }, windowInsets = WindowInsets.displayCutout,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CurrencyExchangerTheme.colors.primaryColor,
                    titleContentColor = CurrencyExchangerTheme.colors.primaryTextColor
                )
            )
        },
        modifier = Modifier.imePadding(),
        containerColor = CurrencyExchangerTheme.colors.backgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            UserBalancesRow(modifier = Modifier, balances = uiState.balances)
            Spacer(modifier = Modifier.height(16.dp))
            CurrencyExchangeCard(modifier = Modifier.padding(16.dp), onFromInputChanged = {
                handleEvent(CurrencyExchangeScreenUiEvent.FromAmountChanged(it))
            }, onPickerClicked = {
                handleEvent(CurrencyExchangeScreenUiEvent.OpenCurrencyPicker(it))
            }, uiState = uiState)

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    handleEvent(CurrencyExchangeScreenUiEvent.Submit)
                },
                enabled = uiState.isSubmitButtonEnabled,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = CurrencyExchangerTheme.colors.primaryColor,
                    disabledContainerColor = CurrencyExchangerTheme.colors.primaryColor.copy(alpha = 0.3f),
                    contentColor = CurrencyExchangerTheme.colors.primaryTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}