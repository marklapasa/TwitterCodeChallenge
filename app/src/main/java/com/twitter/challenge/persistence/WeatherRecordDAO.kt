package com.twitter.challenge.persistence

import androidx.room.Dao
import androidx.room.Query
import com.twitter.challenge.model.WeatherRecord


/**
 * Persist the Weather Record objects for offline support
 */
@Dao
abstract class WeatherRecordDAO : BaseDAO<WeatherRecord>() {
    @Query("SELECT * FROM WeatherRecord")
    abstract fun getAll(): List<WeatherRecord>

    @Query("DELETE FROM WeatherRecord")
    abstract fun deleteAll()
}