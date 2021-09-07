package com.twitter.challenge.repository

import com.twitter.challenge.model.WeatherRecord
import com.twitter.challenge.network.weatherService
import com.twitter.challenge.persistence.WeatherRecordDAO
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Provider for all things relating to weather data
 */
class WeatherDataRepository : KoinComponent {

    private val weatherRecordDAO: WeatherRecordDAO by inject()

    // TODO: Refactor this with Android WorkManager
    suspend fun fetchWeatherData(
        forceFetch : Boolean = false,
        onError: (Exception) -> Unit) = flow {

        if (forceFetch) {
            weatherRecordDAO.deleteAll()
        }

        val weatherRecords : List<WeatherRecord> = weatherRecordDAO.getAll()

        // If don't have any data locally, then pull it fresh remotely
        if (weatherRecords.isEmpty()) {

            val tmp = mutableListOf<WeatherRecord>()

            // current.json
            weatherService.getCurrentWeather().let { respCur ->
                if (respCur.isSuccessful) {
                    respCur.body()?.let {
                        tmp.add(WeatherRecord(it, createTimestamp(0)))

                        // future_1.json
                        weatherService.getFuture01Weather().let { respFuture01 ->
                            if (respFuture01.isSuccessful) {
                                respFuture01.body()?.let { r01 ->
                                    tmp.add(WeatherRecord(r01, createTimestamp(1)))

                                    // future_2.json
                                    weatherService.getFuture02Weather().let { respFuture02 ->
                                        if (respFuture02.isSuccessful) {
                                            respFuture02.body()?.let { r02 ->
                                                tmp.add(WeatherRecord(r02, createTimestamp(2)))

                                                // future_3.json
                                                weatherService.getFuture03Weather()
                                                    .let { respFuture03 ->
                                                        if (respFuture03.isSuccessful) {
                                                            respFuture03.body()?.let { r03 ->
                                                                tmp.add(WeatherRecord(r03, createTimestamp(3)))

                                                                // future_4.json
                                                                weatherService.getFuture04Weather()
                                                                    .let { respFuture04 ->
                                                                        if (respFuture04.isSuccessful) {
                                                                            respFuture04.body()
                                                                                ?.let { r04 ->
                                                                                    tmp.add(
                                                                                        WeatherRecord(r04, createTimestamp(4))
                                                                                    )

                                                                                    // future_5.json
                                                                                    weatherService.getFuture05Weather()
                                                                                        .let { respFuture05 ->
                                                                                            if (respFuture05.isSuccessful) {
                                                                                                respFuture05.body()
                                                                                                    ?.let { r05 ->
                                                                                                        tmp.add(
                                                                                                            WeatherRecord(
                                                                                                                r05,
                                                                                                                createTimestamp(5)
                                                                                                            )
                                                                                                        )

                                                                                                        if (tmp.isNotEmpty()) {
                                                                                                            weatherRecordDAO.insert(
                                                                                                                tmp
                                                                                                            )
                                                                                                            emit(
                                                                                                                tmp
                                                                                                            )
                                                                                                        }

                                                                                                    }
                                                                                            } else {
                                                                                                onError(
                                                                                                    getFailException()
                                                                                                )
                                                                                            }
                                                                                        }

                                                                                }
                                                                        } else {
                                                                            onError(getFailException())
                                                                        }
                                                                    }

                                                            }
                                                        } else {
                                                            onError(getFailException())
                                                        }
                                                    }

                                            }
                                        } else {
                                            onError(getFailException())
                                        }
                                    }

                                }
                            } else {
                                onError(getFailException())
                            }
                        }
                    }
                } else {
                    onError(getFailException())
                }
            }
        } else {
            // Emit what is already cached
            emit(weatherRecords)
        }

    }.flowOn(Dispatchers.IO)

    private fun createTimestamp(dayIncrement: Int): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, dayIncrement)
    }.timeInMillis


    private fun getFailException(): Exception {
        return Exception("Failed to get weather data")
    }
}