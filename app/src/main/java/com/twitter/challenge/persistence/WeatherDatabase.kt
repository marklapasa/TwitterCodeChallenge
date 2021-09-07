package com.twitter.challenge.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.twitter.challenge.model.WeatherRecord

@Database(
    entities = [WeatherRecord::class],
    exportSchema = false,
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherRecordDAO() : WeatherRecordDAO
}
