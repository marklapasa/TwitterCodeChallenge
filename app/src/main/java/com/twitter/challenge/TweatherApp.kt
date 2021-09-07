package com.twitter.challenge

import android.app.Application
import androidx.room.Room
import com.twitter.challenge.persistence.WeatherDatabase
import com.twitter.challenge.persistence.WeatherRecordDAO
import com.twitter.challenge.repository.WeatherDataRepository
import com.twitter.challenge.ui.MainViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TweatherApp : Application() {

    init {
        // Initialize Koin dependency injection
        startKoin {
            modules(
                module {

                    fun provideDatabase(): WeatherDatabase {
                        return Room.databaseBuilder(
                            this@TweatherApp,
                            WeatherDatabase::class.java,
                            "weather-db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }

                    fun provideWeatherRecordDAO(db: WeatherDatabase): WeatherRecordDAO {
                        return db.weatherRecordDAO()
                    }

                    single { provideDatabase() }
                    single { provideWeatherRecordDAO(get()) }
                    single { WeatherDataRepository() }
                    single { MainViewModel(get()) }
                }
            )
        }
    }
}