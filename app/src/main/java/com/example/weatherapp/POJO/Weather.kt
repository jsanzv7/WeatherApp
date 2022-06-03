package com.example.weatherapp.POJO

import com.google.gson.annotations.SerializedName

/**
 * A `Weather` is a data class with four properties: `id`, `main`, `description`, and `icon`.
 *
 * The `@SerializedName` annotation tells Gson to use the value in the annotation as the name of the
 * property when it serializes the object to JSON.
 *
 * The `val` keyword tells Kotlin that the property is immutable.
 *
 * The `data` keyword tells Kotlin that this class is a data class.
 *
 * The `:Int`, `:String`, etc. are the types of the properties.
 *
 * The
 * @property {Int} id - Weather condition id
 * @property {String} main - The main weather condition.
 * @property {String} description - A human-readable text summary of this data point.
 * @property {String} icon - The icon representing the weather condition.
 */
data class Weather(
    @SerializedName("id") val id:Int,
    @SerializedName("main") val main:String,
    @SerializedName("description") val description:String,
    @SerializedName("icon") val icon:String

    )

