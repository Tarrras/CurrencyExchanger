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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components.CurrencyExchangeCard
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components.UserBalancesRow
import com.foxdev.currencyexchanger.ui.theme.CurrencyExchangerTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = getViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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