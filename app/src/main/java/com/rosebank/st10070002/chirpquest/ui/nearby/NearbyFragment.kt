package com.rosebank.st10070002.chirpquest.ui.nearby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.databinding.FragmentNearbyBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.model.PolylineOptions
import com.rosebank.st10070002.chirpquest.DirectionsResponse
import com.rosebank.st10070002.chirpquest.DirectionsService
import com.rosebank.st10070002.chirpquest.BirdHotspot
import com.rosebank.st10070002.chirpquest.BirdHotspotService



class NearbyFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentNearbyBinding? = null
    private val binding get() = _binding!!

    private lateinit var myMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ebird.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService: BirdHotspotService = retrofit.create(BirdHotspotService::class.java)
    private var currentZoomLevel: Float = 12f
    private var nearestHotspot: BirdHotspot? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Set up map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up zoom buttons
        binding.btnZoomIn.setOnClickListener { zoomIn() }
        binding.btnZoomOut.setOnClickListener { zoomOut() }

        // Button to show route to the nearest hotspot
        binding.btnShowRoute.setOnClickListener { showRouteToNearestHotspot() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
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
        val maxResults = 50
        val radius = 50

        apiService.getBirdHotspots(latitude, longitude, apiKey = apiKey).enqueue(object : Callback<List<BirdHotspot>> {
            override fun onResponse(call: Call<List<BirdHotspot>>, response: Response<List<BirdHotspot>>) {
                if (response.isSuccessful) {
                    response.body()?.let { hotspots ->
                        var nearestDistance = Double.MAX_VALUE
                        var nearestHotspot: BirdHotspot? = null
                        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                        val distanceMetric = sharedPreferences.getString("distance_metric", "Kilometers")

                        for (hotspot in hotspots) {
                            val hotspotLocation = Location("").apply {
                                setLatitude(hotspot.lat) // Set latitude using the setter method
                                setLongitude(hotspot.lng)
                            }
                            val userLocation = Location("").apply {
                                this.latitude = latitude
                                this.longitude = longitude
                            }

                            var distance = userLocation.distanceTo(hotspotLocation).toDouble()

                            if (distanceMetric == "Miles") {
                                distance /= 1609.34  // Convert meters to miles
                            } else {
                                distance /= 1000 // Convert meters to kilometers
                            }

                            if (distance < nearestDistance) {
                                nearestDistance = distance
                                nearestHotspot = hotspot
                            }

                            val latLng = LatLng(hotspot.lat, hotspot.lng)
                            val hotspotMarkerOptions = MarkerOptions()
                                .position(latLng)
                                .title("${hotspot.locName} - ${String.format("%.2f", distance)} $distanceMetric")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                            myMap.addMarker(hotspotMarkerOptions)
                        }

                        this@NearbyFragment.nearestHotspot = nearestHotspot
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

            val userLocationObj = Location("").apply {
                latitude = userLocation.latitude
                longitude = userLocation.longitude
            }
            val hotspotLocationObj = Location("").apply {
                latitude = hotspotLocation.latitude
                longitude = hotspotLocation.longitude
            }

            var distance = userLocationObj.distanceTo(hotspotLocationObj).toDouble()

            val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val distanceMetric = sharedPreferences.getString("distance_metric", "Kilometers")

            if (distanceMetric == "Miles") {
                distance /= 1609.34
            } else {
                distance /= 1000
            }

            Toast.makeText(requireContext(), "Nearest Hotspot: ${String.format("%.2f", distance)} $distanceMetric", Toast.LENGTH_LONG).show()

            getDirections(userLocation, hotspotLocation)
        } ?: run {
            Toast.makeText(requireContext(), "No hotspots found nearby", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        val directionsApiKey = "AIzaSyBFzVMvcUXyJPv-y3EtkJUEBgcwMuxWb1I" //Google directions API
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
