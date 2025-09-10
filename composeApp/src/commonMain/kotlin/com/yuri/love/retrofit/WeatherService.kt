package com.yuri.love.retrofit

import com.yuri.love.share.GlobalValue
import com.yuri.love.share.WeatherApiKey
import com.yuri.love.share.WeatherApiUrl
import com.yuri.love.share.json
import kotlinx.serialization.SerialName
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.create

@Serializable
data class WeatherResponse(
    val results: List<WeatherResult>
)

@Serializable
data class WeatherResult(
    val location: Location,
    val now: CurrentWeather,
    @SerialName("last_update")
    val lastUpdate: String
)

@Serializable
data class Location(
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timezone: String,
    @SerialName("timezone_offset")
    val timezoneOffset: String
)

@Serializable
data class CurrentWeather(
    val text: String,
    val code: String,
    val temperature: String
)

val WeatherService: WeatherApi by lazy {
    Retrofit.Builder()
        .baseUrl(WeatherApiUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create<WeatherApi>()
}

interface WeatherApi {
    @GET("weather/now.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = WeatherApiKey,
        @Query("location") location: String = "ip",  // 默认使用IP定位
        @Query("language") language: String = "zh-Hans",  // 默认中文
        @Query("unit") unit: String = "c"  // 默认摄氏度
    ): WeatherResponse
}

suspend fun initCurrentWeather() {
    val weathers = WeatherService.getCurrentWeather()
    if (weathers.results.isNotEmpty()) {
        val weather = weathers.results.first().now
        GlobalValue.weather = "${weather.text} ${weather.temperature}℃"
    }
}