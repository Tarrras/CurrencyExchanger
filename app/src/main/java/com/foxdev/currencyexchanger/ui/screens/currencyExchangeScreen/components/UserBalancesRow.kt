package com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.UserBalancesRowUiItemModel

@Composable
fun UserBalancesRow(
    modifier: Modifier,
    balances: List<UserBalancesRowUiItemModel>
) = Column(
    modifier
) {
    Text(
        text = stringResource(R.string.my_balances),
        modifier = Modifier.padding(horizontal = 16.dp),
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(balances, key = { it.currency }) {
            UserBalancesRowUiItem(model = it)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun UserBalancesRowUiItem(
    modifier: Modifier = Modifier,
    model: UserBalancesRowUiItemModel
) {
    Box(modifier = modifier) {
        Text(text = "${model.balance} ${model.currency}")
    }
}