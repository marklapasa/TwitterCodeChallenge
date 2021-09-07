package com.twitter.challenge.ui

import androidx.lifecycle.ViewModel
import com.twitter.challenge.model.WeatherRecord
import com.twitter.challenge.repository.WeatherDataRepository
import com.twitter.challenge.util.TemperatureConverter
import com.twitter.challenge.util.Units
import com.twitter.challenge.util.WeatherDataFormatter
import com.twitter.challenge.util.round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Encapsulates the business logic of the Tweather app
 */
class MainViewModel(private val weatherDataRepo: WeatherDataRepository) : ViewModel(),
    KoinComponent {

    private var stdDevValue: Double = 0.0

    private val onError: (Exception) -> Unit = { err ->
        _uiState.value = UIState.Error(
            err.localizedMessage ?: run {
                "There was an error getting weather data"
            })
    }

    private var _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Empty)

    var uiState: StateFlow<UIState> = _uiState

    /**
     * Retrieve the data required for the view.
     */
    suspend fun refresh(isForceFetch: Boolean = false) {

        _uiState.value = UIState.Loading

        weatherDataRepo.fetchWeatherData(isForceFetch, onError).collect { list ->
            if (list.isEmpty()) {
                _uiState.value = UIState.Error("There was an error getting weather data")
            } else {
                stdDevValue = calcStdDev(list)

                _uiState.value = UIState.Refreshed(list)
            }
        }
    }

    /**
     * Calculate the standard deviation taking Imperial/Metric units into account
     */
    private fun calcStdDev(list: List<WeatherRecord>): Double {

        var stdDev = 0.0

        val temps: List<Double> = list.mapNotNull {
            if (WeatherDataFormatter.curUnit == Units.IMPERIAL && it.temp != null) {
                TemperatureConverter.celsiusToFahrenheit(it.temp.toFloat()).toDouble()
            } else {
                it.temp
            }

        }

        val mean = temps.sum() / temps.size

        temps.forEach { temp ->
            stdDev += (temp - mean).pow(2.0)
        }

        return sqrt(stdDev / temps.size)

    }

    /**
     * Return the string to be shown in the action bar subtitle
     */
    fun getStdDevStr(): String {
        return "Std Dev : ${stdDevValue.round(2)}"
    }

}

sealed class UIState {
    data class Refreshed(val list: List<WeatherRecord>) : UIState()
    data class Error(val msg: String) : UIState()
    object Loading : UIState()
    object Empty : UIState()
}