package com.twitter.challenge.util

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object WeatherDataFormatter {
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
    private val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.US)
    private val dayOfMonthFormatter = SimpleDateFormat("d", Locale.US)
    private val dateFormat = SimpleDateFormat("MMM d", Locale.US)

    private val compassDirectionLabels = mutableListOf<String>(
        "North",
        "North East",
        "East",
        "South East",
        "South",
        "South West",
        "West",
        "North West"
    )

    var curUnit: Units = Units.METRIC

    fun getTempStr(temp: Double?): String {

        return if (temp == null) {
            "N/A"
        } else {
            val tempVal = if (curUnit == Units.IMPERIAL) {
                TemperatureConverter.celsiusToFahrenheit(temp.toFloat()).roundToInt().toString()
            } else {
                temp.roundToInt().toString()
            }

            tempVal + curUnit.temp

        }
    }

    fun getWindSpeed(windSpeed: Double?, degree: Int?): String? {

        return if(windSpeed == null || degree == null) {
            "N/A"
        } else {
            val wSpeed = if(curUnit == Units.IMPERIAL) {
                DistanceConverter.metersToFeet(windSpeed)
            } else {
                windSpeed
            }

            "${wSpeed.round(2)} " + curUnit.speed + " " + getCompassDirection(degree)
        }
    }

    private fun getCompassDirection(degree: Int): String {
        val angle = degree / 45
        return compassDirectionLabels[angle % compassDirectionLabels.size]
    }

    fun getTimeOfDay(dateTime: Long): String {
        return timeFormatter.format(Date(dateTime))
    }

    fun getDayOfWeek(dateTime: Long): String {
        return dayOfWeekFormatter.format(Date(dateTime)).toUpperCase(Locale.getDefault())
    }

    private fun getWeatherIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }

    fun getSunIconUrl(): String {
        return getWeatherIconUrl("01d")
    }

    fun getScatteredCloudsUrl(): String {
        return getWeatherIconUrl("03d")
    }

    fun getDayOfMonth(dateTime: Long): String {
        return dayOfMonthFormatter.format(Date(dateTime))
    }

    fun getDesc(description: String): String {
        return description[0].toUpperCase() + description.substring(1)
    }

    fun getDate(dateTime: Long): String {
        return dateFormat.format(dateTime)
    }
}

enum class Units(val label: String, val temp: String, val speed: String, val choiceLabel: String) {
    METRIC("metric", "°C", "m/s", "Metric - °C, m/s "),
    IMPERIAL("imperial", "°F", "ft/s", "Imperial - °F, ft/s")
}