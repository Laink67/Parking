package com.hack.parking.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.hack.parking.R
import com.hack.parking.data.model.Parking
import com.hack.parking.databinding.ActivityMainBinding
import com.hack.parking.utils.Constants
import com.hack.parking.utils.PlaceRenderer
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var geocoder: Geocoder
    private var googleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.apply {
            setContentView(root)

            if (googleMap == null) {
                this@MainActivity.mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

                this@MainActivity.mapFragment.getMapAsync(this@MainActivity)
            }
        }

        geocoder = Geocoder(this, Locale.getDefault())

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetBinding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (googleMap == null) {
            googleMap = p0

            // Разрешение на доступ к собственному местоположению
            enableMyLocation()

            val homeLatLng = LatLng(Constants.SMOLENSK_LATITUDE, Constants.SMOLENSK_LONGITUDE)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, Constants.ZOOM_LEVEL))

/*
            // Установка долгого нажатия для добавления нового маркера
            setMapLongClick()
*/
            // Установка собственного стиля карты
//            setMapStyle()


            addClusteredMarkers(listOf(Parking(0, "Title", 59.92770383387456, 30.36063058604574, 2, 9)))
        }
    }

    private fun addClusteredMarkers(list: List<Parking>) {
        // Create the ClusterManager class and set the custom renderer
        val clusterManager = ClusterManager<Parking>(this, googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                this,
                googleMap,
                clusterManager
            )

        // Add the places to the ClusterManager
        clusterManager.addItems(list)
        clusterManager.cluster()

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out
        googleMap?.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }

        clusterManager.setOnClusterItemClickListener { parking ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            binding.apply {
                bottomSheetBinding.apply {
                    parking.apply {
                        txtParkingName.text = titleParking
                        txtAddress.text = getAddress(position)
                        txtFreePlaces.text = getString(R.string.text_free_places, )
                    }
                }
            }

            true
        }
    }

    private fun getAddress(latLng: LatLng): String {
        return try {
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addressList[0].getAddressLine(0)
        } catch (e: java.lang.Exception) {
            getString(R.string.get_address_error)
        }
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            googleMap?.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    private fun setMapStyle() {
        try {
            val success = googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )

            if (success!!) {
                Log.d("TAG","Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.d("TAG","Can't find style. Error: $e")
        }
    }

}