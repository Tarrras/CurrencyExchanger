package com.foxdev.currencyexchanger.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class StringValue {

    data class DynamicString(val value: String) : StringValue()

    data object Empty : StringValue()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : StringValue()

    @Composable
    fun asString(): String {
        return when (this) {
            is Empty -> ""
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}