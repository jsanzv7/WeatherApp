package com.example.weatherapp.Utilities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    /* A nullable variable. */
    private var retrofit:Retrofit?=null
    var BASE_URL="https://api.openweathermap.org/data/2.5/";
    /**
     * It creates an instance of Retrofit and returns an instance of ApiInterface.
     *
     * @return The return type is a singleton instance of the Retrofit object.
     */
    fun getApiInterface():ApiInterface?{
        if(retrofit==null)
        {
            /* Creating an instance of Retrofit. */
            retrofit=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofit!!.create(ApiInterface::class.java)
    }
}