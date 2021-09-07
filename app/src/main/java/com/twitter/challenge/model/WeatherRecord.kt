package com.twitter.challenge.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.twitter.challenge.util.WeatherDataFormatter


@JsonClass(generateAdapter = true)
data class Clouds(
    val cloudiness: Int? = null // 65
)

@JsonClass(generateAdapter = true)
data class Coord(
    val lat: Double? = null, // 37.77
    val lon: Double? = null // -122.42
)

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "3h")
    val rainInMM: Int? = null // 1
)

@JsonClass(generateAdapter = true)
data class Weather(
    val humidity: Int? = null, // 85
    val pressure: Int? = null, // 1007
    val temp: Double? = null // 14.77
)

@JsonClass(generateAdapter = true)
data class Wind(
    val deg: Int? = null, // 284
    val speed: Double? = null // 0.51
)


/**
 * Simplified flat representation of WeatherResponse
 */
@Entity
data class WeatherRecord(
    @PrimaryKey(autoGenerate = true)
    val id : Long? = null,
    val name: String? = null,
    val cloudiness: Int? = null,
    val lat: Double? = null, // 37.77
    val lon: Double? = null, // -122.42
    val rainInMM: Int? = null, // 1
    val humidity: Int? = null, // 85
    val pressure: Int? = null, // 1007
    val temp: Double? = null, // 14.77
    val deg: Int? = null, // 284
    val speed: Double? = null, // 0.51
    val timeStamp : Long
) {
    constructor(response: WeatherResponse, timeStamp : Long) : this(
        timeStamp = timeStamp,
        name = response.name,
        cloudiness = response.clouds?.cloudiness,
        lat = response.coord?.lat,
        lon = response.coord?.lon,
        rainInMM = response.rain?.rainInMM,
        humidity = response.weather?.humidity,
        pressure = response.weather?.pressure,
        temp = response.weather?.temp,
        deg = response.wind?.deg,
        speed = response.wind?.speed
    )

    fun getDayOfWeek() : String {
        return WeatherDataFormatter.getDayOfWeek(timeStamp)
    }

    fun getDateStr() : String {
        return WeatherDataFormatter.getDate(timeStamp)
    }

    fun getTempStr() : String {
        return WeatherDataFormatter.getTempStr(temp)
    }

    fun getWindInfoStr() : String {
        return WeatherDataFormatter.getWindSpeed(speed, deg) ?: ""
    }

    fun getThumbUrl() : String {
        cloudiness?.let {
            if(cloudiness > 50) {
                return WeatherDataFormatter.getScatteredCloudsUrl()
            }
        }

        return WeatherDataFormatter.getSunIconUrl()
    }
}