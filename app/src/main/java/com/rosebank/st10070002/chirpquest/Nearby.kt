package com.rosebank.st10070002.chirpquest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import android.util.Log

class Nearby : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org/") // Base API url
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: BirdHotspotService = retrofit.create(BirdHotspotService::class.java)
    private var currentZoomLevel: Float = 12f // Starting zoom level
    private var nearestHotspot: BirdHotspot? = null // Store the nearest hotspot

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

        // Button to show route to the nearest hotspot
        val btnShowRoute: Button = findViewById(R.id.btnShowRoute)
        btnShowRoute.setOnClickListener { showRouteToNearestHotspot() }
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
        val apiKey = "p84spluvlo8a"
        val maxResults = 50 // Increases the max results
        val radius = 50 //Radius in KMs

        apiService.getBirdHotspots(latitude, longitude, apiKey = apiKey).enqueue(object : Callback<List<BirdHotspot>> {
            override fun onResponse(call: Call<List<BirdHotspot>>, response: Response<List<BirdHotspot>>) {
                if (response.isSuccessful) {
                    response.body()?.let { hotspots ->
                        var nearestDistance = Double.MAX_VALUE
                        var nearestHotspot: BirdHotspot? = null

                        for (hotspot in hotspots) {
                            // Create a new Location for the hotspot
                            val hotspotLocation = Location("").apply {
                                setLatitude(hotspot.lat) // Set latitude using the setter method
                                setLongitude(hotspot.lng) // Set longitude using the setter method
                            }
                            // Create a new Location for the user's location
                            val userLocation = Location("").apply {
                                setLatitude(latitude) // Set latitude using the setter method
                                setLongitude(longitude) // Set longitude using the setter method
                            }

                            // Calculate distance in meters (Float) and convert to Double
                            val distance = userLocation.distanceTo(hotspotLocation).toDouble() // Convert Float to Double

                            // Check if this hotspot is closer than the current nearest
                            if (distance < nearestDistance) {
                                nearestDistance = distance
                                nearestHotspot = hotspot
                            }

                            // Add the hotspot marker
                            val latLng = LatLng(hotspot.lat, hotspot.lng)
                            val hotspotMarkerOptions = MarkerOptions()
                                .position(latLng)
                                .title(hotspot.locName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                            myMap.addMarker(hotspotMarkerOptions)
                        }

                        this@Nearby.nearestHotspot = nearestHotspot // Set the nearest hotspot
                    }
                }
            }

            override fun onFailure(call: Call<List<BirdHotspot>>, t: Throwable) {
                // Handle failure
            }
        })
    }


    private fun showRouteToNearestHotspot() {
        nearestHotspot?.let { hotspot ->
            val userLocation = LatLng(myMap.cameraPosition.target.latitude, myMap.cameraPosition.target.longitude)
            val hotspotLocation = LatLng(hotspot.lat, hotspot.lng)

            // Call Directions API to get the route
            getDirections(userLocation, hotspotLocation)
        } ?: run {
            // Show a message if no hotspot is found
            Toast.makeText(this, "No hotspots found nearby", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        val directionsApiKey = "AIzaSyBFzVMvcUXyJPv-y3EtkJUEBgcwMuxWb1I" // Replace with your Google Directions API key
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$directionsApiKey"

        // Use Retrofit or any HTTP client to fetch the directions
        val call = retrofit.create(DirectionsService::class.java).getDirections(url)
        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful) {
                    // Parse and draw the route on the map
                    response.body()?.let { directionsResponse ->
                        drawRoute(directionsResponse)
                    }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun drawRoute(directionsResponse: DirectionsResponse) {
        for (route in directionsResponse.routes) {
            val polyline = route.overview_polyline
            if (polyline != null) { // Check if polyline is not null
                val polylineOptions = PolylineOptions().width(10f).color(android.graphics.Color.BLUE)
                val points = decodePolyline(polyline.points) // Ensure polyline.points is accessible
                polylineOptions.addAll(points)
                myMap.addPolyline(polylineOptions)
            } else {
                Log.e("Nearby", "Polyline is null for route: $route")
            }
        }
    }


    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
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
