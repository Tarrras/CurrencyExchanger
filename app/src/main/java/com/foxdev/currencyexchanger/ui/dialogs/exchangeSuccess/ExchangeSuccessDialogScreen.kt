package com.foxdev.currencyexchanger.ui.dialogs.exchangeSuccess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun ExchangeSuccessDialogScreen(
    info: ExchangeSuccessInfo,
    navigator: DestinationsNavigator,
) {
    Box(
        modifier = Modifier
            .background(
                color = CurrencyExchangerTheme.colors.backgroundPrimary,
                shape = MaterialTheme.shapes.medium
            )
            .padding(32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.currency_converted),
                fontWeight = FontWeight.Bold,
                color = CurrencyExchangerTheme.colors.secondaryTextColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(
                    R.string.you_have_converted_message,
                    "${info.fromAmount} ${info.fromCurrency}",
                    "${info.toAmount} ${info.toCurrency}", "${info.fee} ${info.fromCurrency}"
                ),
                color = CurrencyExchangerTheme.colors.secondaryTextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navigator.popBackStack() }) {
                Text(
                    text = stringResource(R.string.done),
                    color = CurrencyExchangerTheme.colors.primaryColor
                )
            }
        }
    }
}