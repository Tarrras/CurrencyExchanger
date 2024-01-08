package com.foxdev.currencyexchanger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Immutable
data class CurrencyExchangeColors(
    val primaryColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val tertiaryTextColor: Color,
    val backgroundPrimary: Color,
    val errorColor: Color,
    val successColor: Color,
)

val LightExtendedColors by lazy {
    CurrencyExchangeColors(
        primaryColor = Color(0xFF4D81E9),
        primaryTextColor = Color.White,
        secondaryTextColor = Color(0xFF000000),
        tertiaryTextColor = Color(0xFF343949),
        backgroundPrimary = Color.White,
        errorColor = Color(0xFFFF645A),
        successColor = Color(0xFF49ED62),
    )
}

val DarkExtendedColors by lazy {
    CurrencyExchangeColors(
        primaryColor = Color(0xFF4D81E9),
        primaryTextColor = Color.Black,
        secondaryTextColor = Color(0xFFFFFFFF),
        tertiaryTextColor = Color(0xFF95A1C5),
        backgroundPrimary = Color.Black,
        errorColor = Color(0xFFFF645A),
        successColor = Color(0xFF49ED62),
    )
}

val LocalExtendedColors = staticCompositionLocalOf {
    DarkExtendedColors
}