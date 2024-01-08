package com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.foxdev.currencyexchanger.R
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.CurrencyExchangeScreenUiState
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.CurrencyPickerUiState
import com.foxdev.currencyexchanger.ui.screens.currencyExchangeScreen.PickerType
import com.foxdev.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.foxdev.currencyexchanger.utils.StringValue

@Composable
fun CurrencyExchangeCard(
    modifier: Modifier = Modifier,
    onFromInputChanged: (String) -> Unit,
    onPickerClicked: (PickerType) -> Unit,
    uiState: CurrencyExchangeScreenUiState
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.currency_exchange),
            fontWeight = FontWeight.Bold,
            color = CurrencyExchangerTheme.colors.secondaryTextColor
        )
        CurrencyExchangeInputRow(
            uiState = uiState.currencyPickerFrom,
            pickerType = PickerType.From,
            onPickerClicked = onPickerClicked,
            onInputChanged = onFromInputChanged
        )
        Divider()
        CurrencyExchangeInputRow(
            uiState = uiState.currencyPickerTo,
            pickerType = PickerType.To,
            onPickerClicked = onPickerClicked,
            onInputChanged = { }
        )
    }
}

@Composable
fun CurrencyExchangeInputRow(
    uiState: CurrencyPickerUiState,
    onInputChanged: (String) -> Unit,
    onPickerClicked: (PickerType) -> Unit,
    pickerType: PickerType
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CurrencyExchangeInputField(
            modifier = Modifier.weight(1f),
            onInputChanged = onInputChanged,
            value = uiState.amount,
            isEditable = pickerType == PickerType.From,
            errorString = uiState.error,
            pickerType = pickerType
        )

        Spacer(modifier = Modifier.width(16.dp))

        PickerRow(title = uiState.currency, onClicked = { onPickerClicked(pickerType) })
    }
}

@Composable
fun PickerRow(
    modifier: Modifier = Modifier,
    title: String,
    onClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClicked() }
    ) {
        Text(
            text = title,
            color = CurrencyExchangerTheme.colors.secondaryTextColor,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
            contentDescription = "Select currency",
            tint = CurrencyExchangerTheme.colors.secondaryTextColor
        )
    }
}

@Composable
fun CurrencyExchangeInputField(
    modifier: Modifier = Modifier,
    value: String,
    onInputChanged: (String) -> Unit,
    pickerType: PickerType,
    errorString: StringValue?,
    isEditable: Boolean
) {
    val (text, icon) = remember {
        when (pickerType) {
            PickerType.From -> R.string.sell to "↑"
            PickerType.To -> R.string.receive to "↓"
        }
    }
    val isErrorVisible = remember(errorString) {
        errorString != null
    }

    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onInputChanged,
            modifier = Modifier.height(60.dp),
            leadingIcon = {
                Text(
                    text = stringResource(id = text) + " $icon",
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .border(
                            width = 2.dp,
                            color = CurrencyExchangerTheme.colors.primaryColor,
                            shape = RoundedCornerShape(8.dp)
                        ).padding(8.dp)
                )
            },
            placeholder = { Text(text = "0.0") },
            isError = isErrorVisible,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
            readOnly = isEditable.not(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        AnimatedVisibility(visible = isErrorVisible) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorString?.asString() ?: "",
                color = CurrencyExchangerTheme.colors.errorColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}