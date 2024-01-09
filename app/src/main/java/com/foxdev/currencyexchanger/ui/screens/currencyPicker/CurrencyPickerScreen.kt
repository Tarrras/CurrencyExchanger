package com.foxdev.currencyexchanger.ui.screens.currencyPicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.UserBalancesRowUiItemModel
import com.foxdev.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun CurrencyPickerScreen(
    info: CurrencyPickerScreenInfo,
    resultNavigator: ResultBackNavigator<CurrencyPickerResult>
) {

    CurrencyPickerScreenContent(
        alreadySelectedCurrency = info.alreadySelectedCurrency,
        currencies = info.availableOption.map {
            UserBalancesRowUiItemModel(
                balance = it.balance,
                currency = it.currency
            )
        }, onCurrencySelected = {
            resultNavigator.navigateBack(
                onlyIfResumed = true, result = CurrencyPickerResult(
                    pickerType = info.pickerType,
                    selectedCurrency = it
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerScreenContent(
    alreadySelectedCurrency: String,
    currencies: List<UserBalancesRowUiItemModel>,
    onCurrencySelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.select_currency),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    )
                }, windowInsets = WindowInsets.statusBars,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CurrencyExchangerTheme.colors.primaryColor,
                    titleContentColor = CurrencyExchangerTheme.colors.primaryTextColor
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues
        ) {
            itemsIndexed(currencies, key = { _, item -> item.currency }) { intex, item ->
                if (intex == 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                CurrencyWithBalancesPickerUiItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    model = item,
                    onCurrencySelected = onCurrencySelected,
                    isSelected = alreadySelectedCurrency == item.currency
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CurrencyWithBalancesPickerUiItem(
    modifier: Modifier = Modifier,
    model: UserBalancesRowUiItemModel,
    onCurrencySelected: (String) -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = modifier
            .background(
                color = CurrencyExchangerTheme.colors.primaryColor.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .clip(
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (isSelected) Modifier.border(
                    color = CurrencyExchangerTheme.colors.primaryColor,
                    width = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) else Modifier
            )
            .clickable {
                onCurrencySelected(model.currency)
            }
            .padding(all = 16.dp)
    ) {
        Text(
            text = model.currency,
            modifier = Modifier.weight(1f),
            color = CurrencyExchangerTheme.colors.secondaryTextColor
        )

        Text(
            text = model.balance, color = CurrencyExchangerTheme.colors.secondaryTextColor
        )
    }
}