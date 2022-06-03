package com.example.weatherapp.POJO

import com.google.gson.annotations.SerializedName

/**
 *
 * @property {Int} type - Internal parameter
 * @property {Int} id - Internal parameter
 * @property {String} country - The country code where the city is located.
 * @property {Int} sunrise - Sunrise time, unix, UTC
 * @property {Int} sunset - The time of sunset (in Unix time)
 */
data class Sys(
    @SerializedName("type") val type : Int,
    @SerializedName("id") val id : Int,
    @SerializedName("country") val country : String,
    @SerializedName("sunrise") val sunrise : Int,
    @SerializedName("sunset") val sunset : Int,
)