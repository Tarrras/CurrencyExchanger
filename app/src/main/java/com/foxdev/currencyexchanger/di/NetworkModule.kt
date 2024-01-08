package com.foxdev.currencyexchanger.di

import com.foxdev.currencyexchanger.BuildConfig
import com.foxdev.currencyexchanger.domain.repository.CurrencyExchangeRepository
import com.foxdev.currencyexchanger.network.CurrencyExchangeApi
import com.foxdev.currencyexchanger.network.dataSource.CurrencyExchangeRemoteDataSource
import com.foxdev.currencyexchanger.utils.CONNECTION_TIMEOUT_SEC
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        GsonBuilder().create()
    }

    single {
        GsonConverterFactory.create(get())
    } bind Converter.Factory::class

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single() {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(get<HttpLoggingInterceptor>())
                }
                connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                writeTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
            }.build()
    }

    single() {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(get())
            .build()
    }

    single {
        get<Retrofit>().create(CurrencyExchangeApi::class.java)
    }

    single { CurrencyExchangeRemoteDataSource(get()) }
}