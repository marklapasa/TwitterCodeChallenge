package com.twitter.challenge.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val clouds: Clouds? = null,
    val coord: Coord? = null,
    val name: String? = null, // San Francisco
    val rain: Rain? = null,
    val weather: Weather? = null,
    val wind: Wind? = null
)