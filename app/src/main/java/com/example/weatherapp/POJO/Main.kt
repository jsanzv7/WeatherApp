package com.example.weatherapp.POJO

import com.google.gson.annotations.SerializedName

/**
 * `Main` is a data class that has 6 properties: `temp`, `feels_like`, `temp_min`, `temp_max`,
 * `pressure`, and `humidity`.
 *
 * The `@SerializedName` annotation is used to tell the Gson library how to map the JSON keys to the
 * Kotlin properties.
 *
 * The `val` keyword is used to declare a read-only property.
 *
 * The `:` symbol is used to declare the type of the property.
 *
 * The `=` symbol is used to assign a default value
 * @property {Double} temp - Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @property {Double} feels_like - The current temperature feels like this temperature.
 * @property {Double} temp_min - The minimum temperature at the moment. This is deviation from current
 * temp that is possible for large cities and megalopolises geographically expanded (use these
 * parameter optionally).
 * @property {Double} temp_max - Maximum temperature at the moment. This is deviation from current temp
 * that is possible for large cities and megalopolises geographically expanded (use these parameter
 * optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
 * @property {Int} pressure - Atmospheric pressure (on the sea level, if there is no sea_level or
 * grnd_level data), hPa
 * @property {Int} humidity - The percentage of humidity in the air.
 */
data class Main(
    @SerializedName("temp") val temp : Double,
    @SerializedName("feels_like") val feels_like : Double,
    @SerializedName("temp_min") val temp_min : Double,
    @SerializedName("temp_max") val temp_max : Double,
    @SerializedName("pressure") val pressure : Int,
    @SerializedName("humidity") val humidity : Int
)