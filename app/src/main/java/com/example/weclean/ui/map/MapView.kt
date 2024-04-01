package com.example.weclean.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.getIntentOld
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weclean.R
import com.example.weclean.backend.LitteringData
import com.example.weclean.ui.add.Add
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import java.util.Locale


class MapView : Fragment() {
    // Google maps fragment
    private lateinit var mapFragment: SupportMapFragment

    // Location update interval
    private val interval: Long = 10000 // 10seconds
    private val fastestInterval: Long = 5000 // 5 seconds

    // Firebase instance
    private val db = Firebase.firestore

    // Last location received from the fusedLocationProviderClient
    private lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private val requestPermissionCode = 999
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    // Geocoder to get address from latitude and longitude
    private lateinit var geocoder : Geocoder

    // List of littering entries
    private var litteringData = ArrayList<LitteringData>()
    // Map of markers added to the map
    private var markersAdded : MutableMap<String, String> = mutableMapOf()

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Geocoder to get address from latitude and longitude
        geocoder = Geocoder(activity as AppCompatActivity, Locale.getDefault())

        // Add littering button to navigate to the add littering activity
        val addTrashButton = view.findViewById<Button>(R.id.add_trash_button)
        addTrashButton.setOnClickListener {
            startActivity(Intent(activity as AppCompatActivity, Add::class.java))
        }

        // Check for location permissions
        checkForPermission(activity as AppCompatActivity)
        // Start location updates
        startLocationUpdates()

        // Fragment containing the google maps support fragment
        mapFragment = getChildFragmentManager().findFragmentById(R.id.map_fragment) as SupportMapFragment
        // Wait till map is loaded
        mapFragment.getMapAsync { googleMap ->
            // Remove indoor styling from map
            googleMap.setIndoorEnabled(false)
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnCameraMoveListener {
                val mapPosition = googleMap.cameraPosition
                getNearbyLitteringEntries(mapPosition.target.latitude, mapPosition.target.longitude)
            }

            googleMap.setOnInfoWindowClickListener { marker ->
                val litteringId = markersAdded[marker.id] ?: return@setOnInfoWindowClickListener

                val context = activity as AppCompatActivity
                context.switchFragment(MapViewStatus.LitteringDetails, litteringId)

                return@setOnInfoWindowClickListener
            }

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

    // Location callback for the fusedLocationProviderClient
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            if (locationResult.lastLocation == null) {
                return
            }

            locationUpdate(locationResult.lastLocation!!)
        }
    }

    /**
     * Get littering entries nearby the specified coordinates
     *
     * @param latitude
     * @param longitude
     */
    private fun getNearbyLitteringEntries(latitude : Double, longitude : Double) {
        // About 1 mile in latitude and longitude
        val lat = 0.0144927536231884
        val lon = 0.0181818181818182
        val radius = 5

        // Lower and upper bounds for the coordinates
        val lowerLat = latitude - (lat * radius)
        val lowerLon = longitude - (lon * radius)
        val greaterLat = latitude + (lat * radius)
        val greaterLon = longitude + (lon * radius)

        // Lower and upper GeoPoint for retrieving littering entries in range
        val lesserGeoPoint = GeoPoint(lowerLat, lowerLon)
        val greaterGeoPoint = GeoPoint(greaterLat, greaterLon)

        // Get entries from Firebase
        db.collection("LitteringData")
            .whereGreaterThan("geoPoint", lesserGeoPoint)
            .whereLessThan("geoPoint", greaterGeoPoint)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Latitude and longitude of the entry
                    val entryLatitude = document.getGeoPoint("geoPoint")!!.latitude
                    val entryLongitude = document.getGeoPoint("geoPoint")!!.longitude

                    // Construct LitteringData object for the entry from database
                    val litteringEntry = LitteringData(geocoder)
                    litteringEntry.timeStamp = document.getDate("date")!!.time
                    litteringEntry.community = document.getString("community")!!
                    litteringEntry.creator = document.getString("creator")!!
                    litteringEntry.description = document.getString("description")!!
                        .replace("_newline", "\n")
                    litteringEntry.updateLocation(entryLatitude, entryLongitude)
                    litteringEntry.id = document.id

                    // Add entry to return value
                    litteringData.add(litteringEntry)
                }

                // Display markers on the map
                updateLitteringMarkers()
            }
            .addOnFailureListener {
                println(it)
                Toast.makeText(
                    activity as AppCompatActivity,
                    "Unable to get littering entries",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Update the littering markers on the map
     *
     */
    private fun updateLitteringMarkers() {
        mapFragment.getMapAsync { googleMap ->
            // Loop through littering entries and add markers to the map
            for (litteringEntry in litteringData) {
                // If marker not yet displayed on map, add marker
                if (!markersAdded.values.contains(litteringEntry.id)) {
                    // Marker options for the littering entry
                    val markerOptions = MarkerOptions()
                        .title("Littering location")
                        .position(LatLng(litteringEntry.latitude, litteringEntry.longitude))
                        .icon(bitmapDescriptorFromVector(
                            activity as AppCompatActivity,
                            R.drawable.garbage_bag_icon))

                    // Add marker to the map
                    val marker = googleMap.addMarker(markerOptions) ?: return@getMapAsync

                    markersAdded[marker.id] = litteringEntry.id
                }
            }
        }
    }

    /**
     * Convert a vector resource to a bitmap descriptor
     *
     * @param context
     * @param vectorResId
     * @return BitmapDescriptor of the vector resource
     */
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)

            // Create a bitmap from the vector drawable
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    /**
     * Pause the location update listener
     *
     */
    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

    /**
     * Start the location update listener using the fusedLocationProviderClient
     *
     */
    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setMinUpdateIntervalMillis(fastestInterval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        // Builder for the location settings request
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

    /**
     * Function to run when location update received from fusedLocationProviderClient
     *
     * @param location
     */
    private fun locationUpdate(location: Location) {
        mLastLocation = location

        mapFragment.getMapAsync { googleMap ->
            // Camera position of the map
            val mapPosition = googleMap.cameraPosition

            // Move the map to the current location if the map is at the default location
            if (mapPosition.target.latitude == 0.0 && mapPosition.target.longitude == 0.0) {
                val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }

            // Get littering entries nearby the current location
            getNearbyLitteringEntries(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    /**
     * Check for location permissions
     *
     * @param context
     */
    private fun checkForPermission(context: Context) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestPermissionCode)
        }

        return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false)
    }
}

