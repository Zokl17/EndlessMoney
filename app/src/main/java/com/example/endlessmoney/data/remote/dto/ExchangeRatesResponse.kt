package com.example.endlessmoney.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateItem(
    val date: String,
    val base: String,
    val quote: String,
    val rate: Double
)