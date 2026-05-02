package com.example.endlessmoney.data.remote.api

import com.example.endlessmoney.data.remote.dto.ExchangeRateItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType

class CurrencyApi(
    private val client: HttpClient = httpClient
) {
    suspend fun getLatestRates(base: String): List<ExchangeRateItem> {
        return client.get("https://api.frankfurter.dev/v2/rates") {
            url {
                parameters.append("base", base)
                parameters.append("quotes", "USD,EUR")
            }
            accept(ContentType.Application.Json)
        }.body()
    }
}