package com.hack.parking.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Parking(
    val id: Int,
    val titleParking: String,
    val latitude: Double,
    val longitude: Double,
    val cols: Int,
    val rows: Int
) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(latitude, longitude)
    override fun getTitle(): String = titleParking

    override fun getSnippet(): String? {
        return null
    }
}
