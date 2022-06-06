package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.POJO.ModelClass
import com.example.weatherapp.Utilities.ApiUtilities
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt

/* The main activity of the app. It is used to get the user's location and display the weather data */
class MainActivity : AppCompatActivity() {

    /* Declaring a variable that will be used to get the user's location. */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /* Binding the layout to the activity. */
    private lateinit var activityMainBinding: ActivityMainBinding

    /**
     * The function is called when the activity is created. It binds the layout to the activity and
     * gets the current location of the user
     *
     * @param savedInstanceState This is the bundle that contains the activity's previously saved
     * state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        /* Calling the superclass implementation of the method. */
        super.onCreate(savedInstanceState)
        /* Binding the layout to the activity. */
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        /* Getting the current location of the user. */
        /* Hiding the action bar. */
        supportActionBar?.hide()
        /* Getting the user's location. */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        /* Hiding the layout until the user's location is fetched. */
        activityMainBinding.rlMainLayout.visibility = View.GONE

        /*  */
        getCurrentLocation()

        activityMainBinding.etGetCityName.setOnEditorActionListener { v, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /* Calling the getCityWeather() function with the city name entered by the user. */
                getCityWeather(activityMainBinding.etGetCityName.text.toString())
                /* Getting the current focus of the activity. */
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        /* Getting the InputMethodManager service. */
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    /* Hiding the keyboard. */
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    /* Clearing the focus from the EditText. */
                    activityMainBinding.etGetCityName.clearFocus()
                }
                true
            } else false
        }
    }

    /**
     * The above function is used to get the weather data of a city.
     *
     * @param cityName The name of the city you want to get the weather for.
     */
    private fun getCityWeather(cityName: String) {
        /* Showing the progress bar. */
        activityMainBinding.pbLoading.visibility = View.VISIBLE
        ApiUtilities.getApiInterface()?.getCityWeatherData(cityName, API_KEY)?.enqueue(object : Callback<ModelClass>
        {
            /**
             * A function that is called when the response is received from the server.
             *
             * @param call The call object that was used to make the request.
             * @param response Response<ModelClass>
             */
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                /* Setting the data on the views. */
                setDataOnViews(response.body())
            }
            /**
             * T/* Creating a toast message that will be displayed when the user enters an invalid city
                name. */
                he function takes in a call object and a throwable object as parameters and displays a
             * toast message if the call fails
             *
             * @param call The Call object that is used to make the network request.
             * @param t Throwable - The exception that was thrown when the request failed.
             */
            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                Toast.makeText(applicationContext, "Not a valid City Name", Toast.LENGTH_SHORT)
                    .show()
                }

        })
    }

    /**
     * If the user has granted location permissions, check if location is enabled. If it is, get the
     * user's location. If it isn't, prompt the user to enable location. If the user hasn't granted
     * location permissions, prompt the user to grant location permissions
     */
    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                //final latitude and longitude code here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    /* Requesting the user's permission to access their location. */
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    /* Getting the current location of the user. */
                    val location: Location? = task.result
                    if (location == null) {
                        /* Showing a toast message. */
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    } else {
                        //fetch weather here
                        fetchCurrentLocationWeather(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }
                }

            } else {
                //setting open here
                /* Showing a toast message to the user. */
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                /* Opening the location settings. */
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            /* Requesting the user's permission to access their location. */
            requestPermission()
        }
    }

    /**
     * It fetches the current weather data from the API.
     *
     * @param latitude The latitude of the location to fetch weather for.
     * @param longitude The longitude of the location you want to get the weather for.
     */
    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {

        /* Showing the progress bar. */
        activityMainBinding.pbLoading.visibility = View.VISIBLE
        /* Calling the getCurrentWeatherData() function in the ApiInterface class. */
        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude, longitude, API_KEY)
            ?.enqueue(object :
                Callback<ModelClass> {
                /**
                 * A function that is called when the response is received from the server.
                 *
                 * @param call The call object that was used to make the request.
                 * @param response Response<ModelClass>
                 */
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                    if (response.isSuccessful) {
                        /* Setting the data on the views. */
                        setDataOnViews(response.body())
                    }
                }

                /**
                 * The function is called when the request fails
                 *
                 * @param call The Call object that is used to make the network request.
                 * @param t The response body
                 */
                override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                    /* Showing a toast message to the user. */
                    Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
            )
    }

    /**
     * The above function is used to set the data on the views.
     *
     * @param body ModelClass?
     */
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataOnViews(body: ModelClass?) {
        /* Creating a SimpleDateFormat object with the format dd/MM/yyy hh:mm */
        val sdf = SimpleDateFormat("dd/MM/yyy hh:mm")
        /* Getting the current date and time and storing it in a variable called currentDate. */
        val currentDate = sdf.format(Date())
        /* Setting the text of the TextView to the current date. */
        activityMainBinding.tvDateAndTime.text = currentDate
        /* Setting the text of the TextView to the value of the temp_max variable in the Main class of
        the OpenWeatherMap API. */
        activityMainBinding.tvDayMaxTemp.text = "Day" + kelvinToCelsius(body!!.main.temp_max) + "°"
        /* Setting the text of the TextView to the string "Night" + the value of the function
        kelvinToCelsius(body.main.temp_min) + "°" */
        activityMainBinding.tvDayMinTemp.text = "Night" + kelvinToCelsius(body.main.temp_min) + "°"
        /* Setting the text of the TextView to the temperature in Celsius. */
        activityMainBinding.tvTemp.text = "" + kelvinToCelsius(body.main.temp) + "°"
        /* Setting the text of the TextView to the string "Feels like" + the result of the
        kelvinToCelsius function + "°" */
        activityMainBinding.tvFeelsLike.text = "Feels like" + kelvinToCelsius(body.main.feels_like) + "°"
        /* Setting the text of the TextView to the weather type. */
        activityMainBinding.tvWeatherType.text = body.weather[0].main
        /* Converting the sunrise time from a long to a string. */
        activityMainBinding.tvSunrise.text = (body.sys.sunrise.toLong().toString())
        /* The above code is converting the time stamp to a local date. */
        activityMainBinding.tvSunset.text = timeStampToLocalDate(body.sys.sunset.toLong())
        /* Setting the text of the TextView to the value of the pressure in the body.main.pressure. */
        activityMainBinding.tvPressure.text = body.main.pressure.toString()
        /* Setting the text of the humidity text view to the humidity value from the API response. */
        activityMainBinding.tvHumidity.text = body.main.humidity.toString() + "%"
        /* Setting the text of the TextView to the value of the wind speed. */
        activityMainBinding.tvWindSpeed.text = body.wind.speed.toString() + "m/s"

        activityMainBinding.tvTempFahrenheit.text =
            "" + ((kelvinToCelsius(body.main.temp)).times(1.8).plus(32).roundToInt())
        activityMainBinding.etGetCityName.setText(body.name)

        updateUI(body.weather[0].id)
    }

    private fun updateUI(id: Int) {
        when (id) {
            in 200..232 -> {
                //thunderstorm
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.thunderstorm)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.thunderstorm))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.thunderstrom_bg
                )

                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.thunderstrom_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.thunderstrom)
            }
            in 300..321 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.drizzle)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.drizzle))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.drizzle_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.drizzle_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.drizzle)
                //drizzle
            }
            in 500..531 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.rain)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.rain))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.rainy_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.rainy_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.rain)
                //rain
            }
            in 600..620 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.snow)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.snow))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.snow_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.snow_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.snow)
                //snow
            }
            in 701..781 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.atmosphere)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.atmosphere))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mist_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.mist_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.mist_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.mist)
                //atmosphere
            }
            800 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.clear)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.clear))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.clear_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.clear_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.clear)
                //clear
            }
            else -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = resources.getColor(R.color.clouds)
                activityMainBinding.rlToolbar.setBackgroundColor(resources.getColor(R.color.clouds))
                activityMainBinding.rlSubLayout.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.cloud_bg
                )
                activityMainBinding.llMainBgBelow.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.cloud_bg
                )
                activityMainBinding.llMainBgAbove.background = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.cloud_bg
                )
                activityMainBinding.ivWeatherPg.setImageResource(R.drawable.cloud_bg)
                activityMainBinding.ivWeatherIcon.setImageResource(R.drawable.clouds)
                //clouds
            }
        }
        activityMainBinding.pbLoading.visibility = View.GONE
        activityMainBinding.rlMainLayout.visibility = View.VISIBLE

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timeStampToLocalDate(timeStamp: Long): String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }
        return localTime.toString()

    }

    private fun kelvinToCelsius(temp: Double): Double {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

    }

    /* Checking if the location is enabled. */
    private fun isLocationEnabled(): Boolean {
        /* Getting the location manager. */
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * It requests the user for permission to access the location.
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        /* A constant that is used to request the user's location. */
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100

        /* A constant that is used to store the API key. */
        const val API_KEY = "e6211fcc04c907de119bfc9efe5f739a"
    }

    /**
     * If the app has permission to access coarse and fine location, return true. Otherwise, return
     * false
     *
     * @return A boolean value.
     */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            /* Returning a boolean value. */
            return true
        }

        /* Returning a boolean value. */
        return false
    }

    /**
     * A function that is called when the user responds to the permission request.
     *
     * @param requestCode This is the request code you passed to requestPermissions().
     * @param permissions The array of permissions that you want to request.
     * @param grantResults This is an array of the results of each permission you asked for. In this
     * case, you only asked for one permission, so the grantResults array will only have one item in
     * it.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        /* Calling the superclass implementation of the method. */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /* Showing a toast message to the user. */
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                /* Showing a toast message to the user. */
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}