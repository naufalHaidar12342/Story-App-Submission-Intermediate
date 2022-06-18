package xyz.heydarrn.storyappdicoding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.heydarrn.storyappdicoding.databinding.ActivityStoryDicodingMapsBinding
import xyz.heydarrn.storyappdicoding.model.api.ApiConfig
import xyz.heydarrn.storyappdicoding.model.api.response.GetStoriesResponse
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingModelFactory
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingViewModel

class StoryDicodingMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryDicodingMapsBinding
    private lateinit var viewModel: StoriesDicodingViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var _storiesWithLocation = MutableLiveData<List<ListStoryItem?>?>()
    private val storiesWithLocation : LiveData<List<ListStoryItem?>?> get() = _storiesWithLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryDicodingMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getDicodingFriendStoryLocation()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // lat= -6.9835783, lon = 110.4151906
        // adding a marker on ELS Computer Semarang
        val elsComputer = LatLng(ELS_COMPUTER_LAT, ELS_COMPUTER_LON)
        mMap.apply {
            addMarker(
                MarkerOptions()
                    .position(elsComputer)
                    .title(getString(R.string.els_computer))
                    .snippet(getString(R.string.els_computer_address))
            )

            animateCamera(CameraUpdateFactory.newLatLngZoom(elsComputer, 14f))

            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isIndoorLevelPickerEnabled = true
                isMapToolbarEnabled = true
            }
        }

        storiesWithLocation.observe(this) {
            for (storyLocation in storiesWithLocation.value?.indices!!) {
                val friendLocation = LatLng(
                    storiesWithLocation.value?.get(storyLocation)?.lat!!.toDouble(),
                    storiesWithLocation.value?.get(storyLocation)?.lon!!.toDouble()
                )

                mMap.addMarker(
                    MarkerOptions()
                        .position(friendLocation)
                        .title("Location of "+ storiesWithLocation.value?.get(storyLocation)?.name)
                        .snippet("Lat : ${friendLocation.latitude} , lon : ${friendLocation.longitude}")
                )

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(elsComputer, 3f))
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_style_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.normal_view_maps -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }

            R.id.satellite_view_maps -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }

            R.id.hybrid_view_maps -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }

            R.id.map_styling -> {
                try {
                    val successChangeStyle = mMap.setMapStyle(
                        MapStyleOptions
                            .loadRawResourceStyle(
                                this, R.raw.google_maps_styling
                            )
                    )

                    if (!successChangeStyle) {
                        Log.d("STYLE FAIL", "onOptionsItemStylingMaps: STYLE PARSING FAILED")
                    }
                }catch(stylingException : Resources.NotFoundException) {
                    Log.d("STYLE EXCEPTION", "onOptionsItemStylingException: $stylingException")
                }
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private val askForPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getLocationPersonal()
            }
        }
    }

    private fun String.checkIfPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this@StoryDicodingMapsActivity, this) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationPersonal() {
        if (Manifest.permission.ACCESS_FINE_LOCATION.checkIfPermissionGranted()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                when {
                    location != null -> {
                        mMap.isMyLocationEnabled = true
                        showMarkerOfLocationPersonal (location)
                    }

                    else -> {
                        Toast.makeText(
                            this,
                            getString(R.string.location_not_found),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            askForPermission.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    private fun showMarkerOfLocationPersonal(location: Location) {
        PERSONAL_LATITUDE = location.latitude
        PERSONAL_LONGITUDE = location.longitude
        val initializeLocation = LatLng(PERSONAL_LATITUDE, PERSONAL_LONGITUDE)

        mMap.addMarker(
            MarkerOptions()
                .position(initializeLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .draggable(true)
                .title(getString(R.string.my_current_location))
        )

    }

    private fun getDicodingFriendStoryLocation() {
        val factoryStory = StoriesDicodingModelFactory.getStoryModelFactoryInstance(this)
        viewModel = ViewModelProvider(this, factoryStory)[StoriesDicodingViewModel::class.java]

        viewModel.isUserLoggedInStory().observe(this) { loggedIn ->
            if(!loggedIn) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }
        }

        viewModel.getTokenForStory().observe(this) { tokenResult ->
            if (tokenResult.isNotEmpty()) {
                val clientStoryForMaps = ApiConfig.getApiService().getStoriesForMaps("Bearer $tokenResult", 1)
                clientStoryForMaps.enqueue(object : Callback<GetStoriesResponse> {
                    override fun onResponse(
                        call: Call<GetStoriesResponse>,
                        response: Response<GetStoriesResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.message == "Stories fetched successfully") {
                            _storiesWithLocation.value = response.body()!!.listStory
                        }
                        Log.d("STORYMAP GOOD", "onClientStoryMaps: ${response.body()}")
                    }

                    override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                        Log.d("STORYMAP FAIL", "onClientStoryMaps: ${t.printStackTrace()}")
                        Toast.makeText(
                            this@StoryDicodingMapsActivity,
                            getString(R.string.failed_to_fetch_story_with_location),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                })
            }
        }
    }


    companion object {
        var PERSONAL_LATITUDE = 0.0
        var PERSONAL_LONGITUDE = 0.0
        const val ELS_COMPUTER_LAT = -6.9835783
        const val ELS_COMPUTER_LON = 110.4151906
    }
}