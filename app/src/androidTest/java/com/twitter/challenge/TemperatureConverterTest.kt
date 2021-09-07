package com.twitter.challenge

import com.twitter.challenge.util.TemperatureConverter.celsiusToFahrenheit
import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.within
import org.assertj.core.data.Offset
import org.junit.Test

class TemperatureConverterTest {

    @Test
    fun testCelsiusToFahrenheitConversion() {
        val precision: Offset<Float> = within(0.01f)
        assertThat(celsiusToFahrenheit(-50f)).isEqualTo(-58f, precision)
        assertThat(celsiusToFahrenheit(0f)).isEqualTo(32f, precision)
        assertThat(celsiusToFahrenheit(10f)).isEqualTo(50f, precision)
        assertThat(celsiusToFahrenheit(21.11f)).isEqualTo(70f, precision)
        assertThat(celsiusToFahrenheit(37.78f)).isEqualTo(100f, precision)
        assertThat(celsiusToFahrenheit(100f)).isEqualTo(212f, precision)
        assertThat(celsiusToFahrenheit(1000f)).isEqualTo(1832f, precision)
    }

}