package com.example.weatherapp.POJO

import com.google.gson.annotations.SerializedName

/**
 * A data class is a class that holds data.
 * @property {List<Weather>} weather - This is an array of weather objects.
 * @property {Main} main - This is the main weather data.
 * @property {Wind} wind - This contains the wind speed.
 * @property {Sys} sys - This is a nested object that contains country code, sunrise time, and sunset
 * time.
 * @property {Int} id - City ID
 * @property {String} name - The name of the city.
 */
data class ModelClass(
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main") val main:Main,
    @SerializedName("wind") val wind:Wind,
    @SerializedName("sys") val sys:Sys,
    @SerializedName("id") val id:Int,
    @SerializedName("name") val name:String
)
