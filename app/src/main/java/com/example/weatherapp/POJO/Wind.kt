package com.example.weatherapp.POJO

import com.google.gson.annotations.SerializedName

/**
 *
 * @property {Double} speed - Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial:
 * miles/hour.
 * @property {Int} deg - Wind direction, degrees (meteorological)
 */
data class Wind (
    @SerializedName("speed") val speed : Double,
    @SerializedName("deg") val deg : Int
        )