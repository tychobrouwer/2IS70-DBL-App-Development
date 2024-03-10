package com.example.weclean.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.weclean.R
import com.example.weclean.ui.add.Add
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.abs

class MapView : Fragment() {
    private lateinit var mapFragment: SupportMapFragment

    private val interval: Long = 10000 // 10seconds
    private val fastestInterval: Long = 5000 // 5 seconds
    private lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private val requestPermissionCode = 999
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add littering button to navigate to the add littering activity
        val addTrashButton = view.findViewById<Button>(R.id.add_trash_button)
        addTrashButton.setOnClickListener {
            startActivity(Intent(activity as AppCompatActivity, Add::class.java))
        }

        checkForPermission(activity as AppCompatActivity)
        startLocationUpdates()

        // Fragment containing the google maps support fragment
        mapFragment = getChildFragmentManager().findFragmentById(R.id.map_fragment) as SupportMapFragment
        // Wait till map is loaded
        mapFragment.getMapAsync { googleMap ->
            // Remove indoor styling from map
            googleMap.setIndoorEnabled(false)
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Set styling from the mapstyle.json raw resource
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(activity as AppCompatActivity, R.raw.mapstyle))

            if (ActivityCompat.checkSelfPermission(
                    activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity as AppCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            if (locationResult.lastLocation == null) {
                return
            }

            locationChanged(locationResult.lastLocation!!)
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setMinUpdateIntervalMillis(fastestInterval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)

        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(activity as AppCompatActivity)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as AppCompatActivity)

        if (ActivityCompat.checkSelfPermission(
                activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity as AppCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!)
    }

    private fun locationChanged(location: Location) {
        mLastLocation = location

        mapFragment.getMapAsync { googleMap ->
            println("${mLastLocation.latitude}, ${mLastLocation.longitude}")
            val mapPosition = googleMap.cameraPosition

            if (abs(mapPosition.target.latitude - mLastLocation.latitude) > 0.05 &&
                abs(mapPosition.target.longitude - mLastLocation.longitude) > 0.05) {
                val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    private fun checkForPermission(context: Context) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                requestPermissionCode)
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false)
    }
}