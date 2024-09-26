package com.rosebank.st10070002.chirpquest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.app.ActivityCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Nearby : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org/") // Update with your API's base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: BirdHotspotService = retrofit.create(BirdHotspotService::class.java)
    private var currentZoomLevel: Float = 12f // Starting zoom level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_page)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up zoom buttons
        val btnZoomIn: Button = findViewById(R.id.btnZoomIn)
        val btnZoomOut: Button = findViewById(R.id.btnZoomOut)

        btnZoomIn.setOnClickListener { zoomIn() }
        btnZoomOut.setOnClickListener { zoomOut() }
    }

    override fun onMapReady(@NonNull googleMap: GoogleMap) {
        myMap = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        // Get the user's last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))

                // Add a marker for the user's location
                val markerOptions = MarkerOptions()
                    .position(userLatLng)
                    .title("You are here!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                myMap.addMarker(markerOptions)

                // Fetch bird hotspots around the user
                fetchBirdHotspots(it.latitude, it.longitude)
            }
        }
    }

    private fun zoomIn() {
        currentZoomLevel += 1f
        myMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel))
    }

    private fun zoomOut() {
        currentZoomLevel -= 1f
        myMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel))
    }

    private fun fetchBirdHotspots(latitude: Double, longitude: Double) {
        // Replace with your API key

        val apiKey = "p84spluvlo8a"
        val maxResults = 50 // Increase the max results
        val radius = 50 // Example radius in km, check if the API supports it

        apiService.getBirdHotspots(latitude, longitude, apiKey = apiKey).enqueue(object : Callback<List<BirdHotspot>> {
            override fun onResponse(call: Call<List<BirdHotspot>>, response: Response<List<BirdHotspot>>) {
                if (response.isSuccessful) {
                    response.body()?.let { hotspots ->
                        for (hotspot in hotspots) {
                            val latLng = LatLng(hotspot.lat, hotspot.lng)
                            val hotspotMarkerOptions = MarkerOptions()
                                .position(latLng)
                                .title(hotspot.locName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                            // Add the hotspot marker
                            myMap.addMarker(hotspotMarkerOptions)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<BirdHotspot>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get the location again
                onMapReady(myMap)
            }
        }
    }
}