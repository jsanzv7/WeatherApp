package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.databinding.DataBindingUtil
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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
        /* Calling the superclass's implementation of the method. */
        super.onCreate(savedInstanceState)
        /* Binding the layout to the activity. */
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        /* Getting the current location of the user. */
        /* Hiding the action bar. */
        supportActionBar?.hide()
        /* Getting the user's location. */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        /* Hiding the layout until the user's location is fetched. */
        activityMainBinding.rlMainLayout.visibility = View.GONE

        getCurrentLocation();
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
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                    } else {
                        //fetch weather here
                        fetchCurrentLocationWeather(location.latitude.toString(),location.longitude.toString())
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

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {



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
        /* Calling the superclass's implementation of the method. */
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